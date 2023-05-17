package com.apphub.eaa2.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.apphub.eaa2.Activities.GamesActivity;
import com.apphub.eaa2.Activities.MainActivity;
import com.apphub.eaa2.Adapter.OptionsAdapter;
import com.apphub.eaa2.Models.OptionChances;
import com.apphub.eaa2.Models.Options;
import com.apphub.eaa2.R;
import com.apphub.eaa2.Utils.RecyclerViewMargin;
import com.apphub.eaa2.databinding.FragmentHomeBinding;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private static final String TAG = "AviralAPI";

    public FragmentHomeBinding binding;

    private final MainActivity mainActivity;

    private final int TOTAL_CHANCES = 30;
    private int candyCrushChances;
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

        getChancesFromSharedPreference();
    }

    private void getChancesFromSharedPreference() {

        Log.d(TAG, "getChancesFromSharedPreference: Added Chances to Shared Preferences");

        SharedPreferences candyCrushSharedPreferences = requireActivity().getSharedPreferences(
                getString(R.string.cancy_crush_reward),
                Context.MODE_PRIVATE
        );

        candyCrushChances = candyCrushSharedPreferences.getInt(getString(R.string.chances_left), TOTAL_CHANCES);

        Log.d(TAG, "getChancesFromSharedPreference: candyCrushChances in Home Fragment: " + candyCrushChances);

        setUpOptionAdapter();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.banner.setOnClickListener(view1 -> startActivity(new Intent(mainActivity, GamesActivity.class)));
    }

    private void setUpOptionAdapter() {

        Log.d(TAG, "setUpOptionAdapter: candyCrushChances: " + candyCrushChances);
        OptionChances optionChances = new OptionChances(candyCrushChances);

        ArrayList<Options> optionsArrayList = new ArrayList<>();

        optionsArrayList.add(new Options(
                R.drawable.ic_candy_crush,
                requireContext().getString(R.string.candy_crush),
                requireContext().getString(R.string.options_desc),
                0.23,
                optionChances.getCandyCrushChances()
        ));

        optionsArrayList.add(new Options(
                R.drawable.ic_candy_crush,
                requireContext().getString(R.string.candy_crush),
                requireContext().getString(R.string.options_desc),
                0.23,
                optionChances.getCandyCrushChances()
        ));

        optionsArrayList.add(new Options(
                R.drawable.ic_candy_crush,
                requireContext().getString(R.string.candy_crush),
                requireContext().getString(R.string.options_desc),
                0.23,
                optionChances.getCandyCrushChances()
        ));

        optionsArrayList.add(new Options(
                R.drawable.ic_candy_crush,
                requireContext().getString(R.string.candy_crush),
                requireContext().getString(R.string.options_desc),
                0.23,
                optionChances.getCandyCrushChances()
        ));

        optionsArrayList.add(new Options(
                R.drawable.ic_candy_crush,
                requireContext().getString(R.string.candy_crush),
                requireContext().getString(R.string.options_desc),
                0.23,
                optionChances.getCandyCrushChances()
        ));

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false);

        binding.optionsRecyclerView.setLayoutManager(linearLayoutManager);

        OptionsAdapter optionsRecyclerViewAdapter =
                new OptionsAdapter(optionsArrayList, mainActivity, optionChances);

        RecyclerViewMargin recyclerViewMargin = new RecyclerViewMargin(1);
        binding.optionsRecyclerView.addItemDecoration(recyclerViewMargin);

        binding.optionsRecyclerView.setAdapter(optionsRecyclerViewAdapter);

    }
}