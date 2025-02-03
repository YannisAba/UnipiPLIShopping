package com.jabat.personal.unipiplishopping;


//Χρησιμοποιούμε τα name και username ως static ώστε να μένουν σταθερά καθ'όλη τη διάρκεια του προγράμματος για τον τωρινό χρήστη
// Επειδή τα χρειαζόμαστε αρκετές φορές κατά τη διάρκεια του προγράμματος μετά την πρώτη χρήση των sets μπορούμε να πάρουμε πληροφορίες για τον χρήστη με τα gets.

public class User {
    static String name;
    String  surname;
    static String username;
    String password;

    public User(String surname, String name, String username, String password) {
        this.surname = surname;
        this.name = name;
        this.username = username;
        this.password = password;
    }
    public User() {
    }

    public static String getUsername() {
        return username;
    }

    public static void setUsername(String username) {
        User.username = username;
    }

    public static String getName() {
        return name;
    }

    public static void setName(String name) {
        User.name = name;
    }
}
