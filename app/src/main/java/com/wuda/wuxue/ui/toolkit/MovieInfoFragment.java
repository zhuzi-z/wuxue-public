package com.wuda.wuxue.ui.toolkit;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.wuda.wuxue.R;
import com.wuda.wuxue.bean.Movie;
import com.wuda.wuxue.network.ResponseResult;
import com.wuda.wuxue.ui.base.DialogFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MovieInfoFragment extends ToolFragment {

    private Movie movie;
    private MovieInfoViewModel mViewModel;

    private ImageView posterImageView;
    private TextView title_tv;
    private TextView time_tv;
    private TextView place_tv;
    private TextView type_tv;
    private TextView actors_tv;
    private TextView storyline_tv;
    private TextView douban_tv;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (tool != null && tool instanceof Movie) {
            movie = (Movie) tool;
        } else {
            requireActivity().onBackPressed();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_movie_info, container, false);

        posterImageView = view.findViewById(R.id.movie_poster_iv);
        title_tv = view.findViewById(R.id.movie_title_tv);
        time_tv = view.findViewById(R.id.movie_time_tv);
        place_tv = view.findViewById(R.id.movie_place_tv);
        type_tv = view.findViewById(R.id.movie_type_tv);
        actors_tv = view.findViewById(R.id.movie_actors_tv);
        storyline_tv = view.findViewById(R.id.movie_storyline_tv);
        douban_tv = view.findViewById(R.id.movie_douban_tv);

        showContent();

        closeProgressBar();

        Pattern pattern = Pattern.compile("(?<=《).*?(?=》)");
        Matcher matcher = pattern.matcher(movie.getTitle());
        if (matcher.find()) {
            douban_tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("https://m.douban.com/search?query=" + matcher.group()));
                    startActivity(intent);
                }
            });
        } else {
            douban_tv.setVisibility(View.GONE);
        }

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(MovieInfoViewModel.class);

        mViewModel.getSuccessResponse().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                storyline_tv.append(Html.fromHtml(s));
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

        if (mViewModel.getSuccessResponse().getValue() == null) {
            mViewModel.requestStoryline(movie.getId());
        }

    }

    private void showContent() {
        Glide.with(this)
                .load(movie.getPoster())
                .error(R.drawable.ic_broken_image)
                .into(posterImageView);

        title_tv.append(movie.getTitle());
        time_tv.append(movie.getTime());
        place_tv.append(movie.getPlace());
        type_tv.append(movie.getType());
        actors_tv.append(movie.getActors());
    }
}