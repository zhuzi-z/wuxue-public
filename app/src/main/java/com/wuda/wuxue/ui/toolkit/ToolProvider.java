package com.wuda.wuxue.ui.toolkit;

import com.wuda.wuxue.R;
import com.wuda.wuxue.bean.ToolEntry;
import com.wuda.wuxue.network.ServerURL;

import java.util.ArrayList;
import java.util.List;

public class ToolProvider {
    public static List<ToolEntry> getAllTools() {
        List<ToolEntry> tools = new ArrayList<>();

        tools.add(new ToolEntry(R.drawable.ic_announcement, "#234534", "通知公告", ServerURL.ANNOUNCEMENT, BaseAnnouncementFragment.class));
        tools.add(new ToolEntry(R.drawable.ic_movie, "#986764", "梅操电影", ServerURL.MOVIE, MovieFragment.class));
        tools.add(new ToolEntry(R.drawable.ic_calendar, "#abc344", "校历", ServerURL.CALENDAR, SchoolCalendarFragment.class));
        tools.add(new ToolEntry(R.drawable.ic_yellow_page, "#dacd5e", "电话本", ServerURL.YELLOW_PAGES, YellowPageFragment.class));
        tools.add(new ToolEntry(R.drawable.ic_lecture, "#324654", "论坛讲座", ServerURL.LECTURE, LectureFragment.class));
        // 会吞钱，我的200块啊，慎开
//        tools.add(new ToolEntry(R.drawable.ic_campus_card, "#234563", "校园卡", ServerURL.CAMPUS_CARD_PRE_LOGIN, CampusCardFragment.class));
        tools.add(new ToolEntry(R.drawable.ic_local_library, "#547634", "座位预约", ServerURL.CAS_REDIRECT + ServerURL.LIB_SEAT_CAS, LibSeatFragment.class));
        tools.add(new ToolEntry(R.drawable.ic_library_books, "#ac3242", "查找图书", ServerURL.LIB_SEARCH, BookSearchFragment.class));
        tools.add(new ToolEntry(R.drawable.ic_room, "#723d23", "空教室", ServerURL.FREE_ROOM, FreeRoomFragment.class));
        tools.add(new ToolEntry(R.drawable.ic_baseline_medical_services_24, "#fbecde", "核酸检测", ServerURL.NUCLEIC_ACID_TEST, ToolWebViewFragment.class));
        tools.add(new ToolEntry(R.drawable.ic_network, "#567633", "网络自助", ServerURL.NETWORK_SELF_SERVICE, ToolWebViewFragment.class));
        tools.add(new ToolEntry(R.drawable.ic_baseline_directions_bus_24, "#869d9d", "校车", ServerURL.CAS_REDIRECT + ServerURL.BUS, ToolWebViewFragment.class));
        tools.add(new ToolEntry(R.drawable.ic_baseline_auto_fix_high_24, "#951c48", "后勤维修", ServerURL.REPAIR_HOME, ToolWebViewFragment.class));
        return tools;
    }
}
