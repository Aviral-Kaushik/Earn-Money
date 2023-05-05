package com.apphub.eaa2.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.apphub.eaa2.Fragments.HomeFragment;
import com.apphub.eaa2.Fragments.ScratchCardFragment;
import com.apphub.eaa2.Fragments.SpinFragment;
import com.apphub.eaa2.R;
import com.apphub.eaa2.databinding.ActivityMainBinding;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener {

    private ActivityMainBinding binding;

    private HomeFragment homeFragment = new HomeFragment(this);
    private SpinFragment spinFragment = new SpinFragment(this);

    private ScratchCardFragment scratchCardFragment = new ScratchCardFragment(this);

    private final FragmentManager fragmentManager = getSupportFragmentManager();

    public Fragment activeFragment = homeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        boolean isLogin = true;

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
            // Add and hide all others fragment here
            binding.bottomNavigationBar.setOnItemSelectedListener(this);
        } else {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

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

        } else if (item.getItemId() == R.id.menu5) {

        }

        return true;
    }
}