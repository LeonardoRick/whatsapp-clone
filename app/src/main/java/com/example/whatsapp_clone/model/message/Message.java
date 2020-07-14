package com.example.whatsapp_clone.model.message;

public class Message {

    private String senderId;
    private String senderName;
    private String receiverId;
    private String textMessage;
    private boolean image;
    private boolean group;

    public Message() {
        this.setSenderName(""); // Name is not shown if its not setted
    }

    public String getSenderId() {
        return senderId;
    }

    public String getSenderName() { return senderName; }

    public String getReceiverId() { return receiverId; }

    public String getTextMessage() {
        return textMessage;
    }

    public boolean isImage() {
        return image;
    }

    public boolean isGroup() { return group; }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public void setSenderName(String senderName) { this.senderName = senderName; }

    public void setReceiverId(String receiverId) { this.receiverId = receiverId; }

    public void setTextMessage(String textMessage) {
        this.textMessage = textMessage;
    }

    public void setImage(boolean image) { this.image = image; }

    public void setGroup(boolean group) { this.group = group; }
}
