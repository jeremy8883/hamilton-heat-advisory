package net.jeremycasey.hamiltonheatalert.app.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import net.jeremycasey.hamiltonheatalert.R;
import net.jeremycasey.hamiltonheatalert.app.gcm.GcmPreferences;
import net.jeremycasey.hamiltonheatalert.app.gcm.MyGcmListenerService;
import net.jeremycasey.hamiltonheatalert.app.gcm.RegistrationIntentService;
import net.jeremycasey.hamiltonheatalert.app.gcm.UnregistrationIntentService;
import net.jeremycasey.hamiltonheatalert.app.notifications.HeatStatusNotification;
import net.jeremycasey.hamiltonheatalert.app.heatstatus.HeatStatusFetcherAsync;
import net.jeremycasey.hamiltonheatalert.app.utils.PreferenceUtil;
import net.jeremycasey.hamiltonheatalert.heatstatus.HeatStatus;
import net.jeremycasey.hamiltonheatalert.heatstatus.HeatStatusIsImportantChecker;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    @Bind(R.id.advisoryStatus) TextView mAdvisoryStatus;

    private HeatStatusFetcherAsync mHeatAdvisoryFetcher = null;
    @Bind(R.id.refreshButton) Button mRefreshButton;
    @Bind(R.id.pushAlertsMessage) TextView pushAlertsMessage;
    @Bind(R.id.pushAlertsCheckBox) CheckBox pushAlertsCheckBox;

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    public MainActivityFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (checkPlayServices()) {
            if (PreferenceUtil.getBoolean(getActivity(), GcmPreferences.REGISTER_AUTOMATICALLY_ON_LOAD, true)) {
                registerForGcm();
            } else {
                pushAlertsCheckBox.setChecked(PreferenceUtil.getBoolean(getActivity(), GcmPreferences.SENT_TOKEN_TO_SERVER, false));
            }
        }

        updateAdvisoryStatus();
    }

    @OnClick(R.id.refreshButton) void onRefreshButtonClicked() {
        updateAdvisoryStatus();
    }

    @OnCheckedChanged(R.id.pushAlertsCheckBox) void onPushAlertCheckboxChange(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            registerForGcm();
        } else {
            unregisterForGcm();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mOnGcmRegistrtionResponse,
                new IntentFilter(RegistrationIntentService.REGISTRATION_COMPLETE));
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mOnGcmUnregistrtionResponse,
                new IntentFilter(UnregistrationIntentService.UNREGISTRATION_COMPLETE));
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mOnGcmMessageReceived,
                new IntentFilter(MyGcmListenerService.NEW_ALERT_RECEIVED));
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mOnGcmRegistrtionResponse);
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mOnGcmUnregistrtionResponse);
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mOnGcmMessageReceived);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(getActivity());
        mAdvisoryStatus = null;
        mRefreshButton = null;
        if (mHeatAdvisoryFetcher != null) {
            mHeatAdvisoryFetcher.cancel(true);
            mHeatAdvisoryFetcher = null;
        }
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity());
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, getActivity(),
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                showPushAlertMessageBelowCheckbox(R.string.errorGooglePlayServicesNotSupported);
                pushAlertsCheckBox.setEnabled(false);
                pushAlertsCheckBox.setChecked(false);
            }
            return false;
        }
        return true;
    }

    private void registerForGcm() {
        pushAlertsCheckBox.setChecked(true);
        pushAlertsCheckBox.setEnabled(false);
        showPushAlertMessageBelowCheckbox(R.string.registeringForPushNotificationsNowPleaseWait);
        RegistrationIntentService.start(getActivity());
        //mOnGcmRegistrtionResponse callback is (un)registered at onResume and onPause
    }

    private BroadcastReceiver mOnGcmRegistrtionResponse = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            pushAlertsCheckBox.setEnabled(true);
            boolean sentToken = PreferenceUtil.getBoolean(getActivity(), GcmPreferences.SENT_TOKEN_TO_SERVER, false);
            if (sentToken) {
                pushAlertsCheckBox.setChecked(true);
                hidePushAlertMessageBelowCheckbox();
                //Once the first automatic registration is complete, the (un)registration is manual from then on
                PreferenceUtil.put(getActivity(), GcmPreferences.REGISTER_AUTOMATICALLY_ON_LOAD, false);
            } else {
                pushAlertsCheckBox.setChecked(false);
                showPushAlertMessageBelowCheckbox(R.string.pushAlertsRegistrationFailed);
            }
        }
    };

    private void unregisterForGcm() {
        pushAlertsCheckBox.setChecked(false);
        pushAlertsCheckBox.setEnabled(false);
        showPushAlertMessageBelowCheckbox(R.string.unregisteringPushNotificationsPleaseWait);
        UnregistrationIntentService.start(getActivity());
        //mOnGcmUnregistrtionResponse callback is (un)registered at onResume and onPause
    }

    private BroadcastReceiver mOnGcmUnregistrtionResponse = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            pushAlertsCheckBox.setEnabled(true);
            pushAlertsCheckBox.setChecked(false);
            hidePushAlertMessageBelowCheckbox();
        }
    };

    private void updateAdvisoryStatus() {
        displayAsChecking();
        if (mHeatAdvisoryFetcher != null) {
            mHeatAdvisoryFetcher.cancel(true);
        }
        mHeatAdvisoryFetcher = new HeatStatusFetcherAsync(mHeatAdvisoryFetcherListener);
        mHeatAdvisoryFetcher.execute();
    }

    private HeatStatusFetcherAsync.FetchListener mHeatAdvisoryFetcherListener = new HeatStatusFetcherAsync.FetchListener() {
        @Override
        public void onFetchComplete(HeatStatus heatStatus) {
            displayAsNoLongerChecking();
            displayHeatAdvisoryInfo(heatStatus);
        }

        @Override
        public void onFetchError(Exception ex) {
            displayAsNoLongerChecking();
            showError(getString(R.string.heatAdvisoryFetchError));
        }
    };

    private void displayAsChecking() {
        mRefreshButton.setEnabled(false);
        mRefreshButton.setText(R.string.refreshButtonChecking);
        mAdvisoryStatus.setText(R.string.advisoryStatusChecking);
    }
    private void displayAsNoLongerChecking() {
        mRefreshButton.setText(R.string.refreshButton);
        mRefreshButton.setEnabled(true);
    }

    private void displayHeatAdvisoryInfo(HeatStatus heatStatus) {
        mAdvisoryStatus.setText(heatStatus.getStageText());
        HeatStatusNotification heatStatusNotification = new HeatStatusNotification(heatStatus, getActivity());
        if (new HeatStatusIsImportantChecker(heatStatus).isImportant()) {
            heatStatusNotification.showNotification();
        }
    }

    private void showPushAlertMessageBelowCheckbox(int resourceId) {
        pushAlertsMessage.setText(getActivity().getString(resourceId));
        pushAlertsMessage.setVisibility(View.VISIBLE);
    }

    private void hidePushAlertMessageBelowCheckbox() {
        pushAlertsMessage.setVisibility(View.GONE);
    }

    private void showError(String text) {
        Log.e("MainActivity", text);
        mAdvisoryStatus.setText(text);
    }

    BroadcastReceiver mOnGcmMessageReceived = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (mHeatAdvisoryFetcher != null) {
                mHeatAdvisoryFetcher.cancel(true);
            }
            displayAsNoLongerChecking();
            Bundle extras = intent.getExtras();
            HeatStatus heatStatus = (HeatStatus)extras.getSerializable("heatStatus");
            displayHeatAdvisoryInfo(heatStatus);
        }
    };
}
