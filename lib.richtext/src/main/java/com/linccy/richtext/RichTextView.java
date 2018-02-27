package com.linccy.richtext;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.linccy.richtext.util.MatchRule;
import com.linccy.richtext.util.MatcherFlag;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RichTextView extends android.support.v7.widget.AppCompatTextView {
    public static final String TAG = RichTextView.class.getName();
    public static final String MATCH_MENTION = String.format(MatchRule.MATCH_ELEMENT_FORMAT, RichConfig.getTagHeard(), RichConfig.getTagHeard()); //这个和其他不同
    public static final String MATCH_URI = MatchRule.MATCH_URI;
    public static final String MATCH_REALM_NAME = MatchRule.MATCH_REALM_NAME;
    public static final int AT_USER_TEXT_COLOR = -432079;
    public static final int ADD_TOPIC_TEXT_COLOR = AT_USER_TEXT_COLOR;
    public static final int ADD_LINK_TEXT_COLOR = -11172680;

    private OnTagClickListener tagClickListener;

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
        super.setText(text, type);
        Spannable spannable = new SpannableString(text);
        spannable = matchMention(spannable);
//        spannable = matchTopic(spannable);
//        spannable = matchLink(spannable);
        super.setText(spannable, type);
    }

    public void setTagClickListener(OnTagClickListener tagClickListener) {
        this.tagClickListener = tagClickListener;
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
            String type;
            final String tagBody = matcher.group(0);
            final String tagType = matcher.group(1);
            type = tagType;
            final String tagId = matcher.group(2);
            final String tagContent = matcher.group(3);

            if (tagBody == null || tagType == null || tagId == null || tagContent == null) {
                continue;
            }

            int matcherStart = matcher.start();
            int matcherEnd = matcher.end();
            matcherFlags.add(new MatcherFlag(matcherStart, type, tagId, tagBody, tagContent));
            log("matchMention:" + tagId + " " + matcherStart + " " + matcherEnd);
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
            spannable.setSpan(new ClickableSpan() {

                @Override
                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setUnderlineText(false); //去除下划线
                    ds.setColor(AT_USER_TEXT_COLOR);
                }

                @Override
                public void onClick(View widget) {
                    Log.d(TAG, "click tag " + flag.getOriginalStr());
                    if (tagClickListener != null) {
                        tagClickListener.onClick(flag.getType(), flag.getFlagId(), flag.getShouldReplacedStr(), flag.getOriginalStr());
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

    private static void log(String msg) {
        Log.d(TAG, msg);
    }

    public interface OnTagClickListener {
        /**
         * format <aaa-type id=cccc>content</aaa-type>
         */
        void onClick(String type, String id, String content, String realStr);
    }
}
