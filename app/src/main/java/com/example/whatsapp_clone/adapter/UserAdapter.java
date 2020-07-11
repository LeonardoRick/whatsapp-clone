package com.example.whatsapp_clone.adapter;

import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.whatsapp_clone.R;
import com.example.whatsapp_clone.helper.Constants;
import com.example.whatsapp_clone.model.chat_item.ChatItem;
import com.example.whatsapp_clone.model.user.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * This adapter is used for both ChatList and ContactsList since they use the same visual structure
 */
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
        boolean isgGroupButton = false;
        try {

            if (getItemViewType(position) == CONTACT_TYPE) {
                User user = (User) list.get(position);
                holder.itemName.setText(user.getName());
                holder.descLabel.setText(user.getEmail());
                imageUri = user.getPicture();

                // check if its groupButton to set button image
                if (user.getId().equals(Constants.GroupListItem.ID))
                    isgGroupButton = true;

            } else if (getItemViewType(position) == CHAT_TYPE)  { // its CHAT_TYPE
                ChatItem chatItem = (ChatItem) list.get(position);
                if (chatItem.isGroup()) {
                    holder.itemName.setText(chatItem.getGroup().getName());
                    imageUri = chatItem.getGroup().getPicture();
                } else {
                    holder.itemName.setText(chatItem.getReceiver().getName());
                    imageUri = chatItem.getReceiver().getPicture();
                }

                holder.descLabel.setText(chatItem.getLastMessage());

            }

            if(imageUri != null) {
                Picasso.get().load(imageUri).into(holder.userImage);
            } else if (isgGroupButton) {
                holder.userImage.setImageResource(R.drawable.group_icon);
                holder.descLabel.setVisibility(View.GONE);
            } else {
                holder.userImage.setImageResource(R.drawable.profile);
            }
        } catch (Exception e ) {
            Log.e("UserAdapter", "onBindViewHolder: " + e.getMessage() );
        }
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
        TextView itemName, descLabel;

        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            userImage = itemView.findViewById(R.id.circleImageViewContact);
            itemName = itemView.findViewById(R.id.contactListItemName);
            descLabel = itemView.findViewById(R.id.contactListItemEmail);
        }
    }
}
