package net.jeremycasey.hamiltonheatalert.app.gcm;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import net.jeremycasey.hamiltonheatalert.R;
import net.jeremycasey.hamiltonheatalert.app.utils.PreferenceUtil;

import java.io.IOException;

public class UnregistrationIntentService extends IntentService {

    public static final String UNREGISTRATION_COMPLETE = "unregistationComplete";

    private static final String TAG = "RegIntentService";
    private static final String[] TOPICS = {"heatalert"};

    public UnregistrationIntentService() {
        super(TAG);
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, UnregistrationIntentService.class);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            synchronized (RegistrationIntentService.TAG) {
                InstanceID instanceID = InstanceID.getInstance(this);
                String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                        GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                Log.i(TAG, "GCM Registration Token: " + token);

                unsubscribeTopics(token);

                PreferenceUtil.put(this, GcmPreferences.SENT_TOKEN_TO_SERVER, false);
            }
        } catch (Exception e) {
            Log.d(TAG, "Failed to complete token refresh", e);
            //As long as the preference is set to false, push messages will get ignored anyway
            PreferenceUtil.put(this, GcmPreferences.SENT_TOKEN_TO_SERVER, false);
        }
        // Notify UI that unregistration has completed
        Intent unregistrationComplete = new Intent(UNREGISTRATION_COMPLETE);
        LocalBroadcastManager.getInstance(this).sendBroadcast(unregistrationComplete);
    }

    private void unsubscribeTopics(String token) throws IOException {
        for (String topic : TOPICS) {
            GcmPubSub pubSub = GcmPubSub.getInstance(this);
            pubSub.unsubscribe(token, "/topics/" + topic);
        }
    }

}
