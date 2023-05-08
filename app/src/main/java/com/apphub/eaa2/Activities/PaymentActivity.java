package com.apphub.eaa2.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.apphub.eaa2.R;
import com.apphub.eaa2.databinding.ActivityPaymentBinding;
import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;

public class PaymentActivity extends AppCompatActivity {

    private ActivityPaymentBinding binding;

    private String paymentMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPaymentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

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
                        .load(R.drawable.ic_paytm)
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

//                Intent intent = new Intent(this, SuccessfulActivity.class);
//                startActivity(intent);

            }  else {

                Snackbar.make(
                        binding.paymentLayout,
                        "Please fill all the fields",
                        Snackbar.LENGTH_SHORT
                ).show();

            }
        });

    }
}