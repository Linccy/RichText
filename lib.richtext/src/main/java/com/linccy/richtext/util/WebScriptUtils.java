package com.linccy.richtext.util;

import android.content.Context;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebScriptUtils {
    /**
     *  webView中@ 等自定义标签的点击事件处理
     * */
    private static final String TRIBE_ARTICLE_FUNCTION_NAME = "whichElement";

    private static final String TRIBE_ARTICLE_JS = "function " +
            TRIBE_ARTICLE_FUNCTION_NAME +
            "(e)\n" +
            "{\n" +
            "    var targ\n" +
            "    if (!e) var e = window.event\n" +
            "    if (e.target) targ = e.target\n" +
            "    else if (e.srcElement) targ = e.srcElement\n" +
            "    if (targ.nodeType == 3) // defeat Safari bug\n" +
            "        targ = targ.parentNode\n" +
            "    var tname=targ.tagName.toLowerCase()\n" +
            "    if(tname==\"bk-user\"){\n" +
            "        var userid=targ.getAttribute(\"user-id\")\n" +
            "        var username=targ.textContent\n" +
            "        android.clickuser(userid,username)\n" +
            "    }\n" +
            "}";

    /**
     * html样式 通过js回调响应user标签
     * */
    public static String getTribeArticleHtml(Context context, String body) {
        String html = "<!DOCTYPE html>"
                + "<html>"
                + "<head>"
                + "<style>" +
                "a{color:" + "#FF0000" + ";text-decoration:underline; }" +
                "\n" +
                "bk-user\n" +
                "{\n" +
                "color: " +
                "#FF0FFF" +
                ";\n" +
                "}" +
                " </style>"
                + "<script language=\"JavaScript\" type=\"text/javascript\">\n" +
                "\n" +
                WebScriptUtils.TRIBE_ARTICLE_JS +
                "      \n" +
                "</script> "
                + "</head>"
                + "<body onclick=\"" +
                TRIBE_ARTICLE_FUNCTION_NAME +
                "(event)\" style='line-height:180%;font-weight:0;padding:0;margin:0;" +
                "background:" + "#FFFFFFFF" + ";" +
                "color:" + "#FFFFFFFF" + ";" +
                "font-size:" + "20px" + "%;" +
                "'>"
                + body
                + "</body>"
                + "</html>";
        return html;
    }

    /**
     * 获取xss等非法字符
     * */
    private static List<Pattern> getXssPatternList() {
        List<Pattern> patternList = new ArrayList<>();

        patternList.add(Pattern.compile("<(no)?script[^>]*>.*?</(no)?script>", Pattern.CASE_INSENSITIVE));
        patternList.add(Pattern.compile("eval\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL));
        patternList.add(Pattern.compile("expression\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL));
        patternList.add(Pattern.compile("(javascript:|vbscript:|view-source:)*", Pattern.CASE_INSENSITIVE));
        patternList.add(Pattern.compile("<(\"[^\"]*\"|\'[^\']*\'|[^\'\">])*>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL));
        patternList.add(Pattern.compile("(window\\.location|window\\.|\\.location|document\\.cookie|document\\.|alert\\(.*?\\)|window\\.open\\()*",
                Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL));
        patternList.add(Pattern.compile("<+\\s*\\w*\\s*(oncontrolselect|oncopy|oncut|ondataavailable|ondatasetchanged" +
                "|ondatasetcomplete|ondblclick|ondeactivate|ondrag|ondragend|ondragenter|ondragleave|ondragover|ondragstart" +
                "|ondrop|onerror=|onerroupdate|onfilterchange|onfinish|onfocus|onfocusin|onfocusout|onhelp|onkeydown" +
                "|onkeypress|onkeyup|onlayoutcomplete|onload|onlosecapture|onmousedown|onmouseenter|onmouseleave|onmousemove" +
                "|onmousout|onmouseover|onmouseup|onmousewheel|onmove|onmoveend|onmovestart|onabort|onactivate|onafterprint" +
                "|onafterupdate|onbefore|onbeforeactivate|onbeforecopy|onbeforecut|onbeforedeactivate|onbeforeeditocus|onbeforepaste" +
                "|onbeforeprint|onbeforeunload|onbeforeupdate|onblur|onbounce|oncellchange|onchange|onclick|oncontextmenu|onpaste" +
                "|onpropertychange|onreadystatechange|onreset|onresize|onresizend|onresizestart|onrowenter|onrowexit|onrowsdelete" +
                "|onrowsinserted|onscroll|onselect|onselectionchange|onselectstart|onstart|onstop|onsubmit|onunload)+\\s*=+",
                Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL));
        return patternList;
    }

    public static String fliteXss(String value) {
        if(!TextUtils.isEmpty(value)) {
            for(Pattern pattern : getXssPatternList()) {
                Matcher matcher = pattern.matcher(value);
                // 匹配
                if(matcher.find()) {
                    // 删除相关字符串
                    value = matcher.replaceAll("");
                }
            }

//            value = value.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
        }
        return value;
    }
}
