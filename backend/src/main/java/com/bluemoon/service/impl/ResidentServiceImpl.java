package com.bluemoon.service.impl;

import com.bluemoon.dto.request.CreateResidentRequest;
import com.bluemoon.dto.response.ResidentDto;
import com.bluemoon.entity.Person;
import com.bluemoon.exception.AppException;
import com.bluemoon.repository.PersonRepository;
import com.bluemoon.service.ResidentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ResidentServiceImpl implements ResidentService {

    private final PersonRepository personRepository;

    public ResidentServiceImpl(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResidentDto> getAllResidents() {
        return personRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ResidentDto> getResidents(String keyword, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, Math.min(size, 100));
        Page<Person> pageResult = (keyword != null && !keyword.trim().isEmpty())
                ? personRepository.findByFullNameContainingIgnoreCase(keyword.trim(), pageRequest)
                : personRepository.findAll(pageRequest);
        return pageResult.map(this::mapToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public ResidentDto getResidentById(Long id) {
        Person person = personRepository.findById(id)
                .orElseThrow(() -> new AppException("RESIDENT_NOT_FOUND", "Không tìm thấy hồ sơ cư dân", HttpStatus.NOT_FOUND));
        return mapToDto(person);
    }

    @Override
    @Transactional
    public ResidentDto createResident(CreateResidentRequest request) {
        if (request.getIdentityNumber() != null && !request.getIdentityNumber().trim().isEmpty()) {
            if (personRepository.existsByIdentityNumber(request.getIdentityNumber().trim())) {
                throw new AppException("DUPLICATE_IDENTITY_NUMBER", "Số CMND/CCCD đã tồn tại trên hệ thống", HttpStatus.CONFLICT);
            }
        }

        Person person = new Person(
                request.getFullName(),
                request.getPhone(),
                request.getEmail(),
                request.getIdentityNumber()
        );
        Person saved = personRepository.save(person);
        // TODO [Thành - Week 1]: Liên kết cư dân với hộ gia đình/căn hộ ban đầu nếu có thông tin
        return mapToDto(saved);
    }

    @Override
    @Transactional
    public ResidentDto updateResident(Long id, CreateResidentRequest request) {
        Person person = personRepository.findById(id)
                .orElseThrow(() -> new AppException("RESIDENT_NOT_FOUND", "Không tìm thấy hồ sơ cư dân", HttpStatus.NOT_FOUND));

        if (request.getIdentityNumber() != null && !request.getIdentityNumber().trim().isEmpty()
                && !request.getIdentityNumber().equals(person.getIdentityNumber())) {
            if (personRepository.existsByIdentityNumber(request.getIdentityNumber().trim())) {
                throw new AppException("DUPLICATE_IDENTITY_NUMBER", "Số CMND/CCCD đã tồn tại trên hệ thống", HttpStatus.CONFLICT);
            }
        }

        person.setFullName(request.getFullName());
        person.setPhone(request.getPhone());
        person.setEmail(request.getEmail());
        person.setIdentityNumber(request.getIdentityNumber());
        return mapToDto(person);
    }

    private ResidentDto mapToDto(Person p) {
        return new ResidentDto(
                String.valueOf(p.getPersonId()),
                p.getFullName(),
                p.getPhone(),
                p.getEmail(),
                p.getIdentityNumber()
        );
    }
}
