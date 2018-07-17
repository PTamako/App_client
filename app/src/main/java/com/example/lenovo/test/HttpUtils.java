package com.example.lenovo.test;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Callback;


/**
 * 封装的Http请求工具，底层依赖的是OkHttp3的框架
 */

public class HttpUtils {
    public static OkHttpClient client = new OkHttpClient();
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    public static final MediaType FORM_DATA = MediaType.parse("multipart/form-data");
    public final static String TAG = "";

    /**
     * 发送json请求的方法
     *
     * @param url      请求的地址
     * @param json     发送的JSON数据
     * @param callback 接受到数据之后的回调,在此方法中进行界面处理
     * @throws IOException
     */
    public static void post(final String url, final String json, final Callback callback) throws IOException {
        //启动线程提交数据，在Android中网络操作需要在异步线程中进行，不能够在主线程中进行
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    RequestBody body = RequestBody.create(JSON, json);
                    Request request = new Request.Builder()
                            .url(url)
                            .post(body)
                            .build();
                    client.newCall(request).enqueue(callback);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }


    public static void get(final String url, final Callback callback) throws IOException {
        //启动线程请求数据，在Android中网络操作需要在异步线程中进行，不能够在主线程中进行
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Request request = new Request.Builder()
                            .get()
                            .url(url)
                            .build();
                    client.newCall(request).enqueue(callback);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }
}
