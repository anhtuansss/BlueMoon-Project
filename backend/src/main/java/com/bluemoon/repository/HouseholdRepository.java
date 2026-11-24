package com.bluemoon.repository;

import com.bluemoon.entity.Household;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HouseholdRepository extends JpaRepository<Household, Long> {
    List<Household> findByIsActiveTrue();
    Page<Household> findByHouseholdNameContainingIgnoreCase(String keyword, Pageable pageable);
}
