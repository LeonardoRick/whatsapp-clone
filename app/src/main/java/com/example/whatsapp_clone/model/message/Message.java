package com.example.whatsapp_clone.model.message;

public class Message {

    private String senderId;
    private String receiverId;
    private String textMessage;
    private boolean isImage;

    public Message() {}

    public Message(String textMessage, String userId, String receiverId, boolean isImage) {
        this.textMessage = textMessage;
        this.senderId = userId;
        this.receiverId = receiverId;
        this.isImage = isImage;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getTextMessage() {
        return textMessage;
    }

    public boolean isImage() {
        return isImage;
    }

    public String getReceiverId() { return receiverId; }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public void setTextMessage(String textMessage) {
        this.textMessage = textMessage;
    }

    public void setImage(boolean image) { isImage = image; }

    public void setReceiverId(String receiverId) { this.receiverId = receiverId; }
}
