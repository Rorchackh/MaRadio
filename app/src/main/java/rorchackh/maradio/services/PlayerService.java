package rorchackh.maradio.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crash.FirebaseCrash;

import rorchackh.maradio.libraries.Globals;
import rorchackh.maradio.libraries.Statics;
import rorchackh.maradio.models.Station;

public class PlayerService extends Service implements AudioManager.OnAudioFocusChangeListener {

    private Station station;
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    public int onStartCommand(Intent intent, int flag, int startId) {
        if (intent == null) {
            return START_NOT_STICKY;
        }

        try {

            station = intent.getParcelableExtra(Statics.station);
            String action = intent.getStringExtra(Statics.SERVICE_ACTION);

            AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

            Log.d(Statics.debug, String.format("Player Service received %s", action));

            Globals.mediaPlayer.stop();
            Globals.mediaPlayer.reset();

            switch (action + "") {
                case Statics.SERVICE_PAUSE:
                case Statics.SERVICE_STOP:

                    broadCastMessage(this, action);
                    stopSelf();

                    am.abandonAudioFocus(this);

                    return START_NOT_STICKY;

                default:

                    Log.i("Played station: ", station.getLink());

                    int result = am.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
                    if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                        try {

                            Globals.currentStation = station;

                            broadCastMessage(this, Statics.SERVICE_PREPARE);

                            Globals.mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                            Globals.mediaPlayer.setDataSource(station.getLink());
                            Globals.mediaPlayer.prepareAsync();

                            Globals.mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                @Override
                                public void onPrepared(MediaPlayer player) {


                                    broadCastMessage(PlayerService.this, Statics.SERVICE_PLAY);
                                    player.start();

                                    Bundle bundle = new Bundle();
                                    mFirebaseAnalytics = FirebaseAnalytics.getInstance(PlayerService.this);

                                    bundle.putString(FirebaseAnalytics.Param.ITEM_ID, String.valueOf(station.getId()));
                                    bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, station.toString());
                                    bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, Statics.station);

                                    mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                                }
                            });

                        } catch (Exception e) {
                            FirebaseCrash.report(e);
                            broadCastMessage(this, Statics.SERVICE_PAUSE);

                            Log.e(Statics.debug, "Exception: " + e.getMessage());
                            e.printStackTrace();
                        }

                    } else if (result == AudioManager.AUDIOFOCUS_REQUEST_FAILED) {
                        Log.e(Statics.debug, "Cannot get audio focus from other apps");
                    }

                    return START_STICKY;
            }

        } catch (Exception ex) {
            FirebaseCrash.report(ex);
            return START_NOT_STICKY;
        }
    }

    private void broadCastMessage(Context context, String msg) {
        LocalBroadcastManager broadcaster = LocalBroadcastManager.getInstance(context);

        Intent intent = new Intent(Statics.SERVICE_MESSAGE);

        intent.putExtra(Statics.SERVICE_MESSAGE, msg);
        intent.putExtra(Statics.station, station);

        Log.d(Statics.debug, "Broadcasting from service: " + msg);

        broadcaster.sendBroadcast(intent);
    }

    public static void play(Context context, Station station) {
        Intent playerService = new Intent(context, PlayerService.class);
        playerService.putExtra(Statics.station, station);

        context.startService(playerService);
    }

    public static void stop(Context context, String action) {
        Intent playerService = new Intent(context, PlayerService.class);
        playerService.putExtra(Statics.SERVICE_ACTION, action);

        context.startService(playerService);
    }

    public static void seek(Context context, String action) {
        LocalBroadcastManager broadcaster = LocalBroadcastManager.getInstance(context);

        Intent newIntent = new Intent(Statics.SERVICE_MESSAGE);
        newIntent.putExtra(Statics.SERVICE_MESSAGE, action);

        broadcaster.sendBroadcast(newIntent);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent){
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
        Log.d(Statics.debug, String.format("the service received %d focus change", i));
        switch (i) {
            case AudioManager.AUDIOFOCUS_GAIN:
                Globals.mediaPlayer.start();
                Log.d(Statics.debug, "AUDIOFOCUS_GAIN");
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                Log.d(Statics.debug, "AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK");
                Globals.mediaPlayer.pause();
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
                stop(this, Statics.SERVICE_PAUSE);
                break;
        }
    }
}
