package com.example.whatsapp_clone.model.chat_item;

import android.util.Log;

import com.example.whatsapp_clone.helper.Constants;
import com.example.whatsapp_clone.helper.FirebaseConfig;
import com.example.whatsapp_clone.model.message.Message;
import com.example.whatsapp_clone.model.user.User;
import com.example.whatsapp_clone.model.user.UserHelper;
import com.google.firebase.database.DataSnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ChatItemHelper {

    /**
     * Method to show chats list on chatsListFragment
     * @param message
     * @param sender
     * @param receiver
     * @return boolean to control success of operation
     */
    public static boolean saveChatOnDatabase(Message message, User sender, User receiver) {

        try {
            String chatId = UUID.randomUUID().toString();
            String lastMessage;

            if (message.isImage()) {
                lastMessage = "Imagem";
            } else {
                lastMessage = message.getTextMessage();
            }

            ChatItem chatItem = new ChatItem(
                    chatId,
                    lastMessage,
                    receiver
            );

            Map<String, Object> chatMap = convertChatToMap(chatItem);

            // saving to sender
            FirebaseConfig.getFirebaseDatabase()
                    .child(Constants.ChatsNode.KEY)
                    .child(sender.getId())
                    .child(receiver.getId())
                    .setValue(chatMap);

            // saving to receiver
            FirebaseConfig.getFirebaseDatabase()
                    .child(Constants.ChatsNode.KEY)
                    .child(receiver.getId())
                    .child(sender.getId())
                    .setValue(chatMap);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * @param chatItem to be converted to hashMap so Firebase .updateChildren accpets
     * @return Map <String, Object> where Object is user info
     */

    public static Map<String, Object> convertChatToMap(ChatItem chatItem) {
        Map<String, Object> chatMap = new HashMap<>();

        chatMap.put(Constants.ID, chatItem.getId());
        chatMap.put(Constants.ChatsNode.LAST_MESSAGE, chatItem.getLastMessage());

        Map<String, Object> selectedContactMap = UserHelper.convertUserToMap(chatItem.getSelectedContact());
        chatMap.put(Constants.ChatsNode.SELECTED_CONTACT, selectedContactMap);

        return chatMap;
    }

    /**
     * @param chatNode dataSnapshot that has userValues from Firebase Database to be to a HashMap
     * @return Map<String, Object> with single chat info
     *
     * check if dataSnapshotExists before sending
     */
    public static Map<String, Object> mapChatFromFirebase(DataSnapshot chatNode) {
        Map<String, Object> chatMap = new HashMap<>();
        String selectedContactKey = Constants.ChatsNode.SELECTED_CONTACT;
        chatMap.put(Constants.ID, chatNode.getKey());
        for (DataSnapshot chatSnapshot : chatNode.getChildren()) {

            if (chatSnapshot.getKey() == selectedContactKey) {
                chatMap.put(selectedContactKey, UserHelper.mapUserFromFirebse(chatSnapshot));
            } else {
                chatMap.put(chatSnapshot.getKey(), chatSnapshot.getValue());
            }
        }

        return chatMap;
    };

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
        try  {
            String id = chatMap.get(Constants.ID).toString();
            String lastMessage = chatMap.get(Constants.ChatsNode.LAST_MESSAGE).toString();
            Map<String, Object> selectedContactMap =  (Map<String, Object>) chatMap.get(Constants.ChatsNode.SELECTED_CONTACT);

            User selectedContact = UserHelper.convertMapToUser(selectedContactMap);

            return new ChatItem(
                    id,
                    lastMessage,
                    selectedContact
            );
        } catch (Exception e) {
            Log.e("ChatItemHelper", "convertMapToChat: " + e.getMessage());
            return null;
        }
    }
}