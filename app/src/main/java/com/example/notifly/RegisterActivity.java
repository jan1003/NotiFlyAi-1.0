package com.example.notifly;


import android.util.Log;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
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
        db = FirebaseFirestore.getInstance();

        binding.btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        binding.tvGoToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
            }
        });
    }

    private void registerUser() {
        String email = binding.etEmail.getText().toString().trim();
        String pass = binding.etPassword.getText().toString().trim();

        if (!isValidEmail(email)) {
            binding.etEmail.setError("Введите корректный email");
            return;
        }
        if (pass.length() < 6) {
            binding.etPassword.setError("Пароль должен содержать минимум 6 символов");
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser fUser = mAuth.getCurrentUser();
                            if (fUser != null) {
                                // Отправляем письмо с подтверждением
                                fUser.sendEmailVerification()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> emailTask) {
                                                if (emailTask.isSuccessful()) {
                                                    // Сохраняем пользователя в Firestore
                                                    String uid = fUser.getUid();
                                                    // Здесь "Имя" — это просто пример.
                                                    // Если хотите, чтобы пользователь вводил имя, добавьте поле EditText для имени.
                                                    User newUser = new User(uid, "Имя", email, 3);

                                                    db.collection("users")
                                                            .document(uid)
                                                            .set(newUser)
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task2) {
                                                                    if (task2.isSuccessful()) {
                                                                        Log.d("DEBUG", "Документ для пользователя " + uid + " успешно добавлен в Firestore.");
                                                                        // ...
                                                                    } else {
                                                                        Log.e("DEBUG", "Ошибка сохранения профиля: " + task2.getException().getMessage());
                                                                        // ...
                                                                    }
                                                                }
                                                            });
                                                } else {
                                                    toast("Не удалось отправить письмо с подтверждением: " + emailTask.getException().getMessage());
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

    private boolean isValidEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}