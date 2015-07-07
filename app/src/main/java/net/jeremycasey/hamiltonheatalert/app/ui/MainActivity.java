package net.jeremycasey.hamiltonheatalert.app.ui;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import net.jeremycasey.hamiltonheatalert.R;
import net.jeremycasey.hamiltonheatalert.app.notifications.HeatStatusNotification;


public class MainActivity extends ActionBarActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        HeatStatusNotification.hideNotification(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

}
