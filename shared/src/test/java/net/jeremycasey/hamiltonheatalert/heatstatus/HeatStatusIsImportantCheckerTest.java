package net.jeremycasey.hamiltonheatalert.heatstatus;

import org.joda.time.DateTime;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class HeatStatusIsImportantCheckerTest {
    @Test
    public void testShouldNotifyIfImportanceChange() {
        LastFetchedHeatStatus lastHeatStatus = new LastFetchedHeatStatus(1, new DateTime().minusHours(15));
        assertTrue(new HeatStatusIsImportantChecker(2).shouldNotify(lastHeatStatus));
    }

    @Test
    public void testShouldNotifyIfLongerThan18Hours() {
        LastFetchedHeatStatus lastHeatStatus = new LastFetchedHeatStatus(2, new DateTime().minusHours(19));
        assertTrue(new HeatStatusIsImportantChecker(2).shouldNotify(lastHeatStatus));
    }

    @Test
    public void testShouldNotifyIfFirstImportantStatus() {
        assertTrue(new HeatStatusIsImportantChecker(1).shouldNotify(null));
    }

    @Test
    public void testShouldNotNotifyIfNotSerious() {
        assertFalse(new HeatStatusIsImportantChecker(0).shouldNotify(null));
    }

    @Test
    public void testShouldNotNotifyIfJustRecentlyNotified() {
        LastFetchedHeatStatus lastHeatStatus = new LastFetchedHeatStatus(1, new DateTime().minusHours(15));
        assertFalse(new HeatStatusIsImportantChecker(1).shouldNotify(lastHeatStatus));
    }
}
