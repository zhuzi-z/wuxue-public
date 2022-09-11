package com.wuda.wuxue.ui.toolkit;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.wuda.wuxue.R;
import com.wuda.wuxue.bean.SchoolCalendar;
import com.wuda.wuxue.network.ResponseResult;
import com.wuda.wuxue.ui.base.DialogFactory;

import java.util.List;

public class SchoolCalendarFragment extends ToolFragment {

    private WebView schoolCalendarWebView;
    private SchoolCalendarViewModel mViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_school_calendar, container, false);


        schoolCalendarWebView = view.findViewById(R.id.schoolCalendar_webView);
        WebSettings webSettings = schoolCalendarWebView.getSettings();
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);

        FloatingActionButton add_fab = view.findViewById(R.id.schoolCalendar_floatingActionButton);
        add_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgressBar();
                mViewModel.requestCalendarList();
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(SchoolCalendarViewModel.class);

        mViewModel.getCalendarList().observe(getViewLifecycleOwner(), new Observer<List<SchoolCalendar>>() {
            @Override
            public void onChanged(List<SchoolCalendar> schoolCalendars) {

                closeProgressBar();

                final String[] items = new String[schoolCalendars.size()];
                for (int i=0; i<schoolCalendars.size(); i++) {
                    items[i] = schoolCalendars.get(i).getName();
                }

                final Integer[] choice = {-1};

                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (getContext() == null)
                                return;
                            new AlertDialog.Builder(getContext())
                                    .setTitle("请选择校历")
                                    .setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            choice[0] = i;
                                        }
                                    })
                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (choice[0] != -1) {
                                                mViewModel.requestCalendarContent(schoolCalendars.get(choice[0]));
                                            }
                                        }
                                    })
                                    .create()
                                    .show();
                        }
                    });

                }
            }
        });

        mViewModel.getSuccessResponse().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                closeProgressBar();
                schoolCalendarWebView.loadData(s, "text/html", "UTF-8");
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

        showProgressBar();
        mViewModel.queryCalendarContent();
    }
}