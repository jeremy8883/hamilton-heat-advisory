package net.jeremycasey.hamiltonheatalert.app.heatstatus;

import net.jeremycasey.hamiltonheatalert.datetime.TimeProvider;
import net.jeremycasey.hamiltonheatalert.heatstatus.HeatStatus;
import net.jeremycasey.hamiltonheatalert.heatstatus.HeatStatusIsImportantChecker;
import net.jeremycasey.hamiltonheatalert.heatstatus.HeatStatusLogger;

import org.joda.time.DateTime;

public class ClientHeatStatusIsImportantChecker extends HeatStatusIsImportantChecker {
    public ClientHeatStatusIsImportantChecker(int stage, TimeProvider timeProvider) {
        super(stage, timeProvider);
    }

    @Override
    public boolean shouldNotify(HeatStatus lastFetchedStatus, HeatStatus lastNotifiedHeatStatus) {
        return isImportant() && (
                lastFetchedStatus == null ||
                        isDifferentFromTheLastUpdate(lastFetchedStatus) ||
                        isMoreThan5HoursOld(lastFetchedStatus)
        );
    }

    private boolean isMoreThan5HoursOld(HeatStatus lastFetchedStatus) {
        return lastFetchedStatus.getFetchDate() < mTimeProvider.now().minusHours(5).getMillis();
    }
}
