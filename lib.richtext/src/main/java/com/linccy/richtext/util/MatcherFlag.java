package com.linccy.richtext.util;

/**
 * @author Linccy
 * 记录标签属性
 */
public class MatcherFlag {
    private int start;
    private String type;
    private String flagId;
    private String originalStr;
    private String shouldReplacedStr;

    public MatcherFlag(int start, String type, String flagId, String originalStr, String shouldReplacedStr) {
        this.start = start;
        this.type = type;
        this.flagId = flagId;
        this.originalStr = originalStr;
        this.shouldReplacedStr = shouldReplacedStr;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFlagId() {
        return flagId;
    }

    public void setFlagId(String flagId) {
        this.flagId = flagId;
    }

    public String getOriginalStr() {
        return originalStr;
    }

    public void setOriginalStr(String originalStr) {
        this.originalStr = originalStr;
    }

    public String getShouldReplacedStr() {
        return shouldReplacedStr;
    }

    public void setShouldReplacedStr(String shouldReplacedStr) {
        this.shouldReplacedStr = shouldReplacedStr;
    }
}
