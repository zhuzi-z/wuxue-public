package com.wuda.wuxue.ui.toolkit;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.wuda.wuxue.R;
import com.wuda.wuxue.bean.OptionPair;
import com.wuda.wuxue.bean.SeatLocalHistory;
import com.wuda.wuxue.bean.SeatOnlineHistory;
import com.wuda.wuxue.bean.SeatSelectorWithMap;
import com.wuda.wuxue.bean.SeatSelectorWithOptions;
import com.wuda.wuxue.network.LoginFailException;
import com.wuda.wuxue.network.ResponseResult;
import com.wuda.wuxue.ui.adapter.SeatHistoryAdapter;
import com.wuda.wuxue.ui.base.DialogFactory;
import com.wuda.wuxue.ui.base.FullScreenDialog;
import com.wuda.wuxue.ui.base.WaitForLoginFragment;
import com.wuda.wuxue.ui.mine.AccountActivity;

import java.util.List;

public class LibSeatFragment extends ToolFragment {

    FullScreenDialog fullScreenDialog;
    WaitForLoginFragment waitForLoginFragment;

    TextView date_tv;
    ImageView free_mapping_iv;
    ImageView floor_mapping_iv;
    RecyclerView history_rv;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch historyType_switch;

    SeatHistoryAdapter historyAdapter;

    LibSeatViewModel mViewModel;
    LibSeatSharedViewModel mSharedViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_lib_seat, container, false);
        date_tv = view.findViewById(R.id.lib_seat_date_textView);
        free_mapping_iv = view.findViewById(R.id.lib_seat_free_imageView);
        floor_mapping_iv = view.findViewById(R.id.lib_seat_floor_imageView);
        history_rv = view.findViewById(R.id.lib_seat_history_recyclerView);
        historyType_switch = view.findViewById(R.id.lib_seat_type_switch);

        historyAdapter = new SeatHistoryAdapter(R.layout.item_seat_history);
        historyAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                Object history = adapter.getData().get(position);
                if (history instanceof SeatLocalHistory) {
                    view.findViewById(R.id.seat_history_action_textView).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            OrderSeatFragment orderSeatFragment = new OrderSeatFragment();
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("tool", ((SeatLocalHistory) history).getSeat());
                            orderSeatFragment.setArguments(bundle);
                            navigationTo(orderSeatFragment);
                        }
                    });
                } else if (history instanceof SeatOnlineHistory) {
                    view.findViewById(R.id.seat_history_action_textView).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (((SeatOnlineHistory) history).getState() == SeatOnlineHistory.STATE_NORMAL) {
                                mViewModel.cancelOrder(((SeatOnlineHistory) history).getVisaId());
                            }
                        }
                    });
                }
            }
        });

        history_rv.setLayoutManager(new LinearLayoutManager(getContext()));
        history_rv.setAdapter(historyAdapter);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(LibSeatViewModel.class);
        mSharedViewModel = new ViewModelProvider(requireActivity()).get(LibSeatSharedViewModel.class);

        eventBinding();
        if (mViewModel.getSuccessResponse().getValue() == null) {
            mViewModel.login();
            fullScreenDialog = new FullScreenDialog();
            fullScreenDialog.setOnCancelClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    requireActivity().onBackPressed();
                }
            });
            waitForLoginFragment = new WaitForLoginFragment();
            waitForLoginFragment.setOnRetryListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    waitForLoginFragment.setState(WaitForLoginFragment.STATE_LOGIN);
                    mViewModel.login();
                }
            });
            fullScreenDialog.setFragment(waitForLoginFragment);
            fullScreenDialog.show(getChildFragmentManager());
        }
    }

    private void eventBinding() {

        mViewModel.getSuccessResponse().observe(getViewLifecycleOwner(), new Observer<List<OptionPair>>() {
            @Override
            public void onChanged(List<OptionPair> optionPairs) {
                if (fullScreenDialog != null)
                    fullScreenDialog.dismiss();
                if (mViewModel.getLocalHistory().getValue() == null)
                    mViewModel.queryLocalHistory();
                mSharedViewModel.tokens = optionPairs;
            }
        });

        mViewModel.getFailResponse().observe(getViewLifecycleOwner(), new Observer<ResponseResult<?>>() {
            @Override
            public void onChanged(ResponseResult<?> result) {
                if (result == null) return;
                if (result.getException() != null && result.getException() instanceof LoginFailException) {
                    waitForLoginFragment.setState(WaitForLoginFragment.STATE_FAIL);
                    Intent intent = new Intent(requireContext(), AccountActivity.class);
                    startActivity(intent);
                    return;
                }
                DialogFactory.errorInfoDialog(requireContext(), result).show();
                mViewModel.clearFailResponse();
            }
        });

        mViewModel.getOnlineHistory().observe(getViewLifecycleOwner(), new Observer<List<SeatOnlineHistory>>() {
            @Override
            public void onChanged(List<SeatOnlineHistory> seatOnlineHistories) {
                historyAdapter.setList(seatOnlineHistories);
            }
        });

        mViewModel.getCancelResponse().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                mViewModel.requestOnlineHistory();
            }
        });

        mViewModel.getLocalHistory().observe(getViewLifecycleOwner(), new Observer<List<SeatLocalHistory>>() {
            @Override
            public void onChanged(List<SeatLocalHistory> seatLocalHistories) {
                historyAdapter.setList(seatLocalHistories);
            }
        });

        date_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSharedViewModel.today) {
                    date_tv.setText("明天");
                    Toast.makeText(requireContext(), "请勿在22:45前预约次日的座位，否则会遭到系统限制，一段时间内无法操作。", Toast.LENGTH_SHORT).show();
                } else {
                    date_tv.setText("今天");
                }
                mSharedViewModel.changeDate();
            }
        });

        free_mapping_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("tool", new SeatSelectorWithOptions());
                SeatOptionFragment seatOptionFragment = new SeatOptionFragment();
                seatOptionFragment.setArguments(bundle);
                navigationTo(seatOptionFragment);
            }
        });

        floor_mapping_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("tool", new SeatSelectorWithMap());
                SeatMapFragment seatMapFragment = new SeatMapFragment();
                seatMapFragment.setArguments(bundle);
                navigationTo(seatMapFragment);
            }
        });

        historyType_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mViewModel.requestOnlineHistory();
                } else {
                    mViewModel.queryLocalHistory();
                }
            }
        });
    }
}