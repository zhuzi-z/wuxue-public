package com.wuda.wuxue.ui.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.wuda.wuxue.R;

public class WaitForLoginFragment extends Fragment {

    public static final String TAG = "waiting_dialog";

    public static int STATE_LOGIN = 0, STATE_FAIL = -1;

    private int state;

    View.OnClickListener onRetryListener;

    private ConstraintLayout root_cl;
    private LinearProgressIndicator indicator;
    private TextView info_tv;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_wait_for_login, container, false);

        root_cl = view.findViewById(R.id.wait_for_login_root_constrainLayout);
        indicator = view.findViewById(R.id.wait_for_login_linearProgressIndicator);
        info_tv = view.findViewById(R.id.wait_for_login_info_textView);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        root_cl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (state == STATE_LOGIN) return;
                if (onRetryListener != null) {
                    onRetryListener.onClick(v);
                }
            }
        });
    }

    public void setOnRetryListener(View.OnClickListener onRetryListener) {
        this.onRetryListener = onRetryListener;
    }

    public void setState(int state) {
        this.state = state;
        indicator.setIndeterminate(state == STATE_LOGIN);
        info_tv.setText(state == STATE_LOGIN? "请稍候": "点击任何地方重试");
    }
}
