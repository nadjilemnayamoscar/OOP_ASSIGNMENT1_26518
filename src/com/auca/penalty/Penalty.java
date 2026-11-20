package com.auca.penalty;

import com.auca.exceptions.ValidationException;
import com.auca.interfaces.Identifiable;
import com.auca.interfaces.Validatable;
import com.auca.taxobligation.TaxObligation;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class Penalty implements Identifiable, Validatable {


    public static final BigDecimal LATE_FILING_RATE = new BigDecimal("5");

    private String penaltyId;
    private TaxObligation obligation;
    private String penaltyType;
    private BigDecimal baseAmount;
    private BigDecimal penaltyRate;
    private BigDecimal penaltyAmount;

    public Penalty(String penaltyId, TaxObligation obligation, String penaltyType,
                   BigDecimal baseAmount, BigDecimal penaltyRate, BigDecimal penaltyAmount) {
        this.penaltyId = penaltyId;
        this.obligation = obligation;
        this.penaltyType = penaltyType;
        this.baseAmount = baseAmount;
        this.penaltyRate = penaltyRate;
        this.penaltyAmount = penaltyAmount;
    }


    public String getPenaltyId() {
        return penaltyId;
    }

    public TaxObligation getObligation() {
        return obligation;
    }

    public String getPenaltyType() {
        return penaltyType;
    }

    public BigDecimal getBaseAmount() {
        return baseAmount;
    }

    public BigDecimal getPenaltyRate() {
        return penaltyRate;
    }

    public BigDecimal getPenaltyAmount() {
        return penaltyAmount;
    }


    public void setPenaltyId(String penaltyId) {
        this.penaltyId = penaltyId;
    }

    public void setObligation(TaxObligation obligation) {
        this.obligation = obligation;
    }

    public void setPenaltyType(String penaltyType) {
        this.penaltyType = penaltyType;
    }

    public void setBaseAmount(BigDecimal baseAmount) {
        this.baseAmount = baseAmount;
    }

    public void setPenaltyRate(BigDecimal penaltyRate) {
        this.penaltyRate = penaltyRate;
    }

    public void setPenaltyAmount(BigDecimal penaltyAmount) {
        this.penaltyAmount = penaltyAmount;
    }


    public void calculatePenalty() {
        penaltyAmount = baseAmount.multiply(penaltyRate)
                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
    }


    @Override
    public String getId() {
        return penaltyId;
    }

    @Override
    public String getDisplayName() {
        return "Penalty " + penaltyId + " (" + penaltyType + ")";
    }

    @Override
    public boolean isActive() {
        return true;
    }


    @Override
    public void validate() throws ValidationException {
        if (penaltyId == null || penaltyId.trim().length() < 2 || penaltyId.trim().length() > 30) {
            throw new ValidationException("Penalty ID is required (2-30 characters).");
        }
        if (obligation == null) {
            throw new ValidationException("An obligation is required.");
        }
        if (penaltyType == null || penaltyType.trim().length() < 2 || penaltyType.trim().length() > 40) {
            throw new ValidationException("Penalty type is required (2-40 characters).");
        }
        if (baseAmount == null || baseAmount.signum() < 0
                || baseAmount.stripTrailingZeros().scale() > 2
                || baseAmount.compareTo(new BigDecimal("999999999999.99")) > 0) {
            throw new ValidationException("Base amount must be 0 or more, with at most 2 decimals.");
        }
        if (penaltyRate == null || penaltyRate.signum() < 0
                || penaltyRate.compareTo(new BigDecimal("100")) > 0) {
            throw new ValidationException("Penalty rate must be between 0 and 100 inclusive.");
        }
        if (penaltyAmount == null || penaltyAmount.signum() < 0) {
            throw new ValidationException("Penalty amount is required and cannot be negative.");
        }
        BigDecimal expected = baseAmount.multiply(penaltyRate)
                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        if (penaltyAmount.compareTo(expected) != 0) {
            throw new ValidationException("Penalty amount must equal " + expected + ".");
        }
    }

    @Override
    public void printDetails() {
        System.out.println("=== PENALTY ===");
        System.out.println("Penalty ID: " + penaltyId);
        System.out.println("Obligation: " + obligation.getObligationId());
        System.out.println("Penalty type: " + penaltyType);
        System.out.println("Base amount: " + baseAmount);
        System.out.println("Penalty rate (%): " + penaltyRate);
        System.out.println("Penalty amount: " + penaltyAmount);
    }

    @Override
    public String getSummary() {
        return "[PENALTY] " + penaltyId + " | " + obligation.getObligationId() + " | "
                + penaltyType + " | " + penaltyAmount;
    }
}