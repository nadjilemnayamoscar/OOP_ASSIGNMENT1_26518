package com.auca.revenueofficer;

import com.auca.abstracts.TaxParty;
import com.auca.exceptions.ValidationException;
import java.math.BigDecimal;
import java.time.LocalDate;

public class RevenueOfficer extends TaxParty {

    private String staffNumber;
    private String department;
    private String role;
    private LocalDate hireDate;
    private BigDecimal approvalLimit;
    private boolean active;

    public RevenueOfficer(String partyId, String firstName, String lastName,
                          String email, String phoneNumber, LocalDate dateOfBirth,
                          String staffNumber, String department, String role,
                          LocalDate hireDate, BigDecimal approvalLimit, boolean active) {
        super(partyId, firstName, lastName, email, phoneNumber, dateOfBirth);
        this.staffNumber = staffNumber;
        this.department = department;
        this.role = role;
        this.hireDate = hireDate;
        this.approvalLimit = approvalLimit;
        this.active = active;
    }


    public String getStaffNumber() {
        return staffNumber;
    }

    public String getDepartment() {
        return department;
    }

    public String getRole() {
        return role;
    }

    public LocalDate getHireDate() {
        return hireDate;
    }

    public BigDecimal getApprovalLimit() {
        return approvalLimit;
    }


    public void setStaffNumber(String staffNumber) {
        this.staffNumber = staffNumber;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setHireDate(LocalDate hireDate) {
        this.hireDate = hireDate;
    }

    public void setApprovalLimit(BigDecimal approvalLimit) {
        this.approvalLimit = approvalLimit;
    }

    public void setActive(boolean active) {
        this.active = active;
    }


    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public String getPartyRole() {
        return "REVENUE OFFICER";
    }

    @Override
    public String getDisplayName() {
        return "Officer: " + super.getDisplayName() + " (" + role + ")";
    }


    public boolean canApprove(BigDecimal amount) {
        return active && amount.compareTo(approvalLimit) <= 0;
    }

    @Override
    public void validate() throws ValidationException {
        super.validate();

        requireText(staffNumber, "Staff number", 3, 30);
        requireText(department, "Department", 2, 60);
        requireText(role, "Role", 2, 60);
        if (hireDate == null || hireDate.isAfter(LocalDate.now())) {
            throw new ValidationException("Hire date is required and cannot be in the future.");
        }
        if (!hireDate.isAfter(getDateOfBirth())) {
            throw new ValidationException("Hire date must be after the birth date.");
        }
        checkMoney(approvalLimit, "Approval limit");
    }

    @Override
    public void printDetails() {
        System.out.println("=== REVENUE OFFICER ===");
        super.printDetails();
        System.out.println("Staff number: " + staffNumber);
        System.out.println("Department: " + department);
        System.out.println("Role: " + role);
        System.out.println("Hire date: " + hireDate);
        System.out.println("Approval limit: " + approvalLimit);
        System.out.println("Active: " + (active ? "YES" : "NO"));
    }

    @Override
    public String getSummary() {
        return "[OFFICER] " + getPartyId() + " | Staff " + staffNumber
                + " | " + super.getDisplayName() + " | limit " + approvalLimit;
    }
}