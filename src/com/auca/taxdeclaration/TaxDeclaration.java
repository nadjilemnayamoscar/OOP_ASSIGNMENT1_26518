package com.auca.taxdeclaration;

import com.auca.exceptions.InvalidTaxCalculationException;
import com.auca.exceptions.ValidationException;
import com.auca.interfaces.Identifiable;
import com.auca.interfaces.Validatable;
import com.auca.taxobligation.TaxObligation;
import java.math.BigDecimal;
import java.time.LocalDate;

public class TaxDeclaration implements Identifiable, Validatable {

    private String declarationId;
    private TaxObligation obligation;
    private LocalDate filingDate;
    private BigDecimal taxableAmount;
    private BigDecimal declaredTax;
    private String status;
    public TaxDeclaration(String declarationId, TaxObligation obligation, LocalDate filingDate,
                          BigDecimal taxableAmount, BigDecimal declaredTax, String status) {
        this.declarationId = declarationId;
        this.obligation = obligation;
        this.filingDate = filingDate;
        this.taxableAmount = taxableAmount;
        this.declaredTax = declaredTax;
        this.status = status;
    }

    public String getDeclarationId() {
        return declarationId;
    }

    public TaxObligation getObligation() {
        return obligation;
    }

    public LocalDate getFilingDate() {
        return filingDate;
    }

    public BigDecimal getTaxableAmount() {
        return taxableAmount;
    }

    public BigDecimal getDeclaredTax() {
        return declaredTax;
    }

    public String getStatus() {
        return status;
    }


    public void setDeclarationId(String declarationId) {
        this.declarationId = declarationId;
    }

    public void setObligation(TaxObligation obligation) {
        this.obligation = obligation;
    }

    public void setFilingDate(LocalDate filingDate) {
        this.filingDate = filingDate;
    }

    public void setTaxableAmount(BigDecimal taxableAmount) {
        this.taxableAmount = taxableAmount;
    }

    public void setDeclaredTax(BigDecimal declaredTax) {
        this.declaredTax = declaredTax;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public BigDecimal getExpectedTax() {
        return obligation.getTaxType().calculateTax(taxableAmount);
    }


    public void checkCalculation() throws InvalidTaxCalculationException {
        BigDecimal expected = getExpectedTax();
        if (declaredTax.subtract(expected).abs().compareTo(new BigDecimal("0.01")) > 0) {
            throw new InvalidTaxCalculationException("Declared tax " + declaredTax
                    + " does not match expected tax " + expected + ".");
        }
    }

    public boolean isLate() {
        return filingDate.isAfter(obligation.getDueDate());
    }


    public void applyFilingStatus() {
        if (isLate()) {
            status = "LATE";
        } else {
            status = "FILED";
        }
    }


    @Override
    public String getId() {
        return declarationId;
    }

    @Override
    public String getDisplayName() {
        return "Declaration " + declarationId + " for " + obligation.getObligationId();
    }


    @Override
    public boolean isActive() {
        return !"REJECTED".equals(status);
    }


    @Override
    public void validate() throws ValidationException {
        if (declarationId == null || declarationId.trim().length() < 2 || declarationId.trim().length() > 30) {
            throw new ValidationException("Declaration ID is required (2-30 characters).");
        }
        if (obligation == null) {
            throw new ValidationException("An obligation is required.");
        }
        if (filingDate == null || filingDate.isAfter(LocalDate.now())) {
            throw new ValidationException("Filing date is required and cannot be in the future.");
        }
        checkMoney(taxableAmount, "Taxable amount");
        checkMoney(declaredTax, "Declared tax");
        if (status == null || !(status.equals("FILED") || status.equals("LATE")
                || status.equals("REJECTED") || status.equals("ASSESSED"))) {
            throw new ValidationException("Status must be FILED, LATE, REJECTED or ASSESSED.");
        }
    }

    private static void checkMoney(BigDecimal value, String field) throws ValidationException {
        if (value == null) {
            throw new ValidationException(field + " is required.");
        }
        if (value.signum() < 0) {
            throw new ValidationException(field + " cannot be negative.");
        }
        if (value.stripTrailingZeros().scale() > 2) {
            throw new ValidationException(field + " cannot have more than 2 decimal places.");
        }
        if (value.compareTo(new BigDecimal("999999999999.99")) > 0) {
            throw new ValidationException(field + " cannot exceed 999999999999.99.");
        }
    }

    @Override
    public void printDetails() {
        System.out.println("=== TAX DECLARATION ===");
        System.out.println("Declaration ID: " + declarationId);
        System.out.println("Obligation: " + obligation.getObligationId());
        System.out.println("Filing date: " + filingDate);
        System.out.println("Taxable amount: " + taxableAmount);
        System.out.println("Declared tax: " + declaredTax);
        System.out.println("Status: " + status);
    }

    @Override
    public String getSummary() {
        return "[DECLARATION] " + declarationId + " | " + obligation.getObligationId()
                + " | filed " + filingDate + " | tax " + declaredTax + " | " + status;
    }
}