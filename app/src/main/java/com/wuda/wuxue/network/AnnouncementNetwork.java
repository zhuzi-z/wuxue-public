package com.wuda.wuxue.network;

import androidx.annotation.NonNull;

import com.wuda.wuxue.bean.Announcement;
import com.wuda.wuxue.util.NetUtility;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class AnnouncementNetwork {
    public static void requestAnnouncementList(Integer page, ResponseHandler<List<Announcement>> handler) {

        ResponseResult<List<Announcement>> result = new ResponseResult<>();

        String url;
        if (page == -1) {
            url = ServerURL.ANNOUNCEMENT;
        } else {
            url = ServerURL.ANNOUNCEMENT_BASE + page + ".htm";
        }
        result.setFlag(url);

        HttpClient.get(url, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                result.setException(e);
                handler.onHandle(result);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {

                try {
                    ResponseBody body = response.body();
                    if (body == null) {
                        throw new EmptyResponseException();
                    }

                    String data = body.string();

                    Document doc = Jsoup.parse(data);
                    // 页码
                    if (doc.getElementById("fanye46693") == null)
                        throw new ParseResponseException();
                    String footer = doc.getElementById("fanye46693").text();
                    // 共2103条  2/106
                    Matcher numMatcher = Pattern.compile("\\d+").matcher(footer);
                    List<Integer> numbers = new ArrayList<>();
                    while (numMatcher.find()) {
                        numbers.add(Integer.parseInt(numMatcher.group()));
                    }
                    int rowCount = numbers.get(0);
                    int currentPage = numbers.get(1);
                    int totalPage = numbers.get(2);
                    result.setTotalPages(totalPage);
                    /*
                     动态隐藏
                    https://www.whu.edu.cn/tzgg/statlist.js
                        function showStaticListu9(pageCount, rowCount)
    {
        var duannums = 0;
        var start = pageCount- rowCount % pageCount;
        var duannums = 0;
        var start = pageCount- rowCount % pageCount;
        for(i = start ; i < start + pageCount && i < 39; i ++)
        {
            duannums++;
            document.getElementById("lineu9_" + i).style.display="";
            if(document.getElementById("lineimgu9_" + i) != null)
                document.getElementById("lineimgu9_" + i).style.display="";
            if(duannums != 39 && duannums%5==0 && 20!=duannums)
            {
                //document.getElementById("duannumu9_" + i).style.display="";
            }
        }
    }
    showStaticListu9(pageCount,rowCount);
                     */
                    int pageCount = 20; // 来自 https://www.whu.edu.cn/tzgg/statlist.js ，比较固定
                    int start = pageCount - rowCount % pageCount; // 计算隐藏项
                    if (page == -1) { // 第一页没有这个JS
                        start = 0;
                    }
                    // 主要数据
                    Elements lis = doc.getElementsByClass("article").get(0).getElementsByTag("li");
                    List<Announcement> announcementList = new ArrayList<>();
                    // start + 1 : 有一个表头
                    for (int i=start + 1; i <= start + pageCount && i < 39 && i < lis.size(); i++) {
                        Announcement announcement = new Announcement();
                        Elements divs = lis.get(i).getElementsByTag("div");
                        if (divs.isEmpty())
                            throw new ParseResponseException();
                        announcement.title = divs.get(0).getElementsByTag("a").get(0).attr("title");
                        announcement.setUrl(ServerURL.SCHOOL_HOME + "/" + divs.get(0).getElementsByTag("a").get(0).attr("href"));
                        announcement.setDepartment(divs.get(1).text());
                        announcement.time = divs.get(2).text();
                        announcementList.add(announcement);
                    }

                    result.setData(announcementList);
                    handler.onHandle(result);
                } catch (Exception e) {
                    result.setException(e);
                    handler.onHandle(result);
                }
            }
        });
    }

    public static void requestAnnouncementContent(String url, ResponseHandler<String> handler) {

        ResponseResult<String> result = new ResponseResult<>();
        result.setFlag(url);

        HttpClient.get(url, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                result.setException(e);
                handler.onHandle(result);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {

                try {
                    ResponseBody body = response.body();
                    if (body == null)
                        throw new EmptyResponseException();
                    Document doc = Jsoup.parse(body.string());
                    Elements newsContents = doc.getElementsByAttributeValue("name", "_newscontent_fromname");

                    StringBuilder content = new StringBuilder();
                    if (newsContents.size() == 0) {
                        // 无权访问
                        if (doc.getElementsByClass("prompt").size() != 0) {
                            content.append(doc.getElementsByClass("prompt").html());
                        } else {
                            content.append(doc.text());
                        }
                    } else {
                        // 发布单位和时间本右对齐，但TextView不支持
                        Element contentElement = newsContents.get(0);
                        // 图片链接替换
                        Elements imgElements = contentElement.getElementsByTag("img");
                        for (Element img: imgElements) {
                            String link = img.attr("src");
                            if (!link.startsWith("http")) {
                                img.attr("src", NetUtility.getBaseUrl(url) + "/" + link);
                            }
                        }
                        // 有多种ID
                        Element vsb_content_6 = contentElement.getElementById("vsb_content_6");
                        Element vsb_content_2 = contentElement.getElementById("vsb_content_2");
                        Element vsb_content = contentElement.getElementById("vsb_content");
                        if (vsb_content_2 != null)
                            content.append(vsb_content_2.html());
                        if (vsb_content_6 != null)
                            content.append(vsb_content_6.html());
                        if (vsb_content != null)
                            content.append(vsb_content.html());
                        if (content.length() == 0)  // 找不到数据
                            throw new ParseResponseException();
                        // 附件
                        Elements attachElements = contentElement.getElementsByClass("attach");
                        if (attachElements.size() != 0) {
                            Element attachElement = attachElements.get(0);
                            for (Element href : attachElement.getElementsByTag("a")) {
                                String link = href.attr("href");
                                if (!link.startsWith("http")) {
                                    href.attr("href", NetUtility.getBaseUrl(url) + "/" + link);
                                }
                            }
                            content.append(attachElement.html());
                        }
                    }
                    result.setData(content.toString());
                    handler.onHandle(result);
                } catch (Exception e) {
                    result.setException(e);
                    handler.onHandle(result);
                }
            }
        });
    }
}
