package com.linccy.demo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.linccy.richtext.SimpleRichEditor;
import com.linccy.richtext.entity.InsertEntity;

/**
 * SimpleRichText 实例
 */

public class SimpleRichTextActivity extends AppCompatActivity implements View.OnClickListener {
    private SimpleRichEditor simpleRichEditor;
    public static final String TAG_AT = "@";
    public static final String TAG_CURRENCY = "$";
    public static final String TAG_TOPIC = "#";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_rich_text);

        simpleRichEditor = (SimpleRichEditor) findViewById(R.id.edit_content);
        simpleRichEditor.setOnEditListener(new SimpleRichEditor.OnEditListener() {
            @Override
            public void onInputAt() {
                Toast.makeText(SimpleRichTextActivity.this, "进行了@用户操作", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onInputTag() {
                Toast.makeText(SimpleRichTextActivity.this, "进行了输入tag操作", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onDeleteInsetModel(InsertEntity model) {
                Toast.makeText(SimpleRichTextActivity.this, "进行了删除tag操作", Toast.LENGTH_LONG).show();
            }
        });

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
                simpleRichEditor.insertSpecialStr(entityUser);
                break;

            case R.id.add_tag1:
                InsertEntity entityTopic = new InsertEntity(TAG_TOPIC, TAG_TOPIC + "祝你平安" + TAG_TOPIC);
                entityTopic.setId(String.valueOf(System.currentTimeMillis()));
                simpleRichEditor.insertSpecialStr(entityTopic);
                break;

            case R.id.add_tag2:
                InsertEntity entity_Currency = new InsertEntity(TAG_CURRENCY, TAG_CURRENCY + "摩根大通指数" + TAG_CURRENCY);
                entity_Currency.setId(String.valueOf(System.currentTimeMillis()));
                simpleRichEditor.insertSpecialStr(entity_Currency);
                break;

            default:
                break;
        }
    }
}
