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

import com.google.android.gms.cast.framework.CastContext;
import com.google.android.gms.cast.framework.CastSession;
import com.google.android.gms.cast.framework.SessionManager;
import com.google.android.gms.cast.framework.media.RemoteMediaClient;
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

                broadCastMessage(this, Statics.SERVICE_PREPARE, true);

                Globals.mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                Globals.mediaPlayer.setDataSource(Globals.currentStation.getLink());
                Globals.mediaPlayer.prepareAsync();

                Globals.mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer player) {
                        broadCastMessage(PlayerService.this, Statics.SERVICE_PLAY, true);
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

    public static void broadCastMessage(Context context, String msg, boolean inludeNotification) {
        if (inludeNotification) {
            switch (msg) {
                case Statics.SERVICE_STOP:
                    NotificationService.remove();
                    break;
                default:
                    NotificationService.show(context, msg);
            }
        }

        LocalBroadcastManager broadcaster = LocalBroadcastManager.getInstance(context);

        Intent intent = new Intent(Statics.SERVICE_MESSAGE);
        intent.putExtra(Statics.SERVICE_MESSAGE, msg);

        Log.d(Statics.debug, "Broadcasting from service: " + msg);

        broadcaster.sendBroadcast(intent);
    }

    public static void play(Context context, boolean isSeeking) {

        CastSession session = CastContext.getSharedInstance(context).getSessionManager().getCurrentCastSession();
        if (session == null) {
            boolean isPaused = !Globals.mediaPlayer.isPlaying() && Globals.mediaPlayer.getCurrentPosition() > 1000;
            if (isPaused && !isSeeking) {
                Globals.mediaPlayer.start();
                broadCastMessage(context, Statics.SERVICE_PLAY, true);
            } else {
                Intent playerService = new Intent(context, PlayerService.class);
                context.startService(playerService);
            }
        } else {
            Globals.remoteMediaClient = session.getRemoteMediaClient();
            boolean isPlaying = Globals.remoteMediaClient.isPlaying();

            if (!isPlaying && !isSeeking) {
                Globals.remoteMediaClient.play();
                broadCastMessage(context, Statics.SERVICE_PLAY, true);
            } else {
                CastService sessionManagerImpl = new CastService(context);
                sessionManagerImpl.play();
            }

        }
    }

    public static void stop(Context context, String action) {

        CastSession session = CastContext.getSharedInstance(context).getSessionManager().getCurrentCastSession();
        if (session == null) {
            switch (action) {
                case Statics.SERVICE_STOP:
                    Globals.mediaPlayer.stop();
                    Globals.mediaPlayer.reset();

                    break;

                case Statics.SERVICE_PAUSE:
                    Globals.mediaPlayer.pause();
                    break;
            }
        } else {

            RemoteMediaClient remoteMediaClient = session.getRemoteMediaClient();
            switch (action) {
                case Statics.SERVICE_STOP:
                    remoteMediaClient.stop();
                    break;
                case Statics.SERVICE_PAUSE:
                    remoteMediaClient.pause();
                    break;
            }
        }

        broadCastMessage(context, action, session == null);
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

            play(context, true);

        } catch (IndexOutOfBoundsException ignored) {
        }
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {

        NotificationService.remove();
        Globals.mediaPlayer.release();

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
