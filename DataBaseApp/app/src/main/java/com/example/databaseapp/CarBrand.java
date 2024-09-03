package com.example.databaseapp;

public class CarBrand {
    private long id;
    private String name;

    public CarBrand(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
