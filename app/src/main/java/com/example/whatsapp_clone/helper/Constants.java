package com.example.whatsapp_clone.helper;

import android.net.Uri;
import com.example.whatsapp_clone.model.user.User;
import java.util.ArrayList;

public class Constants {

    public static final String ID = "id";

    public static class UsersNode {
        public static final String KEY = "users";
        public static final String NAME = "name";
        public static final String EMAIL = "email";
        public static final String PICTURE = "picture";
    }

    public static class MessagesNode {
        public static final String KEY = "messages";
    }

    public static class ChatsNode {
        public static final String KEY = "chats";
        public static final String LAST_MESSAGE = "lastMessage";
        public static final String SELECTED_CONTACT = "selectedContact";

        public static final String IS_GROUP = "isGroup";
        public static final String GROUP = "group";
    }

    public static class GroupNode {
        public static final String KEY = "groups";
        public static final String NAME = "name";
        public static final String PICTURE = "picture";
        public static final String MEMBERS = "members";
        public static final String CREATOR = "creator";
    }

    public static class FeatureRequest {
        public static final int STORAGE = 100;
        public static final int CAMERA = 200;
        public static final int SETTINGS = 300;
    }

    public static class Storage {
        public static final String IMAGES = "images";
        public static final String PROFILE = "profile";
        public static final String CHAT = "chat";
        public static final String GROUPS = "groups";
        public static  final String JPEG = ".jpeg";
    }

    public static class IntentKey {
        public static final String SELECTED_CONTACT = "SELECTED_CONTACT";
        public static final String CONTACTS_LIST = "CONTACTS_LIST";
        public static final String SELECTED_GROUP = "SELECTED_GROUP";
    }

    public static class GroupListItem {
        public static final String NAME = "Novo grupo";
        public static final String ID = "groupItem";
    }

}
