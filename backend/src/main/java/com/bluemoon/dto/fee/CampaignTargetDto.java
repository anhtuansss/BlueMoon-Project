package com.bluemoon.dto.fee;

public class CampaignTargetDto {
    private String mode; // "ALL_ACTIVE" or "ONE_APARTMENT"
    private Long apartmentId;

    public CampaignTargetDto() {}

    public CampaignTargetDto(String mode, Long apartmentId) {
        this.mode = mode;
        this.apartmentId = apartmentId;
    }

    public String getMode() { return mode; }
    public void setMode(String mode) { this.mode = mode; }

    public Long getApartmentId() { return apartmentId; }
    public void setApartmentId(Long apartmentId) { this.apartmentId = apartmentId; }
}
