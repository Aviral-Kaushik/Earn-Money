package com.apphub.eaa2.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
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
import com.apphub.eaa2.Utils.RandomAlphaNumericGenerator;
import com.apphub.eaa2.Utils.TokenPreference;
import com.apphub.eaa2.databinding.ActivitySignupBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

public class SignupActivity extends AppCompatActivity {

    private static final String TAG = "AviralAPI";

    private final int TOTAL_CHANCES = 30;

    private static final int RC_SIGN_IN = 9001;

    private GoogleSignInClient mGoogleSignInClient;

    private ActivitySignupBinding binding;
    private LoadingDialog loadingDialog;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        AndroidNetworking.initialize(getApplicationContext());

        loadingDialog = new LoadingDialog(this);

        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.google_oauth_key))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        binding.googleSignUp.setOnClickListener(view -> {
            loadingDialog.show();

            Intent signInIntent = mGoogleSignInClient.getSignInIntent();

            startActivityForResult(signInIntent, RC_SIGN_IN);
        });

        binding.signup.setOnClickListener(view -> {

            if (binding.ediTextUserName.getText().toString().equals("")) {
                Snackbar.make(
                        binding.layoutSignup,
                        "Please enter username",
                        Snackbar.LENGTH_SHORT
                ).show();
            } else if (binding.ediTextEmail.getText().toString().equals("")) {
                Snackbar.make(
                        binding.layoutSignup,
                        "Please enter email",
                        Snackbar.LENGTH_SHORT
                ).show();
            } else if (binding.ediTextPassword.getText().toString().equals("")) {
                Snackbar.make(
                        binding.layoutSignup,
                        "Please enter password",
                        Snackbar.LENGTH_SHORT
                ).show();
            } else if (binding.editTextConfirmPassword.getText().toString().equals("")) {
                Snackbar.make(
                        binding.layoutSignup,
                        "Please enter confirm password",
                        Snackbar.LENGTH_SHORT
                ).show();
            } else if (binding.ediTextPassword.getText().toString().equals(binding.editTextConfirmPassword.getText().toString())) {

                checkIfEmailAlreadyRegistered();

            } else {
                Snackbar.make(
                        binding.layoutSignup,
                        "Password did not match",
                        Snackbar.LENGTH_SHORT
                ).show();
            }

        });

        binding.ll2.setOnClickListener(view -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {

                GoogleSignInAccount account = task.getResult(ApiException.class);

                loginUser(account);

            } catch (ApiException e) {
                e.printStackTrace();
                Log.d(TAG, "onActivityResult: Exception while registering user using email: " + e.getMessage());
            }
        }
    }

    private void loginUser(GoogleSignInAccount account) {

        String email = account.getEmail();

        AndroidNetworking.post(ApiLinks.CHECK_EMAIL)
                .addBodyParameter("email", email)
                .build().getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            String message = response.getString("message");

                            if (message.equals("Registered")) {

                                addUserDataToSharedPreferences(email);

                            } else if (message.equals("Email Not Registered")) {

                                registerUserWithEmail(account);

                            } else {

                                loadingDialog.dismiss();

                                Snackbar.make(
                                        binding.layoutSignup,
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

        AndroidNetworking.post(ApiLinks.CHECK_EMAIL)
                .addBodyParameter("email", binding.ediTextUserName.getText().toString())
                .build().getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            String message = response.getString("message");

                            if (message.equals("Registered")) {

                                loadingDialog.dismiss();

                                Snackbar.make(
                                        binding.layoutSignup,
                                        "Email already registered",
                                        Snackbar.LENGTH_SHORT
                                ).show();

                                startActivity(new Intent(SignupActivity.this, LoginActivity.class));


                            } else if (message.equals("Email Not Registered")) {

                                registerUserWithEmailAndPassword();

                            } else {
                                loadingDialog.dismiss();

                                Snackbar.make(
                                        binding.layoutSignup,
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

    public void registerUserWithEmail(GoogleSignInAccount account) {

        String name = account.getDisplayName();
        String email = account.getEmail();
        String uid = RandomAlphaNumericGenerator.generateAlphaNumeric(10);
        String token = TokenPreference.getInstance(getApplication()).getDeviceToken();
        String password = PasswordEncrypt.encrypt(name + email);
        String referCode = RandomAlphaNumericGenerator.generateAlphaNumeric(6).toUpperCase();
        String referredBy = "-";
        String model = Build.MODEL;
        String brand = Build.BRAND;

        AndroidNetworking.post(ApiLinks.REGISTER)
                .addBodyParameter("name", name)
                .addBodyParameter("email", email)
                .addBodyParameter("uid", uid)
                .addBodyParameter("token", token)
                .addBodyParameter("password", password)
                .addBodyParameter("referral_code", referCode)
                .addBodyParameter("referred_by", referredBy)
                .addBodyParameter("model", model)
                .addBodyParameter("brand", brand)
                .build().getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Log.d(TAG, "onResponse: In on response");

                        try {

                            String message = response.getString("message");

                            Log.d(TAG, "onResponse: message: " + message);

                            if (message.equals("user registered successfully")) {
                                Snackbar.make(
                                        binding.layoutSignup,
                                        "Registration Successful",
                                        Snackbar.LENGTH_SHORT
                                ).show();

                                addUserDataToSharedPreferences(email);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d(TAG, "onResponse: Exception in registering user: " + e.getMessage());

                            loadingDialog.dismiss();
                        }

                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d(TAG, "onError: Error while making api call: " + anError.getMessage());
                        loadingDialog.dismiss();
                    }
                });



    }

    private void registerUserWithEmailAndPassword() {

        loadingDialog.show();

        String name = binding.ediTextUserName.getText().toString();
        String email = binding.ediTextEmail.getText().toString();
        String uid = RandomAlphaNumericGenerator.generateAlphaNumeric(10);
        String token = TokenPreference.getInstance(this).getDeviceToken();
        String password = PasswordEncrypt.encrypt(binding.ediTextPassword.getText().toString());
        String referCode = RandomAlphaNumericGenerator.generateAlphaNumeric(6).toUpperCase();
        String referredBy = "-";
        String model = Build.MODEL;
        String brand = Build.BRAND;

        Log.d(TAG, "registerUserWithEmailAndPassword: Making APi call");

        AndroidNetworking.post(ApiLinks.REGISTER)
                .addBodyParameter("name", name)
                .addBodyParameter("email", email)
                .addBodyParameter("uid", uid)
                .addBodyParameter("token", token)
                .addBodyParameter("password", password)
                .addBodyParameter("referral_code", referCode)
                .addBodyParameter("referred_by", referredBy)
                .addBodyParameter("model", model)
                .addBodyParameter("brand", brand)
                .build().getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Log.d(TAG, "onResponse: In on response");

                        try {

                            String message = response.getString("message");

                            Log.d(TAG, "onResponse: message: " + message);

                            if (message.equals("user registered successfully")) {
                                Snackbar.make(
                                        binding.layoutSignup,
                                        "Registration Successful",
                                        Snackbar.LENGTH_SHORT
                                ).show();

                                addUserDataToSharedPreferences(email);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d(TAG, "onResponse: Exception in registering user: " + e.getMessage());

                            loadingDialog.dismiss();
                        }

                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d(TAG, "onError: Error while making api call: " + anError.getMessage());
                        loadingDialog.dismiss();
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
                            editor.apply();

                            loadingDialog.dismiss();

                            startActivity(new Intent(SignupActivity.this, MainActivity.class));

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

        SharedPreferences candyCrushSharedPreferences = getSharedPreferences(
                getString(R.string.cancy_crush_reward),
                Context.MODE_PRIVATE
        );

        SharedPreferences.Editor candyCrushEditor = candyCrushSharedPreferences.edit();

        candyCrushEditor.putInt(getString(R.string.chances_left), TOTAL_CHANCES);

        candyCrushEditor.apply();

        Log.d("AviralAPI", "addChancesToSharedPreferences: Added all the chances in shared preferences");

    }
}