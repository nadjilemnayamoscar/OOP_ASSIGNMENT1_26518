package com.auca.taxobligation;

import com.auca.abstracts.RegisteredTaxpayer;
import com.auca.exceptions.InvalidTaxPeriodException;
import com.auca.exceptions.ValidationException;
import com.auca.interfaces.Identifiable;
import com.auca.interfaces.Validatable;
import com.auca.taxtype.TaxType;

import java.time.LocalDate;

public class TaxObligation implements Identifiable, Validatable {

    private String obligationId;
    private RegisteredTaxpayer taxpayer;
    private TaxType taxType;
    private LocalDate periodStart;
    private LocalDate periodEnd;
    private LocalDate dueDate;

    public TaxObligation(String obligationId, RegisteredTaxpayer taxpayer, TaxType taxType,
                         LocalDate periodStart, LocalDate periodEnd, LocalDate dueDate) {
        this.obligationId = obligationId;
        this.taxpayer = taxpayer;
        this.taxType = taxType;
        this.periodStart = periodStart;
        this.periodEnd = periodEnd;
        this.dueDate = dueDate;
    }

    public String getObligationId() {
        return obligationId;
    }

    public RegisteredTaxpayer getTaxpayer() {
        return taxpayer;
    }

    public TaxType getTaxType() {
        return taxType;
    }

    public LocalDate getPeriodStart() {
        return periodStart;
    }

    public LocalDate getPeriodEnd() {
        return periodEnd;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }


    public void setObligationId(String obligationId) {
        this.obligationId = obligationId;
    }

    public void setTaxpayer(RegisteredTaxpayer taxpayer) {
        this.taxpayer = taxpayer;
    }

    public void setTaxType(TaxType taxType) {
        this.taxType = taxType;
    }

    public void setPeriodStart(LocalDate periodStart) {
        this.periodStart = periodStart;
    }

    public void setPeriodEnd(LocalDate periodEnd) {
        this.periodEnd = periodEnd;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }


    public void checkPeriod() throws InvalidTaxPeriodException {
        if (periodStart == null || periodEnd == null || dueDate == null) {
            throw new InvalidTaxPeriodException("Period start, period end and due date are required.");
        }
        if (periodStart.isAfter(periodEnd)) {
            throw new InvalidTaxPeriodException("Period start must be on or before period end.");
        }
        if (dueDate.isBefore(periodEnd)) {
            throw new InvalidTaxPeriodException("Due date must not precede the period end.");
        }
    }


    public boolean overlaps(TaxObligation other) {
        return taxpayer.getTin().equals(other.taxpayer.getTin())
                && taxType.getTaxTypeId().equals(other.taxType.getTaxTypeId())
                && !periodStart.isAfter(other.periodEnd)
                && !periodEnd.isBefore(other.periodStart);
    }

    public boolean isOverdue() {
        return LocalDate.now().isAfter(dueDate);
    }


    @Override
    public String getId() {
        return obligationId;
    }

    @Override
    public String getDisplayName() {
        return obligationId + " (" + taxType.getTaxCode() + ", " + taxpayer.getTin() + ")";
    }

    @Override
    public boolean isActive() {
        return taxpayer.isActive() && taxType.isActive();
    }


    @Override
    public void validate() throws ValidationException {
        if (obligationId == null || obligationId.trim().length() < 2 || obligationId.trim().length() > 30) {
            throw new ValidationException("Obligation ID is required (2-30 characters).");
        }
        if (taxpayer == null) {
            throw new ValidationException("A registered taxpayer is required.");
        }
        if (!taxpayer.isActive()) {
            throw new ValidationException("The taxpayer must be active.");
        }
        if (taxType == null) {
            throw new ValidationException("A tax type is required.");
        }
        if (!taxType.isActive()) {
            throw new ValidationException("The tax type must be active.");
        }
        if (periodStart == null || periodEnd == null || dueDate == null) {
            throw new ValidationException("Period start, period end and due date are required.");
        }
    }

    @Override
    public void printDetails() {
        System.out.println("=== TAX OBLIGATION ===");
        System.out.println("Obligation ID: " + obligationId);
        System.out.println("Taxpayer: " + taxpayer.getTin() + " - " + taxpayer.getDisplayName());
        System.out.println("Tax type: " + taxType.getDisplayName());
        System.out.println("Period start: " + periodStart);
        System.out.println("Period end: " + periodEnd);
        System.out.println("Due date: " + dueDate);
    }

    @Override
    public String getSummary() {
        return "[OBLIGATION] " + obligationId + " | " + taxpayer.getTin() + " | "
                + taxType.getTaxCode() + " | " + periodStart + " to " + periodEnd
                + " | due " + dueDate;
    }
}