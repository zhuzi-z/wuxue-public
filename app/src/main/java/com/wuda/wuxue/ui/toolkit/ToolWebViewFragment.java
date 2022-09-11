package com.wuda.wuxue.ui.toolkit;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuProvider;

import com.wuda.wuxue.R;
import com.wuda.wuxue.bean.Tool;
import com.wuda.wuxue.ui.base.WebViewFragment;

public class ToolWebViewFragment extends WebViewFragment {

    protected Tool tool;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            tool = (Tool) getArguments().getSerializable("tool");
            if (tool.getUrl() != null)
                mUrl = tool.getUrl();
        }
        if (tool == null) {
            requireActivity().onBackPressed();
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (tool != null && tool.getUrl() != null) {
            requireActivity().addMenuProvider(new MenuProvider() {
                @Override
                public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                    menuInflater.inflate(R.menu.toolkit_toolbar, menu);
                }

                @Override
                public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                    if (menuItem.getItemId() == R.id.tool_detail_open_in_browser) {
                        Intent openInBrowserIntent = new Intent(Intent.ACTION_VIEW);
                        openInBrowserIntent.setData(Uri.parse(tool.getUrl()));
                        startActivity(openInBrowserIntent);
                    } else if (menuItem.getItemId() == R.id.tool_detail_share) {
                        Intent shareIntent = new Intent(Intent.ACTION_SEND);
                        shareIntent.putExtra(Intent.EXTRA_TEXT, tool.getToolName() + "  " + tool.getUrl());
                        shareIntent.setType("text/plain");
                        startActivity(shareIntent);
                    }
                    return true;
                }
            });
        }

        ((ToolActivity) requireActivity()).getSupportActionBar().setTitle(tool.getToolName());
        ((ToolActivity) requireActivity()).closeProgressBar();
    }

}
