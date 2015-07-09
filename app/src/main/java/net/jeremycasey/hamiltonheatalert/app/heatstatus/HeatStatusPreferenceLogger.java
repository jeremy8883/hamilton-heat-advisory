package net.jeremycasey.hamiltonheatalert.app.heatstatus;

import android.content.Context;

import net.jeremycasey.hamiltonheatalert.app.utils.PreferenceUtil;
import net.jeremycasey.hamiltonheatalert.heatstatus.LoggedHeatStatus;
import net.jeremycasey.hamiltonheatalert.heatstatus.HeatStatusLogger;

public class HeatStatusPreferenceLogger implements HeatStatusLogger {
    private Context mContext;
    private static final String PREFERENCE_LAST_FETCHED_HEAT_STATUS = "LastFetchedHeatStatus";
    private static final String PREFERENCE_LAST_FETCHED_AND_NOTIFIED_HEAT_STATUS = "LastFetchedAndNotifiedHeatStatus";

    public HeatStatusPreferenceLogger(Context context) {
        mContext = context;
    }

    @Override
    public void setMostRecentStatus(LoggedHeatStatus fetchedHeatStatus) {
        PreferenceUtil.put(mContext, PREFERENCE_LAST_FETCHED_HEAT_STATUS, fetchedHeatStatus);
    }

    @Override
    public void setLastNotifiedStatus(LoggedHeatStatus fetchedHeatStatus) {
        PreferenceUtil.put(mContext, PREFERENCE_LAST_FETCHED_AND_NOTIFIED_HEAT_STATUS, fetchedHeatStatus);
    }

    @Override
    public LoggedHeatStatus getMostRecentStatus() {
        return PreferenceUtil.getObject(mContext, PREFERENCE_LAST_FETCHED_HEAT_STATUS, null, LoggedHeatStatus.class);
    }

    @Override
    public LoggedHeatStatus getLastNotifiedStatus() {
        return PreferenceUtil.getObject(mContext, PREFERENCE_LAST_FETCHED_AND_NOTIFIED_HEAT_STATUS, null, LoggedHeatStatus.class);
    }
}
