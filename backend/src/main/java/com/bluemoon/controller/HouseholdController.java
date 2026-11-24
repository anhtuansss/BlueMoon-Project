package com.bluemoon.controller;

import com.bluemoon.dto.response.ApiResponse;
import com.bluemoon.dto.household.CreateHouseholdRequest;
import com.bluemoon.dto.household.HouseholdDto;
import com.bluemoon.service.HouseholdService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller cho Quản lý hộ gia đình (Thành viên 2 - Thành - F03).
 */
@RestController
@RequestMapping("/api/v1/households")
public class HouseholdController {

    private final HouseholdService householdService;

    public HouseholdController(HouseholdService householdService) {
        this.householdService = householdService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('BQT', 'NV')")
    public ResponseEntity<ApiResponse<List<HouseholdDto>>> getAllHouseholds(
            @RequestParam(required = false) String keyword
    ) {
        List<HouseholdDto> households = householdService.findAll(keyword);
        return ResponseEntity.ok(ApiResponse.success(households));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('BQT', 'NV', 'CU_DAN')")
    public ResponseEntity<ApiResponse<HouseholdDto>> getHouseholdById(@PathVariable Long id) {
        HouseholdDto household = householdService.findById(id);
        return ResponseEntity.ok(ApiResponse.success(household));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('BQT', 'NV')")
    public ResponseEntity<ApiResponse<HouseholdDto>> createHousehold(@Valid @RequestBody CreateHouseholdRequest request) {
        HouseholdDto created = householdService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(created));
    }
}
