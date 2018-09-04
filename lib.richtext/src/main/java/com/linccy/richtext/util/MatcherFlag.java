package com.linccy.richtext.util;

import android.support.annotation.IntDef;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Linccy
 *         记录标签属性
 */
public class MatcherFlag {
    public interface MatcherFlagType {
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
    public @interface RangeFlagType {
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

    public MatcherFlag(@IntRange(from = 0) int start, @RangeFlagType int type, @NonNull String paramsStr, @NonNull String originalStr, @NonNull String shouldReplacedStr) {
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
