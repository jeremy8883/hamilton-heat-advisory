package net.jeremycasey.hamiltonheatalert.heatadvisory;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import net.jeremycasey.hamiltonheatalert.R;
import net.jeremycasey.hamiltonheatalert.ui.MainActivity;

import java.util.ArrayList;

public class AdvisoryNotification {
    private static final int NOTIFICATION_ID = 1;
    private HeatAdvisory mHeatAdvisory;
    private Context mContext;
    private Iterable<? extends HeatAdvisory> notificationsSentInThePastDay;

    public AdvisoryNotification(HeatAdvisory heatAdvisory, Context context) {
        mHeatAdvisory = heatAdvisory;
        mContext = context;
    }

    public boolean shouldShowNotification() {
        return true;
//        return dangerIsSeriousEnough() &&
//                isTheHighestDangerLevelInThePastDay();
    }

    private boolean dangerIsSeriousEnough() {
        return mHeatAdvisory.getStage() > 0;
    }

    private boolean isTheHighestDangerLevelInThePastDay() {
        for (HeatAdvisory prevHeatAdvisory : getNotificationsSentInThePastDay()) {
            if (mHeatAdvisory.getStage() <= prevHeatAdvisory.getStage()) {
                return false;
            }
        }
        return true;
    }

    public void showNotification() {
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext)
                .setSmallIcon(R.drawable.ic_stat_thermometer)
                .setContentTitle(mHeatAdvisory.getStageText())
                .setContentText(mContext.getString(R.string.alertContextText))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setTicker(mHeatAdvisory.getStageText())
                .setVibrate(new long[] { 500, 500, 500 })
                .setSound(soundUri);

        Intent intent = new Intent(mContext, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext.getApplicationContext(),
                3000, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(pendingIntent);

        NotificationManager mNotificationManager =
                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    public Iterable<? extends HeatAdvisory> getNotificationsSentInThePastDay() {
        return null; //TODO
    }

    public static void hideNotification(Context context) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(NOTIFICATION_ID);
    }
}
