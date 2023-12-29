package com.internevaluation.formfiller.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QrUserEntity {
    private String email;
    private String otp;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }
}
