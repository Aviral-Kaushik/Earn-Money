package com.apphub.eaa2.Fragments;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.apphub.eaa2.Activities.MainActivity;
import com.apphub.eaa2.databinding.FragmentReferBinding;
import com.google.android.material.snackbar.Snackbar;

public class ReferFragment extends Fragment {

    public FragmentReferBinding binding;

    private final MainActivity mainActivity;

    public ReferFragment(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentReferBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.icBack.setOnClickListener(view1 -> mainActivity.showHomeFragment());

        binding.shareWhatsapp.setOnClickListener(view1 -> sendWhatsappMessage());
        binding.shareTelegram.setOnClickListener(view1 -> sendTelegramMessage());
        binding.share.setOnClickListener(view1 -> shareIntent());

        binding.ivCopyCode.setOnClickListener(view1 -> copyReferralCodeToClipboard());
        binding.tvCopyCode.setOnClickListener(view1 -> copyReferralCodeToClipboard());
    }

    private String getShareMessage() {
        return "Welcome to Earnpay!\n" +
                "\n" +
                "With our earning app, you can make money effortlessly by completing tasks and referring friends. Cash out your earnings through multiple payment channels.\n" +
                "\n" +
                "Sign up using my referral code" + binding.referralCode.getText().toString().toUpperCase() + "and get a bonus to start earning right away! Earnpay is a fun and easy way to make extra income. Join now and get a head start!";
    }

    private void sendWhatsappMessage() {

        Intent intent = new Intent(Intent.ACTION_SEND);

        intent.setType("text/plain");
        intent.setPackage("com.whatsapp");


        intent.putExtra(
                Intent.EXTRA_TEXT,
                getShareMessage());

        if (intent.resolveActivity(mainActivity.getPackageManager()) == null) {

            Snackbar snackbar = Snackbar.make(binding.inviteLayout
                    , "Please install whatsapp",
                    Snackbar.LENGTH_SHORT);
            snackbar.show();

        } else {

            startActivity(intent);

        }


    }

    private void sendTelegramMessage() {
        String url = "tg://msg?text=" + getShareMessage();

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));

        if (intent.resolveActivity(mainActivity.getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Snackbar snackbar = Snackbar.make(binding.inviteLayout
                    , "Please install Telegram",
                    Snackbar.LENGTH_SHORT);
            snackbar.show();
        }
    }

    private void shareIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, getShareMessage());
        startActivity(Intent.createChooser(shareIntent, "Share via"));
    }

    private void copyReferralCodeToClipboard() {
        ClipboardManager clipboard = (ClipboardManager) mainActivity.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Referral Code", binding.referralCode.getText().toString());
        clipboard.setPrimaryClip(clip);

        Snackbar snackbar = Snackbar.make(binding.inviteLayout
                , "Referral Code Copied to Clipboard",
                Snackbar.LENGTH_SHORT);
        snackbar.show();
    }
}
