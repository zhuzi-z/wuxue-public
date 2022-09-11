package com.wuda.wuxue.ui.toolkit;

import android.annotation.SuppressLint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.wuda.wuxue.R;
import com.wuda.wuxue.bean.OptionPair;
import com.wuda.wuxue.bean.Seat;
import com.wuda.wuxue.network.ResponseResult;
import com.wuda.wuxue.ui.base.DialogFactory;
import com.wuda.wuxue.ui.base.FoldChipGroup;
import com.wuda.wuxue.ui.base.OptionFoldChipGroup;
import com.wuda.wuxue.util.SharePreferenceManager;

import java.util.List;
import java.util.Map;

public class SeatMapFragment extends ToolFragment {
    // 内容
    NestedScrollView nestedScrollView;
    HorizontalScrollView horizontalScrollView;
    ConstraintLayout seats_cl;
    // 选项相关
    LinearLayout header_ll;
    TextView selected_option_tv;
    LinearLayout optionContainer_ll;
    BottomSheetBehavior<?> optionBottomSheetBehavior;
    Button query_btn;
    //
    OptionFoldChipGroup building_cg;
    OptionFoldChipGroup room_cg;

    SeatViewModel mViewModel;
    LibSeatSharedViewModel mSharedViewModel;

    boolean initRequest = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_seat_map, container, false);

        nestedScrollView = view.findViewById(R.id.seat_map_nestedScrollView);
        horizontalScrollView = view.findViewById(R.id.seat_map_horizontalScrollView);
        seats_cl = view.findViewById(R.id.seat_map_constrainLayout);

        optionBottomSheetBehavior = BottomSheetBehavior.from(view.findViewById(R.id.seat_select_map_sheet));
        optionBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
