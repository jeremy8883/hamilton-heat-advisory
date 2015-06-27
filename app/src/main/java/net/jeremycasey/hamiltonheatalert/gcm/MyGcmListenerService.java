package net.jeremycasey.hamiltonheatalert.gcm;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;

import net.jeremycasey.hamiltonheatalert.R;
import net.jeremycasey.hamiltonheatalert.heatadvisory.AdvisoryNotification;
import net.jeremycasey.hamiltonheatalert.heatadvisory.ErrorNotification;
import net.jeremycasey.hamiltonheatalert.heatadvisory.HeatAdvisory;
import net.jeremycasey.hamiltonheatalert.heatadvisory.XmlToHeatAdvisoryConverter;

//This service is started automatically
public class MyGcmListenerService extends com.google.android.gms.gcm.GcmListenerService {
    private static final String TAG = "MyGcmListenerService";

    public static final String NEW_ALERT_RECEIVED = "newAlertReceived";

    @Override
    public void onMessageReceived(String from, Bundle data) {
        String xml = data.getString("message");
//        Log.d(TAG, "From: " + from);
//        Log.d(TAG, "Message: " + message);

        try {
            HeatAdvisory heatAdvisory = new XmlToHeatAdvisoryConverter(xml).run();
            AdvisoryNotification advisoryNotification = new AdvisoryNotification(heatAdvisory, this);
            if (advisoryNotification.shouldShowNotification()) {
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
