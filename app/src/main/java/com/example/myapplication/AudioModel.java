package com.example.myapplication;

public class AudioModel {
    String title, path;
    long endDuration; // Add this property

    public AudioModel(String title, String path, long endDuration) {
        this.title = title;
        this.path = path;
        this.endDuration = endDuration;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getEndDuration() {
        return endDuration;
    }

    public void setEndDuration(long endDuration) {
        this.endDuration = endDuration;
    }
}
