package net.jeremycasey.hamiltonheatalert.heatstatus;

import org.joda.time.DateTime;

public class HeatStatusIsImportantChecker {
    private int mStage;

    public HeatStatusIsImportantChecker(int stage) {
        mStage = stage;
    }

    public boolean isImportant() {
        return mStage > 0;
    }

    public boolean shouldNotify(LoggedHeatStatus lastNotifiedHeatStatus) {
        return isImportant() && isDifferentFromTheLastUpdateInThePast18Hours(lastNotifiedHeatStatus);
    }

    private boolean isDifferentFromTheLastUpdateInThePast18Hours(LoggedHeatStatus lastNotifiedHeatStatus) {
        return lastNotifiedHeatStatus == null ||
                lastNotifiedHeatStatus.getDateTimeMillis() < new DateTime().minusHours(18).getMillis() ||
                lastNotifiedHeatStatus.getStage() != mStage;
    }
}
