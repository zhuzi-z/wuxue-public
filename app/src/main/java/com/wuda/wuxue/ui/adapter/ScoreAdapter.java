package com.wuda.wuxue.ui.adapter;

import android.graphics.Typeface;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.wuda.wuxue.R;
import com.wuda.wuxue.bean.CourseScore;

import java.text.DecimalFormat;

public class ScoreAdapter extends BaseQuickAdapter<CourseScore, BaseViewHolder> {

    private final DecimalFormat df = new DecimalFormat("#.#");

    public ScoreAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, CourseScore courseScore) {
        baseViewHolder.setText(R.id.item_content_tv, format(courseScore));
    }

    private SpannableStringBuilder format(CourseScore score) {
        SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append(score.getName() + " | " + df.format(score.getScore()) + " | " + df.format(score.getCredit()), new StyleSpan(Typeface.BOLD), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        builder.append("\n");
        builder.append(score.getSemester() + " | " + score.getType(), new RelativeSizeSpan(0.75f), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        return builder;
    };
}
