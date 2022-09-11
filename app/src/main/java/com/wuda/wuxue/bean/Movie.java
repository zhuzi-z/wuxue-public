package com.wuda.wuxue.bean;

import com.google.gson.annotations.SerializedName;
import com.wuda.wuxue.ui.toolkit.MovieInfoFragment;
import com.wuda.wuxue.network.ServerURL;

public class Movie extends BaseInfo {
    // ID 请求详细信息必须
    @SerializedName("ID")
    String id;
    @SerializedName("MOVIE_TYPE")
    String type;
    @SerializedName("PLAY_PLACE")
    String place;
    @SerializedName("ACTORS")
    String actors;
    @SerializedName("FILE_ID")
    String poster;

    public Movie() {
        category = CATEGORY_MOVIE;
    }

    @Override
    public String getUrl() {
        return ServerURL.MOVIE;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getPlace() {
        return place;
    }

    public String getActors() {
        return actors;
    }

    public String getPoster() {
        return ServerURL.MOVIE_POSTER + poster;
    }

    @Override
    public String getUniqueId() {
        return id;
    }

    @Override
    public String getToolName() {
        return "电影详情";
    }

    @Override
    public Class<?> getTargetFragmentCls() {
        return MovieInfoFragment.class;
    }
}
