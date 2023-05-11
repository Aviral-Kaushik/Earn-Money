package com.apphub.eaa2.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.apphub.eaa2.Models.Game;
import com.apphub.eaa2.R;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class GamesAdapter extends BaseAdapter {

    private final Context context;
    private final ArrayList<Game> gameArrayList;

    public GamesAdapter(Context context, ArrayList<Game> gameArrayList) {
        this.context = context;
        this.gameArrayList = gameArrayList;
    }

    @Override
    public int getCount() {
        return gameArrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        if (view == null) {
            LayoutInflater inflater
                    = (LayoutInflater)context.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.layout_snippet_game,
                    null);
        }

        ImageView gameImage = view.findViewById(R.id.game_image);
        TextView gameName = view.findViewById(R.id.game_name);
        TextView gameCoins = view.findViewById(R.id.game_coins);

        gameName.setText(gameArrayList.get(i).getGameName());

        gameCoins.setText(gameArrayList.get(i).getGameCoins());

        Glide.with(context)
                .load(gameArrayList.get(i).getImage())
                .into(gameImage);

        // Handle open link functionality here

        return view;
    }
}
