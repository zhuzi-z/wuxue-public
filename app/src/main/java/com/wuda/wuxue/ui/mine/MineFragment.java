package com.wuda.wuxue.ui.mine;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.PopupMenu;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.wuda.wuxue.R;
import com.wuda.wuxue.util.SharePreferenceManager;

public class MineFragment extends Fragment {

    CardView account_cv;
    TextView startup_tv;
    TextView info_tv;
    TextView nightMode_tv;
    TextView about_tv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mine, container, false);
        account_cv = view.findViewById(R.id.account_cardView);
        startup_tv = view.findViewById(R.id.mine_startup_textView);
        info_tv = view.findViewById(R.id.mine_info_textView);
        nightMode_tv = view.findViewById(R.id.mine_nightMode_textView);
        about_tv = view.findViewById(R.id.mine_about_textView);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        eventBinding();
    }

    private void initView() {
        String status;

        String startup = SharePreferenceManager.loadString(SharePreferenceManager.MIME_STARTUP);
        status = startup.equals("schedule")? "课表": "工具";
        startup_tv.setText(formatItemText("启动页", status));

        info_tv.setText(formatItemText("消息订阅", "定时同步"));

        int nightMode = SharePreferenceManager.loadInteger(SharePreferenceManager.MIME_NIGHT_MODE, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        if (nightMode == AppCompatDelegate.MODE_NIGHT_NO) {
            status = "关闭";
        } else if (nightMode == AppCompatDelegate.MODE_NIGHT_YES) {
            status = "开启";
        } else if (nightMode == AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM) {
            status = "系统默认";
        }
        nightMode_tv.setText(formatItemText("深色模式", status));
    }

    private SpannableStringBuilder formatItemText(String title, String status) {
        SpannableStringBuilder builder = new SpannableStringBuilder();
//        builder.append(title, new StyleSpan(Typeface.BOLD), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.append(title);
        if (status != null) {
            builder.append("\n");
            builder.append(status, new RelativeSizeSpan(0.75f), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return builder;
    }

    private void eventBinding() {
        account_cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent accountIntent = new Intent(requireContext(), AccountActivity.class);
                accountIntent.putExtra("login", false);
                startActivity(accountIntent);
            }
        });

        startup_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(requireContext(), startup_tv);
                popup.getMenu().add(0, Menu.NONE, 0, "课表");
                popup.getMenu().add(0, Menu.NONE, 1, "工具");
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int order = item.getOrder();
                        if (order == 0) {
                            SharePreferenceManager.storeString(SharePreferenceManager.MIME_STARTUP, "schedule");
                        } else {
                            SharePreferenceManager.storeString(SharePreferenceManager.MIME_STARTUP, "toolkit");
                        }
                        initView();
                        popup.dismiss();
                        return false;
                    }
                });
                popup.show();
            }
        });

        info_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(requireContext(), InfoActivity.class));
            }
        });

        nightMode_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(requireContext(), startup_tv);
                popup.getMenu().add(0, Menu.NONE, 0, "关闭");
                popup.getMenu().add(0, Menu.NONE, 1, "开启");
                popup.getMenu().add(0, Menu.NONE, 2, "系统默认");
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int order = item.getOrder();
                        if (order == 0) {
                            SharePreferenceManager.storeInteger(SharePreferenceManager.MIME_NIGHT_MODE, AppCompatDelegate.MODE_NIGHT_NO);
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                        } else if (order == 1) {
                            SharePreferenceManager.storeInteger(SharePreferenceManager.MIME_NIGHT_MODE, AppCompatDelegate.MODE_NIGHT_YES);
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                        } else {
                            SharePreferenceManager.storeInteger(SharePreferenceManager.MIME_NIGHT_MODE, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                        }
                        initView();
                        popup.dismiss();
                        return false;
                    }
                });
                popup.show();
            }
        });

        about_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(requireContext(), AboutActivity.class);
                startActivity(intent);
            }
        });
    }
}