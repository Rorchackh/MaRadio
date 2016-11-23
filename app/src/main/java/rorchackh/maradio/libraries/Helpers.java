package rorchackh.maradio.libraries;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

public class Helpers {

    public static boolean isAppRunning(Context context) {

        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> procInfos = activityManager.getRunningAppProcesses();

        for (ActivityManager.RunningAppProcessInfo info: procInfos) {
            if (info.processName.equals("rorchackh.maradio")) {
                return Globals.currentStation != null;
            }
        }

        return false;
    }
}
