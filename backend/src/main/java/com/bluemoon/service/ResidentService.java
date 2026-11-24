package com.bluemoon.service;

import com.bluemoon.dto.request.CreateResidentRequest;
import com.bluemoon.dto.response.ResidentDto;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ResidentService {
    List<ResidentDto> getAllResidents();
    Page<ResidentDto> getResidents(String keyword, int page, int size);
    ResidentDto getResidentById(Long id);
    ResidentDto createResident(CreateResidentRequest request);
    ResidentDto updateResident(Long id, CreateResidentRequest request);
}
