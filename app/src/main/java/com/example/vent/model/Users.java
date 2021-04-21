package com.example.vent.model;

public class Users {
    String displayname, country, uid, status;

    public Users(){}

    public Users(String displayname, String country, String uid, String status) {
        this.displayname = displayname;
        this.country = country;
        this.uid = uid;
        this.status = status;

    }

    public String getDisplayname() {
        return displayname;
    }

    public void setDisplayname(String displayname) {
        this.displayname = displayname;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


}
