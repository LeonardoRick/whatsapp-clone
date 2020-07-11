package com.example.whatsapp_clone.adapter;

import android.util.Log;
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

public class GroupMemberAdapter extends RecyclerView.Adapter<GroupMemberAdapter.GroupMembersListViewHolder> {

    private ArrayList<User> groupMemberList;
    public GroupMemberAdapter(ArrayList<User> groupMemberList) {

        this.groupMemberList = groupMemberList;
    }

    @NonNull
    @Override
    public GroupMembersListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View listItem = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.selected_group_member_item_list, parent, false);
        return new GroupMembersListViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupMembersListViewHolder holder, int position) {
        try {
            User groupMember = groupMemberList.get(position);
            holder.firstName.setText(groupMember.getName());

            if (groupMember.getPicture() != null){
                Picasso.get().load(groupMember.getPicture()).into(holder.profileImage);
            } else {

                holder.profileImage.setImageResource(R.drawable.profile);
            }
        } catch (Exception e) {
            Log.e("TAG", "onBindViewHolder: " + e.getMessage() );
        }
    }

    @Override
    public int getItemCount() {
        return groupMemberList.size();
    }

    public class GroupMembersListViewHolder extends RecyclerView.ViewHolder {
        CircleImageView profileImage;
        TextView firstName;
        public GroupMembersListViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.circleImageViewProfileImageSelectedGroupMember);
            firstName = itemView.findViewById(R.id.textViewProfileNameSelectedGroupMember);
        }
    }
}
