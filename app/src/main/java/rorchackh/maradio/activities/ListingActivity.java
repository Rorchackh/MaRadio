package rorchackh.maradio.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.LinearLayout;

import rorchackh.maradio.R;
import rorchackh.maradio.drawables.ListItem;
import rorchackh.maradio.libraries.Globals;
import rorchackh.maradio.libraries.Statics;
import rorchackh.maradio.receivers.StationManagerReceiver;

public class ListingActivity extends BaseActivity {
    private BroadcastReceiver dataBaseReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout);

        dataBaseReceiver = createBroadcastReceiver();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    protected void onResume() {
        broadCastManager.registerReceiver(
                dataBaseReceiver,
                new IntentFilter(Statics.DATABASE_MESSAGE)
        );

        plotStations();
        super.onResume();
    }

    @Override
    protected void onStop() {
        broadCastManager.unregisterReceiver(dataBaseReceiver);
        super.onStop();
    }

    protected Context plotStations() {
        Context context = getBaseContext();
        LinearLayout radioListingLayout = (LinearLayout) findViewById(R.id.radio_list);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        int numberOfColumns = Integer.parseInt(sharedPreferences.getString(getString(R.string.pref_key_columns), Statics.defaultGridSize));

        if (stations != null && stations.size() > 0)     {
            radioListingLayout.removeAllViews();
            ListItem.show(context, stations, radioListingLayout, numberOfColumns);
        }

        return context;
    }

    protected BroadcastReceiver createBroadcastReceiver() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                String s = intent.getStringExtra(Statics.DATABASE_MESSAGE);
                Log.d(Statics.debug, "The listing activity receives the message: " + s);

                switch (s) {
                    case Statics.DATABASE_READY:
                        Globals.stationList = stations = StationManagerReceiver.getStations(context);
                        plotStations();
                        break;
                }
            }
        };
    }

}
