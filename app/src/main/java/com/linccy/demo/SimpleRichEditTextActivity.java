package com.linccy.demo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.linccy.richtext.RichEditText;
import com.linccy.richtext.entity.InsertEntity;

/**
 * SimpleRichText 实例
 */

public class SimpleRichEditTextActivity extends AppCompatActivity implements View.OnClickListener {
    private RichEditText richEditText;
    public static final String TAG_AT = "@";
    public static final String TAG_CURRENCY = "$";
    public static final String TAG_TOPIC = "#";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_rich_text);

        richEditText = (RichEditText) findViewById(R.id.edit_content);

        findViewById(R.id.at_user).setOnClickListener(this);
        findViewById(R.id.add_tag1).setOnClickListener(this);
        findViewById(R.id.add_tag2).setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.at_user:
                InsertEntity entityUser = new InsertEntity(TAG_AT, "Linccy");
                entityUser.setId(String.valueOf(System.currentTimeMillis()));
                richEditText.appendMention(entityUser.getInsertContent());
                break;

            case R.id.add_tag1:
                InsertEntity entityTopic = new InsertEntity(TAG_TOPIC, TAG_TOPIC + "祝你平安" + TAG_TOPIC);
                entityTopic.setId(String.valueOf(System.currentTimeMillis()));
                richEditText.appendTopic(entityTopic.getInsertContent());
                break;

//            case R.id.add_tag2:
//                InsertEntity entity_Currency = new InsertEntity(TAG_CURRENCY, TAG_CURRENCY + "摩根大通指数" + TAG_CURRENCY);
//                entity_Currency.setId(String.valueOf(System.currentTimeMillis()));
//                richEditText.append(entity_Currency);
//                break;

            default:
                break;
        }
    }
}
