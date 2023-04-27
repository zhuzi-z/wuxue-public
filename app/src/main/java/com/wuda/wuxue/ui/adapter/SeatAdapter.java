package com.wuda.wuxue.ui.adapter;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.BaseLoadMoreModule;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.wuda.wuxue.R;
import com.wuda.wuxue.bean.Seat;

public class SeatAdapter extends BaseQuickAdapter<Seat, BaseViewHolder> implements LoadMoreModule {

    int[] bgColors;
    int[] bgIcons;

    public SeatAdapter(int layoutResId) {
        super(layoutResId);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        bgColors = new int[] {
                getContext().getColor(R.color.seat_free),
                getContext().getColor(R.color.seat_using),
                getContext().getColor(R.color.seat_ordered),
                getContext().getColor(R.color.seat_left)
        };
        bgIcons = new int[] {
                R.drawable.ic_seat_nothing,
                R.drawable.ic_seat_power,
                R.drawable.ic_seat_window,
                R.drawable.ic_seat_power_window
        };
    }

    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, Seat seat) {

        ImageView bg_iv = baseViewHolder.getView(R.id.item_seat_background_imageView);
        // getConstantState().newDrawable() => clone
//        bg_iv.setImageDrawable(bgIcons[seat.getType() & 0x0f].getConstantState().newDrawable());
        Glide.with(baseViewHolder.itemView)
                .load(bgIcons[seat.getType() & 0x0f])
                .into(bg_iv);
        bg_iv.setColorFilter(bgColors[seat.getType() >> 4]);
        baseViewHolder.setText(R.id.item_seat_num_textView, seat.getNo());
        baseViewHolder.setText(R.id.item_seat_room_textView, seat.getRoom());
    }

    @NonNull
    @Override
    public BaseLoadMoreModule addLoadMoreModule(@NonNull BaseQuickAdapter<?, ?> baseQuickAdapter) {
        return new BaseLoadMoreModule(this);
    }
}
