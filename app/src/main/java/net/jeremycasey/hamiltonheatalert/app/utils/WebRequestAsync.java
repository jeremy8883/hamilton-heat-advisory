package net.jeremycasey.hamiltonheatalert.app.utils;

import android.os.AsyncTask;

import net.jeremycasey.hamiltonheatalert.utils.WebRequest;

import java.io.IOException;

public class WebRequestAsync extends AsyncTask<String, String, AsyncTaskResult<String>> {

    public interface DownloadListener {
        void onDownloadComplete(String result);
        void onDownloadError(Exception ex);
    }

    private DownloadListener mDownloadListener = null;
    private WebRequest mWebRequest = null;

    public WebRequestAsync(DownloadListener downloadListener) {
        mDownloadListener = downloadListener;
    }

    protected AsyncTaskResult<String> doInBackground(String... vars) {
        mWebRequest = new WebRequest(vars[0]);

        try {
            return new AsyncTaskResult<>(mWebRequest.run());
        } catch (IOException e) {
            return new AsyncTaskResult<>(e);
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
        if (mWebRequest != null) {
            mWebRequest.cancel();
            mWebRequest = null;
        }
    }

}
