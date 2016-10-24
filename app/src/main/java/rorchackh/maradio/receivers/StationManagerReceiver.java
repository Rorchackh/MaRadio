package rorchackh.maradio.receivers;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;

import rorchackh.maradio.libraries.Statics;
import rorchackh.maradio.models.Station;

public class StationManagerReceiver {

    private static ArrayList<Station> stations = new ArrayList<>();

    public static void init(@Nullable Context context) {
        if (stations.size() == 0) {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference stationRef = database.getReference(Statics.stations);

            stationRef.addValueEventListener(new StationsDatabaseListener(context));
        }
    }

    public static ArrayList<Station> getAll(@Nullable Context context) {
        init(context);
        return stations;
    }

    public static Station getFirst(@Nullable Context context) {
        init(context);
        return stations.get(0);
    }

    public static ArrayList<Station> getFavs(@Nullable Context context) {
        init(context);

        ArrayList<Station> cloned = (ArrayList<Station>) stations.clone();

        Iterator<Station> iterator = cloned.iterator();
        while (iterator.hasNext()) {
            Station s = iterator.next();
            if (!s.isFavorite()) {
                iterator.remove();
            }
        }

        return cloned;
    }

    private static class StationsDatabaseListener implements ValueEventListener {
        private Context context;

        StationsDatabaseListener(Context context) {
            super();
            this.context = context;
        }

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

            GenericTypeIndicator<ArrayList<Station>> t = new GenericTypeIndicator<ArrayList<Station>>() {};
            ArrayList<Station> list = dataSnapshot.getValue(t);

            int index = 0;

            stations = new ArrayList<>();
            for (Station s: list) {
                if (s == null) {
                    continue;
                }

                if (s.isActive()) {
                    stations.add(index++, s);
                }
            }

            Log.i(Statics.debug, String.format("Number of stations %d", list.size()));
            Log.i(Statics.debug, String.format("Number of active stations %d", stations.size()));

            LocalBroadcastManager broadcaster = LocalBroadcastManager.getInstance(context);

            Intent newIntent = new Intent(Statics.DATABASE_MESSAGE);
            newIntent.putExtra(Statics.DATABASE_MESSAGE, Statics.DATABASE_READY);

            broadcaster.sendBroadcast(newIntent);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Toast.makeText(context, "Failed to load comments.", Toast.LENGTH_SHORT).show();
        }
    }

}
