package com.wuda.wuxue.ui.adapter;

import android.annotation.SuppressLint;
import android.graphics.Typeface;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StyleSpan;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.wuda.wuxue.R;
import com.wuda.wuxue.bean.SeatLocalHistory;
import com.wuda.wuxue.bean.SeatOnlineHistory;

public class SeatHistoryAdapter extends BaseQuickAdapter<Object, BaseViewHolder> {
    public SeatHistoryAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, Object o) {
        if (o instanceof SeatLocalHistory) {
            convertLocalHistory(baseViewHolder, (SeatLocalHistory) o);
        } else if (o instanceof SeatOnlineHistory) {
            convertOnlineHistory(baseViewHolder, (SeatOnlineHistory) o);
        }
    }

    @SuppressLint("ResourceAsColor")
    private void convertLocalHistory(@NonNull BaseViewHolder baseViewHolder, SeatLocalHistory seatLocalHistory) {

        SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append(seatLocalHistory.getTime(), new StyleSpan(Typeface.BOLD), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        builder.append("\n");
        builder.append(seatLocalHistory.getSeat().getRoom()).append(" ").append(seatLocalHistory.getSeat().getNo());
        baseViewHolder.setText(R.id.seat_history_info_textView, builder);

        baseViewHolder.setTextColor(R.id.seat_history_action_textView, R.color.QiuBoLan);
        baseViewHolder.setText(R.id.seat_history_action_textView, "重新预约");
    }

    @SuppressLint("ResourceAsColor")
    private void convertOnlineHistory(@NonNull BaseViewHolder baseViewHolder, SeatOnlineHistory seatOnlineHistory) {

        SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append(seatOnlineHistory.getTime(), new StyleSpan(Typeface.BOLD), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        builder.append("\n");
        builder.append(seatOnlineHistory.getTitle());
        baseViewHolder.setText(R.id.seat_history_info_textView, builder);

        switch (seatOnlineHistory.getState()) {
            case SeatOnlineHistory.STATE_CANCELED:
                baseViewHolder.setText(R.id.seat_history_action_textView, "已取消");
                baseViewHolder.setTextColor(R.id.seat_history_action_textView, R.color.DaLiShiHui);
                break;
            case SeatOnlineHistory.STATE_NORMAL:
                baseViewHolder.setText(R.id.seat_history_action_textView, "取消预约");
                baseViewHolder.setTextColor(R.id.seat_history_action_textView, R.color.QiuBoLan);
                break;
        }
    }
}
