package com.linccy.richtext;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.net.Uri;
import android.support.annotation.IntDef;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewDebug;
import android.widget.TextView;

import com.linccy.richtext.util.MatchRule;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.linccy.richtext.RichTextInitalor.TAG_TYPE_ARTICLE;
import static com.linccy.richtext.RichTextInitalor.TAG_TYPE_K_SITE;
import static com.linccy.richtext.RichTextInitalor.TAG_TYPE_LINK;
import static com.linccy.richtext.RichTextInitalor.TAG_TYPE_USER;

public class RichTextView extends FolderTextView {
    public static final String TAG = RichTextView.class.getName();
    public static final String MATCH_MENTION = MatchRule.MATCH_ELEMENT; //这个和其他不同
    public static final String MATCH_URI = MatchRule.MATCH_URI;
    public static final String MATCH_REALM_NAME = MatchRule.MATCH_REALM_NAME;

    public static final int AT_USER_TEXT_COLOR = 0xFF6482D9;
    public static final int ADD_TOPIC_TEXT_COLOR = AT_USER_TEXT_COLOR;
    public static final int ADD_LINK_TEXT_COLOR = AT_USER_TEXT_COLOR;
    private CharSequence mText;
    private boolean mEllipsized = false;

    private OnMatcherClickListener mMatcherClickListener;

    public RichTextView(Context context) {
        this(context, null);
    }

