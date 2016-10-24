package rorchackh.maradio.activities;

import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;

import rorchackh.maradio.R;
import rorchackh.maradio.libraries.Globals;
import rorchackh.maradio.models.Station;
import rorchackh.maradio.receivers.HeadsetReceiver;
import rorchackh.maradio.receivers.StationManagerReceiver;

public class BaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    protected ArrayList<Station> stations = null;
    protected FirebaseAnalytics mFirebaseAnalytics;
    protected LocalBroadcastManager broadCastManager;
    protected boolean isFav;

    protected void onCreate(Bundle savedInstanceState, boolean isFav) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isLightTheme = sharedPreferences.getBoolean(getString(R.string.pref_key_light), false);

        if (isLightTheme) {
            String classIO = this.getClass().toString();
            setTheme(classIO.endsWith("AllRadiosActivity") ? R.style.MaRadioLightTheme_NoActionBar : R.style.MaRadioLightTheme);
        }

        stations = isFav ? StationManagerReceiver.getFavs(this) : StationManagerReceiver.getAll(this);

        ComponentName component = new ComponentName(this, HeadsetReceiver.class);
        AudioManager am = ((AudioManager) getSystemService(AUDIO_SERVICE));
        am.unregisterMediaButtonEventReceiver(component);
        am.registerMediaButtonEventReceiver(component);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        if (Globals.imageLoader == null) {
            DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .build();
            ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                    .diskCacheExtraOptions(480, 800, null)
                    .defaultDisplayImageOptions(defaultOptions)
                    .diskCacheSize(50 * 1024 * 1024)
                    .diskCacheFileCount(100)
                    .build();
            Globals.imageLoader = ImageLoader.getInstance();
            Globals.imageLoader.init(config);
        }

        broadCastManager = LocalBroadcastManager.getInstance(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings:
                Intent intent = new Intent(this, PreferencesActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_about:
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_all:
                startActivity(new Intent(this, AllRadiosActivity.class));
                break;
            case R.id.nav_favorites:
                startActivity(new Intent(this, FavRadiosActivity.class));
                break;
            case R.id.nav_share:

                String playStoreLink = "https://play.google.com/store/apps/details?id=" + getPackageName();
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, playStoreLink);

                startActivity(Intent.createChooser(intent, getString(R.string.nav_share)));

                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
