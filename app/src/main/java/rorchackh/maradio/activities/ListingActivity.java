package rorchackh.maradio.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.util.Log;
import android.widget.LinearLayout;

import rorchackh.maradio.R;
import rorchackh.maradio.drawables.ListItem;
import rorchackh.maradio.libraries.Globals;
import rorchackh.maradio.libraries.Statics;
import rorchackh.maradio.receivers.StationManagerReceiver;
import rorchackh.maradio.services.NotificationManager;
import rorchackh.maradio.services.PlayerService;

public class ListingActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    protected void onCreate(Bundle savedInstanceState, boolean onlyFavs) {
        super.onCreate(savedInstanceState, onlyFavs);

        notificationBroadCaster.unregisterReceiver(super.receiver);
        receiver = createBroadcastReceiver();

        notificationBroadCaster.registerReceiver(
            receiver,
            new IntentFilter(Statics.SERVICE_MESSAGE)
        );

    }

    protected Context plotStations(boolean onlyFavs) {
        Context context = getBaseContext();
        LinearLayout radioListingLayout = (LinearLayout) findViewById(R.id.radio_list);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        int numberOfColumns = Integer.parseInt(sharedPreferences.getString(getString(R.string.pref_key_columns), Statics.defaultGridSize));

        if (stations != null && stations.size() > 0)     {
            radioListingLayout.removeAllViews();
            ListItem.show(context, stations, radioListingLayout, numberOfColumns, onlyFavs);
        }

        return context;
    }

    @NonNull
    protected BroadcastReceiver createBroadcastReceiver() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                String s = intent.getStringExtra(Statics.SERVICE_MESSAGE);
                Log.d(Statics.debug, "The listing activity receives the message: " + s);

                int currentStationIndex = stations.indexOf(Globals.currentStation);

                boolean hasPrev = currentStationIndex > 0;
                boolean hasNext = currentStationIndex < stations.size() - 1;

                switch (s) {
                    case Statics.SERVICE_PAUSE:
                    case Statics.SERVICE_PREPARE:
                    case Statics.SERVICE_PLAY:
                        NotificationManager.show(getBaseContext(), Globals.currentStation, s, hasNext, hasPrev);
                        break;
                    case Statics.SERVICE_STOP:
                        NotificationManager.remove();
                        break;
                    case Statics.SERVICE_NEXT:

                        if (currentStationIndex < stations.size() - 1) {
                            PlayerService.play(getBaseContext(), stations.get(++currentStationIndex));
                        }

                        break;
                    case Statics.SERVICE_PREV:

                        if (currentStationIndex > 0) {
                            PlayerService.play(getBaseContext(), stations.get(--currentStationIndex));
                        }

                        break;

                    case Statics.DATABASE_READY:

                        if (isFav) {
                            stations = StationManagerReceiver.getAll(context);
                        } else {
                            stations = StationManagerReceiver.getFavs(context);
                        }

                        plotStations(isFav);
                        break;
                }
            }
        };
    }
}
