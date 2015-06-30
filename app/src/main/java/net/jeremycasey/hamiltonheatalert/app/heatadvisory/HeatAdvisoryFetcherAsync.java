package net.jeremycasey.hamiltonheatalert.app.heatadvisory;

import android.os.AsyncTask;

import net.jeremycasey.hamiltonheatalert.app.utils.AsyncTaskResult;
import net.jeremycasey.hamiltonheatalert.app.utils.WebRequestAsync;
import net.jeremycasey.hamiltonheatalert.heatadvisory.HeatAdvisory;
import net.jeremycasey.hamiltonheatalert.heatadvisory.HeatAdvisoryFetcher;
import net.jeremycasey.hamiltonheatalert.heatadvisory.XmlToHeatAdvisoryConverter;

import org.xml.sax.SAXException;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

public class HeatAdvisoryFetcherAsync extends AsyncTask<String, String, AsyncTaskResult<HeatAdvisory>> {

    public interface FetchListener {
        void onFetchComplete(HeatAdvisory heatAdvisory);
        void onFetchError(Exception ex);
    }

    private FetchListener mFetchListener = null;
    private WebRequestAsync mWebRequestAsync = null;
    private HeatAdvisoryFetcher mHeatAdvisoryFetcher = null;

    public HeatAdvisoryFetcherAsync(FetchListener fetchListener) {
        mFetchListener = fetchListener;
    }

    @Override
    protected AsyncTaskResult<HeatAdvisory> doInBackground(String... params) {
        try {
            mHeatAdvisoryFetcher = new HeatAdvisoryFetcher();
            HeatAdvisory heatAdvisory = mHeatAdvisoryFetcher.run();
            return new AsyncTaskResult<>(heatAdvisory);
        } catch (Exception ex) {
            return new AsyncTaskResult<>(ex);
        }
    }

    protected void onPostExecute(AsyncTaskResult<HeatAdvisory> result) {
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
        if (mHeatAdvisoryFetcher != null) {
            mHeatAdvisoryFetcher.cancel();
            mHeatAdvisoryFetcher = null;
        }
    }
}
