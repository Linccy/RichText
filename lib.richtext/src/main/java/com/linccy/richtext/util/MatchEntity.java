package com.linccy.richtext.util;

import android.support.annotation.Keep;

@Keep
public class MatchEntity {
    private String id;
    private String type;
    private String content;
    private String formatRule;

    public MatchEntity(String id, String type, String content, String formatRule) {
        this.id = id;
        this.type = type;
        this.content = content;
        this.formatRule = formatRule;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getFormatRule() {
        return formatRule;
    }

    public void setFormatRule(String formatRule) {
        this.formatRule = formatRule;
    }
}
