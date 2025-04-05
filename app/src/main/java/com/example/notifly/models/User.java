package com.example.notifly.models;

public class User {
    private String userId;
    private String name;
    private String email;
    private int priority;  // ✅ ВОТ ЭТО — мы добавили поле priority

    public User() {
        // Пустой конструктор для Firestore
    }

    public User(String userId, String name, String email) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.priority = 3; // по умолчанию, если не задано
    }

    // Если тебе где-то нужен конструктор с приоритетом — можно использовать этот:
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

    public void setPriority(int priority) {
        this.priority = priority;
    }
}