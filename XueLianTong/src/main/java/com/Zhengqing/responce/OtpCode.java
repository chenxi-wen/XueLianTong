package com.Zhengqing.responce;

public class OtpCode {
    private String telephone;
    private String otpCode;

    public OtpCode(String telephone, String otpCode) {
        this.telephone = telephone;
        this.otpCode = otpCode;
    }

    public String getTelphone() {
        return telephone;
    }

    public void setTelphone(String telphone) {
        this.telephone = telphone;
    }

    public String getOtpCode() {
        return otpCode;
    }

    public void setOtpCode(String otpCode) {
        this.otpCode = otpCode;
    }
}