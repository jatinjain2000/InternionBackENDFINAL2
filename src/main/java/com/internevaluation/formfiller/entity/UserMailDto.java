package com.internevaluation.formfiller.entity;

public class UserMailDto {
    private String sender;
    private String filename;

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}
