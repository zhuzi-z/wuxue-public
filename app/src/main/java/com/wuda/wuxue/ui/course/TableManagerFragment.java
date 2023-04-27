package com.wuda.wuxue.ui.course;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.wuda.wuxue.R;
import com.wuda.wuxue.bean.Course;
import com.wuda.wuxue.bean.Timetable;
import com.wuda.wuxue.db.CourseDBUtility;
import com.wuda.wuxue.ui.adapter.CourseAdapter;
import com.wuda.wuxue.ui.base.DialogFactory;
import com.wuda.wuxue.util.SharePreferenceManager;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class TableManagerFragment extends Fragment {

    private static final String ARG_TIMETABLE = "timetable";
    Timetable table;

    TextView name_tv;
    TextView date_tv;
    RecyclerView course_rv;
    Button setAsCurrent_btn;

    CourseAdapter mAdapter;
    ScheduleManagerViewModel mViewModel;

    ActivityResultLauncher<Intent> editCourseResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            mAdapter.setList(CourseDBUtility.queryCourseSchedule(table.getId()));
        }
    });

    public static TableManagerFragment newInstance(Timetable table) {
        TableManagerFragment fragment = new TableManagerFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_TIMETABLE, table);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            table = (Timetable) getArguments().getSerializable(ARG_TIMETABLE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_table_manager, container, false);

        name_tv = view.findViewById(R.id.tableManager_name_textView);
        date_tv = view.findViewById(R.id.tableManager_date_textView);
        course_rv = view.findViewById(R.id.tableManager_course_recyclerView);
        setAsCurrent_btn = view.findViewById(R.id.tableManager_setAsCurrent_button);

        name_tv.setText(table.getName());
        date_tv.setText(table.getStartDate());
        course_rv.setLayoutManager(new LinearLayoutManager(requireContext()));

        int currentTableId = SharePreferenceManager.loadInteger(SharePreferenceManager.SCHEDULE_CURRENT_TABLE_ID);
        if (currentTableId == table.getId()) {
            setAsCurrent_btn.setBackgroundColor(Color.LTGRAY);
        }

        ((ScheduleManagerActivity) requireActivity()).getSupportActionBar().setTitle("课表管理");

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mViewModel = new ViewModelProvider(requireActivity()).get(ScheduleManagerViewModel.class);

        mAdapter = new CourseAdapter(R.layout.item_textview);
        course_rv.setAdapter(mAdapter);
        mAdapter.setList(CourseDBUtility.queryCourseSchedule(table.getId()));

        requireActivity().addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.delete_schedule, menu);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.schedule_delete) {
                    new MaterialAlertDialogBuilder(requireContext())
                            .setTitle("删除课表")
                            .setMessage("删除后不可恢复，确定要删除吗？")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    mViewModel.deleteTimetable(table);
                                    mViewModel.queryTimetable();
                                    if (SharePreferenceManager.loadInteger(SharePreferenceManager.SCHEDULE_CURRENT_TABLE_ID) == 0) {
                                        SharePreferenceManager.storeInteger(SharePreferenceManager.SCHEDULE_CURRENT_TABLE_ID, 0);
                                    }
                                    requireActivity().onBackPressed();
                                }
                            })
                            .setNegativeButton("取消", null)
                            .create().show();
                }
                return true;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);

        eventBinding();
    }

    private void eventBinding() {
        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                Intent intent = new Intent(requireContext(), EditCourseActivity.class);
                Course course = (Course) adapter.getData().get(position);
                intent.putExtra("course", course);
//                startActivity(intent);
                editCourseResultLauncher.launch(intent);
            }
        });

        name_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFactory.textInputDialog(requireContext(), "课表名", new DialogFactory.ResultCallback<String>() {
                    @Override
                    public void result(String s, int which) {
                        table.setName(s);
                        mViewModel.updateTimetable(table);
                        name_tv.setText(s);
                    }
                }).show();
            }
        });

        date_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                long stamp;
                try {
                    stamp = sdf.parse(table.getStartDate()).getTime();
                } catch (ParseException e) {
                    e.printStackTrace();
                    stamp = Calendar.getInstance().getTime().getTime();
                }

                MaterialDatePicker<Long> picker = MaterialDatePicker.Builder.datePicker()
                        .setTitleText("请选择开学日期")
                        .setSelection(stamp)
                        .build();
                picker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
                            @Override
                            public void onPositiveButtonClick(Long selection) {
                                String date = sdf.format(selection);
                                table.setStartDate(date);
                                date_tv.setText(date);
                                mViewModel.updateTimetable(table);
                            }
                        });

                picker.show(getChildFragmentManager(), "");
            }
        });

        setAsCurrent_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharePreferenceManager.storeInteger(SharePreferenceManager.SCHEDULE_CURRENT_TABLE_ID, table.getId());
                setAsCurrent_btn.setBackgroundColor(Color.LTGRAY);
            }
        });
    }
}