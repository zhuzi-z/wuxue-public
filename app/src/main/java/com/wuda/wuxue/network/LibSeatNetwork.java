package com.wuda.wuxue.network;

import androidx.annotation.NonNull;

import com.wuda.wuxue.bean.OptionPair;
import com.wuda.wuxue.bean.Room;
import com.wuda.wuxue.bean.Seat;
import com.wuda.wuxue.bean.SeatOnlineHistory;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class LibSeatNetwork {

    public static void login(ResponseHandler<List<OptionPair>> handler) {

        ResponseResult<List<OptionPair>> result = new ResponseResult<>();
        result.setFlag(ServerURL.LIB_SEAT_CAS);

        HttpClient.getWithCAS(ServerURL.LIB_SEAT_CAS, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                result.setException(e);
                handler.onHandle(result);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                if (response.networkResponse().request().url().host().equals("cas.whu.edu.cn")) {
                    result.setException(new LoginFailException());
                    handler.onHandle(result);
                    return;
                }
                try {
                    List<OptionPair> tokens = new ArrayList<>();

                    String html = response.body().string();
                    Document doc = Jsoup.parse(html);
                    // TOKEN：用于提交预约申请
                    // 如果这里发生异常，说明CAS验证通过，但是图书馆系统遭到限制（非正常使用后会触发限制）
                    Element element_SYNCHRONIZER_TOKEN = doc.getElementById("SYNCHRONIZER_TOKEN");
                    if (element_SYNCHRONIZER_TOKEN != null) {
                        String SYNCHRONIZER_TOKEN = element_SYNCHRONIZER_TOKEN.attr("value");
                        tokens.add(new OptionPair(SYNCHRONIZER_TOKEN, OptionPair.SYNCHRONIZER_TOKEN));
                    }
                    Element element_SYNCHRONIZER_URI = doc.getElementById("SYNCHRONIZER_URI");
                    if (element_SYNCHRONIZER_URI != null) {
                        String SYNCHRONIZER_URI = element_SYNCHRONIZER_URI.attr("value");
                        tokens.add(new OptionPair(SYNCHRONIZER_URI, OptionPair.SYNCHRONIZER_URI));
                    }
                    result.setData(tokens);
                } catch (IOException e) {
                    e.printStackTrace();
                    result.setException(e);
                }
                handler.onHandle(result);
            }
        });
    }

    public static void requestOnlineHistory(int offset, ResponseHandler<List<SeatOnlineHistory>> handler) {
        // 在线预约记录：用于取消预约
        ResponseResult<List<SeatOnlineHistory>> result = new ResponseResult<>();
        result.setFlag(ServerURL.LIB_SEAT_HISTORY);

        String param = "?offset=" + offset + "&type=SEAT";

        HttpClient.get(ServerURL.LIB_SEAT_HISTORY + param, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                result.setException(e);
                handler.onHandle(result);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                List<SeatOnlineHistory> onlineHistoryList = new ArrayList<>();
                try {
                    ResponseBody body = response.body();
                    if (body == null)
                        throw new EmptyResponseException();

                    JSONObject json = new JSONObject(body.string());
                    result.setTotalPages(json.getInt("offset"));

                    Document doc = Jsoup.parse(json.getString("resStr"));
                    Elements items = doc.getElementsByTag("dl");
                    for (int i = 0; i < items.size(); i++) {
                        Element item = items.get(i);
                        String time = item.getElementsByTag("dt").get(0).text();
                        String title = item.getElementsByTag("a").get(0).attr("title");
                        String visaUrl = item.getElementsByTag("a").get(0).attr("href");
                        // 唯一ID，用于取消预约
                        Pattern visaIdPattern = Pattern.compile("(?<=id=)\\d+");
                        Matcher matcher = visaIdPattern.matcher(visaUrl);
                        String visaId = "";
                        if (matcher.find()) {
                            visaId = matcher.group();
                        }
                        // 当前状态：是否已经取消
                        int state = item.getElementsByTag("a").size() == 2 ? SeatOnlineHistory.STATE_CANCELED : SeatOnlineHistory.STATE_NORMAL;

                        onlineHistoryList.add(new SeatOnlineHistory(title, time, visaId, state));
                    }

                    result.setData(onlineHistoryList);
                    handler.onHandle(result);
                } catch (Exception e) {
                    result.setException(e);
                    handler.onHandle(result);
                }
            }
        });
    }

    public static void cancelOrder(String visaId, ResponseHandler<String> handler) {

        ResponseResult<String> result = new ResponseResult<>();
        result.setFlag(ServerURL.LIB_SEAT_CANCEL);

        HttpClient.get(ServerURL.LIB_SEAT_CANCEL + visaId, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                result.setException(e);
                handler.onHandle(result);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                result.setData("success");
                handler.onHandle(result);
            }
        });
    }

    public static void requestSeatsByRoom(String room, String date, ResponseHandler<List<Seat>> handler) {
        // 布局选座
        ResponseResult<List<Seat>> result = new ResponseResult<>();
        result.setFlag(ServerURL.LIB_SEAT_QUERY_BY_ROOM);

        HttpClient.get(ServerURL.LIB_SEAT_QUERY_BY_ROOM + "?room=" + room + "&date=" +date, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                result.setException(e);
                handler.onHandle(result);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                try {
                    List<Seat> seatList = new ArrayList<>();
                    ResponseBody body = response.body();
                    if (body == null)
                        throw new EmptyResponseException();
                    Document doc = Jsoup.parse(body.string());
                    // 获取行号/列号，重建布局
                    Elements rows = doc.getElementsByTag("ul");
                    for (int r = 0; r < rows.size(); r++) {
                        Elements cols = rows.get(r).getElementsByTag("li");
                        for (int c = 0; c < cols.size(); c++) {
                            Element seat = cols.get(c);
                            if (!seat.hasAttr("id"))
                                continue;
                            Elements link = seat.getElementsByTag("a");
                            int type = parseType(link.get(0).attr("class"), 1);
                            String id = seat.attr("id");
                            String num = seat.getElementsByTag("code").text();

                            seatList.add(new Seat(id, r, c, num, type));
                        }
                    }
                    result.setData(seatList);
                    handler.onHandle(result);
                } catch (Exception e) {
                    result.setException(e);
                    handler.onHandle(result);
                }
            }
        });
    }

    public static void requestSeatsByOption(Map<String, String> param, ResponseHandler<List<Seat>> handler) {
        // 自由选座
        ResponseResult<List<Seat>> result = new ResponseResult<>();
        result.setFlag(ServerURL.LIB_SEAT_QUERY_SEAT);

        StringBuilder builder = new StringBuilder();
        for (String key: param.keySet()) {
            builder.append(key).append("=").append(param.get(key)).append("&");
        }
        builder.deleteCharAt(builder.length()-1);

        HttpClient.get(ServerURL.LIB_SEAT_QUERY_SEAT + "?" + builder, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                result.setException(e);
                handler.onHandle(result);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {

                List<Seat> seatList = new ArrayList<>();
                int offset;

                try {
                    ResponseBody body = response.body();
                    if (body == null)
                        throw new EmptyResponseException();
                    JSONObject json = new JSONObject(body.string());
                    offset = json.getInt("offset");
                    Element seatHtml = Jsoup.parse(json.getString("seatStr"));
                    for (Element s: seatHtml.getElementsByTag("li")) {
                        int type = parseType(s.attr("class"), 0);
                        String id = s.attr("id");
                        String no = s.getElementsByTag("dt").text();
                        String room = s.getElementsByTag("dd").text();
                        seatList.add(new Seat(id, room, no, type));
                    }
                    result.setData(seatList);
                    result.setTotalPages(offset);
                } catch (Exception e) {
                    result.setException(e);
                }

                handler.onHandle(result);
            }
        });
    }

    public static void requestOptions(ResponseHandler<Map<String, List<OptionPair>>> handler) {
        // 按固定顺序：分馆、时、分、电源、窗户
        ResponseResult<Map<String, List<OptionPair>>> result = new ResponseResult<>();
        result.setFlag(ServerURL.LIB_SEAT);

        HttpClient.get(ServerURL.LIB_SEAT, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                result.setException(e);
                handler.onHandle(result);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {

                try {
                    Map<String, List<OptionPair>> options = new HashMap<>();
                    ResponseBody body = response.body();
                    if (body == null) {
                        throw new EmptyResponseException();
                    }
                    String data = body.string();
                    options.put(OptionPair.SEAT_BUILDING, handleOptionPairResponse(data, "options_building"));
                    options.put(OptionPair.SEAT_DURATION, handleOptionPairResponse(data, "options_hour"));
                    options.put(OptionPair.SEAT_START_MIN, handleOptionPairResponse(data, "options_startMin"));
                    options.put(OptionPair.SEAT_POWER, handleOptionPairResponse(data, "options_power"));
                    options.put(OptionPair.SEAT_WINDOW, handleOptionPairResponse(data, "options_window"));

                    result.setData(options);
                    handler.onHandle(result);
                } catch (Exception e) {
                    result.setException(e);
                    handler.onHandle(result);
                }
            }
        });
    }

    private static List<OptionPair> handleOptionPairResponse(String response, String element) {
        List<OptionPair> optionList = new ArrayList<>();
        Element buildings = Jsoup.parse(response).getElementById(element);
        assert buildings != null;
        for (Element b: buildings.getAllElements()) {
            if (b.hasAttr("value")) {
                if (b.text().length() != 0) {
                    optionList.add(new OptionPair(b.attr("value"), b.text()));
                }
            }
        }
        return optionList;
    }

    public static void requestRooms(String building, ResponseHandler<List<Room>> handler) {

        ResponseResult<List<Room>> result = new ResponseResult<>();
        result.setFlag(ServerURL.LIB_SEAT_GET_ROOM);

        HttpClient.get(ServerURL.LIB_SEAT_GET_ROOM + "?id=" + building, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                result.setException(e);
                handler.onHandle(result);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {

                try {
                    List<Room> roomList = new ArrayList<>();
                    roomList.add(new Room("null", building, "不限房间"));
                    ResponseBody body = response.body();
                    if (body == null) {
                        throw new EmptyResponseException();
                    }
                    Element rooms = Jsoup.parse(body.string());
                    for (Element r : rooms.getElementsByTag("a")) {
                        if (r.hasAttr("value")) {
                            if (r.text().length() != 0) {
                                roomList.add(new Room(r.attr("value"), building, r.text()));
                            }
                        }
                    }

                    result.setData(roomList);
                    handler.onHandle(result);
                } catch (Exception e) {
                    result.setException(e);
                    handler.onHandle(result);
                }
            }
        });
    }

    private static int parseType(String type, int sys) {

        // 自选 free free_both(window|power) free_power （绿） using(灰) order(黄)，leave
        // 布局 idle idle_both idle_window idle_power(绿) inuse（红） ... leave(暂离，黄) agreement(被预约，浅灰) noUsre(不可用，深灰)

        String[][] type1_name;
        if (sys == 0) { // 自由选座
            type1_name = new String[][]{
                    {"free"},
                    {"using"},
                    {"order"},
                    {"leave"}
            };
        } else { // 布局选座
            type1_name = new String[][]{{"idle"},
                    {"usre", "inuse"}, // 两种
                    {"agreement"},
                    {"leave"},
                    {"noUsre"}
            };
        }
        int[] type1_value = new int[] {Seat.FREE, Seat.USING, Seat.ORDERED, Seat.LEFT, Seat.DISABLE};

        String[] type2_name = new String[]{"power", "window", "both"};
        int[] type2_value = new int[] {Seat.POWER, Seat.WINDOW, Seat.POWER_WINDOW};

        int iType = 0;
        for (int i=0; i<type1_name.length; ++i) {
            boolean found = false;
            for (int j=0; j<type1_name[i].length; ++j) {
                if (type.startsWith(type1_name[i][j])) {
                    iType |= type1_value[i];
                    found = true;
                    break;
                }
            }
            if (found)
                break;
        }

        for (int i=0; i<3; ++i) {
            if (type.endsWith(type2_name[i])) {
                iType |= type2_value[i];
                break;
            }
        }

        return iType;
    }

    public static void requestStartTime(String seat, String date, ResponseHandler<List<OptionPair>> handler) {

        ResponseResult<List<OptionPair>> result = new ResponseResult<>();

        String param = "?id=" + seat + "&date=" + date;

        HttpClient.get(ServerURL.LIB_SEAT_START_TIME + param, new Callback() {
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
                    result.setData(handlerTimeResponse(body.string()));
                    handler.onHandle(result);
                } catch (Exception e) {
                    result.setException(e);
                    handler.onHandle(result);
                }
            }
        });
    }

    public static void requestEndTime(String seat, String date, String startTime, ResponseHandler<List<OptionPair>> handler) {

        ResponseResult<List<OptionPair>> result = new ResponseResult<>();
        result.setFlag(ServerURL.LIB_SEAT_END_TIME);

        String param = "?start=" + startTime + "&seat=" + seat + "&date=" + date;

        HttpClient.get(ServerURL.LIB_SEAT_END_TIME + param, new Callback() {
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
                    result.setData(handlerTimeResponse(body.string()));
                    handler.onHandle(result);
                } catch (Exception e) {
                    result.setException(e);
                    handler.onHandle(result);
                }
            }
        });
    }

    private static List<OptionPair> handlerTimeResponse(String response) {
        List<OptionPair> timeList = new ArrayList<>();

        Document doc = Jsoup.parse(response);
        for (Element time: doc.getElementsByTag("a")) {
            String name = time.text();
            String value = time.attr("time");
            timeList.add(new OptionPair(value, name));
        }
        return timeList;
    }

    public static void orderSeat(FormBody form, ResponseHandler<String> handler) {

        ResponseResult<String> result = new ResponseResult<>();
        result.setFlag(ServerURL.LIB_SEAT_ORDER);

        HttpClient.post(ServerURL.LIB_SEAT_ORDER, form, new Callback() {
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
                    String text = body.string();
                    Document doc = Jsoup.parse(text);
                    Elements elements = doc.getElementsByClass("layoutSeat");
                    if (!elements.isEmpty()) {
                        Element contentElement = elements.get(0);
                        String content = contentElement.html().replaceAll("dd", "p");
                        // 有换行符匹配
                        content = content.replaceAll("(?s)<div.*?</div>", "");
                        result.setData(content);
                        handler.onHandle(result);
                    }
                } catch (Exception e) {
                    result.setException(e);
                    handler.onHandle(result);
                }
            }
        });
    }
}
