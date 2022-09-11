package com.wuda.wuxue.ui.toolkit;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.listener.OnLoadMoreListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.wuda.wuxue.R;
import com.wuda.wuxue.network.ResponseResult;
import com.wuda.wuxue.ui.adapter.BookAdapter;
import com.wuda.wuxue.bean.BookItem;
import com.wuda.wuxue.ui.base.DialogFactory;

import java.util.ArrayList;
import java.util.List;

public class BookSearchFragment extends ToolFragment {

    TextInputLayout kw_layout;
    TextInputEditText kw_et;
    RadioGroup filed_rg;
    RadioButton wti_btn;
    RadioButton tit_btn;
    RadioButton wau_btn;
    RadioButton wsu_btn;
    Button start_btn;
    RecyclerView result_rv;
    TextView amount_tv;

    BookSearchViewModel mViewModel;

//    String[] filed_list = new String[]{"wti", "tit", "wau", "wsu"};
    BookAdapter bookAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recycler_view, container, false);
        result_rv = view.findViewById(R.id.recyclerView);
        View header = inflater.inflate(R.layout.header_book_search, result_rv, false);

        kw_layout = header.findViewById(R.id.lib_search_kw_textInputLayout);
        kw_et = header.findViewById(R.id.lib_search_kw_et);
        filed_rg = header.findViewById(R.id.lib_search_filed_radioGroup);
        wti_btn = header.findViewById(R.id.lib_search_filed_wti_btn);
        tit_btn = header.findViewById(R.id.lib_search_filed_tit_btn);
        wau_btn = header.findViewById(R.id.lib_search_filed_wau_btn);
        wsu_btn = header.findViewById(R.id.lib_search_filed_wsu_btn);
        start_btn = header.findViewById(R.id.lib_search_start_btn);
        amount_tv = header.findViewById(R.id.lib_search_amount_textView);

        bookAdapter = new BookAdapter(R.layout.item_textview);
        bookAdapter.addHeaderView(header);
        bookAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                BookItem bookItem = (BookItem) adapter.getData().get(position);
                BookInfoFragment bookInfoFragment = new BookInfoFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("tool", bookItem);
                bookInfoFragment.setArguments(bundle);
                navigationTo(bookInfoFragment);
            }
        });
        bookAdapter.getLoadMoreModule().setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                mViewModel.loadMore();
            }
        });
        result_rv.setAdapter(bookAdapter);
        result_rv.addItemDecoration(new DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL));
        closeProgressBar();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(BookSearchViewModel.class);
        eventBinding();

        if (!mViewModel.getData().isEmpty()) {
            bookAdapter.setList(mViewModel.getData());
        }
    }

    private void eventBinding() {

        mViewModel.getSuccessResponse().observe(getViewLifecycleOwner(), new Observer<List<BookItem>>() {
            @Override
            public void onChanged(List<BookItem> bookItems) {
                bookAdapter.addData(bookItems);
                if (mViewModel.hasMore()) {
                    bookAdapter.getLoadMoreModule().loadMoreComplete();
                } else {
                    bookAdapter.getLoadMoreModule().loadMoreEnd();
                }
                amount_tv.setText(getString(R.string.lib_search_amount) + mViewModel.getTotalPage());
                closeProgressBar();
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

        start_btn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onClick(View v) {
                // 状态复原
                mViewModel.setCurrentPage(1);
                bookAdapter.setList(new ArrayList<>());
                amount_tv.setText("");

                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(kw_et.getWindowToken(),0);
                }

                kw_layout.clearFocus();

                // 无关键词
                mViewModel.keyword = kw_et.getText().toString();
                if (mViewModel.keyword.equals("")) {
                    kw_layout.setError(getString(R.string.lib_search_no_kw));
                    return;
                } else {
                    kw_layout.setError(null);
                }

                if (wti_btn.isChecked()) {
                    mViewModel.filed = "wti";
                } else if (tit_btn.isChecked()) {
                    mViewModel.filed = "tit";
                } else if (wau_btn.isChecked()) {
                    mViewModel.filed = "wau";
                } else {
                    mViewModel.filed = "wsu";
                }

                showProgressBar();
                mViewModel.requestBookList();
            }
        });
    }
}