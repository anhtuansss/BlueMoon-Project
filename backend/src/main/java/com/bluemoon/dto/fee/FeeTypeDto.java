package com.bluemoon.dto.fee;

import java.math.BigDecimal;

public class FeeTypeDto {
    private Long id;
    private String code;
    private String name;
    private String category;
    private String chargingMethod;
    private BigDecimal defaultUnitPrice;
    private Boolean isActive;
    private String description;

    public FeeTypeDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getChargingMethod() { return chargingMethod; }
    public void setChargingMethod(String chargingMethod) { this.chargingMethod = chargingMethod; }

    public BigDecimal getDefaultUnitPrice() { return defaultUnitPrice; }
    public void setDefaultUnitPrice(BigDecimal defaultUnitPrice) { this.defaultUnitPrice = defaultUnitPrice; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean active) { isActive = active; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
