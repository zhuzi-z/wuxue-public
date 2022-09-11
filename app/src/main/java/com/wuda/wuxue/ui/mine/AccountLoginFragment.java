package com.wuda.wuxue.ui.mine;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.wuda.wuxue.WuXueApplication;
import com.wuda.wuxue.network.ServerURL;
import com.wuda.wuxue.ui.base.WebViewFragment;


public class AccountLoginFragment extends WebViewFragment {

    public AccountLoginFragment() {
        mUrl = ServerURL.CAS_LOGIN;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                progressIndicator.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressIndicator.setVisibility(View.INVISIBLE);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // 重定向，提前拦截退出
                if (url.equals(ServerURL.CAS_ACCOUNT)) {
                    String cookies = CookieManager.getInstance().getCookie(url);
                    WuXueApplication.setCookies(cookies);
                    // 隐藏 webView（会进入详细信息页）
                    webView.setVisibility(View.GONE);

                    Toast.makeText(requireContext(), "登录成功，请重新操作", Toast.LENGTH_LONG).show();
                    requireActivity().onBackPressed();

                    return true;
                } if (url.contains("retry-reason")) {
                    // retry-reason=2 => 频繁登录致使IP限制
                    String retry_reason = "";
                    for (String s: url.split("&")) {
                        if (s.contains("retry-reason")) {
                            retry_reason = s;
                            break;
                        }
                    }
                    new MaterialAlertDialogBuilder(requireContext())
                            .setTitle("登录失败")
                            .setMessage(retry_reason)
                            .setPositiveButton("确定", null)
                            .create()
                            .show();
                }
                return false;
            }
        });

        return view;
    }
}