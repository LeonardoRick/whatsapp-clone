package com.example.whatsapp_clone.model.chat_item;

import android.util.Log;

import com.example.whatsapp_clone.helper.Constants;
import com.example.whatsapp_clone.helper.FirebaseConfig;
import com.example.whatsapp_clone.model.group.Group;
import com.example.whatsapp_clone.model.group.GroupHelper;
import com.example.whatsapp_clone.model.user.User;
import com.example.whatsapp_clone.model.user.UserHelper;
import com.google.firebase.database.DataSnapshot;

import java.util.HashMap;
import java.util.Map;


public class ChatItemHelper {
    public static final String TAG = "ChatItemHelper";
    /**
     * Method to show chats list on chatsListFragment
     * @param chat to be saved on database
     * @return boolean to control success of operation
     */
    public static boolean saveDirectChatItemOnDatabase(ChatItem chat) {
        String loggedUserId = UserHelper.getLogged().getId();
        String receiverId = chat.getSelectedContact().getId();

        // Creating chatItem to save on receiver database so he
        // can se info from logged user, who is sending message to him
        ChatItem chatToReceiver = new ChatItem(chat); // copy info from first chat
        chatToReceiver.setSelectedContact(UserHelper.getLogged()); // set logged user as receiver of receiver user

        try {
            Map<String, Object> chatMap = convertChatToMap(chat);
            Map<String, Object> chatMapToReceiver = convertChatToMap(chatToReceiver);

            // saving to sender
            FirebaseConfig.getFirebaseDatabase()
                    .child(Constants.ChatsNode.KEY)
                    .child(loggedUserId)
                    .child(receiverId)
                    .setValue(chatMap);

            // saving to receiver
            FirebaseConfig.getFirebaseDatabase()
                    .child(Constants.ChatsNode.KEY)
                    .child(receiverId)
                    .child(loggedUserId)
                    .setValue(chatMapToReceiver);

            return true;
        } catch (Exception e) {
            Log.e(TAG, "saveOnDatabase: " + e.getMessage());
            return false;
        }
    }

    /**
     * Method to show groups on chatsListFragment. Be sure that logged user is setted
     * as a member of group before calling this method
     *
     * (probably he is because when group is created he is add as a member)
     *
     * @param chat object to be saved on database
     * @param memberId of each member o group
     * @return boolean if method saved info on Firebase properly
     */
    public static boolean saveGroupChatItemOnDatabase(ChatItem chat, String memberId) {
        try  {
            Map<String, Object> chatMap = ChatItemHelper.convertChatToMap(chat);
            FirebaseConfig.getFirebaseDatabase()
                    .child(Constants.ChatsNode.KEY)
                    .child(memberId)
                    .child(Constants.GroupNode.KEY)
                    .child(chat.getGroup().getId())
                    .setValue(chatMap);

            return true;
        } catch (Exception e) {
            Log.e(TAG, "saveGroupChatItemOnDatabase: " + e.getMessage());
            return false;
        }
    }

    /**
     * Called to delete chatItem to logged user. Other user will keep his chat
     * Remember to call this method removing messages node too
     * @param chat to be removed
     */

    public static boolean removeDirectChatItem(ChatItem chat) {
        try {
            FirebaseConfig.getFirebaseDatabase()
                    .child(Constants.ChatsNode.KEY)
                    .child(UserHelper.getLogged().getId())
                    .child(chat.getSelectedContact().getId())
                    .removeValue();

            if (cleanMessagesDatabase(UserHelper.getLogged().getId(), chat.getSelectedContact().getId()))
                return true;
            return false;
        } catch (Exception e) {
            Log.e(TAG, "removeDirectChatItem: " + e.getMessage());
            return false;
        }
    }

    public static boolean removeGroupChatItem(ChatItem chat) {
        try {
            FirebaseConfig.getFirebaseDatabase()
                    .child(Constants.ChatsNode.KEY)
                    .child(UserHelper.getLogged().getId())
                    .child(Constants.GroupNode.KEY)
                    .child(chat.getGroup().getId())
                    .removeValue();

            if (cleanMessagesDatabase(UserHelper.getLogged().getId(), chat.getGroup().getId()))
                return true;
            return false;
        } catch (Exception e) {
            Log.e(TAG, "removeDirectChatItem: " + e.getMessage());
            return false;
        }
    }

