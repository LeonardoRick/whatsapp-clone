package com.example.whatsapp_clone.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.example.whatsapp_clone.R;
import com.example.whatsapp_clone.activity.ChatActivity;
import com.example.whatsapp_clone.adapter.UserAdapter;
import com.example.whatsapp_clone.helper.Constants;
import com.example.whatsapp_clone.helper.FirebaseConfig;
import com.example.whatsapp_clone.helper.RecyclerItemClickListener;
import com.example.whatsapp_clone.model.chat_item.ChatItem;
import com.example.whatsapp_clone.model.chat_item.ChatItemHelper;
import com.example.whatsapp_clone.model.user.User;
import com.example.whatsapp_clone.model.user.UserHelper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

public class ChatsListFragment extends Fragment {

    private RecyclerView recyclerViewChats;
    private UserAdapter adapter;
    private ArrayList<ChatItem> chatsList = new ArrayList<>();
    private ValueEventListener eventListener;

    private DatabaseReference chatsRef;
    private User loggedUser;

    public ChatsListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         View view = inflater.inflate(R.layout.users_list, container, false);

        recyclerViewChats = view.findViewById(R.id.recyclerViewUsers);
         loggedUser = UserHelper.getLogged();

         setChatRecyclerView(view);
         return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        recoverChatsList();
    }

    @Override
    public void onStop() {
        super.onStop();
        chatsRef.removeEventListener(eventListener);
    }

    /**
     * Filter chatsItem list
     * @param text passed from MaterialSearchView to search
     *                userName or lastMessage
     *
     */
    public void searchChats(String text) {
        ArrayList<ChatItem> chatsFilteredList = new ArrayList<>();

        for (ChatItem chatItem : chatsList) {

            String name = chatItem.getSelectedContact().getName().toLowerCase();
            String lastMsg = chatItem.getLastMessage().toLowerCase();

            if (name.contains(text) || lastMsg.contains(text)) {
                chatsFilteredList.add(chatItem);
            }
        }

        updateAdapter(chatsFilteredList);
    }


    /**
     * Called from activity to update list when user closes searchView
     */
    public void updateAdapterWithStartList() {
        updateAdapter(chatsList);
    }
    public void updateAdapter(ArrayList<ChatItem> list) {
        adapter = new UserAdapter(list);
        recyclerViewChats.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void recoverChatsList() {
        chatsRef = FirebaseConfig.getFirebaseDatabase()
                .child(Constants.ChatsNode.KEY)
                .child(loggedUser.getId());
        eventListener =
                chatsRef
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            chatsList.clear();
                            Map<String, Object> chatMap;
                            for (DataSnapshot chatNode : dataSnapshot.getChildren()) {

                                chatMap = ChatItemHelper.mapChatFromFirebase(chatNode);
                                ChatItem chatItem = ChatItemHelper.convertMapToChat(chatMap);
                                chatsList.add(chatItem);
                            }
                            adapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) { }
                });
    }


    private void setChatRecyclerView (View view) {
        recyclerViewChats.setHasFixedSize(true);

        // Layout Manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        recyclerViewChats.setLayoutManager(layoutManager);

        // Adapter
        adapter = new UserAdapter(chatsList);
        recyclerViewChats.setAdapter(adapter);

        setRecyclerViewClickListener();
    }

    private void setRecyclerViewClickListener() {
        recyclerViewChats.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        getActivity(),
                        recyclerViewChats,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {

                                ChatItem chatItem = chatsList.get(position);

                                Intent intent = new Intent(getActivity(), ChatActivity.class);

                                // Sending info from selected user to chat activity (Remember to implement Serializable on User class)
                                intent.putExtra(Constants.IntentKey.SELECTED_CONTACT, chatsList.get(position).getSelectedContact());
                                startActivity(intent);
                            }

                            @Override
                            public void onLongItemClick(View view, int position) { }

                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) { }
                        }
                )
        );
    }
}