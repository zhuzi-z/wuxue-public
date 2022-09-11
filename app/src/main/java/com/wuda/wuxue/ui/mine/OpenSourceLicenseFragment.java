package com.wuda.wuxue.ui.mine;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.wuda.wuxue.R;
import com.wuda.wuxue.bean.OpenSourceProject;
import com.wuda.wuxue.ui.adapter.OpenSourceLicenseAdapter;
import com.wuda.wuxue.ui.base.NavHostActivity;
import com.wuda.wuxue.util.OpenSourceProvider;
import com.wuda.wuxue.util.ScreenUtility;

import java.util.List;

public class OpenSourceLicenseFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recycler_view, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        int dp = ScreenUtility.dpToPx(requireContext(), 16);
        recyclerView.setPadding(dp, 0, dp, 0);
        OpenSourceLicenseAdapter adapter = new OpenSourceLicenseAdapter(R.layout.item_textview);
        List<OpenSourceProject> list = OpenSourceProvider.getUsedOpenSourceProjects();
        list.add(new OpenSourceProject("Icon", "iconfont", "https://www.iconfont.cn/", "Other", "部分图标"));
        adapter.setList(list);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                OpenSourceProject project = (OpenSourceProject) adapter.getData().get(position);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(project.getUrl()));
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(adapter);

        return  view;
    }

    @Override
    public void onStart() {
        super.onStart();
        ((NavHostActivity) requireActivity()).getSupportActionBar().setTitle("开源许可");
    }
}