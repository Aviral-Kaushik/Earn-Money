package com.apphub.eaa2.Utils;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerViewMargin extends RecyclerView.ItemDecoration{

    private final int margin;

    /**
     * constructor
     * @param margin desirable margin size in px between the views in the recyclerView
     */
    public RecyclerViewMargin(@IntRange(from=0)int margin) {
        this.margin = margin;
    }

    /**
     * Set different margins for the items inside the recyclerView: no top margin for the first row
     * and no left margin for the first column.
     */
    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        int position = parent.getChildLayoutPosition(view);

        outRect.top = margin;
        outRect.bottom = margin;
//        //set right margin to all
//        outRect.right = margin;
//        //set bottom margin to all
//        outRect.left = margin;
    }
}
