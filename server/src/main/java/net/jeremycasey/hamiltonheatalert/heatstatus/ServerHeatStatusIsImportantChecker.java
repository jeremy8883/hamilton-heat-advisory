package net.jeremycasey.hamiltonheatalert.heatstatus;

import net.jeremycasey.hamiltonheatalert.datetime.TimeProvider;

import org.joda.time.DateTime;

public class ServerHeatStatusIsImportantChecker extends HeatStatusIsImportantChecker {
    public ServerHeatStatusIsImportantChecker(int stage, TimeProvider timeProvider) {
        super(stage, timeProvider);
    }

    public boolean shouldNotify(HeatStatus lastFetchedStatus, HeatStatus lastNotifiedHeatStatus) {
        return isImportant() && (
                    lastFetchedStatus == null ||
                    isDifferentFromTheLastUpdate(lastFetchedStatus) ||
                    isMoreThanADayOld(lastNotifiedHeatStatus) ||
                    //Prioritize notifications in the early morning if possible
                    //A notification in the earlier morning is ok, as the user will see it when they wake up
                    isCommonWakeUpTime(mTimeProvider.now()) && !isMorningButBeforWakeUpTime(lastNotifiedHeatStatus)
                );
    }

    private boolean isMoreThanADayOld(HeatStatus lastNotifiedHeatStatus) {
        return lastNotifiedHeatStatus.getFetchDate() < mTimeProvider.now().minusDays(1).getMillis();
    }

    private boolean isCommonWakeUpTime(DateTime dateTime) {
        return dateTime.getHourOfDay() >= 6 && dateTime.getHourOfDay() < 9;
    }
    private boolean isMorningButBeforWakeUpTime(HeatStatus heatStatus) {
        int hourOfDay = new DateTime(heatStatus.getFetchDate()).getHourOfDay();
        return hourOfDay >= 0 && hourOfDay < 6;
    }
}
