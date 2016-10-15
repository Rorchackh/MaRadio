package rorchackh.maradio.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;

import rorchackh.maradio.R;
import rorchackh.maradio.libraries.Statics;

// Todo: Overwrite the back button here.
public class PreferencesActivity extends BaseActivity  {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, false);
        setContentView(R.layout.content_preferences);

        PreferencesActivityFragment prefFragment = new PreferencesActivityFragment();

        android.app.FragmentManager fragmentManager = getFragmentManager();

        android.app.FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.preferences, prefFragment, Statics.SETTINGS_LABEL);
        transaction.commit();
    }

    public static class PreferencesActivityFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.app_preferences);
        }

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

        }

        @Override
        public void onPause() {
            getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
            super.onPause();
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
            if (s.equals(getString(R.string.pref_key_light))) {

                PreferencesActivity parent = ((PreferencesActivity) getActivity());

                parent.finish();
                parent.startActivity(parent.getIntent());
            }
        }
    }


}
