package com.wuda.wuxue.bean;

import com.google.gson.annotations.SerializedName;
import com.wuda.wuxue.ui.toolkit.LectureContentFragment;

public class Lecture extends BaseInfo {
    @SerializedName("id")
    public String url;
    public String reporter;
//    public String source;  // 来源（一般为发布院系的链接）
    public String content;
    public String introduction;
    @SerializedName("unit")
    public String organizer;
    public String poster;
    public String position;
    public String live_qrcode;
    public long start_time;
    public long end_time;

    public Lecture() {
        category = CATEGORY_LECTURE;
    }

    public String getReporter() {
        return reporter;
    }

    public String getContent() {
        return content;
    }

    public String getIntroduction() {
        return introduction;
    }

    public String getOrganizer() {
        return organizer;
    }

    public String getPoster() {
        if(poster.equals(""))
            return "";
        else
            return "https://www.whu.edu.cn/lectureApiGetImg.jsp?p=" + poster;
    }

    public String getPosition() {
        return position;
    }

    public String getLive_qrcode() {
        if(poster.equals(""))
            return "";
        else
            return "https://www.whu.edu.cn/lectureApiGetImg.jsp?p=" + live_qrcode;
    }

    @Override
    public String getUrl() {
        return "https://www.whu.edu.cn/xsrl/xsrlnr.htm?id=" + url;
    }

    public long getStart_time() {
        return start_time;
    }

    public long getEnd_time() {
        return end_time;
    }

    @Override
    public String getUniqueId() {
        return url;
    }

    @Override
    public String getToolName() {
        return "讲座详情";
    }

    @Override
    public Class<?> getTargetFragmentCls() {
        return LectureContentFragment.class;
    }
}
