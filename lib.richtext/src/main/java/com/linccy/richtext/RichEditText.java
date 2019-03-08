package com.linccy.richtext;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputConnectionWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.linccy.richtext.RichTextInitalor.TAG_TYPE_ARTICLE;
import static com.linccy.richtext.RichTextInitalor.TAG_TYPE_K_SITE;
import static com.linccy.richtext.RichTextInitalor.TAG_TYPE_LINK;
import static com.linccy.richtext.RichTextInitalor.TAG_TYPE_USER;


/**
 * 一个简单的富文本编辑器
 * 实现了@(AT)和##的Tag匹配功能，
 * 具有Tag删除判断，和光标定位判断；预防用户胡乱篡改
 *
 * @author linchenxi
 * @version 1.0.0
 */
@SuppressWarnings("all")
public class RichEditText extends AppCompatEditText {
    public static final String MATCH_MENTION = RichText.MATCH_MENTION;
    public static final String MATCH_TOPIC = RichText.MATCH_TOPIC;
    public static final String MATCH_URI = RichText.MATCH_URI;
    public static final String MATCH_REALM_NAME = RichText.MATCH_REALM_NAME;
    public static final int AT_USER_TEXT_COLOR = 0xFF6482D9;
    public static final int ADD_TOPIC_TEXT_COLOR = AT_USER_TEXT_COLOR;
    public static final int ADD_LINK_TEXT_COLOR = 0xFF6482D9;
    public static boolean DEBUG = false;
    private static final String TAG = RichEditText.class.getName();
    private final RichEditText.TagSpanTextWatcher mTagSpanTextWatcher = new RichEditText.TagSpanTextWatcher();
    private RichEditText.OnKeyArrivedListener mOnKeyArrivedListener;

    public RichEditText(Context context) {
        super(context);
        init();
    }

