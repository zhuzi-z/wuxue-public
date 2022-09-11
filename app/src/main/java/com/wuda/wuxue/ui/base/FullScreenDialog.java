package com.wuda.wuxue.ui.base;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.appbar.MaterialToolbar;
import com.wuda.wuxue.R;

public class FullScreenDialog extends DialogFragment {

    public static final String TAG = "full_screen_dialog";

    private MaterialToolbar toolbar;
    private Fragment fragment;

    View.OnClickListener onCancelClickListener;

    public FullScreenDialog() {
        setCancelable(false);
    }

    public void show(@NonNull FragmentManager manager) {
        this.show(manager, TAG);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (fragment != null) {
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.replace(R.id.full_screen_dialog_fragment_container_frameLayout, fragment);
            transaction.commit();
        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme_FullScreenDialog);
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_full_screen_dialog, container, false);

        toolbar = view.findViewById(R.id.full_screen_dialog_fragment_materialToolbar);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onCancelClickListener != null) {
                    onCancelClickListener.onClick(v);
                }
                dismiss();
            }
        });
    }

    public void setOnCancelClickListener(View.OnClickListener onCancelClickListener) {
        this.onCancelClickListener = onCancelClickListener;
    }

    public void setFragment(Fragment fragment) {
        this.fragment = fragment;
    }
}
