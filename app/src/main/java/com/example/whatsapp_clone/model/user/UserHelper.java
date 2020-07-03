package com.example.whatsapp_clone.model.user;

import android.net.Uri;
import android.util.Log;

import com.example.whatsapp_clone.helper.Constants;
import com.example.whatsapp_clone.helper.FirebaseConfig;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.HashMap;
import java.util.Map;

public class UserHelper {

    public static User getLogged() {
        FirebaseUser firebaseUser = FirebaseConfig.getAuth().getCurrentUser();
        if (firebaseUser == null) {
            return  null;

        } else {
            return new User(
                    firebaseUser.getDisplayName(),
                    firebaseUser.getEmail(),
                    firebaseUser.getPhotoUrl()
            );
        }
    }

    /**
     * Save user on database within his id
     * @return boolean to control success of operation
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
                    .child(FirebaseConfig.getAuth().getUid())
                    .updateChildren(userMap);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     *
     * @param user to be converted to hashMap so Firebase .updateChildren accpets
     * @return hash map <String, Object> where Object is user info
     */
    private static Map<String, Object> convertUserToMap(User user) {
        HashMap<String, Object> userMap = new HashMap<>();

        userMap.put(Constants.UsersNode.NAME, user.getName());
        userMap.put(Constants.UsersNode.EMAIL, user.getEmail());
        userMap.put(Constants.UsersNode.PICTURE, user.getPicture().toString());
        return userMap;
    }

    /**
     * Since we have a Uri type on User, we can't receive Firabse info directly using
     * dataSnapshot.getValue(User.class) because Firabase API can't convert an String
     * to an Uri
     *
     * In this case, we  map each key by ourselves
     *
     * @param userMap received from database
     * @return User object
     */
    public static User convertMapToUser(Map<String, Object> userMap) {
        try {
            Object idObject = userMap.get(Constants.UsersNode.ID);
            Object nameObject = userMap.get(Constants.UsersNode.NAME);
            Object emailObject = userMap.get(Constants.UsersNode.EMAIL);
            Object uriObject = userMap.get(Constants.UsersNode.PICTURE);

            String id = idObject.toString();
            String name = nameObject.toString();
            String email = emailObject.toString();

            Uri uri = null;
            if (uriObject != null ) {
                uri = Uri.parse(uriObject.toString());
            }

            return new User(id, name, email, uri);
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
