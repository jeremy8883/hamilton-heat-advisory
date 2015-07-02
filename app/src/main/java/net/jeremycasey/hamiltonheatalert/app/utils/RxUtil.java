package net.jeremycasey.hamiltonheatalert.app.utils;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class RxUtil {
    public static CompositeSubscription getNewCompositeSubIfUnsubscribed(CompositeSubscription subscriptions) {
        if (subscriptions == null || subscriptions.isUnsubscribed()) {
            return new CompositeSubscription();
        }
        return subscriptions;
    }

    public static void unsubscribeIfNotNull(CompositeSubscription subscriptions) {
        if (subscriptions != null) {
            subscriptions.unsubscribe();
        }
    }

    //This doesn't seem to work
//    public static <T> Observable.Transformer<T,T> scheduleOnIoThenMainThread() {
//        return new Observable.Transformer<T, T>() {
//            @Override
//            public Observable<T> call(Observable<T> tObservable) {
//                return tObservable.subscribeOn(Schedulers.io())
//                        .observeOn(AndroidSchedulers.mainThread());
//            }
//        };
//    }
}
