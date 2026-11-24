package com.bluemoon.repository;

import com.bluemoon.entity.Apartment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApartmentRepository extends JpaRepository<Apartment, Long> {
    Optional<Apartment> findByApartmentNumber(String apartmentNumber);
    boolean existsByApartmentNumber(String apartmentNumber);
    List<Apartment> findByIsActiveTrue();
    Page<Apartment> findByIsActive(Boolean isActive, Pageable pageable);
}
