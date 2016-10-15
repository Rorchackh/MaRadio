package rorchackh.maradio.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;

import rorchackh.maradio.R;

public class FavRadiosActivity extends ListingActivity {

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, true);
        setContentView(R.layout.content_listing);

        plotStations(true);
    }

}
