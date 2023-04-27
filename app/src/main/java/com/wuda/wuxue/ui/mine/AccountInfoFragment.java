package com.wuda.wuxue.ui.mine;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.wuda.wuxue.WuXueApplication;
import com.wuda.wuxue.network.HttpClient;
import com.wuda.wuxue.network.ServerURL;
import com.wuda.wuxue.ui.base.WebViewFragment;

public class AccountInfoFragment extends WebViewFragment {

    Boolean isExpired = false;

    public AccountInfoFragment() {
        mUrl = ServerURL.CAS_ACCOUNT;
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
                super.onPageFinished(view, url);
                if (url.equals(ServerURL.CAS_LOGIN))
                    isExpired = true;
                // 过期时进入帐号管理，重新保存Cookies
                if (isExpired && url.equals(ServerURL.CAS_ACCOUNT)) {
                    String cookies = CookieManager.getInstance().getCookie(url);
                    HttpClient.setCasCookies(cookies);
                }
            }
        });
        return view;
    }
}