package com.wuda.wuxue.network;

import androidx.annotation.NonNull;

import com.wuda.wuxue.bean.BookInfo;
import com.wuda.wuxue.bean.BookItem;

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

public class BookSearchNetwork {
    public static void requestBookList(String kw, String filed, Integer page, ResponseHandler<List<BookItem>> handler) {

        ResponseResult<List<BookItem>> result = new ResponseResult<>();

        String queryOption = "?kw=" + kw + "&filed=" + filed + "&page=" + page.toString();
        String url = ServerURL.LIB_SEARCH + queryOption;
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

                    // 获取所有记录数
                    int amount = 0;
                    String header = doc.getElementsByClass("header").text();
                    Pattern pattern = Pattern.compile("(?<=最大显示记录)(.*?)(?=条)");
                    Matcher matcher = pattern.matcher(header);
                    if (matcher.find()) {
                        amount = Integer.parseInt(matcher.group().trim());
                    }

                    List<BookItem> bookList = new ArrayList<>();

                    for (Element element : doc.getElementsByTag("li")) {
                        String title = element.getElementsByTag("h3").get(0).text();
                        String url = element.getElementsByClass("title").get(0).attr("href");

                        Element infoDiv = element.getElementsByTag("div").get(0);

                        infoDiv.select("br").append("\\n");
                        String[] info = infoDiv.text().split("\\\\n");

                        String author = info[0].trim();
                        String publisher = info[1].trim();

                        bookList.add(new BookItem(title, author, publisher, url));
                    }

                    result.setData(bookList);
                    result.setTotalPages(amount);
                    handler.onHandle(result);
                } catch (Exception e) {
                    result.setException(e);
                    handler.onHandle(result);
                }
            }
        });
    }

    public static void requestBookInfo(String url, ResponseHandler<BookInfo> handler) {

        ResponseResult<BookInfo> result = new ResponseResult<>();
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

                    Element info = Jsoup.parse(body.string()).getElementsByClass("basicInfo").get(0);
                    Elements detailList = info.getElementsByClass("detailList");
                    String title = detailList.get(0).text().substring(3);
                    String author = detailList.get(1).text().substring(3);
                    String keyWord = detailList.get(2).text().substring(4);
                    String publisher = detailList.get(3).text().substring(5);
                    String ISBN = detailList.get(4).text().substring(5);
                    String digest = detailList.get(5).text().substring(3);

                    BookInfo bookInfo = new BookInfo(title, author, keyWord, publisher, ISBN, digest);
                    // 馆藏信息
                    for (Element table : info.getElementsByTag("table")) {
                        // tr => th + td
                        Elements tds = table.getElementsByTag("td");
                        String status = tds.get(0).text();
                        String returnDate = tds.get(1).text();
                        String branch = tds.get(2).text();
                        String shelfId = tds.get(3).text();
                        String requestNum = tds.get(4).text();
                        String barCode = tds.get(5).text();
                        bookInfo.addCollectionInfo2List(new BookInfo.CollectionInfo(status, returnDate,
                                branch, shelfId, requestNum, barCode));
                    }

                    result.setData(bookInfo);
                    handler.onHandle(result);
                } catch (Exception e) {
                    result.setException(e);
                    handler.onHandle(result);
                }
            }
        });
    }
}

