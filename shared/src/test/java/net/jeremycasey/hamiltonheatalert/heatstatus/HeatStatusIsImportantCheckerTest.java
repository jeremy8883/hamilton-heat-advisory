package net.jeremycasey.hamiltonheatalert.heatstatus;

import org.joda.time.DateTime;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class HeatStatusIsImportantCheckerTest {
    @Test
    public void testShouldNotifyIfImportanceChange() {
        LoggedHeatStatus lastHeatStatus = new LoggedHeatStatus(1, new DateTime().minusHours(15));
        assertTrue(new HeatStatusIsImportantChecker(2).shouldNotify(lastHeatStatus));
    }

    @Test
    public void testShouldNotifyIfLongerThan18Hours() {
        LoggedHeatStatus lastHeatStatus = new LoggedHeatStatus(2, new DateTime().minusHours(19));
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
        LoggedHeatStatus lastHeatStatus = new LoggedHeatStatus(1, new DateTime().minusHours(15));
        assertFalse(new HeatStatusIsImportantChecker(1).shouldNotify(lastHeatStatus));
    }
}
