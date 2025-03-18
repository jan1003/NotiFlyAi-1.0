package com.example.notifly;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.notifly.databinding.ActivityRegisterBinding;
import com.example.notifly.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.*;
import com.google.firebase.firestore.*;

public class RegisterActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ActivityRegisterBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance(); // Инициализация Firestore

        binding.btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }

    private void registerUser() {
        String email = binding.etName.getText().toString().trim();
        String pass = binding.etEmail.getText().toString().trim();

        if (email.isEmpty() || pass.isEmpty()) {
            toast("Введите корректные данные для регистрации");
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser fUser = mAuth.getCurrentUser();
                            if (fUser != null) {
                                String uid = fUser.getUid();
                                User newUser = new User(uid, "Имя", email, 3);

                                db.collection("users")
                                        .document(uid)
                                        .set(newUser)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task2) {
                                                if (task2.isSuccessful()) {
                                                    toast("Регистрация завершена!");
                                                    goToUsersActivity();
                                                    finish();
                                                } else {
                                                    toast("Ошибка сохранения профиля: " + task2.getException().getMessage());
                                                }
                                            }
                                        });
                            }
                        } else {
                            toast("Ошибка регистрации: " + task.getException().getMessage());
                        }
                    }
                });
    }

    private void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void goToUsersActivity() {
        Intent intent = new Intent(this, UsersActivity.class);
        startActivity(intent);
    }
}