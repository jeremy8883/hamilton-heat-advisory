package net.jeremycasey.hamiltonheatalert.app.heatstatus;

import android.content.Context;

import net.jeremycasey.hamiltonheatalert.app.notifications.HeatStatusNotification;
import net.jeremycasey.hamiltonheatalert.heatstatus.HeatStatus;
import net.jeremycasey.hamiltonheatalert.heatstatus.HeatStatusIsImportantChecker;
import net.jeremycasey.hamiltonheatalert.heatstatus.LoggedHeatStatus;

public class HeatStatusNotifier {
    private Context mContext;

    public HeatStatusNotifier(Context context) {
        mContext = context;
    }

    public void logAndNotifyIfRequiered(HeatStatus heatStatus) {
        HeatStatusPreferenceLogger logger = new HeatStatusPreferenceLogger(mContext);
        LoggedHeatStatus lastFetchedStatus = logger.getLastNotifiedStatus();
        LoggedHeatStatus newFetchedStatus = new LoggedHeatStatus(heatStatus);
        if (new HeatStatusIsImportantChecker(heatStatus.getStage()).shouldNotify(lastFetchedStatus)) {
            HeatStatusNotification heatStatusNotification = new HeatStatusNotification(heatStatus, mContext);
            heatStatusNotification.showNotification();
            logger.setLastNotifiedStatus(newFetchedStatus);
        }
        logger.setMostRecentStatus(newFetchedStatus);
    }
}
