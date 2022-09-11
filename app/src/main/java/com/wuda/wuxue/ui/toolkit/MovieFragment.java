package com.wuda.wuxue.ui.toolkit;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.listener.OnLoadMoreListener;
import com.wuda.wuxue.R;
import com.wuda.wuxue.bean.BaseInfo;
import com.wuda.wuxue.bean.Movie;
import com.wuda.wuxue.db.InfoDBUtility;
import com.wuda.wuxue.network.ResponseResult;
import com.wuda.wuxue.ui.adapter.MovieAdapter;
import com.wuda.wuxue.ui.base.DialogFactory;
import com.wuda.wuxue.util.SharePreferenceManager;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

public class MovieFragment extends ToolFragment {

    RecyclerView recyclerView;
    MovieAdapter adapter;
    MovieViewModel mViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.recycler_view, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        adapter = new MovieAdapter(R.layout.item_movie);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("tool", (Serializable) adapter.getData().get(position));
                MovieInfoFragment movieInfoFragment = new MovieInfoFragment();
                movieInfoFragment.setArguments(bundle);
                navigationTo(movieInfoFragment);
            }
        });
        adapter.getLoadMoreModule().setAutoLoadMore(false);
        adapter.getLoadMoreModule().setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                mViewModel.loadMore();
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(MovieViewModel.class);

        event();

        if (mViewModel.getData().isEmpty()) {
            showProgressBar();
            mViewModel.requestMovieList(1);
        } else {
            adapter.setList(mViewModel.getData());
        }
    }

    private void event() {
        mViewModel.getSuccessResponse().observe(getViewLifecycleOwner(), new Observer<List<Movie>>() {
            @Override
            public void onChanged(List<Movie> movies) {
                closeProgressBar();

                if (movies == null)
                    return;

                if (mViewModel.isFirstPage()) {
                    adapter.setList(movies);
                    Set<String> subscribed_info = SharePreferenceManager.loadStringSet(SharePreferenceManager.SUBSCRIBE_INFO_SELECTED_ITEMS);
                    if (subscribed_info.contains(BaseInfo.CATEGORY_MOVIE))
                        InfoDBUtility.saveInfoId(movies);
                } else {
                    adapter.addData(movies);
                }

                if (mViewModel.hasMore()) {
                    adapter.getLoadMoreModule().loadMoreComplete();
                } else {
                    adapter.getLoadMoreModule().loadMoreEnd();
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
    }
}
