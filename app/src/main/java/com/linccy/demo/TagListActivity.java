package com.linccy.demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by btc_eth on 2018/8/27.
 */

public class TagListActivity extends AppCompatActivity implements com.linccy.demo.SimpleAdapter.OnClickItemListener {

    public static final String ENTITY = "ENTITY";


    public static void startActivityForResult(Activity activity, int requestCode) {
        Intent intent = new Intent(activity, TagListActivity.class);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_list);
        RecyclerView listView = findViewById(R.id.ammend_list);
        listView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        listView.setAdapter(new com.linccy.demo.SimpleAdapter(MockData.userList, this));
    }

    @Override
    public void onClickItem(Entity entity) {
        if(entity != null) {
            Intent intent = new Intent();
            intent.putExtra(ENTITY, entity);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

}
