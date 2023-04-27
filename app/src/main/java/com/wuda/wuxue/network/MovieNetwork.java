package com.wuda.wuxue.network;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wuda.wuxue.bean.Movie;
import com.wuda.wuxue.util.NetUtility;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MovieNetwork {
    public static void requestMovieList(Integer page, ResponseHandler<List<Movie>> handler) {
        // ?code_table=BI_SCHOOL_CULTURE&code_num=4&page=1&rows=6&__resultType=json&_sysCode=&t=
        FormBody.Builder formBuilder = new FormBody.Builder();
        formBuilder.add("code_table", "BI_SCHOOL_CULTURE");
        formBuilder.add("code_num", "4");
        formBuilder.add("page", page.toString());
        formBuilder.add("rows", "3"); // 一次只请求三条（一般只有两条是新的，所以三条中最后一条应该为历史消息，用于判断是否请求完）

        ResponseResult<List<Movie>> result = new ResponseResult<>();
        result.setFlag(ServerURL.MOVIE_LIST);

        HttpClient.post(ServerURL.MOVIE_LIST, formBuilder.build(), new Callback() {
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
                    JSONObject responseJson = new JSONObject(NetUtility.utf8Converter(data));
                    if (responseJson.getJSONObject("header").getString("code").equals("0")) {
                        JSONArray movieJsonArray = responseJson.getJSONObject("body").getJSONArray("rows");
                        int totalPage = responseJson.getJSONObject("body").getInt("total");
                        List<Movie> movieList = new Gson().fromJson(movieJsonArray.toString(),
                                new TypeToken<ArrayList<Movie>>(){}.getType());
                        result.setData(movieList);
                        result.setTotalPages(totalPage);
                    }
                } catch (Exception e) {
                    result.setException(e);
                }
                
                handler.onHandle(result);
            }
        });
    }

    public static void requestStoryLine(String id, ResponseHandler<String> handler) {
        // "?code_table=BI_SCHOOL_CULTURE&id=id&__resultType=json"
        FormBody.Builder formBuilder = new FormBody.Builder();
        formBuilder.add("code_table", "BI_SCHOOL_CULTURE");
        formBuilder.add("id", id);

        ResponseResult<String> result = new ResponseResult<>();
        result.setFlag(ServerURL.MOVIE_STORYLINE);

        HttpClient.post(ServerURL.MOVIE_STORYLINE, formBuilder.build(), new Callback() {
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
                    JSONObject responseJson = new JSONObject(body.string());
                    String storyline = NetUtility.utf8Converter(responseJson.getJSONObject("body").getString("content"));
                    result.setData(storyline);
                } catch (Exception e) {
                    result.setException(e);
                }
                handler.onHandle(result);
            }
        });
    }
}
