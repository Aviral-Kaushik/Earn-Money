package com.apphub.eaa2.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.anupkumarpanwar.scratchview.ScratchView;
import com.apphub.eaa2.Activities.MainActivity;
import com.apphub.eaa2.databinding.FragmentScratchCardBinding;

import java.util.Locale;

public class ScratchCardFragment extends Fragment {

    private static final String TAG = "AviralPreferences";

    private FragmentScratchCardBinding binding;
    private final MainActivity mainActivity;
    private int chancesLeft;

    private boolean isTimerOn = false;

    public ScratchCardFragment(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = FragmentScratchCardBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getChances();

        binding.scratchView.setRevealListener(new ScratchView.IRevealListener() {
            @Override
            public void onRevealed(ScratchView scratchView) {

//                binding.usernameBalance.setText(String.format("₹%s",
//                        roundOfNumber(Double.parseDouble(String.valueOf(mainActivity.getBalance()))
//                                + earnedAmount)));

                binding.scratchView.reveal();

                decrementChances();

//                updateUserBalance(earnedAmount);

            }

            @Override
            public void onRevealPercentChangedListener(ScratchView scratchView, float percent) {

            }
        });

        Shader gradientShader = new LinearGradient(0, 0,
                binding.tvCongratulations.getPaint().measureText(binding.tvCongratulations.getText().toString()),
                binding.tvCongratulations.getTextSize(),
                new int[]{Color.parseColor("#FFB573"), Color.parseColor("#8DD04B")},
                new float[]{0, 1}, Shader.TileMode.CLAMP);

        binding.tvCongratulations.getPaint().setShader(gradientShader);
    }

    private void decrementChances() {
        SharedPreferences sharedPreferences = mainActivity.getSharedPreferences("scratchChances", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if (chancesLeft > 0) {
            editor.putInt("chancesLeft", (chancesLeft - 1));
//            editor.putInt("chancesLeft", 0);
            editor.apply();

//            chancesLeft = 0;

            binding.chances.setText(String.valueOf(chancesLeft));

            if (chancesLeft == 0) {
                timerUtils();
            }
        }

        getChances();

    }

    private void getChances() {
        SharedPreferences chancesPreferences = mainActivity.getSharedPreferences("scratchChances", Context.MODE_PRIVATE);
        chancesLeft = chancesPreferences.getInt("chancesLeft", 20);

//        chancesLeft = 0;

        binding.chances.setText(String.valueOf(chancesLeft));

        if (chancesLeft == 0) {

            binding.scratchView.setVisibility(View.INVISIBLE);
            binding.scratchCardBack.setVisibility(View.INVISIBLE);
            binding.tvScratchCardDescription.setVisibility(View.INVISIBLE);
            binding.ivNoRewards.setVisibility(View.VISIBLE);
            binding.tvRewards.setText("Rewards Completed.");

            binding.linearLayoutChances.setVisibility(View.INVISIBLE);
            binding.linearLayoutTime.setVisibility(View.VISIBLE);

            timerUtils();
        }
    }

    private void timerUtils() {

        Log.d(TAG, "timerUtils: time util started");

        SharedPreferences sharedPreferences = mainActivity.getSharedPreferences("scratchCardTime", Context.MODE_PRIVATE);

        long startTime = System.currentTimeMillis();

        long endTime;

        if (sharedPreferences.contains("endTime")) {

            Log.d(TAG, "timerUtils: Found endTime in shared preferences");

            endTime = sharedPreferences.getLong("endTime", startTime + (2 * 60 * 60 * 1000));

            Log.d(TAG, "timerUtils: endTime in shared preferences " + endTime);

        } else {

            Log.d(TAG, "timerUtils: Adding tme in preferences");

            startTime = System.currentTimeMillis();
            endTime = startTime + (2 * 60 * 60 * 1000);

            SharedPreferences.Editor myEdit = sharedPreferences.edit();
            myEdit.putLong("endTime", endTime);
            myEdit.apply();

            Log.d(TAG, "timerUtils: endTime not in shared preferences " + endTime);

        }

        if (System.currentTimeMillis() < endTime) {

            isTimerOn = true;

            CountDownTimer countDownTimer = new CountDownTimer(endTime - startTime, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    long seconds = millisUntilFinished / 1000;
                    long minutes = seconds / 60;
                    long hours = minutes / 60;
                    minutes = minutes % 60;
                    seconds = seconds % 60;
                    String time = String.format(Locale.US,"%02d:%02d:%02d", hours, minutes, seconds);
                    // Update the UI with the remaining time
                    binding.timeLeft.setText(time);
                }

                @Override
                public void onFinish() {
                    // Handle the timer finish event
                    // This will be called when the countdown is finished

                    getChances();

                    binding.linearLayoutTime.setVisibility(View.GONE);
                    binding.scratchView.setVisibility(View.VISIBLE);
                    binding.scratchCardBack.setVisibility(View.VISIBLE);
                    binding.ivNoRewards.setVisibility(View.GONE);
                    binding.linearLayoutChances.setVisibility(View.VISIBLE);
                    binding.tvScratchCardDescription.setVisibility(View.VISIBLE);

                    SharedPreferences.Editor myEdit = sharedPreferences.edit();
                    myEdit.remove("endTime");
                    myEdit.apply();
                    refreshChances();

                    isTimerOn = false;
                }
            };
            countDownTimer.start(); // Start the timer

        } else {

            getChances();

            binding.linearLayoutTime.setVisibility(View.GONE);
            binding.scratchView.setVisibility(View.VISIBLE);
            binding.scratchCardBack.setVisibility(View.VISIBLE);
            binding.ivNoRewards.setVisibility(View.GONE);
            binding.linearLayoutChances.setVisibility(View.VISIBLE);
            binding.tvScratchCardDescription.setVisibility(View.VISIBLE);

            SharedPreferences.Editor myEdit = sharedPreferences.edit();
            myEdit.remove("endTime");
            myEdit.apply();
            refreshChances();

            isTimerOn = false;
        }

    }

    private void refreshChances() {
        SharedPreferences sharedPreferences = mainActivity.getSharedPreferences("scratchChances", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putLong("chancesLeft", 20);
        editor.apply();
    }
}
