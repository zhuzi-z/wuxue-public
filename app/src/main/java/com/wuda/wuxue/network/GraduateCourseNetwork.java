package com.wuda.wuxue.network;

import static com.wuda.wuxue.util.CourseUtility.randomLightHexColor;

import androidx.annotation.NonNull;

import com.wuda.wuxue.bean.Course;
import com.wuda.wuxue.bean.CourseScore;
import com.wuda.wuxue.util.CourseUtility;

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
import okhttp3.Response;
import okhttp3.ResponseBody;

public class GraduateCourseNetwork {
    //************************ 研究生 **************************************

    public static void requestGraduateSchedule(ResponseHandler<List<Course>> handler) {
        /*
        * 获取课表：对于相同的课程也视为不同的课程（数据库存储时有不同的ID），即每次上课的时间节点都算一门课
        * */

        ResponseResult<List<Course>> result = new ResponseResult<>();
        result.setFlag(ServerURL.GRADUATE_SCHEDULE);

        HttpClient.getWithCAS(ServerURL.GRADUATE_SCHEDULE, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {

                if (response.networkResponse().request().url().host().equals("cas.whu.edu.cn")) {
                    result.setFlag("LOGIN_FAIL");
                    handler.onHandle(result);
                    return;
                }

                ArrayList<Course> courseList = new ArrayList<>();
                // 通过构建一个 14 * 9 (含表头(1 + 13) * (2 + 7)) 的表来获取上课时间的节
                try {
                    ResponseBody body = response.body();
                    if (body == null)
                        throw new EmptyResponseException();
                    Document doc = Jsoup.parse(body.string());
                    Element table_con = doc.getElementsByClass("table_con").get(0);
                    Elements trs = table_con.getElementsByTag("tr");  // 每一行（共13行（节））

                    // 跨行元素使得下一行的列数减少 -> 用于定位列
                    boolean[][] hasContent = new boolean[14][9];

                    Pattern pattern = Pattern.compile(">(..*?)<");  // 同一个单元格内多门课程

                    for (int row = 0; row < 14; ++row) {
                        Elements tds = trs.get(row).getElementsByTag("td");
                        int skip = 0;
                        for (int col = 0; col < tds.size(); ++col) {
                            while (hasContent[row][col + skip]) {
                                ++skip;
                            }
                            // 跨行元素判断
                            if (tds.get(col).hasAttr("rowspan")) {
                                for (int r = 0; r < Integer.parseInt(tds.get(col).attr("rowspan")); ++r) {
                                    hasContent[row + r][col + skip] = true;
                                }
                            } else if (tds.get(col).text().length() > 0) {
                                hasContent[row][col + skip] = true;
                            }
                            // 表头（非课程信息长度最大为6）
                            if (tds.get(col).text().length() >= 7) {
                                // 多门课程分割
                                Matcher matcher = pattern.matcher(tds.get(col).toString());
                                while (matcher.find()) {
                                    String rawCourse = matcher.group(1);
                                    if (rawCourse != null)
                                        courseList.add(parserGraduateCourseItem(rawCourse, col + skip - 1));
                                }
                            }
                        }
                    }

                    // 颜色分配（同课程颜色相同，颜色值随机）
                    Map<String, String> courseColorMap = new HashMap<>();
                    for (Course course : courseList) {
                        // 按课时推断学分数（不准）
                        course.setCredit((float) (Math.round(course.getEndWeek() - course.getStartWeek() + 1) * (course.getEndNode() - course.getStartNode() + 1) / 16));
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
                } catch (Exception e) {
                    result.setException(e);
                    handler.onHandle(result);
                }
            }
        });
    }

    private static Course parserGraduateCourseItem(String rawCourse, int day) {

        String[] info = rawCourse.split(" ");

        // 课程名可能不规范，包含空格，将时间前部分归为课程名
        int weekNodeIdx = 0;
        StringBuilder rawName = new StringBuilder();
        while (weekNodeIdx < info.length) {
            if (info[weekNodeIdx].contains("周")) {
                break;
            }
            rawName.append(info[weekNodeIdx]);
            ++weekNodeIdx;
        }
        int nameEndIdx = 0;
        for (; nameEndIdx<rawName.length(); ++nameEndIdx) {
            if (Character.isDigit(rawName.charAt(nameEndIdx))) {
                break;
            }
        }
        String name = rawName.substring(0, nameEndIdx);

        // 1-16周1-2节
        // 周
        String week = info[weekNodeIdx].split("周")[0];
        int startWeek = Integer.parseInt(week.split("-")[0]);
        int endWeek = Integer.parseInt(week.split("-")[1]);
        // 节
        String node = info[weekNodeIdx].split("周")[1].split("节")[0];
        int startNode = Integer.parseInt(node.split("-")[0]);
        int endNode = Integer.parseInt(node.split("-")[1]);

        int type = 0;  // 单双周，0->每周
        String room = "";            // 教室
        String teacher = "";         // 老师

        switch (info.length - weekNodeIdx) {
            case 2:  // 课程名，时间，老师
                teacher = info[info.length-1];
                break;
            case 3:  // 课程名，时间，地点，老师
                room = info[info.length-2];
                teacher = info[info.length-1];
            default:
                break;
        }

        return new Course(
                name,
                day,
                room,
                teacher,
                startNode, endNode,
                startWeek, endWeek,
                type
        );
    }

    public static void requestGraduateScore(ResponseHandler<List<CourseScore>> handler) {
        /*
        * 成绩：可能会有未评教部分
        * */
        ResponseResult<List<CourseScore>> result = new ResponseResult<>();
        result.setFlag(ServerURL.GRADUATE_SCORE);

        HttpClient.getWithCAS(ServerURL.GRADUATE_SCORE, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                result.setException(e);
                handler.onHandle(result);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                try {
                    if (response.networkResponse().request().url().host().equals("cas.whu.edu.cn")) {
                        result.setFlag("LOGIN_FAIL");
                        handler.onHandle(result);
                        return;
                    }

                    ArrayList<CourseScore> scoreList = new ArrayList<>();
                    ResponseBody body = response.body();
                    if (body == null)
                        throw new EmptyResponseException();
                    Document doc = Jsoup.parse(body.string());
                    for (Element t_con : doc.getElementById("table_kcxx").getElementsByClass("t_con")) {
                        Elements tds = t_con.getElementsByTag("td");
                        String type = tds.get(0).text();
                        String semester = tds.get(1).text();
                        String name = tds.get(3).text();
                        float credit = Float.parseFloat(tds.get(4).text());
                        float score = Float.parseFloat(tds.get(6).text().split("/")[1]);
                        float gradePoint = CourseUtility.toGradePointGraduate(score);
                        scoreList.add(new CourseScore(name, semester, credit, score, gradePoint, type));
                    }
                    result.setData(scoreList);
                    handler.onHandle(result);
                } catch (Exception e) {
                    result.setException(e);
                    handler.onHandle(result);
                }
            }
        });
    }
}
