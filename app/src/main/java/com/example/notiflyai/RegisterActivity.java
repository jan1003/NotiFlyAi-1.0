package com.example.notiflyai;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.notifly.databinding.ActivityRegisterBinding;
import com.example.notiflyai.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.*;
import com.google.firebase.firestore.*;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        binding.btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String name = binding.etName.getText().toString().trim();
                final String email = binding.etEmail.getText().toString().trim();
                final String pass = binding.etPassword.getText().toString().trim();
                final String priorityStr = binding.etPriority.getText().toString().trim();

                if(name.isEmpty() || email.isEmpty() || pass.isEmpty() || priorityStr.isEmpty()) {
                    toast("Заполните все поля");
                    return;
                }

                int priority = 3; // default
                try {
                    priority = Integer.parseInt(priorityStr);
                } catch (NumberFormatException e) {
                    toast("Некорректный приоритет, используем 3");
                }

                mAuth.createUserWithEmailAndPassword(email, pass)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()) {
                                    // Успех - сохраним в Firestore
                                    FirebaseUser fUser = mAuth.getCurrentUser();
                                    if(fUser != null) {
                                        String uid = fUser.getUid();
                                        User newUser = new User(uid, name, email, priority);
                                        db.collection("users").document(uid)
                                                .set(newUser)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task2) {
                                                        if(task2.isSuccessful()) {
                                                            toast("Регистрация завершена!");
                                                            goToUsersActivity();
                                                            finish();
                                                        } else {
                                                            toast("Ошибка сохранения профиля: " + task2.getException());
                                                        }
                                                    }
                                                });
                                    }
                                } else {
                                    toast("Ошибка регистрации: " + task.getException());
                                }
                            }
                        });
            }
        });

        binding.tvGoToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish(); // вернуться на LoginActivity
            }
        });
    }

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void goToUsersActivity() {
        startActivity(new Intent(this, UsersActivity.class));
    }
}