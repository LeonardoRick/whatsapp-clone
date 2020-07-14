package com.example.whatsapp_clone.model.message;

import android.util.Log;

import com.example.whatsapp_clone.helper.Constants;
import com.example.whatsapp_clone.helper.FirebaseConfig;

import com.google.firebase.database.DatabaseReference;

public class MessageHelper {
    private static  final String TAG = "MessageHelper";
    private static final DatabaseReference messagedRef = FirebaseConfig.getFirebaseDatabase().child(Constants.MessagesNode.KEY);
    /**
     * Method to to save message that can be seen for both users of this chat
     * @param message to be saved twice on Database to appear for both sender and receiver
     */
    public static boolean saveDirectMessageOnDatabase(Message message) {

        try {
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
            return true;
        } catch (Exception e) {
            Log.e(TAG, "saveDirectMessageOnDatabase: " + e.getMessage());
            return false;
        }
    }

    /**
     * Method to to save message that can be seen all group users
     * @param message to be saved
     */
     public static boolean saveGroupMessageOnDatabase(Message message, String memberId) {
        try {
            messagedRef.child(memberId)
                    .child(message.getReceiverId())
                    .push()
                    .setValue(message);
            return true;
        } catch (Exception e) {
            Log.e(TAG, "saveGroupMessageOnDatabase: " + e.getMessage());
            return false;
        }
     }
}
