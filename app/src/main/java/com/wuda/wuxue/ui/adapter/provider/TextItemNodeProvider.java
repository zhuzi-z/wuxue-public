package com.wuda.wuxue.ui.adapter.provider;

import android.view.Gravity;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.entity.node.BaseNode;
import com.chad.library.adapter.base.provider.BaseNodeProvider;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.wuda.wuxue.R;
import com.wuda.wuxue.bean.helper.TextItemNode;

public class TextItemNodeProvider extends BaseNodeProvider {
    @Override
    public int getItemViewType() {
        return 1;
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_textview;
    }

    @Override
    public void convert(@NonNull BaseViewHolder baseViewHolder, BaseNode baseNode) {
        TextItemNode node = (TextItemNode) baseNode;
        ((TextView) baseViewHolder.getView(R.id.item_content_tv)).setGravity(Gravity.CENTER);
        ((TextView) baseViewHolder.getView(R.id.item_content_tv)).setTextSize(16);
        baseViewHolder.setText(R.id.item_content_tv, node.getName());
    }
}
