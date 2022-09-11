package com.wuda.wuxue.ui.course;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.util.Pair;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textview.MaterialTextView;
import com.wuda.wuxue.R;
import com.wuda.wuxue.bean.Course;
import com.wuda.wuxue.ui.base.TipTextView;
import com.wuda.wuxue.util.CourseUtility;
import com.wuda.wuxue.util.ScreenUtility;

import java.util.Calendar;
import java.util.List;

public class ScheduleTableFragment extends Fragment {

    private LinearLayout date_header_ll;
    private ConstraintLayout scheduleTable_cl;

    ScheduleTableViewModel mViewModel;

    private int itemWidth;
    private int itemHeight;
    // 时间列占比（课程单列为基准）
    private final float timeListWeight = 0.6f;
    // 时刻表
    private boolean containTimeList = true;

    public static String ARG_WEEK = "WEEK";
    private int week = -1;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // 课表 item: 1, 时刻 item 0.6
        itemWidth = (int) (ScreenUtility.getScreenWidth(requireContext()) * 1.0 / (7 + timeListWeight));
        // height -> alpha * wight
        itemHeight = (int) (1.3 * itemWidth);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule_table, container, false);
        date_header_ll = view.findViewById(R.id.schedule_date_linearLayout);
        scheduleTable_cl = view.findViewById(R.id.schedule_table_constrainLayout);

        if (getArguments() != null)
            week = getArguments().getInt(ARG_WEEK);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // parent fragment => 所有周的表项共享该ViewModel
        mViewModel = new ViewModelProvider(requireParentFragment()).get(ScheduleTableViewModel.class);
        mViewModel.getCourseList().observe(getViewLifecycleOwner(), new Observer<List<Course>>() {
            @Override
            public void onChanged(List<Course> courses) {
                intiDayPanel();
                updateScheduleView();
            }
        });
    }

    public void updateScheduleView() {
        scheduleTable_cl.removeAllViews();
        addTimeTable(containTimeList);
        for (Course course: mViewModel.getCourseList().getValue()) {
            addCourse((course));
        }
    }

    private void intiDayPanel() {
        date_header_ll.removeAllViews();

        Calendar calendar = Calendar.getInstance();

        int currentDay = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        // 星期偏移
        calendar.add(Calendar.DATE, -currentDay);
        // 周偏移
        calendar.add(Calendar.DATE, (week - mViewModel.getCurrentWeek())*7);

        // 该周周天的月份
        String[] monthStr = getResources().getStringArray(R.array.month);
        MaterialTextView month_tv = new MaterialTextView(requireContext());
        month_tv.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, timeListWeight));
        month_tv.setGravity(Gravity.CENTER);
        month_tv.setText(monthStr[calendar.get(Calendar.MONTH)]);
        date_header_ll.addView(month_tv);

        String[] dayOfWeek = getResources().getStringArray(R.array.day_of_week);
        for (int i=0; i<7; ++i) {
            StringBuilder text = new StringBuilder();
            text.append(dayOfWeek[i]);
            text.append('\n');
            text.append(Integer.valueOf(calendar.get(Calendar.DATE)));

            MaterialTextView day_tv = new MaterialTextView(requireContext());
            day_tv.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
            day_tv.setGravity(Gravity.CENTER);
            day_tv.setText(text);
            // 当前周当前星期
            if (i == currentDay && mViewModel.getCurrentWeek() == week) {
                day_tv.getPaint().setFakeBoldText(true);
            }
            date_header_ll.addView(day_tv);
            calendar.add(Calendar.DATE, 1);
        }
    }

    private void addTimeTable(boolean showDetail) {

        List<Pair<String, String>> timeTable = CourseUtility.getTimeList();

        for (int i=1; i<=13; ++i) {
            SpannableStringBuilder textSpan = new SpannableStringBuilder();

            textSpan.append(Integer.toString(i), new RelativeSizeSpan(1.5f), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            // 包含具体时刻
            if (showDetail) {
                String text = "\n" + timeTable.get(i-1).first + "\n" + timeTable.get(i-1).second;
                // text: 1\n08:00\n08:45
                textSpan.append(text);
            }

            MaterialTextView itemView = new MaterialTextView(requireContext());
            itemView.setTextSize(11);
            itemView.setText(textSpan);

            itemView.setGravity(Gravity.CENTER);
            ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
                    (int) (itemWidth * timeListWeight), itemHeight
            );
            layoutParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
            layoutParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
            layoutParams.topMargin = (i-1) * itemHeight;
            itemView.setLayoutParams(layoutParams);

            scheduleTable_cl.addView(itemView);
        }
    }

    private void addCourse(Course course) {

        if (course.getStartWeek() > week || course.getEndWeek() < week)
            return;

        if (week % 2 == 0 && course.getType() == Course.WEEK_TYPE_ODD)
            return;

        if (week % 2 == 1 && course.getType() == Course.WEEK_TYPE_EVEN)
            return;

        TipTextView course_view = new TipTextView(getContext());

        StringBuilder text = new StringBuilder();
        text.append(course.getName())
                .append("@")
                .append(course.getRoom())
                .append(" ")
                .append(course.getTeacher());
        int bgColor = Color.parseColor(course.getColor());

//        course_view.init(text.toString(), 12, 0xffffffff, bgColor, 200, 0x80ffffff);
        course_view.init(text.toString(), 12, 0xffffffff, bgColor, 200, Color.TRANSPARENT);
        course_view.setPadding(11, 11, 11, 11);

        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
                itemWidth, itemHeight * (course.getEndNode()-course.getStartNode()+1)
        );
        layoutParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
        layoutParams.leftMargin = (int) (itemWidth * (0.6 +  course.getDay()));
        layoutParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
        layoutParams.topMargin = itemHeight * (course.getStartNode()-1);
        course_view.setLayoutParams(layoutParams);

        course_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(requireContext(), EditCourseActivity.class);
                intent.putExtra("course", course);
                assert getParentFragment() != null;
                ((ScheduleFragment) getParentFragment()).editCourseLauncher.launch(intent);
            }
        });
        scheduleTable_cl.addView(course_view);
    }
}