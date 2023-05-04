package com.apphub.eaa2.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.apphub.eaa2.databinding.ActivityForgotPasswordBinding;

public class ForgotPasswordActivity extends AppCompatActivity {

    private ActivityForgotPasswordBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgotPasswordBinding.inflate(getLayoutInflater());;
        setContentView(binding.getRoot());
    }
}