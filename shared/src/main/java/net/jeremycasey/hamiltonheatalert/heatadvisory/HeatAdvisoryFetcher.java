package net.jeremycasey.hamiltonheatalert.heatadvisory;

import net.jeremycasey.hamiltonheatalert.utils.WebRequest;
import org.xml.sax.SAXException;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;

public class HeatAdvisoryFetcher {

    private static final String RSS_URL = "http://old.hamilton.ca/databases/phcs/heatalert/heatevent.xml";
    private WebRequest mWebRequest = null;

    public HeatAdvisoryFetcher() {
    }

    public HeatAdvisory run() throws IOException, ParserConfigurationException, SAXException {
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

    private HeatAdvisory convertXmlToHeatAdvisory(String xml) throws IOException, SAXException, ParserConfigurationException {
        return new XmlToHeatAdvisoryConverter(xml).run();
    }
}
