package rorchackh.maradio.libraries;

import android.app.NotificationManager;
import android.media.MediaPlayer;

import com.nostra13.universalimageloader.core.ImageLoader;

import rorchackh.maradio.models.Station;

public class Globals {

    public static NotificationManager notificationManager;
    public static ImageLoader imageLoader = null;
    public static MediaPlayer mediaPlayer = new MediaPlayer();

    public static Station currentStation;
    public static long currentClick = 0;
}
