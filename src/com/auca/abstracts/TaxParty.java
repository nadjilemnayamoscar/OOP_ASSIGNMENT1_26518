package com.auca.abstracts;

import com.auca.exceptions.ValidationException;
import com.auca.interfaces.Identifiable;
import com.auca.interfaces.Validatable;
import java.math.BigDecimal;
import java.time.LocalDate;

public abstract class TaxParty implements Identifiable, Validatable {

    private String partyId;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private LocalDate dateOfBirth;

    public TaxParty(String partyId, String firstName, String lastName,
                    String email, String phoneNumber, LocalDate dateOfBirth) {
        this.partyId = partyId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.dateOfBirth = dateOfBirth;
    }


    public String getPartyId() {
        return partyId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }


    public void setPartyId(String partyId) {
        this.partyId = partyId;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }


    @Override
    public String getId() {
        return partyId;
    }

    @Override
    public String getDisplayName() {
        return firstName + " " + lastName;
    }

    public abstract String getPartyRole();


    @Override
    public void validate() throws ValidationException {
        requireText(partyId, "Party ID", 4, 30);
        requireText(firstName, "First name", 2, 60);
        requireText(lastName, "Last name", 2, 60);

        if (email == null || email.trim().length() > 120
                || !email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new ValidationException("Email is not valid (max 120 characters).");
        }
        if (phoneNumber == null || !phoneNumber.matches("^\\+?[0-9]{10,15}$")) {
            throw new ValidationException("Phone must be 10-15 digits, optional leading +.");
        }
        if (dateOfBirth == null || dateOfBirth.isAfter(LocalDate.now())) {
            throw new ValidationException("Date of birth is required and cannot be in the future.");
        }
    }

    @Override
    public void printDetails() {
        System.out.println("Party ID: " + partyId);
        System.out.println("First name: " + firstName);
        System.out.println("Last name: " + lastName);
        System.out.println("Email: " + email);
        System.out.println("Phone: " + phoneNumber);
        System.out.println("Date of birth: " + dateOfBirth);
    }

    @Override
    public String getSummary() {
        return partyId + " - " + getDisplayName();
    }


    protected static void requireText(String value, String field, int min, int max)
            throws ValidationException {
        if (value == null || value.trim().length() < min || value.trim().length() > max) {
            throw new ValidationException(field + " is required (" + min + "-" + max + " characters).");
        }
    }

    protected static void checkMoney(BigDecimal value, String field) throws ValidationException {
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
}