package com.linccy.demo;

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

        tvPreview.setMovementMethod(LinkMovementMethod.getInstance());//必须设置否则clickSpan无效
        tvPreview.setTagClickListener(new RichTextView.OnTagClickListener() {
            @Override
            public void onClick(String type, String id, String content, String realStr) {
                Toast.makeText(SimpleRichTextActivity.this, "点击了" + content + " id=" + id + "\n" + realStr, Toast.LENGTH_LONG).show();
            }
        });

        findViewById(R.id.at_user).setOnClickListener(this);
        findViewById(R.id.add_tag1).setOnClickListener(this);
        findViewById(R.id.add_tag2).setOnClickListener(this);
        findViewById(R.id.confirm).setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.at_user:
                richEditText.appendMention(new MatchEntity("1", "user", "Linccy", "@%s "));
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
}
