package com.bluemoon.repository;

import com.bluemoon.entity.CollectionCampaign;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CollectionCampaignRepository extends JpaRepository<CollectionCampaign, Long> {
    Optional<CollectionCampaign> findByFeeTypeIdAndCyclePeriod(Long feeTypeId, String cyclePeriod);
    List<CollectionCampaign> findByStatus(CollectionCampaign.CampaignStatus status);
}
