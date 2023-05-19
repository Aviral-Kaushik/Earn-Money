package com.apphub.eaa2.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.apphub.eaa2.R;
import com.apphub.eaa2.Utils.ApiLinks;
import com.apphub.eaa2.Utils.RandomAlphaNumericGenerator;
import com.apphub.eaa2.databinding.ActivityPaymentBinding;
import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

public class PaymentActivity extends AppCompatActivity {

    private static final String TAG = "AviralAPI";
    private ActivityPaymentBinding binding;

    private String paymentMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPaymentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        AndroidNetworking.initialize(getApplicationContext());

        Intent intent = getIntent();

        if (intent.hasExtra(getString(R.string.payment_mode))) {
            paymentMode = intent.getStringExtra(getString(R.string.payment_mode));
        }

        setUpViews();

    }

    private void setUpViews() {

        switch (paymentMode) {
            case "Paypal":

                Glide.with(this)
                        .load(R.drawable.ic_paypal)
                        .into(binding.imagePaymentMode);

                binding.textPaymentMode.setText(getString(R.string.paypal));

                binding.id.setHint(getString(R.string.paypal_id));

                break;
            case "Paytm":

                Glide.with(this)
                        .load(R.drawable.ic_paytm2)
                        .into(binding.imagePaymentMode);

                binding.textPaymentMode.setText(getString(R.string.paytm));

                binding.id.setHint(getString(R.string.paytm_id));

                break;
            case "Bank":

                Glide.with(this)
                        .load(R.drawable.ic_bank)
                        .into(binding.imagePaymentMode);

                binding.textPaymentMode.setText(getString(R.string.bank));

                binding.id.setHint(getString(R.string.bank_id));
                break;
        }

        binding.btnConfirm.setOnClickListener(view -> {
            if (!(binding.name.getText().toString().equals("")) &&
                    !(binding.id.getText().toString().equals("")) &&
                    !(binding.phoneNumber.getText().toString().equals("")))  {

                makeWithdrawRequestAndNavigateToSuccessfulActivity();

            }  else {

                Snackbar.make(
                        binding.paymentLayout,
                        "Please fill all the fields",
                        Snackbar.LENGTH_SHORT
                ).show();

            }
        });

    }

    private void makeWithdrawRequestAndNavigateToSuccessfulActivity() {

        String uid = getSharedPreferences("user", Context.MODE_PRIVATE).getString("uid", "");


        AndroidNetworking.post(ApiLinks.WITHDRAW_REQUEST)
                .addBodyParameter("user_id", uid)
                .addBodyParameter("name", binding.name.getText().toString())
                .addBodyParameter("number", binding.phoneNumber.getText().toString())
                .addBodyParameter("id", "")
                .addBodyParameter("amount", "100")
                .addBodyParameter("bank_name", "")
                .addBodyParameter("method", paymentMode)
                .addBodyParameter("unique_id", RandomAlphaNumericGenerator.generateAlphaNumeric(10))
                .build().getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Log.d(TAG, "onResponse: Response in payment act: " + response);
                        try {

                            String message = response.getString("message");

                            if (message.equals("added")) {
                                startActivity(new Intent(PaymentActivity.this, SuccessfulActivity.class));
                            } else {
                                Snackbar.make(
                                        binding.paymentLayout,
                                        "Cannot make withdraw request at his moment",
                                        Snackbar.LENGTH_SHORT
                                ).show();
                            }

                        } catch (JSONException e) {

                            e.printStackTrace();

                            Log.d(TAG, "checkForEmailRegistration: Exception Occurred while api call " + e.getMessage());
                            Snackbar.make(
                                    binding.paymentLayout,
                                    "Server Error! Please try again later",
                                    Snackbar.LENGTH_SHORT
                            ).show();

                        }
                    }

                    @Override
                    public void onError(ANError anError) {

                    }
                });

    }
}