package com.predictwin.repository;

import com.predictwin.model.PasswordResetToken;
import com.predictwin.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findFirstByUserAndCodeAndUsedFalse(User user, String code);
    Optional<PasswordResetToken> findFirstByUserAndUsedFalseAndExpiryDateAfter(User user, Date now);
}
