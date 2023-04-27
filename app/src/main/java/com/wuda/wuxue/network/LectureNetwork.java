package com.wuda.wuxue.network;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.wuda.wuxue.bean.Lecture;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class LectureNetwork {
    public static void requestLectures(String start, String end, ResponseHandler<List<Lecture>> handler) {
        ResponseResult<List<Lecture>> result = new ResponseResult<>();
        result.setFlag(ServerURL.LECTURE_JSON);

        FormBody.Builder formBuilder = new FormBody.Builder();
        // start_date=2022-01-01&&end_date=2022-01-07
        formBuilder.add("start_date", start);
        formBuilder.add("end_date", end);

        HttpClient.post(ServerURL.LECTURE_JSON, formBuilder.build(), new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                result.setException(e);
                handler.onHandle(result);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {

                List<Lecture> lectureList = new ArrayList<>();
                // gson
                try {
                    ResponseBody body = response.body();
                    if (body == null)
                        throw new EmptyResponseException();
                    JSONObject responseJson = new JSONObject(body.string());
                    if (responseJson.getString("code").equals("0")) {
                        JSONObject data = responseJson.getJSONObject("data").getJSONObject("data");
                        for (Iterator<String> it = data.keys(); it.hasNext(); ) {
                            String time = it.next();
                            JSONArray lectureArray = data.getJSONArray(time);
                            for (int i=0; i<lectureArray.length(); ++i) {
                                JSONObject lectureObj = (JSONObject) lectureArray.get(i);
                                Lecture lecture = new Gson().fromJson(lectureObj.toString(), Lecture.class);
                                lecture.time = time;
                                lectureList.add(lecture);
                            }
                        }
                    }
                    result.setData(lectureList);
                    handler.onHandle(result);
                } catch (Exception e) {
                    result.setException(e);
                    handler.onHandle(result);
                }
            }
        });
    }
}
