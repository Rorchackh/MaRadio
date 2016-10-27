package rorchackh.maradio.receivers;

import android.content.Context;
import android.content.Intent;

import rorchackh.maradio.libraries.Statics;
import rorchackh.maradio.services.PlayerService;

public class NotificationReceiver extends android.content.BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();
        String option = intent.getStringExtra(Statics.SERVICE_MESSAGE);

        if (action.equals(Statics.SERVICE_MESSAGE)) {

            switch (option) {
                case Statics.SERVICE_PAUSE:
                case Statics.SERVICE_STOP:
                    PlayerService.stop(context, option);
                    break;

                case Statics.SERVICE_NEXT:
                case Statics.SERVICE_PREV:
                    PlayerService.seek(context, option);
                    break;

                case Statics.SERVICE_PLAY:
                    PlayerService.play(context, false);
                    break;
            }

            if (isOrderedBroadcast()) {
                abortBroadcast();
            }
        }

    }
}
