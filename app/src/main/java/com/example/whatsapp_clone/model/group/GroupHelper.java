package com.example.whatsapp_clone.model.group;

import android.net.Uri;
import android.util.Log;

import com.example.whatsapp_clone.helper.Constants;
import com.example.whatsapp_clone.helper.FirebaseConfig;
import com.example.whatsapp_clone.model.chat_item.ChatItem;
import com.example.whatsapp_clone.model.chat_item.ChatItemHelper;
import com.example.whatsapp_clone.model.user.User;
import com.example.whatsapp_clone.model.user.UserHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GroupHelper {

    public static final String TAG = "GroupHelper";

    public static boolean saveOnDatabase(Group group) {

        try {
            Map<String, Object> groupMap = convertGroupToMap(group);
            FirebaseConfig.getFirebaseDatabase()
                    .child(Constants.GroupNode.KEY)
                    .child(group.getId())
                    .setValue(groupMap);

            // Saving new chatItem to each member of group
            ChatItem chat = new ChatItem();
            chat.setId(UUID.randomUUID().toString());
            chat.setLastMessage("Novo grupo");
            chat.setGroup(group);
            chat.setIsGroup(true);

            for (User member : group.getMembers()) {
                ChatItemHelper.saveGroupChatItemOnDatabase(chat, member.getId());
            }

            return true;
        } catch (Exception e) {
            Log.e(TAG, "saveOnDatabase: " + e.getMessage());
            return false;
        }
    }


    /**
     * @param group to be converted to hashMap so Firebase accpets since uri is not serializable
     * @return Map <String, Object> where Object is user info
     */
    public static Map<String, Object> convertGroupToMap(Group group) {
        Map<String, Object> groupMap = new HashMap<>();

        if (group.getId() != null) groupMap.put(Constants.ID, group.getId());
        if (group.getName() != null) groupMap.put(Constants.GroupNode.NAME, group.getName());
        if (group.getPicture() != null ) {
            groupMap.put(Constants.GroupNode.PICTURE, group.getPicture().toString());
        }


        if (group.getCreator() != null)  {
            Map<String, Object> creatorMap = UserHelper.convertUserToMap(group.getCreator());
            groupMap.put(Constants.GroupNode.CREATOR, creatorMap);
        }
         // Mapping each user member
        if (group.getMembers() != null) {
            ArrayList<Map<String, Object>> usersListMap = new ArrayList<>();
            for (User member : group.getMembers()) {
                Map<String, Object> userMap = UserHelper.convertUserToMap(member);
                usersListMap.add(userMap);
            }
            groupMap.put(Constants.GroupNode.MEMBERS, usersListMap);
        }

        return groupMap;
    }

    /**
     * Since we have a Uri type on Group, we can't receive Firebase info directly using
     * dataSnapshot.getValue(Group.class) because Firebase API can't convert an String
     * to an Uri
     *
     * In this case, we  map each key by ourselves
     *
     * @param groupMap received from database
     * @return User object
     */

    public static Group convertMapToGroup(Map<String, Object> groupMap) {
        Group group = new Group();
        Object idObject = groupMap.get(Constants.ID);
        Object nameObject = groupMap.get(Constants.GroupNode.NAME);
        ArrayList<Map<String, Object>> membersObject =  (ArrayList<Map<String, Object>>) groupMap.get(Constants.GroupNode.MEMBERS);
        Map<String, Object> creatorObject = (Map<String, Object>) groupMap.get(Constants.GroupNode.CREATOR);
        Object pictureObject = groupMap.get(Constants.GroupNode.PICTURE);

        if (idObject != null) group.setId(idObject.toString());
        if (nameObject != null) group.setName(nameObject.toString());
        if (membersObject != null)  {
            // add each user to member
            ArrayList<User> membersList = new ArrayList<>();
            for (Map<String, Object> memberMap : membersObject ) {
                User member = UserHelper.convertMapToUser(memberMap);
                membersList.add(member);
                group.setMembers(membersList);
            }
        }
        if (creatorObject != null) {
            User creator = UserHelper.convertMapToUser(creatorObject);
            group.setCreator(creator);
        }
        if (pictureObject != null) {
            Uri uri = Uri.parse(pictureObject.toString());
            group.setPicture(uri);
        }

        return group;
    }
}