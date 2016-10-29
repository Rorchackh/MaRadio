package rorchackh.maradio.services;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.google.android.gms.cast.MediaInfo;
import com.google.android.gms.cast.MediaMetadata;
import com.google.android.gms.cast.framework.CastContext;
import com.google.android.gms.cast.framework.Session;
import com.google.android.gms.cast.framework.SessionManager;
import com.google.android.gms.cast.framework.SessionManagerListener;
import com.google.android.gms.cast.framework.media.RemoteMediaClient;
import com.google.android.gms.common.images.WebImage;

import rorchackh.maradio.libraries.Globals;
import rorchackh.maradio.libraries.Statics;


//Todo:
// Add support for Google cast mini player when app is in background
public class CastService implements SessionManagerListener {

    private Context context;
    private SessionManager mSessionManager;

    public CastService(Context context) {
        this.context = context;
        this.mSessionManager = CastContext.getSharedInstance(context).getSessionManager();
    }

    public void play () {
        MediaMetadata audioTrackInfo = new MediaMetadata(MediaMetadata.MEDIA_TYPE_MUSIC_TRACK);

        audioTrackInfo.putString(MediaMetadata.KEY_TITLE, Globals.currentStation.getTitle());
        audioTrackInfo.putString(MediaMetadata.KEY_ARTIST, Globals.currentStation.getSubtitle());

        // audioTrackInfo.putString(MediaMetadata.KEY_RELEASE_DATE, (new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'")).format(new Date()));
        audioTrackInfo.addImage(new WebImage(Uri.parse(Globals.currentStation.getImageLink())));

        MediaInfo mediaInfo = new MediaInfo.Builder(Globals.currentStation.getLink())
                .setStreamType(MediaInfo.STREAM_TYPE_LIVE)
                .setMetadata(audioTrackInfo)
                .setContentType("audio/mpeg")
                .build();

        PlayerService.broadCastMessage(this.context, Statics.SERVICE_PLAY, false);

        Globals.remoteMediaClient = mSessionManager.getCurrentCastSession().getRemoteMediaClient();
        Globals.remoteMediaClient.addListener(new RemoteMediaClient.Listener() {
            @Override
            public void onStatusUpdated() {
                if (Globals.remoteMediaClient.isBuffering()) {
                    PlayerService.broadCastMessage(context, Statics.SERVICE_PREPARE, false);
                }

                if (Globals.remoteMediaClient.isPlaying()) {
                    PlayerService.broadCastMessage(context, Statics.SERVICE_PLAY, false);
                }
            }

            @Override
            public void onMetadataUpdated() {
            }

            @Override
            public void onQueueStatusUpdated() {
            }

            @Override
            public void onPreloadStatusUpdated() {
            }

            @Override
            public void onSendingRemoteMediaRequest() {
            }
        });

        Globals.remoteMediaClient.load(mediaInfo, true, 0);
    }

    @Override
    public void onSessionStarting(Session session) {
        NotificationService.remove();
    }

    @Override
    public void onSessionStarted(Session session, String sessionId) {
        PlayerService.stop(context, Statics.SERVICE_STOP);

        if (context instanceof Activity) {
            ((Activity) context).invalidateOptionsMenu();
        }

        this.play();
    }

    @Override
    public void onSessionStartFailed(Session session, int i) {
        Log.d(Statics.debug, "onSessionStartFailed");
    }

    @Override
    public void onSessionEnding(Session session) {
        Log.d(Statics.debug, "onSessionEnding");
    }

    @Override
    public void onSessionResumed(Session session, boolean wasSuspended) {

        Log.d(Statics.debug, "onSessionResumed");
        if (context instanceof Activity) {
            ((Activity) context).invalidateOptionsMenu();
        }

    }

    @Override
    public void onSessionResumeFailed(Session session, int i) {
        Log.d(Statics.debug, "onSessionResumeFailed");
    }

    @Override
    public void onSessionSuspended(Session session, int i) {
        Log.d(Statics.debug, "onSessionSuspended");
    }

    @Override
    public void onSessionEnded(Session session, int error) {
        Log.d(Statics.debug, "onSessionEnded");
    }

    @Override
    public void onSessionResuming(Session session, String s) {
        Log.d(Statics.debug, "onSessionResuming");
    }

    public SessionManager getmSessionManager() {
        return mSessionManager;
    }
}