package com.linccy.richtext.span;

import android.os.Parcel;
import android.text.TextPaint;

public class CustomURLSpan extends android.text.style.URLSpan {
    private int linkColor = 0;
    private boolean linkUnderline = true;
    private String urlName;
    private String url;

    public CustomURLSpan(String urlName, String url, int linkColor, boolean linkUnderline) {
        super(urlName);
        this.urlName = urlName;
        this.url = url;
        this.linkColor = linkColor;
        this.linkUnderline = linkUnderline;
    }

    public CustomURLSpan(Parcel src) {
        super(src);
        this.linkColor = src.readInt();
        this.linkUnderline = src.readInt() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(linkColor);
        dest.writeInt(linkUnderline ? 1 : 0);
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        ds.setColor(linkColor != 0 ? linkColor : ds.linkColor);
        ds.setUnderlineText(linkUnderline);
    }

    public String getUrlName() {
        return urlName;
    }

    public String getURL() {
        return url;
    }
}
