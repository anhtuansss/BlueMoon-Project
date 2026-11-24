package com.bluemoon.controller;

import com.bluemoon.dto.response.ApiResponse;
import com.bluemoon.dto.request.CreateResidentRequest;
import com.bluemoon.dto.response.ResidentDto;
import com.bluemoon.service.ResidentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller cho Quản lý cư dân (Thành viên 2 - Thành - F03).
 */
@RestController
@RequestMapping("/api/v1/residents")
public class ResidentController {

    private final ResidentService residentService;

    public ResidentController(ResidentService residentService) {
        this.residentService = residentService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('BQT', 'NV')")
    public ResponseEntity<ApiResponse<List<ResidentDto>>> getAllResidents(
            @RequestParam(required = false) String keyword
    ) {
        List<ResidentDto> residents = residentService.getAllResidents();
        return ResponseEntity.ok(ApiResponse.success(residents));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('BQT', 'NV', 'CU_DAN')")
    public ResponseEntity<ApiResponse<ResidentDto>> getResidentById(@PathVariable Long id) {
        ResidentDto resident = residentService.getResidentById(id);
        return ResponseEntity.ok(ApiResponse.success(resident));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('BQT', 'NV')")
    public ResponseEntity<ApiResponse<ResidentDto>> createResident(@Valid @RequestBody CreateResidentRequest request) {
        ResidentDto created = residentService.createResident(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(created));
    }
}
