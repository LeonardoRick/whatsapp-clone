package com.example.whatsapp_clone.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.whatsapp_clone.R;
import com.example.whatsapp_clone.helper.FirebaseConfig;
import com.example.whatsapp_clone.model.user.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class LoginActivity extends AppCompatActivity {
    FirebaseAuth auth = FirebaseConfig.getAuth();

    TextInputEditText loginInputEmail, loginInputPassword;
    Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (auth.getCurrentUser() != null) startMainActivity(); //check if user is already logged in

        loginInputEmail = findViewById(R.id.loginInputEmail);
        loginInputPassword = findViewById(R.id.loginInputPassword);
        loginButton = findViewById(R.id.loginButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = loginInputEmail.getText().toString();
                String password = loginInputPassword.getText().toString();
                if (email.isEmpty() || password.isEmpty()) {
                    showLongToast("Preencha todos os campos");
                } else {
                    login(new User(email, password));
                }
            }
        });
    }

    public void startRegister(View view) {
        startActivity(new Intent(this, RegisterActivity.class));
    }

    public void login(User user) {
        auth.signInWithEmailAndPassword(
                user.getEmail(),
                user.getPassword()
        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    showLongToast("Usuário logado");
                    startMainActivity();
                } else {
                    try {
                        throw(task.getException());
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        showLongToast("Email ou senha inválidos");
                    } catch(FirebaseAuthInvalidUserException e) {
                        showLongToast("Usuário não cadastrado");
                    } catch (Exception e) {
                        showLongToast("Algo deu errado:  " + e.getMessage());
                    }
                }
            }
        });
    }

    public void showLongToast(String m) {
        Toast.makeText(getApplicationContext(), m, Toast.LENGTH_LONG).show();
    }

    public void startMainActivity() {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish(); // so user can't go back to this activity
    }
}