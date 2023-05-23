package com.apphub.eaa2.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.apphub.eaa2.Adapter.GamesAdapter;
import com.apphub.eaa2.Models.Game;
import com.apphub.eaa2.Utils.ApiLinks;
import com.apphub.eaa2.databinding.ActivityGamesBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class GamesActivity extends AppCompatActivity {

    private static final String TAG = "AviralAPI";
    public ActivityGamesBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGamesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.icBack.setOnClickListener(view -> finish());

        Intent intent = getIntent();

        if (intent.hasExtra("balance")) {
            binding.usernameBalance.setText(intent.getStringExtra("balance"));
        }

        setupGridAdapter();
    }

    private void setupGridAdapter() {

        ArrayList<Game> gameArrayList = new ArrayList<>();

        AndroidNetworking.get(ApiLinks.GET_GAMES)
                .build().getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Log.d(TAG, "onResponse: response: " + response);

                            try {
                                JSONArray gameArray = response.getJSONArray("Data");

                                for (int i = 0; i < gameArray.length(); i++) {

                                    JSONObject object = gameArray.getJSONObject(i);

                                    gameArrayList.add(new Game(object.getString("imageLink"),
                                            object.getString("name"),
                                            "40 Coins",
                                            object.getString("url"))
                                    );

                                }


                            } catch (JSONException e) {
                                e.printStackTrace();
                                Log.d(TAG, "onResponse: Exception While getting game: " + e.getMessage());
                            }

                        GamesAdapter gamesAdapter = new GamesAdapter(GamesActivity.this, gameArrayList);
                        binding.gamesGridView.setAdapter(gamesAdapter);

                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d(TAG, "onError: Error while getting games: " + anError.getMessage());
                    }
                });



    }
}