    public RichEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RichEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        addTextChangedListener(mTagSpanTextWatcher);
    }

    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        return new RichEditText.ZanyInputConnection(super.onCreateInputConnection(outAttrs), true);
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        Spannable spannable = new SpannableString(text);
        spannable = firstMatchMention(spannable);
        spannable = matchMention(spannable);
        spannable = matchTopic(spannable);
        spannable = matchLink(spannable);
        super.setText(spannable, type);
    }

    @Override
    protected void onSelectionChanged(int selStart, int selEnd) {
        log("onSelectionChanged:" + selStart + " " + selEnd);
        Editable message = getText();

        if (selStart == selEnd) {
            RichEditText.TagSpan[] list = message.getSpans(selStart - 1, selStart, RichEditText.TagSpan.class);
            if (list.length > 0) {
                // Get first tag
                RichEditText.TagSpan span = list[0];
                int spanStart = message.getSpanStart(span);
                int spanEnd = message.getSpanEnd(span);
                log("onSelectionChanged#Yes:" + spanStart + " " + spanEnd);
                // Check index
                if (Math.abs(selStart - spanStart) > Math.abs(selStart - spanEnd)) {
                    Selection.setSelection(message, spanEnd);
                    replaceCacheTagSpan(message, span, false);
                    return;
                } else {
                    Selection.setSelection(message, spanStart);
                }
            }
        } else {
            RichEditText.TagSpan[] list = message.getSpans(selStart, selEnd, RichEditText.TagSpan.class);
            if (list.length == 0)
                return;
            int start = selStart;
            int end = selEnd;
            for (RichEditText.TagSpan span : list) {
                int spanStart = message.getSpanStart(span);
                int spanEnd = message.getSpanEnd(span);

                if (spanStart < start)
                    start = spanStart;

                if (spanEnd > end)
                    end = spanEnd;
            }
            if (start != selStart || end != selEnd) {
                Selection.setSelection(message, start, end);
                log("onSelectionChanged#No:" + start + " " + end);
            }
        }

        replaceCacheTagSpan(message, null, false);
    }

    @Override
    public boolean onTextContextMenuItem(int id) {
        // Handle the paste option
        if (id == android.R.id.paste) {
            // Handle to the clipboard service.
            ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
            // Handle the data.
            if (clipboard.hasPrimaryClip()) {
                ClipData.Item item = clipboard.getPrimaryClip().getItemAt(0);
                if (item != null) {
                    // Gets the clipboard date to string and do trim
                    String paste = item.coerceToText(getContext()).toString().trim();
                    // Check need space
                    if (mTagSpanTextWatcher != null && mTagSpanTextWatcher.checkCommit(paste))
                        paste = " " + paste;
                    // Clear add span
                    Spannable spannablePaste = new SpannableString(paste);
                    spannablePaste = matchMention(spannablePaste);
                    spannablePaste = matchTopic(spannablePaste);
                    spannablePaste = matchLink(spannablePaste);
                    getText().replace(getSelectionStart() > 0 ? getSelectionStart() : 0, getSelectionEnd() > 0 ? getSelectionEnd() : 0, spannablePaste);
                    return true;
                }
            }
        }
        // Call super
        return super.onTextContextMenuItem(id);
    }

    public void setOnKeyArrivedListener(RichEditText.OnKeyArrivedListener listener) {
        mOnKeyArrivedListener = listener;
    }

    protected boolean callToMention() {
        RichEditText.OnKeyArrivedListener listener = mOnKeyArrivedListener;
        return listener == null || listener.onMentionKeyArrived(this);
    }

    protected boolean callToTopic() {
        RichEditText.OnKeyArrivedListener listener = mOnKeyArrivedListener;
        return listener == null || listener.onTopicKeyArrived(this);
    }

    private void replaceCacheTagSpan(Editable message, RichEditText.TagSpan span, boolean targetDelState) {
        if (mTagSpanTextWatcher != null) {
            mTagSpanTextWatcher.replaceSpan(message, span, targetDelState);
        }
    }

    private String filterDirty(String str) {
        return str.replace("#", "").replace("@", "").replace(" ", "");
    }

    private void replaceLastChar(@NonNull String chr, SpannableString spannable) {
        Editable msg = getText();
        int selStart = getSelectionStart();
        int selEnd = getSelectionEnd();

        selStart = selStart > 0 ? selStart : 0;
        selEnd = selEnd > 0 ? selEnd : 0;

        int selStartBefore = selStart - 1;
        if (selStart == selEnd && selStart > 0
                && chr.equals(msg.subSequence(selStartBefore, selEnd).toString())
                && msg.getSpans(selStartBefore, selEnd, RichEditText.TagSpan.class).length == 0) {
            selStart = selStartBefore;
        }

        msg.replace(selStart >= 0 ? selStart : 0, selEnd >= 0 ? selEnd : 0, spannable);
    }

    /**
     * 添加提到字符串
     *
     * @param mentions 提及的人，不含@
     */
    @SuppressWarnings("unused")
    public void appendMention(String... mentions) {
        appendMention(true, mentions);
    }

    public void appendMention(boolean needSpace, String... mentions) {
        if (mentions == null || mentions.length == 0)
            return;

        String mentionStr = "";

        for (String mention : mentions) {
            if (mention == null || TextUtils.isEmpty(mention = mention.trim())
                    || TextUtils.isEmpty(mention = filterDirty(mention)))
                continue;
            if (needSpace) {
                mentionStr += String.format("@%s", mention).trim() + " ";
            } else {
                mentionStr += String.format("@%s", mention).trim();
            }
        }
        if (TextUtils.isEmpty(mentionStr))
            return;

        SpannableString spannable = new SpannableString(mentionStr);
        matchMention(spannable);

        replaceLastChar(mOnKeyArrivedListener != null ? "@" : "", spannable);
    }

    /**
     * 添加话题字符串
     *
     * @param topics 话题，不含#
     */
    @SuppressWarnings("unused")
    public void appendTopic(String... topics) {
        if (topics == null || topics.length == 0)
            return;

        String topicStr = "";

        for (String topic : topics) {
            if (topic == null || TextUtils.isEmpty(topic = topic.trim())
                    || TextUtils.isEmpty(topic = filterDirty(topic)))
                continue;
            topicStr += String.format("#%s# ", topic);
        }
        if (TextUtils.isEmpty(topicStr))
            return;

        SpannableString spannable = new SpannableString(topicStr);
        RichEditText.matchTopic(spannable);

        replaceLastChar(mOnKeyArrivedListener != null ? "#" : "", spannable);
    }

    /**
     * 添加链接字符串
     *
     * @param topics 话题，不含#
     */
    @SuppressWarnings("unused")
    public void appendLink(String... links) {
        if (links == null || links.length == 0)
            return;

        String linkStr = "";

        for (String link : links) {
            if (link == null || TextUtils.isEmpty(link = link.trim())
                    || TextUtils.isEmpty(link = filterDirty(link)))
                continue;
            linkStr += String.format(" %s ", link);
        }
        if (TextUtils.isEmpty(linkStr))
            return;

        SpannableString spannable = new SpannableString(linkStr);
        RichEditText.matchLink(spannable);

        replaceLastChar(" ", spannable);
    }

    private class TagSpanTextWatcher extends TextWatcherAdapter {
        private RichEditText.TagSpan willDelSpan;

        void replaceSpan(Editable message, RichEditText.TagSpan span, boolean targetDelState) {
            if (span != null)
                span.changeRemoveState(targetDelState, message);

            if (willDelSpan != span) {
                // When different
                RichEditText.TagSpan cacheSpan = willDelSpan;
                if (cacheSpan != null) {
                    cacheSpan.changeRemoveState(false, message);
                }
                willDelSpan = span;
            }
        }

        boolean checkKeyDel() {
            int selStart = getSelectionStart();
            int selEnd = getSelectionEnd();

            selStart = selStart > 0 ? selStart : 0;
            selEnd = selEnd > 0 ? selEnd : 0;

            Editable message = getText();
            log("TagSpanTextWatcher#checkKeyDel:" + selStart + " " + selEnd);
            if (selStart == selEnd) {
                int start = selStart - 1;
                int count = 1;

                start = start < 0 ? 0 : start;

                int end = start + count;
                RichEditText.TagSpan[] list = message.getSpans(start, end, RichEditText.TagSpan.class);

                if (list.length > 0) {
                    // Only get first
                    final RichEditText.TagSpan span = list[0];
                    final RichEditText.TagSpan cacheSpan = willDelSpan;

                    if (span == cacheSpan) {
                        if (span.willRemove)
                            return true;
                        else {
                            span.changeRemoveState(true, message);
                            return false;
                        }
                    }
                }
            }
            // Replace cache tag to null
            replaceSpan(message, null, false);
            return true;
        }

        boolean checkCommit(CharSequence s) {
            if (willDelSpan != null) {
                willDelSpan.willRemove = false;
                willDelSpan = null;
                return s != null && s.length() > 0 && !" ".equals(s.subSequence(0, 1));
            }
            return false;
        }

        @Override
        public void afterTextChanged(Editable s) {
            final RichEditText.TagSpan span = willDelSpan;
            log("TagSpanTextWatcher#willRemove#span:" + (span == null ? "null" : span.toString()));
            if (span != null && span.willRemove) {
                int start = s.getSpanStart(span);
                int end = s.getSpanEnd(span);

                // Remove the span
                s.removeSpan(span);

                // Remove the remaining emoticon text.
                if (start != end) {
                    s.delete(start, end);
                }
            }
        }
    }

    public interface OnKeyArrivedListener {
        boolean onMentionKeyArrived(RichEditText editText);

        boolean onTopicKeyArrived(RichEditText text);
    }

    public void initContentWitchMention(CharSequence text) {
        if (TextUtils.isEmpty(text)) {
            setText("");
            return;
        }
        Pattern pattern = Pattern.compile(MATCH_MENTION);
        Matcher matcher = pattern.matcher(text);

        int matcherEnd = 0;
        while (matcher.find()) {
            String str = matcher.group();
            int currMatcherStart = matcher.start();
            int currMatcherEnd = matcher.end();
            append(text.subSequence(matcherEnd, currMatcherStart));
            appendMention(false, str);
            matcherEnd = currMatcherEnd;
        }
        if(TextUtils.isEmpty(getText())) {
            append(text);
        } else if(matcherEnd > 0 && matcherEnd < text.length()) {
            CharSequence end = text.subSequence(matcherEnd, text.length());
            if (String.valueOf(end).startsWith(" ")) {
                append(end);
            } else {
                append(" " + end);
            }
        }
    }

    public static Spannable matchMention(Spannable spannable) {
        String text = spannable.toString();

        Pattern pattern = Pattern.compile(MATCH_MENTION);
        Matcher matcher = pattern.matcher(text);

        while (matcher.find()) {
            String str = matcher.group();
            int matcherStart = matcher.start();
            int matcherEnd = matcher.end();
            spannable.setSpan(new RichEditText.TagSpan(str, AT_USER_TEXT_COLOR), matcherStart, matcherEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            log("matchMention:" + str + " " + matcherStart + " " + matcherEnd);
        }

        return spannable;
    }

    public Spannable firstMatchMention(Spannable spannable) {
        String text = spannable.toString();

        if (TextUtils.isEmpty(text)) {
            return spannable;
        }

        Pattern pattern = Pattern.compile(RichText.MATCH_ELEMENT);
        Matcher matcher = pattern.matcher(text);

        List<RichTextView.MatcherFlag> matcherFlags = new ArrayList<>();

        while (matcher.find()) {
            @RichTextView.MatcherFlag.RangeFlagType int type;
            final String tagBody = matcher.group(0);
            final String tagType = matcher.group(4);
            if (TextUtils.isEmpty(tagType)) {
                type = RichTextView.MatcherFlag.MatcherFlagType.UNDEFINE;
            } else {
                switch (tagType) {
                    case TAG_TYPE_USER:
                        type = RichTextView.MatcherFlag.MatcherFlagType.USER;
                        break;

                    case TAG_TYPE_LINK:
                        type = RichTextView.MatcherFlag.MatcherFlagType.LINK;
                        break;

                    case TAG_TYPE_ARTICLE:
                        type = RichTextView.MatcherFlag.MatcherFlagType.ARTICLE;
                        break;

                    case TAG_TYPE_K_SITE:
                        type = RichTextView.MatcherFlag.MatcherFlagType.K_SITE;
                        break;

                    default:
                        type = RichTextView.MatcherFlag.MatcherFlagType.UNDEFINE;
                        break;
                }
            }

            final String paramStr = matcher.group(2);
            String tagContent;
            switch (type) {

                case RichTextView.MatcherFlag.MatcherFlagType.LINK:
                    tagContent = RichTextInitalor.getLinkStr(getContext());
                    break;

                case RichTextView.MatcherFlag.MatcherFlagType.USER:
                case RichTextView.MatcherFlag.MatcherFlagType.ARTICLE:
                case RichTextView.MatcherFlag.MatcherFlagType.K_SITE:
                case RichTextView.MatcherFlag.MatcherFlagType.UNDEFINE:
                default:
                    tagContent = matcher.group(3);
                    break;
            }

            if (tagBody == null || tagType == null || paramStr == null || tagContent == null) {
                continue;
            }

            int matcherStart = matcher.start();
            int matcherEnd = matcher.end();
            matcherFlags.add(new RichTextView.MatcherFlag(matcherStart, type, paramStr, tagBody, tagContent));
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
            final RichTextView.MatcherFlag flag = matcherFlags.get(i);
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
            final RichTextView.MatcherFlag flag = matcherFlags.get(i);
            if (RichTextView.MatcherFlag.MatcherFlagType.UNDEFINE == flag.getType()) {
                continue;
            }
            spannable.setSpan(new RichEditText.TagSpan(result.toString(), ADD_TOPIC_TEXT_COLOR), flag.getStart(), flag.getStart() + flag.getShouldReplacedStr().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return spannable;
    }

    public static Spannable matchTopic(Spannable spannable) {
        String text = spannable.toString();

        Pattern pattern = Pattern.compile(MATCH_TOPIC);
        Matcher matcher = pattern.matcher(text);

        while (matcher.find()) {
            String str = matcher.group();
            int matcherStart = matcher.start();
            int matcherEnd = matcher.end();
            spannable.setSpan(new RichEditText.TagSpan(str, ADD_TOPIC_TEXT_COLOR), matcherStart, matcherEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            log("matchTopic:" + str + " " + matcherStart + " " + matcherEnd);
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
        if (DEBUG)
            Log.e(TAG, msg);
    }

    @SuppressWarnings("WeakerAccess")
    public static class TagSpan extends ForegroundColorSpan implements Parcelable {
        private String value;
        public boolean willRemove;
        public static final int DEFAULT_COLOR = 0x5584B8;
        public int textColor;

        public TagSpan(String value) {
            this(value, DEFAULT_COLOR);
        }

        public TagSpan(String value, int color) {
            super(color);
            this.textColor = color;
            this.value = value;
        }

        public TagSpan(Parcel src) {
            super(src);
            value = src.readString();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<TagSpan> CREATOR = new Creator<TagSpan>() {
            @Override
            public RichEditText.TagSpan createFromParcel(Parcel in) {
                return new RichEditText.TagSpan(in);
            }

            @Override
            public RichEditText.TagSpan[] newArray(int size) {
                return new RichEditText.TagSpan[size];
            }
        };

        @Override
        public void updateDrawState(TextPaint ds) {
            //log("TagSpan:updateDrawState:" + isPreDeleteState);
            if (willRemove) {
                ds.setColor(0xFFFFFFFF);
                ds.bgColor = textColor;
            } else {
                super.updateDrawState(ds);
            }
        }

        void changeRemoveState(boolean willRemove, Editable message) {
            if (this.willRemove == willRemove)
                return;
            this.willRemove = willRemove;
            int cacheSpanStart = message.getSpanStart(this);
            int cacheSpanEnd = message.getSpanEnd(this);
            if (cacheSpanStart >= 0 && cacheSpanEnd >= cacheSpanStart) {
                message.setSpan(this, cacheSpanStart, cacheSpanEnd,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }

        public String getValue() {
            return value;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeString(value);
        }

        @Override
        public String toString() {
            return "TagSpan{" +
                    "value='" + value + '\'' +
                    ", willRemove=" + willRemove +
                    '}';
        }
    }


    private class ZanyInputConnection extends InputConnectionWrapper {

        ZanyInputConnection(InputConnection target, boolean mutable) {
            super(target, mutable);
        }

        @Override
        public boolean sendKeyEvent(KeyEvent event) {
            if (event.getAction() == KeyEvent.ACTION_DOWN
                    && event.getKeyCode() == KeyEvent.KEYCODE_DEL) {
                if (!RichEditText.this.mTagSpanTextWatcher.checkKeyDel())
                    return false;
            }
            return super.sendKeyEvent(event);
        }

        @Override
        public boolean deleteSurroundingText(int beforeLength, int afterLength) {
            // magic: in latest Android, deleteSurroundingText(1, 0) will be called for backspace
            if (beforeLength == 1 && afterLength == 0) {
                // backspace
                return sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL))
                        && sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL));
            }

            return super.deleteSurroundingText(beforeLength, afterLength);
        }

        @Override
        public boolean commitText(CharSequence text, int newCursorPosition) {
            return super.commitText(text, newCursorPosition);
        }

        @Override
        public boolean setComposingText(CharSequence text, int newCursorPosition) {
            return super.setComposingText(text, newCursorPosition);
        }
    }
}
