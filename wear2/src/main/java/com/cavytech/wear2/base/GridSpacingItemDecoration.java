package com.cavytech.wear2.base;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;

import com.cavytech.wear2.R;
import com.cavytech.wear2.util.DensityUtil;

/**
 * Created by longjining on 16/4/6.
 */
public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

    private int spanCount;
    private int itemWidth;

    public GridSpacingItemDecoration(Context context, int spanCount, int itemWidth) {
        this.spanCount = spanCount;
        this.itemWidth = DensityUtil.dip2px(context, itemWidth);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view); // item position
        int column = position % spanCount; // item column

        int  spacing = parent.getWidth() - this.itemWidth * 3;
        spacing = spacing / 2;

        outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
        outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
    }
}
