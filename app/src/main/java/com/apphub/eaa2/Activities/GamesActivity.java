package com.apphub.eaa2.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.apphub.eaa2.Adapter.GamesAdapter;
import com.apphub.eaa2.Models.Game;
import com.apphub.eaa2.R;
import com.apphub.eaa2.databinding.ActivityGamesBinding;

import java.util.ArrayList;

public class GamesActivity extends AppCompatActivity {

    private ActivityGamesBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGamesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupGridAdapter();
    }

    private void setupGridAdapter() {

        ArrayList<Game> gameArrayList = new ArrayList<>();

        for (int i = 0; i < 15; i++) {
            gameArrayList.add(new Game(R.drawable.ic_candy_crush,
                    "Candy Crush",
                    "40 Coins",
                    ""));
        }

        GamesAdapter gamesAdapter = new GamesAdapter(this, gameArrayList);
        binding.gamesGridView.setAdapter(gamesAdapter);

    }
}