package com.linccy.demo;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.linccy.richtext.RichLinkMovementMethod;
import com.linccy.richtext.RichTextView;
import com.linccy.richtext.util.MatcherFlag;

public class DemoTools {
    public static void initRichTextView(RichTextView richTextView) {
        richTextView.setMovementMethod(RichLinkMovementMethod.getInstance());
        richTextView.setMatcherClickListener(new RichTextView.OnMatcherClickListener() {
            String TAG = "initRichTextView";
            @Override
            public void onMatcherClick(Context context, @NonNull MatcherFlag flag) {
                switch (flag.getType()) {

                    case MatcherFlag.MatcherFlagType.LINK:
                        String link = flag.getParamLink();
                        if (TextUtils.isEmpty(link)) {
                            Log.d(TAG, "onMatcherClick: href is empty");
                            return;
                        }
                        Intent intent = new Intent();
                        intent.setAction("android.intent.action.VIEW");
                        intent.setData(Uri.parse(flag.getParamLink()));
                        context.startActivity(intent);

                        break;

                    case MatcherFlag.MatcherFlagType.USER:
//                        Toast.makeText(getContext(), flag.getOriginalStr(), Toast.LENGTH_LONG).show();
                        String userId = flag.getParamId();
                        Log.d(TAG, "onMatcherClick: user" +  flag.getParamsStr());
                        break;

                    case MatcherFlag.MatcherFlagType.ARTICLE:
//                        TribeArticleDetailActivity.startActivity(context, flag.getParamId(), flag.getParams("type"));
                        Log.d(TAG, "onMatcherClick: article");
                        break;

                    case MatcherFlag.MatcherFlagType.K_SITE:
//                        TribeHomeActivity.startActivity(context, flag.getParamId());
                        Log.d(TAG, "onMatcherClick: k_site");
                        break;

                    case MatcherFlag.MatcherFlagType.UNDEFINE:
                    default:
                        break;
                }
            }
        });
    }
}
