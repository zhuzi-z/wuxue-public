package com.wuda.wuxue.ui.toolkit;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.wuda.wuxue.R;
import com.wuda.wuxue.bean.Tool;
import com.wuda.wuxue.ui.base.NavHostActivity;

public class ToolActivity extends NavHostActivity {

    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tool);

        progressBar = findViewById(R.id.tool_progressBar);
        progressBar.setVisibility(View.GONE);

        Tool tool = (Tool) getIntent().getSerializableExtra("tool");

        Toolbar toolbar = findViewById(R.id.back_toolbar);
        toolbar.setTitle(tool.getToolName());
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        if (savedInstanceState == null) {
            try {
                Fragment toolFragment = (Fragment) tool.getTargetFragmentCls().newInstance();
                Bundle bundle = new Bundle();
                bundle.putSerializable("tool", tool);
                toolFragment.setArguments(bundle);
                navigationTo(toolFragment, false);
            } catch (IllegalAccessException | InstantiationException e) {
                e.printStackTrace();
                Toast.makeText(ToolActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    public void showProgressBar() {
        if (progressBar.getVisibility() == View.GONE)
            progressBar.setVisibility(View.VISIBLE);
    }

    public void closeProgressBar() {
        if (progressBar.getVisibility() == View.VISIBLE)
            progressBar.setVisibility(View.GONE);
    }
}