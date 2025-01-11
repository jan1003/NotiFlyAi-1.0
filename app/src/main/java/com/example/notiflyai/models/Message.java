package com.example.notiflyai.models;

public class Message {
    private String messageId;
    private String senderId;
    private String receiverId;
    private String text;
    private long timestamp;

    public Message() { }

    public Message(String messageId, String senderId, String receiverId, String text, long timestamp) {
        this.messageId = messageId;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.text = text;
        this.timestamp = timestamp;
    }

    public String getMessageId() { return messageId; }
    public String getSenderId() { return senderId; }
    public String getReceiverId() { return receiverId; }
    public String getText() { return text; }
    public long getTimestamp() { return timestamp; }
}