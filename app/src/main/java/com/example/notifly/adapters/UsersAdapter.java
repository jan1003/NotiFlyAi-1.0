package com.example.notifly.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import com.example.notifly.databinding.ItemUserBinding;
import com.example.notifly.models.User;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserViewHolder> {

    public interface OnUserClickListener {
        void onUserClick(User user);
    }

    private List<User> userList;
    private Map<String, Integer> priorityMap; // ключ: userId, значение: приоритет
    private OnUserClickListener onUserClickListener;

    public UsersAdapter(List<User> userList, Map<String, Integer> priorityMap, OnUserClickListener listener) {
        this.userList = userList;
        this.priorityMap = priorityMap;
        this.onUserClickListener = listener;
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemUserBinding binding = ItemUserBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new UserViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(UserViewHolder holder, int position) {
        User user = userList.get(position);
        int priority = priorityMap.getOrDefault(user.getUserId(), 3);
        holder.bind(user, priority, onUserClickListener);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        private ItemUserBinding binding;

        public UserViewHolder(ItemUserBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(final User user, int priority, final OnUserClickListener listener) {
            binding.tvUserName.setText(user.getName());
            binding.tvPriority.setText("Приоритет: " + priority);
            binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onUserClick(user);
                }
            });
        }
    }
}