package com.apphub.eaa2.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.apphub.eaa2.Utils.ApiLinks;
import com.apphub.eaa2.Utils.PasswordEncrypt;
import com.apphub.eaa2.Utils.TokenPreference;
import com.apphub.eaa2.Utils.UpdateToken;
import com.apphub.eaa2.databinding.ActivityPasswordBinding;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

public class PasswordActivity extends AppCompatActivity {

    private static final String TAG = "AviralAPI";

    private ActivityPasswordBinding binding;

    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        AndroidNetworking.initialize(getApplicationContext());

        Intent intent = getIntent();

        if (intent.hasExtra("email")) {
            email = intent.getStringExtra("email");
        }

        binding.passwordToggle.setOnClickListener(view -> {
            if (binding.editTextPassword.getInputType() == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                binding.editTextPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            } else {
                binding.editTextPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            }
            binding.editTextPassword.setSelection(binding.editTextPassword.getText().length());
        });


        binding.confirmPasswordToggle.setOnClickListener(view -> {
            if (binding.editTextConfirmPassword.getInputType() == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                binding.editTextConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            } else {
                binding.editTextConfirmPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            }
            binding.editTextConfirmPassword.setSelection(binding.editTextConfirmPassword.getText().length());
        });


        binding.btnResetPassword.setOnClickListener(view -> {

            if (binding.editTextPassword.getText().toString().equals("")) {

                Snackbar.make(
                        binding.layoutPassword,
                        "Please enter password",
                        Snackbar.LENGTH_SHORT
                ).show();

            }

            if (binding.editTextConfirmPassword.getText().toString().equals("")) {

                Snackbar.make(
                        binding.layoutPassword,
                        "Please enter same password in confirm password field",
                        Snackbar.LENGTH_SHORT
                ).show();

            }

            if (!binding.editTextPassword.getText().toString()
                    .equals(binding.editTextConfirmPassword.getText().toString())) {

                Snackbar.make(
                        binding.layoutPassword,
                        "Password does not match",
                        Snackbar.LENGTH_SHORT
                ).show();

            } else {

                resetPassword();

            }

        });

    }

    private void resetPassword() {

        String password = PasswordEncrypt.encrypt(binding.editTextPassword.getText().toString());

        AndroidNetworking.post(ApiLinks.RESET_PASSWORD)
                .addBodyParameter("email", email)
                .addBodyParameter("password", password)
                .build().getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            Log.d(TAG, "onResponse: Response: " + response);

                            String message = response.getString("message");

                            switch (message) {
                                case "Success":

                                    Snackbar.make(
                                            binding.layoutPassword,
                                            "Password has been rested successful, please login",
                                            Snackbar.LENGTH_SHORT
                                    ).show();

                                    startActivity(new Intent(PasswordActivity.this, LoginActivity.class));

                                    break;
                                case "Failed":

                                    Snackbar.make(
                                            binding.layoutPassword,
                                            "Password Reset failed",
                                            Snackbar.LENGTH_SHORT
                                    ).show();

                                    break;
                                case "failed, try again":

                                    Snackbar.make(
                                            binding.layoutPassword,
                                            "Please try again",
                                            Snackbar.LENGTH_SHORT
                                    ).show();

                                    break;
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();

                            Log.d(TAG, "onResponse: Exception while user login: " + e.getMessage());

                            Snackbar.make(
                                    binding.layoutPassword,
                                    "Something went wrong! Please try again",
                                    Snackbar.LENGTH_SHORT
                            ).show();
                        }


                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d(TAG, "onError: Error while login user: " + anError.getMessage());

                        Snackbar.make(
                                binding.layoutPassword,
                                "Server Error",
                                Snackbar.LENGTH_SHORT
                        ).show();
                    }
                });

    }
}