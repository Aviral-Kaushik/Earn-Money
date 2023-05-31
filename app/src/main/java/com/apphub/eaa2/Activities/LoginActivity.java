package com.apphub.eaa2.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.apphub.eaa2.Dialog.LoadingDialog;
import com.apphub.eaa2.R;
import com.apphub.eaa2.Utils.ApiLinks;
import com.apphub.eaa2.Utils.PasswordEncrypt;
import com.apphub.eaa2.Utils.TokenPreference;
import com.apphub.eaa2.Utils.UpdateToken;
import com.apphub.eaa2.databinding.ActivityLoginBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "AviralAPI";

    private final int TOTAL_CHANCES = 1;

    private GoogleSignInClient mGoogleSignInClient;

    private static final int RX_SIGN_IN = 9001;

    private ActivityLoginBinding binding;

    private LoadingDialog loadingDialog;

    private boolean isGoogleLogin = false;

    private String photoUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        AndroidNetworking.initialize(getApplicationContext());

        loadingDialog = new LoadingDialog(this);

        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.google_oauth_key))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        binding.googleSignIn.setOnClickListener(view -> {

            loadingDialog.show();

            Intent signInIntent = mGoogleSignInClient.getSignInIntent();

            startActivityForResult(signInIntent, RX_SIGN_IN);

        });

        binding.login.setOnClickListener(view -> {
            if (binding.ediTextUserName.getText().toString().equals("")) {
                Snackbar.make(
                        binding.layoutLogin,
                        "Please enter username",
                        Snackbar.LENGTH_SHORT
                ).show();
            } else if (binding.ediTextPassword.getText().toString().equals("")) {
                Snackbar.make(
                        binding.layoutLogin,
                        "Please enter password",
                        Snackbar.LENGTH_SHORT
                ).show();
            } else {
                loadingDialog.show();
                checkIfEmailAlreadyRegistered();
            }
        });

        binding.ll2.setOnClickListener(view -> startActivity(new Intent(this, SignupActivity.class)));

        binding.forgotPassword.setOnClickListener(view -> startActivity(new Intent(this, ForgotPasswordActivity.class)));

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RX_SIGN_IN) {

            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {

                GoogleSignInAccount account = task.getResult(ApiException.class);

                loginUser(account);

            } catch (ApiException e) {
                e.printStackTrace();
                Log.d(TAG, "onActivityResult: Exception while sign in user with email: " + e.getMessage());
            }

        }
    }

    private void loginUser(GoogleSignInAccount account) {

        String email = account.getEmail();

        photoUrl = String.valueOf(account.getPhotoUrl());

        AndroidNetworking.post(ApiLinks.CHECK_EMAIL)
                .addBodyParameter("email", email)
                .build().getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Log.d(TAG, "onResponse: Response: " + response);

                        try {
                            String message = response.getString("message");

                            if (message.equals("Registered")) {

                                String token = TokenPreference.getInstance(LoginActivity.this).getDeviceToken();

                                new UpdateToken(getApplicationContext()).updateToken(email, token);

                                isGoogleLogin = true;

                                addUserDataToSharedPreferences(email);

                            } else if (message.equals("Email Not Registered")) {

                                SignupActivity signupActivity = new SignupActivity();

                                signupActivity.registerUserWithEmail(account);

                            } else {

                                loadingDialog.dismiss();

                                Snackbar.make(
                                        binding.layoutLogin,
                                        "Server Error",
                                        Snackbar.LENGTH_SHORT
                                ).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d(TAG, "onResponse: Exception while checking email registration: " + e.getMessage());

                            loadingDialog.dismiss();
                        }

                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d(TAG, "onError: Error while making api call for checking email: " + anError.getMessage());

                        loadingDialog.dismiss();
                    }
                });


    }

    private void checkIfEmailAlreadyRegistered() {

        String email = binding.ediTextUserName.getText().toString();

        AndroidNetworking.post(ApiLinks.CHECK_EMAIL)
                .addBodyParameter("email", email)
                .build().getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Log.d(TAG, "onResponse: Response: " + response);

                        try {

                            String message = response.getString("message");

                            if (message.equals("Registered")) {
                                loginWithEmailAndPassword();
                            } else if (message.equals("Email Not Registered")) {

                                loadingDialog.dismiss();

                                Snackbar.make(
                                        binding.layoutLogin,
                                        "Email not Registered please register",
                                        Snackbar.LENGTH_SHORT
                                ).show();

                            } else {
                                loadingDialog.dismiss();

                                Snackbar.make(
                                        binding.layoutLogin,
                                        "Server Error! Please try again",
                                        Snackbar.LENGTH_SHORT
                                ).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d(TAG, "onResponse: Exception while check email: " + e.getMessage());

                            loadingDialog.dismiss();
                        }

                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d(TAG, "onError: error while making api call for check email: " + anError.getMessage());
                    }
                });

    }

    private void loginWithEmailAndPassword() {

        String email = binding.ediTextUserName.getText().toString();
        String password = PasswordEncrypt.encrypt(binding.ediTextPassword.getText().toString());
        String token = TokenPreference.getInstance(this).getDeviceToken();

        AndroidNetworking.post(ApiLinks.LOGIN)
                .addBodyParameter("email", email)
                .addBodyParameter("password", password)
                .addBodyParameter("token", token)
                .build().getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            Log.d(TAG, "onResponse: Response: " + response);

                            String message = response.getString("message");

                            if (message.equals("success")) {

                                String token = TokenPreference.getInstance(LoginActivity.this).getDeviceToken();

                                new UpdateToken(getApplicationContext()).updateToken(email, token);

                                addUserDataToSharedPreferences(email);

                                isGoogleLogin = false;

                            } else if (message.equals("check email/password")) {

                                Snackbar.make(
                                        binding.layoutLogin,
                                        "Invalid Credentials",
                                        Snackbar.LENGTH_SHORT
                                ).show();

                            } else {

                                Snackbar.make(
                                        binding.layoutLogin,
                                        "Server Error! Please try again",
                                        Snackbar.LENGTH_SHORT
                                ).show();

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d(TAG, "onResponse: Exception while user login: " + e.getMessage());
                        }


                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d(TAG, "onError: Error while login user: " + anError.getMessage());
                    }
                });

    }

    private void addUserDataToSharedPreferences(String email) {

        addChancesToSharedPreferences();

        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.check_shared_preferences), Context.MODE_PRIVATE);
        SharedPreferences.Editor myEdit = sharedPreferences.edit();

        Log.d(TAG, "addUserDataToSharedPreferences: Adding data to Shared Preferences: " + email);
        myEdit.putBoolean("isLoggedIn", true);
        myEdit.putString("email", email);

        myEdit.apply();

        Log.d(TAG, "addUserDataToSharedPreferences: Getting UserData from the server");

        AndroidNetworking.post(ApiLinks.FETCH_DATA)
                .addBodyParameter("email", email)
                .build().getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            String name = response.getString("name");
                            String email = response.getString("email");
                            String uid = response.getString("uid");
                            int disabled = response.getInt("disabled");
                            int referred = response.getInt("referred");
                            String date = response.getString("date");
                            String time = response.getString("time");
                            String referredBy = response.getString("referred_by");
                            String referralCode = response.getString("referral_code");
                            float refer_earning = (float) response.getDouble("refer_earning");
                            float lifetime = (float) response.getDouble("lifetime");
                            String is_rewarded = response.getString("is_rewarded");

                            SharedPreferences sharedPreferences1 = getSharedPreferences("user", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences1.edit();
                            editor.putString("name", name);
                            editor.putString("email", email);
                            editor.putString("uid", uid);
                            editor.putInt("disabled", disabled);
                            editor.putInt("referred", referred);
                            editor.putString("date", date);
                            editor.putString("time", time);
                            editor.putString("referred_by", referredBy);
                            editor.putString("token", "-");
                            editor.putString("referral_code", referralCode);
                            editor.putFloat("refer_earning", (float) refer_earning);
                            editor.putFloat("lifetime", (float) lifetime);
                            editor.putString("is_rewarded", is_rewarded);
                            editor.putBoolean("isGoogleLogin", isGoogleLogin);

                            if (isGoogleLogin) {
                                editor.putString(getString(R.string.photoUrl), photoUrl);
                            }
                            editor.apply();

                            loadingDialog.dismiss();

                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.putExtra(getString(R.string.isGoogleLogin), isGoogleLogin);
                            startActivity(intent);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d(TAG, "onResponse: Exception while getting user data: " + e.getMessage());

                            loadingDialog.dismiss();
                        }

                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d(TAG, "onError: Error while fetching user data from server: " + anError.getMessage());
                        loadingDialog.dismiss();
                    }
                });

    }

    private void addChancesToSharedPreferences() {

        Log.d(TAG, "getChancesFromSharedPreference: Added Chances to Shared Preferences");

        SharedPreferences moneyBagSharedPreferences = getSharedPreferences(
                getString(R.string.money_bag_reward),
                Context.MODE_PRIVATE
        );

        SharedPreferences surpriseGiftSharedPreferences = getSharedPreferences(
                getString(R.string.surprise_gift_reward),
                Context.MODE_PRIVATE
        );

        SharedPreferences dailyBonusSharedPreferences = getSharedPreferences(
                getString(R.string.daily_bonus_reward),
                Context.MODE_PRIVATE
        );

        SharedPreferences earnRewardSharedPreferences = getSharedPreferences(
                getString(R.string.earn_reward_reward),
                Context.MODE_PRIVATE
        );

        SharedPreferences goldCoinSharedPreferences = getSharedPreferences(
                getString(R.string.gold_coin_reward),
                Context.MODE_PRIVATE
        );

        SharedPreferences walletMoneySharedPreferences = getSharedPreferences(
                getString(R.string.money_bag_reward),
                Context.MODE_PRIVATE
        );


        SharedPreferences.Editor moneyBagEditor = moneyBagSharedPreferences.edit();
        SharedPreferences.Editor surpriseGiftEditor = surpriseGiftSharedPreferences.edit();
        SharedPreferences.Editor dailyBonusEditor = dailyBonusSharedPreferences.edit();
        SharedPreferences.Editor earnRewardEditor = earnRewardSharedPreferences.edit();
        SharedPreferences.Editor goldCoinEditor = goldCoinSharedPreferences.edit();
        SharedPreferences.Editor walletMoneyEditor = walletMoneySharedPreferences.edit();

        moneyBagEditor.putInt(getString(R.string.chances_left), TOTAL_CHANCES);
        surpriseGiftEditor.putInt(getString(R.string.chances_left), TOTAL_CHANCES);
        dailyBonusEditor.putInt(getString(R.string.chances_left), TOTAL_CHANCES);
        earnRewardEditor.putInt(getString(R.string.chances_left), TOTAL_CHANCES);
        goldCoinEditor.putInt(getString(R.string.chances_left), TOTAL_CHANCES);
        walletMoneyEditor.putInt(getString(R.string.chances_left), TOTAL_CHANCES);

        moneyBagEditor.apply();
        surpriseGiftEditor.apply();
        dailyBonusEditor.apply();
        earnRewardEditor.apply();
        goldCoinEditor.apply();
        walletMoneyEditor.apply();

        Log.d("AviralAPI", "addChancesToSharedPreferences: Added all the chances in shared preferences");

    }
}