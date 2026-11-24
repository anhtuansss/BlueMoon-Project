package com.bluemoon.service.impl;

import com.bluemoon.dto.request.CreateApartmentRequest;
import com.bluemoon.dto.response.ApartmentDto;
import com.bluemoon.entity.Apartment;
import com.bluemoon.exception.AppException;
import com.bluemoon.repository.ApartmentRepository;
import com.bluemoon.service.ApartmentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ApartmentServiceImpl implements ApartmentService {

    private final ApartmentRepository apartmentRepository;

    public ApartmentServiceImpl(ApartmentRepository apartmentRepository) {
        this.apartmentRepository = apartmentRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ApartmentDto> getAllApartments() {
        return apartmentRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ApartmentDto> getApartments(Boolean isActive, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, Math.min(size, 100));
        Page<Apartment> apartments = isActive != null
                ? apartmentRepository.findByIsActive(isActive, pageRequest)
                : apartmentRepository.findAll(pageRequest);
        return apartments.map(this::mapToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public ApartmentDto getApartmentById(Long id) {
        Apartment apt = apartmentRepository.findById(id)
                .orElseThrow(() -> new AppException("APARTMENT_NOT_FOUND", "Không tìm thấy căn hộ", HttpStatus.NOT_FOUND));
        return mapToDto(apt);
    }

    @Override
    @Transactional
    public ApartmentDto createApartment(CreateApartmentRequest request) {
        if (apartmentRepository.existsByApartmentNumber(request.getApartmentNumber())) {
            throw new AppException("DUPLICATE_APARTMENT_NUMBER", "Số căn hộ đã tồn tại trong hệ thống", HttpStatus.CONFLICT);
        }

        Apartment apartment = new Apartment(request.getApartmentNumber(), request.getArea());
        Apartment saved = apartmentRepository.save(apartment);
        // TODO [Thành - Week 1]: Ghi nhận log khởi tạo căn hộ nếu cần
        return mapToDto(saved);
    }

    @Override
    @Transactional
    public ApartmentDto updateApartment(Long id, CreateApartmentRequest request) {
        Apartment apt = apartmentRepository.findById(id)
                .orElseThrow(() -> new AppException("APARTMENT_NOT_FOUND", "Không tìm thấy căn hộ", HttpStatus.NOT_FOUND));

        if (!apt.getApartmentNumber().equals(request.getApartmentNumber())
                && apartmentRepository.existsByApartmentNumber(request.getApartmentNumber())) {
            throw new AppException("DUPLICATE_APARTMENT_NUMBER", "Số căn hộ đã tồn tại trong hệ thống", HttpStatus.CONFLICT);
        }

        apt.setApartmentNumber(request.getApartmentNumber());
        apt.setArea(request.getArea());
        // TODO [Thành - Week 1]: Kiểm tra nếu căn hộ đã có nghĩa vụ phí thì xử lý cảnh báo thay đổi diện tích
        return mapToDto(apt);
    }

    @Override
    @Transactional
    public void deactivateApartment(Long id) {
        // Quy tắc: Không xóa vật lý căn hộ, chuyển is_active = false
        Apartment apt = apartmentRepository.findById(id)
                .orElseThrow(() -> new AppException("APARTMENT_NOT_FOUND", "Không tìm thấy căn hộ", HttpStatus.NOT_FOUND));
        apt.setIsActive(false);
    }

    private ApartmentDto mapToDto(Apartment apt) {
        return new ApartmentDto(
                String.valueOf(apt.getApartmentId()),
                apt.getApartmentNumber(),
                apt.getArea(),
                apt.getIsActive()
        );
    }
}
