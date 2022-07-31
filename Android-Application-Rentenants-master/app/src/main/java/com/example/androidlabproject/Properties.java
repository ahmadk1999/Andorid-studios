package com.example.androidlabproject;

public class Properties {



    private String City,Description;
    private int postalAddress;
    private int surfaceArea;
    private int numberOfBedrooms;
    private String id;


    public String getId() {
        return id;
    }



    public String getCity() {
        return City;
    }

    public void setCity(String city) {
        City = city;
    }

    //public String getDescription() {
       // return Description;
    //}

    public void setDescription(String description) {
        Description = description;
    }

    public int getPostalAddress() {
        return postalAddress;
    }

    public void setPostalAddress(int postalAddress) {
        this.postalAddress = postalAddress;
    }

    public int getSurfaceArea() {
        return surfaceArea;
    }

    public void setSurfaceArea(int surfaceArea) {
        this.surfaceArea = surfaceArea;
    }

    public int getNumberOfBedrooms() {
        return numberOfBedrooms;
    }

    public void setNumberOfBedrooms(int numberOfBedrooms) {
        this.numberOfBedrooms = numberOfBedrooms;
    }

    public String getRentalPrice() {
        return rentalPrice;
    }

    public void setRentalPrice(String rentalPrice) {
        this.rentalPrice = rentalPrice;
    }

    public String getAvailabilityDate() {
        return availabilityDate;
    }

    public void setAvailabilityDate(String availabilityDate) {
        this.availabilityDate = availabilityDate;
    }

    public String getConstructionYear() {
        return constructionYear;
    }

    public void setConstructionYear(String constructionYear) {
        this.constructionYear = constructionYear;
    }

    private String rentalPrice;
    private String availabilityDate,constructionYear;

    public Properties(){

    }

    public Properties(String rentalPrice, String availabilityDate) {
        this.rentalPrice = rentalPrice;
        this.availabilityDate = availabilityDate;
    }













}