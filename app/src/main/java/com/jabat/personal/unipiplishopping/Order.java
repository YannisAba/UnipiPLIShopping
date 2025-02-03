package com.jabat.personal.unipiplishopping;

//Για την δημιουργία Order objects

public class Order {
    private String productCode;
    private String userUsername;
    private long timestamp;

    private String key;

    public Order(String productCode, String userUsername, long timestamp) {
        this.productCode = productCode;
        this.userUsername = userUsername;
        this.timestamp = timestamp;
    }

    public Order() {
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getUserUsername() {
        return userUsername;
    }

    public void setUserUsername(String userUsername) {
        this.userUsername = userUsername;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
