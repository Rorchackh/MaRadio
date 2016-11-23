package rorchackh.maradio.drawables;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;

import java.util.ArrayList;

import rorchackh.maradio.R;
import rorchackh.maradio.activities.PlayerActivity;
import rorchackh.maradio.libraries.Globals;
import rorchackh.maradio.libraries.Statics;
import rorchackh.maradio.models.Station;


public class ListItem extends ImageView {

    private ListItem(Context context, Station station, ArrayList<Station> stations, int side) {
        super(context);

        this.setLayoutParams(new TableLayout.LayoutParams(side, side, 1));

        if (station != null) {

            String image = station.getImageLink();

            if (image == null || image.equals("")) {
                Log.e(Statics.debug, String.format("Image URL for  %s not found", station.toString()));
                this.setBackgroundResource(R.drawable.none);
            } else {
                Globals.imageLoader.displayImage(image, this);
            }

            this.setOnClickListener(new RowClickListener(station, stations));
        }
    }

    public static void show(Context context, ArrayList<Station> stations, LinearLayout radioListingLayout, int numberOfColumns) {

        LinearLayout r = new LinearLayout(context);
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();

        int height = Math.round(displayMetrics.widthPixels / numberOfColumns);

        int limit = stations.size();
        for (int i = 0; i < limit; i++) {
            Station station = stations.get(i);

            if (i % numberOfColumns == 0) {
                r = new LinearLayout(context);
                r.setOrientation(LinearLayout.HORIZONTAL);
                r.setLayoutParams(new LinearLayoutCompat.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    height
                ));

                radioListingLayout.addView(r);
            }

            r.addView(new ListItem(context, station, stations, height));

            if (i == limit - 1) {
                while (++i % numberOfColumns != 0) r.addView(new ListItem(context, null, stations, height));
            }
        }
    }

    private class RowClickListener implements View.OnClickListener {

        private Station currentStation;
        private ArrayList<Station> stations;

        RowClickListener(Station station, ArrayList<Station> stations) {
            this.currentStation = station;
            this.stations = stations;
        }

        @Override
        public void onClick(View view) {

            Context context = view.getContext();

            Intent intent = new Intent(context, PlayerActivity.class);

            intent.putExtra(Statics.station, currentStation);
            intent.putParcelableArrayListExtra(Statics.stations, stations);

            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            context.startActivity(intent);
        }
    }
}

