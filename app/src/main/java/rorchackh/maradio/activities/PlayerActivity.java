package rorchackh.maradio.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
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
import rorchackh.maradio.services.PlayerService;

public class PlayerActivity extends BaseActivity implements GestureDetector.OnGestureListener {
    private GestureDetector gestureDetector;

    private Station currentStation;
    private boolean isLightTheme;

    private TextView UITitle;
    private TextView UISubTitle;
    private ImageView UIImg;

    private Button playButton;
    private ProgressBar loader;

    private BroadcastReceiver uiBroadCastReceiver;
    private LocalBroadcastManager broadCastManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();

        currentStation = intent.getParcelableExtra(Statics.station);
        Globals.stationList = stations = intent.getParcelableArrayListExtra(Statics.stations);

        super.onCreate(savedInstanceState, intent.getBooleanExtra(Statics.isFav, false));
        setContentView(R.layout.content_player);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        isLightTheme = sharedPreferences.getBoolean(getString(R.string.pref_key_light), false);
        gestureDetector = new GestureDetector(this, this);

        setUIElements();
        adjustImageHeight();
        setEvents();

        playStation();

        uiBroadCastReceiver = createUIBroadcastReceiver();
        broadCastManager = LocalBroadcastManager.getInstance(this);
    }

    private void setEvents() {
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Globals.mediaPlayer.isPlaying()) {
                    PlayerService.stop(PlayerActivity.this, Statics.SERVICE_PAUSE);
                } else {
                    PlayerService.play(PlayerActivity.this, false);
                }
            }
        });
    }

    private void setUIElements() {
        UITitle = (TextView) findViewById(R.id.title_text);
        UISubTitle = (TextView) findViewById(R.id.subtitle_text);
        UIImg = (ImageView) findViewById(R.id.thumbnail_image);
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

    private BroadcastReceiver createUIBroadcastReceiver() {
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

                currentStation = Globals.currentStation;
                applyUI();

                switch (s) {
                    case Statics.SERVICE_PREPARE:
                        loader.setVisibility(View.VISIBLE);
                        playButton.setVisibility(View.GONE);

                        break;

                    case Statics.SERVICE_PLAY:
                        loader.setVisibility(View.GONE);
                        playButton.setVisibility(View.VISIBLE);
                        playButton.setBackgroundResource(pause);

                        break;

                    case Statics.SERVICE_PAUSE:
                        loader.setVisibility(View.GONE);
                        playButton.setVisibility(View.VISIBLE);
                        playButton.setBackgroundResource(play);

                        break;

                    case Statics.SERVICE_STOP:
                        loader.setVisibility(View.GONE);
                        playButton.setVisibility(View.VISIBLE);
                        playButton.setBackgroundResource(play);

                        break;
                }
            }
        };
    }

    private void applyUI() {
        loader.setVisibility(View.GONE);
        playButton.setVisibility(View.VISIBLE);

        UITitle.setText(currentStation.getTitle());
        UISubTitle.setText(currentStation.getSubtitle());

        String image = currentStation.getImageLink();

        if (image == null || image.equals("")) {
            UIImg.setBackgroundResource(R.drawable.none);
        } else {
            Globals.imageLoader.displayImage(image, UIImg);
        }

        int pause = R.drawable.ic_pause_large_dark;
        int play = R.drawable.ic_play_large_dark;

        if (isLightTheme) {
            pause = R.drawable.ic_pause_large_light;
            play = R.drawable.ic_play_large_light;
        }

        playButton.setBackgroundResource(Globals.mediaPlayer.isPlaying() ? pause : play);
        playButton.setVisibility(View.VISIBLE);
        loader.setVisibility(View.GONE);
    }

    public void playNext(View v) {
        PlayerService.seek(this, Statics.SERVICE_NEXT);
    }

    public void playPrev(View v) {
        PlayerService.seek(this, Statics.SERVICE_PREV);
    }

    private void playStation() {

        if (Globals.mediaPlayer.isPlaying() && currentStation.equals(Globals.currentStation)) {
            return;
        }

        Globals.currentStation = currentStation;
        PlayerService.play(this, false);
    }

    @Override
    protected void onResume() {
        super.onResume();

        currentStation = Globals.currentStation;
        applyUI();

        broadCastManager.registerReceiver(
            uiBroadCastReceiver,
            new IntentFilter(Statics.SERVICE_MESSAGE)
        );
    }

    @Override
    protected void onStop () {
        broadCastManager.unregisterReceiver(uiBroadCastReceiver);
        super.onStop();
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
