package com.voidonaut.batterytrends;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by paganwinter on 8/2/15.
 */
public class Utils {
    public static String formatTime(long timeInEpochSec) {
        // SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
        // return sdf.format(new Date(timeInEpochSec * 1000));
        return new SimpleDateFormat("hh:mm a").format(new Date(timeInEpochSec * 1000));
    }

    public static String formatDateTime(long timeInEpochSec) {
        // SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
        // return sdf.format(new Date(timeInEpochSec * 1000));
        return new SimpleDateFormat("hh:mm a, d/MMM").format(new Date(timeInEpochSec * 1000));
    }

    public static String formatDecimal(double value) {
        return String.format("%.2f", value);
    }
    public static String formatDecimal(double value, int decPlaces) {
        return String.format("%."+decPlaces+"f", value);
    }

    public static String formatDuration(long durationInSec) {
        int days = (int) TimeUnit.SECONDS.toDays(durationInSec);
        long hours = TimeUnit.SECONDS.toHours(durationInSec) - (days *24);
        long minutes = TimeUnit.SECONDS.toMinutes(durationInSec) - (TimeUnit.SECONDS.toHours(durationInSec)* 60);
        long seconds = TimeUnit.SECONDS.toSeconds(durationInSec) - (TimeUnit.SECONDS.toMinutes(durationInSec) *60);

/*
        // return String.format("%d:%02d:%02d", durationInSec/3600, (durationInSec%3600)/60, (durationInSec%60));
        int days = (int) (durationInSec/3600);
        days = 0;
        int hours = (int) (durationInSec/3600);
        int minutes = (int) ((durationInSec%3600)/60);
*/
        if (days > 0) {
            return String.format("%dd %dh %02dm", days, hours, minutes);
        }
        else if (hours > 0) {
            return String.format("%dh %02dm", hours, minutes);
        }
        else {
            return String.format("%02dm %02ds", minutes, seconds);
        }
    }

}
