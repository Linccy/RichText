package com.linccy.richtext.util;

/**
 * 正则匹配规则
 */

public interface MatchRule {

    //@用户
    String MATCH_MENTION = "@([^@^\\s^:^,^;^'，'^'；'^>^<]{1,})";//@([^@^\\s^:]{1,})([\\s\\:\\,\\;]{0,1})");//@.+?[\\s:]

    //话题
    String MATCH_TOPIC = "#.+?#";

    //邀请口令
    String MATCH_TRIBE_COMMAND = "฿['\"]?(.*?)฿";

    //网址、链接
    String MATCH_URI = "(https?|ftp|file)://[-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|]\\S";

    //域名
    String MATCH_REALM_NAME = MATCH_URI;
//    String MATCH_REALM_NAME = "[a-zA-Z0-9][-a-zA-Z0-9]{0,62}(\\.[a-zA-Z0-9][-a-zA-Z0-9]{0,62})+\\.?";

    //自定义标签匹配
    String MATCH_ELEMENT = "<(bk-.*?)[^<>]*?\\s(.*?)>(.*?)</bk-(.*?)>";  //"<" + element + "[^<>]*?\\s" + attr + "=['\"]?(.*?)>(.*?)</" + element+">";

    //自定义标签属性过滤
    String MATCH_PARAMS = "(.*?)=['\"](.*?)['\"]";// id="123" or href="www.lin.com"

    //用户标签属性过滤
    String MATCH_USER_ELEMENT = "<bk-user" + "[^<>]*?\\s" + "id" + "=['\"]?(.*?)>"+ "(.*?)" +"</bk-user>";

    //a标签href属性匹配
    String MATCH_A_HREF_ELEMENT = "<a" + "\\s" + "href" + "=['\"]?(.*?)>"+ "(.*?)" +"</a>";

    String MATCH_IMG_ELEMENT = "<(img|IMG)(.*?)(/>|></img>|>)";

    String MATCH_AUDIO_ELEMENT = "<(audio|AUDIO)(.*?)(/>|></audio>|>)";

    String MATCH_I_FRAME = "<(iframe)(.*?)(/>|></iframe>|>)";

    String MATCH_SRC_PARAM = "(src|SRC)=['\"]?(.*?)['\"]";

    String MATCH_DURATION_PARAM = "(duration|DURATION)=['\"]?(.*?)['\"]";

    String MATCH_FILTER_NUMBERS = "[0-9]+";

    String MATCH_K_SITE_TAG_WITH_URL = "tag[/]?(.*?)[/?\\s]";

    String MATCH_K_SITE_ID_WITH_URL = "ksite[/]?(.*?)[/?\\s]";

    String MATCH_K_ARTICLES_ID_WITH_URL = "articles[/]?(.*?)[/?\\s]";

    String MATCH_K_ARTICLE_ID_WITH_URL = "blog[/]?(.*?)[/?\\s]";

    String MATCH_K_ESSAY_ID_WITH_URL = "feed[/]?(.*?)[/?\\s]";

    String MATCH_K_DT42_WITH_URL = "dt42[/]?(.*?)[/?\\s]";
}
