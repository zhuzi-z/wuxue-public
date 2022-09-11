package com.wuda.wuxue.ui.adapter;

import android.text.SpannableStringBuilder;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.BaseLoadMoreModule;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.wuda.wuxue.R;
import com.wuda.wuxue.bean.BaseInfo;

public abstract class SimpleInfoAdapter<T> extends BaseQuickAdapter<T, BaseViewHolder> implements LoadMoreModule {

    public SimpleInfoAdapter(int layoutResId) {
        super(layoutResId);
    }


    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, T data) {
        baseViewHolder.setText(R.id.item_content_tv, format(data));
    }

    // 简单的文本信息，直接重载 format
    protected abstract SpannableStringBuilder format(T data);

    @NonNull
    @Override
    public BaseLoadMoreModule addLoadMoreModule(@NonNull BaseQuickAdapter<?, ?> baseQuickAdapter) {
        return new BaseLoadMoreModule(this);
    }

}
