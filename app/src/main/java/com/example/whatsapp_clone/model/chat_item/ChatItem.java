package com.example.whatsapp_clone.model.chat_item;

import com.example.whatsapp_clone.model.group.Group;
import com.example.whatsapp_clone.model.user.User;

public class ChatItem {

    private String id;
    private String lastMessage;
    private User selectedContact;
    private boolean isGroup;
    private Group group;

    public ChatItem() { }

    // Constructor used to copy chat
    public ChatItem(ChatItem chat) {
        this.id = chat.id;
        this.lastMessage = chat.lastMessage;
        this.selectedContact = chat.selectedContact;
        this.isGroup = chat.isGroup;
        this.group = chat.group;
    }

    public String getId() { return id; }

    public String getLastMessage() { return lastMessage; }

    public User getSelectedContact() { return selectedContact; }

    public boolean isGroup() { return isGroup; }

    public Group getGroup() { return group; }

    public void setId(String id) { this.id = id; }

    public void setLastMessage(String lastMessage) { this.lastMessage = lastMessage; }



    public void setSelectedContact(User selectedContact) { this.selectedContact = selectedContact; }

    public void setIsGroup(boolean group) { isGroup = group; }

    public void setGroup(Group group) { this.group = group; }
}
