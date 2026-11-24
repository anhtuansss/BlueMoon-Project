package com.bluemoon.repository;

import com.bluemoon.entity.FeeObligation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FeeObligationRepository extends JpaRepository<FeeObligation, Long> {
    List<FeeObligation> findByCampaignId(Long campaignId);
    List<FeeObligation> findByApartmentId(Long apartmentId);
    Optional<FeeObligation> findByCampaignIdAndApartmentId(Long campaignId, Long apartmentId);
    List<FeeObligation> findByStatus(FeeObligation.ObligationStatus status);
}
