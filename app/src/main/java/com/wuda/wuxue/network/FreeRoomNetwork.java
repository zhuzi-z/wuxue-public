package com.wuda.wuxue.network;

import androidx.annotation.NonNull;

import com.wuda.wuxue.bean.CampusBuilding;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class FreeRoomNetwork {
    public static void requestBuildings(ResponseHandler<List<CampusBuilding>> handler) {
        // 获取教学楼
        ResponseResult<List<CampusBuilding>> result = new ResponseResult<>();
        result.setFlag(ServerURL.FREE_ROOM_BUILDING);

        HttpClient.get(ServerURL.FREE_ROOM_BUILDING, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                result.setException(e);
                handler.onHandle(result);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                List<CampusBuilding> campusBuildingList = new ArrayList<>();

                try {
                    ResponseBody body = response.body();
                    if (body == null)
                        throw new EmptyResponseException();
                    JSONObject responseJson = new JSONObject(body.string());
                    if (responseJson.getInt("code") == 200) {
                        JSONArray campusList = responseJson.getJSONArray("data");
                        for (int i=0; i<campusList.length(); i++) {
                            JSONObject campus = campusList.getJSONObject(i);

                            String campusName = campus.getString("text");
                            JSONArray buildingList = campus.getJSONArray("children");

                            for (int j=0; j<buildingList.length(); j++) {
                                JSONObject building = buildingList.getJSONObject(j);
                                String id = building.getString("id");
                                String name = building.getString("text");
                                campusBuildingList.add(new CampusBuilding(id, name, campusName));
                            }
                        }
                    }
                    result.setData(campusBuildingList);
                } catch (Exception e) {
                    result.setException(e);
                }
                handler.onHandle(result);
            }
        });
    }

    public static void requestRooms(String building, String date, ResponseHandler<List<List<String>>> handler) {

        ResponseResult<List<List<String>>> result = new ResponseResult<>();
        result.setFlag(ServerURL.FREE_ROOM_DATA);

        HttpClient.get(ServerURL.FREE_ROOM_DATA + "?build=" + building + "&" + "date=" + date, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                result.setException(e);
                handler.onHandle(result);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                List<List<String>> rooms = new ArrayList<>();

                try {
                    ResponseBody body = response.body();
                    if (body == null)
                        throw new EmptyResponseException();
                    JSONObject responseJson = new JSONObject(body.string());
                    if (responseJson.getInt("code") == 200) {
                        JSONObject data = responseJson.getJSONObject("data");
                        for (int i=0; i<data.length(); i++) {
                            JSONArray roomListJson = data.getJSONArray(String.valueOf(i+1));
                            List<String> roomList = new ArrayList<>();
                            for (int j=0; j<roomListJson.length(); j++) {
                                JSONObject room = roomListJson.getJSONObject(j);
                                String name = room.getString("roomName");
                                roomList.add(name);
                            }
                            rooms.add(roomList);
                        }
                    }
                    result.setData(rooms);
                } catch (Exception e) {
                    result.setException(e);
                }

                handler.onHandle(result);
            }
        });

    }
}
