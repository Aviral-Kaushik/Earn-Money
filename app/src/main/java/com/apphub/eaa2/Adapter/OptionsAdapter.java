package com.apphub.eaa2.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.apphub.eaa2.Models.Options;
import com.apphub.eaa2.R;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class OptionsAdapter extends RecyclerView.Adapter<OptionsAdapter.ViewHolder>{

    private final ArrayList<Options> optionList;

    public OptionsAdapter(ArrayList<Options> optionList) {
        this.optionList = optionList;
    }

    @NonNull
    @Override
    public OptionsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.snippet_layout_options,
                parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OptionsAdapter.ViewHolder holder, int position) {

        holder.optionTitle.setText(optionList.get(position).getOptionTitle());
        holder.optionDescription.setText(optionList.get(position).getOptionDescription());
        holder.optionRewardAmount.setText(String.format("$%s",
                optionList.get(position).getOptionEarningAmount()
        ));

        Glide.with(holder.itemView.getContext())
                .load(optionList.get(position).getOptionImage())
                .into(holder.optionImage);

        holder.optionButton.setOnClickListener(view -> Toast.makeText(
                holder.itemView.getContext(),
                "Option Clicked", Toast.LENGTH_SHORT).show());


    }

    private void setAnimation(View itemView, Context context) {
        Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);

        itemView.startAnimation(animation);
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

}
