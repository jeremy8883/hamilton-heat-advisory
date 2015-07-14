package net.jeremycasey.hamiltonheatalert.heatstatus;

import net.jeremycasey.hamiltonheatalert.datetime.TimeProvider;

import org.joda.time.DateTime;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ServerHeatStatusIsImportantCheckerTest {
    @Test
    public void testShouldNotifyIfImportanceChange() {
        HeatStatus lastHeatStatus = newHeatStatus(1, newDateTime(1, 12));
        assertTrue(new ServerHeatStatusIsImportantChecker(2, newTimeProvider(1, 13))
                .shouldNotify(lastHeatStatus, lastHeatStatus));
    }

    @Test
    public void testShouldNotifyIfLongerThanADay() {
        HeatStatus lastHeatStatus = newHeatStatus(2, newDateTime(2, 12));
        HeatStatus lastNotifiedHeatStatus = newHeatStatus(2, newDateTime(1, 12));
        assertTrue(new ServerHeatStatusIsImportantChecker(2, newTimeProvider(2, 13))
                .shouldNotify(lastHeatStatus, lastNotifiedHeatStatus));
    }

    @Test
    public void testShouldNotifyIfFirstImportantStatus() {
        assertTrue(new ServerHeatStatusIsImportantChecker(1, newTimeProvider(1, 0))
                .shouldNotify(null, null));
    }

    @Test
    public void testShouldNotNotifyIfNotSerious() {
        assertFalse(new ServerHeatStatusIsImportantChecker(0, newTimeProvider(1, 0))
                .shouldNotify(null, null));
    }

    @Test
    public void testShouldNotNotifyIfSameStatusAsTheHeatStatusJustRecentlyFetched() {
        HeatStatus lastHeatStatus = newHeatStatus(1, newDateTime(1, 15));
        assertFalse(new ServerHeatStatusIsImportantChecker(1, newTimeProvider(1, 16))
                .shouldNotify(lastHeatStatus, lastHeatStatus));
    }

    @Test
    public void testShouldPrioritizeMorningAlerts() {
        HeatStatus lastHeatStatus = newHeatStatus(1, newDateTime(2, 5));
        HeatStatus lastNotifiedHeatStatus = newHeatStatus(1, newDateTime(1, 10));
        assertTrue(new ServerHeatStatusIsImportantChecker(1, newTimeProvider(2, 6))
                .shouldNotify(lastHeatStatus, lastNotifiedHeatStatus));
    }

    @Test
    public void testDoNotPrioritizeMorningAlertsIfNotificationWasRecent() {
        HeatStatus lastHeatStatus = newHeatStatus(1, newDateTime(2, 5));
        HeatStatus lastNotifiedHeatStatus = newHeatStatus(1, newDateTime(2, 2));
        assertFalse(new ServerHeatStatusIsImportantChecker(1, newTimeProvider(2, 6))
                .shouldNotify(lastHeatStatus, lastNotifiedHeatStatus));
    }

    private HeatStatus newHeatStatus(int stage, DateTime dateTime) {
        HeatStatus lastNotifiedHeatStatus = new HeatStatus();
        lastNotifiedHeatStatus.setStage(stage);
        lastNotifiedHeatStatus.setFetchDate(dateTime.getMillis());
        return lastNotifiedHeatStatus;
    }

    private DateTime newDateTime(int day, int hour) {
        return new DateTime(2015, 4, day, hour, 0);
    }

    private TimeProvider newTimeProvider(final int day, final int hour) {
        return new TimeProvider() {
            @Override
            public DateTime now() {
                return new DateTime(2015, 4, day, hour, 0);
            }
        };
    }
}
