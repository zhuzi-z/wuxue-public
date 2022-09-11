package com.wuda.wuxue.ui.base;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.webkit.WebSettingsCompat;
import androidx.webkit.WebViewFeature;

import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.wuda.wuxue.R;
import com.wuda.wuxue.util.DarkModeUtility;

public class WebViewFragment extends Fragment {
    // 内置浏览器打开

    public static final String ARG_URL = "URL";
    protected String mUrl;

    protected WebView webView;
    protected LinearProgressIndicator progressIndicator;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mUrl = getArguments().getString(ARG_URL);
        }
    }

    @SuppressLint({"SetJavaScriptEnabled", "ResourceType"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_webview, container, false);
        progressIndicator = view.findViewById(R.id.webView_progressIndicator);
        progressIndicator.setIndeterminate(true);

        webView = view.findViewById(R.id.network_webView);
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
        });
        webView.setBackgroundColor(requireContext().getColor(R.color.surface));

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);

        if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
            if(DarkModeUtility.isDarkMode(requireContext())){
                WebSettingsCompat.setForceDark(webSettings, WebSettingsCompat.FORCE_DARK_ON);
            }else{
                WebSettingsCompat.setForceDark(webSettings, WebSettingsCompat.FORCE_DARK_OFF);
            }
        }

        requireActivity().getOnBackPressedDispatcher().addCallback(requireActivity(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (webView.canGoBack()) {
                    webView.goBack();
                } else {
                    requireActivity().finish();
                }
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        webView.loadUrl(mUrl);
    }
}