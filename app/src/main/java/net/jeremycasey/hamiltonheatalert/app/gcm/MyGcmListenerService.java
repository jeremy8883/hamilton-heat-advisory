package net.jeremycasey.hamiltonheatalert.app.gcm;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;

import net.jeremycasey.hamiltonheatalert.R;
import net.jeremycasey.hamiltonheatalert.app.notifications.AdvisoryNotification;
import net.jeremycasey.hamiltonheatalert.app.notifications.ErrorNotification;
import net.jeremycasey.hamiltonheatalert.heatadvisory.HeatAdvisory;
import net.jeremycasey.hamiltonheatalert.heatadvisory.HeatAdvisoryIsImportantChecker;
import com.google.gson.Gson;

//This service is started automatically
public class MyGcmListenerService extends com.google.android.gms.gcm.GcmListenerService {
    private static final String TAG = "MyGcmListenerService";

    public static final String NEW_ALERT_RECEIVED = "newAlertReceived";

    @Override
    public void onMessageReceived(String from, Bundle data) {
        try {
            String heatAdvisoryJson = data.getString("message");

            Gson gson = new Gson();
            HeatAdvisory heatAdvisory = gson.fromJson(heatAdvisoryJson, HeatAdvisory.class);

            AdvisoryNotification advisoryNotification = new AdvisoryNotification(heatAdvisory, this);
            if (new HeatAdvisoryIsImportantChecker(heatAdvisory).isImportant()) {
                advisoryNotification.showNotification();
            }

            Intent registrationComplete = new Intent(NEW_ALERT_RECEIVED);
            Bundle bundle = new Bundle();
            bundle.putSerializable("heatAdvisory", heatAdvisory);
            registrationComplete.putExtras(bundle);
            LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
        } catch (Exception e) {
            new ErrorNotification(getString(R.string.gcmMessageReceiveError), "", this).show(); //This should never happen
        }

    }
}
