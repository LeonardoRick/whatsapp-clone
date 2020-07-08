package com.example.whatsapp_clone.model.message;

public class Message {

    private String userId;
    private String textMessage;
    private boolean isImage;

    public Message() {}

    public Message(String userId, String message, boolean isImage) {
        this.userId = userId;
        this.textMessage = message;
        this.isImage = isImage;
    }

    public String getUserId() {
        return userId;
    }

    public String getTextMessage() {
        return textMessage;
    }

    public boolean isImage() {
        return isImage;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setTextMessage(String textMessage) {
        this.textMessage = textMessage;
    }

    public void setImage(boolean image) { isImage = image; }
}
