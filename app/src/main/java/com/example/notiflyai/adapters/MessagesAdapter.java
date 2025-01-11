package com.example.notiflyai.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notifly.databinding.ItemMessageBinding;
import com.example.notiflyai.models.Message;

import java.util.List;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MsgViewHolder> {

    private List<Message> messageList;
    private String currentUserId;

    public MessagesAdapter(List<Message> messageList, String currentUserId) {
        this.messageList = messageList;
        this.currentUserId = currentUserId;
    }

    @Override
    public MsgViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemMessageBinding binding = ItemMessageBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new MsgViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(MsgViewHolder holder, int position) {
        Message msg = messageList.get(position);
        holder.bind(msg, currentUserId);
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    static class MsgViewHolder extends RecyclerView.ViewHolder {
        private ItemMessageBinding binding;

        public MsgViewHolder(ItemMessageBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Message msg, String currentUid) {
            binding.tvMessageText.setText(msg.getText());
            if (msg.getSenderId().equals(currentUid)) {
                binding.tvMessageText.setBackgroundResource(android.R.color.holo_blue_light);
            } else {
                binding.tvMessageText.setBackgroundResource(android.R.color.darker_gray);
            }
        }
    }
}