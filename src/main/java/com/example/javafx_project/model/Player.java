package com.example.javafx_project.model;

public class Player {
    private final String fullname;
    private final String username;

    public Player(String fullname, String username) {
        this.fullname = fullname;
        this.username = username;
    }
    public String getFullname() { return fullname; }
    public String getUsername() { return username; }
}
