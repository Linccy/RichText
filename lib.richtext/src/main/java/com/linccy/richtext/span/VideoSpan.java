package com.linccy.richtext.span;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.style.ImageSpan;

public class VideoSpan extends ImageSpan {
    private String source;

    public VideoSpan(Context context, Bitmap b, String source) {
        super(context, b);
        this.source = source;
    }

    @Override
    public String getSource() {
        return source;
    }

    @Override
    public String toString() {
        return source;
    }
}
