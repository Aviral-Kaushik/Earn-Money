package com.apphub.eaa2.Fragments;

import android.app.Dialog;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.anupkumarpanwar.scratchview.ScratchView;
import com.apphub.eaa2.Activities.MainActivity;
import com.apphub.eaa2.Dialog.LoadingDialog;
import com.apphub.eaa2.R;
import com.apphub.eaa2.Utils.AdsParameters;
import com.apphub.eaa2.Utils.ApiLinks;
import com.apphub.eaa2.databinding.FragmentScratchCardBinding;
import com.google.android.material.snackbar.Snackbar;
import com.unity3d.ads.IUnityAdsInitializationListener;
import com.unity3d.ads.IUnityAdsLoadListener;
import com.unity3d.ads.IUnityAdsShowListener;
import com.unity3d.ads.UnityAds;
import com.unity3d.ads.UnityAdsShowOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

public class ScratchCardFragment extends Fragment implements IUnityAdsInitializationListener {

    private static final String TAG = "AviralAPI";

    public FragmentScratchCardBinding binding;
    private final MainActivity mainActivity;
    private int chancesLeft;
    private LoadingDialog loadingDialog;
    private double wonAmount;
    private double lower;
    private double upper;

    public ScratchCardFragment(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    private final IUnityAdsLoadListener loadListener = new IUnityAdsLoadListener() {
        @Override
        public void onUnityAdsAdLoaded(String placementId) {
            UnityAds.show(requireActivity(), AdsParameters.rewardedAndroidAdUnitId, new UnityAdsShowOptions(), showListener);
        }

        @Override
        public void onUnityAdsFailedToLoad(String placementId, UnityAds.UnityAdsLoadError error, String message) {
            Log.d(TAG, "Unity Ads failed to load ad for " + placementId + " with error: [" + error + "] " + message);
        }
    };

    private final IUnityAdsShowListener showListener = new IUnityAdsShowListener() {
        @Override
        public void onUnityAdsShowFailure(String placementId, UnityAds.UnityAdsShowError error, String message) {
            Log.d(TAG, "Unity Ads failed to show ad for " + placementId + " with error: [" + error + "] " + message);
        }

        @Override
        public void onUnityAdsShowStart(String placementId) {
            Log.d(TAG, "onUnityAdsShowStart: " + placementId);
        }

        @Override
        public void onUnityAdsShowClick(String placementId) {
            Log.d(TAG, "onUnityAdsShowClick: " + placementId);
        }

        @Override
        public void onUnityAdsShowComplete(String placementId, UnityAds.UnityAdsShowCompletionState state) {
            Log.d(TAG, "onUnityAdsShowComplete: " + placementId);

        }
    };

    private void DisplayRewarded(){
        UnityAds.load(AdsParameters.rewardedAndroidAdUnitId, loadListener);
        UnityAds.show(mainActivity, "Rewarded_Android", new UnityAdsShowOptions(), showListener);
    }

    private void DisplayInterstitial(){
        UnityAds.load(AdsParameters.interstitialAndroidAdUnitId, loadListener);
        UnityAds.show(mainActivity, "Interstitial_Android", new UnityAdsShowOptions(), showListener);
    }

    @Override
    public void onInitializationComplete() {
        Log.d(TAG, "onInitializationComplete: Ads Initialization Complete");
    }

    @Override
    public void onInitializationFailed(UnityAds.UnityAdsInitializationError error, String message) {
        Log.d(TAG, "onInitializationFailed: Ads Initialization failed " + message);
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

        UnityAds.initialize(requireActivity().getApplicationContext(),
                AdsParameters.unityGameID, AdsParameters.testMode, this);

        loadingDialog = new LoadingDialog(requireContext());

        getChances();

        lower = 0.10;
        upper = 0.50;

        wonAmount = Math.random() * (upper - lower) + lower;
        binding.rewardWon.setText(String.format("$%s", String.format(Locale.US, "%.2f", wonAmount)));

        binding.icBack.setOnClickListener(view1 -> mainActivity.showHomeFragment());

        binding.scratchView.setRevealListener(new ScratchView.IRevealListener() {
            @Override
            public void onRevealed(ScratchView scratchView) {

                if (chancesLeft == 0){
                    binding.chances.setText(mainActivity.getString(R.string.no_chances));
                    Toast.makeText(mainActivity, "No chances left", Toast.LENGTH_SHORT).show();
                }
                else{

                    binding.scratchView.reveal();

                    DisplayRewarded();

                    Dialog dialog = new Dialog(mainActivity);
                    dialog.setContentView(R.layout.layout_claim_dialog);
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.setCancelable(false);

                    if (dialog.getWindow() != null) {
                        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                    }

                    dialog.show();

                    TextView earnAmount = dialog.findViewById(R.id.amount_won);
                    earnAmount.setText(String.format(Locale.US, "%.2f", wonAmount));

                    TextView btnCollect = dialog.findViewById(R.id.btn_collect);
                    btnCollect.setOnClickListener(view -> {

                        DisplayInterstitial();

                        decrementChances();

                        getChances();

                        updateUserBalance(wonAmount);

                        dialog.dismiss();

                        binding.scratchView.setRevealListener(this);

                        binding.scratchView.clear();
                        binding.scratchView.mask();

                    });

                }

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
            editor.apply();

            binding.chances.setText(String.valueOf(chancesLeft - 1));

            if (chancesLeft == 0) {

                binding.scratchView.setVisibility(View.INVISIBLE);
                binding.scratchCardBack.setVisibility(View.INVISIBLE);
                binding.tvScratchCardDescription.setVisibility(View.INVISIBLE);
                binding.linearLayoutChances.setVisibility(View.INVISIBLE);

                binding.ivNoRewards.setVisibility(View.VISIBLE);
                binding.tvRewards.setText(R.string.rewards_completed);

                binding.linearLayoutTime.setVisibility(View.VISIBLE);

                timerUtils();
            }
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

            CountDownTimer countDownTimer = new CountDownTimer(endTime - startTime, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    long seconds = millisUntilFinished / 1000;
                    long minutes = seconds / 60;
                    long hours = minutes / 60;
                    minutes = minutes % 60;
                    seconds = seconds % 60;
                    String time = String.format(Locale.US,"%02d:%02d:%02d", hours, minutes, seconds);

                    binding.timeLeft.setText(time);
                }

                @Override
                public void onFinish() {

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
                }
            };
            countDownTimer.start(); // Start the timer

        } else {

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
        }

    }

    private void getChances() {
        SharedPreferences chancesPreferences = mainActivity.getSharedPreferences("scratchChances", Context.MODE_PRIVATE);
        chancesLeft = chancesPreferences.getInt("chancesLeft", 20);

        binding.chances.setText(String.valueOf(chancesLeft));

        if (chancesLeft == 0) {

            binding.scratchView.setVisibility(View.INVISIBLE);
            binding.scratchCardBack.setVisibility(View.INVISIBLE);
            binding.tvScratchCardDescription.setVisibility(View.INVISIBLE);
            binding.linearLayoutChances.setVisibility(View.INVISIBLE);

            binding.ivNoRewards.setVisibility(View.VISIBLE);
            binding.tvRewards.setText(R.string.rewards_completed);

            binding.linearLayoutTime.setVisibility(View.VISIBLE);

            timerUtils();
        }
    }

    private void refreshChances() {
        SharedPreferences sharedPreferences = mainActivity.getSharedPreferences("scratchChances", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt("chancesLeft", 20);
        editor.apply();
    }

    private void updateUserBalance(double value){

        loadingDialog = new LoadingDialog(requireContext());
        loadingDialog.show();

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

                                wonAmount = Math.random() * (upper - lower) + lower;
                                binding.rewardWon.setText(String.format("$%s", String.format(Locale.US, "%.2f", wonAmount)));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();

                            Log.d(TAG, "onResponse: Exception while updating user balance: " + e.getMessage());

                            loadingDialog.dismiss();

                            Snackbar.make(
                                    binding.layoutScratch,
                                    "Cannot Update your balance at this moment",
                                    Snackbar.LENGTH_SHORT
                            ).show();
                        }

                    }

                    @Override
                    public void onError(ANError anError) {

                        Log.d(TAG, "onResponse: Error while updating user balance: " + anError.getMessage());

                        loadingDialog.dismiss();

                        Snackbar.make(
                                binding.layoutScratch,
                                "Server Error! Please try again later",
                                Snackbar.LENGTH_SHORT
                        ).show();

                    }
                });

        loadingDialog.dismiss();

    }
}
