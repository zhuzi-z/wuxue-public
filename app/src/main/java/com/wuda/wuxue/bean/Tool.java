package com.wuda.wuxue.bean;

import java.io.Serializable;

public interface Tool extends Serializable {
    // Toolkit
    // Toolbar's title
    String getToolName();
    // menu
    String getUrl();
    // navigation to
    Class<?> getTargetFragmentCls();
}
