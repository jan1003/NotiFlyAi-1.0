package com.example.notifly;

import com.example.notifly.models.Contact;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.notifly.adapters.UsersAdapter;
import com.example.notifly.databinding.ActivityUsersBinding;
import com.example.notifly.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UsersActivity extends AppCompatActivity {

    private ActivityUsersBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private UsersAdapter usersAdapter;
    private List<User> userList = new ArrayList<>();
    private Map<String, Integer> priorityMap = new HashMap<>();

    private String myUid = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUsersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        myUid = mAuth.getCurrentUser().getUid();

        binding.btnSettings.setOnClickListener(v -> startActivity(new Intent(UsersActivity.this, SettingsActivity.class)));
        binding.btnStartChat.setOnClickListener(v -> showNewChatDialog());

        setupUsersAdapter();
        loadPrioritiesThenUsers();
    }

    private void setupUsersAdapter() {
        usersAdapter = new UsersAdapter(userList, priorityMap, user -> openChatWithUser(user));
        binding.rvUsers.setLayoutManager(new LinearLayoutManager(this));
        binding.rvUsers.setAdapter(usersAdapter);
    }

    private void loadPrioritiesThenUsers() {
        db.collection("contacts").document(myUid).collection("list").get()
                .addOnSuccessListener(snapshot -> {
                    priorityMap.clear();
                    for (DocumentSnapshot doc : snapshot.getDocuments()) {
                        Integer p = doc.getLong("priority") != null ? doc.getLong("priority").intValue() : 3;
                        priorityMap.put(doc.getId(), p);
                    }
                    loadUsers();
                });
    }


        private void loadUsers() {
            db.collection("users").get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            userList.clear();
                            for (DocumentSnapshot doc : task.getResult()) {
                                User user = doc.toObject(User.class);
                                if (user != null && !user.getUserId().equals(myUid)) {
                                    final User currentUser = user;
                                    db.collection("contacts")
                                            .document(myUid)
                                            .collection("list")
                                            .document(currentUser.getUserId())
                                            .get()
                                            .addOnSuccessListener(priorityDoc -> {
                                                int priority = 3;
                                                if (priorityDoc.exists()) {
                                                    Long pr = priorityDoc.getLong("priority");
                                                    if (pr != null) priority = pr.intValue();
                                                }
                                                currentUser.setPriority(priority);
                                                userList.add(currentUser);
                                                usersAdapter.notifyDataSetChanged();
                                            });
                                }
                            }
                        } else {
                            toast("Ошибка загрузки: " + task.getException().getMessage());
                        }
                    });
        }

    private void showNewChatDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Введите email пользователя");

        final EditText input = new EditText(this);
        input.setHint("example@mail.com");
        input.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        builder.setView(input);

        builder.setPositiveButton("Начать чат", (dialog, which) -> {
            String enteredEmail = input.getText().toString().trim();
            if (!enteredEmail.isEmpty()) {
                findUserByEmail(enteredEmail);
            } else {
                toast("Email не может быть пустым");
            }
        });

        builder.setNegativeButton("Отмена", null);
        builder.show();
    }

    private void findUserByEmail(String email) {
        db.collection("users")
                .whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<DocumentSnapshot> docs = task.getResult().getDocuments();
                        if (!docs.isEmpty()) {
                            User user = docs.get(0).toObject(User.class);
                            if (user != null) openChatWithUser(user);
                        } else {
                            toast("Пользователь не найден");
                        }
                    } else {
                        toast("Ошибка поиска: " + task.getException().getMessage());
                    }
                });
    }

    private void openChatWithUser(User user) {
        // Սկզբում ստուգում ենք՝ արդյոք կա պահված priotity տվյալ
        db.collection("contacts")
                .document(myUid)
                .collection("list")
                .document(user.getUserId())
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists() && doc.contains("priority")) {
                        int savedPriority = doc.getLong("priority").intValue();
                        // եթե արդեն պահված է՝ միանգամից բացել չաթը
                        goToChat(user, savedPriority);
                    } else {
                        // եթե պահված չի՝ հարցնել priotity
                        askPriorityAndStartChat(user);
                    }
                })
                .addOnFailureListener(e -> {
                    toast("Սխալ տվյալ ստանալու ժամանակ: " + e.getMessage());
                });
    }

    private void askPriorityAndStartChat(User user) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Ընտրեք priotity " + user.getName() + " համար");

        final EditText input = new EditText(this);
        input.setHint("1 - 5");
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);

        builder.setPositiveButton("Շարունակել", (dialog, which) -> {
            int priority = 3;
            try {
                priority = Integer.parseInt(input.getText().toString().trim());
            } catch (NumberFormatException ignored) {}

            // պահում ենք Firestore-ում
            db.collection("contacts")
                    .document(myUid)
                    .collection("list")
                    .document(user.getUserId())
                    .set(new Contact(user.getUserId(), priority));

            goToChat(user, priority);
        });

        builder.setNegativeButton("Չեղարկել", null);
        builder.show();
    }

    private void goToChat(User user, int priority) {
        Intent i = new Intent(UsersActivity.this, ChatActivity.class);
        i.putExtra("receiverId", user.getUserId());
        i.putExtra("receiverName", user.getName());
        i.putExtra("receiverPriority", priority);
        startActivity(i);
    }
    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
