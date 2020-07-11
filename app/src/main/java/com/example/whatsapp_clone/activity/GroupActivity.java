package com.example.whatsapp_clone.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.example.whatsapp_clone.adapter.GroupMemberAdapter;
import com.example.whatsapp_clone.adapter.UserAdapter;
import com.example.whatsapp_clone.helper.Constants;
import com.example.whatsapp_clone.helper.FirebaseConfig;
import com.example.whatsapp_clone.helper.RecyclerItemClickListener;
import com.example.whatsapp_clone.model.user.User;
import com.example.whatsapp_clone.model.user.UserHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import com.example.whatsapp_clone.R;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;


public class GroupActivity extends AppCompatActivity {

    private RecyclerView recyclerViewGroupMembers, recyclerViewGroupContacts;
    private UserAdapter contactAdapter;
    private ArrayList<User> contactsList = new ArrayList<>();
    private ValueEventListener usersEventListener;
    private DatabaseReference usersRef;
    private User loggedUser;

    private GroupMemberAdapter groupMemberAdapter;
    private ArrayList<User> groupMembersList = new ArrayList<>();

    private Toolbar toolbar;

    private static final int INIT_CONTACTS_LIST_FROM_CREATE = 1000;
    private static final int INIT_CONTACTS_LIST_FROM_BACK_BUTTON = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Novo grupo");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // back Button

        usersRef = FirebaseConfig.getFirebaseDatabase().child(Constants.UsersNode.KEY);
        loggedUser = UserHelper.getLogged();

        setFabCreateGroupListener();


        setSelectedMembersRecyclerView();
    }

    public void updateToolbarMembers() {
        int totalSelectedMembers = groupMembersList.size();
        int totalContacts = contactsList.size() + totalSelectedMembers;

        toolbar.setSubtitle(totalSelectedMembers + " de " + totalContacts + " selecionados");
    }

    @Override
    protected void onStart() {
        super.onStart();
        recoverContactsList();
        setContactsRecyclerView();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (usersEventListener != null)
            usersRef.removeEventListener(usersEventListener);
    }

    /**
     *  recover user list passed from contacts list
     */
    private void recoverContactsList() {
        try {
            if (getIntent().getExtras() != null) {
                contactsList = (ArrayList<User>) getIntent().getExtras().getSerializable(Constants.IntentKey.CONTACTS_LIST);

                // creating Uri for each contact picture since
                // Uri is not serializable and can't be recovered from ContactsListFragment
                for (User contact : contactsList) {
                    String stringPicture = contact.getStringPicture();
                    if (stringPicture != null && !stringPicture.isEmpty())
                        contact.setPicture(Uri.parse(stringPicture));
                }
            } else {
                usersEventListener =
                        usersRef
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                Map<String, Object> userMap;
                                for  (DataSnapshot userNode : dataSnapshot.getChildren()) {
                                    userMap = UserHelper.mapUserFromFirebse(userNode);
                                    User user = UserHelper.convertMapToUser(userMap);
                                    Log.d("TAG", "onDataChange: "+ user.getName());
                                    if (!user.getId().equals(loggedUser.getId()))
                                        contactsList.add(user);
                                }
                                contactAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) { }
                        });
            }
            updateToolbarMembers(); // update toolbar subtitle
        } catch (Exception e) {
            Log.e("TAG", "recoverContactsList: " + e.getMessage() );
        }
    }


    private void setContactsRecyclerView() {
        recyclerViewGroupContacts = findViewById(R.id.recyclerViewGroupContacts);
        recyclerViewGroupContacts.setHasFixedSize(true);

        // Layout Manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerViewGroupContacts.setLayoutManager(layoutManager);

        // Adapter
        contactAdapter = new UserAdapter(contactsList);
        recyclerViewGroupContacts.setAdapter(contactAdapter);
        setContactsRecyclerViewListener();

    }

    private void setSelectedMembersRecyclerView() {
        recyclerViewGroupMembers = findViewById(R.id.recyclerViewGroupMembers);
        recyclerViewGroupMembers.setHasFixedSize(true);

        // Layout Manager
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(
            getApplicationContext(),
            LinearLayoutManager.HORIZONTAL,
            false
        );

        recyclerViewGroupMembers.setLayoutManager(layoutManager);

        // Adapter
        groupMemberAdapter = new GroupMemberAdapter(groupMembersList);
        recyclerViewGroupMembers.setAdapter(groupMemberAdapter);
        setGroupMembersRecyclerViewListener();
    }

    private void setContactsRecyclerViewListener() {
        recyclerViewGroupContacts.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        getApplicationContext(),
                        recyclerViewGroupContacts,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                User groupMember = contactsList.get(position);
                                contactsList.remove(groupMember); // remove from contact list
                                contactAdapter.notifyDataSetChanged();

                                groupMembersList.add(groupMember);
                                groupMemberAdapter.notifyDataSetChanged();

                                updateToolbarMembers();
                            }

                            @Override
                            public void onLongItemClick(View view, int position) { }

                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) { }
                        }
                )
        );
    }

    private void setGroupMembersRecyclerViewListener () {
        recyclerViewGroupMembers.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        getApplicationContext(),
                        recyclerViewGroupMembers,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                User groupMember = groupMembersList.get(position);
                                groupMembersList.remove(groupMember); // remove from new group list
                                groupMemberAdapter.notifyDataSetChanged();

                                contactsList.add(groupMember);
                                Collections.sort(contactsList);
                                contactAdapter.notifyDataSetChanged();

                                updateToolbarMembers();
                            }

                            @Override
                            public void onLongItemClick(View view, int position) { }

                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) { }
                        }
                )
        );
    }

    private void setFabCreateGroupListener() {
        FloatingActionButton fab = findViewById(R.id.fabCreateGroup);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), GroupRegisterActivity.class);
                intent.putExtra(Constants.IntentKey.CONTACTS_LIST, groupMembersList);
                startActivity(intent);
            }
        });
    }
}