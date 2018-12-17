package com.linccy.richtext;

import android.text.Layout;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.view.MotionEvent;
import android.widget.EditText;
import android.widget.TextView;

import com.linccy.richtext.span.VoiceSpan;


/**
 * 处理ClickSpan和Click事件冲突
 */

public class ImageEditViewMovementMethod extends LinkMovementMethod {

    private long pressedTimes = 0;

    @Override
    public boolean onTouchEvent(TextView widget, Spannable buffer, MotionEvent event) {
        boolean b = super.onTouchEvent(widget, buffer, event);
        //解决点击事件冲突问题
        boolean result = true;
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
                        result = clickSpan(event, widget, buffer) || widget.performClick();
                    } else if (widget.isLongClickable() && widget.isEnabled()){
                        pressedTimes = 0;
                        result = widget.performLongClick();
                    }
                default:
                    pressedTimes = System.currentTimeMillis();
                    break;
            }
        }else {
            pressedTimes = System.currentTimeMillis();
        }

        if(widget instanceof EditText && widget.getSelectionEnd() < 0 && event.getAction() == MotionEvent.ACTION_UP) {
            ((EditText)widget).setSelection(0);
        }
        return result || b;
    }

    private boolean clickSpan(MotionEvent event, TextView widget, Spannable buffer) {
        int action = event.getAction();
        int x = (int) event.getX();
        int y = (int) event.getY();

        x -= widget.getTotalPaddingLeft();
        y -= widget.getTotalPaddingTop();

        x += widget.getScrollX();
        y += widget.getScrollY();

        Layout layout = widget.getLayout();
        int line = layout.getLineForVertical(y);
        int off = layout.getOffsetForHorizontal(line, x);

        VoiceSpan[] voiceSpans = buffer.getSpans(off, off, VoiceSpan.class);
        if (voiceSpans != null && voiceSpans.length != 0) {
            if (action == MotionEvent.ACTION_UP) {
                voiceSpans[0].onClick(widget);
                return true;
            }
        }
        return false;
    }

    public static ImageEditViewMovementMethod getInstance() {
        if (sInstance == null)
            sInstance = new ImageEditViewMovementMethod();

        return sInstance;
    }


    private static ImageEditViewMovementMethod sInstance;

}