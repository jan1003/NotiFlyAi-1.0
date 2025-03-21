package com.example.notifly;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.notifly.databinding.ActivitySettingsBinding;
import com.google.firebase.auth.FirebaseAuth;

public class SettingsActivity extends AppCompatActivity {

    private ActivitySettingsBinding binding;
    private FirebaseAuth mAuth;
    private boolean isBusyMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();

        // Показываем стрелку "назад"
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Настройки");
        }

        SharedPreferences prefs = getSharedPreferences("NotiFlyPrefs", MODE_PRIVATE);
        isBusyMode = prefs.getBoolean("IS_BUSY_MODE", false);
        binding.switchBusyMode.setChecked(isBusyMode);

        binding.switchBusyMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                isBusyMode = b;
                prefs.edit().putBoolean("IS_BUSY_MODE", isBusyMode).apply();
                Toast.makeText(SettingsActivity.this, "Busy Mode: " + (isBusyMode ? "ON" : "OFF"), Toast.LENGTH_SHORT).show();
            }
        });

        binding.btnSignOut.setOnClickListener(view -> {
            mAuth.signOut();
            startActivity(new Intent(SettingsActivity.this, LoginActivity.class));
            finishAffinity();
        });
    }

    // Обработка стрелки "назад"
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}