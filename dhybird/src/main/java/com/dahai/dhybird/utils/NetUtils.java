package com.dahai.dhybird.utils;

import android.util.Log;

import com.dahai.dhybird.DHybird;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class NetUtils {

    private static NetUtils singletonLazy;

    private NetUtils() {

    }

    public static NetUtils getInstance() {
        synchronized (NetUtils.class) {
            if (null == singletonLazy) {
                singletonLazy = new NetUtils();
            }
        }
        return singletonLazy;
    }

    public void download(final String downloadUrl, final String savePath,
                         final DownloadListener downloadListener) {
        new Thread() {
            @Override
            public void run() {
                Log.e(DHybird.TAG, "开始下载");
                URL url;
                HttpURLConnection connection;
                try {
                    if (downloadListener != null) {
                        downloadListener.onStart();
                    }
                    //统一资源
                    url = new URL(downloadUrl);
                    //打开链接
                    connection = (HttpURLConnection) url.openConnection();
                    //设置链接超时
                    connection.setConnectTimeout(4000);
                    //设置connection打开链接资源
                    connection.connect();
                    //得到链接地址中的file路径
                    String urlFilePath = connection.getURL().getFile();
                    //得到url地址总文件名 file的separatorChar参数表示文件分离符
                    String fileName = urlFilePath.substring(urlFilePath.lastIndexOf(File.separatorChar) + 1);
                    //创建一个文件对象用于存储下载的文件 此次的getFilesDir()方法只有在继承至Context类的类中
                    // 可以直接调用其他类中必须通过Context对象才能调用，得到的是内部存储中此应用包名下的文件路径
                    //如果使用外部存储的话需要添加文件读写权限，5.0以上的系统需要动态获取权限 此处不在不做过多说明。
                    File file = new File(savePath);
                    //创建一个文件输出流
                    FileOutputStream outputStream = new FileOutputStream(file);
                    //得到链接的响应码 200为成功
                    int responseCode = connection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        //得到服务器响应的输入流
                        InputStream inputStream = connection.getInputStream();
                        //获取请求的内容总长度
                        int contentLength = connection.getContentLength();
                        //创建缓冲输入流对象，相对于inputStream效率要高一些
                        BufferedInputStream bfi = new BufferedInputStream(inputStream);
                        //此处的len表示每次循环读取的内容长度
                        int len;
                        //已经读取的总长度
                        int total = 0;
                        //bytes是用于存储每次读取出来的内容
                        byte[] bytes = new byte[1024];
                        while ((len = bfi.read(bytes)) != -1) {
                            //每次读取完了都将len累加在totle里
                            total += len;
                            //每次读取的都更新一次progressBar
                            if (downloadListener != null) {
                                int progress = (int) (total * 1.0 / contentLength * 100);
                                Log.e(DHybird.TAG, "下载进度："+progress);
                                downloadListener.onProgress(progress);
                            }
                            //通过文件输出流写入从服务器中读取的数据
                            outputStream.write(bytes, 0, len);
                        }
                        //关闭打开的流对象
                        outputStream.close();
                        inputStream.close();
                        bfi.close();
                        Log.e(DHybird.TAG, "下载完成：");
                        if (downloadListener != null) {
                            downloadListener.onFinish();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private DownloadListener downloadListener;

    public interface DownloadListener {
        void onStart();

        void onProgress(int progress);

        void onFinish();
    }
}
