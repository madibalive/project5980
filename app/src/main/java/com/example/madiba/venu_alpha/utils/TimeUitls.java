package com.example.madiba.venu_alpha.utils;

import android.content.Context;
import android.text.format.DateFormat;
import android.text.format.DateUtils;


import com.example.madiba.venu_alpha.R;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by Madiba on 11/23/2016.
 */

public class TimeUitls {
    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

    public TimeUitls() {
    }

    public static long calculateDays(Date mDate1, Date mDate2) {
        return Math.abs((mDate1.getTime() - mDate2.getTime()) / (24 * 60 * 60 * 1000) + 1);
    }
    static long getCurrentTime() {
        return new Date().getTime();
    }

    public static String getTimeAgo(long time, Context context) {
        if (time < 1000000000000L) {
            // if timestamp given in seconds, convert to millis
            time *= 1000;
        }

        long now = getCurrentTime();
        if (time > now || time <= 0) {
            return null;
        }

        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return diff / SECOND_MILLIS + "s";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return 1 + "m";
        } else if (diff < 50 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + "m";
        } else if (diff < 90 * MINUTE_MILLIS) {
            return 1 + "h";
        } else if (diff < 24 * HOUR_MILLIS) {
            if (diff / HOUR_MILLIS == 1)
                return 1 + "h";
            else
                return diff / HOUR_MILLIS + "h";
        } else if (diff < 48 * HOUR_MILLIS) {
            return 1 + "d";
        } else {
            return diff / DAY_MILLIS + "d";
        }
    }

    public static String getLiveBadgeText(Context context, long start, long end) {
        long now = getCurrentTime();

        if (now < start) {
            // Will be live later
            return context.getString(R.string.live_available);
        } else if (start <= now && now <= end) {
            // Live right now!
            // Indicated by a visual live now badge
            return "";
        } else {
            // Too late.
            return "";
        }
    }

    public static String eventBannerForm(Date date){

        String day = (String) DateFormat.format("dd", date); //20
        String stringMonth = (String) DateFormat.format("MMM", date); //Jun

        return day + "\n" + stringMonth;
    }

    private static String eventDateToString(Date date){
        return (String) DateFormat.format("HH:mm", date); //Jun

    }

    public static String elapseTime(Date date){
        long now =getCurrentTime();
        long diffInMillisec = date.getTime() -now;
        long diffInSec = TimeUnit.MILLISECONDS.toSeconds(diffInMillisec);
        float hours = diffInSec % 24;
        return String.valueOf(Math.round(hours));
    }

    public static String getRelativeTime( Date start) {

        if(start!=null) {
            return DateUtils.getRelativeTimeSpanString(start.getTime(),
                    System.currentTimeMillis(),
                    DateUtils.SECOND_IN_MILLIS)
                    .toString().toLowerCase();
        }
        return null;
    }

}
