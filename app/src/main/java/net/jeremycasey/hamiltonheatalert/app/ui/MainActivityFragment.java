package net.jeremycasey.hamiltonheatalert.app.ui;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import net.jeremycasey.hamiltonheatalert.R;
import net.jeremycasey.hamiltonheatalert.app.gcm.GcmPreferenceKeys;
import net.jeremycasey.hamiltonheatalert.app.gcm.MyGcmListenerService;
import net.jeremycasey.hamiltonheatalert.app.gcm.RegistrationIntentService;
import net.jeremycasey.hamiltonheatalert.app.gcm.UnregistrationIntentService;
import net.jeremycasey.hamiltonheatalert.app.notifications.HeatStatusNotification;
import net.jeremycasey.hamiltonheatalert.app.utils.PreferenceUtil;
import net.jeremycasey.hamiltonheatalert.app.utils.RxUtil;
import net.jeremycasey.hamiltonheatalert.heatstatus.HeatStatus;
import net.jeremycasey.hamiltonheatalert.heatstatus.HeatStatusFetcher;
import net.jeremycasey.hamiltonheatalert.heatstatus.HeatStatusIsImportantChecker;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observer;
import rx.android.content.ContentObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;


public class MainActivityFragment extends Fragment {
    @Bind(R.id.advisoryStatus) TextView mAdvisoryStatus;
    @Bind(R.id.pushAlertsMessage) TextView pushAlertsMessage;
    @Bind(R.id.pushAlertsCheckBox) CheckBox pushAlertsCheckBox;
    private MenuItem mRefreshMenuItem = null;
    private Animation mRefreshRotation = null;

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private CompositeSubscription mSubscriptions = new CompositeSubscription();

    public MainActivityFragment() {
        mSubscriptions = RxUtil.getNewCompositeSubIfUnsubscribed(mSubscriptions);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
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
            if (PreferenceUtil.getBoolean(getActivity(), GcmPreferenceKeys.REGISTER_AUTOMATICALLY_ON_LOAD, true)) {
                registerForGcm();
            } else {
                pushAlertsCheckBox.setChecked(PreferenceUtil.getBoolean(getActivity(), GcmPreferenceKeys.SENT_TOKEN_TO_SERVER, false));
            }
        }

        updateAdvisoryStatus();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_fragment, menu);

        mRefreshMenuItem = menu.findItem(R.id.action_refresh);
        mRefreshRotation = AnimationUtils.loadAnimation(getActivity(), R.anim.refresh_rotate);
        LinearLayout lay = (LinearLayout)((LayoutInflater)getActivity()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.ic_action_refresh, null);
        lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected(mRefreshMenuItem);
            }
        });

        mRefreshMenuItem.setActionView(lay);
    }

    @Override
    public void onDestroyOptionsMenu() {
        mRefreshMenuItem = null;
        mRefreshRotation = null;
        super.onDestroyOptionsMenu();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_refresh:
                updateAdvisoryStatus();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.pushAlertsCheckBox) void onPushAlertCheckboxChange() {
        if (pushAlertsCheckBox.isChecked()) {
            registerForGcm();
        } else {
            unregisterForGcm();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mSubscriptions.add(
                ContentObservable.fromLocalBroadcast(getActivity(), new IntentFilter(RegistrationIntentService.REGISTRATION_COMPLETE))
                        .subscribe(new Action1<Intent>() {
                            @Override
                            public void call(Intent intent) {
                                onGcmRegistrationResponse(intent);
                            }
                        })
        );
        mSubscriptions.add(
                ContentObservable.fromLocalBroadcast(getActivity(), new IntentFilter(UnregistrationIntentService.UNREGISTRATION_COMPLETE))
                        .subscribe(new Action1<Intent>() {
                            @Override
                            public void call(Intent intent) {
                                onGcmUnregistrtionResponse(intent);
                            }
                        })
        );
        mSubscriptions.add(
                ContentObservable.fromLocalBroadcast(getActivity(), new IntentFilter(MyGcmListenerService.NEW_ALERT_RECEIVED))
                        .subscribe(new Action1<Intent>() {
                            @Override
                            public void call(Intent intent) {
                                onGcmMessageReceived(intent);
                            }
                        })
        );
    }

    @Override
    public void onPause() {
        super.onPause();
        RxUtil.unsubscribeIfNotNull(mSubscriptions);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(getActivity());
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

    public void onGcmRegistrationResponse(Intent intent) {
        pushAlertsCheckBox.setEnabled(true);
        boolean sentToken = PreferenceUtil.getBoolean(getActivity(), GcmPreferenceKeys.SENT_TOKEN_TO_SERVER, false);
        if (sentToken) {
            pushAlertsCheckBox.setChecked(true);
            hidePushAlertMessageBelowCheckbox();
        } else {
            pushAlertsCheckBox.setChecked(false);
            showPushAlertMessageBelowCheckbox(R.string.pushAlertsRegistrationFailed);
        }
    }

    private void unregisterForGcm() {
        pushAlertsCheckBox.setChecked(false);
        pushAlertsCheckBox.setEnabled(false);
        showPushAlertMessageBelowCheckbox(R.string.unregisteringPushNotificationsPleaseWait);
        UnregistrationIntentService.start(getActivity());
        //mOnGcmUnregistrtionResponse callback is (un)registered at onResume and onPause
    }

    private void onGcmUnregistrtionResponse(Intent intent) {
        hidePushAlertMessageBelowCheckbox();
        pushAlertsCheckBox.setEnabled(true);
        pushAlertsCheckBox.setChecked(false);
    }

    private void updateAdvisoryStatus() {
        displayAsChecking();
        mSubscriptions.add(
                new HeatStatusFetcher().toObservable()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(mOnHeatAlertFetched)
        );
    }

    private Observer<HeatStatus> mOnHeatAlertFetched = new Observer<HeatStatus>() {
        @Override
        public void onNext(HeatStatus heatStatus) {
            displayAsNoLongerChecking();
            displayHeatAdvisoryInfo(heatStatus);
        }
        @Override
        public void onCompleted() { }
        @Override
        public void onError(Throwable e) {
            displayAsNoLongerChecking();
            showError(getString(R.string.heatAdvisoryFetchError));
        }
    };

    private void displayAsChecking() {
        if (mRefreshMenuItem != null) {
            if (mRefreshMenuItem.getActionView().findViewById(R.id.ivIcon).getAnimation() == null) {
                mRefreshMenuItem.getActionView().findViewById(R.id.ivIcon).startAnimation(mRefreshRotation);
            }
        }
        mAdvisoryStatus.setText(R.string.advisoryStatusChecking);
    }
    private void displayAsNoLongerChecking() {
        if (mRefreshMenuItem != null) {
            mRefreshMenuItem.getActionView().findViewById(R.id.ivIcon).clearAnimation();
        }
    }

    private void displayHeatAdvisoryInfo(HeatStatus heatStatus) {
        mAdvisoryStatus.setText(heatStatus.getStageText().replace(" - ", "\r\n"));
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

    public void onGcmMessageReceived(Intent intent) {
        displayAsNoLongerChecking();
        Bundle extras = intent.getExtras();
        HeatStatus heatStatus = (HeatStatus)extras.getSerializable("heatStatus");
        displayHeatAdvisoryInfo(heatStatus);
    }
}
