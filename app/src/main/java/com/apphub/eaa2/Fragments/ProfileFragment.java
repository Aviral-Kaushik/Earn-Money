package com.apphub.eaa2.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.apphub.eaa2.Activities.LoginActivity;
import com.apphub.eaa2.Activities.MainActivity;
import com.apphub.eaa2.Activities.PrivacyPolicyActivity;
import com.apphub.eaa2.Activities.TermsAndConditionActivity;
import com.apphub.eaa2.Activities.WithdrawActivity;
import com.apphub.eaa2.R;
import com.apphub.eaa2.databinding.FramentProfileBinding;

public class ProfileFragment extends Fragment {

    public FramentProfileBinding binding;

    private final MainActivity mainActivity;

    public ProfileFragment(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = FramentProfileBinding.inflate(inflater, container,false);

        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.icBack.setOnClickListener(view1 -> mainActivity.showHomeFragment());

        binding.myBalance.setOnClickListener(view1 -> {
            Intent intent = new Intent(mainActivity, WithdrawActivity.class);
            intent.putExtra(mainActivity.getString(R.string.intent_extra_name), mainActivity.getUsername());
            intent.putExtra(mainActivity.getString(R.string.intent_extra_balance), mainActivity.getBalance());
            mainActivity.startActivity(intent);
        });

        binding.terms.setOnClickListener(view1 -> startActivity(new Intent(mainActivity, TermsAndConditionActivity.class)));

        binding.privacy.setOnClickListener(view1 -> startActivity(new Intent(mainActivity, PrivacyPolicyActivity.class)));

        binding.logout.setOnClickListener(view1 -> {

            SharedPreferences sharedPreferences = mainActivity.getSharedPreferences(mainActivity.getString(R.string.check_shared_preferences), Context.MODE_PRIVATE);
            SharedPreferences.Editor myEdit = sharedPreferences.edit();

            myEdit.putBoolean("isLoggedIn", false);
            myEdit.putString("email", "");

            myEdit.apply();

            SharedPreferences sharedPreferences1 = mainActivity.getSharedPreferences("user", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences1.edit();

            editor.clear();

            editor.apply();

            startActivity(new Intent(mainActivity, LoginActivity.class));

        });


        binding.invite.setOnClickListener(view1 -> mainActivity.showReferFragment());
    }
}
