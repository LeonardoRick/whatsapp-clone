package com.example.whatsapp_clone.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.example.whatsapp_clone.R;
import com.example.whatsapp_clone.activity.ChatActivity;
import com.example.whatsapp_clone.activity.GroupActivity;
import com.example.whatsapp_clone.adapter.UserAdapter;
import com.example.whatsapp_clone.helper.Constants;
import com.example.whatsapp_clone.helper.FirebaseConfig;
import com.example.whatsapp_clone.helper.RecyclerItemClickListener;
import com.example.whatsapp_clone.model.user.User;
import com.example.whatsapp_clone.model.user.UserHelper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

public class ContactsListFragment extends Fragment {

    private RecyclerView recyclerViewContacts;
    private UserAdapter adapter;
    private ArrayList<User> contactList = new ArrayList<>();
    private boolean recyclerViewClickFlag = false;
    private ValueEventListener contactsEventListener;

    private DatabaseReference usersRef;
    private User loggedUser;

    public ContactsListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
        recoverContactsList();
    }

    @Override
    public void onStop() {
        super.onStop();
    usersRef.removeEventListener(contactsEventListener);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.users_list, container, false);


        usersRef = FirebaseConfig.getFirebaseDatabase().child(Constants.UsersNode.KEY);
        loggedUser = UserHelper.getLogged();


        setContactRecyclerView(view);

        return view;  // Inflate the layout for this fragment
    }

    private void recoverContactsList() {
        contactsEventListener =
                usersRef
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        contactList.clear();
                        if (dataSnapshot.exists()) {
                            setCreateGroupButton(); // add first item of list as createGroupButton
                            Map<String, Object> userMap;              // userMap to recover users info to list
                            for (DataSnapshot userNode : dataSnapshot.getChildren()) {
                                userMap = UserHelper.mapUserFromFirebse(userNode);
                                User user = UserHelper.convertMapToUser(userMap);

                                if (!user.getId().equals(loggedUser.getId()))// remove user from his own list of contacts
                                    contactList.add(user);
                            }
                            recyclerViewClickFlag = true;
                            adapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) { }
                });
    }

    private void setContactRecyclerView(View view) {
        recyclerViewContacts = view.findViewById(R.id.recyclerViewUsers);
        recyclerViewContacts.setHasFixedSize(true);

        // Layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        recyclerViewContacts.setLayoutManager(layoutManager);

        // Adapter
        adapter = new UserAdapter(contactList);
        recyclerViewContacts.setAdapter(adapter);
        setRecyclerViewClickListener();
    }

    private void setRecyclerViewClickListener() {
        recyclerViewContacts.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        getActivity(),
                        recyclerViewContacts,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {

                                // Check if click is allowed (after list of contacts is loaded)
                                if (recyclerViewClickFlag) {
                                    User selectedContact = contactList.get(position);
                                    // check if its groupButton to start group intent
                                    if (selectedContact.getId().equals(Constants.GroupListItem.ID)) {
                                        contactList.remove(selectedContact); // remove button from list
                                        navigateToGroupActivity(view);
                                    } else {
                                        navigateToChatActivity(view, selectedContact);
                                    }
                                }

                            }

                            @Override
                            public void onLongItemClick(View view, int position) { }

                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) { }
                        }
                )
        );
    }

    private void navigateToGroupActivity(View view) {
        Intent intent = new Intent (view.getContext(), GroupActivity.class);
        intent.putExtra(Constants.IntentKey.CONTACTS_LIST, contactList);
        startActivity(intent);
    }

    private void navigateToChatActivity(View view, User selectedContact) {
        Intent intent = new Intent(view.getContext(), ChatActivity.class);
        // Sending info from selected user to chat activity (Remember to implement Serializable on User class)
        intent.putExtra(Constants.IntentKey.SELECTED_CONTACT, selectedContact);
        startActivity(intent);
    }

    /**
     * Creating item that allows user to create groups
     */
    private void setCreateGroupButton() {
        User groupItem = new User();
        groupItem.setId(Constants.GroupListItem.ID);
        groupItem.setName(Constants.GroupListItem.NAME);
        contactList.add(groupItem);
    }
}