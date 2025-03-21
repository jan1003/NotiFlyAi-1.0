package com.example.notifly;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.notifly.adapters.UsersAdapter;
import com.example.notifly.databinding.ActivityUsersBinding;
import com.example.notifly.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.*;
import com.google.firebase.firestore.*;

import java.util.ArrayList;
import java.util.List;

public class UsersActivity extends AppCompatActivity {

    private ActivityUsersBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private UsersAdapter usersAdapter;
    private List<User> userList = new ArrayList<>();

    private String myUid = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUsersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Если пользователь не авторизован — сразу на экран логина
        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }
        myUid = mAuth.getCurrentUser().getUid();

        // Кнопка "Настройки"
        binding.btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(UsersActivity.this, SettingsActivity.class));
            }
        });

        // Кнопка "Начать чат"
        binding.btnStartChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showNewChatDialog();
            }
        });

        setupUsersAdapter();
        loadUsers();
    }

    /**
     * Настраиваем адаптер для списка пользователей
     */
    private void setupUsersAdapter() {
        usersAdapter = new UsersAdapter(userList, new UsersAdapter.OnUserClickListener() {
            @Override
            public void onUserClick(User user) {
                openChatWithUser(user);
            }
        });
        binding.rvUsers.setLayoutManager(new LinearLayoutManager(this));
        binding.rvUsers.setAdapter(usersAdapter);
    }

    /**
     * Загружаем всех пользователей из Firestore, кроме себя
     */
    private void loadUsers() {
        db.collection("users").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            userList.clear();
                            for(DocumentSnapshot doc : task.getResult()) {
                                User user = doc.toObject(User.class);
                                if(user != null && !user.getUserId().equals(myUid)) {
                                    userList.add(user);
                                }
                            }
                            usersAdapter.notifyDataSetChanged();
                        } else {
                            toast("Ошибка загрузки: " + task.getException().getMessage());
                        }
                    }
                });
    }

    /**
     * Показываем диалог: пользователь вводит email,
     * по которому будет найден собеседник в Firestore
     */
    private void showNewChatDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Введите email пользователя");

        final EditText input = new EditText(this);
        input.setHint("example@mail.com");
        input.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        builder.setView(input);

        // Кнопка "Начать чат" в диалоге
        builder.setPositiveButton("Начать чат", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String enteredEmail = input.getText().toString().trim();
                if (!enteredEmail.isEmpty()) {
                    findUserByEmail(enteredEmail);
                } else {
                    toast("Email не может быть пустым");
                }
            }
        });

        // Кнопка "Отмена"
        builder.setNegativeButton("Отмена", null);

        builder.show();
    }

    /**
     * Ищем пользователя в Firestore по email
     */
    private void findUserByEmail(String email) {
        db.collection("users")
                .whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot snapshot = task.getResult();
                            if (snapshot != null && !snapshot.isEmpty()) {
                                // Берём первого найденного пользователя
                                DocumentSnapshot doc = snapshot.getDocuments().get(0);
                                User user = doc.toObject(User.class);

                                if(user != null) {
                                    openChatWithUser(user);
                                } else {
                                    toast("Пользователь не найден");
                                }
                            } else {
                                toast("Пользователь с таким email не найден");
                            }
                        } else {
                            toast("Ошибка поиска: " + task.getException().getMessage());
                        }
                    }
                });
    }

    /**
     * Открываем ChatActivity с указанным пользователем
     */
    private void openChatWithUser(User user) {
        Intent i = new Intent(UsersActivity.this, ChatActivity.class);
        i.putExtra("receiverId", user.getUserId());
        i.putExtra("receiverName", user.getName());
        i.putExtra("receiverPriority", user.getPriority());
        startActivity(i);
    }

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}