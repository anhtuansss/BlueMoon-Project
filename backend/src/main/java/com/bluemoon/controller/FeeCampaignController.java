package com.bluemoon.controller;

import com.bluemoon.dto.response.ApiResponse;
import com.bluemoon.dto.fee.*;
import com.bluemoon.security.UserPrincipal;
import com.bluemoon.service.FeeCampaignService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller cho Quản lý khoản thu và đợt thu phí (Thành viên 3 - Long - F05).
 */
@RestController
@RequestMapping("/api/v1")
public class FeeCampaignController {

    private final FeeCampaignService feeCampaignService;

    public FeeCampaignController(FeeCampaignService feeCampaignService) {
        this.feeCampaignService = feeCampaignService;
    }

    @GetMapping("/fee-types")
    @PreAuthorize("hasAnyRole('BQT', 'NV')")
    public ResponseEntity<ApiResponse<List<FeeTypeDto>>> getFeeTypes() {
        List<FeeTypeDto> feeTypes = feeCampaignService.getActiveFeeTypes();
        return ResponseEntity.ok(ApiResponse.success(feeTypes));
    }

    @PostMapping("/fee-campaigns/preview")
    @PreAuthorize("hasAnyRole('BQT', 'NV')")
    public ResponseEntity<ApiResponse<FeeCampaignPreviewResponse>> previewFeeCampaign(
            @Valid @RequestBody FeeCampaignPreviewRequest request
    ) {
        FeeCampaignPreviewResponse preview = feeCampaignService.previewCampaign(request);
        return ResponseEntity.ok(ApiResponse.success(preview));
    }

    @PostMapping("/fee-campaigns")
    @PreAuthorize("hasAnyRole('BQT', 'NV')")
    public ResponseEntity<ApiResponse<FeeCampaignResponse>> createFeeCampaign(
            @Valid @RequestBody CreateFeeCampaignRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        Long accountId = userPrincipal != null ? userPrincipal.getId() : 1L;
        FeeCampaignResponse created = feeCampaignService.createCampaign(request, accountId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(created));
    }

    @GetMapping("/fee-campaigns")
    @PreAuthorize("hasAnyRole('BQT', 'NV', 'CU_DAN')")
    public ResponseEntity<ApiResponse<List<FeeCampaignResponse>>> getFeeCampaigns() {
        List<FeeCampaignResponse> campaigns = feeCampaignService.getCampaigns();
        return ResponseEntity.ok(ApiResponse.success(campaigns));
    }

    @GetMapping("/fee-obligations")
    @PreAuthorize("hasAnyRole('BQT', 'NV', 'CU_DAN')")
    public ResponseEntity<ApiResponse<List<FeeObligationDto>>> getFeeObligations(
            @RequestParam(required = false) Long campaignId,
            @RequestParam(required = false) Long apartmentId
    ) {
        List<FeeObligationDto> obligations = feeCampaignService.getObligations(campaignId, apartmentId);
        return ResponseEntity.ok(ApiResponse.success(obligations));
    }
}
