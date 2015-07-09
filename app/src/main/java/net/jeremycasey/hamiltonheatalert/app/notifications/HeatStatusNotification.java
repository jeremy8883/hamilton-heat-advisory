package net.jeremycasey.hamiltonheatalert.app.notifications;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import net.jeremycasey.hamiltonheatalert.R;
import net.jeremycasey.hamiltonheatalert.app.ui.MainActivity;
import net.jeremycasey.hamiltonheatalert.heatstatus.HeatStatus;

public class HeatStatusNotification {
    private static final int NOTIFICATION_ID = 1;
    private HeatStatus mHeatStatus;
    private Context mContext;
    private Iterable<? extends HeatStatus> notificationsSentInThePastDay;

    public HeatStatusNotification(HeatStatus heatStatus, Context context) {
        mHeatStatus = heatStatus;
        mContext = context;
    }

    public void showNotification() {
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext)
                .setSmallIcon(R.drawable.ic_stat_thermometer)
                .setContentTitle(mHeatStatus.getStageText())
                .setContentText(mContext.getString(R.string.alert_context_text))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setTicker(mHeatStatus.getStageText())
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

    public static void hideNotification(Context context) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(NOTIFICATION_ID);
    }
}
