package com.bluemoon.repository;

import com.bluemoon.entity.FeeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FeeTypeRepository extends JpaRepository<FeeType, Long> {
    Optional<FeeType> findByCode(String code);
    List<FeeType> findByIsActiveTrue();
    boolean existsByCode(String code);
}
