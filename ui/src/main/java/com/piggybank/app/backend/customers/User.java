package com.piggybank.app.backend.customers;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import com.piggybank.app.backend.employees.Employee;
import com.piggybank.app.backend.exceptions.PasswordException;
import com.piggybank.app.backend.utils.ContactCard;

// Used by Jackson-Databind for handling Json files with abstract classes, specifying which subclass an object belong to
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = Employee.class, name = "employee"),
        @JsonSubTypes.Type(value = Customer.class, name = "customer")
})
public abstract class User {

	// attributes:
    private String userId;
    private String password;
    private ContactCard contactInfo;
	private final int MAX_PASSWORD_LENGTH = 8;

	// Bare constructor used by Jackson-Databind for Json deserializing
    public User() {}

    // Main constructor
    public User(String userId, String password, ContactCard contactInfo) throws PasswordException {
        this.userId = userId;
        this.contactInfo = contactInfo;
        setPassword(password);
    }

    //--------------------Getters--------------------
    public String getUserId() {
        return this.userId;
    }
    public ContactCard getContactInfo() {
        return this.contactInfo;
    }
    public String getPassword() {
        return this.password;
    }

	//--------------------Setters--------------------
    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setContactInfo(ContactCard contactInfo) {
        this.contactInfo = contactInfo;
    }

	// change password method which checks:
    // new password must be longer than 8 symbols,
    // new password must contain at least one capital letter and at least one digit
    public void setPassword(String newPassword) throws PasswordException {
        if (newPassword.length() >= MAX_PASSWORD_LENGTH && newPassword.matches(".*[A-Z].*") && newPassword.matches(".*\\d.*")) {
            this.password = newPassword;
        } else {
            throw new PasswordException("Password has invalid format");
        }
    }

    //--------------------Methods--------------------

	// getters for ContactCard information
    @JsonIgnore
    public String getEmail() {return contactInfo.getEmail();}
    @JsonIgnore
    public String getPhoneNumber() {return contactInfo.getPhoneNumber();}
    @JsonIgnore
    public String getStreet() {return contactInfo.getStreetAddress();}
    @JsonIgnore
    public String getZipCode() {return contactInfo.getZipCode();}
    @JsonIgnore
    public String getCity() {return contactInfo.getCity();}

    // setters for ContactCard information
    @JsonIgnore
    public void setEmail(String newEmail) throws Exception {contactInfo.setEmail(newEmail);}
    @JsonIgnore
    public void setPhoneNumber(String newPhoneNr) {contactInfo.setPhoneNumber(newPhoneNr);}
    @JsonIgnore
    public void setStreet(String newStreet) {contactInfo.setStreetAddress(newStreet);}
    @JsonIgnore
    public void setZipCode(String newZip) {contactInfo.setZipCode(newZip);}
    @JsonIgnore
    public void setCity(String newCity) {contactInfo.setCity(newCity);}

	public boolean validatePassword(String inputString) {
        return password.equals(inputString);
    }
}
