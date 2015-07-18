package net.jeremycasey.hamiltonheatalert.heatstatus;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.junit.Test;
import org.xml.sax.SAXException;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import static junit.framework.Assert.assertEquals;

public class XmlToHeatStatusConverterTest {
    @Test
    public void testAlertXmlIsConverted() throws ParserConfigurationException, IOException, SAXException {
        String xml = "<?xml version='1.0' encoding='utf-8' ?>\n" +
                "\t\t<rss version='2.0'> \n" +
                "\t\t    <channel> \n" +
                "\t\t        <title>City of Hamilton - Heat Events</title> \n" +
                "\t\t        <description>City of Hamilton Heat Event</description> \n" +
                "\t\t        <link>http://www.hamilton.ca/heat</link> \n" +
                "\t\t\t\t<lastBuildDate>2015-05-19 2:52:42 PM</lastBuildDate>\n" +
                "\t\t\t\t<image><title>Current Heat Event Status for City of Hamilton</title><url>http://old.hamilton.ca/databases/phcs/heatalert/current1.jpg</url><link>http://www.hamilton.ca/heat</link></image>\n" +
                "\t\t        <item> \n" +
                "\t\t\t\t\t   <title>Current Heat Event Status: Monitoring - Stage 0</title>\n" +
                "\t\t\t\t\t   <stage>Monitoring - Stage 0</stage>\n" +
                "            <description>Weather forecasts are being monitored for conditions that can be hazardous to health. </description>\n" +
                "            <link>http://www.hamilton.ca/heat</link>\n" +
                "        </item>\n" +
                "    </channel>\n" +
                "\t</rss> ";
        HeatStatus fetchedHeatStatus = new XmlToHeatStatusConverter(xml).run();

        HeatStatus heatStatus = new HeatStatus();
        heatStatus.setStageText("Monitoring - Stage 0");
        heatStatus.setStage(0);
        heatStatus.setImageUrl("http://old.hamilton.ca/databases/phcs/heatalert/current1.jpg");
        heatStatus.setLastBuildDate(new DateTime(2015, 5, 19, 14, 52, 42).getMillis()); //TODO: what time zome is this?
        heatStatus.setFetchDate(fetchedHeatStatus.getFetchDate()); //We won't test the date
        heatStatus.setDescription("Weather forecasts are being monitored for conditions that can be hazardous to health. ");

        hsAssertEquals(heatStatus, fetchedHeatStatus);
    }

    @Test
    public void TestLevel2Converted() throws IOException, SAXException, ParserConfigurationException {
        String xml = "<?xml version='1.0' encoding='utf-8' ?>\n" +
                "\t\t<rss version='2.0'> \n" +
                "\t\t    <channel> \n" +
                "\t\t        <title>City of Hamilton - Heat Events</title> \n" +
                "\t\t        <description>City of Hamilton Heat Event</description> \n" +
                "\t\t        <link>http://www.hamilton.ca/heat</link> \n" +
                "\t\t\t\t<lastBuildDate>2015-07-17 4:18:39 PM</lastBuildDate>\n" +
                "\t\t\t\t<image><title>Current Heat Event Status for City of Hamilton</title><url>http://old.hamilton.ca/databases/phcs/heatalert/current1.jpg</url><link>http://www.hamilton.ca/heat</link></image>\n" +
                "\t\t        <item> \n" +
                "\t\t\t\t\t   <title>Current Heat Event Status: Heat Warning - Stage 2</title>\n" +
                "\t\t\t\t\t   <stage>Heat Warning - Stage 2</stage>\n" +
                "            <description>2 + days with 40 or greater humidex</description>\n" +
                "            <link>http://www.hamilton.ca/heat</link>\n" +
                "        </item>\n" +
                "    </channel>\n" +
                "\t</rss> \n";
        HeatStatus fetchedHeatStatus = new XmlToHeatStatusConverter(xml).run();

        HeatStatus heatStatus = new HeatStatus();
        heatStatus.setStageText("Heat Warning - Stage 2");
        heatStatus.setStage(2);
        heatStatus.setImageUrl("http://old.hamilton.ca/databases/phcs/heatalert/current1.jpg");
        heatStatus.setLastBuildDate(new DateTime(2015, 7, 17, 16, 18, 39).getMillis());
        heatStatus.setFetchDate(fetchedHeatStatus.getFetchDate()); //We won't test the date
        heatStatus.setDescription("2 + days with 40 or greater humidex");

        hsAssertEquals(heatStatus, fetchedHeatStatus);
    }

    private void hsAssertEquals(HeatStatus heatStatus, HeatStatus fetchedHeatStatus) {
        assertEquals(heatStatus.getStage(), fetchedHeatStatus.getStage());
        assertEquals(heatStatus.getStageText(), fetchedHeatStatus.getStageText());
        assertEquals(heatStatus.getImageUrl(), fetchedHeatStatus.getImageUrl());
        assertEquals(heatStatus.getLastBuildDate(), fetchedHeatStatus.getLastBuildDate());
        assertEquals(heatStatus.getDescription(), fetchedHeatStatus.getDescription());
    }
}
