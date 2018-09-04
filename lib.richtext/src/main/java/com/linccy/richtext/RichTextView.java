package com.linccy.richtext;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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

import com.linccy.richtext.util.MatchRule;
import com.linccy.richtext.util.MatcherFlag;

import java.util.ArrayList;
import java.util.List;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 必须设置.setMovementMethod(RichLinkMovementMethod.getNewInstance());span点击事件才会生效
 * {@link RichLinkMovementMethod#getInstance()}
 */
public class RichTextView extends android.support.v7.widget.AppCompatTextView {
    public static final String TAG = RichTextView.class.getName();
    public static final String MATCH_MENTION = MatchRule.MATCH_ELEMENT; //这个和其他不同
    public static final String MATCH_URI = MatchRule.MATCH_URI;
    public static final String MATCH_REALM_NAME = MatchRule.MATCH_REALM_NAME;

    public static final String TAG_TYPE_USER = "user";
    public static final String TAG_TYPE_LINK = "link";
    public static final String TAG_TYPE_ARTICLE = "article";
    public static final String TAG_TYPE_K_SITE = "site";
    public static final int AT_USER_TEXT_COLOR = -11759394;
    public static final int ADD_TOPIC_TEXT_COLOR = AT_USER_TEXT_COLOR;
    public static final int ADD_LINK_TEXT_COLOR = -11759394;

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
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && getMaxLines() != Integer.MAX_VALUE && !mEllipsized) {
            float avail = getMeasuredWidth() * (getMaxLines() > 0 ? getMaxLines() : getLineCount());
            if (getMeasuredWidth() < avail && getLineCount() > 1) {
                avail -= getMeasuredWidth() * 0.1;
            }
            if (!TextUtils.isEmpty(getText())) {
                mEllipsized = true;
            }
            super.setText(TextUtils.ellipsize(getText(), getPaint(), avail, getEllipsize()));
        }
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
                    tagContent = RichConfig.getLinkDesc(getContext());
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
                        mMatcherClickListener.onMatcherClick(getContext(), flag);
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
        } else {
            return super.onTouchEvent(event);
        }
    }

    private static void log(String msg) {
        Log.d(TAG, msg);
    }


    @SuppressWarnings("unused")
    @Nullable
    public OnMatcherClickListener getMatcherClickListener() {
        return mMatcherClickListener;
    }

    @SuppressWarnings("unused")
    public void setMatcherClickListener(@Nullable OnMatcherClickListener matcherClickListener) {
        this.mMatcherClickListener = matcherClickListener;
    }

    public interface OnMatcherClickListener {
        void onMatcherClick(Context context, @NonNull MatcherFlag flag);
    }
}
