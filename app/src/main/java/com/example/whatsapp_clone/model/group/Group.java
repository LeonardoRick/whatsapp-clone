package com.example.whatsapp_clone.model.group;

import android.net.Uri;

import com.example.whatsapp_clone.helper.Constants;
import com.example.whatsapp_clone.helper.FirebaseConfig;
import com.example.whatsapp_clone.model.user.User;

import java.io.Serializable;
import java.util.ArrayList;

import androidx.annotation.Nullable;

public class Group implements Serializable {

    private String id;
    private String name;
    private ArrayList<User> members;
    private User creator;

    @Nullable
    private transient Uri picture = null;
    private String stringPicture;  // Used because we can't serialize Uri

    // Use this only to map on GroupItemHelper
    public Group() {}

    public Group(User creator) {
        setId();
        this.creator = creator;
    }

    // use to copy instance of group to another one
    public Group (Group group) {
        this.id = group.id;
        this.name = group.name;
        this.members = group.members;
        this.creator = group.creator;
        this.picture = group.picture;
        this.stringPicture = group.stringPicture;
    }

    public void addGroupMember(User user) {
        this.members.add(user);
    }

    public void removeGroupMember(User user) {this.members.remove(user);}

    /****** getters and setters ******/

    public String getId() { return id; }

    public String getName() { return name; }

    public Uri getPicture() { return picture; }

    public String getStringPicture() { return stringPicture; }

    public ArrayList<User> getMembers() { return members; }

    public User getCreator() { return creator; }

    public void setId() {
        this.id = FirebaseConfig.getFirebaseDatabase().child(Constants.GroupNode.KEY).push().getKey();
    }

    // use this overload only to map o GroupItemHelper
    public void setId(String id) {
        this.id = id;
    }

    public void setName(String nome) { this.name = nome; }

    public void setPicture(Uri picture) {
        this.picture = picture;
        this.stringPicture = picture.toString();
    }

    public void setMembers(ArrayList<User> members) { this.members = members; }

    public void setCreator(User creator) {this.creator = creator; }

}