package com.apphub.eaa2.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.apphub.eaa2.R;
import com.apphub.eaa2.databinding.ActivityWithdrawBinding;
import com.google.android.material.snackbar.Snackbar;

public class WithdrawActivity extends AppCompatActivity {

    private ActivityWithdrawBinding binding;

    private boolean isPaytmSelected = false,
            isPaypalSelected = false,
            isBankSelected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWithdrawBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.icBack.setOnClickListener(view -> startActivity(new Intent(this, MainActivity.class)));

        binding.paypal.setOnClickListener(view -> {

            resetAllPaymentMethods();

            isPaypalSelected = true;

            binding.paypalCheck.setVisibility(View.VISIBLE);

        });

        binding.paytm.setOnClickListener(view -> {

            resetAllPaymentMethods();

            isPaytmSelected = true;

            binding.paytmCheck.setVisibility(View.VISIBLE);

        });

        binding.bank.setOnClickListener(view -> {

            resetAllPaymentMethods();

            isBankSelected = true;

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

        String paymentMode = "";

        if (isPaypalSelected) {
            paymentMode = "Paypal";
        } else if (isPaytmSelected) {
            paymentMode = "Paytm";
        } else if (isBankSelected) {
            paymentMode = "Bank";
        }

        if ((isPaytmSelected || isPaypalSelected || isBankSelected) && !(paymentMode.equals(""))) {
            Intent intent = new Intent(this, PaymentActivity.class);
            intent.putExtra(getString(R.string.payment_mode), paymentMode);
            startActivity(intent);
        } else {
            Snackbar.make(
                    binding.withdrawLayout,
                    "Please Select a Payment Method",
                    Snackbar.LENGTH_SHORT
            ).show();
        }

    }

}