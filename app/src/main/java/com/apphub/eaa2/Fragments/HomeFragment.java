package com.apphub.eaa2.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.apphub.eaa2.Activities.GamesActivity;
import com.apphub.eaa2.Activities.MainActivity;
import com.apphub.eaa2.Adapter.OptionsAdapter;
import com.apphub.eaa2.Models.OptionChances;
import com.apphub.eaa2.Models.Options;
import com.apphub.eaa2.R;
import com.apphub.eaa2.Utils.ApiLinks;
import com.apphub.eaa2.Utils.RecyclerViewMargin;
import com.apphub.eaa2.databinding.FragmentHomeBinding;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private static final String TAG = "AviralAPI";

    public FragmentHomeBinding binding;

    private final MainActivity mainActivity;

    private final int TOTAL_CHANCES = 1;

    private int moneyBagChances;
    private int surpriseGiftChances;
    private int dailyBonusChances;
    private int earnRewardChances;
    private int goldCoinChances;
    private int walletMoneyChances;

    public HomeFragment(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);

        AndroidNetworking.initialize(mainActivity.getApplicationContext());

        setUpLinks();

        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();

        getChancesFromSharedPreference();
    }

    private void getChancesFromSharedPreference() {

        Log.d(TAG, "getChancesFromSharedPreference: Added Chances to Shared Preferences");

        SharedPreferences moneyBagSharedPreferences = requireActivity().getSharedPreferences(
                getString(R.string.money_bag_reward),
                Context.MODE_PRIVATE
        );

        SharedPreferences surpriseGiftSharedPreferences = requireActivity().getSharedPreferences(
                getString(R.string.surprise_gift_reward),
                Context.MODE_PRIVATE
        );

        SharedPreferences dailyBonusSharedPreferences = requireActivity().getSharedPreferences(
                getString(R.string.daily_bonus_reward),
                Context.MODE_PRIVATE
        );

        SharedPreferences earnRewardSharedPreferences = requireActivity().getSharedPreferences(
                getString(R.string.earn_reward_reward),
                Context.MODE_PRIVATE
        );

        SharedPreferences goldCoinSharedPreferences = requireActivity().getSharedPreferences(
                getString(R.string.gold_coin_reward),
                Context.MODE_PRIVATE
        );

        SharedPreferences walletMoneySharedPreferences = requireActivity().getSharedPreferences(
                getString(R.string.wallet_money_reward),
                Context.MODE_PRIVATE
        );

        moneyBagChances = moneyBagSharedPreferences.getInt(getString(R.string.chances_left), TOTAL_CHANCES);
        surpriseGiftChances = surpriseGiftSharedPreferences.getInt(getString(R.string.chances_left), TOTAL_CHANCES);
        dailyBonusChances = dailyBonusSharedPreferences.getInt(getString(R.string.chances_left), TOTAL_CHANCES);
        earnRewardChances = earnRewardSharedPreferences.getInt(getString(R.string.chances_left), TOTAL_CHANCES);
        goldCoinChances = goldCoinSharedPreferences.getInt(getString(R.string.chances_left), TOTAL_CHANCES);
        walletMoneyChances = walletMoneySharedPreferences.getInt(getString(R.string.chances_left), TOTAL_CHANCES);

        Log.d(TAG, "getChancesFromSharedPreference: moneyBagChances in Home Fragment: " + moneyBagChances);
        Log.d(TAG, "getChancesFromSharedPreference: surpriseGiftChances in Home Fragment: " + surpriseGiftChances);
        Log.d(TAG, "getChancesFromSharedPreference: dailyBonusChances in Home Fragment: " + dailyBonusChances);
        Log.d(TAG, "getChancesFromSharedPreference: earnRewardChances in Home Fragment: " + earnRewardChances);
        Log.d(TAG, "getChancesFromSharedPreference: goldCoinChances in Home Fragment: " + goldCoinChances);
        Log.d(TAG, "getChancesFromSharedPreference: walletMoneyChances in Home Fragment: " + walletMoneyChances);

        setUpOptionAdapter();

    }

    private void setUpLinks() {


        AndroidNetworking.get(ApiLinks.GET_LINKS)
                .build().getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Log.d(TAG, "onResponse: response: " + response.toString());

                        ArrayList<String> links = new ArrayList<>();

                        try {

                            links.add(response.getString("1"));
                            links.add(response.getString("2"));
                            links.add(response.getString("3"));
                            links.add(response.getString("4"));


                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d(TAG, "onResponse: Exception while getting links: " + e.getMessage());

                            Snackbar.make(
                                    binding.homeLayout,
                                    "Something went wrong! Please try again later",
                                    Snackbar.LENGTH_SHORT
                            ).show();
                        }

                        binding.ludo.setOnClickListener(view -> openUrl(links.get(1)));
                        binding.pubg.setOnClickListener(view -> openUrl(links.get(2)));
                        binding.card.setOnClickListener(view -> openUrl(links.get(3)));
                        binding.chess.setOnClickListener(view -> openUrl(links.get(4)));

                    }

                    @Override
                    public void onError(ANError anError) {

                    }
                });


    }

    private void openUrl(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setPackage("com.android.chrome");
        startActivity(intent);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.more.setOnClickListener(view1 -> {

            Intent intent = new Intent(mainActivity, GamesActivity.class);
            intent.putExtra("balance", binding.usernamePrice.getText().toString());
            startActivity(intent);

        });

        binding.banner.setOnClickListener(view1 -> {

            Intent intent = new Intent(mainActivity, GamesActivity.class);
            intent.putExtra("balance", binding.usernamePrice.getText().toString());
            startActivity(intent);

        });

    }

    private void setUpOptionAdapter() {

        OptionChances optionChances = new OptionChances(
                moneyBagChances,
                surpriseGiftChances,
                dailyBonusChances,
                earnRewardChances,
                goldCoinChances,
                walletMoneyChances
        );

        ArrayList<Options> optionsArrayList = new ArrayList<>();

        optionsArrayList.add(new Options(
                R.drawable.ic_money_bag,
                "Money Bag",
                "Earn $0.10 by completing the ...",
                0.10,
                optionChances.getMoneyBagChances(),
                1
        ));

        optionsArrayList.add(new Options(
                R.drawable.ic_surprise_gift,
                "Surprise Gift",
                "Earn $0.05 by completing the ...",
                0.05,
                optionChances.getSurpriseGiftChances(),
                1
        ));

        optionsArrayList.add(new Options(
                R.drawable.ic_daily_bonus,
                "Daily Bonus",
                "Earn $0.20 by completing the ...",
                0.20,
                optionChances.getDailyBonusChances(),
                1
        ));

        optionsArrayList.add(new Options(
                R.drawable.ic_earn_reward,
                "Earn Reward",
                "Earn $0.13 by completing the ...",
                0.13,
                optionChances.getEarnRewardChances(),
                1
        ));

        optionsArrayList.add(new Options(
                R.drawable.ic_gold_coin,
                "Gold Coin",
                "Earn $0.15 by completing the ...",
                0.15,
                optionChances.getGoldCoinChances(),
                1
        ));

        optionsArrayList.add(new Options(
                R.drawable.ic_wallet_money,
                "Wallet Money",
                "Earn $0.25 by completing the ...",
                0.25,
                optionChances.getWalletMoneyChances(),
                1
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