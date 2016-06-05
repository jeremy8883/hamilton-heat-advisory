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

public class ErrorNotification {
    private static final int NOTIFICATION_ID = 2;

    private final String mText;
    private final String mTitle;
    private final Context mContext;
    public ErrorNotification(String title, String text, Context context) {
        mTitle = title;
        mText = text;
        mContext = context;
    }

    public void show() {
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext)
                .setSmallIcon(R.drawable.ic_stat_thermometer)
                .setContentTitle(mTitle)
                .setContentText(mText)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setTicker(mTitle)
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
}
