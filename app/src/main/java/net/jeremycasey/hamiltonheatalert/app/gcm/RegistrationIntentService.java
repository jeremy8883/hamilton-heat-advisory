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
import net.jeremycasey.hamiltonheatalert.gcm.GcmSettings;

import java.io.IOException;

public class RegistrationIntentService extends IntentService {

    public static final String REGISTRATION_CHANGED = "net.jeremycasey.hamiltonheatalert.app.gcm.RegistrationIntentService.registrationChanged";

    public static final String TAG = "RegIntentService";
    private static final String[] TOPICS = {GcmSettings.TOPIC};
    public static final String EXTRA_IS_SUBSCRIPTION = "isSubscription";

    public RegistrationIntentService() {
        super(TAG);
    }

    public static void start(Context context, boolean isSubscription) {
        Intent intent = new Intent(context, RegistrationIntentService.class);
        intent.putExtra(EXTRA_IS_SUBSCRIPTION, isSubscription);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        boolean isSubscription = intent.getBooleanExtra(EXTRA_IS_SUBSCRIPTION, true);
        try {
            synchronized (TAG) {
                String token = getGcmToken();

                if (isSubscription) {
                    subscribeTopics(token);
                } else {
                    unsubscribeTopics(token);
                }

                PreferenceUtil.put(this, GcmPreferenceKeys.SENT_TOKEN_TO_SERVER, true);

                //Once the first automatic registration is complete, the (un)registration is manual from then on
                PreferenceUtil.put(this, GcmPreferenceKeys.REGISTER_AUTOMATICALLY_ON_LOAD, false);
            }
        } catch (Exception e) {
            Log.d(TAG, "Failed to complete token refresh", e);
            PreferenceUtil.put(this, GcmPreferenceKeys.SENT_TOKEN_TO_SERVER, false);
        }
        notifySubscribersOfCompletion(isSubscription);
    }

    private String getGcmToken() throws IOException {
        // Initially this call goes out to the network to retrieve the token, subsequent calls
        // are local.
        InstanceID instanceID = InstanceID.getInstance(this);
        String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
        Log.i(TAG, "GCM Registration Token: " + token);
        return token;
    }

    private void subscribeTopics(String token) throws IOException {
        for (String topic : TOPICS) {
            GcmPubSub pubSub = GcmPubSub.getInstance(this);
            pubSub.subscribe(token, "/topics/" + topic, null);
        }
    }

    private void unsubscribeTopics(String token) throws IOException {
        for (String topic : TOPICS) {
            GcmPubSub pubSub = GcmPubSub.getInstance(this);
            pubSub.unsubscribe(token, "/topics/" + topic);
        }
    }

    private void notifySubscribersOfCompletion(boolean isSubscription) {
        Intent intent = new Intent(REGISTRATION_CHANGED);
        intent.putExtra(EXTRA_IS_SUBSCRIPTION, isSubscription);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
