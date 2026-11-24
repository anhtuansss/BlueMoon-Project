package com.bluemoon.repository;

import com.bluemoon.entity.HouseholdApartment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HouseholdApartmentRepository extends JpaRepository<HouseholdApartment, Long> {

    @Query("SELECT ha FROM HouseholdApartment ha WHERE ha.apartment.apartmentId = :apartmentId AND ha.endDate IS NULL")
    Optional<HouseholdApartment> findActiveByApartmentId(@Param("apartmentId") Long apartmentId);

    @Query("SELECT ha FROM HouseholdApartment ha WHERE ha.household.householdId = :householdId AND ha.endDate IS NULL")
    Optional<HouseholdApartment> findActiveByHouseholdId(@Param("householdId") Long householdId);
}
