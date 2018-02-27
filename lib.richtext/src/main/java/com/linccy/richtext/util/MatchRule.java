package com.linccy.richtext.util;

/**
 * 正则匹配规则
 */

public interface MatchRule {
    //@用户
    String MATCH_MENTION = "@([^@^\\s^:^,^;^'，'^'；'^>^<]{1,})";//@([^@^\\s^:]{1,})([\\s\\:\\,\\;]{0,1})");//@.+?[\\s:]

    //话题
    String MATCH_TOPIC = "#.+?#";

    //网址、链接
    String MATCH_URI = "(https?|ftp|file)://[-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|]\\s";

    //域名
    String MATCH_REALM_NAME = "[a-zA-z]+.[^\\s]*.[^\\s]*\\s";

    //自定义标签属性过滤
    String MATCH_ELEMENT_FORMAT = "<%s(.*?)[^<>]*?\\sid=['\"]?(.*?)>(.*?)</%s(.*?)>";  //"<" + element + "[^<>]*?\\s" + attr + "=['\"]?(.*?)>(.*?)</" + element+">";

    //用户标签属性过滤
    String MATCH_USER_ELEMENT = "<" + "bk-user" + "[^<>]*?\\s" + "id" + "=['\"]?(.*?)>"+ "(.*?)" +"</bk-user>";
}
