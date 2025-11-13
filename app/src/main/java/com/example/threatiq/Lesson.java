package com.example.threatiq;

public class

Lesson {
    private String title;
    private int imageResourceId;    public Lesson(String title, int imageResourceId) {
        this.title = title;
        this.imageResourceId = imageResourceId;
    }

    public String getTitle() {
        return title;
    }

    public int getImageResourceId() {
        return imageResourceId;
    }
}

