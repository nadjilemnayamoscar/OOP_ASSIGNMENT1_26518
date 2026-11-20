package com.auca.taxassessment;

import com.auca.exceptions.InvalidStatusTransitionException;
import com.auca.exceptions.ValidationException;
import com.auca.interfaces.Identifiable;
import com.auca.interfaces.Validatable;
import com.auca.taxdeclaration.TaxDeclaration;
import java.math.BigDecimal;
import java.time.LocalDate;

public class TaxAssessment implements Identifiable, Validatable {

    private String assessmentId;
    private TaxDeclaration declaration;
    private BigDecimal assessedTax;
    private BigDecimal adjustmentAmount;
    private LocalDate assessmentDate;
    private String status;

    public TaxAssessment(String assessmentId, TaxDeclaration declaration, BigDecimal assessedTax,
                         BigDecimal adjustmentAmount, LocalDate assessmentDate, String status) {
        this.assessmentId = assessmentId;
        this.declaration = declaration;
        this.assessedTax = assessedTax;
        this.adjustmentAmount = adjustmentAmount;
        this.assessmentDate = assessmentDate;
        this.status = status;
    }


    public String getAssessmentId() {
        return assessmentId;
    }

    public TaxDeclaration getDeclaration() {
        return declaration;
    }

    public BigDecimal getAssessedTax() {
        return assessedTax;
    }

    public BigDecimal getAdjustmentAmount() {
        return adjustmentAmount;
    }

    public LocalDate getAssessmentDate() {
        return assessmentDate;
    }

    public String getStatus() {
        return status;
    }


    public void setAssessmentId(String assessmentId) {
        this.assessmentId = assessmentId;
    }

    public void setDeclaration(TaxDeclaration declaration) {
        this.declaration = declaration;
    }

    public void setAssessedTax(BigDecimal assessedTax) {
        this.assessedTax = assessedTax;
    }

    public void setAdjustmentAmount(BigDecimal adjustmentAmount) {
        this.adjustmentAmount = adjustmentAmount;
    }

    public void setAssessmentDate(LocalDate assessmentDate) {
        this.assessmentDate = assessmentDate;
    }


    public BigDecimal getTotal() {
        return assessedTax.add(adjustmentAmount);
    }

    public void confirm() throws InvalidStatusTransitionException {
        if (!"DRAFT".equals(status)) {
            throw new InvalidStatusTransitionException(
                    "Only a DRAFT assessment can be confirmed. Current status: " + status);
        }
        status = "CONFIRMED";
    }

    public void cancel() throws InvalidStatusTransitionException {
        if ("CANCELLED".equals(status)) {
            throw new InvalidStatusTransitionException("The assessment is already CANCELLED.");
        }
        status = "CANCELLED";
    }


    @Override
    public String getId() {
        return assessmentId;
    }

    @Override
    public String getDisplayName() {
        return "Assessment " + assessmentId + " for " + declaration.getDeclarationId();
    }

    @Override
    public boolean isActive() {
        return !"CANCELLED".equals(status);
    }


    @Override
    public void validate() throws ValidationException {
        if (assessmentId == null || assessmentId.trim().length() < 2 || assessmentId.trim().length() > 30) {
            throw new ValidationException("Assessment ID is required (2-30 characters).");
        }
        if (declaration == null) {
            throw new ValidationException("A declaration is required.");
        }
        if ("REJECTED".equals(declaration.getStatus())) {
            throw new ValidationException("A rejected declaration cannot be assessed.");
        }
        if (assessedTax == null || assessedTax.signum() < 0) {
            throw new ValidationException("Assessed tax is required and cannot be negative.");
        }
        if (adjustmentAmount == null) {
            throw new ValidationException("Adjustment amount is required (use 0 if none).");
        }
        if (assessedTax.stripTrailingZeros().scale() > 2 || adjustmentAmount.stripTrailingZeros().scale() > 2) {
            throw new ValidationException("Amounts cannot have more than 2 decimal places.");
        }
        BigDecimal max = new BigDecimal("999999999999.99");
        if (assessedTax.compareTo(max) > 0 || adjustmentAmount.abs().compareTo(max) > 0) {
            throw new ValidationException("Amounts cannot exceed 999999999999.99.");
        }
        if (getTotal().signum() < 0) {
            throw new ValidationException("The assessment total cannot be negative.");
        }
        if (assessmentDate == null || assessmentDate.isAfter(LocalDate.now())) {
            throw new ValidationException("Assessment date is required and cannot be in the future.");
        }
        if (status == null || !(status.equals("DRAFT") || status.equals("CONFIRMED")
                || status.equals("CANCELLED"))) {
            throw new ValidationException("Status must be DRAFT, CONFIRMED or CANCELLED.");
        }
    }

    @Override
    public void printDetails() {
        System.out.println("=== TAX ASSESSMENT ===");
        System.out.println("Assessment ID: " + assessmentId);
        System.out.println("Declaration: " + declaration.getDeclarationId());
        System.out.println("Assessed tax: " + assessedTax);
        System.out.println("Adjustment amount: " + adjustmentAmount);
        System.out.println("Assessment date: " + assessmentDate);
        System.out.println("Status: " + status);
        System.out.println("Total: " + getTotal());
    }

    @Override
    public String getSummary() {
        return "[ASSESSMENT] " + assessmentId + " | " + declaration.getDeclarationId()
                + " | total " + getTotal() + " | " + status;
    }
}