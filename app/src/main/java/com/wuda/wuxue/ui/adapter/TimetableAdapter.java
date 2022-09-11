package com.wuda.wuxue.ui.adapter;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.google.android.material.textview.MaterialTextView;
import com.wuda.wuxue.R;
import com.wuda.wuxue.bean.Timetable;

public class TimetableAdapter extends BaseQuickAdapter<Timetable, BaseViewHolder> {
    // 课程表的所有表项管理
    Integer currentTableId;

    public TimetableAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, Timetable timeTable) {
        MaterialTextView tv = baseViewHolder.getView(R.id.item_content_tv);
        tv.setText(format(timeTable));
        if (currentTableId == timeTable.getId()) {
            @SuppressLint("UseCompatLoadingForDrawables") Drawable drawable = getContext().getDrawable(R.drawable.ic_check);
            drawable.setBounds(0,0 ,drawable.getMinimumWidth(), drawable.getMinimumHeight());
            tv.setCompoundDrawables(null, null, drawable, null);
        } else {
            tv.setCompoundDrawables(null, null, null, null);
        }
    }

    private SpannableStringBuilder format(Timetable table) {
        SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append(table.getName());
        builder.append("\n");
        builder.append(table.getStartDate(), new RelativeSizeSpan(0.75f), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        return builder;
    }

    public void setCurrentTableId(Integer currentTableId) {
        this.currentTableId = currentTableId;
    }
}
