package com.wuda.wuxue.network;

import androidx.annotation.NonNull;

import com.wuda.wuxue.util.SharePreferenceManager;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;

public class HttpClient {

    private static String CAS_COOKIES = SharePreferenceManager.loadString(SharePreferenceManager.CAS_COOKIE);

    public static void setCasCookies(String cookies) {
        SharePreferenceManager.storeString(SharePreferenceManager.CAS_COOKIE, cookies);
        HttpClient.CAS_COOKIES = cookies;
        // 清除 Cookie
        HttpClient.clearCookieStore();
    }

    private static final ConcurrentHashMap<String, List<Cookie>> cookieStore = new ConcurrentHashMap<>();

    private static final CookieJar cookieJar = new CookieJar() {
        @Override
        public void saveFromResponse(@NonNull HttpUrl httpUrl, @NonNull List<Cookie> list) {
            cookieStore.put(httpUrl.host(), list);  // 第一次登录会使用旧的cookie，必须退出后台才可以
        }

        @NonNull
        @Override
        public List<Cookie> loadForRequest(@NonNull HttpUrl httpUrl) {
            List<Cookie> cookies = cookieStore.get(httpUrl.host());
            return cookies != null ? cookies : new ArrayList<>();
        }
    };

    // 部分页面（图书馆座位预约）限制了UA
    private static final String USER_AGENT = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.45 Safari/537.36";

    // 登录时清除旧的CAS Cookies
    // cas 请求（记录一次） => 失效
    // 登录 => 新Cookie未加载
    // 重新请求（失败）
    public static void clearCookieStore() {
        cookieStore.clear();
    }

    public static void removeCookie(String host) {
        cookieStore.remove(host);
    }

    // 不需要登录
    public static void get(String address, okhttp3.Callback callback) {
        OkHttpClient client = new OkHttpClient.Builder()
                .cookieJar(cookieJar)
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        Request request = new Request.Builder()
                .url(address)
                .addHeader("User-Agent", USER_AGENT)
                .build();

        client.newCall(request).enqueue(callback);
    }

    // 需要登录
    public static void getWithCAS(String address, okhttp3.Callback callback) {
        Request request = new Request.Builder()
                .url(ServerURL.CAS_REDIRECT + address)
                // CAS的Cookie
                .addHeader("User-Agent", USER_AGENT)
                .header("cookie", HttpClient.CAS_COOKIES)
                .build();
        // 移除旧的
        try {
            removeCookie(new URL(address).getHost());
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return;
        }

        OkHttpClient client = new OkHttpClient.Builder()
                // 必须：不然会 401 => 重定向会丢失（研究生成绩）
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))
                .cookieJar(cookieJar)
                .followRedirects(true)
                .followSslRedirects(true)
                .build();

        client.newCall(request).enqueue(callback);
    }

    // post
    public static void post(String address, FormBody formBody, okhttp3.Callback callback) {
        OkHttpClient client = new OkHttpClient.Builder()
                .cookieJar(cookieJar)
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))
                .build();
        Request request = new Request.Builder()
                .url(address)
                .addHeader("User-Agent", USER_AGENT)
                .post(formBody)
                .build();
        client.newCall(request).enqueue(callback);
    }

    // post
    public static void post(String address, Map<String, String> data, okhttp3.Callback callback) {
        OkHttpClient client = new OkHttpClient.Builder()
                .cookieJar(cookieJar)
                .build();

        FormBody.Builder bodyBuilder = new FormBody.Builder();
        for (String key: data.keySet()) {
            bodyBuilder.add(key, Objects.requireNonNull(data.get(key)));
        }

        Request request = new Request.Builder()
                .url(address)
                .addHeader("User-Agent", USER_AGENT)
                .post(bodyBuilder.build())
                .build();
        client.newCall(request).enqueue(callback);
    }

    public static void postWithHeader(String address, FormBody formBody, Map<String, String> header, okhttp3.Callback callback) {
        OkHttpClient client = new OkHttpClient.Builder()
                .cookieJar(cookieJar)
                .build();

        Request.Builder builder = new Request.Builder();
        if (!header.isEmpty()) {
            for (String key: header.keySet()) {
                builder.addHeader(key, Objects.requireNonNull(header.get(key)));
            }
        }

        Request request = builder.url(address)
                .addHeader("User-Agent", USER_AGENT)
                .post(formBody)
                .build();

        client.newCall(request).enqueue(callback);
    }
}
