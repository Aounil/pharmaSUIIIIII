package com.pharmacy.pharmacist.controller;

import com.pharmacy.pharmacist.dto.PrescriptionQueueItemDTO;
import com.pharmacy.pharmacist.security.PharmacistPrincipal;
import com.pharmacy.pharmacist.service.PrescriptionQueueService;
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
public class PrescriptionQueueController {

    private final PrescriptionQueueService prescriptionQueueService;

    public PrescriptionQueueController(PrescriptionQueueService prescriptionQueueService) {
        this.prescriptionQueueService = prescriptionQueueService;
    }

    @GetMapping("/prescriptions")
    @Operation(summary = "Get incoming prescription queue ordered by arrival time")
    @ApiResponse(responseCode = "200", description = "Queue retrieved successfully")
    public Page<PrescriptionQueueItemDTO> getQueue(
            @AuthenticationPrincipal PharmacistPrincipal principal,
            @PageableDefault(size = 10) Pageable pageable) {
        return prescriptionQueueService.getQueue(principal.getPharmacyId(), pageable);
    }
}
