package com.example.notifly.models;

public class Contact {
    private String userId;
    private int priority;

    public Contact() {
        // Пустой конструктор нужен Firestore
    }

    public Contact(String userId, int priority) {
        this.userId = userId;
        this.priority = priority;
    }

    public String getUserId() {
        return userId;
    }

    public int getPriority() {
        return priority;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}