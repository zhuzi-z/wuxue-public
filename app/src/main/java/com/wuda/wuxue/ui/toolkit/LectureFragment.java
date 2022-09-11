package com.wuda.wuxue.ui.toolkit;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.wuda.wuxue.R;
import com.wuda.wuxue.bean.BaseInfo;
import com.wuda.wuxue.bean.Lecture;
import com.wuda.wuxue.db.InfoDBUtility;
import com.wuda.wuxue.network.ResponseResult;
import com.wuda.wuxue.ui.adapter.SimpleInfoAdapter;
import com.wuda.wuxue.ui.base.DialogFactory;
import com.wuda.wuxue.util.SharePreferenceManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class LectureFragment extends ToolFragment{

    RecyclerView recyclerView;
    SimpleInfoAdapter<Lecture> mAdapter;
    LectureViewModel mViewModel;

    View dateSelector_view;
    TextView startDate_tv;
    TextView endDate_tv;
    Button query_btn;

    @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.recycler_view, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));

        mAdapter = new SimpleInfoAdapter<Lecture>(R.layout.item_textview) {
            @Override
            protected SpannableStringBuilder format(Lecture lecture) {
                SpannableStringBuilder builder = new SpannableStringBuilder();
                builder.append(lecture.getTitle());
                builder.append("\n" + lecture.getOrganizer() + " | " + lecture.getTime(),
                        new RelativeSizeSpan(0.75f),
                        Spanned.SPAN_INCLUSIVE_EXCLUSIVE
                );
                return builder;
            }
        };

        dateSelector_view = getLayoutInflater().inflate(R.layout.header_date_selector, recyclerView, false);
        startDate_tv = dateSelector_view.findViewById(R.id.date_selector_start_textView);
        endDate_tv = dateSelector_view.findViewById(R.id.date_selector_end_textView);
        query_btn = dateSelector_view.findViewById(R.id.date_selector_query_button);
        startDate_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                        .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                        .build();
                datePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
                    @Override
                    public void onPositiveButtonClick(Long selection) {
                        String date = dateFormat.format(selection);
                        startDate_tv.setText(date);
                    }
                });
                datePicker.show(getParentFragmentManager(), datePicker.toString());
            }
        });
        endDate_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                        .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                        .build();
                datePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
                    @Override
                    public void onPositiveButtonClick(Long selection) {
                        String date = dateFormat.format(selection);
                        endDate_tv.setText(date);
                    }
                });
                datePicker.show(getParentFragmentManager(), datePicker.toString());
            }
        });
        query_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                query();
            }
        });
        mAdapter.setHeaderView(dateSelector_view);

        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                Lecture lecture = (Lecture) adapter.getData().get(position);
                Bundle bundle = new Bundle();
                bundle.putSerializable("tool", lecture);
                LectureContentFragment lectureContentFragment = new LectureContentFragment();
                lectureContentFragment.setArguments(bundle);
                navigationTo(lectureContentFragment);
            }
        });

        View emptyView = getLayoutInflater().inflate(R.layout.empty_view, recyclerView, false);
        mAdapter.setEmptyView(emptyView);
        mAdapter.setHeaderWithEmptyEnable(true);

        recyclerView.setAdapter(mAdapter);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mViewModel = new ViewModelProvider(this).get(LectureViewModel.class);
        mViewModel.getSuccessResponse().observe(getViewLifecycleOwner(), new Observer<List<Lecture>>() {
            @Override
            public void onChanged(List<Lecture> lectures) {
                mAdapter.setList(lectures);
                Set<String> subscribed_info = SharePreferenceManager.loadStringSet(SharePreferenceManager.SUBSCRIBE_INFO_SELECTED_ITEMS);
                if (subscribed_info.contains(BaseInfo.CATEGORY_LECTURE))
                    InfoDBUtility.saveInfoId(lectures);
            }
        });

        mViewModel.getFailResponse().observe(getViewLifecycleOwner(), new Observer<ResponseResult<?>>() {
            @Override
            public void onChanged(ResponseResult<?> result) {
                if (result == null) return;
                DialogFactory.errorInfoDialog(requireContext(), result).show();
                mViewModel.clearFailResponse();
            }
        });

        if (mViewModel.start == null) {
            Calendar calendar = Calendar.getInstance();
            Date startDate = new Date();
            calendar.setTime(startDate);
            calendar.add(Calendar.DATE, 6);
            Date endDate = calendar.getTime();

            mViewModel.start = dateFormat.format(startDate);
            mViewModel.end = dateFormat.format(endDate);
        }
        startDate_tv.setText(mViewModel.start);
        endDate_tv.setText(mViewModel.end);


        if (mViewModel.getSuccessResponse().getValue() == null)
            query();
    }

    private void query() {
        mViewModel.start = (String) startDate_tv.getText();
        mViewModel.end = (String) endDate_tv.getText();
        mViewModel.requestLectures();
    }
}
