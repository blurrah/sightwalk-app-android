package net.sightwalk.Tasks;

import android.annotation.TargetApi;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;

import net.sightwalk.Controllers.Route.NewRouteActivity;
import net.sightwalk.R;

public class AlarmReceiver extends BroadcastReceiver {

    @TargetApi(16)
    @Override
    public void onReceive(Context context, Intent intent) {
        Bitmap icon = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setLargeIcon(icon)
                .setSmallIcon(R.drawable.ic_walking)
                .setContentTitle("Sightwalk")
                .setContentText("Er zijn nieuwe Sights beschikbaar!");

        Intent resultIntent = new Intent(context, NewRouteActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);

        stackBuilder.addNextIntent(resultIntent);

        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setContentIntent(resultPendingIntent);

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(1, mBuilder.build());
    }
}