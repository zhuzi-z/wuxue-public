package com.wuda.wuxue.ui.toolkit;

import com.wuda.wuxue.network.ServerURL;
import com.wuda.wuxue.util.NetUtility;

public class AnnouncementViewModelFactory {
    /*
    * 设计为：AnnouncementFragment, AnnouncementContentFragment为所有Announcement的UI
    * 通过增加AnnouncementNetwork,AnnouncementViewModel进行扩展
    * 公告页存在于很多部门，目前只实现了学校主页的信息
    * 研究生院，本科生院以及各院系主页未接入
    * */
    public static Class getViewModelClz(String url) {
        String domain = NetUtility.getDomain(url);

        if (domain.equals(NetUtility.getDomain(ServerURL.SCHOOL_HOME))) {
            return AnnouncementViewModel.class;
        }

        return null;
    }
}
