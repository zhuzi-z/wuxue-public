package com.wuda.wuxue.ui.adapter.provider;

import android.view.View;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.entity.node.BaseNode;
import com.chad.library.adapter.base.provider.BaseNodeProvider;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.wuda.wuxue.R;
import com.wuda.wuxue.bean.helper.RootNode;

public class RootNodeProvider extends BaseNodeProvider {
    @Override
    public int getItemViewType() {
        return 0;
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_section_head;
    }

    @Override
    public void convert(@NonNull BaseViewHolder baseViewHolder, BaseNode baseNode) {
        RootNode rootNode = (RootNode) baseNode;
        baseViewHolder.setText(R.id.item_section_head_textView, ((RootNode) baseNode).getTitle());
    }

    @Override
    public void onClick(@NonNull BaseViewHolder helper, @NonNull View view, BaseNode data, int position) {
        getAdapter().expandOrCollapse(position);
    }
}
