package com.apphub.eaa2.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.apphub.eaa2.databinding.ActivitySuccessfulBinding;

public class SuccessfulActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivitySuccessfulBinding binding = ActivitySuccessfulBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnDone.setOnClickListener(view -> startActivity(new Intent(this, MainActivity.class)));
    }
}