package com.example.androidlabproject;

public class Agency {
    private String email;
    private String name;
    private String password;
    private String country;
    private String city;
    private String phoneNumber;

    public Agency(String email, String name, String password, String country, String city, String phoneNumber) {
        this.email = email;
        this.name = name;
        this.password = password;
        this.country = country;
        this.city = city;
        this.phoneNumber = phoneNumber;
    }

    public Agency() {

    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

}