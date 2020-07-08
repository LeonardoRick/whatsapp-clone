package com.example.whatsapp_clone.model.chat_item;

import com.example.whatsapp_clone.model.user.User;

import java.io.Serializable;

public class ChatItem {

    private String id;
    private String lastMessage;
    private User selectedContact;

    public ChatItem() {}

    public ChatItem(String id, String lastMessage, User selectedContact) {
        this.id = id;
        this.selectedContact = selectedContact;
        this.lastMessage = lastMessage;
    }

    public String getId() { return id; }

    public String getLastMessage() { return lastMessage; }

    public User getSelectedContact() { return selectedContact; }

    public void setId(String id) { this.id = id; }

    public void setLastMessage(String lastMessage) { this.lastMessage = lastMessage; }

    public void setSelectedContact(User selectedContact) { this.selectedContact = selectedContact; }
}
