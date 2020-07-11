package com.example.whatsapp_clone.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

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
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.whatsapp_clone.R;
import com.example.whatsapp_clone.adapter.GroupMemberAdapter;
import com.example.whatsapp_clone.helper.Constants;
import com.example.whatsapp_clone.helper.FirebaseConfig;
import com.example.whatsapp_clone.helper.GenericHelper;
import com.example.whatsapp_clone.model.group.Group;
import com.example.whatsapp_clone.model.group.GroupHelper;
import com.example.whatsapp_clone.model.user.User;
import com.example.whatsapp_clone.model.user.UserHelper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;

public class GroupRegisterActivity extends AppCompatActivity {

    private RecyclerView recyclerViewGroupMembers;
    private GroupMemberAdapter groupMemberAdapter;
    private ArrayList<User> groupMembersList;
    private User loggedUser;

    private TextView textViewParticipants;
    private CircleImageView circleImageViewGroup;
    private  EditText editTextGroupName;
    private Toolbar toolbar;
    private Group group;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_register);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Novo grupo");
        toolbar.setSubtitle("Defina um nome");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // enable return button

        loggedUser = UserHelper.getLogged();
        group = new Group(loggedUser); // created here because we need to invocate constructor of group to have it's id set;
        textViewParticipants = findViewById(R.id.textViewParticipants);
        circleImageViewGroup = findViewById(R.id.circleImageViewGroup);
        editTextGroupName = findViewById(R.id.editTextGroupName);
    }

    @Override
    protected void onStart() {
        super.onStart();
        recoverGroupMembersList();

        setGroupMembersRecyclerView();
        textViewParticipants.setText("Participantes " + groupMembersList.size());
    }

    /**
     *  recover groupMemberList passed from GroupActivity
     */
    private void recoverGroupMembersList() {
        try {
            groupMembersList = (ArrayList<User>) getIntent().getExtras().getSerializable(Constants.IntentKey.CONTACTS_LIST);

            // creating Uri for each contact picture since
            // Uri is not serializable and can't be recovered from ContactsListFragment
            for (User groupMember : groupMembersList) {
                String stringPicture = groupMember.getStringPicture();
                if ( stringPicture != null && !stringPicture.isEmpty()) {
                    groupMember.setPicture(Uri.parse(stringPicture));
                }
            }
        } catch (Exception e) {
            Log.e("TAG", "groupMemberslist: " +  e.getMessage());
        }

    }

    private void setGroupMembersRecyclerView() {
        recyclerViewGroupMembers = findViewById(R.id.recyclerViewGroupMembers2);
        recyclerViewGroupMembers.setHasFixedSize(true);

        // Layout Manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(
                this,
                LinearLayoutManager.HORIZONTAL,
                false
        );
        recyclerViewGroupMembers.setLayoutManager(layoutManager);

        // Adapter
        groupMemberAdapter = new GroupMemberAdapter(groupMembersList);
        recyclerViewGroupMembers.setAdapter(groupMemberAdapter);
    }

    /**
     * Method called when FAB is clicked and group is created
     */
    public void saveGroup(View view) {
        // Set group name


        if (!groupNameisEmpty()) {
            group.setName(editTextGroupName.getText().toString());
            // Set group members with loggedUser
            group.setMembers(groupMembersList);
            group.addGroupMember(loggedUser);

            // Save group on database
            GroupHelper.saveOnDatabase(group);
        } else {
            Toast.makeText(this, "O grupo precisa ter um nome definido", Toast.LENGTH_LONG).show();
        }
    }

    private boolean groupNameisEmpty () {
        if (editTextGroupName.getText() == null ||
                editTextGroupName.getText().toString().trim().isEmpty() ||
                editTextGroupName.getText().toString() == null)
                    return true;
        return false;
    }

    /**
     * Method called to request user permission to access library
     */
    public void validatePermisson(View view) {
        ActivityCompat.requestPermissions(
                this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                Constants.FeatureRequest.STORAGE
        );
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // if user accepted
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startIntentAddGroupImage(requestCode);
            // if user denied first time
        } else if (shouldShowRequestPermissionRationale(permissions[0])) {
            alertUserPermissionNeeded();
            // if user denied more than one time (returns false to last method, so drops on this case)
        } else {
            showDefaultSettingsPermissionRequired();
        }
    }

    /**
     * Method called when user grant permission and we want to access gallery
     * param requestCode
     */
    private void startIntentAddGroupImage(int requestCode) {
        try {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(intent, requestCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *  User denied permission first time
     */
    private void alertUserPermissionNeeded() {
        String msg = "Você irá pecisar conceder permissão à galeria se quiser escolher uma foto para o grupo";

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle("Permissão Necessária");
        dialogBuilder.setMessage(msg);
        dialogBuilder.setPositiveButton("Entendi", null);

        dialogBuilder.create();
        dialogBuilder.show();
    }

    /**
     *  User denied permission second time
     */
    private void showDefaultSettingsPermissionRequired() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);

        dialogBuilder.setTitle("Mudar a permissão em configuraçẽos");
        dialogBuilder.setMessage("Clique em configurações para manualmente permitir o aplicativo acessar o recurso");
        dialogBuilder.setPositiveButton("Configurações", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                openPhoneSettings();
            }
        });
        dialogBuilder.setNegativeButton("Continuar sem permissões", null);
    }

    /**
     * Open native smartphone settings
     */
    private void openPhoneSettings() {
        Intent intent  = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);

        startActivityForResult(intent, Constants.FeatureRequest.SETTINGS);
    }

    /**
     * Method that returns to activity from any ActivityForResult with some info selected by user
     */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Bitmap image;
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

                        if (image != null) {
                            circleImageViewGroup.setImageBitmap(image);
                            uploadGroupImageToStorage(image);
                        }
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Method taht upload selected image to Firebase Storage
     * @param image Bitmap of image selected to be group picture
     */
    private void uploadGroupImageToStorage(Bitmap image) {

        byte[] imgData = GenericHelper.bitmapToByteArray(image);

        StorageReference imageStorageRef = FirebaseConfig.getFirebaseStorage()
                .child(Constants.Storage.IMAGES)
                .child(Constants.Storage.GROUPS)
                .child(group.getId() + Constants.Storage.JPEG);

        UploadTask uploadTask = imageStorageRef.putBytes(imgData);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Erro ao fazer upload" + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        group.setPicture(uri);
                    }
                });

            }
        });
    }
}