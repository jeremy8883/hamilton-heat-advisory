package net.jeremycasey.hamiltonheatalert.utils;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

public class WebRequest {
    private String mUrl;
    private Object mRequestTag = new Object();
    private OkHttpClient mClient;

    public WebRequest(String url) {
        mUrl = url;
    }

    public String run() throws IOException {
        mClient = new OkHttpClient();

        Request request = new Request.Builder()
                .url(mUrl)
                .tag(mRequestTag)
                .build();

        Response response = mClient.newCall(request).execute();
        return response.body().string();
    }

    public void cancel() {
        if (mClient != null) {
            mClient.cancel(mRequestTag);
            mClient = null;
        }
    }
}
