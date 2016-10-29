package rorchackh.maradio.libraries;

import android.app.NotificationManager;
import android.media.MediaPlayer;

import com.google.android.gms.cast.framework.media.RemoteMediaClient;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

import rorchackh.maradio.models.Station;

public class Globals {

    public static NotificationManager notificationManager;
    public static ImageLoader imageLoader = null;

    public static MediaPlayer mediaPlayer = new MediaPlayer();
    public static RemoteMediaClient remoteMediaClient;

    public static Station currentStation;
    public static ArrayList<Station> stationList;
}
