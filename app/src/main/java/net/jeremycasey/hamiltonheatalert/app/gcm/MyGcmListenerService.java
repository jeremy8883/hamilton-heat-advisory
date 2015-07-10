package net.jeremycasey.hamiltonheatalert.app.gcm;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;

import com.google.gson.Gson;

import net.jeremycasey.hamiltonheatalert.R;
import net.jeremycasey.hamiltonheatalert.app.heatstatus.HeatStatusNotifier;
import net.jeremycasey.hamiltonheatalert.app.notifications.ErrorNotification;
import net.jeremycasey.hamiltonheatalert.heatstatus.HeatStatus;

//This service is started automatically
public class MyGcmListenerService extends com.google.android.gms.gcm.GcmListenerService {
    private static final String TAG = "MyGcmListenerService";

    public static final String NEW_ALERT_RECEIVED = "newAlertReceived";

    @Override
    public void onMessageReceived(String from, Bundle data) {
        try {
            String heatAdvisoryJson = data.getString("message");

            Gson gson = new Gson();
            HeatStatus heatStatus = gson.fromJson(heatAdvisoryJson, HeatStatus.class);

            new HeatStatusNotifier(this).logAndNotifyIfRequiered(heatStatus);

            Intent registrationComplete = new Intent(NEW_ALERT_RECEIVED);
            Bundle bundle = new Bundle();
            bundle.putSerializable("heatStatus", heatStatus);
            registrationComplete.putExtras(bundle);
            LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
        } catch (Exception e) {
            new ErrorNotification(getString(R.string.gcm_message_receive_error), "", this).show(); //This should never happen
        }

    }
}
