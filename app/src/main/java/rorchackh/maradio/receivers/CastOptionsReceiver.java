package rorchackh.maradio.receivers;

import android.content.Context;

import com.google.android.gms.cast.CastMediaControlIntent;
import com.google.android.gms.cast.framework.CastOptions;
import com.google.android.gms.cast.framework.OptionsProvider;
import com.google.android.gms.cast.framework.SessionProvider;

import java.util.List;

class CastOptionsReceiver implements OptionsProvider {
    @Override
    public CastOptions getCastOptions(Context appContext) {
        CastOptions castOptions = new CastOptions.Builder()
                .setReceiverApplicationId(CastMediaControlIntent.DEFAULT_MEDIA_RECEIVER_APPLICATION_ID)
                .build();
        return castOptions;
    }

    @Override
    public List<SessionProvider> getAdditionalSessionProviders(Context context) {
        return null;
    }
}