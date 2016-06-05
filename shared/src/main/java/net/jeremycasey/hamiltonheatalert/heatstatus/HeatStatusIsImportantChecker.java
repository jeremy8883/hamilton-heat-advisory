package net.jeremycasey.hamiltonheatalert.heatstatus;

import net.jeremycasey.hamiltonheatalert.datetime.TimeProvider;

import org.joda.time.DateTime;

public abstract class HeatStatusIsImportantChecker {
    protected int mStage;
    protected TimeProvider mTimeProvider;

    public HeatStatusIsImportantChecker(int stage, TimeProvider timeProvider) {
        mStage = stage;
        mTimeProvider = timeProvider;
    }

    public boolean isImportant() {
        return mStage > 0;
    }

    public boolean shouldNotify(HeatStatusLogger logger) {
        HeatStatus lastFetchedStatus = logger.getMostRecentStatus();
        HeatStatus lastNotifiedHeatStatus = logger.getLastNotifiedStatus();
        return shouldNotify(lastFetchedStatus, lastNotifiedHeatStatus);
    }

    public abstract boolean shouldNotify(HeatStatus lastFetchedStatus, HeatStatus lastNotifiedHeatStatus);

    protected boolean isDifferentFromTheLastUpdate(HeatStatus lastFetchedStatus) {
        return lastFetchedStatus.getStage() != mStage;
    }
}
