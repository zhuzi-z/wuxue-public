package com.wuda.wuxue.ui.adapter;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.wuda.wuxue.R;
import com.wuda.wuxue.bean.ToolEntry;

public class ToolkitAdapter extends BaseQuickAdapter<ToolEntry, BaseViewHolder> {
    public ToolkitAdapter(int layoutResId) {
        super(layoutResId);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, ToolEntry tool) {
        ImageView icon_iv = baseViewHolder.getView(R.id.tool_icon_imgView);
        icon_iv.setImageDrawable(getContext().getDrawable(tool.getIconId()));
        icon_iv.setColorFilter(Color.parseColor(tool.getIconHintColor()));
        baseViewHolder.setText(R.id.tool_title_textView, tool.getToolName());
    }
}
