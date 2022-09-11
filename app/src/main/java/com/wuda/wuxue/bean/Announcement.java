package com.wuda.wuxue.bean;

import com.wuda.wuxue.ui.toolkit.BaseAnnouncementContentFragment;

public class Announcement extends BaseInfo {
    // content: 通过 detailURL 二次获取，唯一，等价于ID
    private String url;
    // 发布单位
    private String department;

    public Announcement() {
        category = CATEGORY_ANNOUNCEMENT;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getDepartment() {
        return department;
    }

    @Override
    public String getUniqueId() {
        return url;
    }

    @Override
    public String getToolName() {
        return "公告详情";
    }

    @Override
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public Class<?> getTargetFragmentCls() {
        return BaseAnnouncementContentFragment.class;
    }
}