    public RichTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RichTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setDefaultMatcherClickListener();
    }

    @Override
    public void setText(CharSequence text, TextView.BufferType type) {
        if (!TextUtils.isEmpty(text) && mText != null
                && !mText.toString().contains(text.subSequence(0, text.length() > 3 ? text.length() - 3 : text.length() - 1))) {
            mEllipsized = false;
        }
        super.setText(text, type);
        if (!TextUtils.isEmpty(text)) {
            Spannable spannable = new SpannableString(text);
            spannable = matchMention(spannable);
//        spannable = matchTopic(spannable);
//        spannable = matchLink(spannable);
            super.setText(spannable, type);
            mText = spannable;
        } else {
            mText = getText();
        }
    }

    @ViewDebug.CapturedViewProperty
    public CharSequence getText() {
        return mText != null ? mText : super.getText();
    }

    @Override
    protected void onDraw(Canvas canvas) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && getMaxLines() != Integer.MAX_VALUE && !mEllipsized) {
//            float avail = getMeasuredWidth() * (getMaxLines() > 0 ? getMaxLines() : getLineCount());
//            if (getMeasuredWidth() < avail && getLineCount() > 1) {
//                avail -= getMeasuredWidth() * 0.1;
//            }
//            if (!TextUtils.isEmpty(getText())) {
//                mEllipsized = true;
//            }
//            super.setText(TextUtils.ellipsize(getText(), getPaint(), avail, getEllipsize()));
//        }
        super.onDraw(canvas);
    }

    public Spannable getCurrentSpannable() {
        if (getText() instanceof Spannable) {
            return (Spannable) getText();
        } else {
            return new SpannableString(getText());
        }
    }

    /**
     * 处理<bk-xxxxx id=xx>@user or #sfvug#</bk-xxxxx>
     */
    public Spannable matchMention(Spannable spannable) {
        String text = spannable.toString();

        if (TextUtils.isEmpty(text)) {
            return spannable;
        }

        Pattern pattern = Pattern.compile(MATCH_MENTION);
        Matcher matcher = pattern.matcher(text);

        List<MatcherFlag> matcherFlags = new ArrayList<>();

        while (matcher.find()) {
            @MatcherFlag.RangeFlagType int type;
            final String tagBody = matcher.group(0);
            final String tagType = matcher.group(4);
            if (TextUtils.isEmpty(tagType)) {
                type = MatcherFlag.MatcherFlagType.UNDEFINE;
            } else {
                switch (tagType) {
                    case TAG_TYPE_USER:
                        type = MatcherFlag.MatcherFlagType.USER;
                        break;

                    case TAG_TYPE_LINK:
                        type = MatcherFlag.MatcherFlagType.LINK;
                        break;

                    case TAG_TYPE_ARTICLE:
                        type = MatcherFlag.MatcherFlagType.ARTICLE;
                        break;

                    case TAG_TYPE_K_SITE:
                        type = MatcherFlag.MatcherFlagType.K_SITE;
                        break;

                    default:
                        type = MatcherFlag.MatcherFlagType.UNDEFINE;
                        break;
                }
            }

            final String paramStr = matcher.group(2);
            String tagContent;
            switch (type) {

                case MatcherFlag.MatcherFlagType.LINK:
                    tagContent = RichTextInitalor.getLinkStr(getContext());
                    break;

                case MatcherFlag.MatcherFlagType.USER:
                case MatcherFlag.MatcherFlagType.ARTICLE:
                case MatcherFlag.MatcherFlagType.K_SITE:
                case MatcherFlag.MatcherFlagType.UNDEFINE:
                default:
                    tagContent = matcher.group(3);
                    break;
            }

            if (tagBody == null || tagType == null || paramStr == null || tagContent == null) {
                continue;
            }

            int matcherStart = matcher.start();
            int matcherEnd = matcher.end();
            matcherFlags.add(new MatcherFlag(matcherStart, type, paramStr, tagBody, tagContent));
            log("matchMention:" + paramStr + " " + matcherStart + " " + matcherEnd);
        }

        if (matcherFlags.isEmpty()) {
            return spannable;
        }

        //处理和替换
        StringBuilder result = new StringBuilder();
        String temp = "" + spannable.toString();//深拷贝
        int index = 0;
        for (int i = 0; i < matcherFlags.size(); i++) {
            final MatcherFlag flag = matcherFlags.get(i);
            result.append(temp.substring(index, flag.getStart()));
            result.append(flag.getShouldReplacedStr());
            index = flag.getStart() + flag.getOriginalStr().length();
            flag.setStart(result.length() - flag.getShouldReplacedStr().length());
            if (i == matcherFlags.size() - 1) {
                result.append(temp.substring(index, temp.length()));
            }
        }

        spannable = new SpannableString(result);

        for (int i = 0; i < matcherFlags.size(); i++) {
            final MatcherFlag flag = matcherFlags.get(i);
            if (MatcherFlag.MatcherFlagType.UNDEFINE == flag.getType()) {
                continue;
            }
            spannable.setSpan(new ClickableSpan() {

                @Override
                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setUnderlineText(false); //去除下划线
                    switch (flag.getType()) {

                        case MatcherFlag.MatcherFlagType.LINK:
                            ds.setColor(ADD_LINK_TEXT_COLOR);
                            break;
                        case MatcherFlag.MatcherFlagType.USER:
                        case MatcherFlag.MatcherFlagType.ARTICLE:
                        case MatcherFlag.MatcherFlagType.K_SITE:
                            ds.setColor(AT_USER_TEXT_COLOR);
                            break;
                        case MatcherFlag.MatcherFlagType.UNDEFINE:
                        default:
                            break;
                    }
                }

                @Override
                public void onClick(View widget) {
                    if (mMatcherClickListener != null) {
                        mMatcherClickListener.onMatcherClick(flag);
                    }
                }
            }, flag.getStart(), flag.getStart() + flag.getShouldReplacedStr().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return spannable;
    }

    public static Spannable matchLink(Spannable spannable) {
        String text = spannable.toString();

        Pattern uriPattern = Pattern.compile(MATCH_URI);
        Matcher uriMatcher = uriPattern.matcher(text);

        while (uriMatcher.find()) {
            String str = uriMatcher.group();
            int matcherStart = uriMatcher.start();
            int matcherEnd = uriMatcher.end();
            spannable.setSpan(new RichEditText.TagSpan(str, ADD_LINK_TEXT_COLOR), matcherStart, matcherEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            log("uri matchLink:" + str + " " + matcherStart + " " + matcherEnd);
        }


        Pattern realmNamePattern = Pattern.compile(MATCH_REALM_NAME);
        Matcher realmNameMatcher = realmNamePattern.matcher(text);

        while (realmNameMatcher.find()) {
            String str = realmNameMatcher.group();
            int matcherStart = realmNameMatcher.start();
            int matcherEnd = realmNameMatcher.end();
            spannable.setSpan(new RichEditText.TagSpan(str, ADD_LINK_TEXT_COLOR), matcherStart, matcherEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            log("realm name matchLink:" + str + " " + matcherStart + " " + matcherEnd);
        }

        return spannable;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (getMovementMethod() != null && getLinksClickable() && getText() instanceof Spannable) {
            return getMovementMethod().onTouchEvent(this, (Spannable) getText(), event);
        } else if (getText() instanceof SpannableString) {
            return onSpannableTouchEvent(event);
        } else {
            return super.onTouchEvent(event);
        }
    }

    // setMovementMethod方法会导致ellipsize属性出现问题，所以默认在这里处理ClickableSpan点击
    public boolean onSpannableTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            int x = (int) event.getX();
            int y = (int) event.getY();

            x -= getTotalPaddingLeft();
            y -= getTotalPaddingTop();

            x += getScrollX();
            y += getScrollY();

            Layout layout = getLayout();
            int line = layout.getLineForVertical(y);
            int off = layout.getOffsetForHorizontal(line, x);

            ClickableSpan[] link = ((SpannableString)getText()).getSpans(off, off, ClickableSpan.class);
            if (link.length != 0) {
                link[0].onClick(this);
                return true;
            }
        }

        return super.onTouchEvent(event);
    }

    private static void log(String msg) {
        Log.d(TAG, msg);
    }

    protected static class MatcherFlag {
        interface MatcherFlagType {
            int UNDEFINE = 1;
            int USER = 2;
            int LINK = 3;
            int ARTICLE = 4;
            int K_SITE = 5;
        }

        @IntDef({
                MatcherFlagType.UNDEFINE,
                MatcherFlagType.USER,
                MatcherFlagType.LINK,
                MatcherFlagType.ARTICLE,
                MatcherFlagType.K_SITE
        })
        @Retention(RetentionPolicy.SOURCE)
        @interface RangeFlagType {
        }

        private static final String PARAM_ID = "id";
        private static final String PARAM_LINK = "href";
        private static final String MATCH_PARAMS = MatchRule.MATCH_PARAMS;
        private @IntRange(from = 0)
        int start;
        private @RangeFlagType
        int type;
        private String paramsStr;
        private String originalStr;
        private String shouldReplacedStr;
        private Map<String, String> params = new HashMap<>();

        protected MatcherFlag(@IntRange(from = 0) int start, @RangeFlagType int type, @NonNull String paramsStr, @NonNull String originalStr, @NonNull String shouldReplacedStr) {
            this.start = start;
            this.type = type;
            this.paramsStr = paramsStr;
            this.originalStr = originalStr;
            this.shouldReplacedStr = shouldReplacedStr;

            Pattern paramsPattern = Pattern.compile(MATCH_PARAMS);
            Matcher paramsMatcher = paramsPattern.matcher(paramsStr);

            while (paramsMatcher.find()) {
                params.put(paramsMatcher.group(1), paramsMatcher.group(2));
            }
        }

        public @Nullable
        String getParams(@NonNull String key) {
            return params.get(key);
        }

        @SuppressWarnings("unused")
        public @Nullable
        String getParamId() {
            return getParams(PARAM_ID);
        }

        public @Nullable
        String getParamLink() {
            return getParams(PARAM_LINK);
        }

        @SuppressWarnings("unused")
        public int getStart() {
            return start;
        }

        @SuppressWarnings("unused")
        public void setStart(int start) {
            this.start = start;
        }

        public @RangeFlagType
        int getType() {
            return type;
        }

        @SuppressWarnings("unused")
        public void setType(@RangeFlagType int type) {
            this.type = type;
        }

        @SuppressWarnings("unused")
        public String getParamsStr() {
            return paramsStr;
        }

        @SuppressWarnings("unused")
        public void setParamsStr(String paramsStr) {
            this.paramsStr = paramsStr;
        }

        public @NonNull
        String getOriginalStr() {
            return originalStr;
        }

        @SuppressWarnings("unused")
        public void setOriginalStr(@NonNull String originalStr) {
            this.originalStr = originalStr;
        }

        public @NonNull
        String getShouldReplacedStr() {
            return shouldReplacedStr;
        }

        @SuppressWarnings("unused")
        public void setShouldReplacedStr(@NonNull String shouldReplacedStr) {
            this.shouldReplacedStr = shouldReplacedStr;
        }
    }

    @SuppressWarnings("unused")
    public @Nullable
    OnMatcherClickListener getMatcherClickListener() {
        return mMatcherClickListener;
    }

    @SuppressWarnings("unused")
    public void setMatcherClickListener(@Nullable OnMatcherClickListener matcherClickListener) {
        this.mMatcherClickListener = matcherClickListener;
    }

    @SuppressWarnings("unused")
    public void setDefaultMatcherClickListener() {
        mMatcherClickListener = RichTextInitalor.getDefaultMatcherClickListener(getContext());
    }

    public interface OnMatcherClickListener {
        void onMatcherClick(@NonNull MatcherFlag flag);
    }
}
