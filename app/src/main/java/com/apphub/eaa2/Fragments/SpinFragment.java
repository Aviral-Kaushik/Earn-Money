package com.apphub.eaa2.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.apphub.eaa2.Activities.MainActivity;
import com.apphub.eaa2.R;
import com.apphub.eaa2.databinding.FragmentSpinBinding;
import com.google.android.material.snackbar.Snackbar;

import java.util.Locale;
import java.util.Random;

public class SpinFragment extends Fragment {

    private static final String TAG = "AviralSpinPreferences";

    private FragmentSpinBinding binding;

    private final MainActivity mainActivity;

    public SpinFragment(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    final double[] sectors = {0, 0.02, 0.05, 0.01, 0.05, 0.20, 0.15, 0.25, 0.01, 0.05, 0.10, 100};
    final int[] sectorsDegree = new int[sectors.length];

    int randomSectorIndex = 0;
    boolean spinning = false;

    Random random = new Random();

    private int chancesLeft;

    private double earnedAmount;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentSpinBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getChances();

        binding.btnSpin.setOnClickListener(slideToActView -> startSpin());
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void getChances() {
        SharedPreferences chancesPreferences = mainActivity.getSharedPreferences("spinChances", Context.MODE_PRIVATE);
        chancesLeft = chancesPreferences.getInt("chancesLeft", 20);

        binding.chances.setText(String.valueOf(chancesLeft));

        if (chancesLeft == 0) {
            binding.btnSpin.setClickable(false);

            Log.d(TAG, "getChances: Into if of getChanses()");

            timerUtils();
        }
    }

    private void startSpin() {
        generateSectorDegree();

        if (chancesLeft > 0) {
            if (!spinning) {
                spin();
                spinning = true;
            }
        } else {
            Snackbar snackbar = Snackbar.make(binding.spinLayout,
                    "You Cannot Spin as 0 Chances Left",
                    Snackbar.LENGTH_SHORT);
            snackbar.show();
        }
    }

    private void spin() {
        randomSectorIndex = random.nextInt(sectors.length);

        int randomDegree = generateRandomDegreeToSpin();

        RotateAnimation rotateAnimation = new RotateAnimation(0, randomDegree,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);

        rotateAnimation.setDuration(3600);
        rotateAnimation.setFillAfter(true);

        rotateAnimation.setInterpolator(new DecelerateInterpolator());

        rotateAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                earnedAmount = sectors[sectors.length - (randomSectorIndex + 1)];

//                binding.usernameBalance.setText(String.format("₹%s",
//                        roundOfNumber(Double.parseDouble(String.valueOf(mainActivity.getBalance()))
//                                + earnedAmount)));

                decrementChances();

//                updateUserBalance(earnedAmount);

                spinning = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        binding.spinningWheel.startAnimation(rotateAnimation);

    }

    private void decrementChances() {
        SharedPreferences sharedPreferences = mainActivity.getSharedPreferences("spinChances", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if (chancesLeft > 0) {
            editor.putInt("chancesLeft", (chancesLeft - 1));
            editor.apply();

            chancesLeft = 0;

            binding.chances.setText(String.valueOf(chancesLeft));

            if (chancesLeft == 0) {
                timerUtils();
            }
        }

    }

    private void timerUtils() {

        Log.d(TAG, "timerUtils: time util started");

        SharedPreferences sharedPreferences = mainActivity.getSharedPreferences("spinTime", Context.MODE_PRIVATE);

        long startTime = System.currentTimeMillis();

        long endTime;

        if (sharedPreferences.contains("endTime")) {

            Log.d(TAG, "timerUtils: Found time in shared preferences");

            endTime = sharedPreferences.getLong("endTime", startTime + (2 * 60 * 60 * 1000));

            Log.d(TAG, "timerUtils: endTime in shared preferences " + endTime);

        } else {

            Log.d(TAG, "timerUtils: Adding endTime in preferences");

            startTime = System.currentTimeMillis();
            endTime = startTime + (2 * 60 * 60 * 1000);

            SharedPreferences.Editor myEdit = sharedPreferences.edit();
            myEdit.putLong("endTime", endTime);
            myEdit.apply();
            Log.d(TAG, "timerUtils: added endTime in shared preferences " + endTime);

        }

        if (System.currentTimeMillis() < endTime) {

            Log.d(TAG, "timerUtils: startTime inside counter: " + startTime);
            Log.d(TAG, "timerUtils: endTime inside counter: " + endTime);


            CountDownTimer countDownTimer = new CountDownTimer(endTime - startTime, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    long seconds = millisUntilFinished / 1000;
                    long minutes = seconds / 60;
                    long hours = minutes / 60;
                    minutes = minutes % 60;
                    seconds = seconds % 60;
                    String time = String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds);
                    // Update the UI with the remaining time
                    binding.btnSpin.setText(time);
                }

                @Override
                public void onFinish() {
                    // Handle the timer finish event
                    // This will be called when the countdown is finished
                    binding.btnSpin.setText(mainActivity.getString(R.string.spin));

                    SharedPreferences.Editor myEdit = sharedPreferences.edit();
                    myEdit.remove("endTime");
                    myEdit.apply();
                    refreshChances();

                }
            };
            countDownTimer.start(); // Start the timer

        } else {

            binding.btnSpin.setText(mainActivity.getString(R.string.spin));

            SharedPreferences.Editor myEdit = sharedPreferences.edit();
            myEdit.remove("endTime");
            myEdit.apply();
            refreshChances();

        }

    }

    private void refreshChances() {
        SharedPreferences sharedPreferences = mainActivity.getSharedPreferences("spinChances", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putLong("chancesLeft", 20);
        editor.apply();

        getChances();
    }

    private int generateRandomDegreeToSpin() {
        return (360 * sectors.length) + sectorsDegree[randomSectorIndex] + 10;
    }

    private void generateSectorDegree() {
        int sectorDegree = 360 / sectors.length;

        for (int i = 0; i < sectors.length; i++) {
            sectorsDegree[i] = (i + 1) * sectorDegree;
        }
    }
}
