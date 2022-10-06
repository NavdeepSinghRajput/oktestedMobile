package com.oktested.roomDatabase.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity(tableName = "contactsTable")
public class ContactModel {

    @ColumnInfo(name = "picture")
    public String picture;

    @PrimaryKey(autoGenerate = true)
    private int primaryKey;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "app_installed")
    public boolean app_installed;

    @ColumnInfo(name = "contact_no")
    public String contact_no;

    @ColumnInfo(name = "status")
    public String status;

    @ColumnInfo(name = "id")
    public String id;

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public int getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(int primaryKey) {
        this.primaryKey = primaryKey;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isApp_installed() {
        return app_installed;
    }

    public void setApp_installed(boolean app_installed) {
        this.app_installed = app_installed;
    }

    public String getContact_no() {
        return contact_no;
    }

    public void setContact_no(String contact_no) {
        this.contact_no = contact_no;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
