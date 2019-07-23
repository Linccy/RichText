package com.linccy.richtext;

import com.linccy.richtext.util.MatcherFlag;

public class RichText {

    public static Builder defaultBuilder;
    //自定义标签匹配
    public static final String MATCH_ELEMENT = "<(%1$s-.*?)[^<>]*?\\s(.*?)>(.*?)</%1$s-(.*?)>";  //"<" + element + "[^<>]*?\\s" + attr + "=['\"]?(.*?)>(.*?)</" + element+">";
    //自定义标签属性过滤
    public static String MATCH_PARAMS = "(.*?)=['\"](.*?)['\"]";// id="123

    public abstract static class Builder {
        public String tagName;

        public abstract int getColors(MatcherFlag flag);

        public Builder setTagName(String tagName) {
            this.tagName = tagName;
            return this;
        }

        public String getMatchElementRegix() {
            return String.format(MATCH_ELEMENT, tagName);
        }

        public String getMatchParamsRegix() {
            return MATCH_PARAMS;
        }

        public void build() {
            defaultBuilder = this;
        }

    }

}
