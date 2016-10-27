package rorchackh.maradio.receivers;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;

import rorchackh.maradio.libraries.Globals;
import rorchackh.maradio.libraries.Statics;
import rorchackh.maradio.services.PlayerService;

public class HeadsetReceiver extends android.content.BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();
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
            if (event == null || event.getAction() != KeyEvent.ACTION_DOWN) {
                return;
            }
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_MEDIA_PLAY:
                    PlayerService.play(context, false);
                    break;
                case KeyEvent.KEYCODE_MEDIA_STOP:
                    PlayerService.stop(context, Statics.SERVICE_STOP);
                    break;
                case KeyEvent.KEYCODE_HEADSETHOOK:
                case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:

                    if (Globals.mediaPlayer.isPlaying()) {
                        PlayerService.stop(context, Statics.SERVICE_PAUSE);
                    } else {
                        PlayerService.play(context, false);
                    }
                    break;
                case KeyEvent.KEYCODE_MEDIA_PAUSE:
                    PlayerService.stop(context, Statics.SERVICE_PAUSE);
                    break;
                case KeyEvent.KEYCODE_MEDIA_NEXT:
                    PlayerService.seek(context, Statics.SERVICE_NEXT);
                    break;
                case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                    PlayerService.seek(context, Statics.SERVICE_PREV);
                    break;
            }

            if (isOrderedBroadcast()) {
                abortBroadcast();
            }
        }

    }
}
