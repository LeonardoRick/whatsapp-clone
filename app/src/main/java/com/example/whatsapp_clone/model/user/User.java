package com.example.whatsapp_clone.model.user;
import android.net.Uri;

import com.google.firebase.database.Exclude;

import java.io.Serializable;

import androidx.annotation.Nullable;

public class User implements Serializable, Comparable<User> {

    private String id;
    private String name;
    private String email;
    private String password;

    @Nullable
    private transient Uri picture = null;
    private String stringPicture; // Used because we can't serialize Uri

    public  User() {}

    public User(String name, String email, String password ) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public User(String id, String name, String email, Uri picture ) {
        this.id = id;
        this.name = name;
        this.email = email;

        if (picture != null) {
            this.picture = picture;
            // Used because we can't serialize Uri
            this.stringPicture = picture.toString();
        }
    }

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    /**
     *
     * @param id Firebase Database father key
     * @return User object with id property set
     */
    public User withId(String id) {
        this.id = id;
        return this;
    }

    /** getters **/
    @Exclude
    public String getId() { return id; }

    public String getName() { return name; }

    public String getEmail() { return email; }

    @Exclude
    public String getPassword() { return password; }

    public Uri getPicture() { return picture; }

    @Exclude
    public String getStringPicture() { return stringPicture; }

    /** setters **/

    public void setPicture(Uri picture) {
        this.picture = picture;
        this.stringPicture = picture.toString();
    }

    public void setId(String id) { this.id = id; }

    public void setName(String name) { this.name = name; }

    public void setEmail(String email) { this.email = email; }

    public void setPassword(String password) { this.password = password; }

    /**
     * To set that index of sort is user name
     * @param user
     * @return
     */
    @Override
    public int compareTo(User user) {
        return name.compareTo(user.name);
    }
}
