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
    public static boolean saveOnDatabase(ChatItem chat) {

        try {
            Map<String, Object> chatMap = convertChatToMap(chat);


            if (chat.isGroup()) {
                FirebaseConfig.getFirebaseDatabase()
                        .child(Constants.ChatsNode.KEY)
                        .child(chat.getSender().getId())
                        .child(Constants.GroupNode.KEY)
                        .child(chat.getGroup().getId())
                        .setValue(chatMap);
            } else {
                // saving to sender
                FirebaseConfig.getFirebaseDatabase()
                        .child(Constants.ChatsNode.KEY)
                        .child(chat.getSender().getId())
                        .child(chat.getReceiver().getId())
                        .setValue(chatMap);

                // saving to receiver
                FirebaseConfig.getFirebaseDatabase()
                        .child(Constants.ChatsNode.KEY)
                        .child(chat.getReceiver().getId())
                        .child(chat.getSender().getId())
                        .setValue(chatMap);
            }

            return true;
        } catch (Exception e) {
            Log.e(TAG, "saveOnDatabase: " + e.getMessage());
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

        if (chatItem.getSender() != null) {
            Map<String, Object> senderMap = UserHelper.convertUserToMap(chatItem.getSender());
            chatMap.put(Constants.ChatsNode.SENDER, senderMap);
        }
        if (chatItem.getReceiver() != null) {
            Map<String, Object> receiverMap = UserHelper.convertUserToMap(chatItem.getReceiver());
            chatMap.put(Constants.ChatsNode.RECEIVER, receiverMap);
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

        for (DataSnapshot chatProperty : node.getChildren()) {
            switch (chatProperty.getKey()) {
                case Constants.ChatsNode.RECEIVER:
                    chatMap.put(Constants.ChatsNode.RECEIVER, UserHelper.mapUserFromFirebse(chatProperty));
                    break;
                case Constants.ChatsNode.SENDER:
                    chatMap.put(Constants.ChatsNode.SENDER, UserHelper.mapUserFromFirebse(chatProperty));
                    break;
                default:
                    chatMap.put(chatProperty.getKey(), chatProperty.getValue());
            }
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
            Object senderObject = chatMap.get(Constants.ChatsNode.SENDER);
            Object receiverObject = chatMap.get(Constants.ChatsNode.RECEIVER);

            Object isGroupObject = chatMap.get(Constants.ChatsNode.IS_GROUP);
            Object groupObject = chatMap.get(Constants.ChatsNode.GROUP);

            if (idObject != null) chat.setId(idObject.toString());

            if (lastMessageObject != null) chat.setLastMessage(lastMessageObject.toString());

            if (senderObject != null) {
                Map<String, Object> senderMap = (Map<String, Object>) senderObject;
                User sender = UserHelper.convertMapToUser(senderMap);
                chat.setSender(sender);
            }

            if (receiverObject != null) {
                Map<String, Object> receiverMap =  (Map<String, Object>) receiverObject;
                User receiver = UserHelper.convertMapToUser(receiverMap);
                chat.setReceiver(receiver);
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