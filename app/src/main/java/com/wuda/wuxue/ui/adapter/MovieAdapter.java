package com.wuda.wuxue.ui.adapter;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.BaseLoadMoreModule;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.wuda.wuxue.R;
import com.wuda.wuxue.bean.Movie;
import com.wuda.wuxue.network.ServerURL;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MovieAdapter extends BaseQuickAdapter<Movie, BaseViewHolder> implements LoadMoreModule {

    String time;

    @SuppressLint("SimpleDateFormat")
    public MovieAdapter(int layoutResId) {
        super(layoutResId);
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        time = sdf.format(Calendar.getInstance().getTime());
    }

    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, Movie movie) {
        ImageView poster_iv = baseViewHolder.getView(R.id.item_movie_poster_iv);
        if (movie.getPoster().equals(ServerURL.MOVIE_POSTER)) { // 无文件ID（暂停通知等）
            Glide.with(baseViewHolder.itemView)
                    .load(R.drawable.ic_movie)
                    .into(poster_iv);
        } else {
            Glide.with(baseViewHolder.itemView)
                    .load(movie.getPoster())
                    .error(R.drawable.ic_broken_image)
                    .into(poster_iv);
        }
        baseViewHolder.setText(R.id.item_movie_info_tv, formatInfo(movie));
    }

    @NonNull
    @Override
    public BaseLoadMoreModule addLoadMoreModule(@NonNull BaseQuickAdapter<?, ?> baseQuickAdapter) {
        return new BaseLoadMoreModule(this);
    }

    SpannableStringBuilder formatInfo(Movie movie) {
        SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append(movie.getTitle(), new RelativeSizeSpan(1.25f), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        builder.append("\n类型：").append(movie.getType())
                .append("\n主演：").append(movie.getActors())
                .append("\n时间：").append(movie.getTime());
        // 过期项目
        if (time.compareTo(movie.getTime()) >= 0) {
            builder.setSpan(new ForegroundColorSpan(Color.LTGRAY), 0, builder.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        }

        return builder;
    }
}
