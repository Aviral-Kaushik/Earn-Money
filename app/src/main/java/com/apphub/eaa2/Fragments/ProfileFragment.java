package com.apphub.eaa2.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.apphub.eaa2.Activities.MainActivity;
import com.apphub.eaa2.Activities.WithdrawActivity;
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

        binding.myBalance.setOnClickListener(view1 -> mainActivity.startActivity(new Intent(mainActivity, WithdrawActivity.class)));

        binding.invite.setOnClickListener(view1 -> mainActivity.showReferFragment());
    }
}
