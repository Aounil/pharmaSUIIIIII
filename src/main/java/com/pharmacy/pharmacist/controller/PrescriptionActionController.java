package com.pharmacy.pharmacist.controller;

import com.pharmacy.pharmacist.dto.PrescriptionActionRequest;
import com.pharmacy.pharmacist.dto.PrescriptionDetailDTO;
import com.pharmacy.pharmacist.security.PharmacistPrincipal;
import com.pharmacy.pharmacist.service.PrescriptionActionServiceI;
import com.pharmacy.pharmacist.repository.PharmacistPrescriptionRepository;
import com.pharmacy.shared.storage.FileStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/pharmacist")
@PreAuthorize("hasRole('PHARMACIST')")
@Tag(name = "Pharmacist", description = "Pharmacist prescription operations")
public class PrescriptionActionController {

    private final PrescriptionActionServiceI prescriptionActionService;
    private final PharmacistPrescriptionRepository prescriptionRepository;
    private final FileStorageService fileStorageService;

    public PrescriptionActionController(PrescriptionActionServiceI prescriptionActionService,
                                       PharmacistPrescriptionRepository prescriptionRepository,
                                       FileStorageService fileStorageService) {
        this.prescriptionActionService = prescriptionActionService;
        this.prescriptionRepository = prescriptionRepository;
        this.fileStorageService = fileStorageService;
    }

    @PatchMapping("/prescriptions/{id}/accept")
    @Operation(summary = "Accept a received prescription and move it into preparation")
    @ApiResponse(responseCode = "200", description = "Prescription accepted")
    @ApiResponse(responseCode = "404", description = "Prescription not found")
    @ApiResponse(responseCode = "409", description = "Invalid prescription state")
    public PrescriptionDetailDTO acceptPrescription(@PathVariable Long id,
                                                     @RequestBody @Valid PrescriptionActionRequest request,
                                                     @AuthenticationPrincipal PharmacistPrincipal principal) {
        return prescriptionActionService.accept(id,
                principal.getPharmacyId(),
                request.getTotalAmount(),
                request.getPricingType());
    }

    @PatchMapping("/prescriptions/{id}/reject")
    @Operation(summary = "Reject a received prescription")
    @ApiResponse(responseCode = "200", description = "Prescription rejected")
    @ApiResponse(responseCode = "404", description = "Prescription not found")
    @ApiResponse(responseCode = "409", description = "Invalid prescription state")
    public PrescriptionDetailDTO rejectPrescription(@PathVariable Long id,
                                                     @RequestBody @Valid PrescriptionActionRequest request,
                                                     @AuthenticationPrincipal PharmacistPrincipal principal) {
        return prescriptionActionService.reject(id,
                principal.getPharmacyId(),
                request.getComment());
    }

    @PatchMapping("/prescriptions/{id}/ready")
    @Operation(summary = "Mark an in-preparation prescription as ready")
    @ApiResponse(responseCode = "200", description = "Prescription marked ready")
    @ApiResponse(responseCode = "404", description = "Prescription not found")
    @ApiResponse(responseCode = "409", description = "Invalid prescription state")
    public PrescriptionDetailDTO markReady(@PathVariable Long id,
                                            @AuthenticationPrincipal PharmacistPrincipal principal) {
        return prescriptionActionService.markReady(id, principal.getPharmacyId());
    }

    @GetMapping("/prescriptions/{id}/file")
    @Operation(summary = "Download the uploaded prescription file")
    @ApiResponse(responseCode = "200", description = "Prescription file downloaded")
    @ApiResponse(responseCode = "404", description = "Prescription or file not found")
    public ResponseEntity<Resource> downloadPrescriptionFile(@PathVariable Long id,
                                                              @AuthenticationPrincipal PharmacistPrincipal principal) {
        return prescriptionRepository.findByIdAndPharmacyId(id, principal.getPharmacyId())
                .map(prescription -> {
                    Resource resource = fileStorageService.load(prescription.getFilePath());
                    String filename = Optional.ofNullable(prescription.getFilePath()).orElse("prescription");
                    return ResponseEntity.ok()
                            .contentType(MediaType.APPLICATION_OCTET_STREAM)
                            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                            .body(resource);
                })
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Prescription not found"));
    }
}
