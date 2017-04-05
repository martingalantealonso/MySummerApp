package com.example.mgalante.mysummerapp.views.main.Entities;

/**
 * Created by mgalante on 5/04/17.
 */

public class User {
    public String uid;
    public String name;
    public String photoUrl;
    public String firebaseToken;


    public User() {}

    public User(String uid, String name, String photoUrl, String firebaseToken) {
        this.uid = uid;
        this.name = name;
        this.photoUrl = photoUrl;
        this.firebaseToken = firebaseToken;
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
}
