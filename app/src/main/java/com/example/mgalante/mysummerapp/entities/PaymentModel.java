package com.example.mgalante.mysummerapp.entities;

import com.example.mgalante.mysummerapp.entities.users.User;

/**
 * Created by mgalante on 13/04/17.
 */

public class PaymentModel {

    private User userModel;
    private String title;
    private String description;
    private double amount;
    private String timeStamp;
    private FileModel file;

    public PaymentModel() {
    }

    public PaymentModel(User userModel, String title, String description, double amount, String timeStamp, FileModel file) {
        this.userModel = userModel;
        this.title = title;
        this.description = description;
        this.amount = amount;
        this.timeStamp = timeStamp;
        this.file = file;
    }

    public User getUserModel() {
        return userModel;
    }

    public void setUserModel(User userModel) {
        this.userModel = userModel;
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

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
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
        return "PaymentModel{" +
                "userModel=" + userModel +
                ", description='" + description + '\'' +
                ", amount=" + amount +
                ", timeStamp='" + timeStamp + '\'' +
                ", file=" + file +
                '}';
    }
}
