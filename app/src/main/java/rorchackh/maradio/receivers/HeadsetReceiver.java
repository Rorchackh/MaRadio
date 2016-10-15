package rorchackh.maradio.receivers;

import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;

import rorchackh.maradio.libraries.Globals;
import rorchackh.maradio.libraries.Statics;
import rorchackh.maradio.services.PlayerService;

public class HeadsetReceiver extends android.content.BroadcastReceiver {

    private static final long CLICK_DELAY = 500;

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();

        Log.d(Statics.debug, "Headset received the action:" + action);

        /**
         *
         * First this stops when the the headset is removed.
         *
         */
        if (action.equals(android.media.AudioManager.ACTION_AUDIO_BECOMING_NOISY)) {
            PlayerService.stop(context, Statics.SERVICE_PAUSE);
            return;
        }

        /**
         * This handles the headset clicks
         *
         */
        if (action.equals(Intent.ACTION_MEDIA_BUTTON)) {

            KeyEvent event = intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
            if (event == null) {
                return;
            }

            if (event.getKeyCode() == KeyEvent.KEYCODE_HEADSETHOOK) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    long lastClick = Globals.currentClick;
                    Globals.currentClick = System.currentTimeMillis();

                    if (Globals.currentClick - lastClick > CLICK_DELAY) {

                        if (Globals.mediaPlayer.isPlaying()) {
                            PlayerService.stop(context, Statics.SERVICE_PAUSE);
                        } else {

                            if (Globals.currentStation == null) {
                                Globals.currentStation = StationManagerReceiver.getFirst(context);
                            }
                            PlayerService.play(context, Globals.currentStation);
                        }

                    } else {
                        PlayerService.seek(context, Statics.SERVICE_NEXT);
                    }

                // todo: change from long click to tripple click
                } else if (event.getAction() == KeyEvent.ACTION_UP) {
                    if (SystemClock.uptimeMillis() - event.getDownTime() > 2000) {
                        PlayerService.seek(context, Statics.SERVICE_PREV);
                    }
                }

            }

            if (isOrderedBroadcast()) {
                abortBroadcast();
            }
        }

    }
}
