package com.example.whatsapp_clone.model.message;

import com.example.whatsapp_clone.helper.Constants;
import com.example.whatsapp_clone.helper.FirebaseConfig;
import com.example.whatsapp_clone.model.chat_item.ChatItemHelper;
import com.example.whatsapp_clone.model.user.User;
import com.google.firebase.database.DatabaseReference;

public class MessageHelper {
    /**
     * Method to to save message that can be seen for both users of this chat
     *
     * @param textMsg message to be saved twice on Database to appear for both sender and receiver
     * @param sender user
     * @param receiver user
     */
    public static void saveMessageOnDatabase(String textMsg, User sender, User receiver, boolean isImage) {

        DatabaseReference messagedRef = FirebaseConfig.getFirebaseDatabase().child(Constants.MessagesNode.KEY);
        if(!textMsg.isEmpty()) {
            Message message = new Message(sender.getId(), textMsg, isImage );

            // saving to sender
            messagedRef
                    .child(sender.getId())                                       // current User id: '6KOKRa3ZjChRgDgJibBd81OQBWB2'
                    .child(receiver.getId())                                    // selected contact id: 'Gtvn1CPiFGMgqyhau5M33LcRoH'
                    .push()                                              // generated id for this msg 'message_-MBMIPnJtG7PnE9bDS'
                    .setValue(message);                                 // value Object Message with its properties

            // saving to receiver
            messagedRef
                    .child(receiver.getId())
                    .child(sender.getId())
                    .push()
                    .setValue(message);

            // Saving chat to show on list of chats
            ChatItemHelper.saveChatOnDatabase(message, sender, receiver);
        }
    }
}
