package com.example.whatsapp_clone.helper;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseConfig {

    public static FirebaseAuth auth;
    public static DatabaseReference db;

    /**
     * static method to keep FirebaseAuth as one instance on entire app
     * @return FirebaseAuth global instance
     */
    public static FirebaseAuth getAuth() {
        if(auth == null) auth = FirebaseAuth.getInstance();
        return auth;
    }

    /**
     *  sattic method to keep DatabaseReference as one instance on entire app
     * @return DatabaseReference global instance
     */
    public static DatabaseReference getFirebaseDatabase() {
        if(db == null) db = FirebaseDatabase.getInstance().getReference();
        return db;
    }
}
