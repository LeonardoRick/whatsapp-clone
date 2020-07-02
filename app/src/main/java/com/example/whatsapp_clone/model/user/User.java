package com.example.whatsapp_clone.model.user;
import android.net.Uri;

import com.google.firebase.database.Exclude;

public class User {

    private String id;
    private String name;
    private String email;
    private String password;
    private Uri picture;

    public  User() {}

    public User(String name, String email, String password ) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public User(String name, String email, Uri picture ) {
        this.name = name;
        this.email = email;
        this.picture = picture;
    }

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }



    @Exclude
    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    @Exclude
    public String getPassword() { return password; }

    public void setPassword(String password) { this.password = password; }

    public void setPicture(Uri picture) { this.picture = picture; }

    public Uri getPicture() { return picture; }
}
