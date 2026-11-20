package com.auca.taxclearance;

import com.auca.abstracts.RegisteredTaxpayer;
import com.auca.exceptions.UnauthorizedApprovalException;
import com.auca.exceptions.ValidationException;
import com.auca.interfaces.Identifiable;
import com.auca.interfaces.Validatable;
import com.auca.revenueofficer.RevenueOfficer;
import java.time.LocalDate;

public class TaxClearance implements Identifiable, Validatable {

    private String clearanceId;
    private RegisteredTaxpayer taxpayer;
    private LocalDate requestDate;
    private LocalDate expiryDate;
    private String status;
    private RevenueOfficer approvedBy;

    public TaxClearance(String clearanceId, RegisteredTaxpayer taxpayer, LocalDate requestDate,
                        LocalDate expiryDate, String status, RevenueOfficer approvedBy) {
        this.clearanceId = clearanceId;
        this.taxpayer = taxpayer;
        this.requestDate = requestDate;
        this.expiryDate = expiryDate;
        this.status = status;
        this.approvedBy = approvedBy;
    }


    public String getClearanceId() {
        return clearanceId;
    }

    public RegisteredTaxpayer getTaxpayer() {
        return taxpayer;
    }

    public LocalDate getRequestDate() {
        return requestDate;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public String getStatus() {
        return status;
    }

    public RevenueOfficer getApprovedBy() {
        return approvedBy;
    }


    public void setClearanceId(String clearanceId) {
        this.clearanceId = clearanceId;
    }

    public void setTaxpayer(RegisteredTaxpayer taxpayer) {
        this.taxpayer = taxpayer;
    }

    public void setRequestDate(LocalDate requestDate) {
        this.requestDate = requestDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setApprovedBy(RevenueOfficer approvedBy) {
        this.approvedBy = approvedBy;
    }

    public void approve(RevenueOfficer officer) throws UnauthorizedApprovalException {
        if (officer == null || !officer.isActive()) {
            throw new UnauthorizedApprovalException("The approving officer must be active.");
        }
        if (!taxpayer.isActive()) {
            throw new UnauthorizedApprovalException("The taxpayer must be active.");
        }
        approvedBy = officer;
        status = "APPROVED";
    }

    public boolean isValidOn(LocalDate date) {
        return "APPROVED".equals(status) && !date.isAfter(expiryDate);
    }


    @Override
    public String getId() {
        return clearanceId;
    }

    @Override
    public String getDisplayName() {
        return "Clearance " + clearanceId + " for " + taxpayer.getTin();
    }

    @Override
    public boolean isActive() {
        return isValidOn(LocalDate.now());
    }


    @Override
    public void validate() throws ValidationException {
        if (clearanceId == null || clearanceId.trim().length() < 2 || clearanceId.trim().length() > 30) {
            throw new ValidationException("Clearance ID is required (2-30 characters).");
        }
        if (taxpayer == null) {
            throw new ValidationException("A registered taxpayer is required.");
        }
        if (requestDate == null || requestDate.isAfter(LocalDate.now())) {
            throw new ValidationException("Request date is required and cannot be in the future.");
        }
        if (expiryDate == null || !expiryDate.isAfter(requestDate)) {
            throw new ValidationException("Expiry date must be after the request date.");
        }
        if (status == null || !(status.equals("PENDING") || status.equals("APPROVED")
                || status.equals("REJECTED"))) {
            throw new ValidationException("Status must be PENDING, APPROVED or REJECTED.");
        }
        if ("APPROVED".equals(status) && (approvedBy == null || !approvedBy.isActive())) {
            throw new ValidationException("An approved clearance needs an active approving officer.");
        }
    }

    @Override
    public void printDetails() {
        System.out.println("=== TAX CLEARANCE ===");
        System.out.println("Clearance ID: " + clearanceId);
        System.out.println("Taxpayer: " + taxpayer.getTin() + " - " + taxpayer.getDisplayName());
        System.out.println("Request date: " + requestDate);
        System.out.println("Expiry date: " + expiryDate);
        System.out.println("Status: " + status);
        System.out.println("Approved by: " + (approvedBy == null ? "-" : approvedBy.getDisplayName()));
    }

    @Override
    public String getSummary() {
        return "[CLEARANCE] " + clearanceId + " | " + taxpayer.getTin() + " | " + status
                + " | expires " + expiryDate;
    }
}