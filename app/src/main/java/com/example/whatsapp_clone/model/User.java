package com.example.whatsapp_clone.model;

import com.example.whatsapp_clone.helper.Constants;
import com.example.whatsapp_clone.helper.FirebaseConfig;
import com.google.firebase.database.Exclude;

public class User {

    private static String id;
    private static String name;
    private static String email;
    private static String password;

    public  User() {}

    public User(String name, String email, String password ) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public void saveOnDatabase() {
        FirebaseConfig.getFirebaseDatabase()
                .child(Constants.UsersNode.KEY)
                .child(id)
                .setValue(this);
    }

    @Exclude
    public String getId() { return id; }

    public void setId(String id) { User.id = id; }

    public String getName() { return name; }

    public void setName(String name) { User.name = name; }

    public String getEmail() { return email; }

    public void setEmail(String email) { User.email = email; }

    @Exclude
    public String getPassword() { return password; }

    public void setPassword(String password) { User.password = password; }
}
