package com.linccy.richtext;

import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.view.MotionEvent;
import android.widget.TextView;


/**
 * 处理ClickSpan和Click事件冲突
 */

public class CustomLinkMovementMethod extends LinkMovementMethod {

    private long pressedTimes = 0;

    @Override
    public boolean onTouchEvent(TextView widget, Spannable buffer, MotionEvent event) {
        boolean b = super.onTouchEvent(widget, buffer, event);
        //解决点击事件冲突问题
        if (!b) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_MOVE:
                    if (pressedTimes == 0){
                        pressedTimes = System.currentTimeMillis();
                    }
                    break;

                case MotionEvent.ACTION_UP:
                    if (widget.isClickable() && widget.isEnabled() && System.currentTimeMillis() - pressedTimes >= 0 && System.currentTimeMillis() - pressedTimes < 500) {
                        pressedTimes = 0;
                        return widget.performClick();
                    } else if (widget.isLongClickable() && widget.isEnabled()){
                        pressedTimes = 0;
                        return widget.performLongClick();
                    }
                default:
                    pressedTimes = System.currentTimeMillis();
                    break;
            }
        }else {
            pressedTimes = System.currentTimeMillis();
        }
        return true;
    }

    public static CustomLinkMovementMethod getInstance() {
        if (sInstance == null)
            sInstance = new CustomLinkMovementMethod();

        return sInstance;
    }


    private static CustomLinkMovementMethod sInstance;

}