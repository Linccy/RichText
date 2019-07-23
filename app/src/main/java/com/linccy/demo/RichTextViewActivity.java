package com.linccy.demo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.linccy.richtext.RichTextView;

public class RichTextViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rich_text_view);
        RichTextView textView = findViewById(R.id.source_text);
        textView.setText("试试看k站<bk-user id='6307303'>@大金链子花衬衫</bk-user> <bk-market id='954'>$ BTC\\/USDT, 火币全球站 $</bk-market> <bk-link href=\\\"https:\\/\\/www.aicoin.net.cn\\/download\\\" target=\\\"_blank\\\" >相关链接 </bk-link> 哈哈");
    }
}
