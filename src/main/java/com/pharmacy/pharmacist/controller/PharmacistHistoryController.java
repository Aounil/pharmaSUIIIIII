package com.pharmacy.pharmacist.controller;

import com.pharmacy.pharmacist.dto.PrescriptionHistoryItemDTO;
import com.pharmacy.pharmacist.security.PharmacistPrincipal;
import com.pharmacy.pharmacist.service.PharmacistHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/pharmacist")
@PreAuthorize("hasRole('PHARMACIST')")
@Tag(name = "Pharmacist", description = "Pharmacist prescription operations")
public class PharmacistHistoryController {

    private final PharmacistHistoryService historyService;

    public PharmacistHistoryController(PharmacistHistoryService historyService) {
        this.historyService = historyService;
    }

    @GetMapping("/prescriptions/history")
    @Operation(summary = "Get processed prescription history ordered by most recently updated")
    @ApiResponse(responseCode = "200", description = "History retrieved successfully")
    public Page<PrescriptionHistoryItemDTO> getHistory(@AuthenticationPrincipal PharmacistPrincipal principal,
                                                        @PageableDefault(size = 10) Pageable pageable) {
        return historyService.getHistory(principal.getPharmacyId(), pageable);
    }
}
