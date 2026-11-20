package com.auca.taxrefund;

import com.auca.abstracts.RegisteredTaxpayer;
import com.auca.exceptions.InvalidStatusTransitionException;
import com.auca.exceptions.UnauthorizedApprovalException;
import com.auca.exceptions.ValidationException;
import com.auca.interfaces.Identifiable;
import com.auca.interfaces.Validatable;
import com.auca.revenueofficer.RevenueOfficer;
import com.auca.taxassessment.TaxAssessment;
import java.math.BigDecimal;
import java.time.LocalDate;

public class TaxRefund implements Identifiable, Validatable {

    private String refundId;
    private RegisteredTaxpayer taxpayer;
    private TaxAssessment assessment;
    private BigDecimal requestedAmount;
    private LocalDate requestDate;
    private String status;


    private RevenueOfficer approvedBy;

    public TaxRefund(String refundId, RegisteredTaxpayer taxpayer, TaxAssessment assessment,
                     BigDecimal requestedAmount, LocalDate requestDate, String status) {
        this.refundId = refundId;
        this.taxpayer = taxpayer;
        this.assessment = assessment;
        this.requestedAmount = requestedAmount;
        this.requestDate = requestDate;
        this.status = status;
    }


    public String getRefundId() {
        return refundId;
    }

    public RegisteredTaxpayer getTaxpayer() {
        return taxpayer;
    }

    public TaxAssessment getAssessment() {
        return assessment;
    }

    public BigDecimal getRequestedAmount() {
        return requestedAmount;
    }

    public LocalDate getRequestDate() {
        return requestDate;
    }

    public String getStatus() {
        return status;
    }

    public RevenueOfficer getApprovedBy() {
        return approvedBy;
    }


    public void setRefundId(String refundId) {
        this.refundId = refundId;
    }

    public void setTaxpayer(RegisteredTaxpayer taxpayer) {
        this.taxpayer = taxpayer;
    }

    public void setAssessment(TaxAssessment assessment) {
        this.assessment = assessment;
    }

    public void setRequestedAmount(BigDecimal requestedAmount) {
        this.requestedAmount = requestedAmount;
    }

    public void setRequestDate(LocalDate requestDate) {
        this.requestDate = requestDate;
    }


    public void approve(RevenueOfficer officer)
            throws UnauthorizedApprovalException, InvalidStatusTransitionException {
        if (!"PENDING".equals(status)) {
            throw new InvalidStatusTransitionException(
                    "Only a PENDING refund can be approved. Current status: " + status);
        }
        if (officer == null || !officer.isActive()) {
            throw new UnauthorizedApprovalException("The approving officer must be active.");
        }
        if (!officer.canApprove(requestedAmount)) {
            throw new UnauthorizedApprovalException("Refund " + requestedAmount
                    + " exceeds the officer's approval limit " + officer.getApprovalLimit() + ".");
        }
        approvedBy = officer;
        status = "APPROVED";
    }

    public void reject() throws InvalidStatusTransitionException {
        if (!"PENDING".equals(status)) {
            throw new InvalidStatusTransitionException(
                    "Only a PENDING refund can be rejected. Current status: " + status);
        }
        status = "REJECTED";
    }


    @Override
    public String getId() {
        return refundId;
    }

    @Override
    public String getDisplayName() {
        return "Refund " + refundId + " for " + taxpayer.getTin();
    }

    @Override
    public boolean isActive() {
        return !"REJECTED".equals(status);
    }


    @Override
    public void validate() throws ValidationException {
        if (refundId == null || refundId.trim().length() < 2 || refundId.trim().length() > 30) {
            throw new ValidationException("Refund ID is required (2-30 characters).");
        }
        if (taxpayer == null) {
            throw new ValidationException("A registered taxpayer is required.");
        }
        if (assessment == null) {
            throw new ValidationException("A supporting assessment is required.");
        }
        if (!taxpayer.getTin().equals(assessment.getDeclaration().getObligation().getTaxpayer().getTin())) {
            throw new ValidationException("The assessment does not belong to this taxpayer.");
        }
        if (requestedAmount == null || requestedAmount.signum() <= 0) {
            throw new ValidationException("Requested amount must be greater than zero.");
        }
        if (requestedAmount.stripTrailingZeros().scale() > 2
                || requestedAmount.compareTo(new BigDecimal("999999999999.99")) > 0) {
            throw new ValidationException("Requested amount: max 2 decimals and <= 999999999999.99.");
        }
        if (requestDate == null || requestDate.isAfter(LocalDate.now())) {
            throw new ValidationException("Request date is required and cannot be in the future.");
        }
        if (status == null || !(status.equals("PENDING") || status.equals("APPROVED")
                || status.equals("REJECTED"))) {
            throw new ValidationException("Status must be PENDING, APPROVED or REJECTED.");
        }
    }

    @Override
    public void printDetails() {
        System.out.println("=== TAX REFUND ===");
        System.out.println("Refund ID: " + refundId);
        System.out.println("Taxpayer: " + taxpayer.getTin() + " - " + taxpayer.getDisplayName());
        System.out.println("Assessment: " + assessment.getAssessmentId());
        System.out.println("Requested amount: " + requestedAmount);
        System.out.println("Request date: " + requestDate);
        System.out.println("Status: " + status);
        if (approvedBy != null) {
            System.out.println("Approved by: " + approvedBy.getDisplayName());
        }
    }

    @Override
    public String getSummary() {
        return "[REFUND] " + refundId + " | " + taxpayer.getTin() + " | "
                + assessment.getAssessmentId() + " | " + requestedAmount + " | " + status;
    }
}