//        optionBottomSheetBehavior.setPeekHeight(150);
        header_ll = view.findViewById(R.id.seat_selected_header_linearLayout);
        selected_option_tv = view.findViewById(R.id.seat_selected_option_textView);
        optionContainer_ll = view.findViewById(R.id.seat_options_linearLayout);
        query_btn = view.findViewById(R.id.seat_option_query_button);

        building_cg = new OptionFoldChipGroup(requireContext());
        building_cg.setLabel("场馆");
        optionContainer_ll.addView(building_cg);

        room_cg = new OptionFoldChipGroup(requireContext());
        room_cg.setLabel("房间");
        optionContainer_ll.addView(room_cg);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        closeProgressBar();

        mViewModel = new ViewModelProvider(this).get(SeatViewModel.class);
        mSharedViewModel = new ViewModelProvider(requireActivity()).get(LibSeatSharedViewModel.class);
        eventBinding();

        if (mViewModel.getAllOptions().getValue() == null)
            mViewModel.queryOptions();
    }

    @Override
    public void onStop() {
        super.onStop();
        mViewModel.scrollY = nestedScrollView.getScrollY();
        mViewModel.scrollX = horizontalScrollView.getScrollX();
    }

    private void eventBinding() {

        mViewModel.getAllOptions().observe(getViewLifecycleOwner(), new Observer<Map<String, List<OptionPair>>>() {
            @Override
            public void onChanged(Map<String, List<OptionPair>> allOptions) {
                List<OptionPair> buildings = allOptions.get(OptionPair.SEAT_BUILDING);
                for (int i=0; i<buildings.size(); ++i) {
                    if (buildings.get(i).getValue().equals("null")) {
                        buildings.remove(i);
                        --i;
                    }
                }
                building_cg.setItems(buildings);

                OptionPair building = mViewModel.getOptionPairByValue(SharePreferenceManager.loadString(SharePreferenceManager.LIB_SEAT_MAP_BUILDING), buildings);
                building_cg.select(building);
                mViewModel.selectedOptions.put(OptionPair.SEAT_BUILDING, building);
            }
        });

        mViewModel.getRooms().observe(getViewLifecycleOwner(), new Observer<List<OptionPair>>() {
            @Override
            public void onChanged(List<OptionPair> rooms) {
                for (int i=0; i<rooms.size(); ++i) {
                    if (rooms.get(i).getValue().equals("null")) {
                        rooms.remove(i);
                        ++i;
                    }
                }
                room_cg.setItems(rooms);

                OptionPair room = mViewModel.getOptionPairByValue(SharePreferenceManager.loadString(SharePreferenceManager.LIB_SEAT_MAP_ROOM), rooms);
                room_cg.select(room);
                mViewModel.selectedOptions.put(OptionPair.SEAT_ROOM, room);
                if (initRequest) {
                    mViewModel.requestSeats(mSharedViewModel.date);
                    initRequest = false;
                }
            }
        });

        mViewModel.getSuccessResponse().observe(getViewLifecycleOwner(), new Observer<List<Seat>>() {
            @Override
            public void onChanged(List<Seat> seats) {
                placeAllSeats(seats);
                // 从预约界面退出时
                if (mViewModel.scrollX != null && mViewModel.scrollY != null) {
                    horizontalScrollView.setScrollX(mViewModel.scrollX);
                    nestedScrollView.setScrollY(mViewModel.scrollY);
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

        building_cg.setSelectionChangedListener(new FoldChipGroup.OnSelectionChangedListener<OptionPair>() {
            @Override
            public void onSelectionChanged(View view, OptionPair selection, int index) {
                mViewModel.selectedOptions.put(OptionPair.SEAT_BUILDING, selection);
                mViewModel.queryRooms(selection.getValue());
                showSelectedOption();
            }
        });

        room_cg.setSelectionChangedListener(new FoldChipGroup.OnSelectionChangedListener<OptionPair>() {
            @Override
            public void onSelectionChanged(View view, OptionPair selection, int index) {
                mViewModel.selectedOptions.put(OptionPair.SEAT_ROOM, selection);
                showSelectedOption();
            }
        });

        query_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mViewModel.selectedOptions.get(OptionPair.SEAT_ROOM) == null) {
                    Toast.makeText(requireContext(), "请选择房间", Toast.LENGTH_SHORT).show();
                } else {
                    mViewModel.requestSeats(mSharedViewModel.date);
                    optionBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });
    }

    private void showSelectedOption() {
        StringBuilder builder = new StringBuilder();
        if (mViewModel.selectedOptions.get(OptionPair.SEAT_BUILDING) != null) {
            OptionPair building = mViewModel.selectedOptions.get(OptionPair.SEAT_BUILDING);
            SharePreferenceManager.storeString(SharePreferenceManager.LIB_SEAT_MAP_BUILDING, building.getValue());
            builder.append(building.getName());
        }
        if (mViewModel.selectedOptions.get(OptionPair.SEAT_ROOM) != null) {
            OptionPair room = mViewModel.selectedOptions.get(OptionPair.SEAT_ROOM);
            SharePreferenceManager.storeString(SharePreferenceManager.LIB_SEAT_MAP_ROOM, room.getValue());
            builder.append(" | ");
            builder.append(room.getName());
        }
        selected_option_tv.setText(builder);
    }

    private void placeAllSeats(List<Seat> seats) {
        seats_cl.removeAllViews();

        int[] bgColors = new int[] {
                requireContext().getColor(R.color.seat_free),
                requireContext().getColor(R.color.seat_using),
                requireContext().getColor(R.color.seat_ordered),
                requireContext().getColor(R.color.seat_left),
                requireContext().getColor(R.color.seat_disable)
        };

        @SuppressLint("UseCompatLoadingForDrawables")
        Drawable[] bgIcons = new Drawable[] {
                requireContext().getDrawable(R.drawable.ic_seat_nothing),
                requireContext().getDrawable(R.drawable.ic_seat_power),
                requireContext().getDrawable(R.drawable.ic_seat_window),
                requireContext().getDrawable(R.drawable.ic_seat_power_window)
        };

        for (Seat seat: seats) {

            if (seat.getType() >> 4 > 0) {
                Log.d("seat", seat.getNo() + String.valueOf(seat.getType() >> 4));
            }

            RelativeLayout seatItem_rl = new RelativeLayout(requireContext());
            seatItem_rl.setPadding(8, 8, 8, 8);
            seatItem_rl.setGravity(Gravity.CENTER);
            ImageView bg_iv = new ImageView(requireContext());
            bg_iv.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            // 复制一个对象 drawable.getConstantState().newDrawable()
            bg_iv.setImageDrawable(bgIcons[seat.getType() & 0x0f].getConstantState().newDrawable());
            bg_iv.setColorFilter(bgColors[seat.getType() >> 4]);
            seatItem_rl.addView(bg_iv);
            TextView number_tv = new TextView(seatItem_rl.getContext());
            number_tv.setPadding(0, 2, 0, 0);
            RelativeLayout.LayoutParams number_param = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            number_param.addRule(RelativeLayout.CENTER_HORIZONTAL);
            number_tv.setLayoutParams(number_param);
            number_tv.setGravity(Gravity.CENTER);
            number_tv.setText(seat.getNo());
            number_tv.setTypeface(null, Typeface.BOLD);
            seatItem_rl.addView(number_tv);

            seatItem_rl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    seat.setRoom(mViewModel.selectedOptions.get(OptionPair.SEAT_ROOM).getName());
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("tool", seat);
                    OrderSeatFragment orderSeatFragment = new OrderSeatFragment();
                    orderSeatFragment.setArguments(bundle);
                    navigationTo(orderSeatFragment);
                }
            });

            ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
            params.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
            params.leftMargin = (seat.getCol()) * 150;
            params.topMargin = seat.getRow() * 150;
            seatItem_rl.setLayoutParams(params);
            seats_cl.addView(seatItem_rl);
        }
    }
}