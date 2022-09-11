package com.wuda.wuxue.ui.course;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.wuda.wuxue.R;
import com.wuda.wuxue.bean.Course;
import com.wuda.wuxue.bean.Timetable;
import com.wuda.wuxue.bean.UserType;
import com.wuda.wuxue.network.ResponseResult;
import com.wuda.wuxue.ui.adapter.ScheduleTableVP2Adapter;
import com.wuda.wuxue.ui.base.DialogFactory;
import com.wuda.wuxue.ui.mine.AccountActivity;
import com.wuda.wuxue.util.CourseUtility;

import java.util.Calendar;
import java.util.List;

public class ScheduleFragment extends Fragment {

    TextView currentWeek_tv;
    ImageButton score_btn;
    ImageButton addCourse_btn;
    ImageButton addSchedule_btn;
    ImageButton setting_btn;
    ViewPager2 viewPager2;

    ScheduleViewModel mViewModel;
    ScheduleTableViewModel mTableViewModel;
    // 编辑以及新建
    ActivityResultLauncher<Intent> editCourseLauncher;
    ActivityResultLauncher<Intent> settingLauncher;
    // 本科生课表成绩导入
    ActivityResultLauncher<Intent> underGraduateCourseImportLauncher;

    AlertDialog loadingDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_schedule, container, false);
        currentWeek_tv = view.findViewById(R.id.schedule_currentWeek_textView);
        score_btn = view.findViewById(R.id.score_btn);
        addCourse_btn = view.findViewById(R.id.add_course_btn);
        addSchedule_btn = view.findViewById(R.id.add_schedule_btn);
        setting_btn = view.findViewById(R.id.schedule_setting_imageButton);
        viewPager2 = view.findViewById(R.id.schedule_viewPager2);

        editCourseLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                mTableViewModel.update();
            }
        });

        settingLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                mViewModel.reloadCurrentTable(); // 可能修改了当前表项，直接暴力更新（页面会有一定跳变感）
                mTableViewModel.update();
            }
        });

        underGraduateCourseImportLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                mTableViewModel.update();
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ScheduleViewModel.class);
        // ScheduleTableFragment 通过 getParentFragment 创建进行共享
        mTableViewModel = new ViewModelProvider(this).get(ScheduleTableViewModel.class);
        eventBinding();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private void eventBinding() {

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                initCurrentWeek_tv(position + 1);
            }
        });

        mViewModel.getCurrentTimeTable().observe(getViewLifecycleOwner(), new Observer<Timetable>() {
            @Override
            public void onChanged(Timetable timetable) {
                viewPager2.setAdapter(new ScheduleTableVP2Adapter(getChildFragmentManager(), getLifecycle()));
                if (timetable != null) {
                    viewPager2.setCurrentItem(mViewModel.getCurrentWeek() - 1, false);
                    initCurrentWeek_tv(mViewModel.getCurrentWeek());
                } else {
                    currentWeek_tv.setText("未选择任何课表");
                }
            }
        });

        mViewModel.getSuccessResponse().observe(getViewLifecycleOwner(), new Observer<List<Course>>() {
            @Override
            public void onChanged(List<Course> courses) {
                if (loadingDialog != null) {
                    loadingDialog.dismiss();
                    mViewModel.reloadCurrentTable();
                    mTableViewModel.update();
                }
            }
        });

        mViewModel.getFailResponse().observe(getViewLifecycleOwner(), new Observer<ResponseResult<?>>() {
            @Override
            public void onChanged(ResponseResult<?> result) {
                if (result == null) return; // 失败后离开页面，再次进入时会再次收到该事件
                if (result.getFlag().equals("LOGIN_FAIL")) {
                    Intent intent = new Intent(requireContext(), AccountActivity.class);
                    startActivity(intent);
                } else
                    DialogFactory.errorInfoDialog(requireContext(), result).show();
                mViewModel.clearFailResponse();
                if (loadingDialog != null)
                    loadingDialog.dismiss();
            }
        });

        addCourse_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mViewModel.getCurrentTimeTable().getValue() == null) {
                    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext())
                            .setTitle("当前未选择任何课表，是否创建新课表")
                            .setPositiveButton("是", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mViewModel.createTimeTable();
                                }
                            });
                    builder.create().show();
                    return;
                }
                int table_id = mViewModel.getCurrentTimeTable().getValue().getId();

                String color = CourseUtility.randomLightHexColor();
                // 新建默认值，id输入数据库时自增，星期一，1-2节，1-16周，2学分
                // 传递 table_id
                Course course = new Course(-1, "", 1, "", "", 1, 2, 1, 16, 0, 2, color, table_id);
                Intent intent = new Intent(getContext(), EditCourseActivity.class);
                intent.putExtra("course", course);
                editCourseLauncher.launch(intent);
            }
        });

        addSchedule_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFactory.selectUserDialog(requireContext(), new DialogFactory.ResultCallback<UserType>() {
                    @Override
                    public void result(UserType userType, int which) {
                        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext())
                                .setTitle("是否创建新课表")
                                .setMessage("如果不创建新课表将会覆盖当前课表~~~")
                                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        mViewModel.createTimeTable();
                                        if (loadingDialog == null)
                                            loadingDialog = DialogFactory.loadingDialog(requireContext());
                                        loadingDialog.show();
                                        if (userType == UserType.GRADUATE) {
                                            mViewModel.requestGraduateSchedule();
                                        } else if (userType == UserType.UNDERGRADUATE) {
                                            Intent intent = new Intent(requireContext(), UnderGraduateCourseImportActivity.class);
                                            underGraduateCourseImportLauncher.launch(intent);
                                            loadingDialog.dismiss();
                                        }
                                    }
                                });
                        if (mViewModel.getCurrentTimeTable().getValue() != null) {
                            // 当前表没有选择的时候，隐藏该按钮
                            builder.setNeutralButton("否", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (loadingDialog == null)
                                        loadingDialog = DialogFactory.loadingDialog(requireContext());
                                    loadingDialog.show();
                                    if (userType == UserType.GRADUATE) {
                                        mViewModel.requestGraduateSchedule();
                                    } else if (userType == UserType.UNDERGRADUATE) {
                                        Intent intent = new Intent(requireContext(), UnderGraduateCourseImportActivity.class);
                                        underGraduateCourseImportLauncher.launch(intent);
                                        loadingDialog.dismiss();
                                    }
                                }
                            });
                        }
                        builder.create().show();
                    }
                }).show();
            }
        });

        score_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), ScoreStatisticActivity.class));
            }
        });

        setting_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingLauncher.launch(new Intent(requireContext(), ScheduleManagerActivity.class));
            }
        });
    }

    private void initCurrentWeek_tv(int week) {
        StringBuilder text = new StringBuilder();

        Timetable timetable = mViewModel.getCurrentTimeTable().getValue();

        if (timetable == null) {
            text.append("未选择任何课表");
        } else {
            if (week <= 0) {
                text.append("还没开课哟");
            } else if (week > 18) {
                text.append("学期结束啦");
            } else {
                int currentDay = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1;
                text.append(getResources().getStringArray(R.array.current_week)[week - 1]);
                text.append("  ");
                if (mViewModel.getCurrentWeek() == week)
                    text.append(getResources().getStringArray(R.array.current_day)[currentDay]);
                else
                    text.append("非本周");
            }
        }
        currentWeek_tv.setText(text);
    }
}