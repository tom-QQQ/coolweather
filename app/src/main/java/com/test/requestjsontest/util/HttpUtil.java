package com.test.requestjsontest.util;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by 瞿康宁 on 2017/2/23.
 */

public class HttpUtil {

    public static void sendHttpRequest(final String address, final okhttp3.Callback callBack) {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(address)
                .build();
        okHttpClient.newCall(request).enqueue(callBack);
    }
}
