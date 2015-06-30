//package net.jeremycasey.hamiltonheatalert.app.heatadvisory;
//
//import org.joda.time.DateTime;
//import org.junit.Test;
//import org.xml.sax.SAXException;
//
//import java.io.IOException;
//
//import javax.xml.parsers.ParserConfigurationException;
//
//import static junit.framework.Assert.assertEquals;
//
//public class XmlToHeatAdvisoryConverterTest {
//    @Test
//    public void testAdvisoryXmlIsConverted() throws ParserConfigurationException, IOException, SAXException {
//        String xml = "<?xml version='1.0' encoding='utf-8' ?>\n" +
//                "\t\t<rss version='2.0'> \n" +
//                "\t\t    <channel> \n" +
//                "\t\t        <title>City of Hamilton - Heat Events</title> \n" +
//                "\t\t        <description>City of Hamilton Heat Event</description> \n" +
//                "\t\t        <link>http://www.hamilton.ca/heat</link> \n" +
//                "\t\t\t\t<lastBuildDate>5/19/2015 2:52:42 PM</lastBuildDate>\n" +
//                "\t\t\t\t<image><title>Current Heat Event Status for City of Hamilton</title><url>http://old.hamilton.ca/databases/phcs/heatalert/current1.jpg</url><link>http://www.hamilton.ca/heat</link></image>\n" +
//                "\t\t        <item> \n" +
//                "\t\t\t\t\t   <title>Current Heat Event Status: Monitoring - Stage 0</title>\n" +
//                "\t\t\t\t\t   <stage>Monitoring - Stage 0</stage>\n" +
//                "            <description>Weather forecasts are being monitored for conditions that can be hazardous to health. </description>\n" +
//                "            <link>http://www.hamilton.ca/heat</link>\n" +
//                "        </item>\n" +
//                "    </channel>\n" +
//                "\t</rss> ";
//        HeatStatus ha = new HeatStatus();
//        ha.setStageText("Monitoring - Stage 0");
//        ha.setStage(1);
//        ha.setImageUrl("http://old.hamilton.ca/databases/phcs/heatalert/current1.jpg");
//        ha.setLastBuildDate(new DateTime(2015, 5, 19, 2, 52, 42)); //TODO: what time zome is this?
//
//        assertEquals(new HeatStatus(), new XmlToHeatStatusConverter(xml).run());
//    }
//
////    @Test
////    public void testAdvisoryRequiresAlert() {
////
////    }
//}
