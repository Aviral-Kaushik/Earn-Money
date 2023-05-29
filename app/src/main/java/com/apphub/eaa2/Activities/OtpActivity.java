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
import com.apphub.eaa2.databinding.ActivityOtpBinding;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

public class OtpActivity extends AppCompatActivity {

    private static final String TAG = "AviralAPI";

    private ActivityOtpBinding binding;

    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOtpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        AndroidNetworking.initialize(getApplicationContext());

        Intent intent = getIntent();

        if (intent.hasExtra("email")) {
            email = intent.getStringExtra("email");
            binding.tvEmail.setText(String.format("OTP send to %s", email));
        }

        Snackbar.make(
                binding.layoutOtp,
                "Verification code has been sent on your email",
                Snackbar.LENGTH_SHORT
        ).show();

        binding.submitOtp.setOnClickListener(view -> {

            Log.d(TAG, "onCreate: Otp entered by user: " + binding.editTextOtp.getText());

            if (!String.valueOf(binding.editTextOtp.getText()).equals("")) {

                verifyOtp();

            } else {
                Snackbar.make(
                        binding.layoutOtp,
                        "Please enter otp",
                        Snackbar.LENGTH_SHORT
                ).show();
            }

        });

//        resendOtp();


    }

    private void verifyOtp() {

        AndroidNetworking.post(ApiLinks.VERIFY_OTP)
                .addBodyParameter("email_id", email)
                .addBodyParameter("otp", String.valueOf(binding.editTextOtp.getText()))
                .build().getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Log.d(TAG, "onResponse: Response of otp verify: " + response);

                        try {
                            if (response.getString("message").equals("Success")) {

                                Intent intent = new Intent(OtpActivity.this, PasswordActivity.class);
                                intent.putExtra("email", email);
                                startActivity(intent);

                            } else if (response.getString("message").equals("Wrong Otp")) {

                                Snackbar.make(
                                        binding.layoutOtp,
                                        "Invalid Otp",
                                        Snackbar.LENGTH_SHORT
                                ).show();

                            } else if (response.getString("message").equals("Otp validity expired")) {

                                Snackbar.make(
                                        binding.layoutOtp,
                                        "The otp validity has expired we have send a new otp on your mail",
                                        Snackbar.LENGTH_SHORT
                                ).show();

                                resendOtp();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();

                            Log.d(TAG, "onResponse: Exception while generating otp: " + e.getMessage());

                            Snackbar.make(
                                    binding.layoutOtp,
                                    "Server Error",
                                    Snackbar.LENGTH_SHORT
                            ).show();
                        }

                    }

                    @Override
                    public void onError(ANError anError) {

                        Log.d(TAG, "onError: Error occurred while verifying otp: " + anError.getMessage());

                        Snackbar.make(
                                binding.layoutOtp,
                                "Server Error",
                                Snackbar.LENGTH_SHORT
                        ).show();

                    }
                });

    }

    private void resendOtp() {

        AndroidNetworking.post(ApiLinks.RESEND_OTP)
                .addBodyParameter("email_id", email)
                .addBodyParameter("otp", RandomAlphaNumericGenerator.generateOTP(6))
                .build().getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Log.d(TAG, "onResponse: Response of otp resending: " + response);

                        try {

                            if(response.getString("message").equals("Email sent")) {

                                Snackbar.make(
                                        binding.layoutOtp,
                                        "Otp send on your mail",
                                        Snackbar.LENGTH_SHORT
                                ).show();

                            } else if (response.getString("message").equals("Email not sent")) {

                                Snackbar.make(
                                        binding.layoutOtp,
                                        "Something went wrong! Please try again later",
                                        Snackbar.LENGTH_SHORT
                                ).show();

                            } else if (response.getString("message").equals("Otp resend request failed, Try Again")) {

                                Snackbar.make(
                                        binding.layoutOtp,
                                        "Something went wrong! Please try again later",
                                        Snackbar.LENGTH_SHORT
                                ).show();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();

                            Log.d(TAG, "onResponse: Exception while generating otp: " + e.getMessage());

                            Snackbar.make(
                                    binding.layoutOtp,
                                    "Server Error",
                                    Snackbar.LENGTH_SHORT
                            ).show();
                        }

                    }

                    @Override
                    public void onError(ANError anError) {

                        Log.d(TAG, "onError: Error occurred while resending otp: " + anError.getMessage());

                        Snackbar.make(
                                binding.layoutOtp,
                                "Server Error",
                                Snackbar.LENGTH_SHORT
                        ).show();

                    }
                });

    }
}