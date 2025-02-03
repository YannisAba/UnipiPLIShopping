package com.jabat.personal.unipiplishopping;

//Για την δημιουργία Product αντικειμένων και την πρόσβαση σε στοιχεία αυτών.

public class Product {
    private String code;
    private String title;
    private String description;
    private String releaseDate;
    private double price;
    private double longitude;
    private double latitude;
    private boolean available;
    private String reservedBy;


    private String key;

    public String getReservedBy() {
        return reservedBy;
    }

    public void setReservedBy(String reservedBy) {
        this.reservedBy = reservedBy;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Product() {
    }

    public Product(String code, String title, String description, String releaseDate, double price, double longitude, double latitude, boolean available, String reservedBy) {
        this.code = code;
        this.title = title;
        this.description = description;
        this.releaseDate = releaseDate;
        this.price = price;
        this.longitude = longitude;
        this.latitude = latitude;
        this.available = available;
        this.reservedBy = reservedBy;
    }




}
