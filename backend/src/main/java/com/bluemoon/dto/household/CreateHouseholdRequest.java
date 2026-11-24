package com.bluemoon.dto.household;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateHouseholdRequest {

    @NotBlank(message = "Tên hộ gia đình không được để trống")
    private String householdName;

    private String contactPhone;

    @NotNull(message = "ID căn hộ không được để trống")
    private Long apartmentId;

    public CreateHouseholdRequest() {}

    public String getHouseholdName() { return householdName; }
    public void setHouseholdName(String householdName) { this.householdName = householdName; }

    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }

    public Long getApartmentId() { return apartmentId; }
    public void setApartmentId(Long apartmentId) { this.apartmentId = apartmentId; }
}
