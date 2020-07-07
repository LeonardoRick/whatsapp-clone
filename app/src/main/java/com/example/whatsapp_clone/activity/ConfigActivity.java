package com.example.whatsapp_clone.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
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

import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.whatsapp_clone.R;
import com.example.whatsapp_clone.helper.Constants;
import com.example.whatsapp_clone.helper.FirebaseConfig;
import com.example.whatsapp_clone.helper.GenericHelper;
import com.example.whatsapp_clone.model.user.User;
import com.example.whatsapp_clone.model.user.UserHelper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;


public class ConfigActivity extends AppCompatActivity {

    private CircleImageView profileImage;
    private EditText userName;

    private User loggedUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);

        profileImage = findViewById(R.id.circleImageViewProfile);
        userName = findViewById(R.id.editTextTextProfileName);

        recoverUserInfo();

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Configurações");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Show back button

    }

    /**
     * recover user logged info
     */
    public void recoverUserInfo() {
        loggedUser = UserHelper.getLogged();
        if (loggedUser != null) {
            Picasso.get().load(loggedUser.getPicture())   // set user profile image
                    .placeholder(R.drawable.profile)
                    .error(R.drawable.profile)
                    .into(profileImage);
            userName.setText(loggedUser.getName()); // set user name
        }
    }

    /**
     * Method called when user clicks on pen button to update his name
     */
    public void updateUserName(View view) {
        String updatedName = userName.getText().toString();
        if(UserHelper.updateNameOnProfile(updatedName)) { //update userName on profile
            loggedUser.setName(updatedName);
            UserHelper.updateOnDatabase(loggedUser); // update all old info form user on database
            showLongToast("Nome atualizado com sucesso");
        } else {
            showLongToast("Erro ao atualizar o nome do usuário, tente novamente mais tarde");
        }
    }

    /**
     * method called to request user permission to take picture or access library
     */
    public void validatePermission(View view) {
        switch (view.getId()) {
            case R.id.buttonAddPicture:
                ActivityCompat.requestPermissions(
                        this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, Constants.FeatureRequest.STORAGE);
                break;
            case R.id.buttonTakePicture:
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
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startIntentChangeProfilePicture(requestCode);
            // if used denied first time
        } else if (shouldShowRequestPermissionRationale(permissions[0])) {
            alertUserPermissionNeeded(requestCode);
            // if user denied more than one time (returns false to last method, so drops on this case)
        } else {
            showDefaultSettingsPermitionRequired();
        }
    }

    /**
     * Method called when user grant permission and we want to access a feature of Android SmartPhone
     * @param requestCode
     */
    public void startIntentChangeProfilePicture(int requestCode) {
        Intent intent = null;

        switch (requestCode) {
            case Constants.FeatureRequest.STORAGE:
                intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
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
     * User denied permission first time
     */
    public void alertUserPermissionNeeded(int requestCode) {
        String msg = "";
        switch (requestCode) {
            case Constants.FeatureRequest.STORAGE:
                msg = "Você irá precisar conceder permissão à galeria se quiser escolher uma foto de perfil";
                break;
            case Constants.FeatureRequest.CAMERA:
                msg = "Você irá precisar conceder permissão à câmera se quiser tirar uma foto de perfil";
                break;
            default:
                msg= "Você precisa conceder permissão para utilizar este recurso do celular";
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
        dialog.setMessage("Clique em Configurações para manualmente permitir o aplicativo acessar o recurso")
                .setCancelable(false)
                .setPositiveButton("Configurações", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        openPhoneSettings();
                    }
                })
                .setNegativeButton("Continuar sem permissões", null);

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
     * Method that returns from any ActivityForResult with some info selected by user
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
                    profileImage.setImageBitmap(image);
                    // save image on firebase Storage
                    uploadImageToStorage(image);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Method that upload selected image to Firebase Storage
     * @param image selected from user to be his profile picture
     */
    public void uploadImageToStorage(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();         // object that allows convertion to byte array
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);    // compress img
        byte[] imgData = baos.toByteArray();                            // convert BAOS to pixels (literally byte array/matrix)

        StorageReference imageStorageRef = FirebaseConfig.getFirebaseStorage()
                .child(Constants.Storage.IMAGES)
                .child(Constants.Storage.PROFILE)
                .child(FirebaseConfig.getAuth().getUid() + Constants.Storage.JPEG); // save image with user id name

        UploadTask uploadTask = imageStorageRef.putBytes(imgData);             // upload image and return task to control success
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showLongToast("Erro ao fazer upload da imagem :(. Tente novamente mais tarde");
            }
        });

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                 taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        loggedUser.setPicture(uri);
                        if(UserHelper.updateImageOnProfile(uri)
                                && UserHelper.updateOnDatabase(loggedUser)) {
                            showLongToast("Sucesso ao atualizar a imagem!");
                        } else {
                            showLongToast("Erro ao fazer upload da imagem :(. Tente novamente mais tarde");

                        }

                    }
                });
            }

        });
    }

    private void showLongToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }
}