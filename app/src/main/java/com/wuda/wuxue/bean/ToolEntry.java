package com.wuda.wuxue.bean;

public class ToolEntry implements Tool {
    int iconId;
    String iconHintColor;
    String name;
    String url;
    // ToolActivity 加载的 Fragment
    Class<?> targetFragmentCls;

    public ToolEntry(int iconId, String iconHintColor, String name, String url, Class<?> targetFragmentCls) {
        this.iconId = iconId;
        this.iconHintColor = iconHintColor;
        this.name = name;
        this.url = url;
        this.targetFragmentCls = targetFragmentCls;
    }

    public int getIconId() {
        return iconId;
    }

    public String getIconHintColor() {
        return iconHintColor;
    }

    public String getToolName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Class<?> getTargetFragmentCls() {
        return targetFragmentCls;
    }
}
