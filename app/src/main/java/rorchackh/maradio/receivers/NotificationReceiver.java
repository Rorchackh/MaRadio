package rorchackh.maradio.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import rorchackh.maradio.libraries.Statics;
import rorchackh.maradio.models.Station;
import rorchackh.maradio.services.PlayerService;

// Todo: this event handler only works from the context of the app. Fix this.
// Todo: This apply to all receivers not just this one.
public class NotificationReceiver extends BroadcastReceiver {

    public NotificationReceiver() {
        super();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getStringExtra(Statics.SERVICE_MESSAGE);
        Station station = intent.getParcelableExtra(Statics.station);

        Log.d(Statics.debug, "Received action from notification: " + action);
        switch (action) {
            case Statics.SERVICE_STOP:
            case Statics.SERVICE_PAUSE:
                PlayerService.stop(context, action);
                break;
            case Statics.SERVICE_PLAY:
                PlayerService.play(context, station);
                break;

            case Statics.SERVICE_PREV:
            case Statics.SERVICE_NEXT:
                PlayerService.seek(context, action);
                break;
        }
    }
}