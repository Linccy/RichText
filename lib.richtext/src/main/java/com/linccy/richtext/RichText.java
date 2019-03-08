package com.linccy.richtext;

public class RichText {

    //@用户
    public static String MATCH_MENTION = "@([^@^\\s^:^,^;^'，'^'；'^>^<]{1,})";//@([^@^\\s^:]{1,})([\\s\\:\\,\\;]{0,1})");//@.+?[\\s:]
    //话题
    public static String MATCH_TOPIC = "#.+?#";
    //邀请口令
    public static String MATCH_TRIBE_COMMAND = "฿['\"]?(.*?)฿";
    //网址、链接
    public static String MATCH_URI = "(https?|ftp|file)://[-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|]\\S";
    //域名
    public static String MATCH_REALM_NAME = MATCH_URI;
    //自定义标签匹配
    public static String MATCH_ELEMENT = "<(bk-.*?)[^<>]*?\\s(.*?)>(.*?)</bk-(.*?)>";  //"<" + element + "[^<>]*?\\s" + attr + "=['\"]?(.*?)>(.*?)</" + element+">";
    //自定义标签属性过滤
    public static String MATCH_PARAMS = "(.*?)=['\"](.*?)['\"]";// id="123" or href="www.lin.com"
    //用户标签属性过滤
    public static String MATCH_USER_ELEMENT = "<bk-user" + "[^<>]*?\\s" + "id" + "=['\"]?(.*?)>" + "(.*?)" + "</bk-user>";
    //a标签href属性匹配
    public static String MATCH_A_HREF_ELEMENT = "<a" + "\\s" + "href" + "=['\"]?(.*?)>" + "(.*?)" + "</a>";
    public static String MATCH_IMG_ELEMENT = "<(img|IMG)(.*?)(/>|></img>|>)";
    public static String MATCH_AUDIO_ELEMENT = "<(audio|AUDIO)(.*?)(/>|></audio>|>)";
    public static String MATCH_I_FRAME = "<(iframe)(.*?)(/>|></iframe>|>)";
    public static String MATCH_SRC_PARAM = "(src|SRC)=['\"]?(.*?)['\"]";
    public static String MATCH_DURATION_PARAM = "(duration|DURATION)=['\"]?(.*?)['\"]";
    public static String MATCH_FILTER_NUMBERS = "[0-9]+";
    public static String MATCH_K_SITE_TAG_WITH_URL = "tag[/]?(.*?)[/?\\s]";
    public static String MATCH_K_SITE_ID_WITH_URL = "ksite[/]?(.*?)[/?\\s]";
    public static String MATCH_K_ARTICLES_ID_WITH_URL = "articles[/]?(.*?)[/?\\s]";
    public static String MATCH_K_ARTICLE_ID_WITH_URL = "blog[/]?(.*?)[/?\\s]";
    public static String MATCH_K_ESSAY_ID_WITH_URL = "feed[/]?(.*?)[/?\\s]";
    public static String MATCH_K_DT42_WITH_URL = "dt42[/]?(.*?)[/?\\s]";
    //    String MATCH_REALM_NAME = "[a-zA-Z0-9][-a-zA-Z0-9]{0,62}(\\.[a-zA-Z0-9][-a-zA-Z0-9]{0,62})+\\.?";

    public static final String MATCH_MENTION_DEFAULT = "bk-user";
    public static String MATCH_USER_ELEMENT_FORMAT = "<%1$s" + "[^<>]*?\\s" + "id" + "=['\"]?(.*?)>" + "(.*?)" + "</%1$s>";

    public static class Builder {
        private String matchMention = MATCH_MENTION_DEFAULT;
        public String getMatchMention() {
            return String.format(MATCH_USER_ELEMENT_FORMAT, matchMention);
        }

        public Builder setMatchMention(String matchMention) {
            this.matchMention = matchMention;
            return this;
        }

        public String getMatchTopic() {
            return MATCH_TOPIC;
        }

        public Builder setMatchTopic(String matchTopic) {
            MATCH_TOPIC = matchTopic;
            return this;
        }

        public String getMatchTribeCommand() {
            return MATCH_TRIBE_COMMAND;
        }

        public Builder setMatchTribeCommand(String matchTribeCommand) {
            MATCH_TRIBE_COMMAND = matchTribeCommand;
            return this;
        }

        public String getMatchUri() {
            return MATCH_URI;
        }

        public Builder setMatchUri(String matchUri) {
            MATCH_URI = matchUri;
            return this;
        }

