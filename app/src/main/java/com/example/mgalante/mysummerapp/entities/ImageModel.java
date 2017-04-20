package com.example.mgalante.mysummerapp.entities;


import com.example.mgalante.mysummerapp.entities.users.User;

public class ImageModel {

    private String id;
    private User userModel;
    private String timeStamp;
    private FileModel file;

    public ImageModel() {
    }

    public ImageModel(String id, User userModel, String timeStamp, FileModel file) {
        this.id = id;
        this.userModel = userModel;
        this.timeStamp = timeStamp;
        this.file = file;
    }

    public ImageModel(User userModel, FileModel file) {
        this.userModel = userModel;
        this.file = file;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getUserModel() {
        return userModel;
    }

    public void setUserModel(User userModel) {
        this.userModel = userModel;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public FileModel getFile() {
        return file;
    }

    public void setFile(FileModel file) {
        this.file = file;
    }

    @Override
    public String toString() {
        return "ImageModel{" +
                "id='" + id + '\'' +
                ", userModel=" + userModel +
                ", timeStamp='" + timeStamp + '\'' +
                ", file=" + file +
                '}';
    }
}
