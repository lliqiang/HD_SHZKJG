package com.hengda.shzkjg.m.tool;/**
 * Created by lenovo on 2017/6/26.
 */

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * 创建人：lenovo
 * 创建时间：2017/6/26 14:23
 * 类描述：
 */
public class SpacesItemDecoration extends RecyclerView.ItemDecoration{
    private int space;

    public SpacesItemDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.top = space;
        outRect.bottom = space;


        // Add top margin only for the first item to avoid double space between items


    }
}
