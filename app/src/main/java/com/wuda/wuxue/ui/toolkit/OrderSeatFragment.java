package com.wuda.wuxue.ui.toolkit;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.wuda.wuxue.R;
import com.wuda.wuxue.bean.OptionPair;
import com.wuda.wuxue.bean.Seat;
import com.wuda.wuxue.bean.SeatLocalHistory;
import com.wuda.wuxue.db.LibSeatDBUtility;
import com.wuda.wuxue.network.ResponseResult;
import com.wuda.wuxue.ui.base.DialogFactory;

import java.util.List;

public class OrderSeatFragment extends ToolFragment {

    Seat seat;

    TextView seat_tv;
    ChipGroup startTimeGroup;
    ChipGroup endTimeGroup;
    Button order_btn;

    OrderSeatViewModel mViewModel;
    LibSeatSharedViewModel mSharedViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (tool != null && tool instanceof Seat) {
            seat = (Seat) tool;
        } else {
            requireActivity().onBackPressed();
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_seat, container, false);

        seat_tv = view.findViewById(R.id.orderSeat_seat_textView);
        startTimeGroup = view.findViewById(R.id.orderSeat_startTime_chipGroup);
        endTimeGroup = view.findViewById(R.id.orderSeat_endTime_chipGroup);
        order_btn = view.findViewById(R.id.orderSeat_order_button);

        seat_tv.setText(seat.getRoom() + " " + seat.getNo());

        mViewModel = new ViewModelProvider(this).get(OrderSeatViewModel.class);
        mSharedViewModel = new ViewModelProvider(requireActivity()).get(LibSeatSharedViewModel.class);

        eventBinding();

        if (mViewModel.getStartTimeList().getValue() == null) {
            mViewModel.requestStartTimeList(seat.getId(), mSharedViewModel.date);
            showProgressBar();
        }

        return view;
    }


    private void eventBinding() {

        mViewModel.getStartTimeList().observe(getViewLifecycleOwner(), new Observer<List<OptionPair>>() {
            @Override
            public void onChanged(List<OptionPair> times) {
                closeProgressBar();
                startTimeGroup.removeAllViews();
                mViewModel.selectedStartTime = null;
                for (OptionPair time: times) {
                    Chip startTime_chip = new Chip(requireContext());
                    startTime_chip.setText(time.getName());
                    startTime_chip.setCheckable(true);

                    startTime_chip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (isChecked) {
                                mViewModel.selectedStartTime = time;
                                mViewModel.selectedEndTime = null;
                                endTimeGroup.removeAllViews();
                                showProgressBar();
                                mViewModel.requestEndTimeList(seat.getId(), mSharedViewModel.date);
                            }
                        }
                    });
                    startTimeGroup.addView(startTime_chip);
                }
            }
        });

        mViewModel.getEndTimeList().observe(getViewLifecycleOwner(), new Observer<List<OptionPair>>() {
            @Override
            public void onChanged(List<OptionPair> timeList) {
                closeProgressBar();
                for (OptionPair time: timeList) {
                    Chip endTime_chip = new Chip(requireContext());
                    endTime_chip.setText(time.getName());
                    endTime_chip.setCheckable(true);

                    endTime_chip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (isChecked) {
                                mViewModel.selectedEndTime = time;
                            }
                        }
                    });

                    endTimeGroup.addView(endTime_chip);
                }
            }
        });

        mViewModel.getSuccessResponse().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                closeProgressBar();
                new MaterialAlertDialogBuilder(requireContext())
                        .setMessage(Html.fromHtml(s))
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                requireActivity().onBackPressed();
                            }
                        }).create().show();
                // 本地历史
                String time = mSharedViewModel.date + " " + mViewModel.selectedStartTime.getName() + " -- " + mViewModel.selectedEndTime.getName();
                LibSeatDBUtility.saveLocalHistory(new SeatLocalHistory(time, seat));
            }
        });

        mViewModel.getFailResponse().observe(getViewLifecycleOwner(), new Observer<ResponseResult<?>>() {
            @Override
            public void onChanged(ResponseResult<?> result) {
                closeProgressBar();
                if (result == null) return;
                DialogFactory.errorInfoDialog(requireContext(), result).show();
                mViewModel.clearFailResponse();
            }
        });

        order_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgressBar();
                mViewModel.orderSeat(seat.getId(), mSharedViewModel.date, mSharedViewModel.tokens);
                order_btn.setEnabled(false);
            }
        });
    }
}