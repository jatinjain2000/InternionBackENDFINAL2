package com.internevaluation.formfiller.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;


public class   ListOfUser {
    private String username;
    private String filename;
    private ArrayList<String> listUser;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public ArrayList<String> getListUser() {
        return listUser;
    }

    public void setListUser(ArrayList<String> listUser) {
        this.listUser = listUser;
    }


}
