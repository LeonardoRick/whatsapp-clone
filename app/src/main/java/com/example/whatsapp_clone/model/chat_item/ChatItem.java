package com.example.whatsapp_clone.model.chat_item;

import com.example.whatsapp_clone.model.group.Group;
import com.example.whatsapp_clone.model.user.User;

public class ChatItem {

    private String id;
    private String lastMessage;
    private User sender;
    private User receiver;

    private boolean isGroup;
    private Group group;


    public ChatItem() {
        this.isGroup = false;
    }

    public ChatItem(String id, String lastMessage, User sender, User receiver) {
        this.isGroup = false;
        this.id = id;
        this.sender = sender;
        this.receiver = receiver;
        this.lastMessage = lastMessage;
    }

    public String getId() { return id; }

    public String getLastMessage() { return lastMessage; }

    public User getSender() { return sender; }

    public User getReceiver() { return receiver; }

    public boolean isGroup() { return isGroup; }

    public Group getGroup() { return group; }

    public void setId(String id) { this.id = id; }

    public void setLastMessage(String lastMessage) { this.lastMessage = lastMessage; }

    public void setSender(User sender) { this.sender = sender; }

    public void setReceiver(User receiver) { this.receiver = receiver; }

    public void setIsGroup(boolean group) { isGroup = group; }

    public void setGroup(Group group) { this.group = group; }
}
