package net.jeremycasey.hamiltonheatalert.heatstatus;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class XmlToHeatStatusConverter {
    private String mXml;
    public XmlToHeatStatusConverter(String xml) {
        mXml = xml;
    }

    public HeatStatus run() throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;

        builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new InputSource(new StringReader(mXml)));

        NodeList itemNodes = getFirstInstanceOf(doc.getChildNodes(), "item").getChildNodes();
        NodeList imageNodes = getFirstInstanceOf(doc.getChildNodes(), "image").getChildNodes();

        HeatStatus ha = new HeatStatus();
        ha.setStageText(getValueOf(itemNodes, "stage"));
        ha.setStage(getStageIntFromStageText(ha.getStageText()));
        ha.setImageUrl(getValueOf(imageNodes, "url"));
        ha.setLastBuildDate(parseDate(getValueOf(doc.getChildNodes(), "lastBuildDate")).getMillis());
        ha.setDescription(getValueOf(itemNodes, "description"));

        return ha;
    }

    private static Node getFirstInstanceOf(NodeList nodes, String nodeName) {
        for (int i = 0; i < nodes.getLength(); i++) {
            if (nodes.item(i).getNodeType() == Node.ELEMENT_NODE) {
                if (nodes.item(i).getNodeName().equalsIgnoreCase(nodeName)) {
                    return nodes.item(i);
                }
                else {
                    Node node = getFirstInstanceOf(nodes.item(i).getChildNodes(), nodeName);
                    if (node != null) return node;
                }
            }
        }
        return null;
    }

    private static String getValueOf(NodeList nodes, String nodeName) {
        Node node = getFirstInstanceOf(nodes, nodeName);
        String value = node.getChildNodes().item(0).getNodeValue();
        return value;
    }

    private static int getStageIntFromStageText(String text) {
        return Integer.parseInt(text.substring(text.lastIndexOf(" ") + 1));
    }

    private static DateTime parseDate(String date) {
        return DateTime.parse(date, DateTimeFormat.forPattern("yyyy-MM-dd h:mm:ss a"));
    }
}
