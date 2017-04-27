package com.example.mgalante.mysummerapp.database.entities;

import com.example.mgalante.mysummerapp.database.PantinDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * Created by mgalante on 27/04/17.
 */

@Table(database = PantinDatabase.class)
public class DataBaseImageSentModel extends BaseModel {

    @Column
    @PrimaryKey (autoincrement = true)
    int id;

    @Column
    String firebaseKey;

    @Column
    String imagePath;

    public DataBaseImageSentModel() {
    }

    public DataBaseImageSentModel(String firebaseKey, String imagePath) {
        this.firebaseKey = firebaseKey;
        this.imagePath = imagePath;
    }

    public DataBaseImageSentModel(int id, String firebaseKey, String imagePath) {
        this.id = id;
        this.firebaseKey = firebaseKey;
        this.imagePath = imagePath;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirebaseKey() {
        return firebaseKey;
    }

    public void setFirebaseKey(String firebaseKey) {
        this.firebaseKey = firebaseKey;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    @Override
    public String toString() {
        return "DataBaseImageSentModel{" +
                "id=" + id +
                ", firebaseKey='" + firebaseKey + '\'' +
                ", imagePath='" + imagePath + '\'' +
                '}';
    }
}