    /**
     * Used to clean messaged node on database when user delete chat with group or user
     * @param senderId logged user id
     * @param receiverId id of user or group specified from user
     */
    private static boolean cleanMessagesDatabase(String senderId, String receiverId) {
        try {
            FirebaseConfig.getFirebaseDatabase()
                    .child(Constants.MessagesNode.KEY)
                    .child(senderId)
                    .child(receiverId)
                    .removeValue();
            return true;
        } catch (Exception e) {
            Log.e(TAG, "cleanMessagesDatabase: " + e.getMessage() );
            return false;
        }
    }

    /**
     * @param chatItem to be converted to hashMap so Firebase .updateChildren accpets
     * @return Map <String, Object> where Object is user info
     */
    public static Map<String, Object> convertChatToMap(ChatItem chatItem) {
        Map<String, Object> chatMap = new HashMap<>();

        if (chatItem.getId() != null) chatMap.put(Constants.ID, chatItem.getId());
        if (chatItem.getLastMessage() != null) chatMap.put(Constants.ChatsNode.LAST_MESSAGE, chatItem.getLastMessage());

        if (chatItem.getSelectedContact() != null) {
            Map<String, Object> selectedContactMap = UserHelper.convertUserToMap(chatItem.getSelectedContact());
            chatMap.put(Constants.ChatsNode.SELECTED_CONTACT, selectedContactMap);
        }

        // this property must never be null
        chatMap.put(Constants.ChatsNode.IS_GROUP, chatItem.isGroup());

        if (chatItem.getGroup() != null) chatMap.put(Constants.ChatsNode.GROUP, GroupHelper.convertGroupToMap(chatItem.getGroup()));

        return chatMap;
    }

    /**
     * @param chatNode dataSnapshot that has userValues from Firebase Database to be to a HashMap
     * @return Map<String, Object> with single chat info
     *
     * check if dataSnapshotExists before sending
     */
    public static Map<String, Object> mapChatFromFirebase(DataSnapshot chatNode) {
        Map<String, Object> chatMap;

        if (chatNode.getKey().equals(Constants.GroupNode.KEY)) {
            chatMap = mapChatProperties(chatNode.child(Constants.GroupNode.KEY));
            return chatMap;
        }

        chatMap = mapChatProperties(chatNode);
        return chatMap;
    }

    private static Map<String, Object> mapChatProperties(DataSnapshot node) {
        Map<String, Object> chatMap = new HashMap<>();
        chatMap.put(Constants.ID, node.getKey());

        Log.d(TAG, "mapChatFromFirebase: " + chatMap.get("picture"));
        for (DataSnapshot chatProperty : node.getChildren()) {

            if (chatProperty.getKey().equals(Constants.ChatsNode.SELECTED_CONTACT))
                chatMap.put(Constants.ChatsNode.SELECTED_CONTACT, UserHelper.mapUserFromFirebse(chatProperty));
            else
                chatMap.put(chatProperty.getKey(), chatProperty.getValue());
        }
        Log.d(TAG, "mapChatProperties: " + chatMap.get("isGroup"));
        return chatMap;
    }

    /**
     * ChatItem class has a User property and User has a Uri property. We can't receive Firebase info directly using
     * dataSnapshot.getValue(User.class) because Firebase API can't convert an String
     * to an Uri.
     *
     * In this case, we  map each key by ourselves
     *
     * @param chatMap received from database
     * @return ChatItem object containing a selectedUser;
     */

    public static ChatItem convertMapToChat(Map<String, Object> chatMap) {
        ChatItem chat = new ChatItem();
        try  {
            Object idObject = chatMap.get(Constants.ID);
            Object lastMessageObject = chatMap.get(Constants.ChatsNode.LAST_MESSAGE);
            Object selectedContactObject = chatMap.get(Constants.ChatsNode.SELECTED_CONTACT);

            Object isGroupObject = chatMap.get(Constants.ChatsNode.IS_GROUP);
            Object groupObject = chatMap.get(Constants.ChatsNode.GROUP);

            if (idObject != null) chat.setId(idObject.toString());

            if (lastMessageObject != null) chat.setLastMessage(lastMessageObject.toString());

            if (selectedContactObject != null) {
                Map<String, Object> selectedContactMap = (Map<String, Object>) selectedContactObject;
                User selectedContact = UserHelper.convertMapToUser(selectedContactMap);
                chat.setSelectedContact(selectedContact);
            }

            if (isGroupObject != null) chat.setIsGroup(((boolean) isGroupObject));

            if (groupObject != null) {
                Map<String, Object> groupMap = (Map<String, Object>) groupObject;
                Group group = GroupHelper.convertMapToGroup(groupMap);

                chat.setGroup(group);
            }

            return chat;
        } catch (Exception e) {
            Log.e("ChatItemHelper", "convertMapToChat: " + e.getMessage());
            return null;
        }
    }
}