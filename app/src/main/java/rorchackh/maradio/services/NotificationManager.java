package rorchackh.maradio.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import rorchackh.maradio.R;
import rorchackh.maradio.activities.PlayerActivity;
import rorchackh.maradio.libraries.Globals;
import rorchackh.maradio.libraries.Statics;
import rorchackh.maradio.models.Station;

public class NotificationManager {

    public static void show(Context context, Station station, String status, boolean hasNext, boolean hasPrev) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

        Intent intent = new Intent(context, PlayerActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        builder.setContentIntent(PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT));

        RemoteViews contentView = new RemoteViews(context.getPackageName(), R.layout.notification);
        RemoteViews expandedView = new RemoteViews(context.getPackageName(), R.layout.notification_expanded);

        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setAutoCancel(false);
        builder.setOngoing(true);
        builder.setWhen(0);

        builder.setContent(contentView);
        builder.setCustomBigContentView(expandedView);

        Notification notification = builder.build();

        applyUI(context, contentView, expandedView, station, status);
        applyEvents(context, station, contentView, expandedView, hasNext, hasPrev, status);

        if (Globals.notificationManager == null) {
            Globals.notificationManager = (android.app.NotificationManager) context.getSystemService(Service.NOTIFICATION_SERVICE);
        }

        Globals.notificationManager.notify(Statics.notificationId, notification);
    }

    public static void remove() {
        if (Globals.notificationManager != null) {
            Globals.notificationManager.cancel(Statics.notificationId);
        }
    }

    private static void applyEvents(Context context, Station station, RemoteViews contentView, RemoteViews expandedViews, boolean hasNext, boolean hasPrev, String status) {
        int broadCastIds = 0;

        Intent stopIntent = new Intent(Statics.SERVICE_MESSAGE);
        stopIntent.putExtra(Statics.SERVICE_MESSAGE, Statics.SERVICE_STOP);
        PendingIntent stopPendingIntent = PendingIntent.getBroadcast(context, broadCastIds++, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        expandedViews.setOnClickPendingIntent(R.id.stop, stopPendingIntent);
        contentView.setOnClickPendingIntent(R.id.stop, stopPendingIntent);

        Intent nextIntent = new Intent(Statics.SERVICE_MESSAGE);
        nextIntent.putExtra(Statics.SERVICE_MESSAGE, Statics.SERVICE_NEXT);
        PendingIntent nextPendingIntent = PendingIntent.getBroadcast(context, broadCastIds++, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (hasNext) {
            expandedViews.setOnClickPendingIntent(R.id.next, nextPendingIntent);
        } else {
            expandedViews.setOnClickPendingIntent(R.id.next, null);
            nextPendingIntent.cancel();
        }

        Intent prevIntent = new Intent(Statics.SERVICE_MESSAGE);
        prevIntent.putExtra(Statics.SERVICE_MESSAGE, Statics.SERVICE_PREV);
        PendingIntent prevPendingIntent = PendingIntent.getBroadcast(context, broadCastIds++, prevIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        if (hasPrev) {
            expandedViews.setOnClickPendingIntent(R.id.prev, prevPendingIntent);
        } else {
            expandedViews.setOnClickPendingIntent(R.id.prev, null);
            prevPendingIntent.cancel();
        }


        Intent pauseIntent = new Intent(Statics.SERVICE_MESSAGE);
        switch (status) {
            case Statics.SERVICE_PAUSE:
                status = Statics.SERVICE_PLAY;
                break;
            case Statics.SERVICE_PLAY:
                status = Statics.SERVICE_PAUSE;
                break;
            default:
                return;
        }

        pauseIntent.putExtra(Statics.SERVICE_MESSAGE, status);
        pauseIntent.putExtra(Statics.station, station);
        PendingIntent pausePendingIntent = PendingIntent.getBroadcast(context, broadCastIds++, pauseIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        expandedViews.setOnClickPendingIntent(R.id.toggle_container, pausePendingIntent);
    }

    private static void applyUI(Context context, RemoteViews contentView, RemoteViews expandedViews, Station station, String status) {
         contentView.setTextViewText(R.id.notification_title, station.toString());

        // Todo: This needs to be fixed using the new image library
        String url = station.getImageLink();
        Log.i(Statics.debug, String.format("URL in the notification is %s", url));
        if (url  == null || url.equals("")) {
            contentView.setImageViewResource(R.id.notification_image_small, R.drawable.none);
            expandedViews.setImageViewResource(R.id.notification_image_big, R.drawable.none);
        } else {
            Bitmap image = Globals.imageLoader.loadImageSync(url);

            contentView.setImageViewBitmap(R.id.notification_image_small, image);
            contentView.setImageViewBitmap(R.id.notification_image_big, image);
        }

        expandedViews.setTextViewText(R.id.title, station.getTitle());
        expandedViews.setTextViewText(R.id.subtitle, station.getSubtitle());

        switch (status){
            case Statics.SERVICE_PAUSE:
                expandedViews.setInt(R.id.toggle, "setBackgroundResource", R.drawable.ic_play_dark);
                expandedViews.setInt(R.id.loader, "setVisibility", View.GONE);
                expandedViews.setInt(R.id.toggle, "setVisibility", View.VISIBLE);
                break;
            case Statics.SERVICE_PLAY:
                expandedViews.setInt(R.id.toggle, "setBackgroundResource", R.drawable.ic_pause_dark);
                expandedViews.setInt(R.id.loader, "setVisibility", View.GONE);
                expandedViews.setInt(R.id.toggle, "setVisibility", View.VISIBLE);
                break;
            case Statics.SERVICE_PREPARE:
                expandedViews.setInt(R.id.loader, "setVisibility", View.VISIBLE);
                expandedViews.setInt(R.id.toggle, "setVisibility", View.GONE);
                break;
        }
    }
}
