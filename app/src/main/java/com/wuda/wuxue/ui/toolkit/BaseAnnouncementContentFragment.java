package com.wuda.wuxue.ui.toolkit;

import android.annotation.SuppressLint;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.wuda.wuxue.R;
import com.wuda.wuxue.bean.Announcement;
import com.wuda.wuxue.network.ResponseResult;
import com.wuda.wuxue.ui.base.DialogFactory;
import com.wuda.wuxue.ui.base.GlideImageGetter;

import org.xml.sax.XMLReader;

public class BaseAnnouncementContentFragment extends ToolFragment {

    private Announcement announcement;
    AnnouncementViewModel mViewModel;
    TextView header_tv;
    TextView body_tv;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (tool !=null && tool instanceof Announcement) {
            announcement = (Announcement) tool;
        } else {
            requireActivity().onBackPressed();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_announcement_content, container, false);

        header_tv = view.findViewById(R.id.announcement_header_textView);
        body_tv = view.findViewById(R.id.announcement_body_textView);

        body_tv.setMovementMethod(LinkMovementMethod.getInstance());
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mViewModel = (AnnouncementViewModel) new ViewModelProvider(this).get(AnnouncementViewModelFactory.getViewModelClz(announcement.getUrl()));
        mViewModel.getContent().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                closeProgressBar();
                // 标题（本地）
                SpannableStringBuilder header = new SpannableStringBuilder();
                header.append(announcement.getTitle(), new StyleSpan(Typeface.BOLD), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                header.setSpan(new RelativeSizeSpan(1.25f), 0, header.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                header.append("\n");
                int start = header.length();
                header.append(announcement.getDepartment() + "    |    " + announcement.getTime(), new RelativeSizeSpan(0.75f), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                header.setSpan(new StyleSpan(Typeface.ITALIC), start, header.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                header_tv.setText(header);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    body_tv.setText(Html.fromHtml(s, Html.FROM_HTML_MODE_LEGACY, new GlideImageGetter(body_tv, true, null), new Html.TagHandler() {
                        @Override
                        public void handleTag(boolean b, String s, Editable editable, XMLReader xmlReader) {

                        }
                    }));
                } else {
                    body_tv.setText(Html.fromHtml(s, new GlideImageGetter(body_tv, true, null), new Html.TagHandler() {
                        @Override
                        public void handleTag(boolean b, String s, Editable editable, XMLReader xmlReader) {

                        }
                    }));
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

        if (mViewModel.getSuccessResponse().getValue() == null) {
            mViewModel.requestContent(announcement);
        }
        showProgressBar();
    }
}