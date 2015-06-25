package net.jeremycasey.hamiltonheatalert.heatadvisory;

import android.content.Context;
import android.os.AsyncTask;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import net.jeremycasey.hamiltonheatalert.utils.AsyncTaskResult;
import net.jeremycasey.hamiltonheatalert.utils.WebRequestAsync;

import org.xml.sax.SAXException;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

public class HeatAdvisoryFetcher {

    private static final String RSS_URL = "http://old.hamilton.ca/databases/phcs/heatalert/heatevent.xml";

    public interface FetchListener {
        void onFetchComplete(HeatAdvisory heatAdvisory);
        void onFetchError(Exception ex);
    }

    private FetchListener mFetchListener = null;
    private WebRequestAsync mWebRequestAsync = null;

    public HeatAdvisoryFetcher(FetchListener fetchListener) {
        mFetchListener = fetchListener;
    }

    public void run() {
        cancel();
        mWebRequestAsync = new WebRequestAsync(new WebRequestAsync.DownloadListener() {
            @Override
            public void onDownloadComplete(String xml) {
                try {
                    mFetchListener.onFetchComplete(convertXmlToHeatAdvisory(xml));
                } catch (Exception ex) {
                    mFetchListener.onFetchError(ex);
                }
            }

            @Override
            public void onDownloadError(Exception ex) {
                mFetchListener.onFetchError(ex);
            }
        });
        mWebRequestAsync.execute(RSS_URL);
    }

    public void cancel() {
        if (mWebRequestAsync != null) {
            mWebRequestAsync.cancel(true);
            mWebRequestAsync = null;
        }
    }

    private HeatAdvisory convertXmlToHeatAdvisory(String xml) throws IOException, SAXException, ParserConfigurationException {
        return new XmlToHeatAdvisoryConverter(xml).run();
    }
}
