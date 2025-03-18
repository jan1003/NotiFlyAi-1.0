package com.example.notifly;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.SharedPreferences;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
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
    private boolean isBusyMode = false;  // локально

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUsersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        if(mAuth.getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        myUid = mAuth.getCurrentUser().getUid();

        // Считываем BusyMode из SharedPreferences (чтобы сохранилось)
        SharedPreferences prefs = getSharedPreferences("NotiFlyPrefs", MODE_PRIVATE);
        isBusyMode = prefs.getBoolean("IS_BUSY_MODE", false);
        binding.switchBusyMode.setChecked(isBusyMode);

        binding.switchBusyMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton sw, boolean checked) {
                isBusyMode = checked;
                SharedPreferences.Editor ed = getSharedPreferences("NotiFlyPrefs", MODE_PRIVATE).edit();
                ed.putBoolean("IS_BUSY_MODE", isBusyMode);
                ed.apply();
                toast("Busy Mode: " + (isBusyMode ? "ON" : "OFF"));
            }
        });

        setupUsersAdapter();
        loadUsers();

        binding.btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                startActivity(new Intent(UsersActivity.this, LoginActivity.class));
                finish();
            }
        });
    }

    private void setupUsersAdapter() {
        usersAdapter = new UsersAdapter(userList, new UsersAdapter.OnUserClickListener() {
            @Override
            public void onUserClick(User user) {
                Intent i = new Intent(UsersActivity.this, ChatActivity.class);
                i.putExtra("receiverId", user.getUserId());
                i.putExtra("receiverName", user.getName());
                i.putExtra("receiverPriority", user.getPriority());
                startActivity(i);
            }
        });
        binding.rvUsers.setLayoutManager(new LinearLayoutManager(this));
        binding.rvUsers.setAdapter(usersAdapter);
    }

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
                            toast("Ошибка загрузки списка: " + task.getException());
                        }
                    }
                });
    }

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}