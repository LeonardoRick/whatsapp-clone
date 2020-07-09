package com.example.whatsapp_clone.helper;

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
        public static  final String JPEG = ".jpeg";
    }

    public static class IntentKey {
        public static final String SELECTED_CONTACT = "SELECTED_CONTACT";
        public static final String CONTACTS_LIST = "CONTACTS_LIST";
    }

    public static class GroupItem {
        public static final String NAME = "Novo grupo";
        public static final String ID = "groupItem";
    }

}
