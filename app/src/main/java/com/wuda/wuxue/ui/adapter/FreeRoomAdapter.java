package com.wuda.wuxue.ui.adapter;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseNodeAdapter;
import com.chad.library.adapter.base.entity.node.BaseNode;
import com.wuda.wuxue.bean.helper.RootNode;
import com.wuda.wuxue.bean.helper.TextItemNode;
import com.wuda.wuxue.ui.adapter.provider.RootNodeProvider;
import com.wuda.wuxue.ui.adapter.provider.TextItemNodeProvider;

import java.util.List;

public class FreeRoomAdapter extends BaseNodeAdapter {

    public FreeRoomAdapter() {
        super();
        addFullSpanNodeProvider(new RootNodeProvider());
        addNodeProvider(new TextItemNodeProvider());
    }

    @Override
    protected int getItemType(@NonNull List<? extends BaseNode> list, int i) {
        BaseNode node = list.get(i);
        if (node instanceof RootNode) {
            return 0;
        } else if (node instanceof TextItemNode) {
            return 1;
        }
        return -1;
    }
}
