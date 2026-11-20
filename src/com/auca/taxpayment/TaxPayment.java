package com.auca.taxpayment;

import com.auca.exceptions.ValidationException;
import com.auca.interfaces.Identifiable;
import com.auca.interfaces.Validatable;
import com.auca.taxassessment.TaxAssessment;
import java.math.BigDecimal;
import java.time.LocalDate;

public class TaxPayment implements Identifiable, Validatable {

    private String paymentId;
    private TaxAssessment assessment;
    private String paymentReference;
    private BigDecimal amount;
    private LocalDate paymentDate;
    private String paymentMethod;
    public TaxPayment(String paymentId, TaxAssessment assessment, String paymentReference,
                      BigDecimal amount, LocalDate paymentDate, String paymentMethod) {
        this.paymentId = paymentId;
        this.assessment = assessment;
        this.paymentReference = paymentReference;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.paymentMethod = paymentMethod;
    }


    public String getPaymentId() {
        return paymentId;
    }

    public TaxAssessment getAssessment() {
        return assessment;
    }

    public String getPaymentReference() {
        return paymentReference;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }


    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public void setAssessment(TaxAssessment assessment) {
        this.assessment = assessment;
    }

    public void setPaymentReference(String paymentReference) {
        this.paymentReference = paymentReference;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }


    @Override
    public String getId() {
        return paymentId;
    }

    @Override
    public String getDisplayName() {
        return "Payment " + paymentReference;
    }


    @Override
    public boolean isActive() {
        return true;
    }


    @Override
    public void validate() throws ValidationException {
        if (paymentId == null || paymentId.trim().length() < 2 || paymentId.trim().length() > 30) {
            throw new ValidationException("Payment ID is required (2-30 characters).");
        }
        if (assessment == null) {
            throw new ValidationException("An assessment is required.");
        }
        if (!"CONFIRMED".equals(assessment.getStatus())) {
            throw new ValidationException("Payments require a CONFIRMED assessment.");
        }
        if (paymentReference == null || paymentReference.trim().length() < 3
                || paymentReference.trim().length() > 40) {
            throw new ValidationException("Payment reference is required (3-40 characters).");
        }
        if (amount == null || amount.signum() <= 0) {
            throw new ValidationException("Payment amount must be greater than zero.");
        }
        if (amount.stripTrailingZeros().scale() > 2) {
            throw new ValidationException("Payment amount cannot have more than 2 decimal places.");
        }
        if (amount.compareTo(new BigDecimal("999999999999.99")) > 0) {
            throw new ValidationException("Payment amount cannot exceed 999999999999.99.");
        }
        if (paymentDate == null || paymentDate.isAfter(LocalDate.now())) {
            throw new ValidationException("Payment date is required and cannot be in the future.");
        }
        if (paymentDate.isBefore(assessment.getAssessmentDate())) {
            throw new ValidationException("Payment date cannot be before the assessment date.");
        }
        if (paymentMethod == null || !(paymentMethod.equals("CASH") || paymentMethod.equals("MOBILE_MONEY")
                || paymentMethod.equals("BANK_TRANSFER") || paymentMethod.equals("CARD"))) {
            throw new ValidationException("Method must be CASH, MOBILE_MONEY, BANK_TRANSFER or CARD.");
        }
    }

    @Override
    public void printDetails() {
        System.out.println("=== TAX PAYMENT ===");
        System.out.println("Payment ID: " + paymentId);
        System.out.println("Assessment: " + assessment.getAssessmentId());
        System.out.println("Payment reference: " + paymentReference);
        System.out.println("Amount: " + amount);
        System.out.println("Payment date: " + paymentDate);
        System.out.println("Payment method: " + paymentMethod);
    }

    @Override
    public String getSummary() {
        return "[PAYMENT] " + paymentId + " | " + paymentReference + " | "
                + assessment.getAssessmentId() + " | " + amount + " | " + paymentDate;
    }
}