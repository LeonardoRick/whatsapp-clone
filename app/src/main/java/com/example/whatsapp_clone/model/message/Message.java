package com.example.whatsapp_clone.model.message;

public class Message {

    private String senderId;
    private String receiverId;
    private String textMessage;
    private boolean image;
    private boolean group;

    public Message() {}

    public String getSenderId() {
        return senderId;
    }

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

    public void setTextMessage(String textMessage) {
        this.textMessage = textMessage;
    }

    public void setReceiverId(String receiverId) { this.receiverId = receiverId; }

    public void setImage(boolean image) { this.image = image; }

    public void setGroup(boolean group) { this.group = group; }
}
