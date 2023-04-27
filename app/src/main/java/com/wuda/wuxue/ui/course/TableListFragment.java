package com.wuda.wuxue.ui.course;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.wuda.wuxue.R;
import com.wuda.wuxue.bean.Timetable;
import com.wuda.wuxue.ui.adapter.TimetableAdapter;
import com.wuda.wuxue.ui.base.NavigationHost;
import com.wuda.wuxue.util.SharePreferenceManager;

import java.util.List;

public class TableListFragment extends Fragment {
    RecyclerView recyclerView;
    TextView footer_tv;
    ScheduleManagerViewModel mViewModel;
    TimetableAdapter mAdapter;

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tabel_list, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));

        mAdapter = new TimetableAdapter(R.layout.item_textview);

        footer_tv = new TextView(requireContext());
        footer_tv.setGravity(Gravity.CENTER);
        footer_tv.setPadding(8, 8, 8, 8);
        footer_tv.setText("课表的实现参考了'WakeUp课程表'，如需要更多的功能请下载'WakeUp课程表'");

        mAdapter.setFooterView(footer_tv);
        recyclerView.setAdapter(mAdapter);

        ((ScheduleManagerActivity) requireActivity()).getSupportActionBar().setTitle("所有课表");

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mViewModel = new ViewModelProvider(requireActivity()).get(ScheduleManagerViewModel.class);
        eventBinding();

        if (mViewModel.getTimetable().getValue() == null)
            mViewModel.queryTimetable();

        requireActivity().addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.add_shechdule, menu);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.schedule_add) {
                    mViewModel.createTimeTable();
                    mViewModel.queryTimetable();
                }
                return true;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);
    }

    private void eventBinding() {

        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                Timetable table = (Timetable) adapter.getData().get(position);
                ((NavigationHost) requireActivity()).navigationTo(TableManagerFragment.newInstance(table), true);
            }
        });

        mViewModel.getTimetable().observe(getViewLifecycleOwner(), new Observer<List<Timetable>>() {
            @Override
            public void onChanged(List<Timetable> timetables) {
                mAdapter.setCurrentTableId(SharePreferenceManager.loadInteger(SharePreferenceManager.SCHEDULE_CURRENT_TABLE_ID));
                mAdapter.setList(timetables);
            }
        });
    }
}