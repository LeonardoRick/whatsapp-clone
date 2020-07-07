package com.example.whatsapp_clone.model.user;

import android.net.Uri;
import android.util.Log;

import com.example.whatsapp_clone.helper.Constants;
import com.example.whatsapp_clone.helper.FirebaseConfig;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;

import java.util.HashMap;
import java.util.Map;


public class UserHelper {

    /**
     * @return User logged
     */
    public static User getLogged() {
        FirebaseUser firebaseUser = FirebaseConfig.getAuth().getCurrentUser();
        if (firebaseUser == null) {
            return  null;

        } else {
            return new User(
                    firebaseUser.getUid(),
                    firebaseUser.getDisplayName(),
                    firebaseUser.getEmail(),
                    firebaseUser.getPhotoUrl()
            );
        }
    }

    /**
     * Save user on database within his id
     * @return boolean to control success of operation
     *
     * Uri property don't give us a trobule here because it's not defined yes
     * When this method is called. Refactor to use Map as updateOnDatabase() if needed
     */
    public static boolean saveOnDatabase(User user) {
        try {
            FirebaseConfig.getFirebaseDatabase()
                    .child(Constants.UsersNode.KEY)
                    .child(user.getId())
                    .setValue(user);

            updateNameOnProfile(user.getName());
            return true;
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     *
     * @param user to update each old value on database
     * @return boolean that show if operation was successful
     */
    public static boolean updateOnDatabase(User user) {
        try {
            // Convert user object to Map, so updateChildren can accept it
            Map<String, Object> userMap = convertUserToMap(user);

            FirebaseConfig.getFirebaseDatabase()
                    .child(Constants.UsersNode.KEY)
                    .child(getLogged().getId())
                    .updateChildren(userMap);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * @param userNode dataSnapshot that has userValues from Firebase Database to be to a HashMap
     * @return Map<String, Object> with single user info
     *
     * check if dataSnapshotExists before sending
     */
    public static Map<String, Object> mapUserFromFirebse(DataSnapshot userNode) {
        Map<String, Object> userMap = new HashMap<>();                      // userMap to recover users info to list

        userMap.put(Constants.ID, userNode.getKey());                       // for each user, associate a key value
        for (DataSnapshot userSnapshot : userNode.getChildren()) {
            userMap.put(userSnapshot.getKey(), userSnapshot.getValue());

        }
        return userMap;
    }

    /**
     *
     * @param user to be converted to hashMap so Firebase .updateChildren accpets
     * @return Map <String, Object> where Object is user info
     */
    public static Map<String, Object> convertUserToMap(User user) {
        Map<String, Object> userMap = new HashMap<>();

        userMap.put(Constants.UsersNode.NAME, user.getName());
        userMap.put(Constants.UsersNode.EMAIL, user.getEmail());
        userMap.put(Constants.UsersNode.PICTURE, user.getPicture().toString());
        return userMap;
    }

    /**
     * Since we have a Uri type on User, we can't receive Firebase info directly using
     * dataSnapshot.getValue(User.class) because Firebase API can't convert an String
     * to an Uri
     *
     * In this case, we  map each key by ourselves
     *
     * @param userMap received from database
     * @return User object
     */
    public static User convertMapToUser(Map<String, Object> userMap) {
        User user = new User();

        try {
            Object idObject = userMap.get(Constants.ID);
            Object nameObject = userMap.get(Constants.UsersNode.NAME);
            Object emailObject = userMap.get(Constants.UsersNode.EMAIL);
            Object uriObject = userMap.get(Constants.UsersNode.PICTURE);

            if (idObject != null)  {
                String id = idObject.toString();
                user.setId(id);
            };
            if (nameObject != null) {
                String name = nameObject.toString();
                user.setName(name);
            };
            if (emailObject != null) {
                String email = emailObject.toString();
                user.setEmail(email);
            }
            if (uriObject != null ) {
                Uri uri = Uri.parse(uriObject.toString());
                user.setPicture(uri);
            }

            return user;
        } catch (Exception e) {
            Log.e("UserHelper", "convertMapToUser: " + e.getMessage() );
            return null;
        }
    }

    /**
     *
     * @param name to update on profile
     * @return boolean to control success of operation
     */
    public static boolean updateNameOnProfile(String name) {
        try {
            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .build();
            FirebaseConfig.getAuth().getCurrentUser().updateProfile(profile);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * save profile image of user on his profile of firebase
     * @param uri
     * @return boolean to control success of operation
     */
    public static boolean updateImageOnProfile(Uri uri) {
        try {
            UserProfileChangeRequest profile  = new UserProfileChangeRequest.Builder()
                    .setPhotoUri(uri)
                    .build();

            FirebaseConfig.getAuth().getCurrentUser().updateProfile(profile);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
