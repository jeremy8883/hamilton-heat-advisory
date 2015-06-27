package net.jeremycasey.hamiltonheatalert.ui;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

import net.jeremycasey.hamiltonheatalert.R;
import net.jeremycasey.hamiltonheatalert.gcm.RegistrationIntentService;
import net.jeremycasey.hamiltonheatalert.heatadvisory.AdvisoryNotification;


public class MainActivity extends ActionBarActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AdvisoryNotification.hideNotification(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    //    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//        if (id == R.id.action_settings) {
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }

}
