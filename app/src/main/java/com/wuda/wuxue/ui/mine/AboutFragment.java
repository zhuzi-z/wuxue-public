package com.wuda.wuxue.ui.mine;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.wuda.wuxue.BuildConfig;
import com.wuda.wuxue.R;
import com.wuda.wuxue.network.ServerURL;
import com.wuda.wuxue.ui.base.NavHostActivity;
import com.wuda.wuxue.ui.base.NavigationHost;

public class AboutFragment extends Fragment {

    String[] tricks = {
            "这不能点哟！要不你再点一下看看。",
            "你还真点呀！要不再点一下？",
            "好吧，告诉你个秘密，开发者跑路了。",
            "没了啦，不用点了。",
            "真没了。",
            "还点呢？行，你点吧，不想理你了。"
    };
    int trickIdx = 0;

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about, container, false);

        TextView version_tv = view.findViewById(R.id.about_version_textView);
        TextView appStatement_tv = view.findViewById(R.id.about_app_statement_textView);
        TextView licenseStatement_tv = view.findViewById(R.id.about_license_statement_textView);
        TextView sourceCode_tv = view.findViewById(R.id.about_source_code_textView);

        version_tv.setText("当前版本：" + BuildConfig.VERSION_NAME);
        version_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (trickIdx < tricks.length) {
                    String text = tricks[trickIdx++];
                    Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show();
                }
            }
        });

        appStatement_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StatementFragment statementFragment = new StatementFragment();
                statementFragment.setShowCheck(false);
                ((NavigationHost) requireActivity()).navigationTo(statementFragment, true);
            }
        });

        licenseStatement_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OpenSourceLicenseFragment openSourceLicenseFragment = new OpenSourceLicenseFragment();
                ((NavigationHost) requireActivity()).navigationTo(openSourceLicenseFragment, true);
            }
        });

        sourceCode_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(ServerURL.CODE));
                startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        ((NavHostActivity) requireActivity()).getSupportActionBar().setTitle("关于");
    }
}