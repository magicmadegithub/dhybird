package com.dahai.dhybird.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.dahai.dhybird.R;

/**
 * describe：文件阅读类
 */
public class FileDisplayActivity extends Activity {

    private ImageView ivBack;
    private FileReaderView fileReaderView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_display);
        init();
    }


    public void init() {
        ivBack = findViewById(R.id.img_go_back);
        fileReaderView = findViewById(R.id.documentReaderView);
        fileReaderView.show(getIntent().getStringExtra("path"));

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (fileReaderView != null) {
            fileReaderView.stop();
        }
    }


    public static void show(Context context, String url) {
        Intent intent = new Intent(context, FileDisplayActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("path", url);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }
}
