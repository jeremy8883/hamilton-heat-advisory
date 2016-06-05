package net.jeremycasey.hamiltonheatalert.app.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import net.jeremycasey.hamiltonheatalert.R;
import net.jeremycasey.hamiltonheatalert.app.gcm.GcmPreferenceKeys;
import net.jeremycasey.hamiltonheatalert.app.notifications.HeatStatusNotification;
import net.jeremycasey.hamiltonheatalert.app.utils.PreferenceUtil;

import de.keyboardsurfer.android.widget.crouton.Crouton;


public class MainActivity extends ActionBarActivity implements FirstTimeSubscribeFragment.OnDoneCallback {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        HeatStatusNotification.hideNotification(this);

        boolean pushNotificationsAreSupported = checkPlayServicesAndShowAlertsIfNecessary();
        if (pushNotificationsAreSupported && needToRegisterForPushNotificationsOnLoad()) {
            showFirstTimeSubscribeFragment();
            //Then, onFirstTimeSubscribeFragmentDone() will be called when the user hits continue, which in turn calls showMainFragment()
        } else {
            showMainFragment();
        }
    }

    @Override
    public void onFirstTimeSubscribeFragmentDone() {
        showMainFragment();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Crouton.cancelAllCroutons();
    }

    private boolean checkPlayServicesAndShowAlertsIfNecessary() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, 9000).show();
            } else {
                showPlayServicesNotSupportedErrorIfAllowed();
            }
            return false;
        }
        return true;
    }

    private void showPlayServicesNotSupportedErrorIfAllowed() {
        if (GooglePlayServicesNotSupportedDialog.shouldShow(this)) {
            new GooglePlayServicesNotSupportedDialog().show(getSupportFragmentManager(), null);
        }
    }

    private boolean needToRegisterForPushNotificationsOnLoad() {
        return PreferenceUtil.getBoolean(this, GcmPreferenceKeys.REGISTER_AUTOMATICALLY_ON_LOAD, true);
    }

    private void showFirstTimeSubscribeFragment() {
        replaceMainFragment(new FirstTimeSubscribeFragment(), "FirstTimeSubscribe");
    }

    private void showMainFragment() {
        replaceMainFragment(new CurrentStatusFragment(), "Main");
    }

    private void replaceMainFragment(Fragment newFragment, String tag) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.mainFragmentHolder, newFragment, tag);
        transaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.menuDisclaimer:
                showDisclaimer();
                return true;
            case R.id.menuWebsite:
                openWebsite();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void openWebsite() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(
                getString(R.string.heat_alert_website)
        ));
        startActivity(browserIntent);
    }

    private void showDisclaimer() {
        new DisclaimerDialog()
                .show(getSupportFragmentManager(), null);
    }
}
