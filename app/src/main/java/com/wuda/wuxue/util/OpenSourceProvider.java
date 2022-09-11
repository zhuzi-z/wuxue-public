package com.wuda.wuxue.util;

import com.wuda.wuxue.bean.OpenSourceProject;

import java.util.ArrayList;
import java.util.List;

public class OpenSourceProvider {

    static final String LICENCE_APACHE_2 = "Apache License, Version 2.0";
    static final String LICENCE_MIT = "MIT License";
    static final String LICENCE_UNKNOWN = "Unknown License";

    public static List<OpenSourceProject> getUsedOpenSourceProjects() {
        List<OpenSourceProject> openSourceProjectList = new ArrayList<>();
        openSourceProjectList.add(new OpenSourceProject(
                "AndroidX",
                "Google",
                "https://github.com/androidx",
                LICENCE_APACHE_2,
                "Development environment for Android Jetpack extension libraries" +
                        " under the androidx namespace. Synchronized with Android Jetpack's" +
                        " primary development branch on AOSP."));
        openSourceProjectList.add(new OpenSourceProject(
                "OkHttp3",
                "Square",
                "https://square.github.io",
                LICENCE_APACHE_2,
                ""
        ));
        openSourceProjectList.add(new OpenSourceProject(
                "gson",
                "Google",
                "https://github.com/google/gson",
                LICENCE_APACHE_2,
                "A Java serialization/deserialization library to convert Java Objects into JSON and back."
        ));
        openSourceProjectList.add(new OpenSourceProject(
                "jsoup",
                "Jonathan Hedley",
                "https://jsoup.org/",
                LICENCE_MIT,
                "jsoup is a Java library for working with real-world HTML. " +
                        "It provides a very convenient API for fetching URLs and extracting and " +
                        "manipulating data, using the best of HTML5 DOM methods and CSS selectors."
        ));
        openSourceProjectList.add(new OpenSourceProject(
                "HtmlTextView",
                "SufficientlySecure",
                "https://github.com/SufficientlySecure/html-textview",
                LICENCE_APACHE_2,
                "HtmlTextView is an extended TextView component for Android, " +
                        "which can load very simple HTML by converting it into Android Spannables for viewing."
        ));
        openSourceProjectList.add(new OpenSourceProject(
                "Glide",
                "Sam Judd",
                "https://bumptech.github.io/glide/",
                "BSD, part MIT and Apache 2.0",
                "Glide is a fast and efficient image loading library for " +
                        "Android focused on smooth scrolling. Glide offers an easy to use API, " +
                        "a performant and extensible resource decoding pipeline and automatic resource pooling."
        ));
        openSourceProjectList.add(new OpenSourceProject(
                " MPAndroidChart",
                "PhilJay",
                "https://github.com/PhilJay/MPAndroidChart",
                LICENCE_APACHE_2,
                "A powerful & easy to use chart library for Android."
        ));
        openSourceProjectList.add(new OpenSourceProject(
                "colorpicker",
                "QuadFlask",
                "https://github.com/QuadFlask/colorpicker",
                LICENCE_APACHE_2,
                "simple android color picker with color wheel and lightness bar."
        ));
        openSourceProjectList.add(new OpenSourceProject(
                "NumberPickerView",
                "Carbs0126",
                "https://github.com/Carbs0126/NumberPickerView",
                LICENCE_APACHE_2,
                "Another NumberPicker with more flexible attributes on Android platform."
        ));
        openSourceProjectList.add(new OpenSourceProject(
                "BaseRecyclerViewAdapterHelper",
                "CymChad",
                "https://github.com/CymChad/BaseRecyclerViewAdapterHelper",
                LICENCE_MIT,
                "Powerful and flexible RecyclerView Adapter, Please feel free to use this."
        ));
        openSourceProjectList.add(new OpenSourceProject(
                "WakeupSchedule",
                "YZune",
                "https://github.com/YZune/WakeupSchedule_Kotlin",
                LICENCE_APACHE_2,
                ""
        ));

        return openSourceProjectList;
    }
}