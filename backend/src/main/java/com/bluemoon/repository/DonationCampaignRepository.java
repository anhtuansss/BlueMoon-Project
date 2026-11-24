package com.bluemoon.repository;

import com.bluemoon.entity.DonationCampaign;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DonationCampaignRepository extends JpaRepository<DonationCampaign, Long> {
    List<DonationCampaign> findByStatus(DonationCampaign.DonationStatus status);
}
