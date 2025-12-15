package com.predictwin.service;

import com.predictwin.model.RefreshToken;
import com.predictwin.model.User;
import com.predictwin.repository.RefreshTokenRepository;
import com.predictwin.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UserRepository userRepository;

    // default 7 days expiry (can be configured later)
    private final int refreshTokenMinutes = 60 * 24 * 7;

    public RefreshToken createRefreshToken(User user) {
        // delete existing tokens for user
        refreshTokenRepository.deleteByUser(user);
        String token = UUID.randomUUID().toString();
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, refreshTokenMinutes);
        Date expiry = cal.getTime();
        RefreshToken rt = new RefreshToken(token, user, expiry);
        return refreshTokenRepository.save(rt);
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public boolean isExpired(RefreshToken rt) {
        return rt.getExpiryDate().before(new Date());
    }

    public void deleteByUser(User user) {
        refreshTokenRepository.deleteByUser(user);
    }
}
