package com.study.jdbc.domain;

import java.sql.Blob;

public class UserPhoto {
    private Integer id;
    private String name;
    private Blob photo;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Blob getPhoto() {
        return photo;
    }

    public void setPhoto(Blob photo) {
        this.photo = photo;
    }

    @Override
    public String toString() {
        return "UserPhoto [id=" + id + ", name=" + name + ", photo=" + photo + "]";
    }

}
