package com.linccy.demo;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.linccy.richtext.RichConfig;
import com.linccy.richtext.RichText;
import com.linccy.richtext.util.MatcherFlag;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_1).setOnClickListener(this);
        findViewById(R.id.btn_2).setOnClickListener(this);

        new RichText.Builder() {
            @Override
            public int getColors(MatcherFlag flag) {
                return Color.BLUE;
            }
        }.setTagName("bk").build();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_1:
                startActivity(new Intent(this, SimpleRichEditTextActivity.class));
                break;

            case R.id.btn_2:
                startActivity(new Intent(this, RichTextViewActivity.class));
                break;

            default:
                break;
        }
    }
}
