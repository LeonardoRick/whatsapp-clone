package com.example.whatsapp_clone.adapter;

import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.whatsapp_clone.R;
import com.example.whatsapp_clone.helper.FirebaseConfig;
import com.example.whatsapp_clone.model.message.Message;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private ArrayList<Message> messagesList;

    private static final int SENDER_VIEW_TYPE = 0;
    private static final int RECEIVER_VIEW_TYPE = 1;

    public MessageAdapter(ArrayList<Message> messagesList) {
        this.messagesList = messagesList;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layout;

        // Inflate the right view depending on message type (sender or reciver)
        if (viewType == SENDER_VIEW_TYPE) layout = R.layout.message_sender;
        else layout = R.layout.message_receiver;

        View listItem = LayoutInflater.from(parent.getContext())
                .inflate(layout, parent, false);

        return new MessageViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message msg = messagesList.get(position);

        holder.setIsRecyclable(false);
        if(msg.isImage()) {
            Uri uri = Uri.parse(msg.getTextMessage());
            Picasso.get()
                    .load(uri)
                    .error(R.drawable.placeholder)
                    .into(holder.imageViewMessageChat);
            holder.textViewMessageChat.setVisibility(View.GONE);
        } else {
            holder.textViewMessageChat.setText(msg.getTextMessage());
            holder.imageViewMessageChat.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return messagesList.size();
    }

    /**
     *
     * @param position of list element to recover view type
     * @return view type so we can know witch layout to inflate
     * (if its a sender message or a receiver messege)
     */
    @Override
    public int getItemViewType(int position) {
        Message msg = messagesList.get(position);
        if(FirebaseConfig.getAuth().getUid().equals(msg.getSenderId()))
            return SENDER_VIEW_TYPE;

        return RECEIVER_VIEW_TYPE;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {

        TextView textViewMessageChat;
        ImageView imageViewMessageChat;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);

            // Both (message_sender.xml and message_receiver.xml) has view with same id
            // so code don't change here when inflating one or other
            textViewMessageChat = itemView.findViewById(R.id.textViewMessageChat);
            imageViewMessageChat = itemView.findViewById(R.id.imageViewMessageChat);

        }

    }

}