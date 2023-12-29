package com.internevaluation.formfiller.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResidenceCertificateForm {
    private String name;
    private String parent_name;
    private String village;
    private  String taluka;
    private String district;
    private String place_of_register;
    private String date_of_register;
    private String document_for_verify;
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParent_name() {
        return parent_name;
    }

    public void setParent_name(String parent_name) {
        this.parent_name = parent_name;
    }

    public String getVillage() {
        return village;
    }

    public void setVillage(String village) {
        this.village = village;
    }

    public String getTaluka() {
        return taluka;
    }

    public void setTaluka(String taluka) {
        this.taluka = taluka;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getPlace_of_register() {
        return place_of_register;
    }

    public void setPlace_of_register(String place_of_register) {
        this.place_of_register = place_of_register;
    }

    public String getDate_of_register() {
        return date_of_register;
    }

    public void setDate_of_register(String date_of_register) {
        this.date_of_register = date_of_register;
    }

    public String getDocument_for_verify() {
        return document_for_verify;
    }

    public void setDocument_for_verify(String document_for_verify) {
        this.document_for_verify = document_for_verify;
    }


}
