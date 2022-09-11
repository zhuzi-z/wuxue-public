package com.wuda.wuxue.ui.toolkit;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.wuda.wuxue.R;
import com.wuda.wuxue.bean.BookInfo;
import com.wuda.wuxue.bean.BookItem;
import com.wuda.wuxue.network.ResponseResult;
import com.wuda.wuxue.ui.base.DialogFactory;

import java.util.ArrayList;
import java.util.List;

public class BookInfoFragment extends ToolFragment {

    BookItem bookItem;
    BookInfoViewModel mViewModel;

    TextView info_tv;
    TableLayout collectionInfo_table;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (tool != null && tool instanceof BookItem) {
            bookItem = (BookItem) tool;
        } else {
            requireActivity().onBackPressed();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_book_info, container, false);

        info_tv = view.findViewById(R.id.book_detailInfo_textView);
        collectionInfo_table = view.findViewById(R.id.book_collectionInfo_table);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(BookInfoViewModel.class);

        mViewModel.getSuccessResponse().observe(getViewLifecycleOwner(), new Observer<BookInfo>() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onChanged(BookInfo bookInfo) {
                closeProgressBar();

                SpannableStringBuilder builder = new SpannableStringBuilder();
                builder.append(labelSSBuilder("题名", bookInfo.getTitle()))
                        .append("\n")
                        .append(labelSSBuilder("作者", bookInfo.getAuthor()))
                        .append("\n")
                        .append(labelSSBuilder("主题词", bookInfo.getKeyWord()))
                        .append("\n")
                        .append(labelSSBuilder("出版发行", bookInfo.getPublisher()))
                        .append("\n")
                        .append(labelSSBuilder("ISBN", bookInfo.getISBN()))
                        .append("\n")
                        .append(labelSSBuilder("摘要", bookInfo.getDigest()))
                        .append("\n\n")
                        .append(labelSSBuilder("全馆馆藏", ""));
                info_tv.setText(builder);

                collectionInfo_table.setDividerDrawable(requireContext().getDrawable(R.drawable.table_h_divider));
                collectionInfo_table.setShowDividers(LinearLayout.SHOW_DIVIDER_BEGINNING | LinearLayout.SHOW_DIVIDER_MIDDLE | LinearLayout.SHOW_DIVIDER_END);

                List<BookInfo.CollectionInfo> collectionInfoList = bookInfo.getCollectionInfoList();
                for (int i=0; i<collectionInfoList.size(); ++i) {
                    for (TableRow row: initTableRowList(collectionInfoList.get(i))) {
                        collectionInfo_table.addView(row);
                    }
                    if (i != collectionInfoList.size()-1) {
                        TableRow splitRow = new TableRow(getContext());
                        splitRow.addView(new TextView(getContext()));
                        collectionInfo_table.addView(splitRow);
                    }
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

        if (mViewModel.getSuccessResponse().getValue() == null)
            mViewModel.requestBookInfo(bookItem);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private List<TableRow> initTableRowList(BookInfo.CollectionInfo collectionInfo) {

        List<TableRow> tableRowList = new ArrayList<>();

        Context mContext = requireContext();
        String[] header = {"单册状态", "应还日期", "分馆", "架位", "请求数", "条码"};
        String[] body = {
                collectionInfo.getStatus(), collectionInfo.getReturnDate(),
                collectionInfo.getBranch(), collectionInfo.getShelfId(),
                collectionInfo.getRequestNum(), collectionInfo.getBarCode()
        };

        TableRow.LayoutParams headerParams = new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
        headerParams.setMarginStart(8);
        TableRow.LayoutParams bodyParams = new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 2);
        bodyParams.setMarginStart(8);

        for (int i=0; i<header.length; ++i) {
            TableRow row = new TableRow(mContext);
            row.setDividerDrawable(requireContext().getDrawable(R.drawable.table_v_divider));
            row.setShowDividers(LinearLayout.SHOW_DIVIDER_BEGINNING | LinearLayout.SHOW_DIVIDER_MIDDLE | LinearLayout.SHOW_DIVIDER_END);

            TextView header_tv = new TextView(mContext);
            header_tv.setTextSize(16);
            header_tv.setText(header[i]);
            header_tv.setLayoutParams(headerParams);
            TextView body_tv = new TextView(mContext);
            body_tv.setTextSize(16);
            body_tv.setLayoutParams(bodyParams);
            body_tv.setText(body[i]);

            row.addView(header_tv);
            row.addView(body_tv);
            tableRowList.add(row);
        }

        return tableRowList;
    }

    private SpannableStringBuilder labelSSBuilder(String label, String content) {
        SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append(label);
        builder.append(" : ");
        builder.setSpan(new StyleSpan(Typeface.BOLD), 0, builder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.append(content);
        return builder;
    }
}