        public String getMatchRealmName() {
            return MATCH_REALM_NAME;
        }

        public Builder setMatchRealmName(String matchRealmName) {
            MATCH_REALM_NAME = matchRealmName;
            return this;
        }

        public String getMatchElement() {
            return MATCH_ELEMENT;
        }

        public Builder setMatchElement(String matchElement) {
            MATCH_ELEMENT = matchElement;
            return this;
        }

        public static String getMatchParams() {
            return MATCH_PARAMS;
        }

        public Builder setMatchParams(String matchParams) {
            MATCH_PARAMS = matchParams;
            return this;
        }

        public String getMatchUserElement() {
            return MATCH_USER_ELEMENT;
        }

        public String getMatchAHrefElement() {
            return MATCH_A_HREF_ELEMENT;
        }

        public Builder setMatchAHrefElement(String matchAHrefElement) {
            MATCH_A_HREF_ELEMENT = matchAHrefElement;
            return this;
        }

        public String getMatchImgElement() {
            return MATCH_IMG_ELEMENT;
        }

        public Builder setMatchImgElement(String matchImgElement) {
            MATCH_IMG_ELEMENT = matchImgElement;
            return this;
        }

        public String getMatchAudioElement() {
            return MATCH_AUDIO_ELEMENT;
        }

        public Builder setMatchAudioElement(String matchAudioElement) {
            MATCH_AUDIO_ELEMENT = matchAudioElement;
            return this;
        }

        public String getMatchIFrame() {
            return MATCH_I_FRAME;
        }

        public Builder setMatchIFrame(String matchIFrame) {
            MATCH_I_FRAME = matchIFrame;
            return this;
        }

        public String getMatchSrcParam() {
            return MATCH_SRC_PARAM;
        }

        public Builder setMatchSrcParam(String matchSrcParam) {
            MATCH_SRC_PARAM = matchSrcParam;
            return this;
        }

        public String getMatchDurationParam() {
            return MATCH_DURATION_PARAM;
        }

        public Builder setMatchDurationParam(String matchDurationParam) {
            MATCH_DURATION_PARAM = matchDurationParam;
            return this;
        }

        public String getMatchFilterNumbers() {
            return MATCH_FILTER_NUMBERS;
        }

        public Builder setMatchFilterNumbers(String matchFilterNumbers) {
            MATCH_FILTER_NUMBERS = matchFilterNumbers;
            return this;
        }

        public static String getMatchKSiteTagWithUrl() {
            return MATCH_K_SITE_TAG_WITH_URL;
        }

        public static void setMatchKSiteTagWithUrl(String matchKSiteTagWithUrl) {
            MATCH_K_SITE_TAG_WITH_URL = matchKSiteTagWithUrl;
        }

        public static String getMatchKSiteIdWithUrl() {
            return MATCH_K_SITE_ID_WITH_URL;
        }

        public static void setMatchKSiteIdWithUrl(String matchKSiteIdWithUrl) {
            MATCH_K_SITE_ID_WITH_URL = matchKSiteIdWithUrl;
        }

        public static String getMatchKArticlesIdWithUrl() {
            return MATCH_K_ARTICLES_ID_WITH_URL;
        }

        public static void setMatchKArticlesIdWithUrl(String matchKArticlesIdWithUrl) {
            MATCH_K_ARTICLES_ID_WITH_URL = matchKArticlesIdWithUrl;
        }

        public static String getMatchKArticleIdWithUrl() {
            return MATCH_K_ARTICLE_ID_WITH_URL;
        }

        public static void setMatchKArticleIdWithUrl(String matchKArticleIdWithUrl) {
            MATCH_K_ARTICLE_ID_WITH_URL = matchKArticleIdWithUrl;
        }

        public static String getMatchKEssayIdWithUrl() {
            return MATCH_K_ESSAY_ID_WITH_URL;
        }

        public static void setMatchKEssayIdWithUrl(String matchKEssayIdWithUrl) {
            MATCH_K_ESSAY_ID_WITH_URL = matchKEssayIdWithUrl;
        }

        public static String getMatchKDt42WithUrl() {
            return MATCH_K_DT42_WITH_URL;
        }

        public static void setMatchKDt42WithUrl(String matchKDt42WithUrl) {
            MATCH_K_DT42_WITH_URL = matchKDt42WithUrl;
        }

        public Builder setMatchUserElement(String elementName) {
            return this;
        }

        public void build() {
        }

    }

}
