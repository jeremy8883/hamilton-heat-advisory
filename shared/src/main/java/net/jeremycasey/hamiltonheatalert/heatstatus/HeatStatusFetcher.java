package net.jeremycasey.hamiltonheatalert.heatstatus;

import net.jeremycasey.hamiltonheatalert.utils.WebRequest;
import org.xml.sax.SAXException;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;

import rx.Observable;
import rx.Subscriber;

public class HeatStatusFetcher {

    private static final String RSS_URL = "http://old.hamilton.ca/databases/phcs/heatalert/heatevent.xml";
    private WebRequest mWebRequest = null;

    public HeatStatusFetcher() {
    }

    public HeatStatus run() throws IOException, ParserConfigurationException, SAXException {
        mWebRequest = new WebRequest(RSS_URL);
        String xml = mWebRequest.run();
        return convertXmlToHeatAdvisory(xml);
    }

    public void cancel() {
        if (mWebRequest != null) {
            mWebRequest.cancel();
            mWebRequest = null;
        }
    }

    private HeatStatus convertXmlToHeatAdvisory(String xml) throws IOException, SAXException, ParserConfigurationException {
        return new XmlToHeatStatusConverter(xml).run();
    }

    public Observable<HeatStatus> toObservable() {
        return Observable.create(new Observable.OnSubscribe<HeatStatus>() {
            @Override
            public void call(Subscriber<? super HeatStatus> subscriber) {
                try {
                    HeatStatus heatStatus = run();
                    subscriber.onNext(heatStatus);
                    subscriber.onCompleted();
                } catch (Exception ex) {
                    subscriber.onError(ex);
                }
            }
        });
    }
}
