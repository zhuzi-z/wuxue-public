package com.wuda.wuxue.ui.adapter;

import android.graphics.Typeface;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.BaseLoadMoreModule;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.wuda.wuxue.R;
import com.wuda.wuxue.bean.BookItem;

public class BookAdapter extends BaseQuickAdapter<BookItem, BaseViewHolder> implements LoadMoreModule {
    public BookAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, BookItem bookItem) {
        SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append(bookItem.getName(), new StyleSpan(Typeface.BOLD), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        builder.append("\n").append(bookItem.getAuthor(), new RelativeSizeSpan(0.75f), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        builder.append("\n").append(bookItem.getPublisher(), new RelativeSizeSpan(0.75f), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        baseViewHolder.setText(R.id.item_content_tv, builder);
    }

    @NonNull
    @Override
    public BaseLoadMoreModule addLoadMoreModule(@NonNull BaseQuickAdapter<?, ?> baseQuickAdapter) {
        return new BaseLoadMoreModule(this);
    }
}
