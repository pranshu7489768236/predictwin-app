package com.predictwin.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "ratelimit")
@Component
public class RateLimitProperties {
    private int ipLogin = 10;
    private int ipLoginMinutes = 15;
    private int ipForgot = 5;
    private int ipForgotMinutes = 15;
    private int userLogin = 5;
    private int userLoginMinutes = 15;
    private int userForgot = 3;
    private int userForgotMinutes = 30;

    public int getIpLogin() { return ipLogin; }
    public void setIpLogin(int ipLogin) { this.ipLogin = ipLogin; }
    public int getIpLoginMinutes() { return ipLoginMinutes; }
    public void setIpLoginMinutes(int ipLoginMinutes) { this.ipLoginMinutes = ipLoginMinutes; }
    public int getIpForgot() { return ipForgot; }
    public void setIpForgot(int ipForgot) { this.ipForgot = ipForgot; }
    public int getIpForgotMinutes() { return ipForgotMinutes; }
    public void setIpForgotMinutes(int ipForgotMinutes) { this.ipForgotMinutes = ipForgotMinutes; }
    public int getUserLogin() { return userLogin; }
    public void setUserLogin(int userLogin) { this.userLogin = userLogin; }
    public int getUserLoginMinutes() { return userLoginMinutes; }
    public void setUserLoginMinutes(int userLoginMinutes) { this.userLoginMinutes = userLoginMinutes; }
    public int getUserForgot() { return userForgot; }
    public void setUserForgot(int userForgot) { this.userForgot = userForgot; }
    public int getUserForgotMinutes() { return userForgotMinutes; }
    public void setUserForgotMinutes(int userForgotMinutes) { this.userForgotMinutes = userForgotMinutes; }
}
