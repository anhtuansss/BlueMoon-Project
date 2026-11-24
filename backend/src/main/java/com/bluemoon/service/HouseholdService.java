package com.bluemoon.service;

import com.bluemoon.dto.household.CreateHouseholdRequest;
import com.bluemoon.dto.household.HouseholdDto;

import java.util.List;

public interface HouseholdService {
    List<HouseholdDto> findAll(String keyword);
    HouseholdDto findById(Long id);
    HouseholdDto create(CreateHouseholdRequest request);
}
