package com.example.whatsapp_clone.activity;

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

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import com.example.whatsapp_clone.R;

import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.Collections;


public class GroupActivity extends AppCompatActivity {

    private RecyclerView recyclerViewGroupMembers, recyclerViewGroupContacts;
    private UserAdapter contactAdapter;
    private ArrayList<User> contactsList = new ArrayList<>();
    private DatabaseReference usersRef;

    private GroupMemberAdapter groupMemberAdapter;
    private ArrayList<User> groupMembersList = new ArrayList<>();
    private User loggedUser;

    private Toolbar toolbar;

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

        recoverContactsList();

        FloatingActionButton fab = findViewById(R.id.fabCreateGroup);


        setContactsRecyclerView();
        setSelectedMembersRecyclerView();
    }

    public void updateToolbarMembers() {
        int totalSelectedMembers = groupMembersList.size();
        int totalContacts = contactsList.size() + totalSelectedMembers;

        toolbar.setSubtitle(totalSelectedMembers + " de " + totalContacts + " selecionados");
    }


    /**
     *  recover user list passed from contacts list
     */
    public void recoverContactsList() {
        try {
            contactsList = (ArrayList<User>) getIntent().getExtras().getSerializable(Constants.IntentKey.CONTACTS_LIST);

            // creating Uri for each contact picture since
            // Uri is not serializable and can't be recovered from ContactsListFragment
            for (User contact : contactsList) {
                String stringPicture = contact.getStringPicture();
                if (stringPicture != null && !stringPicture.isEmpty())
                    contact.setPicture(Uri.parse(stringPicture));
            }
            updateToolbarMembers(); // update toolbar sbtitle
        } catch (Exception e) {
            Log.e("TAG", "recoverContactsList: " + e.getMessage() );
        }
    }

    public void setContactsRecyclerView() {
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

    public void setSelectedMembersRecyclerView() {
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

    public void setContactsRecyclerViewListener() {
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

    public void setGroupMembersRecyclerViewListener () {
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