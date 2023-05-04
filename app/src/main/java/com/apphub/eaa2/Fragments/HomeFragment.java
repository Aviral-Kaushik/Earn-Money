package com.apphub.eaa2.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.apphub.eaa2.Activities.LoginActivity;
import com.apphub.eaa2.Activities.MainActivity;
import com.apphub.eaa2.Adapter.OptionsAdapter;
import com.apphub.eaa2.Models.Options;
import com.apphub.eaa2.R;
import com.apphub.eaa2.Utils.RecyclerViewMargin;
import com.apphub.eaa2.databinding.FragmentHomeBinding;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    private final MainActivity mainActivity;

    public HomeFragment(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();

        setUpOptionAdapter();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.userProfilePicture.setOnClickListener(view1 -> startActivity(new Intent(mainActivity, LoginActivity.class)));
    }

    private void setUpOptionAdapter() {

        ArrayList<Options> optionsArrayList = new ArrayList<>();

        optionsArrayList.add(new Options(
                R.drawable.ic_candy_crush,
                requireContext().getString(R.string.candy_crush),
                requireContext().getString(R.string.options_desc),
                0.23
        ));

        optionsArrayList.add(new Options(
                R.drawable.ic_candy_crush,
                requireContext().getString(R.string.candy_crush),
                requireContext().getString(R.string.options_desc),
                0.23
        ));

        optionsArrayList.add(new Options(
                R.drawable.ic_candy_crush,
                requireContext().getString(R.string.candy_crush),
                requireContext().getString(R.string.options_desc),
                0.23
        ));

        optionsArrayList.add(new Options(
                R.drawable.ic_candy_crush,
                requireContext().getString(R.string.candy_crush),
                requireContext().getString(R.string.options_desc),
                0.23
        ));

        optionsArrayList.add(new Options(
                R.drawable.ic_candy_crush,
                requireContext().getString(R.string.candy_crush),
                requireContext().getString(R.string.options_desc),
                0.23
        ));

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false);

        binding.optionsRecyclerView.setLayoutManager(linearLayoutManager);

        OptionsAdapter optionsRecyclerViewAdapter =
                new OptionsAdapter(optionsArrayList);

        RecyclerViewMargin recyclerViewMargin = new RecyclerViewMargin(1);
        binding.optionsRecyclerView.addItemDecoration(recyclerViewMargin);

        binding.optionsRecyclerView.setAdapter(optionsRecyclerViewAdapter);

    }
}