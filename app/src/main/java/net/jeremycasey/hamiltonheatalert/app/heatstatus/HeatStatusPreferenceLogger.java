package net.jeremycasey.hamiltonheatalert.app.heatstatus;

import android.content.Context;

import net.jeremycasey.hamiltonheatalert.app.utils.PreferenceUtil;
import net.jeremycasey.hamiltonheatalert.heatstatus.HeatStatus;
import net.jeremycasey.hamiltonheatalert.heatstatus.HeatStatusLogger;

public class HeatStatusPreferenceLogger implements HeatStatusLogger {
    private Context mContext;
    private static final String PREFERENCE_LAST_FETCHED_HEAT_STATUS = "LastFetchedHeatStatus";
    private static final String PREFERENCE_LAST_FETCHED_AND_NOTIFIED_HEAT_STATUS = "LastFetchedAndNotifiedHeatStatus";

    public HeatStatusPreferenceLogger(Context context) {
        mContext = context;
    }

    @Override
    public void setMostRecentStatus(HeatStatus fetchedHeatStatus) {
        PreferenceUtil.put(mContext, PREFERENCE_LAST_FETCHED_HEAT_STATUS, fetchedHeatStatus);
    }

    @Override
    public void setLastNotifiedStatus(HeatStatus fetchedHeatStatus) {
        PreferenceUtil.put(mContext, PREFERENCE_LAST_FETCHED_AND_NOTIFIED_HEAT_STATUS, fetchedHeatStatus);
    }

    @Override
    public HeatStatus getMostRecentStatus() {
        return PreferenceUtil.getObject(mContext, PREFERENCE_LAST_FETCHED_HEAT_STATUS, null, HeatStatus.class);
    }

    @Override
    public HeatStatus getLastNotifiedStatus() {
        return PreferenceUtil.getObject(mContext, PREFERENCE_LAST_FETCHED_AND_NOTIFIED_HEAT_STATUS, null, HeatStatus.class);
    }
}
