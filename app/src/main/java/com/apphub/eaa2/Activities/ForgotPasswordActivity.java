package com.apphub.eaa2.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.apphub.eaa2.Utils.ApiLinks;
import com.apphub.eaa2.Utils.RandomAlphaNumericGenerator;
import com.apphub.eaa2.databinding.ActivityForgotPasswordBinding;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

public class ForgotPasswordActivity extends AppCompatActivity {

    private static final String TAG = "AviralAPI";

    private ActivityForgotPasswordBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgotPasswordBinding.inflate(getLayoutInflater());;
        setContentView(binding.getRoot());

        AndroidNetworking.initialize(getApplicationContext());

        binding.btnContinue.setOnClickListener(view -> {

            if (!binding.editTextEmail.getText().toString().equals("")) {
                generateOtpAndNavigateToOtpActivity();
            } else {
                Snackbar.make(
                        binding.layoutForgotPassword,
                        "Please Enter email",
                        Snackbar.LENGTH_SHORT
                ).show();
            }
        });
    }

    private void generateOtpAndNavigateToOtpActivity() {

        AndroidNetworking.post(ApiLinks.GENERATE_OTP)
                .addBodyParameter("email_id", binding.editTextEmail.getText().toString())
                .addBodyParameter("otp", RandomAlphaNumericGenerator.generateOTP(6))
                .build().getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Log.d(TAG, "onResponse: Response while generating otp: " + response);

                        try {

                            if(response.getString("message").equals("Email sent")) {

//                                Intent intent = new Intent(ForgotPasswordActivity.this, OtpActivity.class);
//                                intent.putExtra("email", binding.editTextEmail.getText().toString());
//                                startActivity(intent);

                            } else if (response.getString("message").equals("Email not sent")) {

                                Snackbar.make(
                                        binding.layoutForgotPassword,
                                        "Something went wrong! Please try again later",
                                        Snackbar.LENGTH_SHORT
                                ).show();

                            } else if (response.getString("message").equals("Otp Generation Failed")) {

                                Snackbar.make(
                                        binding.layoutForgotPassword,
                                        "Server Error",
                                        Snackbar.LENGTH_SHORT
                                ).show();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d(TAG, "onResponse: Exception while generating otp: " + e.getMessage());

                            Snackbar.make(
                                    binding.layoutForgotPassword,
                                    "Server Error",
                                    Snackbar.LENGTH_SHORT
                            ).show();
                        }

                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d(TAG, "onError: Error while generating otp: " + anError.getMessage());

                        Snackbar.make(
                                binding.layoutForgotPassword,
                                "Server Error",
                                Snackbar.LENGTH_SHORT
                        ).show();
                    }
                });

    }
}