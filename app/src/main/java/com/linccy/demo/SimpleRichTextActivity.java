package com.linccy.demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.linccy.richtext.RichEditText;
import com.linccy.richtext.RichTextView;
import com.linccy.richtext.util.MatchEntity;

/**
 * SimpleRichText 实例
 */

public class SimpleRichTextActivity extends AppCompatActivity implements View.OnClickListener {
    public static final int REQUEST_AT_USER = 0x11;
    public static final int REQUEST_ADD_TOPIC = 0x12;
    public static final int REQUEST_ADD_LINK = 0x13;
    public static final String TYPE_USER = "user";

    private RichEditText richEditText;

    private RichTextView tvPreview;
    private TextView tvResult;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_rich_text);

        richEditText = findViewById(R.id.edit_content);

        tvPreview = findViewById(R.id.tv_result_preview);
        tvResult = findViewById(R.id.tv_result_content);

        DemoTools.initRichTextView(tvPreview);

        findViewById(R.id.at_user).setOnClickListener(this);
        findViewById(R.id.add_tag1).setOnClickListener(this);
        findViewById(R.id.add_tag2).setOnClickListener(this);
        findViewById(R.id.confirm).setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.at_user:
//                richEditText.appendMention(new MatchEntity("1", "user", "Linccy", "@%s "));
                TagListActivity.startActivityForResult(this, REQUEST_AT_USER);
                break;

//            case R.id.add_tag1:
//                richEditText.insertSpecialStr(entityTopic);
//                break;
//
//            case R.id.add_tag2:
//                InsertEntity entity_Currency = new InsertEntity(TAG_CURRENCY, TAG_CURRENCY + "摩根大通指数" + TAG_CURRENCY);
//                entity_Currency.setId(String.valueOf(System.currentTimeMillis()));
//                richEditText.insertSpecialStr(entity_Currency);
//                break;

            case R.id.confirm:
                String content = richEditText.toRealResult();

                tvPreview.setText(content);
                tvResult.setText(content);

                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                }
                break;

            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Entity entity;
            switch (requestCode) {
                case SimpleRichTextActivity.REQUEST_AT_USER:
                    entity = (Entity) data.getSerializableExtra(TagListActivity.ENTITY);
                    richEditText.appendMention(entity.getName());
                    break;

                case SimpleRichTextActivity.REQUEST_ADD_TOPIC:
                    entity = (Entity) data.getSerializableExtra(TagListActivity.ENTITY);
                    richEditText.appendTopic(entity.getName());
                    break;

                default:
                    super.onActivityResult(requestCode, resultCode, data);
                    break;
            }
        }


    }
}
