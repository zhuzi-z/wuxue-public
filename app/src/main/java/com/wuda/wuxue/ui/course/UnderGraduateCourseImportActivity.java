package com.wuda.wuxue.ui.course;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.ValueCallback;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.wuda.wuxue.R;
import com.wuda.wuxue.bean.Course;
import com.wuda.wuxue.bean.CourseScore;
import com.wuda.wuxue.network.ResponseResult;
import com.wuda.wuxue.network.ServerURL;
import com.wuda.wuxue.network.UnderGraduateCourseNetwork;
import com.wuda.wuxue.ui.base.DialogFactory;
import com.wuda.wuxue.util.NetUtility;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class UnderGraduateCourseImportActivity extends AppCompatActivity {

    WebView webView;
    FloatingActionButton fab;

    UnderGraduateCourseImportViewModel mViewModel;

    // url 里有一个学号
    String url;
    // 当前页面：1 => 课表，2 =》成绩
    Integer page = 0;
    final Integer PAGE_SCHEDULE = 1, PAGE_SCORE = 2;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_under_graduate_course_import);

        mViewModel = new ViewModelProvider(this).get(UnderGraduateCourseImportViewModel.class);

        Toolbar toolbar = findViewById(R.id.back_toolbar);
        toolbar.setTitle("数据导入");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        webView = findViewById(R.id.webView);
        fab = findViewById(R.id.floatingActionButton);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);

        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                fab.setVisibility(View.GONE);
            }

            @Nullable
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                if (NetUtility.removeParams(request.getUrl().toString()).equals(ServerURL.UNDER_GRADUATE_COURSE_SCHEDULE)) {
                    url = request.getUrl().toString();
                    page = PAGE_SCHEDULE;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(UnderGraduateCourseImportActivity.this, "当前位于课表页，请点击右下角按键导入课表", Toast.LENGTH_SHORT).show();
                            fab.setVisibility(View.VISIBLE);
                        }
                    });
                } else if (NetUtility.removeParams(request.getUrl().toString()).equals(ServerURL.UNDER_GRADUATE_COURSE_SCORE)) {
                    page = PAGE_SCORE;
                    url = request.getUrl().toString();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(UnderGraduateCourseImportActivity.this, "当前位于成绩页，请点击右下角按键导入成绩（仅支持全部导入）", Toast.LENGTH_SHORT).show();
                            fab.setVisibility(View.VISIBLE);
                        }
                    });
                }
                return super.shouldInterceptRequest(view, request);
            }
        });

        webView.loadUrl(ServerURL.UNDER_GRADUATE_COURSE_LOGIN);

        new MaterialAlertDialogBuilder(this)
                .setMessage("请前往课表/成绩页面按提示导入。\n\n课表页面需要手动选中对应学期。\n\n成绩页面会一次性导入所有课程（选项无效）。")
                .setPositiveButton("确定", null)
                .create().show();

        event();
    }

    private void event() {
        mViewModel.getSuccessResponse().observe(this, new Observer<List<Course>>() {
            @Override
            public void onChanged(List<Course> courses) {
                Toast.makeText(UnderGraduateCourseImportActivity.this, "已导入" + courses.size() + "项记录", Toast.LENGTH_SHORT).show();
            }
        });

        mViewModel.getScoreResponse().observe(this, new Observer<List<CourseScore>>() {
            @Override
            public void onChanged(List<CourseScore> courseScores) {
                Toast.makeText(UnderGraduateCourseImportActivity.this, "已导入" + courseScores.size() + "门成绩", Toast.LENGTH_SHORT).show();
            }
        });

        mViewModel.getFailResponse().observe(this, new Observer<ResponseResult<?>>() {
            @Override
            public void onChanged(ResponseResult<?> responseResult) {
                DialogFactory.errorInfoDialog(UnderGraduateCourseImportActivity.this, responseResult);
            }
        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (webView.canGoBack()) {
                    webView.goBack();
                    fab.setVisibility(View.GONE);
                } else {
                    finish();
                }
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Objects.equals(page, PAGE_SCHEDULE)) {
                    webView.evaluateJavascript(
                            "(function() { return ('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>'); })();",
                            new ValueCallback<String>() {
                                @Override
                                public void onReceiveValue(String html) {
                                    String str = NetUtility.removeUTFCharacters(html);
                                    Map<String, String> tableParams = UnderGraduateCourseNetwork.parseTableParams(str);
                                    Map<String, String> header = new HashMap<>();
                                    String cookies = CookieManager.getInstance().getCookie(url);
                                    header.put("cookie", cookies);
                                    mViewModel.requestCourseList(url, tableParams, header);
                                }
                            });
                } else if (Objects.equals(page, PAGE_SCORE)) {
                    Map<String, String> header = new HashMap<>();
                    String cookies = CookieManager.getInstance().getCookie(url);
                    header.put("cookie", cookies);
                    mViewModel.requestScoreList(url, header);
                }
            }
        });
    }
}