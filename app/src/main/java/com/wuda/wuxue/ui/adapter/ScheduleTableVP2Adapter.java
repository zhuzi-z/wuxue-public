package com.wuda.wuxue.ui.adapter;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.wuda.wuxue.ui.course.ScheduleFragment;
import com.wuda.wuxue.ui.course.ScheduleTableFragment;

public class ScheduleTableVP2Adapter extends FragmentStateAdapter {
    int totalWeeks = 18;

    // 向下传递 getParentFragment
    public ScheduleTableVP2Adapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        ScheduleTableFragment fragment = new ScheduleTableFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ScheduleTableFragment.ARG_WEEK, position + 1);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public int getItemCount() {
        return totalWeeks;
    }
}
