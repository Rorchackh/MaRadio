package rorchackh.maradio.cast;

import android.app.Activity;
import android.net.Uri;
import android.util.Log;

import com.google.android.gms.cast.MediaInfo;
import com.google.android.gms.cast.MediaMetadata;
import com.google.android.gms.cast.framework.Session;
import com.google.android.gms.cast.framework.SessionManager;
import com.google.android.gms.cast.framework.SessionManagerListener;
import com.google.android.gms.cast.framework.media.RemoteMediaClient;
import com.google.android.gms.common.images.WebImage;

import java.util.Calendar;

import rorchackh.maradio.libraries.Globals;
import rorchackh.maradio.libraries.Statics;
import rorchackh.maradio.services.PlayerService;

public class SessionManagerListenerImpl implements SessionManagerListener {

    private Activity callingActivity;
    private SessionManager mSessionManager;

    public SessionManagerListenerImpl(Activity activity, SessionManager mSessionManager) {
        this.callingActivity = activity;
        this.mSessionManager = mSessionManager;
    }

    @Override
    public void onSessionStarting(Session session) {
        Log.d(Statics.debug, "onSessionStarting");
    }

    @Override
    public void onSessionStarted(Session session, String sessionId) {
        if (Globals.currentStation == null) {
            // Todo: show the user an error message
            return;
        }

        PlayerService.stop(callingActivity, Statics.SERVICE_STOP);

        callingActivity.invalidateOptionsMenu();
        Log.d(Statics.debug, "onSessionStarted");

        MediaMetadata audioTrackInfo = new MediaMetadata(MediaMetadata.MEDIA_TYPE_MUSIC_TRACK);

        audioTrackInfo.putString(MediaMetadata.KEY_TITLE, Globals.currentStation.getTitle());
        audioTrackInfo.putString(MediaMetadata.KEY_ARTIST, Globals.currentStation.getSubtitle());
        audioTrackInfo.putString(MediaMetadata.KEY_RELEASE_DATE, String.valueOf(Calendar.getInstance().get(Calendar.YEAR)));
        audioTrackInfo.addImage(new WebImage(Uri.parse(Globals.currentStation.getImageLink())));

        MediaInfo mediaInfo = new MediaInfo.Builder(Globals.currentStation.getLink())
                .setStreamType(MediaInfo.STREAM_TYPE_LIVE)
                .setMetadata(audioTrackInfo)
                .setContentType("audio/mpeg")
                .build();

        RemoteMediaClient remoteMediaClient = mSessionManager.getCurrentCastSession().getRemoteMediaClient();
        remoteMediaClient.load(mediaInfo, true, 0);
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
        this.callingActivity.invalidateOptionsMenu();
        Log.d(Statics.debug, "onSessionResumed");
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
}