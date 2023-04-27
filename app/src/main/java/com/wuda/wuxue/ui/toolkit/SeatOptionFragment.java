package com.wuda.wuxue.ui.toolkit;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.listener.OnLoadMoreListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.wuda.wuxue.R;
import com.wuda.wuxue.bean.OptionPair;
import com.wuda.wuxue.bean.Seat;
import com.wuda.wuxue.network.ResponseResult;
import com.wuda.wuxue.ui.adapter.SeatAdapter;
import com.wuda.wuxue.ui.base.DialogFactory;
import com.wuda.wuxue.ui.base.FoldChipGroup;
import com.wuda.wuxue.ui.base.OptionFoldChipGroup;
import com.wuda.wuxue.util.SharePreferenceManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SeatOptionFragment extends ToolFragment {

    SeatOptionViewModel mViewModel;
    LibSeatSharedViewModel mSharedViewModel;

    // 内容
    RecyclerView recyclerView;
    SeatAdapter mAdapter;
    // 选项相关
    LinearLayout header_ll;
    TextView selected_option_tv;
    LinearLayout optionContainer_ll;
    BottomSheetBehavior<?> optionBottomSheetBehavior;
    Button query_btn;
    // 方便使用for循环
    String[] optionGroupLabel = new String[] {"场馆", "房间", "时长", "开始时间", "结束时间", "电源", "窗户"};
    String[] queryName = new String[] {"building", "room", "hour", "startMin", "endMin", "power", "window"};
    String[] SpSeatOptionKeys = new String[] {
            SharePreferenceManager.LIB_SEAT_OPTION_BUILDING, SharePreferenceManager.LIB_SEAT_OPTION_ROOM,
            SharePreferenceManager.LIB_SEAT_OPTION_DURATION, SharePreferenceManager.LIB_SEAT_OPTION_START_MIN,
            SharePreferenceManager.LIB_SEAT_OPTION_END_MIN, SharePreferenceManager.LIB_SEAT_OPTION_POWER,
            SharePreferenceManager.LIB_SEAT_OPTION_WINDOW
    };
    String[] OptionPairKeys = new String[] {
            OptionPair.SEAT_BUILDING, OptionPair.SEAT_ROOM, OptionPair.SEAT_DURATION,
            OptionPair.SEAT_START_MIN, OptionPair.SEAT_START_MIN,
            OptionPair.SEAT_POWER, OptionPair.SEAT_WINDOW
    };

    OptionFoldChipGroup[] allOptionGroup = new OptionFoldChipGroup[7];

    boolean initRequest = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_seat_selector_with_option, container, false);

        recyclerView = view.findViewById(R.id.seat_recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 4));
        mAdapter = new SeatAdapter(R.layout.item_seat);
        mAdapter.getLoadMoreModule().setAutoLoadMore(false);
        recyclerView.setAdapter(mAdapter);

        header_ll = view.findViewById(R.id.seat_selected_header_linearLayout);
        selected_option_tv = view.findViewById(R.id.seat_selected_option_textView);
        optionContainer_ll = view.findViewById(R.id.seat_options_linearLayout);
        query_btn = view.findViewById(R.id.seat_option_query_button);

        for (int i=0; i<optionGroupLabel.length; ++i) {
            allOptionGroup[i] = new OptionFoldChipGroup(getContext());
            allOptionGroup[i].setPadding(0, 16, 0, 0);
            allOptionGroup[i].setLabel(optionGroupLabel[i]);
            allOptionGroup[i].setTag(i); // 每次回调通过 i 获取具体的选择项组
            optionContainer_ll.addView(allOptionGroup[i]);
        }

        optionBottomSheetBehavior = BottomSheetBehavior.from(view.findViewById(R.id.seat_select_option_sheet));
        optionBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        closeProgressBar();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(SeatOptionViewModel.class);
        mSharedViewModel = new ViewModelProvider(requireActivity()).get(LibSeatSharedViewModel.class);

        eventBinding();
        if (mViewModel.getAllOptions().getValue() == null)
            mViewModel.queryOptions();

        if (!mViewModel.getData().isEmpty()) {
            mAdapter.setList(mViewModel.getData());
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        mViewModel.clearSuccessResponse();
    }

    private void eventBinding() {

        mViewModel.getAllOptions().observe(getViewLifecycleOwner(), new Observer<Map<String, List<OptionPair>>>() {
            @Override
            public void onChanged(Map<String, List<OptionPair>> allOptions) {
                for (int i=0; i<allOptionGroup.length; i++) {
                    if (i == 1) continue; // Room
                    allOptionGroup[i].setItems(allOptions.get(OptionPairKeys[i]));
                    OptionPair optionPair =mViewModel.getOptionPairByValue(SharePreferenceManager.loadString(SpSeatOptionKeys[i]), allOptions.get(OptionPairKeys[i]));
                    allOptionGroup[i].select(optionPair);
                    mViewModel.selectedOptions.put(queryName[i], optionPair);
                }
                showSelectedOption();
            }
        });

        mViewModel.getRooms().observe(getViewLifecycleOwner(), new Observer<List<OptionPair>>() {
            @Override
            public void onChanged(List<OptionPair> rooms) {
                allOptionGroup[1].setItems(rooms);

                OptionPair optionPair = mViewModel.getOptionPairByValue(SharePreferenceManager.loadString(SpSeatOptionKeys[1]), rooms);
                mViewModel.selectedOptions.put(queryName[1], optionPair);
                allOptionGroup[1].select(optionPair);
                showSelectedOption();

                if (initRequest) {
                    mViewModel.requestSeats(getParam(), mSharedViewModel.date);
                    initRequest = false;
                }
            }
        });

        mViewModel.getSuccessResponse().observe(getViewLifecycleOwner(), new Observer<List<Seat>>() {
            @Override
            public void onChanged(List<Seat> seats) {
                if (mViewModel.getTotalPage() == -2) {
                    Toast.makeText(getContext(), "操作过快，歇会吧", Toast.LENGTH_SHORT).show();
                    return;
                }
                mAdapter.addData(seats);
                if (mViewModel.hasMore()) {
                    mAdapter.getLoadMoreModule().loadMoreComplete();
                } else {
                    mAdapter.getLoadMoreModule().loadMoreEnd();
                }
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

        mAdapter.getLoadMoreModule().setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                mViewModel.requestSeats(getParam(), mSharedViewModel.date);
            }
        });

        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                Seat seat = (Seat) adapter.getData().get(position);
                OrderSeatFragment orderSeatFragment = new OrderSeatFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("tool", seat);
                orderSeatFragment.setArguments(bundle);
                navigationTo(orderSeatFragment);
            }
        });

        header_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (optionBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                    optionBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else if (optionBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                    optionBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });

        FoldChipGroup.OnSelectionChangedListener<OptionPair> listener = new FoldChipGroup.OnSelectionChangedListener<OptionPair>() {
            @Override
            public void onSelectionChanged(View view, OptionPair selection, int index) {
                int groupIdx = (int) view.getTag();
                mViewModel.selectedOptions.put(OptionPairKeys[groupIdx], selection);
                showSelectedOption();
                // 场馆选择
                if (groupIdx == 0) {
                    mViewModel.queryRooms(selection.getValue());
                }

                SharePreferenceManager.storeString(SpSeatOptionKeys[groupIdx], selection.getValue());
            }
        };

        for (OptionFoldChipGroup optionFoldChipGroup : allOptionGroup) {
            optionFoldChipGroup.setSelectionChangedListener(listener);
        }

        query_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 条件改变，清空数据
                mAdapter.setList(new ArrayList<>());
                mViewModel.getData().clear();
                mViewModel.setTotalPage(0);
                mViewModel.requestSeats(getParam(), mSharedViewModel.date);
                optionBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });
    }

    private Map<String, String> getParam() {
        Map<String, String> param = new HashMap<>();
        for (int i=0; i<queryName.length; i++) {
            OptionPair pair = mViewModel.selectedOptions.get(OptionPairKeys[i]);
            if (pair == null) {
                param.put(queryName[i], "null");
            } else {
                param.put(queryName[i], pair.getValue());
            }
        }
        return param;
    }

    @SuppressLint("SetTextI18n")
    private void showSelectedOption() {
        StringBuilder selections = new StringBuilder();
        for (String optionPairKey : OptionPairKeys) {
            OptionPair pair = mViewModel.selectedOptions.get(optionPairKey);
            if (pair != null && !pair.getValue().equals("null")) {
                selections.append(pair.getName()).append(" | ");
            }
        }
        if (selections.length() != 0) {
            selections.delete(selections.length()- 3, selections.length());
        }
        selected_option_tv.setText(selections);
    }
}