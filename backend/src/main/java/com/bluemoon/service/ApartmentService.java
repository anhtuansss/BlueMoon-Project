package com.bluemoon.service;

import com.bluemoon.dto.request.CreateApartmentRequest;
import com.bluemoon.dto.response.ApartmentDto;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ApartmentService {
    List<ApartmentDto> getAllApartments();
    Page<ApartmentDto> getApartments(Boolean isActive, int page, int size);
    ApartmentDto getApartmentById(Long id);
    ApartmentDto createApartment(CreateApartmentRequest request);
    ApartmentDto updateApartment(Long id, CreateApartmentRequest request);
    void deactivateApartment(Long id);
}
