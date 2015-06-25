package net.jeremycasey.hamiltonheatalert.utils;

import android.content.Context;
import android.os.AsyncTask;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import net.jeremycasey.hamiltonheatalert.utils.AsyncTaskResult;

import java.io.IOException;

public class WebRequestAsync extends AsyncTask<String, String, AsyncTaskResult<String>> {

    public interface DownloadListener {
        void onDownloadComplete(String result);
        void onDownloadError(Exception ex);
    }

    private DownloadListener mDownloadListener = null;
    private OkHttpClient mClient = null;
    private Object mRequestTag = new Object();

    public WebRequestAsync(DownloadListener downloadListener) {
        mDownloadListener = downloadListener;
    }

    protected AsyncTaskResult<String> doInBackground(String... vars) {
        mClient = new OkHttpClient();

        Request request = new Request.Builder()
                .url(vars[0])
                .tag(mRequestTag)
                .build();

        Response response = null;
        try {
            response = mClient.newCall(request).execute();
            return new AsyncTaskResult<String>(response.body().string());
        } catch (IOException e) {
            return new AsyncTaskResult<String>(e);
        }
    }

    protected void onPostExecute(AsyncTaskResult<String> result) {
        if (result.getError() != null) {
            mDownloadListener.onDownloadError(result.getError());
        }
        else {
            mDownloadListener.onDownloadComplete(result.getResult());
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        cleanUp();
    }

    private void cleanUp() {
        mDownloadListener = null;
        mClient.cancel(mRequestTag);
        mClient = null;
    }

}
