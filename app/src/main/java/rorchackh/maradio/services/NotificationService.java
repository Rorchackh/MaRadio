package rorchackh.maradio.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.google.firebase.crash.FirebaseCrash;

import rorchackh.maradio.R;
import rorchackh.maradio.activities.PlayerActivity;
import rorchackh.maradio.libraries.Globals;
import rorchackh.maradio.libraries.Statics;

public class NotificationService {

    static void show(Context context, String status) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

        Intent intent = new Intent(context, PlayerActivity.class);
        intent.putExtra(Statics.station, Globals.currentStation);
        intent.putExtra(Statics.stations, Globals.stationList);
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

        applyUI(contentView, expandedView, status);
        applyEvents(context, contentView, expandedView, status);

        if (Globals.notificationManager == null) {
            Globals.notificationManager = (android.app.NotificationManager) context.getSystemService(Service.NOTIFICATION_SERVICE);
        }

        Globals.notificationManager.notify(Statics.notificationId, notification);
    }

    static void remove() {
        if (Globals.notificationManager != null) {
            Globals.notificationManager.cancel(Statics.notificationId);
        }
    }

    private static void applyEvents(Context context, RemoteViews contentView, RemoteViews expandedViews, String status) {

        Intent stopIntent = new Intent(Statics.SERVICE_MESSAGE);
        stopIntent.putExtra(Statics.SERVICE_MESSAGE, Statics.SERVICE_STOP);
        PendingIntent stopPendingIntent = PendingIntent.getBroadcast(context, 0, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        expandedViews.setOnClickPendingIntent(R.id.stop, stopPendingIntent);
        contentView.setOnClickPendingIntent(R.id.stop, stopPendingIntent);

        Intent nextIntent = new Intent(Statics.SERVICE_MESSAGE);
        nextIntent.putExtra(Statics.SERVICE_MESSAGE, Statics.SERVICE_NEXT);
        PendingIntent nextPendingIntent = PendingIntent.getBroadcast(context, 1, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        int index = Globals.stationList.indexOf(Globals.currentStation);
        if (index < Globals.stationList.size() - 1) {
            expandedViews.setOnClickPendingIntent(R.id.next, nextPendingIntent);

            expandedViews.setInt(R.id.next_enabled, "setVisibility", View.VISIBLE);
            expandedViews.setInt(R.id.next_disabled, "setVisibility", View.GONE);

        } else {
            expandedViews.setOnClickPendingIntent(R.id.next, null);

            expandedViews.setInt(R.id.next_enabled, "setVisibility", View.GONE);
            expandedViews.setInt(R.id.next_disabled, "setVisibility", View.VISIBLE);

            nextPendingIntent.cancel();
        }

        Intent prevIntent = new Intent(Statics.SERVICE_MESSAGE);
        prevIntent.putExtra(Statics.SERVICE_MESSAGE, Statics.SERVICE_PREV);
        PendingIntent prevPendingIntent = PendingIntent.getBroadcast(context, 2, prevIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        if (index > 0) {
            expandedViews.setOnClickPendingIntent(R.id.prev, prevPendingIntent);

            expandedViews.setInt(R.id.prev_enabled, "setVisibility", View.VISIBLE);
            expandedViews.setInt(R.id.prev_disabled, "setVisibility", View.GONE);
        } else {
            expandedViews.setOnClickPendingIntent(R.id.prev, null);
            prevPendingIntent.cancel();

            expandedViews.setInt(R.id.prev_enabled, "setVisibility", View.GONE);
            expandedViews.setInt(R.id.prev_disabled, "setVisibility", View.VISIBLE);
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
        pauseIntent.putExtra(Statics.station, Globals.currentStation);
        PendingIntent pausePendingIntent = PendingIntent.getBroadcast(context, 3, pauseIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        expandedViews.setOnClickPendingIntent(R.id.toggle_container, pausePendingIntent);
    }

    private static void applyUI(RemoteViews contentView, RemoteViews expandedViews, String status) {
         contentView.setTextViewText(R.id.notification_title, Globals.currentStation.toString());

        String url = Globals.currentStation.getImageLink();

        contentView.setImageViewResource(R.id.notification_image_small, R.drawable.none);
        expandedViews.setImageViewResource(R.id.notification_image_big, R.drawable.none);
        if (url  != null && !url.equals("")) {

            try {

                Bitmap image = Globals.imageLoader.loadImageSync(url);

                contentView.setImageViewBitmap(R.id.notification_image_small, image);
                expandedViews.setImageViewBitmap(R.id.notification_image_big, image);

            } catch (Exception ignored) {
                FirebaseCrash.report(ignored);
            }
        }

        expandedViews.setTextViewText(R.id.title, Globals.currentStation.getTitle());
        expandedViews.setTextViewText(R.id.subtitle, Globals.currentStation.getSubtitle());

        switch (status){
            case Statics.SERVICE_PAUSE:
                expandedViews.setInt(R.id.toggle_bg, "setBackgroundResource", R.drawable.ic_play_dark);

                expandedViews.setInt(R.id.loader, "setVisibility", View.GONE);
                expandedViews.setInt(R.id.toggle, "setVisibility", View.VISIBLE);
                expandedViews.setInt(R.id.toggle_bg, "setVisibility", View.VISIBLE);
                break;
            case Statics.SERVICE_PLAY:
                expandedViews.setInt(R.id.toggle_bg, "setBackgroundResource", R.drawable.ic_pause_dark);

                expandedViews.setInt(R.id.loader, "setVisibility", View.GONE);
                expandedViews.setInt(R.id.toggle, "setVisibility", View.VISIBLE);
                expandedViews.setInt(R.id.toggle_bg, "setVisibility", View.VISIBLE);
                break;
            case Statics.SERVICE_PREPARE:
                expandedViews.setInt(R.id.loader, "setVisibility", View.VISIBLE);
                expandedViews.setInt(R.id.toggle, "setVisibility", View.GONE);
                expandedViews.setInt(R.id.toggle_bg, "setVisibility", View.GONE);
                break;
        }
    }
}
