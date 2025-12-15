package com.predictwin.service;

import com.predictwin.model.PasswordResetToken;
import com.predictwin.model.User;
import com.predictwin.repository.PasswordResetTokenRepository;
import com.predictwin.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.Optional;
import java.util.Random;

@Service
public class PasswordResetService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final Random random = new Random();

    public String createTokenForUser(String username) {
        Optional<User> ou = userRepository.findByUsername(username);
        if (!ou.isPresent()) return null;
        User user = ou.get();
        // 6-digit OTP
        int code = 100000 + random.nextInt(900000);
        String codeStr = String.valueOf(code);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, 10);
        Date expiry = cal.getTime();
        PasswordResetToken token = new PasswordResetToken(user, codeStr, expiry);
        tokenRepository.save(token);
        // For development we log the code. In production, send SMS/email.
        System.out.println("[PasswordReset] code for user=" + username + " code=" + codeStr);
        return codeStr;
    }

    public boolean verifyAndReset(String username, String code, String newPassword) {
        Optional<User> ou = userRepository.findByUsername(username);
        if (!ou.isPresent()) return false;
        User user = ou.get();
        Optional<PasswordResetToken> ot = tokenRepository.findFirstByUserAndCodeAndUsedFalse(user, code);
        if (!ot.isPresent()) return false;
        PasswordResetToken token = ot.get();
        if (token.getExpiryDate().before(new Date())) return false;
        // mark used and reset password
        token.setUsed(true);
        tokenRepository.save(token);
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return true;
    }
}
