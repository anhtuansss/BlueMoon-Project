package com.bluemoon.service;

import com.bluemoon.dto.fee.*;

import java.util.List;

public interface FeeCampaignService {
    List<FeeTypeDto> getActiveFeeTypes();
    FeeCampaignPreviewResponse previewCampaign(FeeCampaignPreviewRequest request);
    FeeCampaignResponse createCampaign(CreateFeeCampaignRequest request, Long currentAccountId);
    List<FeeCampaignResponse> getCampaigns();
    List<FeeObligationDto> getObligations(Long campaignId, Long apartmentId);
}
