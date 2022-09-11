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
import com.wuda.wuxue.bean.Course;

public class CourseAdapter extends BaseQuickAdapter<Course, BaseViewHolder> {
    public CourseAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, Course course) {
        baseViewHolder.setText(R.id.item_content_tv, format(course));
    }

    private SpannableStringBuilder format(Course course) {
        SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append(course.getName(), new StyleSpan(Typeface.BOLD), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        builder.append("\n");
        builder.append(course.getTeacher() + " | " + course.getStartWeek() + "-" + course.getEndWeek(), new RelativeSizeSpan(0.75f), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        return builder;
    }
}
