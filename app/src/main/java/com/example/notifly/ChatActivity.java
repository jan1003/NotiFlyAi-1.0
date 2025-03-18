package com.example.notifly;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.notifly.adapters.MessagesAdapter;
import com.example.notifly.databinding.ActivityChatBinding;
import com.example.notifly.models.Message;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.*;
import com.google.firebase.firestore.*;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private ActivityChatBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private String myUid;
    private String receiverId;
    private String receiverName;
    private int receiverPriority;

    private String chatId;

    private List<Message> messageList = new ArrayList<>();
    private MessagesAdapter messagesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        if(mAuth.getCurrentUser() == null) {
            finish();
            return;
        }

        myUid = mAuth.getCurrentUser().getUid();
        receiverId = getIntent().getStringExtra("receiverId");
        receiverName = getIntent().getStringExtra("receiverName");
        receiverPriority = getIntent().getIntExtra("receiverPriority", 3);

        setTitle("Чат с " + receiverName);

        // chatId — чтобы был уникален для пары (myUid, receiverId)
        if(myUid.compareTo(receiverId) < 0) {
            chatId = myUid + "_" + receiverId;
        } else {
            chatId = receiverId + "_" + myUid;
        }

        setupMessagesAdapter();
        listenMessagesUpdates();

        binding.btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = binding.etMessage.getText().toString().trim();
                if(!text.isEmpty()) {
                    sendMessageToFirestore(text);
                }
            }
        });

        binding.btnQuickReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText input = new EditText(ChatActivity.this);
                input.setHint("Короткий ответ...");
                // Можно вывести диалог или AlertDialog
                // Упростим - просто добавим во вью
                Toast.makeText(ChatActivity.this,
                        "Введите короткий текст (смотрите консоль)",
                        Toast.LENGTH_LONG).show();

                // Или используем Dialog:
                /*
                new AlertDialog.Builder(ChatActivity.this)
                    .setTitle("Short Reply")
                    .setView(input)
                    .setPositiveButton("OK", (dialog, which) -> {
                        String draft = input.getText().toString().trim();
                        if(!draft.isEmpty()) {
                            expandShortReplyAndSend(draft);
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
                 */
                // Для простоты в демо: набираем в LogCat: (этот код просто иллюстрирует)
                // Обычно - полноценный диалог.

                // В реальной app можно реализовать иначе:
                expandShortReplyAndSend("Сори, занят");
            }
        });
    }

    private void expandShortReplyAndSend(String shortDraft) {
        NotiFlyAI.generateExpandedReply(shortDraft, new NotiFlyAI.AICallback() {
            @Override
            public void onResult(String expanded) {
                // Отправим
                sendMessageToFirestore(expanded);
            }
        });
    }

    private void setupMessagesAdapter() {
        messagesAdapter = new MessagesAdapter(messageList, myUid);
        binding.rvMessages.setLayoutManager(new LinearLayoutManager(this));
        binding.rvMessages.setAdapter(messagesAdapter);
    }

    private void listenMessagesUpdates() {
        db.collection("chats")
                .document(chatId)
                .collection("messages")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener(this, (value, error) -> {
                    if(error != null) {
                        toast("Listen failed: " + error.getMessage());
                        return;
                    }
                    if(value != null) {
                        messageList.clear();
                        for(DocumentSnapshot doc : value.getDocuments()) {
                            Message msg = doc.toObject(Message.class);
                            if(msg != null) {
                                messageList.add(msg);
                            }
                        }
                        messagesAdapter.notifyDataSetChanged();
                        binding.rvMessages.scrollToPosition(messageList.size()-1);
                    }
                });
    }

    private void sendMessageToFirestore(String text) {
        String msgId = db.collection("chats")
                .document(chatId)
                .collection("messages")
                .document().getId();
        Message newMsg = new Message(
                msgId,
                myUid,
                receiverId,
                text,
                System.currentTimeMillis()
        );
        db.collection("chats")
                .document(chatId)
                .collection("messages")
                .document(msgId)
                .set(newMsg)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(!task.isSuccessful()) {
                            toast("Ошибка отправки: " + task.getException());
                        } else {
                            binding.etMessage.setText("");
                        }
                    }
                });
    }

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}