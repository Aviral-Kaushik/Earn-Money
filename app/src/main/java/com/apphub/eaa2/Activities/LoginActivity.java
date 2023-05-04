package com.apphub.eaa2.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.apphub.eaa2.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.ll2.setOnClickListener(view -> startActivity(new Intent(this, SignupActivity.class)));

        binding.forgotPassword.setOnClickListener(view -> startActivity(new Intent(this, ForgotPasswordActivity.class)));

    }
}