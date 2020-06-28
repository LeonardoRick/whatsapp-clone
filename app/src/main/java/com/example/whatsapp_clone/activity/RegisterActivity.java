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
import com.example.whatsapp_clone.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

public class RegisterActivity extends AppCompatActivity {
    FirebaseAuth auth = FirebaseConfig.getAuth();

    TextInputEditText registerInputName, registerInputEmail, registerInputPassword;
    Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        registerInputName = findViewById(R.id.registerInputName);
        registerInputEmail = findViewById(R.id.registerInputEmail);
        registerInputPassword = findViewById(R.id.registerInputPassword);
        registerButton = findViewById(R.id.registerButton);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = registerInputName.getText().toString();
                String email = registerInputEmail.getText().toString();
                String password = registerInputPassword.getText().toString();
                if(name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                    showLongToast("Preencha todos os campos");
                } else {
                    register(new User(name, email, password));
                }
            }
        });
    }

    public void register(final User user) {
        auth.createUserWithEmailAndPassword(
                user.getEmail(),
                user.getPassword()
        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    // set user id and save it on database
                    user.setId(auth.getCurrentUser().getUid());
                    user.saveOnDatabase();

                    //starts mainActivity
                    showLongToast("Usuário criado com sucesso");
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish(); // so user can't go back to this activity
                } else {
                    try {
                        throw (task.getException());
                    } catch (FirebaseAuthInvalidUserException e) {
                        showLongToast("Email inválido");
                    } catch (FirebaseAuthWeakPasswordException e) {
                        showLongToast("Senha fraca, insira uma ");
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        showLongToast("Usuário ou senha incorretos");
                    } catch (Exception e) {
                        showLongToast("Erro ao cadastrar o usuário: " + e.getMessage());
                    }
                }
            }
        });
    }

    public void showLongToast(String m) {
        Toast.makeText(getApplicationContext(), m, Toast.LENGTH_LONG).show();
    }
}