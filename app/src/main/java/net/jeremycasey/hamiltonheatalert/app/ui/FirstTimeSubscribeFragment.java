package net.jeremycasey.hamiltonheatalert.app.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.jeremycasey.hamiltonheatalert.R;
import net.jeremycasey.hamiltonheatalert.app.gcm.GcmPreferenceKeys;
import net.jeremycasey.hamiltonheatalert.app.gcm.RegistrationIntentService;
import net.jeremycasey.hamiltonheatalert.app.utils.PreferenceUtil;
import net.jeremycasey.hamiltonheatalert.app.utils.RxUtil;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.content.ContentObservable;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

public class FirstTimeSubscribeFragment extends Fragment {
    public interface OnDoneCallback {
        void onFirstTimeSubscribeFragmentDone();
    }

    @Bind(R.id.layout_subscribing) ViewGroup mSubscribingLayout;
    @Bind(R.id.layout_subscribe_success) ViewGroup mSubscribeSuccessLayout;
    @Bind(R.id.layout_subscribe_error) ViewGroup mSubscribeErrorLayout;

    OnDoneCallback mOnDoneCallback;

    CompositeSubscription mSubscriptions = new CompositeSubscription();

    public FirstTimeSubscribeFragment() {
        mSubscriptions = RxUtil.getNewCompositeSubIfUnsubscribed(mSubscriptions);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof OnDoneCallback)) {
            throw new ClassCastException("Activity must implement OnDoneListener.OnDoneCallback.");
        }
        mOnDoneCallback = (OnDoneCallback) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragmen_first_time_subscribe, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        registerForGcm();
    }

    @Override
    public void onResume() {
        super.onResume();

        mSubscriptions.add(
                ContentObservable.fromLocalBroadcast(getActivity(), new IntentFilter(RegistrationIntentService.REGISTRATION_CHANGED))
                        .subscribe(new Action1<Intent>() {
                            @Override
                            public void call(Intent intent) {
                                boolean isSubscribe = intent.getBooleanExtra(RegistrationIntentService.EXTRA_IS_SUBSCRIPTION, true);
                                if (isSubscribe) {
                                    onGcmRegistrationResponse();
                                }
                            }
                        })
        );
    }

    @Override
    public void onPause() {
        super.onPause();
        RxUtil.unsubscribeIfNotNull(mSubscriptions);
    }

    @OnClick({R.id.continue_from_success_button, R.id.continue_from_error_button}) void onContinueClicked() {
        mOnDoneCallback.onFirstTimeSubscribeFragmentDone();
    }

    @OnClick(R.id.retry_from_error_button) void onRetryClicked() {
        registerForGcm();
    }

    private void registerForGcm() {
        setVisibleLayout(mSubscribingLayout);
        RegistrationIntentService.start(getActivity(), true);
        //mOnGcmRegistrtionResponse callback is registered at onResume and onPause
    }

    private void setVisibleLayout(ViewGroup layout) {
        setVisibleIfEqual(mSubscribingLayout, layout);
        setVisibleIfEqual(mSubscribeSuccessLayout, layout);
        setVisibleIfEqual(mSubscribeErrorLayout, layout);
    }

    private void setVisibleIfEqual(ViewGroup layoutA, ViewGroup layoutB) {
        layoutA.setVisibility(layoutA == layoutB ? View.VISIBLE : View.GONE);
    }

    public void onGcmRegistrationResponse() {
        boolean sentToken = PreferenceUtil.getBoolean(getActivity(), GcmPreferenceKeys.SENT_TOKEN_TO_SERVER, false);
        if (sentToken) {
            setVisibleLayout(mSubscribeSuccessLayout);
        } else {
            setVisibleLayout(mSubscribeErrorLayout);
        }
    }

}
