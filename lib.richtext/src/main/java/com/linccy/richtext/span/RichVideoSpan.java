package com.linccy.richtext.span;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.style.ImageSpan;

public class RichVideoSpan extends ImageSpan {
    private String source;

    public RichVideoSpan(Context context, Bitmap b, String source) {
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
