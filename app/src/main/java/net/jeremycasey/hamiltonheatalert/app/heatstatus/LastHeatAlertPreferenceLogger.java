package net.jeremycasey.hamiltonheatalert.app.heatstatus;

import android.content.Context;

import net.jeremycasey.hamiltonheatalert.app.utils.PreferenceUtil;
import net.jeremycasey.hamiltonheatalert.heatstatus.LastFetchedHeatStatus;
import net.jeremycasey.hamiltonheatalert.heatstatus.LastFetchedHeatStatusLogger;

public class LastHeatAlertPreferenceLogger implements LastFetchedHeatStatusLogger {
    private Context mContext;
    private static final String PREFERENCE_LAST_FETCHED_HEAT_STATUS = "LastFetchedHeatStatus";
    private static final String PREFERENCE_LAST_FETCHED_AND_NOTIFIED_HEAT_STATUS = "LastFetchedAndNotifiedHeatStatus";

    public LastHeatAlertPreferenceLogger(Context context) {
        mContext = context;
    }

    @Override
    public void logFetchedStatus(LastFetchedHeatStatus fetchedHeatStatus) {
        PreferenceUtil.put(mContext, PREFERENCE_LAST_FETCHED_HEAT_STATUS, fetchedHeatStatus);
    }

    @Override
    public void logFetchedAndNotifiedStatus(LastFetchedHeatStatus fetchedHeatStatus) {
        PreferenceUtil.put(mContext, PREFERENCE_LAST_FETCHED_AND_NOTIFIED_HEAT_STATUS, fetchedHeatStatus);
    }

    @Override
    public LastFetchedHeatStatus getLastFetchedHeatStatus() {
        return PreferenceUtil.getObject(mContext, PREFERENCE_LAST_FETCHED_HEAT_STATUS, null, LastFetchedHeatStatus.class);
    }

    @Override
    public LastFetchedHeatStatus getLastFetchedAndNotifiedHeatStatus() {
        return PreferenceUtil.getObject(mContext, PREFERENCE_LAST_FETCHED_AND_NOTIFIED_HEAT_STATUS, null, LastFetchedHeatStatus.class);
    }
}
