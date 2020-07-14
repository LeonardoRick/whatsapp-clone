package com.example.whatsapp_clone.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.Toast;

import com.example.whatsapp_clone.R;
import com.example.whatsapp_clone.activity.ChatActivity;
import com.example.whatsapp_clone.adapter.UserAdapter;
import com.example.whatsapp_clone.helper.Constants;
import com.example.whatsapp_clone.helper.FirebaseConfig;
import com.example.whatsapp_clone.helper.RecyclerItemClickListener;
import com.example.whatsapp_clone.model.chat_item.ChatItem;
import com.example.whatsapp_clone.model.chat_item.ChatItemHelper;
import com.example.whatsapp_clone.model.group.GroupHelper;
import com.example.whatsapp_clone.model.message.MessageHelper;
import com.example.whatsapp_clone.model.user.User;
import com.example.whatsapp_clone.model.user.UserHelper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

public class ChatsListFragment extends Fragment {

    private RecyclerView recyclerViewChats;
    private UserAdapter adapter;
    private ArrayList<ChatItem> chatsList = new ArrayList<>();
    private ValueEventListener eventListener;

    private DatabaseReference currentUserChatsRef;
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
        currentUserChatsRef.removeEventListener(eventListener);
    }

    /**
     * Filter chatsItem list
     * @param text passed from MaterialSearchView to search
     *                userName or lastMessage
     *
     */
    public void searchChats(String text) {
        ArrayList<ChatItem> chatsFilteredList = new ArrayList<>();
        String name;
        String lastMsg;
        for (ChatItem chat : chatsList) {
            if (chat.isGroup()) name = chat.getGroup().getName().toLowerCase();
            else name = chat.getSelectedContact().getName().toLowerCase();

            lastMsg = chat.getLastMessage().toLowerCase();

            if (name.contains(text) || lastMsg.contains(text))
                chatsFilteredList.add(chat);
        }
        updateAdapter(chatsFilteredList);
    }

    /**
     * Called from MainActivity to update list when user closes searchView
     */
    public void updateAdapterWithStartList() {
        updateAdapter(chatsList);
    }

    private void updateAdapter(ArrayList<ChatItem> list) {
        adapter = new UserAdapter(list);
        recyclerViewChats.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void recoverChatsList() {
        currentUserChatsRef = FirebaseConfig.getFirebaseDatabase()
                .child(Constants.ChatsNode.KEY)
                .child(loggedUser.getId());

        eventListener =
                currentUserChatsRef
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            chatsList.clear();
                            for (DataSnapshot chatNode : dataSnapshot.getChildren()) {
                                // if its a group chat, we access info one level bellow (bellow groups key, see firebase)
                                if (chatNode.getKey().equals(Constants.GroupNode.KEY)) {
                                    for (DataSnapshot groupNode : chatNode.getChildren()) {
                                        addChatItemOnList(groupNode);
                                    }
                                } else {
                                    addChatItemOnList(chatNode);
                                }
                            }
                            adapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) { }
                });
    }

    private void addChatItemOnList(DataSnapshot node) {
        Map<String, Object> nodeMap = ChatItemHelper.mapChatFromFirebase(node);
        ChatItem  chatItem = ChatItemHelper.convertMapToChat(nodeMap);
        chatsList.add(chatItem);
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
                                navigateToChatAvtivity(position);
                            }

                            @Override
                            public void onLongItemClick(View view, int position) {
                                deleteChatItem(position);
                            }

                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) { }
                        }
                )
        );
    }

    private void navigateToChatAvtivity (int position) {
        Intent intent = new Intent(getActivity(), ChatActivity.class);

        // Using adapter list so when user filtered list
        // items will have right id and will open correct chat
        ChatItem chat = (ChatItem) adapter.getList().get(position);

        if (chat.isGroup())
            intent.putExtra(Constants.IntentKey.SELECTED_GROUP, chat.getGroup());
        else
            // Sending info from selected user to chat activity (Remember to implement Serializable on User class)
            intent.putExtra(Constants.IntentKey.SELECTED_CONTACT, chat.getSelectedContact());

        startActivity(intent);
    }

    private void deleteChatItem(int position) {
        final ChatItem chat = (ChatItem) adapter.getList().get(position);
        final DatabaseReference currentUserMessagesRef = FirebaseConfig.getFirebaseDatabase()
                .child(Constants.MessagesNode.KEY)
                .child(loggedUser.getId());
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setTitle("Excluir conversa");


        if (chat.isGroup())
            dialog.setMessage("Certeza que deseja excluir este grupo? Não será possível recuperar as informações depois desta ação");
        else
            dialog.setMessage("Certeza que deseja excluir essa conversa? Não será possível recuperar as informações depois desta ação");


        dialog.setPositiveButton("Sim, tenho certeza", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (chat.isGroup())
                    removeGroupMessage(chat, currentUserMessagesRef);
                else
                    removeDirectMessage(chat, currentUserMessagesRef);
            }
        });
        dialog.setNegativeButton("Cancelar", null);
        dialog.create();
        dialog.show();
    }

    private void removeDirectMessage(ChatItem chat, DatabaseReference currentUserMessagesRef) {
        if (ChatItemHelper.removeDirectChatItem(chat)) {
            chatsList.remove(chat);
            adapter.notifyDataSetChanged();
            showLongToast("Conversa removida com sucesso!");
        } else
            showLongToast("Algo deu errado, tente novamente mais tarde");
    }

    private void removeGroupMessage(ChatItem chat, DatabaseReference currentUserMessagesRef) {
        if (GroupHelper.quitGroup(chat.getGroup())
                && ChatItemHelper.removeGroupChatItem(chat)) {
            chatsList.remove(chat);
            adapter.notifyDataSetChanged();
            showLongToast("Grupo removido com sucesso!");
        } else
            showLongToast("Algo deu errado, tente novamente mais tarde");
    }

    private void showLongToast(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
    }
}