package com.apphub.eaa2.Utils;

import android.util.Log;

import java.util.Calendar;

public class TimeUtils {

    private static final String TAG = "AviralAPI";

    public static long getCurrentTime() {

        Log.d(TAG, "getCurrentTime: Current Time in Millis: " + Calendar.getInstance().getTimeInMillis());

        return Calendar.getInstance().getTimeInMillis();

    }

    public static boolean compareTimeWithSixHours(long storedTimeInMillis, String tag) {

        boolean isDifferenceMoreThenSixHours = false;

        if (storedTimeInMillis != -1) {

            Log.d(TAG, "compareTimeWithSixHours: Got Time in Millis From Shared Preferences " + tag);

            long currentTimeInMillis = System.currentTimeMillis();
            long timeDifferenceInMillis = currentTimeInMillis - storedTimeInMillis;

            Log.d(TAG, "compareTimeWithSixHours: Difference of time in Millis: " + timeDifferenceInMillis + " " + tag);

            // Check if the time difference is greater than 6 hours (6 * 60 * 60 * 1000 = 21600000)
            if (timeDifferenceInMillis > 21600000) {

                Log.d(TAG, "compareTimeWithSixHours: Time Difference is Greater then 6 hours " + tag);

                isDifferenceMoreThenSixHours = true;
            }
        }

        return isDifferenceMoreThenSixHours;

    }

}
