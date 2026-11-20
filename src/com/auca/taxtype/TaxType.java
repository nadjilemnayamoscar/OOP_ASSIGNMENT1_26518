package com.auca.taxtype;

import com.auca.exceptions.ValidationException;
import com.auca.interfaces.Identifiable;
import com.auca.interfaces.Validatable;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class TaxType implements Identifiable, Validatable {

    private String taxTypeId;
    private String taxCode;
    private String taxName;
    private BigDecimal rate;
    private String filingFrequency;
    private boolean active;

    public TaxType(String taxTypeId, String taxCode, String taxName,
                   BigDecimal rate, String filingFrequency, boolean active) {
        this.taxTypeId = taxTypeId;
        this.taxCode = taxCode;
        this.taxName = taxName;
        this.rate = rate;
        this.filingFrequency = filingFrequency;
        this.active = active;
    }


    public String getTaxTypeId() {
        return taxTypeId;
    }

    public String getTaxCode() {
        return taxCode;
    }

    public String getTaxName() {
        return taxName;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public String getFilingFrequency() {
        return filingFrequency;
    }


    public void setTaxTypeId(String taxTypeId) {
        this.taxTypeId = taxTypeId;
    }

    public void setTaxCode(String taxCode) {
        this.taxCode = taxCode;
    }

    public void setTaxName(String taxName) {
        this.taxName = taxName;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public void setFilingFrequency(String filingFrequency) {
        this.filingFrequency = filingFrequency;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public BigDecimal calculateTax(BigDecimal taxableAmount) {
        return taxableAmount.multiply(rate).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
    }


    @Override
    public String getId() {
        return taxTypeId;
    }

    @Override
    public String getDisplayName() {
        return taxCode + " - " + taxName;
    }

    @Override
    public boolean isActive() {
        return active;
    }


    @Override
    public void validate() throws ValidationException {
        if (taxTypeId == null || taxTypeId.trim().length() < 2 || taxTypeId.trim().length() > 30) {
            throw new ValidationException("Tax type ID is required (2-30 characters).");
        }
        if (taxCode == null || taxCode.trim().length() < 2 || taxCode.trim().length() > 20) {
            throw new ValidationException("Tax code is required (2-20 characters).");
        }
        if (taxName == null || taxName.trim().length() < 2 || taxName.trim().length() > 60) {
            throw new ValidationException("Tax name is required (2-60 characters).");
        }
        if (rate == null || rate.signum() < 0 || rate.compareTo(new BigDecimal("100")) > 0) {
            throw new ValidationException("Rate must be between 0 and 100 inclusive.");
        }
        if (rate.stripTrailingZeros().scale() > 2) {
            throw new ValidationException("Rate cannot have more than 2 decimal places.");
        }
        if (filingFrequency == null || !(filingFrequency.equals("MONTHLY")
                || filingFrequency.equals("QUARTERLY") || filingFrequency.equals("ANNUAL"))) {
            throw new ValidationException("Filing frequency must be MONTHLY, QUARTERLY or ANNUAL.");
        }
    }

    @Override
    public void printDetails() {
        System.out.println("=== TAX TYPE ===");
        System.out.println("Tax type ID: " + taxTypeId);
        System.out.println("Tax code: " + taxCode);
        System.out.println("Tax name: " + taxName);
        System.out.println("Rate (%): " + rate);
        System.out.println("Filing frequency: " + filingFrequency);
        System.out.println("Active: " + (active ? "YES" : "NO"));
    }

    @Override
    public String getSummary() {
        return "[TAX TYPE] " + taxTypeId + " | " + taxCode + " | " + taxName
                + " | " + rate + "% | " + filingFrequency;
    }
}