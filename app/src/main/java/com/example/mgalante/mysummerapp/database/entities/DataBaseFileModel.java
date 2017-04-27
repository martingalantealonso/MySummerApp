package com.example.mgalante.mysummerapp.database.entities;

import com.example.mgalante.mysummerapp.database.PantinDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * Created by Alessandro Barreto on 22/06/2016.
 */
@Table(database = PantinDatabase.class)
public class DataBaseFileModel extends BaseModel {

    @Column
    @PrimaryKey
    int id;
    @Column
    String key;
    @Column
    private String type;
    @Column
    private String url_file;
    @Column
    private String name_file;
    @Column
    private String size_file;

    public DataBaseFileModel() {
    }

    public DataBaseFileModel(int id, String type, String url_file, String name_file, String size_file) {
        this.id = id;
        this.type = type;
        this.url_file = url_file;
        this.name_file = name_file;
        this.size_file = size_file;
    }

    public DataBaseFileModel(String type, String url_file, String name_file, String size_file) {
        this.type = type;
        this.url_file = url_file;
        this.name_file = name_file;
        this.size_file = size_file;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl_file() {
        return url_file;
    }

    public void setUrl_file(String url_file) {
        this.url_file = url_file;
    }

    public String getName_file() {
        return name_file;
    }

    public void setName_file(String name_file) {
        this.name_file = name_file;
    }

    public String getSize_file() {
        return size_file;
    }

    public void setSize_file(String size_file) {
        this.size_file = size_file;
    }

    @Override
    public String toString() {
        return "DataBaseFileModel{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", url_file='" + url_file + '\'' +
                ", name_file='" + name_file + '\'' +
                ", size_file='" + size_file + '\'' +
                '}';
    }
}
