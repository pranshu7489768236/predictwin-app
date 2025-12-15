package com.predictwin.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "password_reset_tokens")
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date expiryDate;

    @Column(nullable = false)
    private boolean used = false;

    public PasswordResetToken() {}

    public PasswordResetToken(User user, String code, Date expiryDate) {
        this.user = user;
        this.code = code;
        this.expiryDate = expiryDate;
        this.used = false;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public Date getExpiryDate() { return expiryDate; }
    public void setExpiryDate(Date expiryDate) { this.expiryDate = expiryDate; }
    public boolean isUsed() { return used; }
    public void setUsed(boolean used) { this.used = used; }
}
