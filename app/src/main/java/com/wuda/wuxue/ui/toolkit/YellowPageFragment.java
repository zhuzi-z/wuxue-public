package com.wuda.wuxue.ui.toolkit;

import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.wuda.wuxue.R;
import com.wuda.wuxue.ui.adapter.SimpleInfoAdapter;
import com.wuda.wuxue.bean.Contact;

public class YellowPageFragment extends ToolFragment{

    RecyclerView recyclerView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.recycler_view, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        closeProgressBar();

        SimpleInfoAdapter<Contact> mAdapter = new SimpleInfoAdapter<Contact>(R.layout.item_textview) {
            @Override
            protected SpannableStringBuilder format(Contact data) {
                return new SpannableStringBuilder(data.getName());
            }
        };
        mAdapter.addData(Contact.getAllContacts());
        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                Contact contact = (Contact) adapter.getData().get(position);
                Bundle bundle = new Bundle();
                bundle.putSerializable("tool", contact);
                YellowPageDetailFragment yellowPageDetailFragment = new YellowPageDetailFragment();
                yellowPageDetailFragment.setArguments(bundle);
                navigationTo(yellowPageDetailFragment);
            }
        });
        recyclerView.setAdapter(mAdapter);

        return view;
    }

}
