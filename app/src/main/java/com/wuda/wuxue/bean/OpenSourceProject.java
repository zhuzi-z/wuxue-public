package com.wuda.wuxue.bean;

public class OpenSourceProject {
    // 所用的开源项目
    String name;
    String contributor;
    String url;
    String licence;
    String introduction;

    public OpenSourceProject(String name, String contributor, String url, String licence, String introduction) {
        this.name = name;
        this.contributor = contributor;
        this.url = url;
        this.licence = licence;
        this.introduction = introduction;
    }

    public String getName() {
        return name;
    }

    public String getContributor() {
        return contributor;
    }

    public String getUrl() {
        return url;
    }

    public String getLicence() {
        return licence;
    }

    public String getIntroduction() {
        return introduction;
    }
}