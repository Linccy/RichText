package com.linccy.richtext;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

public class RichTextInitalor {
    private static final String TAG = RichTextInitalor.class.getName();
    
    public static final String TAG_TYPE_USER = "user";
    public static final String TAG_TYPE_LINK = "link";
    public static final String TAG_TYPE_ARTICLE = "article";
    public static final String TAG_TYPE_K_SITE = "site";

    public static void initRule() {
        
    }
    
    public static RichTextView.OnMatcherClickListener getDefaultMatcherClickListener(Context context) {
        return new RichTextView.OnMatcherClickListener() {
            @Override
            public void onMatcherClick(@NonNull RichTextView.MatcherFlag flag) {
                switch (flag.getType()) {

                    case RichTextView.MatcherFlag.MatcherFlagType.LINK:
                        String link = flag.getParamLink();
                        if (TextUtils.isEmpty(link)) {
                            Log.e(TAG, "onMatcherClick: href is empty");
                            return;
                        }
//                        if(!Misc.doTribeActionByURL(context, link)) {
//                            Intent intent = new Intent();
//                            intent.setAction("android.intent.action.VIEW");
//                            intent.setData(Uri.parse(flag.getParamLink()));
//                            context.startActivity(intent);
//                        }
                        break;

//                    case RichTextView.MatcherFlag.MatcherFlagType.USER:
////                        Toast.makeText(context, flag.getOriginalStr(), Toast.LENGTH_LONG).show();
//                        UserHomeActivity.startActivity(context, flag.getParamId());
//                        break;
//
//                    case RichTextView.MatcherFlag.MatcherFlagType.ARTICLE:
//                        TribeArticleDetailActivity.startActivity(context, flag.getParamId(), flag.getParams("type"));
//                        break;
//
//                    case RichTextView.MatcherFlag.MatcherFlagType.K_SITE:
//                        ChatRoomHelper.startChatRoom(context, flag.getParamId());
//                        break;

                    case RichTextView.MatcherFlag.MatcherFlagType.UNDEFINE:
                    default:
                        break;
                }
            }
        };
    }
    
    public static String getLinkStr(Context context) {
        return "相关链接";
    }
}
