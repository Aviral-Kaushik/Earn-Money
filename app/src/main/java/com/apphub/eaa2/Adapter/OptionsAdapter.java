package com.apphub.eaa2.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.apphub.eaa2.Activities.MainActivity;
import com.apphub.eaa2.Dialog.LoadingDialog;
import com.apphub.eaa2.Models.OptionChances;
import com.apphub.eaa2.Models.Options;
import com.apphub.eaa2.R;
import com.apphub.eaa2.Utils.AdsParameters;
import com.apphub.eaa2.Utils.ApiLinks;
import com.apphub.eaa2.Utils.TimeUtils;
import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.unity3d.ads.IUnityAdsInitializationListener;
import com.unity3d.ads.IUnityAdsLoadListener;
import com.unity3d.ads.IUnityAdsShowListener;
import com.unity3d.ads.UnityAds;
import com.unity3d.ads.UnityAdsShowOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class OptionsAdapter
        extends RecyclerView.Adapter<OptionsAdapter.ViewHolder>
        implements IUnityAdsInitializationListener {

    private static final String TAG = "AviralAPI";

    private static final String TAG_ADD = "AviralAdsOptionsAdapter";

    private final int TOTAL_CHANCES = 1;

    private final ArrayList<Options> optionList;
    private final MainActivity mainActivity;

    private final OptionChances chances;

    private LoadingDialog loadingDialog;

    public OptionsAdapter(ArrayList<Options> optionList, MainActivity mainActivity, OptionChances chances) {
        this.optionList = optionList;
        this.mainActivity = mainActivity;
        this.chances = chances;
    }

    private final IUnityAdsLoadListener loadListener = new IUnityAdsLoadListener() {
        @Override
        public void onUnityAdsAdLoaded(String placementId) {
            UnityAds.show(mainActivity, AdsParameters.rewardedAndroidAdUnitId, new UnityAdsShowOptions(), showListener);
        }

        @Override
        public void onUnityAdsFailedToLoad(String placementId, UnityAds.UnityAdsLoadError error, String message) {
            Log.d(TAG_ADD, "Unity Ads failed to load ad for " + placementId + " with error: [" + error + "] " + message);
        }
    };

    private final IUnityAdsShowListener showListener = new IUnityAdsShowListener() {
        @Override
        public void onUnityAdsShowFailure(String placementId, UnityAds.UnityAdsShowError error, String message) {
            Log.d(TAG_ADD, "Unity Ads failed to show ad for " + placementId + " with error: [" + error + "] " + message);
        }

        @Override
        public void onUnityAdsShowStart(String placementId) {
            Log.d(TAG_ADD, "onUnityAdsShowStart: " + placementId);
        }

        @Override
        public void onUnityAdsShowClick(String placementId) {
            Log.d(TAG_ADD, "onUnityAdsShowClick: " + placementId);
        }

        @Override
        public void onUnityAdsShowComplete(String placementId, UnityAds.UnityAdsShowCompletionState state) {
            Log.d(TAG_ADD, "onUnityAdsShowComplete: " + placementId);
        }
    };

    @NonNull
    @Override
    public OptionsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.snippet_layout_options,
                parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OptionsAdapter.ViewHolder holder, int position) {

        UnityAds.initialize(mainActivity.getApplicationContext(),
                AdsParameters.unityGameID, AdsParameters.testMode, this);

        setAnimation(holder.itemView, holder.itemView.getContext());

        if (optionList.get(position).getChancesLeft() <= 0) {
            holder.optionButton.setBackground(AppCompatResources.getDrawable(
                    mainActivity, R.drawable.btn_grey_get_background
            ));

            holder.optionRewardAmount.setTextColor(Color.parseColor("#CCCCCC"));

            holder.optionButton.setOnClickListener(view -> Snackbar.make(
                    holder.itemView,
                    "All chances for this reward are finished. Please wait for some time",
                    Snackbar.LENGTH_SHORT
            ).show());
        }

        holder.optionTitle.setText(optionList.get(position).getOptionTitle());
        holder.optionDescription.setText(optionList.get(position).getOptionDescription());
        holder.optionRewardAmount.setText(String.format("$%s",
                optionList.get(position).getOptionEarningAmount()
        ));

        Log.d(TAG, "onBindViewHolder: Chances in adapter: " + optionList.get(position).getChancesLeft());

        Glide.with(holder.itemView.getContext())
                .load(optionList.get(position).getOptionImage())
                .into(holder.optionImage);

        loadingDialog = new LoadingDialog(mainActivity);

        if (optionList.get(position).getChancesLeft() > 0) {

            holder.optionButton.setOnClickListener(view -> DisplayRewardedAd(position, holder));

        } else {

            holder.optionButton.setBackground(AppCompatResources.getDrawable(
                    mainActivity, R.drawable.btn_grey_get_background
            ));

            holder.optionRewardAmount.setTextColor(Color.parseColor("#CCCCCC"));

            holder.optionButton.setOnClickListener(view -> Snackbar.make(
                    holder.itemView,
                    "All chances for this reward are finished. Please wait for some time",
                    Snackbar.LENGTH_SHORT
            ).show());

            checkForChancesRenewal(holder.optionButton, holder.optionRewardAmount, position, holder);

        }

    }


    @Override
    public int getItemCount() {
        return optionList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final ImageView optionImage;
        private final TextView optionTitle;
        private final TextView optionDescription;
        private final TextView optionRewardAmount;
        private final TextView optionButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            optionImage = itemView.findViewById(R.id.option_image);
            optionTitle = itemView.findViewById(R.id.option_title);
            optionDescription = itemView.findViewById(R.id.option_desc);
            optionRewardAmount = itemView.findViewById(R.id.options_reward_amount);
            optionButton = itemView.findViewById(R.id.btn_get);

        }
    }

    @Override
    public void onInitializationComplete() {
        Log.d(TAG_ADD, "onInitializationComplete: Ads Initialization Complete");
    }

    @Override
    public void onInitializationFailed(UnityAds.UnityAdsInitializationError error, String message) {
        Log.d(TAG_ADD, "onInitializationFailed: Ads Initialization failed " + message);
    }

    private void checkForChancesRenewal(TextView optionButton, TextView rewardAmount, int position, ViewHolder holder) {

        loadingDialog.show();

        if (chances.getMoneyBagChances() == 0) {

            SharedPreferences moneyBagSharedPreferences = mainActivity.getSharedPreferences(
                    mainActivity.getString(R.string.money_bag_reward),
                    Context.MODE_PRIVATE
            );

            long storedTime = moneyBagSharedPreferences.getLong(mainActivity.getString(R.string.money_bag_time), -1);

            boolean comparison = TimeUtils.compareTimeWithSixHours(storedTime, "moneyBag");

            if (comparison) {

                SharedPreferences.Editor moneyBagEditor = moneyBagSharedPreferences.edit();

                moneyBagEditor.putInt(
                        mainActivity.getString(R.string.chances_left),
                        TOTAL_CHANCES
                );

                moneyBagEditor.apply();

                toggleView(optionButton, rewardAmount,position, holder);
            }


        }

        if (chances.getSurpriseGiftChances() == 0) {

            SharedPreferences moneyBagSharedPreferences = mainActivity.getSharedPreferences(
                    mainActivity.getString(R.string.surprise_gift_reward),
                    Context.MODE_PRIVATE
            );

            long storedTime = moneyBagSharedPreferences.getLong(mainActivity.getString(R.string.surprise_gift_time), -1);

            boolean comparison = TimeUtils.compareTimeWithSixHours(storedTime, "surpriseGift");

            if (comparison) {

                SharedPreferences.Editor moneyBagEditor = moneyBagSharedPreferences.edit();

                moneyBagEditor.putInt(
                        mainActivity.getString(R.string.chances_left),
                        TOTAL_CHANCES
                );

                moneyBagEditor.apply();

                toggleView(optionButton, rewardAmount,position, holder);
            }


        }

        if (chances.getDailyBonusChances() == 0) {

            SharedPreferences moneyBagSharedPreferences = mainActivity.getSharedPreferences(
                    mainActivity.getString(R.string.daily_bonus_reward),
                    Context.MODE_PRIVATE
            );

            long storedTime = moneyBagSharedPreferences.getLong(mainActivity.getString(R.string.daily_bonus_time), -1);

            boolean comparison = TimeUtils.compareTimeWithSixHours(storedTime, "dailyBonus");

            if (comparison) {

                SharedPreferences.Editor moneyBagEditor = moneyBagSharedPreferences.edit();

                moneyBagEditor.putInt(
                        mainActivity.getString(R.string.chances_left),
                        TOTAL_CHANCES
                );

                moneyBagEditor.apply();

                toggleView(optionButton, rewardAmount,position, holder);
            }


        }

        if (chances.getEarnRewardChances() == 0) {

            SharedPreferences moneyBagSharedPreferences = mainActivity.getSharedPreferences(
                    mainActivity.getString(R.string.earn_reward_reward),
                    Context.MODE_PRIVATE
            );

            long storedTime = moneyBagSharedPreferences.getLong(mainActivity.getString(R.string.earn_reward_time), -1);

            boolean comparison = TimeUtils.compareTimeWithSixHours(storedTime, "earnReward");

            if (comparison) {

                SharedPreferences.Editor moneyBagEditor = moneyBagSharedPreferences.edit();

                moneyBagEditor.putInt(
                        mainActivity.getString(R.string.chances_left),
                        TOTAL_CHANCES
                );

                moneyBagEditor.apply();


                toggleView(optionButton, rewardAmount,position, holder);
            }


        }

        if (chances.getGoldCoinChances() == 0) {

            SharedPreferences moneyBagSharedPreferences = mainActivity.getSharedPreferences(
                    mainActivity.getString(R.string.gold_coin_reward),
                    Context.MODE_PRIVATE
            );

            long storedTime = moneyBagSharedPreferences.getLong(mainActivity.getString(R.string.gold_coin_time), -1);

            boolean comparison = TimeUtils.compareTimeWithSixHours(storedTime, "moneyBag");

            if (comparison) {

                SharedPreferences.Editor moneyBagEditor = moneyBagSharedPreferences.edit();

                moneyBagEditor.putInt(
                        mainActivity.getString(R.string.chances_left),
                        TOTAL_CHANCES
                );

                moneyBagEditor.apply();

                toggleView(optionButton, rewardAmount,position, holder);
            }


        }

        if (chances.getWalletMoneyChances() == 0) {

            SharedPreferences moneyBagSharedPreferences = mainActivity.getSharedPreferences(
                    mainActivity.getString(R.string.wallet_money_reward),
                    Context.MODE_PRIVATE
            );

            long storedTime = moneyBagSharedPreferences.getLong(mainActivity.getString(R.string.wallet_money_time), -1);

            boolean comparison = TimeUtils.compareTimeWithSixHours(storedTime, "moneyBag");

            if (comparison) {

                SharedPreferences.Editor moneyBagEditor = moneyBagSharedPreferences.edit();

                moneyBagEditor.putInt(
                        mainActivity.getString(R.string.chances_left),
                        TOTAL_CHANCES
                );

                moneyBagEditor.apply();

                toggleView(optionButton, rewardAmount,position, holder);
            }

        }


        loadingDialog.dismiss();

    }

    private void decrementChances(String rewardName, int position) {

        Log.d(TAG, "decrementChances: Decrementing Chances");

        switch (rewardName) {
            case "Money Bag":
                Log.d(TAG, "decrementChances: Opening SP of Money Bag");

                SharedPreferences candyCrushSharedPreferences = mainActivity.getSharedPreferences(
                        mainActivity.getString(R.string.money_bag_reward),
                        Context.MODE_PRIVATE
                );

                SharedPreferences.Editor candyCrushEditor = candyCrushSharedPreferences.edit();

                if (chances.getMoneyBagChances() > 1) {

                    candyCrushEditor.putInt(
                            mainActivity.getString(R.string.chances_left),
                            (chances.getMoneyBagChances() - 1)
                    );

                    optionList.get(position).setChancesLeft(optionList.get(position).getChancesLeft() - 1);

                } else {

                    candyCrushEditor.putInt(
                            mainActivity.getString(R.string.chances_left),
                            0
                    );

                    Log.d(TAG, "decrementChances: Adding Time to shared Preferences for Money Bag: " + TimeUtils.getCurrentTime());

                    candyCrushEditor.putLong(
                            mainActivity.getString(R.string.money_bag_time),
                            TimeUtils.getCurrentTime()
                    );

                    optionList.get(position).setChancesLeft(0);
                }

                candyCrushEditor.apply();

                Log.d(TAG, "decrementChances: money bag chances--");

                notifyItemChanged(position);

                break;
            case "Surprise Gift":
                Log.d(TAG, "decrementChances: Opening SP of Surprise Gift");

                SharedPreferences surpriseGiftSharedPreferences = mainActivity.getSharedPreferences(
                        mainActivity.getString(R.string.surprise_gift_reward),
                        Context.MODE_PRIVATE
                );

                SharedPreferences.Editor surpriseGiftEditor = surpriseGiftSharedPreferences.edit();

                if (chances.getSurpriseGiftChances() > 1) {

                    surpriseGiftEditor.putInt(
                            mainActivity.getString(R.string.chances_left),
                            (chances.getSurpriseGiftChances() - 1)
                    );

                    optionList.get(position).setChancesLeft(optionList.get(position).getChancesLeft() - 1);

                } else {

                    surpriseGiftEditor.putInt(
                            mainActivity.getString(R.string.chances_left),
                            0
                    );

                    Log.d(TAG, "decrementChances: Adding Time to shared Preferences for Surprise Gift: " + TimeUtils.getCurrentTime());

                    surpriseGiftEditor.putLong(
                            mainActivity.getString(R.string.surprise_gift_time),
                            TimeUtils.getCurrentTime()
                    );

                    optionList.get(position).setChancesLeft(0);
                }

                surpriseGiftEditor.apply();

                Log.d(TAG, "decrementChances: surprise gift chances--");

                notifyItemChanged(position);

                break;
            case "Daily Bonus":
                Log.d(TAG, "decrementChances: Opening SP of Daily Bonus");

                SharedPreferences dailyBonusSharedPreferences = mainActivity.getSharedPreferences(
                        mainActivity.getString(R.string.daily_bonus_reward),
                        Context.MODE_PRIVATE
                );

                SharedPreferences.Editor dailyBonusEditor = dailyBonusSharedPreferences.edit();

                if (chances.getDailyBonusChances() > 1) {

                    dailyBonusEditor.putInt(
                            mainActivity.getString(R.string.chances_left),
                            (chances.getDailyBonusChances() - 1)
                    );

                    optionList.get(position).setChancesLeft(optionList.get(position).getChancesLeft() - 1);

                } else {

                    dailyBonusEditor.putInt(
                            mainActivity.getString(R.string.chances_left),
                            0
                    );

                    Log.d(TAG, "decrementChances: Adding Time to shared Preferences for Daily Bonus: " + TimeUtils.getCurrentTime());

                    dailyBonusEditor.putLong(
                            mainActivity.getString(R.string.daily_bonus_time),
                            TimeUtils.getCurrentTime()
                    );

                    optionList.get(position).setChancesLeft(0);
                }

                dailyBonusEditor.apply();

                Log.d(TAG, "decrementChances: dailyBonus chances--");

                notifyItemChanged(position);

                break;
            case "Earn Reward":
                Log.d(TAG, "decrementChances: Opening SP of Earn Reward");

                SharedPreferences earnRewardSharedPreferences = mainActivity.getSharedPreferences(
                        mainActivity.getString(R.string.earn_reward_reward),
                        Context.MODE_PRIVATE
                );

                SharedPreferences.Editor earnRewardEditor = earnRewardSharedPreferences.edit();

                if (chances.getEarnRewardChances() > 1) {

                    earnRewardEditor.putInt(
                            mainActivity.getString(R.string.chances_left),
                            (chances.getEarnRewardChances() - 1)
                    );

                    optionList.get(position).setChancesLeft(optionList.get(position).getChancesLeft() - 1);

                } else {

                    earnRewardEditor.putInt(
                            mainActivity.getString(R.string.chances_left),
                            0
                    );

                    Log.d(TAG, "decrementChances: Adding Time to shared Preferences for Earn Reward: " + TimeUtils.getCurrentTime());

                    earnRewardEditor.putLong(
                            mainActivity.getString(R.string.earn_reward_time),
                            TimeUtils.getCurrentTime()
                    );

                    optionList.get(position).setChancesLeft(0);
                }

                earnRewardEditor.apply();

                Log.d(TAG, "decrementChances: Earn Reward chances--");

                notifyItemChanged(position);
                break;
            case "Gold Coin":
                Log.d(TAG, "decrementChances: Opening SP of Gold Coin");

                SharedPreferences goldCoinsSharedPreferences = mainActivity.getSharedPreferences(
                        mainActivity.getString(R.string.gold_coin_reward),
                        Context.MODE_PRIVATE
                );

                SharedPreferences.Editor goldCoinEditor = goldCoinsSharedPreferences.edit();

                if (chances.getGoldCoinChances() > 1) {

                    goldCoinEditor.putInt(
                            mainActivity.getString(R.string.chances_left),
                            (chances.getGoldCoinChances() - 1)
                    );

                    optionList.get(position).setChancesLeft(optionList.get(position).getChancesLeft() - 1);

                } else {

                    goldCoinEditor.putInt(
                            mainActivity.getString(R.string.chances_left),
                            0
                    );

                    Log.d(TAG, "decrementChances: Adding Time to shared Preferences for Gold Coin Bonus: " + TimeUtils.getCurrentTime());

                    goldCoinEditor.putLong(
                            mainActivity.getString(R.string.gold_coin_time),
                            TimeUtils.getCurrentTime()
                    );

                    optionList.get(position).setChancesLeft(0);
                }

                goldCoinEditor.apply();

                Log.d(TAG, "decrementChances: Gold Coin chances--");

                notifyItemChanged(position);

                break;
            case "Wallet Money":
                Log.d(TAG, "decrementChances: Opening SP of wallet money");

                SharedPreferences walletMoneySharedPreferences = mainActivity.getSharedPreferences(
                        mainActivity.getString(R.string.wallet_money_reward),
                        Context.MODE_PRIVATE
                );

                SharedPreferences.Editor walletMoneyEditor = walletMoneySharedPreferences.edit();

                if (chances.getWalletMoneyChances() > 1) {

                    walletMoneyEditor.putInt(
                            mainActivity.getString(R.string.chances_left),
                            (chances.getWalletMoneyChances() - 1)
                    );

                    optionList.get(position).setChancesLeft(optionList.get(position).getChancesLeft() - 1);

                } else {

                    walletMoneyEditor.putInt(
                            mainActivity.getString(R.string.chances_left),
                            0
                    );

                    Log.d(TAG, "decrementChances: Adding Time to shared Preferences for wallet money: " + TimeUtils.getCurrentTime());

                    walletMoneyEditor.putLong(
                            mainActivity.getString(R.string.wallet_money_time),
                            TimeUtils.getCurrentTime()
                    );

                    optionList.get(position).setChancesLeft(0);
                }

                walletMoneyEditor.apply();

                Log.d(TAG, "decrementChances: wallet money chances--");

                notifyItemChanged(position);

                break;
        }

    }

    private void toggleView(TextView optionButton,TextView rewardAmount, int position, ViewHolder holder) {

        optionButton.setBackground(AppCompatResources.getDrawable(
                mainActivity, R.drawable.btn_get_background
        ));

        rewardAmount.setTextColor(Color.parseColor("#E96200"));

        loadingDialog.dismiss();

        optionButton.setOnClickListener(view -> DisplayRewardedAd(position, holder));



    }

    private void setAnimation(View itemView, Context context) {
        Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);

        itemView.startAnimation(animation);
    }

    public void DisplayRewardedAd(int position, ViewHolder holder) {
        UnityAds.load(AdsParameters.rewardedAndroidAdUnitId, loadListener);
        UnityAds.show(mainActivity, "Rewarded_Android", new UnityAdsShowOptions(), showListener);

        Dialog dialog = new Dialog(mainActivity);
        dialog.setContentView(R.layout.layout_claim_dialog);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        dialog.show();

        TextView earnAmount = dialog.findViewById(R.id.amount_won);
        earnAmount.setText(String.valueOf(optionList.get(position).getOptionEarningAmount()));

        TextView btnCollect = dialog.findViewById(R.id.btn_collect);
        btnCollect.setOnClickListener(view1 -> {

            if ((optionList.get(position).getChancesLeft() - 1) == 0) {
                holder.optionButton.setBackground(AppCompatResources.getDrawable(
                        mainActivity, R.drawable.btn_grey_get_background
                ));

                holder.optionRewardAmount.setTextColor(Color.parseColor("#CCCCCC"));

                holder.optionButton.setOnClickListener(view -> Snackbar.make(
                        holder.itemView,
                        "All chances for this reward are finished. Please wait for some time",
                        Snackbar.LENGTH_SHORT
                ).show());
            }

            notifyItemChanged(position);

            loadingDialog.show();

            decrementChances(optionList.get(position).getOptionTitle(), position);

            DisplayInterstitial();

            updateUserBalance(optionList.get(position).getOptionEarningAmount(), holder);

            dialog.dismiss();

        });
    }

    private void DisplayInterstitial() {
        UnityAds.load(AdsParameters.interstitialAndroidAdUnitId, loadListener);
        UnityAds.show(mainActivity, "Interstitial_Android", new UnityAdsShowOptions(), showListener);
    }

    private void updateUserBalance(double value, ViewHolder holder) {
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

                            if (status.equals("updated")) {

                                Log.d(TAG, "onResponse: User Balance Updated");

                                mainActivity.getUserBalance();
                                loadingDialog.dismiss();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();

                            Log.d(TAG, "onResponse: Exception while updating user balance: " + e.getMessage());

                            loadingDialog.dismiss();

                            Snackbar.make(
                                    holder.itemView,
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
                                holder.itemView,
                                "Server Error! Please try again later",
                                Snackbar.LENGTH_SHORT
                        ).show();
                    }
                });

        loadingDialog.dismiss();

    }

}
