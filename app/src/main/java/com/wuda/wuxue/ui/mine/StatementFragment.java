package com.wuda.wuxue.ui.mine;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textview.MaterialTextView;
import com.wuda.wuxue.R;

public class StatementFragment extends Fragment {

    View.OnClickListener onCheckClickListener;
    boolean showCheck;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_statement, container, false);

        MaterialTextView statement_tv = view.findViewById(R.id.app_statement_textView);
        MaterialCheckBox check_cb = view.findViewById(R.id.app_statement_checkBox);
        MaterialButton check_btn = view.findViewById(R.id.app_statement_button);

        statement_tv.append(Html.fromHtml(getString(R.string.statement_common)));
        statement_tv.append(Html.fromHtml(getString(R.string.statement_permission)));
        statement_tv.append(Html.fromHtml(getString(R.string.statement_source_code)));

        if (showCheck) {
            check_cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    check_btn.setEnabled(b);
                }
            });

            check_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onCheckClickListener != null) {
                        onCheckClickListener.onClick(view);
                    }
                }
            });
        } else {
            check_cb.setVisibility(View.GONE);
            check_btn.setVisibility(View.GONE);
        }

        return view;
    }

    public void setOnCheckClickListener(View.OnClickListener onCheckClickListener) {
        this.onCheckClickListener = onCheckClickListener;
    }

    public void setShowCheck(boolean showCheck) {
        this.showCheck = showCheck;
    }
}