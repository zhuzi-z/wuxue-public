package com.wuda.wuxue.ui.toolkit;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.wuda.wuxue.R;
import com.wuda.wuxue.bean.ToolEntry;
import com.wuda.wuxue.ui.adapter.ToolkitAdapter;


public class ToolkitFragment extends Fragment {

   RecyclerView recyclerView;
   ToolkitAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recycler_view, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        mAdapter = new ToolkitAdapter(R.layout.item_toolkit_tool);
        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                ToolEntry tool = (ToolEntry) adapter.getData().get(position);
                Intent intent = new Intent(getContext(), ToolActivity.class);
                intent.putExtra("tool", tool);
                startActivity(intent);
            }
        });
        mAdapter.setList(ToolProvider.getAllTools());

        recyclerView.setAdapter(mAdapter);

        return recyclerView;
    }
}