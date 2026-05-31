package com.pharmacy.pharmacist.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

/**
 * Request payload for pharmacist actions on a prescription.
 * <p>
 * action values: ACCEPT, REJECT, MARK_READY.
 * comment is required only when action is REJECT.
 * pricingType defaults to STANDARD when not provided.
 */
public class PrescriptionActionRequest {

    @NotBlank
    @JsonProperty("action")
    private String action;

    @JsonProperty("comment")
    private String comment;

    @JsonProperty("totalAmount")
    private BigDecimal totalAmount;

    @JsonProperty("pricingType")
    private String pricingType;

    public PrescriptionActionRequest() {
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getPricingType() {
        return pricingType == null || pricingType.isBlank() ? "STANDARD" : pricingType;
    }

    public void setPricingType(String pricingType) {
        this.pricingType = pricingType;
    }
}
