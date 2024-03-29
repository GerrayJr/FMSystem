package com.gerray.fmsystem.ContractorModule;

public class CreateContractor {
    public String consultantName;
    public String category;
    public String specialization;
    public String consultantLocation;
    public String userID;
    public String emailAddress;
    public String phoneNumber;
    public String consultantImageUrl;

    public CreateContractor() {
    }

    public CreateContractor(String consultantName, String category, String specialization, String consultantLocation, String userID, String emailAddress, String phoneNumber, String consultantImageUrl) {
        this.consultantName = consultantName;
        this.category = category;
        this.specialization = specialization;
        this.consultantLocation = consultantLocation;
        this.userID = userID;
        this.emailAddress = emailAddress;
        this.phoneNumber = phoneNumber;
        this.consultantImageUrl = consultantImageUrl;
    }

    public String getConsultantName() {
        return consultantName;
    }

    public void setConsultantName(String consultantName) {
        this.consultantName = consultantName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getConsultantLocation() {
        return consultantLocation;
    }

    public void setConsultantLocation(String consultantLocation) {
        this.consultantLocation = consultantLocation;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getConsultantImageUrl() {
        return consultantImageUrl;
    }

    public void setConsultantImageUrl(String consultantImageUrl) {
        this.consultantImageUrl = consultantImageUrl;
    }
}
