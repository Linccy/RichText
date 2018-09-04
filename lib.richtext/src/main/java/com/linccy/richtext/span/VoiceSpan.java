package com.linccy.richtext.span;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.style.ImageSpan;
import android.widget.TextView;

public abstract class VoiceSpan extends ImageSpan {
    private String source;
    private int duration;

    public VoiceSpan(Context context, Bitmap b, String source, int duration) {
        super(context, b);
        this.source = source;
        this.duration = duration;
    }

    public int getDuration() {
        return duration;
    }

    @Override
    public String getSource() {
        return source;
    }

    @Override
    public String toString() {
        return source;
    }

    public abstract void onClick(TextView widget);
}
