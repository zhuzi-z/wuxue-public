package com.wuda.wuxue.ui.toolkit;

import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.listener.OnLoadMoreListener;
import com.wuda.wuxue.R;
import com.wuda.wuxue.bean.Announcement;
import com.wuda.wuxue.bean.BaseInfo;
import com.wuda.wuxue.db.InfoDBUtility;
import com.wuda.wuxue.network.ResponseResult;
import com.wuda.wuxue.ui.adapter.SimpleInfoAdapter;
import com.wuda.wuxue.ui.base.DialogFactory;
import com.wuda.wuxue.util.SharePreferenceManager;

import java.util.List;
import java.util.Set;

public class BaseAnnouncementFragment extends ToolFragment {

    RecyclerView recyclerView;
    AnnouncementViewModel mViewModel;
    SimpleInfoAdapter<Announcement> adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.recycler_view, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));

        adapter = new SimpleInfoAdapter<Announcement>(R.layout.item_textview) {
            @Override
            protected SpannableStringBuilder format(Announcement announcement) {
                SpannableStringBuilder builder = new SpannableStringBuilder();
                builder.append(announcement.getTitle());
                if (announcement.getDepartment() == null) {
                    builder.append("\n" + announcement.getTime(),
                            new RelativeSizeSpan(0.75f),
                            Spanned.SPAN_INCLUSIVE_EXCLUSIVE
                    );
                } else {
                    builder.append("\n" + announcement.getDepartment() + " | " + announcement.getTime(),
                            new RelativeSizeSpan(0.75f),
                            Spanned.SPAN_INCLUSIVE_EXCLUSIVE
                    );
                }
                return builder;
            }
        };
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                Announcement announcement = (Announcement) adapter.getData().get(position);
                BaseAnnouncementContentFragment contentFragment = new BaseAnnouncementContentFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("tool", announcement);
                contentFragment.setArguments(bundle);
                navigationTo(contentFragment);
            }
        });
        adapter.getLoadMoreModule().setAutoLoadMore(false);
        adapter.getLoadMoreModule().setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                mViewModel.loadMore();
            }
        });

        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mViewModel = (AnnouncementViewModel) new ViewModelProvider(this).get(AnnouncementViewModelFactory.getViewModelClz(tool.getUrl()));

        mViewModel.getSuccessResponse().observe(getViewLifecycleOwner(), new Observer<List<Announcement>>() {
            @Override
            public void onChanged(List<Announcement> announcements) {
                if (mViewModel.isFirstPage()) {
                    adapter.setList(announcements);
                    // 更新消息同步
                    Set<String> subscribed_info = SharePreferenceManager.loadStringSet(SharePreferenceManager.SUBSCRIBE_INFO_SELECTED_ITEMS);
                    if (subscribed_info.contains(BaseInfo.CATEGORY_ANNOUNCEMENT))
                        InfoDBUtility.saveInfoId(announcements);
                } else {
                    adapter.addData(announcements);
                }
                if (mViewModel.hasMore()) {
                    adapter.getLoadMoreModule().loadMoreComplete();
                } else {
                    adapter.getLoadMoreModule().loadMoreEnd();
                }
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

        if (mViewModel.getData().isEmpty()) {
            showProgressBar();
            mViewModel.requestAnnouncementList(-1);
        } else {
            adapter.setList(mViewModel.getData());
        }
    }
}
