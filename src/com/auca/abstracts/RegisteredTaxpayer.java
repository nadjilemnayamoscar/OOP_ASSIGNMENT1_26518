package com.auca.abstracts;

import com.auca.exceptions.ValidationException;
import java.time.LocalDate;

public abstract class RegisteredTaxpayer extends TaxParty {

    private String tin;
    private LocalDate registrationDate;
    private String taxpayerStatus;
    private String district;
    private String sector;
    private String taxOffice;

    public RegisteredTaxpayer(String partyId, String firstName, String lastName,
                              String email, String phoneNumber, LocalDate dateOfBirth,
                              String tin, LocalDate registrationDate, String taxpayerStatus,
                              String district, String sector, String taxOffice) {
        super(partyId, firstName, lastName, email, phoneNumber, dateOfBirth);
        this.tin = tin;
        this.registrationDate = registrationDate;
        this.taxpayerStatus = taxpayerStatus;
        this.district = district;
        this.sector = sector;
        this.taxOffice = taxOffice;
    }


    public String getTin() {
        return tin;
    }

    public LocalDate getRegistrationDate() {
        return registrationDate;
    }

    public String getTaxpayerStatus() {
        return taxpayerStatus;
    }

    public String getDistrict() {
        return district;
    }

    public String getSector() {
        return sector;
    }

    public String getTaxOffice() {
        return taxOffice;
    }

    public void setTin(String tin) {
        this.tin = tin;
    }

    public void setRegistrationDate(LocalDate registrationDate) {
        this.registrationDate = registrationDate;
    }

    public void setTaxpayerStatus(String taxpayerStatus) {
        this.taxpayerStatus = taxpayerStatus;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public void setTaxOffice(String taxOffice) {
        this.taxOffice = taxOffice;
    }

    @Override
    public boolean isActive() {
        return "ACTIVE".equalsIgnoreCase(taxpayerStatus);
    }

    @Override
    public void validate() throws ValidationException {
        super.validate();

        if (tin == null || !tin.matches("^[0-9]{9}$")) {
            throw new ValidationException("TIN must be exactly 9 digits.");
        }
        if (registrationDate == null || registrationDate.isAfter(LocalDate.now())) {
            throw new ValidationException("Registration date is required and cannot be in the future.");
        }
        if (!registrationDate.isAfter(getDateOfBirth())) {
            throw new ValidationException("Birth date must be before the registration date.");
        }
        if (taxpayerStatus == null
                || !(taxpayerStatus.equals("ACTIVE") || taxpayerStatus.equals("INACTIVE")
                || taxpayerStatus.equals("SUSPENDED"))) {
            throw new ValidationException("Status must be ACTIVE, INACTIVE or SUSPENDED.");
        }
        requireText(district, "District", 2, 60);
        requireText(sector, "Sector", 2, 60);
        requireText(taxOffice, "Tax office", 2, 60);
    }

    @Override
    public void printDetails() {
        super.printDetails();
        System.out.println("TIN: " + tin);
        System.out.println("Registration date: " + registrationDate);
        System.out.println("Status: " + taxpayerStatus);
        System.out.println("District: " + district);
        System.out.println("Sector: " + sector);
        System.out.println("Tax office: " + taxOffice);
    }
}