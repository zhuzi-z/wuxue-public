package com.wuda.wuxue.ui.mine;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.wuda.wuxue.R;
import com.wuda.wuxue.bean.Announcement;
import com.wuda.wuxue.bean.BaseInfo;
import com.wuda.wuxue.bean.Lecture;
import com.wuda.wuxue.bean.Movie;
import com.wuda.wuxue.db.InfoDBUtility;
import com.wuda.wuxue.network.AnnouncementNetwork;
import com.wuda.wuxue.network.LectureNetwork;
import com.wuda.wuxue.network.MovieNetwork;
import com.wuda.wuxue.network.ResponseHandler;
import com.wuda.wuxue.network.ResponseResult;
import com.wuda.wuxue.receiver.InfoAlarmReceiver;
import com.wuda.wuxue.util.SharePreferenceManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class SubscribeInfoFragment extends Fragment {

    SwitchMaterial announcement_switch;
    SwitchMaterial movie_switch;
    SwitchMaterial lecture_switch;
    LinearLayout time_ll;
    TextView add_time_tv;
    TextView edit_time_tv;
    TextView tip_tv;
    ChipGroup selected_time_cp;

    Set<String> selected_items;
    Set<String> selected_time;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_subscribe_info, container, false);

        announcement_switch = view.findViewById(R.id.subscribeInfo_announcement_switch);
        movie_switch = view.findViewById(R.id.subscribeInfo_movie_switch);
        lecture_switch = view.findViewById(R.id.subscribeInfo_lecture_switch);
        time_ll = view.findViewById(R.id.subscribeInfo_updateTime_linearLayout);
        edit_time_tv = view.findViewById(R.id.subscribeInfo_edit_updateTime_textView);
        add_time_tv = view.findViewById(R.id.subscribeInfo_add_updateTime_textView);
        tip_tv = view.findViewById(R.id.subscribeInfo_tip_textView);
        selected_time_cp = view.findViewById(R.id.subscribeInfo_updateTime_chipGroup);

        selected_items = SharePreferenceManager.loadStringSet(SharePreferenceManager.SUBSCRIBE_INFO_SELECTED_ITEMS);
        announcement_switch.setChecked(selected_items.contains(BaseInfo.CATEGORY_ANNOUNCEMENT));
        movie_switch.setChecked(selected_items.contains(BaseInfo.CATEGORY_MOVIE));
        lecture_switch.setChecked(selected_items.contains(BaseInfo.CATEGORY_LECTURE));

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // SP存储返回HashSet
        selected_time = SharePreferenceManager.loadStringSet(SharePreferenceManager.SUBSCRIBE_INFO_TIME_LIST);
        // 排个序（小根堆）
        for (String time: new TreeSet<>(selected_time)) {
            if (time.equals("")) continue;
            addTimeChip(time);
        }

        eventBinding();
    }

    private void eventBinding() {
        announcement_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    selected_items.add(BaseInfo.CATEGORY_ANNOUNCEMENT);
                    // 同步当前状态作为是否为新信息的标准
                    AnnouncementNetwork.requestAnnouncementList(-1, new ResponseHandler<List<Announcement>>() {
                        @Override
                        public void onHandle(ResponseResult<List<Announcement>> result) {
                            if (result.getData() != null) {
                                InfoDBUtility.saveInfoId(result.getData());
                            }
                        }
                    });
                } else {
                    selected_items.remove(BaseInfo.CATEGORY_ANNOUNCEMENT);
                }
                storeSelectedItems();
            }
        });

        movie_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    selected_items.add(BaseInfo.CATEGORY_MOVIE);
                    // 同步状态
                    MovieNetwork.requestMovieList(1, new ResponseHandler<List<Movie>>() {
                        @Override
                        public void onHandle(ResponseResult<List<Movie>> result) {
                            if (result.getData() != null) {
                                InfoDBUtility.saveInfoId(result.getData());
                            }
                        }
                    });
                } else {
                    selected_items.remove(BaseInfo.CATEGORY_MOVIE);
                }
                storeSelectedItems();
            }
        });

        lecture_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    selected_items.add(BaseInfo.CATEGORY_LECTURE);
                    // 同步状态
                    @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Calendar calendar = Calendar.getInstance();
                    Date startDate = new Date();
                    calendar.setTime(startDate);
                    // 只同步一周内的信息
                    calendar.add(Calendar.DATE, 6);
                    Date endDate = calendar.getTime();
                    LectureNetwork.requestLectures(sdf.format(startDate), sdf.format(endDate), new ResponseHandler<List<Lecture>>() {
                        @Override
                        public void onHandle(ResponseResult<List<Lecture>> result) {
                            if (result.getData() != null) {
                                InfoDBUtility.saveInfoId(result.getData());
                            }
                        }
                    });
                } else {
                    selected_items.remove(BaseInfo.CATEGORY_LECTURE);
                }
                storeSelectedItems();
            }
        });

        add_time_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialTimePicker timePicker = new MaterialTimePicker.Builder().setTimeFormat(TimeFormat.CLOCK_24H).build();
                timePicker.addOnPositiveButtonClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String time = formatTime(timePicker.getHour(), timePicker.getMinute());
                        if (!selected_time.contains(time)) {
                            addTimeChip(time);
                            selected_time.add(time);
                            // 但凡操作都更新一遍定时器
                            updateSyncTime();
                        } else {
                            Toast.makeText(requireContext(), "该时间已存在", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                timePicker.show(getChildFragmentManager(), "TIME");
            }
        });

        edit_time_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i=0; i<selected_time_cp.getChildCount(); i++) {
                    if (selected_time_cp.getChildAt(i) instanceof Chip) {
                        Chip chip = ((Chip) selected_time_cp.getChildAt(i));
                        chip.setCloseIconVisible(!chip.isCloseIconVisible());
                    }
                }
            }
        });
    }

    private void addTimeChip(String time) {
        Chip chip = new Chip(requireContext());
        chip.setText(time);
        chip.setCloseIconResource(R.drawable.ic_baseline_close_24);
        chip.setOnCloseIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chip.setVisibility(View.GONE);
                selected_time.remove(chip.getText().toString());
                // 更新定时器
                updateSyncTime();
            }
        });
        chip.setCloseIconVisible(false);
        selected_time_cp.addView(chip);
    }

    private String formatTime(int hour, int minute) {
        // 格式 HH-mm，不足两位补0
        StringBuilder builder = new StringBuilder();
        if (hour < 10) {
            builder.append("0");
        }
        builder.append(hour);
        builder.append(":");
        if (minute < 10) {
            builder.append("0");
        }
        builder.append(minute);
        return builder.toString();
    }

    private void updateSyncTime() {
        SharePreferenceManager.storeStringSet(SharePreferenceManager.SUBSCRIBE_INFO_TIME_LIST, new HashSet<>(selected_time));
        InfoAlarmReceiver.registerAlarm(selected_time);
    }

    private void storeSelectedItems() {
        SharePreferenceManager.storeStringSet(SharePreferenceManager.SUBSCRIBE_INFO_SELECTED_ITEMS, selected_items);
    }
}