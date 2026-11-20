
package com.auca.corporatetaxpayer;

import com.auca.abstracts.RegisteredTaxpayer;
import com.auca.exceptions.ValidationException;
import java.math.BigDecimal;
import java.time.LocalDate;

public class CorporateTaxpayer extends RegisteredTaxpayer {

    private String companyRegistrationNo;
    private String legalName;
    private LocalDate incorporationDate;
    private String businessSector;
    private String authorizedRepresentative;
    private BigDecimal annualTurnover;

    public CorporateTaxpayer(String partyId, String firstName, String lastName,
                             String email, String phoneNumber, LocalDate dateOfBirth,
                             String tin, LocalDate registrationDate, String taxpayerStatus,
                             String district, String sector, String taxOffice,
                             String companyRegistrationNo, String legalName,
                             LocalDate incorporationDate, String businessSector,
                             String authorizedRepresentative, BigDecimal annualTurnover) {
        super(partyId, firstName, lastName, email, phoneNumber, dateOfBirth,
                tin, registrationDate, taxpayerStatus, district, sector, taxOffice);
        this.companyRegistrationNo = companyRegistrationNo;
        this.legalName = legalName;
        this.incorporationDate = incorporationDate;
        this.businessSector = businessSector;
        this.authorizedRepresentative = authorizedRepresentative;
        this.annualTurnover = annualTurnover;
    }


    public String getCompanyRegistrationNo() {
        return companyRegistrationNo;
    }

    public String getLegalName() {
        return legalName;
    }

    public LocalDate getIncorporationDate() {
        return incorporationDate;
    }

    public String getBusinessSector() {
        return businessSector;
    }

    public String getAuthorizedRepresentative() {
        return authorizedRepresentative;
    }

    public BigDecimal getAnnualTurnover() {
        return annualTurnover;
    }


    public void setCompanyRegistrationNo(String companyRegistrationNo) {
        this.companyRegistrationNo = companyRegistrationNo;
    }

    public void setLegalName(String legalName) {
        this.legalName = legalName;
    }

    public void setIncorporationDate(LocalDate incorporationDate) {
        this.incorporationDate = incorporationDate;
    }

    public void setBusinessSector(String businessSector) {
        this.businessSector = businessSector;
    }

    public void setAuthorizedRepresentative(String authorizedRepresentative) {
        this.authorizedRepresentative = authorizedRepresentative;
    }

    public void setAnnualTurnover(BigDecimal annualTurnover) {
        this.annualTurnover = annualTurnover;
    }


    @Override
    public String getPartyRole() {
        return "CORPORATE TAXPAYER";
    }

    @Override
    public String getDisplayName() {
        return "Company: " + legalName;
    }

     public boolean isLargeTaxpayer() {
        return annualTurnover.compareTo(new BigDecimal("700000000")) >= 0;
    }

    @Override
    public void validate() throws ValidationException {
        super.validate();

        if (companyRegistrationNo == null
                || !companyRegistrationNo.matches("^[A-Z0-9-]{5,30}$")) {
            throw new ValidationException(
                    "Company registration number must be 5-30 uppercase letters, digits or hyphens.");
        }
        requireText(legalName, "Legal name", 3, 120);
        if (incorporationDate == null || incorporationDate.isAfter(LocalDate.now())) {
            throw new ValidationException("Incorporation date is required and cannot be in the future.");
        }
        if (!incorporationDate.isBefore(getRegistrationDate())) {
            throw new ValidationException("Incorporation date must be before the registration date.");
        }
        requireText(businessSector, "Business sector", 2, 60);
        requireText(authorizedRepresentative, "Authorized representative", 2, 60);
        checkMoney(annualTurnover, "Annual turnover");
    }

    @Override
    public void printDetails() {
        System.out.println("=== CORPORATE TAXPAYER ===");
        super.printDetails();
        System.out.println("Company registration no: " + companyRegistrationNo);
        System.out.println("Legal name: " + legalName);
        System.out.println("Incorporation date: " + incorporationDate);
        System.out.println("Business sector: " + businessSector);
        System.out.println("Authorized representative: " + authorizedRepresentative);
        System.out.println("Annual turnover: " + annualTurnover);
    }

    @Override
    public String getSummary() {
        return "[CORPORATE] " + getPartyId() + " | TIN " + getTin()
                + " | " + legalName + " | " + getTaxpayerStatus();
    }
}