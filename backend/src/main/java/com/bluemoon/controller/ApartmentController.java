package com.bluemoon.controller;

import com.bluemoon.dto.request.CreateApartmentRequest;
import com.bluemoon.dto.response.ApiResponse;
import com.bluemoon.dto.response.ApartmentDto;
import com.bluemoon.service.ApartmentService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/apartments")
public class ApartmentController {

    private final ApartmentService apartmentService;

    public ApartmentController(ApartmentService apartmentService) {
        this.apartmentService = apartmentService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ApartmentDto>>> getAllApartments(
            @RequestParam(required = false) Boolean isActive) {
        List<ApartmentDto> result = apartmentService.getAllApartments();
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/paged")
    public ResponseEntity<ApiResponse<Page<ApartmentDto>>> getPagedApartments(
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<ApartmentDto> result = apartmentService.getApartments(isActive, page, size);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ApartmentDto>> getApartmentById(@PathVariable Long id) {
        ApartmentDto result = apartmentService.getApartmentById(id);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ApartmentDto>> createApartment(@Valid @RequestBody CreateApartmentRequest request) {
        ApartmentDto result = apartmentService.createApartment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Tạo căn hộ thành công", result));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ApartmentDto>> updateApartment(
            @PathVariable Long id,
            @Valid @RequestBody CreateApartmentRequest request) {
        ApartmentDto result = apartmentService.updateApartment(id, request);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật căn hộ thành công", result));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deactivateApartment(@PathVariable Long id) {
        apartmentService.deactivateApartment(id);
        return ResponseEntity.ok(ApiResponse.success("Ngừng hoạt động căn hộ thành công", null));
    }
}
