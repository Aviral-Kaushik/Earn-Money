package com.apphub.eaa2.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.apphub.eaa2.R;
import com.apphub.eaa2.databinding.ActivityWithdrawBinding;
import com.google.android.material.snackbar.Snackbar;

import java.util.Locale;

public class WithdrawActivity extends AppCompatActivity {

    private ActivityWithdrawBinding binding;

    private boolean isPaytmSelected = false,
            isPaypalSelected = false,
            isBankSelected = false;

    private double balance;

    private String paymentMode = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWithdrawBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();

        if (intent.hasExtra(getString(R.string.intent_extra_name))) {
            String name = intent.getStringExtra(getString(R.string.intent_extra_name));
            balance = intent.getDoubleExtra(getString(R.string.intent_extra_balance), 0.00);

            binding.username.setText(name);
            binding.userBalance.setText(String.format(Locale.US ,"$%.2f", balance));
        }

        binding.btnCancel.setOnClickListener(view -> finish());

        binding.icBack.setOnClickListener(view -> finish());

        binding.paypal.setOnClickListener(view -> {

            resetAllPaymentMethods();

            isPaypalSelected = true;

            paymentMode = "Paypal";

            binding.paypalCheck.setVisibility(View.VISIBLE);

        });

        binding.paytm.setOnClickListener(view -> {

            resetAllPaymentMethods();

            isPaytmSelected = true;

            paymentMode = "Paytm";

            binding.paytmCheck.setVisibility(View.VISIBLE);

        });

        binding.bank.setOnClickListener(view -> {

            resetAllPaymentMethods();

            isBankSelected = true;

            paymentMode = "Bank";

            binding.bankCheck.setVisibility(View.VISIBLE);

        });

        binding.btnConfirm.setOnClickListener(view -> navigateToPaymentActivity());
    }

    private void resetAllPaymentMethods() {

        binding.paypalCheck.setVisibility(View.INVISIBLE);
        binding.paytmCheck.setVisibility(View.INVISIBLE);
        binding.bankCheck.setVisibility(View.INVISIBLE);

        isPaypalSelected = false;
        isPaytmSelected = false;
        isBankSelected = false;

    }

    private void navigateToPaymentActivity() {

        if ((isPaytmSelected || isPaypalSelected || isBankSelected) && !(paymentMode.equals(""))) {

            if (balance >= 100) {
                Intent intent = new Intent(this, PaymentActivity.class);
                intent.putExtra(getString(R.string.payment_mode), paymentMode);
                intent.putExtra(getString(R.string.intent_extra_balance), balance);
                startActivity(intent);
            } else {
                Snackbar.make(
                        binding.withdrawLayout,
                        "Minimum withdraw amount is $100",
                        Snackbar.LENGTH_SHORT
                ).show();
            }


        } else {
            Snackbar.make(
                    binding.withdrawLayout,
                    "Please Select a Payment Method",
                    Snackbar.LENGTH_SHORT
            ).show();
        }

    }

}