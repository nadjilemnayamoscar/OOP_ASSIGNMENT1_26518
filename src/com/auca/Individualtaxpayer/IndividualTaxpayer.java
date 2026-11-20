
package com.auca.Individualtaxpayer;

import com.auca.abstracts.RegisteredTaxpayer;
import com.auca.exceptions.ValidationException;
import java.math.BigDecimal;
import java.time.LocalDate;

public class IndividualTaxpayer extends RegisteredTaxpayer {

    private String nationalId;
    private String occupation;
    private String businessName;
    private LocalDate businessStartDate;
    private BigDecimal annualTurnover;
    private boolean resident;

    public IndividualTaxpayer(String partyId, String firstName, String lastName,
                              String email, String phoneNumber, LocalDate dateOfBirth,
                              String tin, LocalDate registrationDate, String taxpayerStatus,
                              String district, String sector, String taxOffice,
                              String nationalId, String occupation, String businessName,
                              LocalDate businessStartDate, BigDecimal annualTurnover,
                              boolean resident) {
        super(partyId, firstName, lastName, email, phoneNumber, dateOfBirth,
                tin, registrationDate, taxpayerStatus, district, sector, taxOffice);
        this.nationalId = nationalId;
        this.occupation = occupation;
        this.businessName = businessName;
        this.businessStartDate = businessStartDate;
        this.annualTurnover = annualTurnover;
        this.resident = resident;
    }


    public String getNationalId() {
        return nationalId;
    }

    public String getOccupation() {
        return occupation;
    }

    public String getBusinessName() {
        return businessName;
    }

    public LocalDate getBusinessStartDate() {
        return businessStartDate;
    }

    public BigDecimal getAnnualTurnover() {
        return annualTurnover;
    }

    public boolean isResident() {
        return resident;
    }


    public void setNationalId(String nationalId) {
        this.nationalId = nationalId;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public void setBusinessStartDate(LocalDate businessStartDate) {
        this.businessStartDate = businessStartDate;
    }

    public void setAnnualTurnover(BigDecimal annualTurnover) {
        this.annualTurnover = annualTurnover;
    }

    public void setResident(boolean resident) {
        this.resident = resident;
    }


    @Override
    public String getPartyRole() {
        return "INDIVIDUAL TAXPAYER";
    }

    @Override
    public String getDisplayName() {
        return "Individual: " + super.getDisplayName();
    }


    public String getTurnoverCategory() {
        if (annualTurnover.compareTo(new BigDecimal("12000000")) < 0) {
            return "SMALL";
        }
        return "STANDARD";
    }

    @Override
    public void validate() throws ValidationException {
        super.validate();

        if (nationalId == null || !nationalId.matches("^[0-9]{16}$")) {
            throw new ValidationException("National ID must be exactly 16 digits.");
        }
        requireText(occupation, "Occupation", 2, 60);
        requireText(businessName, "Business name", 2, 120);
        if (businessStartDate == null || businessStartDate.isAfter(LocalDate.now())) {
            throw new ValidationException("Business start date is required and cannot be in the future.");
        }
        if (!businessStartDate.isBefore(getRegistrationDate())) {
            throw new ValidationException("Business start date must be before the registration date.");
        }
        checkMoney(annualTurnover, "Annual turnover");
    }

    @Override
    public void printDetails() {
        System.out.println("=== INDIVIDUAL TAXPAYER ===");
        super.printDetails();
        System.out.println("National ID: " + nationalId);
        System.out.println("Occupation: " + occupation);
        System.out.println("Business name: " + businessName);
        System.out.println("Business start date: " + businessStartDate);
        System.out.println("Annual turnover: " + annualTurnover);
        System.out.println("Resident: " + (resident ? "YES" : "NO"));
    }

    @Override
    public String getSummary() {
        return "[INDIVIDUAL] " + getPartyId() + " | TIN " + getTin()
                + " | " + super.getDisplayName() + " | " + getTaxpayerStatus();
    }
}