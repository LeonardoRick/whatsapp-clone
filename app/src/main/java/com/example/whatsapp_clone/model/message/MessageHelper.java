package com.example.whatsapp_clone.model.message;

import com.example.whatsapp_clone.helper.Constants;
import com.example.whatsapp_clone.helper.FirebaseConfig;
import com.example.whatsapp_clone.model.chat_item.ChatItem;
import com.example.whatsapp_clone.model.chat_item.ChatItemHelper;
import com.example.whatsapp_clone.model.user.User;
import com.google.firebase.database.DatabaseReference;

public class MessageHelper {
    /**
     * Method to to save message that can be seen for both users of this chat
     *
     * @param message to be saved twice on Database to appear for both sender and receiver
     */
    public static void saveMessageOnDatabase(Message message) {

        DatabaseReference messagedRef = FirebaseConfig.getFirebaseDatabase().child(Constants.MessagesNode.KEY);
        if(!message.getTextMessage().isEmpty()) {

            // saving to sender
            messagedRef
                    .child(message.getSenderId())                          // current User id: '6KOKRa3ZjChRgDgJibBd81OQBWB2'
                    .child(message.getReceiverId())                       // selected contact id: 'Gtvn1CPiFGMgqyhau5M33LcRoH'
                    .push()                                              // generated id for this msg 'message_-MBMIPnJtG7PnE9bDS'
                    .setValue(message);                                 // value Object Message with its properties

            // saving to receiver
            messagedRef
                    .child(message.getReceiverId())
                    .child(message.getSenderId())
                    .push()
                    .setValue(message);

        }
    }
}
