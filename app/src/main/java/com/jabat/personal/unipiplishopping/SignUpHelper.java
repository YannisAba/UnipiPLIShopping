package com.jabat.personal.unipiplishopping;

//Χρήσιμο για την διαδικασία της εγγραφής χρήστη. Δεν χρησιμοποιούμε την ίδια class με την user γιατί η άλλη πρέπει να έχει τις τιμές static ώστε να μένουν σταθερές σε όλο το πρόγραμμα.

public class SignUpHelper {

    String name;
    String surname;
    String username;
    String password;

    public SignUpHelper(String surname, String name, String username, String password) {
        this.surname = surname;
        this.name = name;
        this.username = username;
        this.password = password;
    }
    public SignUpHelper() {
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
