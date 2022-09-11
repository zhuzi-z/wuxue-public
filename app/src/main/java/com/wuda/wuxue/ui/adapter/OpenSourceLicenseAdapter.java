package com.wuda.wuxue.ui.adapter;

import android.graphics.Typeface;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.wuda.wuxue.R;
import com.wuda.wuxue.bean.OpenSourceProject;

public class OpenSourceLicenseAdapter extends BaseQuickAdapter<OpenSourceProject, BaseViewHolder> {
    public OpenSourceLicenseAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, OpenSourceProject openSourceProject) {
        TextView tv = baseViewHolder.getView(R.id.item_content_tv);
        tv.setPadding(0, 24, 0, 24);
        baseViewHolder.setText(R.id.item_content_tv, buildOpenSourceProjectItem(openSourceProject));
    }

    private SpannableStringBuilder buildOpenSourceProjectItem(OpenSourceProject project) {
        SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append(
                project.getName() + " - " + project.getContributor(),
                new StyleSpan(Typeface.BOLD),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        builder.append(
                "\n" + project.getUrl() + "\n" + project.getLicence(),
                new RelativeSizeSpan(0.75f),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        return builder;
    }
}
