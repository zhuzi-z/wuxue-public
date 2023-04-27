package com.wuda.wuxue.network;

import androidx.annotation.NonNull;

import com.wuda.wuxue.R;
import com.wuda.wuxue.WuXueApplication;
import com.wuda.wuxue.bean.SchoolCalendar;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class SchoolCalendarNetwork {
    public static void requestCalendarList(ResponseHandler<List<SchoolCalendar>> handler) {

        ResponseResult<List<SchoolCalendar>> result = new ResponseResult<>();
        result.setFlag(ServerURL.CALENDAR);

        HttpClient.get(ServerURL.CALENDAR, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                result.setException(e);
                handler.onHandle(result);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                ResponseBody body = response.body();
                if (body != null) {
                    String data = body.string();
                    Document doc = Jsoup.parse(data);
                    List<SchoolCalendar> calendarList = new ArrayList<>();

                    if (doc.getElementsByClass("substance_l").size() == 0) {
                        result.setException(new NullPointerException());
                    } else {
                        Element calendarItemElement = doc.getElementsByClass("substance_l").get(0);
                        for (Element item: calendarItemElement.getElementsByTag("a")) {
                            SchoolCalendar schoolCalendar = new SchoolCalendar();
                            schoolCalendar.setName(item.text());
                            schoolCalendar.setUrl(item.attr("href"));
                            calendarList.add(schoolCalendar);
                        }
                        result.setData(calendarList);
                    }
                } else {
                    result.setException(new EmptyResponseException());
                }
                handler.onHandle(result);
            }
        });
    }

    public static void requestCalendarContent(SchoolCalendar calendar, ResponseHandler<String> handler) {
        ResponseResult<String> result = new ResponseResult<>();
        result.setFlag(calendar.getUrl());

        HttpClient.get(calendar.getUrl(), new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                result.setException(e);
                handler.onHandle(result);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                try {
                    StringBuilder contentBuilder = new StringBuilder();
                    // 自适应宽度
                    contentBuilder.append("<!DOCTYPE html>" +
                            "<html>" +
                            "<head>" +
                            "<meta name=\"viewport\" content=\"width=device-width\">" +
                            "</head>" +
                            "<body>");
                    ResponseBody body = response.body();
                    if(body == null) {
                        throw new EmptyResponseException();
                    }
                    Element calendarTable = Jsoup.parse(body.string()).getElementById("vsb_content");
                    if (calendarTable == null) {
                        contentBuilder.append(WuXueApplication.getContext().getResources().getString(R.string.no_calendar));
                    } else {
                        // 可能为Html的表格，也可能为图片
                        for (Element table : calendarTable.getElementsByTag("table")) {
                            // 两个 table 宽度不一样 570 - 580
                            table.attr("width", "580");
                        }

                        for (Element imgTag : calendarTable.getElementsByTag("img")) {
                            String imgSrc = imgTag.attr("src");
                            imgTag.attr("src", "https://uc.whu.edu.cn/" + imgSrc);
                        }
                        contentBuilder.append(calendarTable.html());
                    }
                    contentBuilder.append("</body></html>");

                    result.setData(contentBuilder.toString());
                } catch (Exception e) {
                    result.setException(e);
                }

                handler.onHandle(result);
            }
        });
    }
}
