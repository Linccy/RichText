package com.linccy.richtext.util;

/**
 * 正则匹配规则
 */

public interface MatchRule {

    //自定义标签属性过滤
    String MATCH_PARAMS = "(.*?)=['\"](.*?)['\"]";// id="123" or href="www.lin.com"

    //自定义标签匹配
    String MATCH_ELEMENT = "<(bk-.*?)[^<>]*?\\s(.*?)>(.*?)</bk-(.*?)>";  //"<" + element + "[^<>]*?\\s" + attr + "=['\"]?(.*?)>(.*?)</" + element+">";

}
