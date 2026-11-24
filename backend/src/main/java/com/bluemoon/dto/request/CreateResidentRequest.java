package com.bluemoon.dto.request;

import jakarta.validation.constraints.NotBlank;

public class CreateResidentRequest {

    @NotBlank(message = "Họ và tên không được để trống")
    private String fullName;

    private String phone;
    private String email;
    private String identityNumber;

    public CreateResidentRequest() {}

    public CreateResidentRequest(String fullName, String phone, String email, String identityNumber) {
        this.fullName = fullName;
        this.phone = phone;
        this.email = email;
        this.identityNumber = identityNumber;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIdentityNumber() {
        return identityNumber;
    }

    public void setIdentityNumber(String identityNumber) {
        this.identityNumber = identityNumber;
    }
}
