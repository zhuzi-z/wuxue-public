package com.wuda.wuxue.util;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NetUtility {
    public static String utf8Converter(String text) {
        // 解析 utf-8 数据，非标准数据(%u -> \\u)
        // 无法使用Java内置的算法变换
        final StringBuilder buffer = new StringBuilder();
        for (int i=0; i<text.length()-1;) {
            if (text.charAt(i)=='%') {  // %  str 本身包含 % ？？
                if (text.charAt(i+1) == 'u') {  // %u  -> %u4e2d%u56fd => 中国
                    buffer.append((char)Integer.parseInt(text.substring(i+2, i+6), 16));
                    i = i + 6;
                } else {  // %  -> 20%3a10 => 20:10
                    buffer.append((char)Integer.parseInt(text.substring(i+1, i+3), 16));
                    i = i + 3;
                }
            } else {
                buffer.append(text.charAt(i));
                ++i;
            }
        }
        if (text.charAt(text.length()-1) == '}')
            buffer.append('}');  // i < str.length()-1

        return buffer.toString();
    }

    public static String getBaseUrl(String url) {
        String baseUrl = "";
        try {
            String domain = (new URL(url)).getHost();
            if (url.startsWith("https")) {
                baseUrl = "https://" + domain;
            } else {
                baseUrl = "http://" + domain;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return baseUrl;
    }

    public static String getPath(String url) {
        int end;
        for (end = url.length()-1; end>=0; end--) {
            if (url.charAt(end) == '/') {
                break;
            }
        }
        return url.substring(0, end);
    }

    public static String getDomain(String url) {
        String domain = "";
        try {
            domain = (new URL(url).getHost());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return domain;
    }

    public static String removeParams(String url) {
        if (url.contains("?")) {
            return url.split("\\?")[0];
        } else {
            return url;
        }
    }

    public static String removeUTFCharacters(String data) {
        Pattern p = Pattern.compile("\\\\u(\\p{XDigit}{4})");
        Matcher m = p.matcher(data);
        StringBuffer buf = new StringBuffer(data.length());
        while (m.find()) {
            String ch = String.valueOf((char) Integer.parseInt(m.group(1), 16));
            m.appendReplacement(buf, Matcher.quoteReplacement(ch));
        }
        m.appendTail(buf);
        String html = buf.toString();
        html = html.replaceAll("\\\\t", "\t");
        html = html.replaceAll("\\\\n", "\n");
        html = html.replaceAll("\\\\\"", "");
        return html;
    }
}
