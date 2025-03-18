package com.example.notifly.models;

public class User {
    private String userId;
    private String name;
    private String email;
    private int priority; // 1..5 (чем выше, тем важнее)

    public User() {
        // Для Firebase
    }

    public User(String userId, String name, String email, int priority) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.priority = priority;
    }

    public String getUserId() {
        return userId;
    }
    public String getName() {
        return name;
    }
    public String getEmail() {
        return email;
    }
    public int getPriority() {
        return priority;
    }
}