package net.jeremycasey.hamiltonheatalert.ui;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import net.jeremycasey.hamiltonheatalert.R;
import net.jeremycasey.hamiltonheatalert.heatadvisory.AdvisoryNotification;
import net.jeremycasey.hamiltonheatalert.heatadvisory.HeatAdvisory;
import net.jeremycasey.hamiltonheatalert.heatadvisory.HeatAdvisoryFetcher;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    TextView mAdvisoryStatus;
    HeatAdvisoryFetcher mHeatAdvisoryFetcher = null;
    Button mRefreshButton;

    public MainActivityFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mAdvisoryStatus = (TextView)getView().findViewById(R.id.advisoryStatus);
        mRefreshButton = (Button)getView().findViewById(R.id.refreshButton);
        mRefreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateAdvisoryStatus();
            }
        });

        updateAdvisoryStatus();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mAdvisoryStatus = null;
        mRefreshButton = null;
        if (mHeatAdvisoryFetcher != null) {
            mHeatAdvisoryFetcher.cancel();
            mHeatAdvisoryFetcher = null;
        }
    }

    private void updateAdvisoryStatus() {
        displayAsChecking();
        if (mHeatAdvisoryFetcher == null) {
            mHeatAdvisoryFetcher = new HeatAdvisoryFetcher(mHeatAdvisoryFetcherListener);
        }
        mHeatAdvisoryFetcher.run();
    }

    private HeatAdvisoryFetcher.FetchListener mHeatAdvisoryFetcherListener = new HeatAdvisoryFetcher.FetchListener() {
        @Override
        public void onFetchComplete(HeatAdvisory heatAdvisory) {
            displayAsNoLongerChecking();
            displayHeatAdvisoryInfo(heatAdvisory);
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

    private void displayHeatAdvisoryInfo(HeatAdvisory heatAdvisory) {
        mAdvisoryStatus.setText(heatAdvisory.getStageText());
        AdvisoryNotification advisoryNotification = new AdvisoryNotification(heatAdvisory, getActivity());
        if (advisoryNotification.shouldShowNotification()) {
            advisoryNotification.showNotification();
        }
    }

    private void showError(String text) {
        Log.e("MainActivity", text);
        mAdvisoryStatus.setText(text);
    }
}
