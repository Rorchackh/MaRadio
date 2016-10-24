package rorchackh.maradio.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.crash.FirebaseCrash;

import rorchackh.maradio.libraries.Globals;
import rorchackh.maradio.libraries.Statics;

public class PlayerService extends Service implements AudioManager.OnAudioFocusChangeListener {

    @Override
    public int onStartCommand(Intent intent, int flag, int startId) {
        if (intent == null) {
            return START_NOT_STICKY;
        }

        AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int result = am.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            try {

                Globals.mediaPlayer.stop();
                Globals.mediaPlayer.reset();

                broadCastMessage(this, Statics.SERVICE_PREPARE);
                NotificationManager.show(this, Statics.SERVICE_PREPARE);

                Globals.mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                Globals.mediaPlayer.setDataSource(Globals.currentStation.getLink());
                Globals.mediaPlayer.prepareAsync();

                Globals.mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer player) {
                        broadCastMessage(PlayerService.this, Statics.SERVICE_PLAY);
                        NotificationManager.show(PlayerService.this, Statics.SERVICE_PLAY);
                        player.start();
                    }
                });

                return START_STICKY;

            } catch (Exception ex) {
                FirebaseCrash.report(ex);
                return START_NOT_STICKY;
            }
        } else {
            return START_NOT_STICKY;
        }
    }

    private static void broadCastMessage(Context context, String msg) {
        LocalBroadcastManager broadcaster = LocalBroadcastManager.getInstance(context);

        Intent intent = new Intent(Statics.SERVICE_MESSAGE);
        intent.putExtra(Statics.SERVICE_MESSAGE, msg);

        Log.d(Statics.debug, "Broadcasting from service: " + msg);

        broadcaster.sendBroadcast(intent);
    }

    public static void play(Context context) {
        boolean isPaused = !Globals.mediaPlayer.isPlaying() && Globals.mediaPlayer.getCurrentPosition() > 1;
        if (isPaused) {
            Globals.mediaPlayer.start();
            broadCastMessage(context, Statics.SERVICE_PLAY);
            NotificationManager.show(context, Statics.SERVICE_PLAY);
        } else {
            Intent playerService = new Intent(context, PlayerService.class);
            context.startService(playerService);
        }
    }

    public static void stop(Context context, String action) {
        broadCastMessage(context, action);

        switch (action) {
            case Statics.SERVICE_STOP:

                Globals.mediaPlayer.stop();
                Globals.mediaPlayer.reset();

                NotificationManager.remove();
                break;

            case Statics.SERVICE_PAUSE:

                Globals.mediaPlayer.pause();
                NotificationManager.show(context, action);
                break;
        }
    }

    public static void seek(Context context, String action) {

        int index = Globals.stationList.indexOf(Globals.currentStation);

        try {

            switch (action) {
                case Statics.SERVICE_PREV:
                    Globals.currentStation = Globals.stationList.get(--index);
                    break;
                case Statics.SERVICE_NEXT:
                    Globals.currentStation = Globals.stationList.get(++index);
                    break;
            }

            play(context);

        } catch (IndexOutOfBoundsException ex) {
        }
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        NotificationManager.remove();
        super.onTaskRemoved(rootIntent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onAudioFocusChange(int i) {
        switch (i) {
            case AudioManager.AUDIOFOCUS_GAIN:
                Globals.mediaPlayer.start();
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                Globals.mediaPlayer.pause();
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
                PlayerService.stop(this, Statics.SERVICE_PAUSE);
                break;
        }
    }
}
