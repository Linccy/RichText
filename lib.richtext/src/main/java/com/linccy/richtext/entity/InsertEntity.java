package com.linccy.richtext.entity;

import android.graphics.Color;
import android.support.annotation.ColorInt;


public class InsertEntity {
    private String id;
    private String insertRule;
    private String insertContent;
    private String insertColor;

    public InsertEntity(String insertRule, String insertContent) {
        this(insertRule, insertContent, Color.BLUE);
    }

    public InsertEntity(String insertRule, String insertContent, @ColorInt int insertColor) {
        this(insertRule, insertContent, "#" + Integer.toHexString(insertColor));
    }

    public InsertEntity(String insertRule, String insertContent, String insertColor) {
        this.insertRule = insertRule;
        this.insertContent = insertContent;
        this.insertColor = insertColor;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getInsertRule() {
        return insertRule;
    }

    public void setInsertRule(String insertRule) {
        this.insertRule = insertRule;
    }

    public String getInsertContent() {
        return insertContent;
    }

    public void setInsertContent(String insertContent) {
        this.insertContent = insertContent;
    }

    public String getInsertColor() {
        return insertColor;
    }

    public void setInsertColor(String insertColor) {
        this.insertColor = insertColor;
    }

    @Override
    public String toString() {
        return "InsertEntity{" +
                "insertRule='" + insertRule + '\'' +
                ", insertContent='" + insertContent + '\'' +
                ", insertColor='" + insertColor + '\'' +
                '}';
    }
}
