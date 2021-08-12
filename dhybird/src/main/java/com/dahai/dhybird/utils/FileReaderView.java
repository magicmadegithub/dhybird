package com.dahai.dhybird.utils;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.dahai.dhybird.DHybird;
import com.tencent.smtt.sdk.TbsReaderView;

import java.io.File;

/**
 * 文件阅读器  文件打开核心类
 */
public class FileReaderView extends FrameLayout implements TbsReaderView.ReaderCallback {

    private TbsReaderView mTbsReaderView;
    private Context context;

    public FileReaderView(Context context) {
        this(context, null, 0);
    }

    public FileReaderView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FileReaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mTbsReaderView = getTbsReaderView(context);
        this.addView(mTbsReaderView, new LinearLayout.LayoutParams(-1, -1));
        this.context = context;
    }


    private TbsReaderView getTbsReaderView(Context context) {
        return new TbsReaderView(context, this);
    }

    /**
     * 初始化完布局调用此方法浏览文件
     *
     * @param filePath 文件路径
     */
    public void show(String filePath) {
        if (!TextUtils.isEmpty(filePath)) {
            //增加下面一句解决没有TbsReaderTemp文件夹存在导致加载文件失败
            String tbsReaderTemp = Environment.getExternalStorageDirectory() + "/TbsReaderTemp";
            File bsReaderTempFile = new File(tbsReaderTemp);
            if (!bsReaderTempFile.exists()) {
                Log.d(DHybird.TAG, "准备创建/TbsReaderTemp！！");
                boolean mkdir = bsReaderTempFile.mkdir();
                if (!mkdir) {
                    Log.d(DHybird.TAG, "创建/TbsReaderTemp失败！！！！！");
                }
            }
            //加载文件
            Bundle localBundle = new Bundle();
            localBundle.putString("filePath", filePath);
            localBundle.putString("tempPath", Environment.getExternalStorageDirectory() + "/" + "TbsReaderTemp");
            if (this.mTbsReaderView == null) {
                this.mTbsReaderView = getTbsReaderView(context);
            }
            boolean bool = this.mTbsReaderView.preOpen(getFileType(filePath), false);
            if (bool) {
                this.mTbsReaderView.openFile(localBundle);
            }
        } else {
            Log.e(DHybird.TAG, "文件路径无效！");
        }
    }

    @Override
    public void onCallBackAction(Integer integer, Object o, Object o1) {
    }

    /**
     * 务必在onDestroy方法中调用此方法，否则第二次打开无法浏览
     */
    public void stop() {
        if (mTbsReaderView != null) {
            mTbsReaderView.onStop();
        }
    }

    /***
     * 获取文件类型
     */
    private String getFileType(String paramString) {
        String str = "";

        if (TextUtils.isEmpty(paramString)) {
            return str;
        }
        int i = paramString.lastIndexOf('.');
        if (i <= -1) {
            return str;
        }
        str = paramString.substring(i + 1);
        return str;
    }

}
