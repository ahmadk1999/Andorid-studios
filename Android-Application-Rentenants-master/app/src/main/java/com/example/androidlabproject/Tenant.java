package com.example.androidlabproject;

public class Tenant {

    private String emailAddress;
    private String firstName;
    private String lastName;
    private String gender;
    private String password;
    private String Nationality;
    private Double grossMonthlySalary;
    private String Occupation;
    private Double familySize;
    private String currentResidenceCountry;
    private String City;
    private String phoneNumber;

    public Tenant() {
    }

    public Tenant(String emailAddress, String firstName, String lastName, String gender, String password, String nationality, Double grossMonthlySalary, String occupation, Double familySize, String currentResidenceCountry, String city, String phoneNumber) {
        this.emailAddress = emailAddress;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.password = password;
        Nationality = nationality;
        this.grossMonthlySalary = grossMonthlySalary;
        Occupation = occupation;
        this.familySize = familySize;
        this.currentResidenceCountry = currentResidenceCountry;
        City = city;
        this.phoneNumber = phoneNumber;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNationality() {
        return Nationality;
    }

    public void setNationality(String nationality) {
        Nationality = nationality;
    }

    public Double getGrossMonthlySalary() {
        return grossMonthlySalary;
    }

    public void setGrossMonthlySalary(Double grossMonthlySalary) {
        this.grossMonthlySalary = grossMonthlySalary;
    }

    public String getOccupation() {
        return Occupation;
    }

    public void setOccupation(String occupation) {
        Occupation = occupation;
    }

    public Double getFamilySize() {
        return familySize;
    }

    public void setFamilySize(Double familySize) {
        this.familySize = familySize;
    }

    public String getCurrentResidenceCountry() {
        return currentResidenceCountry;
    }

    public void setCurrentResidenceCountry(String currentResidenceCountry) {
        this.currentResidenceCountry = currentResidenceCountry;
    }

    public String getCity() {
        return City;
    }

    public void setCity(String city) {
        City = city;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public String toString() {
        return "Agency{" +
                "\nemailAddress= " + emailAddress +
                "\nfirstName= " + firstName +
                "\nlastName= " + lastName +
                "\ngender= " + gender +
                "\npassword= " + password +
                "\nNationality= " + Nationality +
                "\ngrossMonthlySalary= " + grossMonthlySalary +
                "\nOccupation= " + Occupation +
                "\nfamilySize= " + familySize +
                "\ncurrentResidenceCountry= " + currentResidenceCountry +
                "\nCity= " + City +
                "\nphoneNumber= " + phoneNumber +
                +'\n'+'}'+'\n';
    }
}

