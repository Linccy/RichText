package com.linccy.richtext;

import android.text.TextUtils;

public class RichConfig {
    private static String tag_heard;

    public static String getTagHeard() {
        return (TextUtils.isEmpty(tag_heard) ? "default" : tag_heard) + "-";
    }

    public static void setTagHeard(String str) {
        tag_heard = str;
    }
}
