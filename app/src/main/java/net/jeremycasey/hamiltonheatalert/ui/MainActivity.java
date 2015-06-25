package net.jeremycasey.hamiltonheatalert.ui;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import net.jeremycasey.hamiltonheatalert.R;
import net.jeremycasey.hamiltonheatalert.heatadvisory.HeatAdvisory;
import net.jeremycasey.hamiltonheatalert.heatadvisory.HeatAdvisoryFetcher;


public class MainActivity extends ActionBarActivity {

    TextView advisoryStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        advisoryStatus = (TextView) findViewById(R.id.advisoryStatus);

        updateAdvisoryStatus();
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

    private void updateAdvisoryStatus() {
        new HeatAdvisoryFetcher(mHeatAdvisoryFetcherListener).run();
    }

    private HeatAdvisoryFetcher.FetchListener mHeatAdvisoryFetcherListener = new HeatAdvisoryFetcher.FetchListener() {
        @Override
        public void onFetchComplete(HeatAdvisory heatAdvisory) {
            displayHeatAdvisoryInfo(heatAdvisory);
        }

        @Override
        public void onFetchError(Exception ex) {
            showError(getString(R.string.heatAdvisoryFetchError));
        }
    };

    private void displayHeatAdvisoryInfo(HeatAdvisory heatAdvisory) {
        advisoryStatus.setText(heatAdvisory.getStageText());
    }

    private void showError(String text) {
        Log.e("MainActivity", text);
        advisoryStatus.setText(text);
    }


}
