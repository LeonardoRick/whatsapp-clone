package com.example.whatsapp_clone.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.whatsapp_clone.R;
import com.example.whatsapp_clone.adapter.ContactAdapter;
import com.example.whatsapp_clone.helper.Constants;
import com.example.whatsapp_clone.helper.FirebaseConfig;
import com.example.whatsapp_clone.model.user.User;
import com.example.whatsapp_clone.model.user.UserHelper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ContactFragment extends Fragment {

    private ArrayList<User> contactList = new ArrayList<>();
    ContactAdapter adapter;
    RecyclerView recyclerView;

    public ContactFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_contact, container, false);

        recyclerView = view.findViewById(R.id.contactRecyclerView);
        setContactRecyclerView(view);

        FirebaseConfig.getFirebaseDatabase()
                .child(Constants.UsersNode.KEY)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        contactList.clear();
                        if (dataSnapshot.exists()) {
                            Map<String, Object> userMap = new HashMap<>();
                            for (DataSnapshot usersNode : dataSnapshot.getChildren()) {
                                userMap.put(Constants.UsersNode.ID, usersNode.getKey());
                                for (DataSnapshot userSnapshot : usersNode.getChildren() ) {
                                    userMap.put(userSnapshot.getKey(), userSnapshot.getValue());
                                }
                                User user = UserHelper.convertMapToUser(userMap);
                                contactList.add(user);
                            }



                            adapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) { }
                });

        return view;  // Inflate the layout for this fragment
    }

    public void setContactRecyclerView(View view) {

        recyclerView.setHasFixedSize(true);
        // Layout manager
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.addItemDecoration(new DividerItemDecoration(view.getContext(), LinearLayout.VERTICAL));
        recyclerView.setLayoutManager(layoutManager);

        // Specify adapter
        adapter = new ContactAdapter(contactList);
        recyclerView.setAdapter(adapter);
    }
}