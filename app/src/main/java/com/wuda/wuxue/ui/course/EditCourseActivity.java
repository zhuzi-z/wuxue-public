package com.wuda.wuxue.ui.course;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
//import com.skydoves.colorpickerview.ColorEnvelope;
//import com.skydoves.colorpickerview.ColorPickerDialog;
//import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener;
import com.wuda.wuxue.R;
import com.wuda.wuxue.bean.Course;
import com.wuda.wuxue.db.CourseDBUtility;
import com.wuda.wuxue.util.CourseUtility;

import java.util.List;

import cn.carbswang.android.numberpickerview.library.NumberPickerView;

public class EditCourseActivity extends AppCompatActivity {

    Toolbar toolbar;
    EditText name_et;
    EditText teacher_et;
    EditText room_et;
    EditText credit_et;
    EditText week_et;
    EditText time_et;
    EditText bgColor_et;
    Button delete_btn;
    Button save_btn;

    Course course;

    String[] weekType;
    String[] days;

    boolean bgColorModified = false;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_course);

        toolbar = findViewById(R.id.back_toolbar);
        name_et = findViewById(R.id.editCourse_name_et);
        teacher_et = findViewById(R.id.editCourse_teacher_et);
        room_et = findViewById(R.id.editCourse_room_et);
        credit_et = findViewById(R.id.editCourse_credit_et);
        week_et = findViewById(R.id.editCourse_week_et);
        time_et = findViewById(R.id.editCourse_time_et);
        bgColor_et = findViewById(R.id.editCourse_bgColor_et);
        delete_btn = findViewById(R.id.editCourse_delete_btn);
        save_btn = findViewById(R.id.editCourse_save_btn);


        // 全周，单周，双周
        weekType = getResources().getStringArray(R.array.type_weeks);
        days = getResources().getStringArray(R.array.select_days);

        Intent intent = getIntent();
        // 添加课程时，从 course 中获取 table_id
        course = (Course) intent.getSerializableExtra("course");
        if (course.getId() == -1) { // 如果是添加，tableID为-1
            toolbar.setTitle("添加新课程");
            delete_btn.setVisibility(View.GONE);
        } else {
            toolbar.setTitle("修改课程");
        }
        name_et.setText(course.getName());
        week_et.setText(
                Integer.valueOf(course.getStartWeek()).toString() +
                        "-" +
                        Integer.valueOf(course.getEndWeek()).toString() +
                        "周, " +
                        weekType[course.getType()] + "周"
        );
        time_et.setText(
                days[course.getDay()] + ", " +
                        Integer.valueOf(course.getStartNode()).toString() +
                        "-" +
                        Integer.valueOf(course.getEndNode()).toString() +
                        "节"
        );
        teacher_et.setText(course.getTeacher());
        room_et.setText(course.getRoom());
        credit_et.setText(Float.valueOf(course.getCredit()).toString());
        bgColor_et.setText(course.getColor());
        bgColor_et.setTextColor(Color.parseColor(course.getColor()));

        eventBinding();
    }

    private void eventBinding() {

        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        week_et.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = getLayoutInflater().inflate(R.layout.dialog_select_week, null, false);
                NumberPickerView startWeekPicker = view.findViewById(R.id.dialog_startWeekPicker);
                NumberPickerView endWeekPicker = view.findViewById(R.id.dialog_endWeekPicker);
                NumberPickerView weekTypePicker = view.findViewById(R.id.dialog_weekTypePicker);
                startWeekPicker.setOnValueChangedListener(new NumberPickerView.OnValueChangeListener() {
                    @Override
                    public void onValueChange(NumberPickerView picker, int oldVal, int newVal) {
                        if (newVal > endWeekPicker.getValue())
                            endWeekPicker.setValue(newVal);
                    }
                });
                endWeekPicker.setOnValueChangedListener(new NumberPickerView.OnValueChangeListener() {
                    @Override
                    public void onValueChange(NumberPickerView picker, int oldVal, int newVal) {
                        if (newVal < startWeekPicker.getValue())
                            startWeekPicker.setValue(newVal);
                    }
                });

                startWeekPicker.setMinValue(0);
                startWeekPicker.setMaxValue(17);
                startWeekPicker.setValue(course.getStartWeek() - 1);
                endWeekPicker.setMinValue(0);
                endWeekPicker.setMaxValue(17);
                endWeekPicker.setValue(course.getEndWeek() - 1);
                weekTypePicker.setMinValue(0);
                weekTypePicker.setMaxValue(2);
                weekTypePicker.setValue(course.getType());

                AlertDialog.Builder builder = new AlertDialog.Builder(EditCourseActivity.this);
                builder.setView(view);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        course.setStartWeek(startWeekPicker.getValue() + 1);
                        course.setEndWeek(endWeekPicker.getValue() + 1);
                        course.setType(weekTypePicker.getValue());

                        week_et.setText(
                                Integer.valueOf(course.getStartWeek()).toString() +
                                "-" +
                                Integer.valueOf(course.getEndWeek()).toString() +
                                "周, " +
                                weekType[course.getType()] + "周"
                        );
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.create().show();
            }
        });

        time_et.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = getLayoutInflater().inflate(R.layout.dialog_select_day_node, null, false);
                NumberPickerView dayPicker = view.findViewById(R.id.dialog_dayPicker);
                NumberPickerView startNodePicker = view.findViewById(R.id.dialog_startNodePicker);
                NumberPickerView endNodePicker = view.findViewById(R.id.dialog_endNodePicker);
                startNodePicker.setOnValueChangedListener(new NumberPickerView.OnValueChangeListener() {
                    @Override
                    public void onValueChange(NumberPickerView picker, int oldVal, int newVal) {
                        if (newVal > endNodePicker.getValue())
                            endNodePicker.setValue(newVal);
                    }
                });
                endNodePicker.setOnValueChangedListener(new NumberPickerView.OnValueChangeListener() {
                    @Override
                    public void onValueChange(NumberPickerView picker, int oldVal, int newVal) {
                        Log.d("new", Integer.valueOf(newVal).toString());
                        if (newVal < startNodePicker.getValue())
                            startNodePicker.setValue(newVal);
                    }
                });

                dayPicker.setMinValue(0);
                dayPicker.setMaxValue(6);
                dayPicker.setValue(course.getDay());
                startNodePicker.setMinValue(0);
                startNodePicker.setMaxValue(12);
                startNodePicker.setValue(course.getStartNode() - 1);
                endNodePicker.setMinValue(0);
                endNodePicker.setMaxValue(12);
                endNodePicker.setValue(course.getEndNode() - 1);

                AlertDialog.Builder builder = new AlertDialog.Builder(EditCourseActivity.this);
                builder.setView(view);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        course.setDay(dayPicker.getValue());
                        course.setStartNode(startNodePicker.getValue() + 1);
                        course.setEndNode(endNodePicker.getValue() + 1);

                        time_et.setText(
                                days[course.getDay()] + ", " +
                                        Integer.valueOf(course.getStartNode()).toString() +
                                        "-" +
                                        Integer.valueOf(course.getEndNode()).toString() +
                                        "节"
                        );
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.create().show();
            }
        });

        bgColor_et.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorPickerDialogBuilder
                        .with(EditCourseActivity.this)
                        .initialColor(Color.parseColor(course.getColor()))
                        .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                        .density(8)
                        .lightnessSliderOnly()
                        .showColorEdit(true)
                        .setColorEditTextColor(R.color.black)
                        .setPositiveButton("确认", new ColorPickerClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                                String hexColor = "#" + Integer.toHexString(selectedColor).toUpperCase().substring(2);
                                course.setColor(hexColor);
                                bgColor_et.setText(hexColor);
                                bgColor_et.setTextColor(selectedColor);
                                bgColorModified = true;
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .build()
                        .show();
            }
        });

        delete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CourseDBUtility.deleteCourse(course);
                setResult(RESULT_OK);
                finish();
            }
        });

        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                course.setName(name_et.getText().toString());
                course.setTeacher(teacher_et.getText().toString());
                course.setRoom(room_et.getText().toString());
                if(!credit_et.getText().toString().isEmpty())
                    course.setCredit(Float.parseFloat(credit_et.getText().toString()));
                // 更新或插入课程
                CourseDBUtility.insertOneCourse(course);

                if (bgColorModified) {
                    List<Course> courseList = CourseDBUtility.queryCourseSchedule(course.getTableId());
                    for (Course c: courseList) {
                        if (c.getName().equals(course.getName())) {
                            c.setColor(course.getColor());
                            CourseDBUtility.insertOneCourse(c);
                        }
                    }
                }

                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
}