package com.example.whatsapp_clone.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.example.whatsapp_clone.adapter.MessageAdapter;
import com.example.whatsapp_clone.helper.Constants;
import com.example.whatsapp_clone.helper.FirebaseConfig;
import com.example.whatsapp_clone.helper.GenericHelper;
import com.example.whatsapp_clone.model.message.Message;
import com.example.whatsapp_clone.model.message.MessageHelper;
import com.example.whatsapp_clone.model.user.User;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.whatsapp_clone.R;
import com.example.whatsapp_clone.model.user.UserHelper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.UUID;

public class ChatActivity extends AppCompatActivity {

    private CircleImageView contactProfilePicture;
    private EditText messageToSend;
    private TextView contactName;

    private ArrayList<Message> messagesList = new ArrayList<>();
    private MessageAdapter adapter;

    private RecyclerView recyclerViewMessages;
    private DatabaseReference messagesRef;
    private ChildEventListener messageEventListener;
    private User loggedUser;
    private User selectedContact; // contact user is talking with

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // show back button

        contactName = findViewById(R.id.textViewNameChat);
        contactProfilePicture = findViewById(R.id.circleImageViewChat);
        messageToSend = findViewById(R.id.messageToSend);

        loggedUser = UserHelper.getLogged();

        recoverSelectedContactInfo();
        setMessagesRecyclerView();
        chatHistoryListener();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        messagesRef.removeEventListener(messageEventListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    /**
     * Recover all past messagens between selectedContact and logged user
     */
    private void chatHistoryListener() {
        messagesRef = FirebaseConfig.getFirebaseDatabase()
                .child(Constants.MessagesNode.KEY)
                .child(loggedUser.getId())
                .child(selectedContact.getId());

        messageEventListener =
                messagesRef
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        messagesList.add(dataSnapshot.getValue(Message.class));
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) { }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) { }
                });
    }

    /**
     * Send single message to database when clicking on send button
     * @param view
     */
    public void sendMessage(View view) {
        String textMsg = messageToSend.getText().toString();

        MessageHelper.saveMessageOnDatabase(textMsg, loggedUser, selectedContact, false); // saving sender msg
        messageToSend.setText("");
    }


    /**
     * Send single image to database when selecting gallery or taking picture
     * @param image from gallery to send on chat
     */
    private void sendImage(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();           //object that allows convertion
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);      // compress image
        byte[] imgData = baos.toByteArray();                              // convert BAOS to pixels (literally byte array/matrix)


        String imageName = UUID.randomUUID().toString();
        StorageReference imageStorageRef = FirebaseConfig.getFirebaseStorage()
                .child(Constants.Storage.IMAGES)
                .child(Constants.Storage.CHAT)
                .child(loggedUser.getId())
                .child(imageName + Constants.Storage.JPEG);               // Save image with user id name


        UploadTask uploadTask  = imageStorageRef.putBytes(imgData);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showLongToast("Erro ao fazer upload " + e.getMessage());
            }
        });

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        MessageHelper.saveMessageOnDatabase(uri.toString(), loggedUser, selectedContact, true);
                    }
                });
            }
        });
    }


    /**
     * Recover info from selected user on ContactsListFragment list to fill toolbar info
     */
    private void recoverSelectedContactInfo() {
        // Recover info from selected user
        try {
            selectedContact = (User) getIntent().getExtras().getSerializable(Constants.IntentKey.SELECTED_CONTACT);
        } catch (Exception e) {
            Log.e("TAG", "recoverSelectedContactInfo: " + e.getMessage());
        }

        String stringPicture = selectedContact.getStringPicture();

        if (stringPicture != null && !stringPicture.isEmpty()) {
            Uri picture = Uri.parse(selectedContact.getStringPicture()); // recovering Uri from string because Uri can't be serialized
            selectedContact.setPicture(picture);
            Picasso.get()
                    .load(picture)
                    .error(R.drawable.profile)
                    .into(contactProfilePicture);
        }

        // Set contact name and image on toolbar
        contactName.setText(selectedContact.getName());
    };

    /**
     * set recyclerView properties to show messages on list
     */
    private void setMessagesRecyclerView() {
        recyclerViewMessages = findViewById(R.id.recyclerViewMessages);
        recyclerViewMessages.setHasFixedSize(true);

        // Layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true); // to start at the end of the conversation

        recyclerViewMessages.setLayoutManager(layoutManager);

        // Specifiy adapter
        adapter = new MessageAdapter(messagesList);
        recyclerViewMessages.setAdapter(adapter);
    }

    /**
     * method called to request user permission to take picture or access library
     */
    public void validatePermission(View view) {
        switch (view.getId()) {
            case R.id.imageViewAddPictureChat:
                ActivityCompat.requestPermissions(
                        this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, Constants.FeatureRequest.STORAGE);
                break;
            case R.id.imageViewTakePictureChat:
                ActivityCompat.requestPermissions(
                        this, new String[] {Manifest.permission.CAMERA}, Constants.FeatureRequest.CAMERA);
                break;
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // if user accepted
        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startIntentUploadPicture(requestCode);
            // if user denied first time
        } else if (shouldShowRequestPermissionRationale(permissions[0])) {
            alerUserPermissionNeeded(requestCode);
        } else {
            // if user denied more than one time (returns false to last method, so drops on this case
            showDefaultSettingsPermitionRequired();
        }
    }

    /**
     * Method called when user grant permission and we want to access a feature of Android SmartPhone
     * @param requestCode
     */
    public void startIntentUploadPicture(int requestCode) {
        Intent intent = null;

        switch (requestCode) {
            case Constants.FeatureRequest.STORAGE:

                intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                break;
            case Constants.FeatureRequest.CAMERA:
                intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                break;
        }
        try {
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(intent, requestCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * User denied permission for the first time
     */

    public void alerUserPermissionNeeded(int requestCode) {
        String msg = "";
        switch (requestCode) {
            case Constants.FeatureRequest.STORAGE:
                msg = "Você irá precisar conceder permissão à galeria se quiser escolher uma foto da galeria";
                break;
            case Constants.FeatureRequest.CAMERA:
                msg = "Você irá precisar conceder permissão à câmera se quiser tirar uma foto usando a câmera";
                break;
            default:
                msg = "Você precisa conceder permissão para utilizar este recurso do celular";
        }

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Permissão necessária");
        dialog.setMessage(msg);
        dialog.setPositiveButton("Entendi", null);
        dialog.create();
        dialog.show();
    }

    /**
     * User denied permission second time
     */
    public void showDefaultSettingsPermitionRequired() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Mudar a permissão em configurações");
        dialog.setMessage("Clique em Configurações para manualmente permitir o aplicativo acessar o recurso");
        dialog.setPositiveButton("", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                openPhoneSettings();
            }
        });
        dialog.setNegativeButton("Continuar sem permissões", null);

        dialog.create();
        dialog.show();
    }

    /**
     * Open native smartphone Settings
     */
    public void openPhoneSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        // current activity will be notified when config is finished (back button is pressed)
        startActivityForResult(intent, Constants.FeatureRequest.SETTINGS);
    }

    /**
     * Method that returns from activity any ActivityForResult call with some info selected
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Bitmap image = null;
            try {
                switch (requestCode) {
                    case Constants.FeatureRequest.STORAGE:
                        Uri selectedImageUri = data.getData();

                        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                            ImageDecoder.Source source = ImageDecoder.createSource(getContentResolver(), selectedImageUri);
                            image = ImageDecoder.decodeBitmap(source);
                        } else {
                            image = GenericHelper.getDeprecatedBitmap(this, selectedImageUri);
                        }
                        break;
                    case Constants.FeatureRequest.CAMERA:
                        image = (Bitmap) data.getExtras().get("data");
                        break;
                }

                if (image != null) {
                    sendImage(image);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void showLongToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
    }
}