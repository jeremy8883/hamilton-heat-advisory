package net.jeremycasey.hamiltonheatalert.app.heatstatus;

import net.jeremycasey.hamiltonheatalert.datetime.TimeProvider;
import net.jeremycasey.hamiltonheatalert.heatstatus.HeatStatus;

import org.joda.time.DateTime;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ClientHeatStatusIsImportantCheckerTest {
    @Test
    public void testShouldNotifyIfLongerThan5Hours() {
        HeatStatus lastHeatStatus = newHeatStatus(2, newDateTime(1, 6));
        assertTrue(new ClientHeatStatusIsImportantChecker(2, newTimeProvider(1, 12))
                .shouldNotify(lastHeatStatus, lastHeatStatus));
    }

    @Test
    public void testShouldNotNotifyIfSameStatusAsTheHeatStatusJustRecentlyFetched() {
        HeatStatus lastHeatStatus = newHeatStatus(1, newDateTime(1, 15));
        assertFalse(new ClientHeatStatusIsImportantChecker(1, newTimeProvider(1, 16))
                .shouldNotify(lastHeatStatus, lastHeatStatus));
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
