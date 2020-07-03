package com.example.whatsapp_clone.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.whatsapp_clone.R;
import com.example.whatsapp_clone.model.user.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {

    private ArrayList<User> contactList;

    public ContactAdapter(ArrayList<User> contactList) {
        this.contactList = contactList;
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
        User user = contactList.get(position);

        holder.contactListItemName.setText(user.getName());
        holder.contactListItemEmail.setText(user.getEmail());

        Picasso.get().load(user.getPicture()) // Set contact image
                .placeholder(R.drawable.profile)
                .error(R.drawable.profile)
                .into(holder.contactImage);
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    public class ContactViewHolder extends RecyclerView.ViewHolder {

        CircleImageView contactImage;
        TextView contactListItemName, contactListItemEmail;

        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            contactImage = itemView.findViewById(R.id.circleImageViewContact);
            contactListItemName = itemView.findViewById(R.id.contactListItemName);
            contactListItemEmail = itemView.findViewById(R.id.contactListItemEmail);
        }
    }
}
