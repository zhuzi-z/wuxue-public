package com.wuda.wuxue.network;

import static com.wuda.wuxue.util.CourseUtility.randomLightHexColor;

import androidx.annotation.NonNull;

import com.wuda.wuxue.bean.Course;
import com.wuda.wuxue.bean.CourseScore;
import com.wuda.wuxue.util.CourseUtility;

import org.json.JSONArray;
import org.json.JSONException;
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

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Response;

public class UnderGraduateCourseNetwork {
    public static Map<String, String> parseTableParams(String html) {
        Document doc = Jsoup.parse(html);
        Element form = doc.getElementById("ajaxForm");

        if (form == null) return null;

        Elements selections = form.getElementsByTag("select");

        if (selections.size() != 2) return null;

        Map<String, String> params = new HashMap<>();
        for (Element sel: selections) {
            for (Element option: sel.getElementsByTag("option")) {
                if (option.attr("selected").equals("selected")) {
                    params.put(sel.attr("name"), option.attr("value"));
                    break;
                }
            }
        }

        return params;
    }

    public static void requestCourseList(String address, FormBody formBody, Map<String, String> header, ResponseHandler<List<Course>> handler) {

        ResponseResult<List<Course>> result = new ResponseResult<>();
        result.setFlag(address);

        HttpClient.postWithHeader(address, formBody, header, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                result.setException(e);
                handler.onHandle(result);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try {
                    String data = response.body().string();
                    JSONObject jsonObject = new JSONObject(data);
                    JSONArray courseArray = jsonObject.getJSONArray("kbList");
                    List<Course> courseList = new ArrayList<>();
                    for (int i=0; i<courseArray.length(); i++) {
                        JSONObject courseObject = courseArray.getJSONObject(i);
                        String name = courseObject.getString("kcmc");
                        int day = Integer.parseInt(courseObject.getString("xqj"));
                        String room = courseObject.getString("cdmc");
                        String teacher = courseObject.getString("xm");
                        String[] nodes = courseObject.getString("jcs").split("-");
                        int startNode = 0, endNode = 0;
                        if (nodes.length == 2) {
                            startNode = Integer.parseInt(nodes[0]);
                            endNode = Integer.parseInt(nodes[1]);
                        }
                        String[] weeks = courseObject.getString("zcd").split("-");
                        int startWeek = 0, endWeek = 0;
                        if (weeks.length == 2) {
                            startWeek = Integer.parseInt(weeks[0]);
                            endWeek = Integer.parseInt(weeks[1].substring(0, weeks[1].length()-1));
                        }
                        int type = Course.WEEK_TYPE_ALL;

                        Course course = new Course(
                                name,
                                day,
                                room,
                                teacher,
                                startNode, endNode,
                                startWeek, endWeek,
                                type
                        );
                        course.setCredit(Float.parseFloat(courseObject.getString("xf")));
                        courseList.add(course);
                    }

                    // 颜色分配（同课程颜色相同，颜色值随机）
                    Map<String, String> courseColorMap = new HashMap<>();
                    for (Course course : courseList) {
                        // 颜色
                        if (courseColorMap.containsKey(course.getName())) {
                            course.setColor(courseColorMap.get(course.getName()));
                        } else {
                            course.setColor(randomLightHexColor());
                            courseColorMap.put(course.getName(), course.getColor());
                        }
                    }

                    result.setData(courseList);
                    handler.onHandle(result);
                } catch (JSONException e) {
                    e.printStackTrace();
                    result.setException(e);
                    handler.onHandle(result);
                }
            }
        });
    }

    public static void requestScoreList(String address, FormBody formBody, Map<String, String> header, ResponseHandler<List<CourseScore>> handler){
        ResponseResult<List<CourseScore>> result = new ResponseResult<>();
        result.setFlag(address);

        HttpClient.postWithHeader(address, formBody, header, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                result.setException(e);
                handler.onHandle(result);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try {
                    String data = response.body().string();
                    JSONObject jsonObject = new JSONObject(data);
                    JSONArray scoreArray = jsonObject.getJSONArray("items");
                    List<CourseScore> scoreList = new ArrayList<>();
                    for (int i=0; i<scoreArray.length(); i++) {
                        JSONObject scoreObject = scoreArray.getJSONObject(i);
                        String type = scoreObject.getString("kcxzmc");
                        String semester = scoreObject.getString("xnmmc") + "学年 第" + scoreObject.getString("xqmmc") + "学期";
                        String name = scoreObject.getString("kcmc");
                        float credit = Float.parseFloat(scoreObject.getString("xf"));
                        float score = Float.parseFloat(scoreObject.getString("cj"));
                        float gradePoint = CourseUtility.toGradePointUndergraduate(score);
                        scoreList.add(new CourseScore(name, semester, credit, score, gradePoint, type));
                    }
                    result.setData(scoreList);
                    handler.onHandle(result);
                } catch (JSONException e) {
                    result.setException(e);
                    handler.onHandle(result);
                    e.printStackTrace();
                }
            }
        });
    }
}
