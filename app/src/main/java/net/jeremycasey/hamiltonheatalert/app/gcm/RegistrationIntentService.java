package net.jeremycasey.hamiltonheatalert.app.gcm;

/**
 * Copyright 2015 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

    public static final String REGISTRATION_COMPLETE = "registrationComplete";

    public static final String TAG = "RegIntentService";
    private static final String[] TOPICS = {GcmSettings.TOPIC};

    public RegistrationIntentService() {
        super(TAG);
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, RegistrationIntentService.class);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            synchronized (TAG) {
                String token = getGcmToken();

                subscribeTopics(token);

                PreferenceUtil.put(this, GcmPreferenceKeys.SENT_TOKEN_TO_SERVER, true);

                //Once the first automatic registration is complete, the (un)registration is manual from then on
                PreferenceUtil.put(this, GcmPreferenceKeys.REGISTER_AUTOMATICALLY_ON_LOAD, false);
            }
        } catch (Exception e) {
            Log.d(TAG, "Failed to complete token refresh", e);
            //This ensures that we'll attempt the update at a later time.
            PreferenceUtil.put(this, GcmPreferenceKeys.REGISTER_AUTOMATICALLY_ON_LOAD, false);
            PreferenceUtil.put(this, GcmPreferenceKeys.SENT_TOKEN_TO_SERVER, false);
        }
        notifySubscribersOfCompletion();
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

    private void notifySubscribersOfCompletion() {
        Intent registrationComplete = new Intent(REGISTRATION_COMPLETE);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }
}
