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
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.apphub.eaa2.Activities.MainActivity;
import com.apphub.eaa2.Dialog.LoadingDialog;
import com.apphub.eaa2.R;
import com.apphub.eaa2.Utils.ApiLinks;
import com.apphub.eaa2.databinding.FragmentSpinBinding;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;
import java.util.Random;

public class SpinFragment extends Fragment {

    private static final String TAG = "AviralAPI";

    public FragmentSpinBinding binding;

    private final MainActivity mainActivity;

    public SpinFragment(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    // 100 = Jackpot
    final double[] sectors = {0.10, 0, 0.02, 0.05, 0.01, 0.05, 0.20, 100, 0.15, 0.25, 0.01, 0.05};
    final int[] sectorsDegree = new int[sectors.length];

    int randomSectorIndex = 0;
    boolean spinning = false;

    Random random = new Random();

    private int chancesLeft;

    private double earnedAmount;

    private LoadingDialog loadingDialog;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentSpinBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loadingDialog = new LoadingDialog(requireContext());

        getChances();

        binding.btnSpin.setOnClickListener(view1 -> startSpin());

        binding.icBack.setOnClickListener(view1 -> mainActivity.showHomeFragment());
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

            Log.d(TAG, "getChances: Into if of getChances()");

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

                decrementChances();

                getChances();

                updateUserBalance(earnedAmount);

                spinning = false;

                binding.spinningWheel.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_spining_wheel));

                binding.btnSpin.setOnClickListener(view1 -> startSpin());

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

            binding.chances.setText(String.valueOf(chancesLeft - 1));

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

        editor.putInt("chancesLeft", 20);
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

    private void updateUserBalance(double value){
        String uid = mainActivity.getSharedPreferences("user", Context.MODE_PRIVATE).getString("uid", "");
        String balance_add = String.valueOf(value);

        AndroidNetworking.post(ApiLinks.UPDATE_USER_BALANCE)
                .addBodyParameter("user_id", uid)
                .addBodyParameter("value", balance_add)
                .build().getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Log.d(TAG, "onResponse: Response in Adapter: " + response);

                        try {

                            String status = response.getString("status");

                            if (status.equals("updated")){

                                Log.d(TAG, "onResponse: User Balance Updated");

                                mainActivity.getUserBalance();
                                loadingDialog.dismiss();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d(TAG, "onResponse: Exception while updating user balance: " + e.getMessage());
                            loadingDialog.dismiss();
                        }

                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d(TAG, "onResponse: Error while updating user balance: " + anError.getMessage());
                        loadingDialog.dismiss();
                    }
                });

    }
}
