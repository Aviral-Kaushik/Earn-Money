package com.apphub.eaa2.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.apphub.eaa2.Fragments.HomeFragment;
import com.apphub.eaa2.Fragments.ProfileFragment;
import com.apphub.eaa2.Fragments.ReferFragment;
import com.apphub.eaa2.Fragments.ScratchCardFragment;
import com.apphub.eaa2.Fragments.SpinFragment;
import com.apphub.eaa2.R;
import com.apphub.eaa2.Utils.ApiLinks;
import com.apphub.eaa2.databinding.ActivityMainBinding;
import com.google.android.material.navigation.NavigationBarView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

public class MainActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener {

    private static final String TAG = "AviralAPI";

    private ActivityMainBinding binding;

    private final HomeFragment homeFragment = new HomeFragment(this);
    private final SpinFragment spinFragment = new SpinFragment(this);
    private final ScratchCardFragment scratchCardFragment = new ScratchCardFragment(this);
    private final ReferFragment referFragment = new ReferFragment(this);
    private final ProfileFragment profileFragment = new ProfileFragment(this);

    private final FragmentManager fragmentManager = getSupportFragmentManager();

    public Fragment activeFragment = homeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        AndroidNetworking.initialize(getApplicationContext());

        SharedPreferences sharedPreferences = getSharedPreferences(
                getString(R.string.check_shared_preferences), Context.MODE_PRIVATE);

        boolean isLogin = sharedPreferences.getBoolean("isLoggedIn", false);

        if (isLogin) {

            fragmentManager.beginTransaction()
                    .add(R.id.frame_container, homeFragment, "Home Fragment")
                    .commit();

            fragmentManager.beginTransaction()
                    .add(R.id.frame_container, spinFragment, "Spin Fragment")
                    .hide(spinFragment)
                    .commit();

            fragmentManager.beginTransaction()
                    .add(R.id.frame_container, scratchCardFragment, "Scratch Card Fragment")
                    .hide(scratchCardFragment)
                    .commit();

            fragmentManager.beginTransaction()
                    .add(R.id.frame_container, referFragment, "Refer Fragment")
                    .hide(referFragment)
                    .commit();

            fragmentManager.beginTransaction()
                    .add(R.id.frame_container, profileFragment, "Profile Fragment")
                    .hide(profileFragment)
                    .commit();

            binding.bottomNavigationBar.setOnItemSelectedListener(this);

            getUserData();
        } else {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

    }

    private void getUserData() {

        SharedPreferences sharedPreferences = getSharedPreferences(
                getString(R.string.check_shared_preferences), Context.MODE_PRIVATE);
        String email = sharedPreferences.getString("email", null);

        AndroidNetworking.post(ApiLinks.FETCH_DATA)
                .addBodyParameter("email", email)
                .build().getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Log.d(TAG, "onResponse: response: " + response);

                        try {

                            Log.d(TAG, "onResponse: Getting user data in main activity");

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

                            homeFragment.binding.username.setText(String.format("Hi, %s", name));
                            profileFragment.binding.username.setText(name);
                            referFragment.binding.referralCode.setText(referralCode);

                            Log.d(TAG, "onResponse: Adding user data in Shared Preference");

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

                            getUserBalance();

                        } catch (JSONException e) {
                            e.getMessage();
                            Log.d(TAG, "onResponse: Ecxception while fetching user data in main activity: " + e.getMessage());
                        }

                    }

                    @Override
                    public void onError(ANError anError) {

                        Log.d(TAG, "onError: Error while getting user data in main activity: " + anError.getMessage());

                    }
                });

    }

    public void getUserBalance() {

        String uid = getSharedPreferences("user", Context.MODE_PRIVATE).getString("uid", null);

        AndroidNetworking.post(ApiLinks.GET_USER_COINS)
                .addBodyParameter("user_id", uid)
                .build().getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Log.d(TAG, "onResponse: Response: " + response);

                        try {

                            float balance = (float) response.getDouble("balance");

                            String balance_text = "₹"+String.format(Locale.US, "%.2f", balance);

                            homeFragment.binding.usernamePrice.setText(balance_text);
                            profileFragment.binding.userBalance.setText(balance_text);
                            spinFragment.binding.usernameBalance.setText(balance_text);
                            scratchCardFragment.binding.usernameBalance.setText(balance_text);

                        } catch (JSONException e) {
                            e.getMessage();
                            Log.d(TAG, "onResponse: Ecxception while fetching user data in main activity: " + e.getMessage());
                        }

                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d(TAG, "onError: Error while getting user coins in main activity: " + anError.getMessage());
                    }
                });

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.menu1) {

            fragmentManager.beginTransaction()
                    .hide(activeFragment)
                    .show(homeFragment)
                    .commit();
            activeFragment = homeFragment;

        } else if (item.getItemId() == R.id.menu2) {

            fragmentManager.beginTransaction()
                    .hide(activeFragment)
                    .show(spinFragment)
                    .commit();
            activeFragment = spinFragment;

        } else if (item.getItemId() == R.id.menu3) {

            fragmentManager.beginTransaction()
                    .hide(activeFragment)
                    .show(scratchCardFragment)
                    .commit();
            activeFragment = scratchCardFragment;

        } else if (item.getItemId() == R.id.menu4) {

            fragmentManager.beginTransaction()
                    .hide(activeFragment)
                    .show(referFragment)
                    .commit();
            activeFragment = referFragment;

        } else if (item.getItemId() == R.id.menu5) {

            fragmentManager.beginTransaction()
                    .hide(activeFragment)
                    .show(profileFragment)
                    .commit();
            activeFragment = profileFragment;

        }

        return true;
    }

    public void showReferFragment() {
        fragmentManager.beginTransaction()
                .hide(activeFragment)
                .show(referFragment)
                .commit();
        activeFragment = referFragment;

        binding.bottomNavigationBar.setSelectedItemId(R.id.menu4);
    }

    public void showHomeFragment() {
        fragmentManager.beginTransaction()
                .hide(activeFragment)
                .show(homeFragment)
                .commit();
        activeFragment = homeFragment;

        binding.bottomNavigationBar.setSelectedItemId(R.id.menu1);
    }
}