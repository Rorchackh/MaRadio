package rorchackh.maradio.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.crash.FirebaseCrash;

import rorchackh.maradio.R;
import rorchackh.maradio.libraries.Globals;
import rorchackh.maradio.libraries.Statics;
import rorchackh.maradio.models.Station;
import rorchackh.maradio.services.NotificationManager;
import rorchackh.maradio.services.PlayerService;

public class PlayerActivity extends BaseActivity implements GestureDetector.OnGestureListener {
    private GestureDetector gestureDetector;

    private Station currentStation;
    private int currentStationIndex;
    private boolean isLightTheme;

    private TextView UITitle;
    private TextView UISubTitle;
    private ImageView UIImg;

    private Button prevButton;
    private Button nextButton;
    private Button playButton;
    private ProgressBar loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        currentStation = intent.getParcelableExtra(Statics.station);
        boolean isFav = intent.getBooleanExtra(Statics.isFav, false);

        if (currentStation == null) {
            currentStation = Globals.currentStation;
        }

        super.onCreate(savedInstanceState, isFav);
        setContentView(R.layout.content_player);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        isLightTheme = sharedPreferences.getBoolean(getString(R.string.pref_key_light), false);
        gestureDetector = new GestureDetector(this, this);

        currentStationIndex = stations.indexOf(currentStation);

        setUIElements();
        adjustImageHeight();
        setEvents();

        receiver = createBroadcastReceiver();

        playStation();
    }

    private void setEvents() {
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Globals.mediaPlayer.isPlaying()) {
                    PlayerService.stop(PlayerActivity.this, Statics.SERVICE_PAUSE);
                } else {
                    playStation();
                }
            }
        });
    }

    private void setUIElements() {
        UITitle = (TextView) findViewById(R.id.title_text);
        UISubTitle = (TextView) findViewById(R.id.subtitle_text);
        UIImg = (ImageView) findViewById(R.id.thumbnail_image);
        prevButton = (Button) findViewById(R.id.prevButton);
        nextButton = (Button) findViewById(R.id.nextButton);
        playButton = (Button) findViewById(R.id.playButton);
        loader = (ProgressBar) findViewById(R.id.loader);
    }

    private void adjustImageHeight() {

        UIImg.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {

                UIImg.getViewTreeObserver().removeOnPreDrawListener(this);
                UIImg.getLayoutParams().width = UIImg.getMeasuredHeight();
                UIImg.requestLayout();

                return true;
            }
        });
    }

    @NonNull
    protected BroadcastReceiver createBroadcastReceiver() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                int pause = R.drawable.ic_pause_large_dark;
                int play = R.drawable.ic_play_large_dark;

                if (isLightTheme) {
                    pause = R.drawable.ic_pause_large_light;
                    play = R.drawable.ic_play_large_light;
                }

                String s = intent.getStringExtra(Statics.SERVICE_MESSAGE);
                Log.d(Statics.debug, "The player activity receives the message: " + s);

                boolean hasPrev = currentStationIndex > 0;
                boolean hasNext = currentStationIndex < stations.size() - 1;

                switch (s) {
                    case Statics.SERVICE_PREPARE:
                        loader.setVisibility(View.VISIBLE);
                        playButton.setVisibility(View.GONE);

                        NotificationManager.show(getBaseContext(), currentStation, s, hasNext, hasPrev);
                        break;

                    case Statics.SERVICE_PLAY:
                        loader.setVisibility(View.GONE);
                        playButton.setVisibility(View.VISIBLE);
                        playButton.setBackgroundResource(pause);

                        NotificationManager.show(getBaseContext(), currentStation, s, hasNext, hasPrev);
                        break;

                    case Statics.SERVICE_PAUSE:
                        loader.setVisibility(View.GONE);
                        playButton.setVisibility(View.VISIBLE);
                        playButton.setBackgroundResource(play);

                        NotificationManager.show(getBaseContext(), currentStation, s, hasNext, hasPrev);
                        break;

                    case Statics.SERVICE_STOP:
                        loader.setVisibility(View.GONE);
                        playButton.setVisibility(View.VISIBLE);
                        playButton.setBackgroundResource(play);

                        NotificationManager.remove();
                        break;

                    case Statics.SERVICE_NEXT:
                        playNext(null);
                        break;
                    case Statics.SERVICE_PREV:
                        playPrev(null);
                        break;
                }
            }
        };
    }

    private void applyUI() {

        UITitle.setText(currentStation.getTitle());

        UISubTitle.setText(currentStation.getSubtitle());
        // UISubTitle.setVisibility(currentStation.getSubtitle().isEmpty() ? View.GONE : View.VISIBLE);

        String image = currentStation.getImageLink();
        if (image.equals("")) {
            UIImg.setBackgroundResource(R.drawable.none);
        } else {
            Globals.imageLoader.displayImage(image, UIImg);
        }

        prevButton.setEnabled(currentStationIndex > 0);
        nextButton.setEnabled(currentStationIndex < stations.size() - 1);

        int pause = R.drawable.ic_pause_large_dark;
        int play = R.drawable.ic_play_large_dark;
        int liked = R.drawable.ic_nav_fav_dark;

        if (isLightTheme) {
            pause = R.drawable.ic_pause_large_light;
            play = R.drawable.ic_play_large_light;
            liked = R.drawable.ic_nav_fav_light;
        }


        playButton.setBackgroundResource(Globals.mediaPlayer.isPlaying() ? pause : play);
    }

    public void playNext(View v) {
        try {
            currentStation = stations.get(++currentStationIndex);

            applyUI();
            playStation();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void playPrev(View v) {
        try {
            currentStation = stations.get(--currentStationIndex);

            applyUI();
            playStation();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void playStation() {
        if (Globals.mediaPlayer.isPlaying() && currentStation.equals(Globals.currentStation)) {
            return;
        }

        PlayerService.play(this, currentStation);
    }

    @Override
    protected void onResume() {
        super.onResume();
        applyUI();
    }

    @Override
    public void onStop() {
        Log.e(Statics.debug, "Player unregisterReceiver");
        notificationBroadCaster.unregisterReceiver(receiver);

        Log.e(Statics.debug, "Base registerReceiver");
        notificationBroadCaster.registerReceiver(
            super.receiver,
            new IntentFilter(Statics.SERVICE_MESSAGE)
        );

        super.onStop();
    }

    public void onStart() {
        // Todo: This is not perfect. It is still caught twice
        Log.e(Statics.debug, "Base unregisterReceiver");
        Log.e(Statics.debug, "Player registerReceiver");

        notificationBroadCaster.unregisterReceiver(super.receiver);
        notificationBroadCaster.unregisterReceiver(receiver);
        notificationBroadCaster.registerReceiver(
                receiver,
                new IntentFilter(Statics.SERVICE_MESSAGE)
        );

        super.onStart();
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float v1, float v2) {
        try {
            if (Math.abs(e1.getY() - e2.getY()) > 250)
                return false;
            if (e1.getX() - e2.getX() > 120 && Math.abs(v1) > 200) {
                playNext(null);
            } else if (e2.getX() - e1.getX() > 120 && Math.abs(v2) > 200) {
                playPrev(null);
            }

            return false;

        } catch (Exception e) {
            FirebaseCrash.report(e);
            return false;
        }
    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return true;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {
    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return true;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return true;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

}
