package net.jeremycasey.hamiltonheatalert.app.heatstatus;

import android.os.AsyncTask;

import net.jeremycasey.hamiltonheatalert.app.utils.AsyncTaskResult;
import net.jeremycasey.hamiltonheatalert.app.utils.WebRequestAsync;
import net.jeremycasey.hamiltonheatalert.heatstatus.HeatStatus;
import net.jeremycasey.hamiltonheatalert.heatstatus.HeatStatusFetcher;

public class HeatStatusFetcherAsync extends AsyncTask<String, String, AsyncTaskResult<HeatStatus>> {

    public interface FetchListener {
        void onFetchComplete(HeatStatus heatStatus);
        void onFetchError(Exception ex);
    }

    private FetchListener mFetchListener = null;
    private WebRequestAsync mWebRequestAsync = null;
    private HeatStatusFetcher mHeatStatusFetcher = null;

    public HeatStatusFetcherAsync(FetchListener fetchListener) {
        mFetchListener = fetchListener;
    }

    @Override
    protected AsyncTaskResult<HeatStatus> doInBackground(String... params) {
        try {
            mHeatStatusFetcher = new HeatStatusFetcher();
            HeatStatus heatStatus = mHeatStatusFetcher.run();
            return new AsyncTaskResult<>(heatStatus);
        } catch (Exception ex) {
            return new AsyncTaskResult<>(ex);
        }
    }

    protected void onPostExecute(AsyncTaskResult<HeatStatus> result) {
        if (result.getError() != null) {
            mFetchListener.onFetchError(result.getError());
        }
        else {
            mFetchListener.onFetchComplete(result.getResult());
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        cleanUp();
    }

    private void cleanUp() {
        mFetchListener = null;
        if (mHeatStatusFetcher != null) {
            mHeatStatusFetcher.cancel();
            mHeatStatusFetcher = null;
        }
    }
}
