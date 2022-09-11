package com.wuda.wuxue.bean.adapterHelper;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.entity.node.BaseNode;

import java.util.List;

public class TextItemNode extends BaseNode {
    private final String name;

    public TextItemNode(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Nullable
    @Override
    public List<BaseNode> getChildNode() {
        return null;
    }
}
