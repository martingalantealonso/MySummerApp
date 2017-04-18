package com.example.mgalante.mysummerapp.entities.users;

/**
 * Created by mgalante on 5/04/17.
 */

public class User {
    public String uid;
    public String name;
    public String photoUrl;
    public String firebaseToken;
    public double paymentsSum;

    public User() {}

    public User(String uid, String name, String photoUrl, String firebaseToken) {
        this.uid = uid;
        this.name = name;
        this.photoUrl = photoUrl;
        this.firebaseToken = firebaseToken;
    }

    public User(String displayName, String s, String uid) {
        //User userModel = new User(user.getDisplayName(), user.getPhotoUrl().toString(), user.getUid());
        this.uid = uid;
        this.name = displayName;
        this.photoUrl = s;
    }

    public User(String uid, String name, String photoUrl, String firebaseToken, double paymentsSum) {
        this.uid = uid;
        this.name = name;
        this.photoUrl = photoUrl;
        this.firebaseToken = firebaseToken;
        this.paymentsSum = paymentsSum;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getFirebaseToken() {
        return firebaseToken;
    }

    public void setFirebaseToken(String firebaseToken) {
        this.firebaseToken = firebaseToken;
    }

    public double getPaymentsSum() {
        return paymentsSum;
    }

    public void setPaymentsSum(double paymentsSum) {
        this.paymentsSum = paymentsSum;
    }

    @Override
    public String toString() {
        return "User{" +
                "uid='" + uid + '\'' +
                ", name='" + name + '\'' +
                ", photoUrl='" + photoUrl + '\'' +
                ", firebaseToken='" + firebaseToken + '\'' +
                '}';
    }
}
