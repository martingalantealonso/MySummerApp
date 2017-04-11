package com.example.mgalante.mysummerapp.views.main.Fragment2Chat;

/**
 * Created by mgalante on 28/03/17.
 */

public class FriendlyMessage {


    private String text;
    private String name;
    public String senderUid;
    private String photoUrl;
    public String receiver;
    public String timestamp;

    public FriendlyMessage() {
    }

    public FriendlyMessage(String text, String name, String photoUrl) {
        this.text = text;
        this.name = name;
        this.photoUrl = photoUrl;
    }

    public FriendlyMessage(String text, String name, String senderUid, String photoUrl, String receiver, String timestamp) {
        this.text = text;
        this.name = name;
        this.senderUid = senderUid;
        this.photoUrl = photoUrl;
        this.receiver = receiver;
        this.timestamp = timestamp;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
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

}
