package com.wuda.wuxue.bean.adapterHelper;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.entity.node.BaseExpandNode;
import com.chad.library.adapter.base.entity.node.BaseNode;

import java.util.List;

public class RootNode extends BaseExpandNode {

    private final List<BaseNode> childNodes;
    private final String title;

    public RootNode(List<BaseNode> childNodes, String title) {
        this.childNodes = childNodes;
        this.title = title;
        setExpanded(false);
    }

    public String getTitle() {
        return title;
    }

    @Nullable
    @Override
    public List<BaseNode> getChildNode() {
        return childNodes;
    }
}
