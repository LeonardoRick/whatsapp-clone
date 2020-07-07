package com.example.whatsapp_clone.adapter;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.whatsapp_clone.R;
import com.example.whatsapp_clone.model.chat_item.ChatItem;
import com.example.whatsapp_clone.model.user.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ContactViewHolder> {

    private ArrayList<?> list;

    public static final int CONTACT_TYPE = 0;
    public static final int CHAT_TYPE = 1;
    public static final int NON_DEFINED_TYPE = -1;

    public UserAdapter(ArrayList<?> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View listItem = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contact_list_item, parent, false);
        return new ContactViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {

        Uri imageUri = null;
        if (getItemViewType(position) == CONTACT_TYPE) {
            User user = (User) list.get(position);
            holder.userListItemName.setText(user.getName());
            holder.userDescLabel.setText(user.getEmail());
            imageUri = user.getPicture();

        } else if (getItemViewType(position) == CHAT_TYPE)  { // its CHAT_TYPE
            ChatItem chatItem = (ChatItem) list.get(position);
            holder.userListItemName.setText(chatItem.getSelectedContact().getName());
            holder.userDescLabel.setText(chatItem.getLastMessage());
            imageUri = chatItem.getSelectedContact().getPicture();
        } else {

            // default view for non defined type
        }



        Picasso.get().load(imageUri) // Set contact image
                .placeholder(R.drawable.profile)
                .error(R.drawable.profile)
                .into(holder.userImage);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    @Override
    public int getItemViewType(int position) {
        if (list.get(0) instanceof User)  { return CONTACT_TYPE; }

        else if (list.get(0) instanceof ChatItem) { return CHAT_TYPE; }

        return NON_DEFINED_TYPE;
    }

    public class ContactViewHolder extends RecyclerView.ViewHolder {

        CircleImageView userImage;
        TextView userListItemName, userDescLabel;

        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            userImage = itemView.findViewById(R.id.circleImageViewContact);
            userListItemName = itemView.findViewById(R.id.contactListItemName);
            userDescLabel = itemView.findViewById(R.id.contactListItemEmail);
        }
    }
}
