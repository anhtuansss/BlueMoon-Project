package com.bluemoon.dto.response;

public class ResidentDto {
    private String personId;
    private String fullName;
    private String phone;
    private String email;
    private String identityNumber;

    public ResidentDto() {}

    public ResidentDto(String personId, String fullName, String phone, String email, String identityNumber) {
        this.personId = personId;
        this.fullName = fullName;
        this.phone = phone;
        this.email = email;
        this.identityNumber = identityNumber;
    }

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
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
