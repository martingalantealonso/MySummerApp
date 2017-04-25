package com.example.mgalante.mysummerapp.entities;


import com.example.mgalante.mysummerapp.entities.users.User;

public class ImageModel {

    private String id;
    private User userModel;
    private String timeStamp;
    private FileModel fileModel;

    public ImageModel() {
    }

    public ImageModel(String id, User userModel, String timeStamp, FileModel file) {
        this.id = id;
        this.userModel = userModel;
        this.timeStamp = timeStamp;
        this.fileModel = file;
    }

    public ImageModel(User userModel, FileModel file) {
        this.userModel = userModel;
        this.fileModel = file;
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

    public FileModel getFileModel() {
        return fileModel;
    }

    public void setFileModel(FileModel fileModel) {
        this.fileModel = fileModel;
    }

    @Override
    public String toString() {
        return "ImageModel{" +
                "id='" + id + '\'' +
                ", userModel=" + userModel +
                ", timeStamp='" + timeStamp + '\'' +
                ", file=" + fileModel +
                '}';
    }
}
