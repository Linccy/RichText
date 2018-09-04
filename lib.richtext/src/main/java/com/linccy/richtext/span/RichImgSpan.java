package com.linccy.richtext.span;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.style.ImageSpan;

public class RichImgSpan extends ImageSpan {
    private Uri mUri;
    public RichImgSpan(Context context, Bitmap b, Uri uri) {
        super(context, b);
        mUri = uri;
    }

    @Override
    public String getSource() {
        return mUri.toString();
    }
}
