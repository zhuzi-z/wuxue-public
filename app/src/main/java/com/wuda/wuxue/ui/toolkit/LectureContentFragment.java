package com.wuda.wuxue.ui.toolkit;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.wuda.wuxue.R;
import com.wuda.wuxue.bean.Lecture;

import java.text.SimpleDateFormat;
import java.util.Date;

public class LectureContentFragment extends ToolFragment {

    private Lecture lecture;

    private TextView title_tv;
    private TextView reporter_tv;
    private TextView time_tv;
    private TextView position_tv;
    private TextView organizer_tv;
    private TextView reporterIntroduction_tv;
    private TextView content_tv;
    private ImageView qrCode_iv;
    private ImageView poster_iv;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (tool != null && tool instanceof Lecture) {
            lecture = (Lecture) tool;
        } else {
            requireActivity().onBackPressed();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_lecture_content, container, false);

        title_tv = view.findViewById(R.id.lecture_title_textView);
        reporter_tv = view.findViewById(R.id.lecture_reporter_textView);
        time_tv = view.findViewById(R.id.lecture_time_textView);
        position_tv = view.findViewById(R.id.lecture_position_textView);
        organizer_tv = view.findViewById(R.id.lecture_organizer_textView);
        reporterIntroduction_tv = view.findViewById(R.id.lecture_repoter_introduction_textView);
        content_tv = view.findViewById(R.id.lecutre_content_textView);
        qrCode_iv = view.findViewById(R.id.lecture_qrCode_imageView);
        poster_iv = view.findViewById(R.id.lecture_poster_imageView);

        show();

        closeProgressBar();

        return view;
    }

    private void show() {
        title_tv.setText(lecture.getTitle());
        reporter_tv.setText(lecture.getReporter());
        time_tv.setText(this.getLectureTime());
        position_tv.setText(lecture.getPosition());
        organizer_tv.setText(lecture.getOrganizer());
        reporterIntroduction_tv.setText(lecture.getIntroduction());
        content_tv.setText(lecture.getContent());

        String qrCodeUrl = lecture.getLive_qrcode();
        if(!qrCodeUrl.isEmpty())
            Glide.with(this).load(qrCodeUrl).into(qrCode_iv);

        String posterUrl = lecture.getPoster();
        if(!posterUrl.isEmpty())
            Glide.with(this).load(posterUrl).into(poster_iv);

    }

    private String getLectureTime() {
        StringBuilder time = new StringBuilder();

        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        Date date = new Date(lecture.getStart_time());
        time.append(sdf.format(date));

        if (lecture.getEnd_time() != 0) {
            time.append(" - ");
            date.setTime(lecture.getEnd_time());
            time.append(sdf.format(lecture.getEnd_time()));
        }

        return time.toString();
    }
}