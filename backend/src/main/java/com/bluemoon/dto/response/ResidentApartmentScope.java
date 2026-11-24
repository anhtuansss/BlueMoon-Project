package com.bluemoon.dto.response;

public class ResidentApartmentScope {
    private Long accountId;
    private Long personId;
    private Long apartmentId;

    public ResidentApartmentScope() {}

    public ResidentApartmentScope(Long accountId, Long personId, Long apartmentId) {
        this.accountId = accountId;
        this.personId = personId;
        this.apartmentId = apartmentId;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public Long getPersonId() {
        return personId;
    }

    public void setPersonId(Long personId) {
        this.personId = personId;
    }

    public Long getApartmentId() {
        return apartmentId;
    }

    public void setApartmentId(Long apartmentId) {
        this.apartmentId = apartmentId;
    }
}
