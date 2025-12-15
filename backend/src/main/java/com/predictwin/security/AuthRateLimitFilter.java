package com.predictwin.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.predictwin.config.RateLimitProperties;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.util.ContentCachingRequestWrapper;
import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Order(1)
public class AuthRateLimitFilter extends OncePerRequestFilter {

    private final Map<String, Bucket> ipLoginBuckets = new ConcurrentHashMap<>();
    private final Map<String, Bucket> ipForgotBuckets = new ConcurrentHashMap<>();
    private final Map<String, Bucket> userLoginBuckets = new ConcurrentHashMap<>();
    private final Map<String, Bucket> userForgotBuckets = new ConcurrentHashMap<>();

    private final ObjectMapper mapper = new ObjectMapper();
    private final MeterRegistry meterRegistry;
    private final Logger log = LoggerFactory.getLogger(AuthRateLimitFilter.class);

    private final Bandwidth IP_LOGIN_LIMIT;
    private final Bandwidth IP_FORGOT_LIMIT;
    private final Bandwidth USER_LOGIN_LIMIT;
    private final Bandwidth USER_FORGOT_LIMIT;

    public AuthRateLimitFilter(RateLimitProperties props, MeterRegistry meterRegistry) {
        this.IP_LOGIN_LIMIT = Bandwidth.classic(props.getIpLogin(), Refill.intervally(props.getIpLogin(), Duration.ofMinutes(props.getIpLoginMinutes())));
        this.IP_FORGOT_LIMIT = Bandwidth.classic(props.getIpForgot(), Refill.intervally(props.getIpForgot(), Duration.ofMinutes(props.getIpForgotMinutes())));
        this.USER_LOGIN_LIMIT = Bandwidth.classic(props.getUserLogin(), Refill.intervally(props.getUserLogin(), Duration.ofMinutes(props.getUserLoginMinutes())));
        this.USER_FORGOT_LIMIT = Bandwidth.classic(props.getUserForgot(), Refill.intervally(props.getUserForgot(), Duration.ofMinutes(props.getUserForgotMinutes())));
        this.meterRegistry = meterRegistry;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        return !(path.startsWith("/api/auth/login") || path.startsWith("/api/auth/forgot"));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        ContentCachingRequestWrapper wrapped = new ContentCachingRequestWrapper(request);
        String ip = resolveClientIp(wrapped);
        String path = wrapped.getRequestURI();

        if (path.startsWith("/api/auth/login")) {
            Bucket ipBucket = ipLoginBuckets.computeIfAbsent(ip, k -> Bucket4j.builder().addLimit(IP_LOGIN_LIMIT).build());
            if (ipBucket.tryConsume(1) == false) {
                sendTooMany(response, "Too many login attempts from this IP", "login", "ip", ip, null);
                return;
            }
        }

        if (path.startsWith("/api/auth/forgot")) {
            Bucket ipBucket = ipForgotBuckets.computeIfAbsent(ip, k -> Bucket4j.builder().addLimit(IP_FORGOT_LIMIT).build());
            if (ipBucket.tryConsume(1) == false) {
                sendTooMany(response, "Too many password-reset attempts from this IP", "forgot", "ip", ip, null);
                return;
            }
        }

        // username-based limits (if username/mobile present in JSON body)
        String username = extractUsernameFromBody(wrapped);
        if (username != null) {
            if (path.startsWith("/api/auth/login")) {
                Bucket uBucket = userLoginBuckets.computeIfAbsent(username, k -> Bucket4j.builder().addLimit(USER_LOGIN_LIMIT).build());
                if (uBucket.tryConsume(1) == false) {
                    sendTooMany(response, "Too many login attempts for this account", "login", "user", username, null);
                    return;
                }
            }
            if (path.startsWith("/api/auth/forgot")) {
                Bucket uBucket = userForgotBuckets.computeIfAbsent(username, k -> Bucket4j.builder().addLimit(USER_FORGOT_LIMIT).build());
                if (uBucket.tryConsume(1) == false) {
                    sendTooMany(response, "Too many password-reset attempts for this account", "forgot", "user", username, null);
                    return;
                }
            }
        }

        filterChain.doFilter(wrapped, response);
    }

    private String resolveClientIp(HttpServletRequest request) {
        String xf = request.getHeader("X-Forwarded-For");
        if (xf == null) return request.getRemoteAddr();
        return xf.split(",")[0].trim();
    }

    private String extractUsernameFromBody(ContentCachingRequestWrapper request) {
        try {
            byte[] buf = request.getContentAsByteArray();
            if (buf == null || buf.length == 0) return null;
            String body = new String(buf, request.getCharacterEncoding() != null ? request.getCharacterEncoding() : "UTF-8");
            Map map = mapper.readValue(body, Map.class);
            Object u = map.get("username");
            if (u == null) u = map.get("mobile");
            if (u == null) return null;
            return String.valueOf(u).toLowerCase();
        } catch (Exception ex) {
            return null;
        }
    }

    private void sendTooMany(HttpServletResponse response, String msg, String endpoint, String scope, String key, String extra) throws IOException {
        // Log and emit a metric for observability
        try {
            if ("ip".equals(scope)) {
                log.warn("Rate limit hit: endpoint={} scope=ip ip={} msg={}", endpoint, key, msg);
                meterRegistry.counter("ratelimit.hits", "endpoint", endpoint, "scope", scope).increment();
            } else {
                log.warn("Rate limit hit: endpoint={} scope=user user={} msg={}", endpoint, key, msg);
                meterRegistry.counter("ratelimit.hits", "endpoint", endpoint, "scope", scope).increment();
            }
        } catch (Exception ex) {
            // don't fail the request because metrics/logging failed
            log.debug("Failed to record rate-limit metric", ex);
        }

        response.setStatus(429);
        response.setContentType("application/json");
        String body = mapper.writeValueAsString(java.util.Collections.singletonMap("error", msg));
        response.getWriter().write(body);
    }
}
