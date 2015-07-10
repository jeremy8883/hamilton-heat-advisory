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

    public boolean shouldNotify(HeatStatus lastNotifiedHeatStatus) {
        return isImportant() && isDifferentFromTheLastUpdateInThePast18Hours(lastNotifiedHeatStatus);
    }

    private boolean isDifferentFromTheLastUpdateInThePast18Hours(HeatStatus lastNotifiedHeatStatus) {
        return lastNotifiedHeatStatus == null ||
                lastNotifiedHeatStatus.getFetchDate() < new DateTime().minusHours(18).getMillis() ||
                lastNotifiedHeatStatus.getStage() != mStage;
    }
}
