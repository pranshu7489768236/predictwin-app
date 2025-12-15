package com.predictwin.controller;

import com.predictwin.model.User;
import com.predictwin.repository.UserRepository;
import com.predictwin.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.CookieValue;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private com.predictwin.service.PasswordResetService passwordResetService;
    
    @Autowired
    private com.predictwin.service.RefreshTokenService refreshTokenService;
    
    @org.springframework.beans.factory.annotation.Value("${refresh.cookie.secure:false}")
    private boolean refreshCookieSecure;
    @org.springframework.beans.factory.annotation.Value("${refresh.cookie.name:refreshToken}")
    private String refreshCookieName;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> body) {
        String username = body.getOrDefault("username", body.get("mobile"));
        String password = body.get("password");
        if (username == null || password == null) {
            java.util.Map<String,Object> resp = new java.util.HashMap<>();
            resp.put("error","username and password required");
            return ResponseEntity.badRequest().body(resp);
        }
        if (userRepository.existsByUsername(username)) {
            java.util.Map<String,Object> resp = new java.util.HashMap<>();
            resp.put("error","username taken");
            return ResponseEntity.badRequest().body(resp);
        }
        User user = new User(username, passwordEncoder.encode(password), "USER");
        userRepository.save(user);
        java.util.Map<String,Object> resp = new java.util.HashMap<>();
        resp.put("message","registered");
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String username = body.getOrDefault("username", body.get("mobile"));
        String password = body.get("password");
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (BadCredentialsException ex) {
            java.util.Map<String,Object> resp = new java.util.HashMap<>();
            resp.put("error","invalid credentials");
            return ResponseEntity.status(401).body(resp);
        }
        String token = jwtUtil.generateToken(username);
        // create refresh token and set it as HttpOnly cookie (production recommended)
        com.predictwin.model.User u = userRepository.findByUsername(username).orElse(null);
        String refresh = null;
        HttpHeaders headers = new HttpHeaders();
        if (u != null) {
            com.predictwin.model.RefreshToken rt = refreshTokenService.createRefreshToken(u);
            refresh = rt.getToken();
            // create cookie
            ResponseCookie cookie = ResponseCookie.from(refreshCookieName, refresh)
                    .httpOnly(true)
                    .secure(refreshCookieSecure)
                    .path("/api/auth")
                    .maxAge(60L * 60L * 24L * 7L) // 7 days
                    .sameSite("Lax")
                    .build();
            headers.add(HttpHeaders.SET_COOKIE, cookie.toString());
        }
        java.util.Map<String,Object> resp = new java.util.HashMap<>();
        resp.put("accessToken", token);
        return ResponseEntity.ok().headers(headers).body(resp);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody(required = false) Map<String, String> body,
                                     @CookieValue(name = "refreshToken", required = false) String refreshTokenCookie) {
        String refreshToken = null;
        if (refreshTokenCookie != null && !refreshTokenCookie.isEmpty()) refreshToken = refreshTokenCookie;
        if (refreshToken == null && body != null) refreshToken = body.get("refreshToken");
        if (refreshToken == null) {
            java.util.Map<String,Object> r = new java.util.HashMap<>(); r.put("error","refreshToken required");
            return ResponseEntity.badRequest().body(r);
        }
        java.util.Optional<com.predictwin.model.RefreshToken> ort = refreshTokenService.findByToken(refreshToken);
        if (!ort.isPresent()) {
            java.util.Map<String,Object> r = new java.util.HashMap<>(); r.put("error","invalid refresh token");
            return ResponseEntity.status(401).body(r);
        }
        com.predictwin.model.RefreshToken rt = ort.get();
        if (refreshTokenService.isExpired(rt)) {
            java.util.Map<String,Object> r = new java.util.HashMap<>(); r.put("error","refresh token expired");
            return ResponseEntity.status(401).body(r);
        }
        String username = rt.getUser().getUsername();
        String newAccess = jwtUtil.generateToken(username);
        java.util.Map<String,Object> r = new java.util.HashMap<>(); r.put("accessToken", newAccess);
        return ResponseEntity.ok(r);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@CookieValue(name = "refreshToken", required = false) String refreshTokenCookie) {
        if (refreshTokenCookie != null) {
            java.util.Optional<com.predictwin.model.RefreshToken> ort = refreshTokenService.findByToken(refreshTokenCookie);
            if (ort.isPresent()) {
                refreshTokenService.deleteByUser(ort.get().getUser());
            }
        }
        // clear cookie
        ResponseCookie cookie = ResponseCookie.from(refreshCookieName, "")
                .httpOnly(true).secure(refreshCookieSecure).path("/api/auth").maxAge(0).sameSite("Lax").build();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body(java.util.Collections.singletonMap("ok", true));
    }

    @PostMapping("/forgot")
    public ResponseEntity<?> forgot(@RequestBody Map<String, String> body) {
        String username = body.getOrDefault("username", body.get("mobile"));
        if (username == null) {
            java.util.Map<String,Object> r = new java.util.HashMap<>();
            r.put("error", "username/mobile required");
            return ResponseEntity.badRequest().body(r);
        }
        String code = passwordResetService.createTokenForUser(username);
        if (code == null) {
            java.util.Map<String,Object> r = new java.util.HashMap<>();
            r.put("error", "user not found");
            return ResponseEntity.badRequest().body(r);
        }
        // In dev we return ok (and code is logged). Do not return OTP in production.
        java.util.Map<String,Object> r = new java.util.HashMap<>();
        r.put("ok", true);
        r.put("message", "otp_sent");
        return ResponseEntity.ok(r);
    }

    @PostMapping("/reset")
    public ResponseEntity<?> reset(@RequestBody Map<String, String> body) {
        String username = body.getOrDefault("username", body.get("mobile"));
        String otp = body.get("otp");
        String password = body.get("password");
        if (username == null || otp == null || password == null) {
            java.util.Map<String,Object> r = new java.util.HashMap<>();
            r.put("error", "username/mobile, otp and password required");
            return ResponseEntity.badRequest().body(r);
        }
        boolean ok = passwordResetService.verifyAndReset(username, otp, password);
        if (!ok) {
            java.util.Map<String,Object> r = new java.util.HashMap<>();
            r.put("error", "invalid or expired otp");
            return ResponseEntity.status(400).body(r);
        }
        java.util.Map<String,Object> r = new java.util.HashMap<>();
        r.put("ok", true);
        r.put("message", "password_reset");
        return ResponseEntity.ok(r);
    }
}
