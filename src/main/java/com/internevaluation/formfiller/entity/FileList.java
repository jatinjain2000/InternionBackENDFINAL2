package com.internevaluation.formfiller.entity;

import lombok.*;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import javax.persistence.*;

@Entity
@Table(name = "user_file")
@Getter
@Setter
@NoArgsConstructor
public class FileList {
    public FileList(String username, String filename, Boolean permission) {
        this.username = username;
        this.filename = filename;
        this.permission = permission;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

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

    public Boolean getPermission() {
        return permission;
    }

    public void setPermission(Boolean permission) {
        this.permission = permission;
    }

    public FileList(String username, String filename) {
        this.username = username;
        this.filename = filename;
    }

    public FileList(Integer count, String username, String filename) {
        this.count = count;
        this.username = username;
        this.filename = filename;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer count=1;

    public FileList(Integer count, String username, String filename, String email) {
        this.count = count;
        this.username = username;
        this.filename = filename;
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    private String username;
    private String filename;
    private Boolean permission = false;
    private String email;
}
