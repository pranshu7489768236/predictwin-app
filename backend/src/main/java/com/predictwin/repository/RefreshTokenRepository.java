package com.predictwin.repository;

import com.predictwin.model.RefreshToken;
import com.predictwin.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    Optional<RefreshToken> findFirstByUserAndExpiryDateAfter(User user, java.util.Date now);
    void deleteByUser(User user);
}
