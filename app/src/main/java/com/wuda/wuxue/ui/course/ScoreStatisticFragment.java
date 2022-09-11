package com.wuda.wuxue.ui.course;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.DefaultValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textview.MaterialTextView;
import com.wuda.wuxue.R;
import com.wuda.wuxue.bean.CourseScore;
import com.wuda.wuxue.bean.UserType;
import com.wuda.wuxue.network.ResponseResult;
import com.wuda.wuxue.ui.adapter.ScoreAdapter;
import com.wuda.wuxue.ui.base.DialogFactory;
import com.wuda.wuxue.ui.mine.AccountActivity;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class ScoreStatisticFragment extends Fragment {

    // 适配夜间模式
    @ColorInt
    int themeTextColor;

    private MaterialTextView semester_tv;
    private MaterialTextView type_tv;
    private TableLayout scoreTable;
    private PieChart gpaPieChart;
    private LineChart trendChart;
    AlertDialog loadingDialog;

    ScoreStatisticViewModel mViewModel;

    // 本科生课表成绩导入
    ActivityResultLauncher<Intent> underGraduateCourseImportLauncher;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_score_statistic, container, false);

        // 文字颜色，适配图表的夜间模式
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = requireActivity().getTheme();
        theme.resolveAttribute(android.R.attr.textColorPrimary, typedValue, true);
        TypedArray arr = requireActivity().obtainStyledAttributes(typedValue.data, new int[]{
                android.R.attr.textColorPrimary});
        themeTextColor = arr.getColor(0, -1);

        semester_tv = view.findViewById(R.id.score_semester_textView);
        type_tv = view.findViewById(R.id.score_type_textView);
        scoreTable = view.findViewById(R.id.score_table);
        gpaPieChart = view.findViewById(R.id.gpaPieChart);
        trendChart = view.findViewById(R.id.trend_chart);

        ((ScoreStatisticActivity) requireActivity()).getSupportActionBar().setTitle("成绩");

        requireActivity().addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.score_toolbar, menu);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.score_sync) {
                    DialogFactory.selectUserDialog(requireContext(), new DialogFactory.ResultCallback<UserType>() {
                        @Override
                        public void result(UserType userType, int which) {
                            if (loadingDialog == null) {
                                loadingDialog = DialogFactory.loadingDialog(requireContext());
                            }
                            loadingDialog.show();
                            if (userType == UserType.GRADUATE) {
                                mViewModel.requestGraduateScore();
                            } else if (userType == UserType.UNDERGRADUATE) {
                                Intent intent = new Intent(requireContext(), UnderGraduateCourseImportActivity.class);
                                underGraduateCourseImportLauncher.launch(intent);
                                loadingDialog.dismiss();
                            }
                        }
                    }).show();
                } else if (menuItem.getItemId() == R.id.score_about) {
                    ((ScoreStatisticActivity) requireActivity()).navigationTo(new ScoreAboutFragment(), true);
                }
                return true;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);

        underGraduateCourseImportLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                mViewModel.queryScore();
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mViewModel = new ViewModelProvider(this).get(ScoreStatisticViewModel.class);

        // 饼图初始化（事件监听）
        initGPAPieChart();
        initTrendChart();

        mViewModel.getSuccessResponse().observe(getViewLifecycleOwner(), new Observer<List<CourseScore>>() {
            @Override
            public void onChanged(List<CourseScore> courseScores) {
                if (loadingDialog != null) {
                    loadingDialog.dismiss();
                }
                updateView();
            }
        });

        mViewModel.getFailResponse().observe(getViewLifecycleOwner(), new Observer<ResponseResult<?>>() {
            @Override
            public void onChanged(ResponseResult<?> result) {
                if (result == null) return;
                if (result.getFlag().equals("LOGIN_FAIL")) {
                    Intent intent = new Intent(requireContext(), AccountActivity.class);
                    startActivity(intent);
                } else {
                    DialogFactory.errorInfoDialog(requireContext(), result).show();
                }
                loadingDialog.dismiss();
                mViewModel.clearFailResponse();
            }
        });

        if (mViewModel.getSuccessResponse().getValue() == null)
            mViewModel.queryScore();
    }

    private void updateView() {
        // bug -> 图先完成，后表格（跳变感）
        addHeader2Table();
        // 初始化选择器（学期，类型）
        initSpinner();
        setTrendChartData();
        setGPAPieChartData();
    }

    private void initSpinner() {
        // 保持添加顺序
        Set<String> semesters = new LinkedHashSet<>();
        Set<String> types = new LinkedHashSet<>();
        // “全部” 选项
        String all = getResources().getString(R.string.score_all);
        semesters.add(all);
        types.add(all);
        semester_tv.setText(all);
        type_tv.setText(all);
        queryScore2Table(semester_tv.getText().toString(), type_tv.getText().toString());

        for (CourseScore score: mViewModel.getSuccessResponse().getValue()) {
            semesters.add(score.getSemester());
            types.add(score.getType());
        }

        semester_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu semesterMenu = new PopupMenu(requireContext(), semester_tv);
                for (String semester: semesters) {
                    semesterMenu.getMenu().add(semester);
                }
                semesterMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        semester_tv.setText(item.getTitle());
                        queryScore2Table(semester_tv.getText().toString(), type_tv.getText().toString());
                        return false;
                    }
                });
                semesterMenu.show();
            }
        });

        type_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu semesterMenu = new PopupMenu(requireContext(), semester_tv);
                for (String type: types) {
                    semesterMenu.getMenu().add(type);
                }
                semesterMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        type_tv.setText(item.getTitle());
                        queryScore2Table(semester_tv.getText().toString(), type_tv.getText().toString());
                        return false;
                    }
                });
                semesterMenu.show();
            }
        });
    }

    private void queryScore2Table(String semester, String type) {
        // 现清除所有在添加新的
        scoreTable.removeAllViews();
        // 添加表头
        addHeader2Table();
        // 统计成绩
        CourseScore totalScore = new CourseScore("", "", 0.0f, 0.0f, "");
        int totalCourse = 0;
        float totalCredit = 0.0f;
        float totalGrade = 0.0f;
        float totalGP = 0.0f;
        // 添加满足选择器条件的成绩
        for (CourseScore score: mViewModel.getSuccessResponse().getValue()) {
            if (matchQueryOption(score, semester, type)) {
                ++totalCourse;
                totalCredit += score.getCredit();
                totalGrade += score.getScore() * score.getCredit();
                totalGP += score.getGradePoint() * score.getCredit();
                addScoreItem2Table(score);
            }
        }
        // 汇总
        totalScore.setCredit(totalCredit);
        totalScore.setName(getString(R.string.score_total)  + Integer.valueOf(totalCourse).toString());
        if (totalCredit != 0) {
            totalScore.setScore(totalGrade / totalCredit);
            totalScore.setGradePoint(totalGP / totalCredit);
        }
        addScoreItem2Table(totalScore);
    }

    private void addHeader2Table() {
        TableRow.LayoutParams params = new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        MaterialTextView nameHeader_tv = new MaterialTextView(requireContext());
        MaterialTextView creditHeader_tv = new MaterialTextView(requireContext());
        MaterialTextView gradeHeader_tv = new MaterialTextView(requireContext());

        nameHeader_tv.setGravity(Gravity.START);
        nameHeader_tv.setTextSize(16);
        params.weight = 7;
        nameHeader_tv.setLayoutParams(params);

        creditHeader_tv.setGravity(Gravity.CENTER);
        creditHeader_tv.setTextSize(16);
        params.weight = 1;
        creditHeader_tv.setLayoutParams(params);

        gradeHeader_tv.setGravity(Gravity.END);
        gradeHeader_tv.setTextSize(16);
        params.weight = 2;
        gradeHeader_tv.setLayoutParams(params);

        nameHeader_tv.setTypeface(Typeface.DEFAULT_BOLD);
        creditHeader_tv.setTypeface(Typeface.DEFAULT_BOLD);
        gradeHeader_tv.setTypeface(Typeface.DEFAULT_BOLD);

        nameHeader_tv.setText(R.string.score_name);
        creditHeader_tv.setText(R.string.score_credit);
        gradeHeader_tv.setText(R.string.score_gpa);

        TableRow row = new TableRow(requireContext());
        row.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT));
        row.addView(nameHeader_tv);
        row.addView(creditHeader_tv);
        row.addView(gradeHeader_tv);

        scoreTable.addView(row);
    }

    private boolean matchQueryOption(CourseScore score, String semester, String type) {
        String all = getString(R.string.score_all);
        if (semester.equals(all) && type.equals(all)) {
            return true;
        } else if (semester.equals(all) && score.getType().equals(type)) {
            return true;
        } else if (type.equals(all) && score.getSemester().equals(semester)) {
            return true;
        } else if (score.getSemester().equals(semester) && score.getType().equals(type)) {
            return true;
        }
        return false;
    }

    @SuppressLint("SetTextI18n")
    private void addScoreItem2Table(CourseScore score) {

        TableRow.LayoutParams params = new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        MaterialTextView name_tv = new MaterialTextView(requireContext());
        MaterialTextView credit_tv = new MaterialTextView(requireContext());
        MaterialTextView grade_tv = new MaterialTextView(requireContext());

        name_tv.setGravity(Gravity.START);
        name_tv.setTextSize(14);
        params.weight = 7;
        name_tv.setLayoutParams(params);

        credit_tv.setGravity(Gravity.CENTER);
        credit_tv.setTextSize(14);
        params.weight = 1;
        credit_tv.setLayoutParams(params);

        grade_tv.setGravity(Gravity.END);
        grade_tv.setTextSize(14);
        params.weight = 2;
        grade_tv.setLayoutParams(params);

        DecimalFormat df = new DecimalFormat("#.##");
        name_tv.setText(score.getName());
        credit_tv.setText(df.format(score.getCredit()));
        grade_tv.setText(df.format(score.getScore()) + "/" + df.format(score.getGradePoint()));

        TableRow row = new TableRow(requireContext());
        row.setPadding(0, 5, 0, 5);
        row.addView(name_tv);
        row.addView(credit_tv);
        row.addView(grade_tv);

        scoreTable.addView(row);
    }

    private void initTrendChart() {
        trendChart.setTouchEnabled(false);
        trendChart.setDragEnabled(false);
        trendChart.setScaleEnabled(false);
        // if disabled, scaling can be done on x- and y-axis separately
        trendChart.setPinchZoom(false);
        trendChart.setDrawGridBackground(false);
        trendChart.getDescription().setEnabled(false);
        trendChart.setDrawBorders(false);
        // 左右坐标轴
        trendChart.getAxisLeft().setEnabled(false);
        trendChart.getAxisRight().setEnabled(false);
        trendChart.getXAxis().setDrawAxisLine(false);
        trendChart.getXAxis().setDrawGridLines(false);
        // X 轴
        XAxis xAxis = trendChart.getXAxis();
        xAxis.setTextSize(11f);
        xAxis.setDrawAxisLine(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setLabelRotationAngle(85f);

        Legend l = trendChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setTextSize(12f);
        l.setTextColor(themeTextColor);
    }

    private void setTrendChartData() {
        // 按学期分类
        // TreeMap排序
        Map<String, List<CourseScore>> semesterMap = new TreeMap<>();
        for (CourseScore courseScore: mViewModel.getSuccessResponse().getValue()) {
            if (!semesterMap.containsKey(courseScore.getSemester())) {
                semesterMap.put(courseScore.getSemester(), new ArrayList<CourseScore>());
            }
            semesterMap.get(courseScore.getSemester()).add(courseScore);
        }
        ArrayList<Entry> gpaEntry = new ArrayList<>();
        ArrayList<Entry> avgScoreEntry = new ArrayList<>();

        int index = 0;
        // 对两个曲线进行偏移，防止靠的过近
        float maxAvgScore = 0, minAvgScore = Float.MAX_VALUE;
        float maxGPA = 0, minGPA = Float.MAX_VALUE;
        for (String semester: semesterMap.keySet()) {
            float totalGP = 0;
            float totalScore = 0;
            float totalCredit = 0;
            for (CourseScore courseScore: semesterMap.get(semester)) {
                totalCredit += courseScore.getCredit();
                totalGP += courseScore.getGradePoint() * courseScore.getCredit();
                totalScore += courseScore.getScore() * courseScore.getCredit();
            }
            float gpa = totalGP / totalCredit;
            minGPA = Math.min(minGPA, gpa);
            maxGPA = Math.max(maxGPA, gpa);
            gpaEntry.add(new Entry(index, gpa));

            float avgScore = totalScore / totalCredit;
            minAvgScore = Math.min(minAvgScore, avgScore);
            maxAvgScore = Math.max(maxAvgScore, avgScore);
            avgScoreEntry.add(new Entry(index, avgScore));
            ++index;
        }

        LineDataSet gpaSet, scoreSet;

        gpaSet = new LineDataSet(gpaEntry, "GPA");
        gpaSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        gpaSet.setColor(getResources().getColor(R.color.lineChart_gpa));
        gpaSet.setCircleColor(getResources().getColor(R.color.lineChart_gpa));
        gpaSet.setLineWidth(2.5f);
        gpaSet.setCircleRadius(5f);
        gpaSet.setValueTextColor(themeTextColor);

        scoreSet = new LineDataSet(avgScoreEntry, getString(R.string.score_avgScore));
        scoreSet.setAxisDependency(YAxis.AxisDependency.RIGHT);
        scoreSet.setColor(getResources().getColor(R.color.lineChart_avgScore));
        scoreSet.setCircleColor(getResources().getColor(R.color.lineChart_avgScore));
        scoreSet.setLineWidth(2.5f);
        scoreSet.setCircleRadius(5f);

        LineData data = new LineData(gpaSet, scoreSet);
        data.setValueTextColor(themeTextColor);
        data.setValueTextSize(9f);
        // 两位小数
        data.setValueFormatter(new DefaultValueFormatter(2));

        // Y轴偏移
        // GPA在上 .1空白偏移
        trendChart.getAxisLeft().setAxisMinimum((float) (minGPA - (maxGPA-minGPA)*0.6));
        // AVG
        trendChart.getAxisRight().setAxisMaximum((float) (maxAvgScore + (maxAvgScore-minAvgScore)*0.6));
        // X 坐标轴标签
        IndexAxisValueFormatter formatter = new IndexAxisValueFormatter(semesterMap.keySet());
        XAxis xAxis = trendChart.getXAxis();
        xAxis.setTextColor(themeTextColor);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(formatter);
        // set data
        trendChart.setData(data);
        trendChart.notifyDataSetChanged();
        trendChart.invalidate();
    }

    private void initGPAPieChart() {
        gpaPieChart.setUsePercentValues(true);
        gpaPieChart.getDescription().setEnabled(false);

        SpannableStringBuilder text = new SpannableStringBuilder("GPA");
        text.setSpan(new RelativeSizeSpan(2), 0, 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        gpaPieChart.setCenterText(text);

        gpaPieChart.setDrawHoleEnabled(true);
        gpaPieChart.setHoleColor(Color.WHITE);

        gpaPieChart.setTransparentCircleColor(Color.WHITE);
        gpaPieChart.setTransparentCircleAlpha(110);

        gpaPieChart.setHoleRadius(48f);
        gpaPieChart.setTransparentCircleRadius(53f);

        gpaPieChart.setDrawCenterText(true);

        gpaPieChart.setRotationEnabled(true);
        gpaPieChart.setHighlightPerTapEnabled(true);

        Legend l = gpaPieChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setTextColor(themeTextColor);
        // 扇形区内的颜色
        gpaPieChart.setEntryLabelColor(themeTextColor);
        gpaPieChart.setEntryLabelTextSize(14f);

        gpaPieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                showBottomSheet((PieEntry) e);
            }

            @Override
            public void onNothingSelected() {

            }
        });
    }

    private void setGPAPieChartData() {
        ArrayList<PieEntry> entries = new ArrayList<>();

        // 相同GPA的课程分类
        // 有序Map
        Map<Float, List<CourseScore>> gpaMap = new TreeMap<>();
        for (CourseScore courseScore: mViewModel.getSuccessResponse().getValue()) {
            if (!gpaMap.containsKey(courseScore.getGradePoint())) {
                gpaMap.put(courseScore.getGradePoint(), new ArrayList<CourseScore>());
            }
            gpaMap.get(courseScore.getGradePoint()).add(courseScore);
        }

        for (Float gpa: gpaMap.keySet()) {
            float totalCredit = 0;
            for (CourseScore courseScore: gpaMap.get(gpa)) {
                totalCredit += courseScore.getCredit();
            }
            entries.add(new PieEntry(totalCredit, gpa.toString(), gpaMap.get(gpa)));
        }

        PieDataSet dataSet = new PieDataSet(entries, "GPA");

        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);

        dataSet.setColors(ColorTemplate.VORDIPLOM_COLORS);
        // 数据放到外面
        dataSet.setValueLinePart1OffsetPercentage(60.f);
        dataSet.setValueLinePart1Length(0.15f);
        dataSet.setValueLinePart2Length(0.9f);
        dataSet.setValueLineColor(themeTextColor);

        dataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(12f);
        data.setValueTextColor(themeTextColor);

        gpaPieChart.setData(data);
        gpaPieChart.notifyDataSetChanged();

        gpaPieChart.highlightValues(null);

        gpaPieChart.invalidate();
    }

    @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
    private void showBottomSheet(PieEntry pieEntry) {
        BottomSheetDialog gpaBottomSheetDialog;
        View view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_gpa_bottomsheet, null, false);
        MaterialTextView gpaBottomSheetLabel = view.findViewById(R.id.gpa_bottomsheet_label_tv);
        RecyclerView gpaBottomSheetRecyclerView = view.findViewById(R.id.gpa_bottomsheet_recyclerView);
        gpaBottomSheetRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        ScoreAdapter scoreAdapter = new ScoreAdapter(R.layout.item_textview);
        gpaBottomSheetRecyclerView.setAdapter(scoreAdapter);

        gpaBottomSheetDialog = new BottomSheetDialog(requireContext(), R.style.BottomSheetDialogBg);
        gpaBottomSheetDialog.setCanceledOnTouchOutside(true);
        // 遮罩
        gpaBottomSheetDialog.getWindow().setDimAmount(.3f);
        gpaBottomSheetDialog.setContentView(view);
        // 该GPA统计下的课程总数
        List<CourseScore> scoreList = (List<CourseScore>) pieEntry.getData();
        int total = scoreList.size();
        float totalCredit = 0;
        for (CourseScore score: scoreList) {
            totalCredit += score.getCredit();
        }
        gpaBottomSheetLabel.setText("GPA " + pieEntry.getLabel() + "  共" + Integer.toString(total) + "门  " + Float.toString(totalCredit) + "学分");
        scoreAdapter.setList((List<CourseScore>) pieEntry.getData());

        gpaBottomSheetDialog.show();
    }

}
