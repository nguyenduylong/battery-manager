/*
    Copyright (c) 2010-2013 Darshan-Josiah Barber

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.
*/

package com.example.nguyenduylong.pin.util;

import android.content.res.Resources;
import android.text.Spanned;

import com.example.nguyenduylong.pin.R;
import com.example.nguyenduylong.pin.model.BatteryInfo;

import java.util.Random;


public class Str {
    private Resources res;

    public String degree_symbol;
    public String fahrenheit_symbol;
    public String celsius_symbol;
    public String volt_symbol;
    public String percent_symbol;
    public String since;

    public String yes;
    public String cancel;
    public String okay;

    public String currently_set_to;

    public String silent;

    public String[] statuses;
    public String[] healths;
    public String[] pluggeds;

    public static final int DEFAULT_STATE  = 0;
    public static final int SLEEP_STATE  = 1;
    public static final int CALL_STATE  = 2;
    public static final int WEB_STATE  = 3;
    public static final int MOVIE_STATE  = 4;
    public static final int MUSIC_STATE  = 5;
    public static final int READING_STATE  = 6;
    public static final int GPS_STATE  = 7;
    public static final int CAMERA_STATE  = 8;
    public static final  int VIDEO_RECORD_STATE = 9;
    public static final int GAME_STATE = 10;
    public Str(Resources r) {
        res = r;

        degree_symbol          = res.getString(R.string.degree_symbol);
        fahrenheit_symbol      = res.getString(R.string.fahrenheit_symbol);
        celsius_symbol         = res.getString(R.string.celsius_symbol);
        volt_symbol            = res.getString(R.string.volt_symbol);
        percent_symbol         = res.getString(R.string.percent_symbol);
        since                  = res.getString(R.string.since);

        yes                = res.getString(R.string.yes);
        cancel             = res.getString(R.string.cancel);
        okay               = res.getString(R.string.okay);

        currently_set_to    = res.getString(R.string.currently_set_to);

        silent = res.getString(R.string.silent);

        statuses            = res.getStringArray(R.array.statuses);
        healths             = res.getStringArray(R.array.healths);
        pluggeds            = res.getStringArray(R.array.pluggeds);
    }

    public String for_n_hours(int n) {
        return String.format(res.getQuantityString(R.plurals.for_n_hours, n), n);
    }

    public String n_hours_m_minutes_long(int n, int m) {
        return (String.format(res.getQuantityString(R.plurals.n_hours_long, n), n) +
                String.format(res.getQuantityString(R.plurals.n_minutes_long, m), m));
    }

    public String n_minutes_long(int n) {
        return String.format(res.getQuantityString(R.plurals.n_minutes_long, n), n);
    }

    public String n_hours_m_minutes_medium(int n, int m) {
        return (String.format(res.getQuantityString(R.plurals.n_hours_medium, n), n) +
                String.format(res.getQuantityString(R.plurals.n_minutes_medium, m), m));
    }

    public String n_hours_long_m_minutes_medium(int n, int m) {
        return (String.format(res.getQuantityString(R.plurals.n_hours_long, n), n) +
                String.format(res.getQuantityString(R.plurals.n_minutes_medium, m), m));
    }

    public String n_hours_m_minutes_short(int n, int m) {
        return (String.format(res.getQuantityString(R.plurals.n_hours_short, n), n) +
                String.format(res.getQuantityString(R.plurals.n_minutes_short, m), m));
    }

    public String n_days_m_hours(int n, int m) {
        return (String.format(res.getQuantityString(R.plurals.n_days, n), n) +
                String.format(res.getQuantityString(R.plurals.n_hours, m), m));
    }

    public String n_log_items(int n) {
        return String.format(res.getQuantityString(R.plurals.n_log_items, n), n);
    }

    /* temperature is the integer number of tenths of degrees Celcius, as returned by BatteryManager */
    public String formatTemp(int temperature, boolean convertF, boolean includeTenths) {
        double d;
        String s;

        if (convertF){
            d = java.lang.Math.round(temperature * 9 / 5.0) / 10.0 + 32.0;
            s = degree_symbol + fahrenheit_symbol;
        } else {
            d = temperature / 10.0;
            s = degree_symbol + celsius_symbol;
        }

        // Weird: the ternary operator seems to compile down to a "function" that has to return a single particular type
        //return "" + (includeTenths ? d : java.lang.Math.round(d)) + s;
        return (includeTenths ? String.valueOf(d) : String.valueOf(java.lang.Math.round(d))) + s;
    }

    public String formatTemp(int temperature, boolean convertF) {
        return formatTemp(temperature, convertF, true);
    }

    public String formatVoltage(int voltage) {
        return String.valueOf(voltage / 1000.0) + volt_symbol;
    }

    public static int indexOf(String[] a, String key) {
        for (int i=0, size=a.length; i < size; i++)
            if (key.equals(a[i])) return i;

        return -1;
    }
    /*
    public static void overrideLanguage(Resources res, WindowManager wm, String lang_override) {
        android.content.res.Configuration conf = res.getConfiguration();
        if (! lang_override.equals("default")) {
            conf.locale = SettingsActivity.codeToLocale(lang_override);
            android.util.DisplayMetrics metrics = new android.util.DisplayMetrics();
            wm.getDefaultDisplay().getMetrics(metrics);
            res.updateConfiguration(conf, metrics);
        } else {

        }
    }
    */


    public Spanned predictorTimeRemaining(BatteryInfo info , int state) {
        Spanned predicTimeRemaining = null;
        BatteryInfo.RelativeTime predicted = info.prediction.last_rtime;
        int remainingMinute = predicted.days * 24 * 60 + predicted.hours * 60 + predicted.minutes;
        if (info.plugged == BatteryInfo.PLUGGED_UNPLUGGED) {
            Random random = new Random();
            remainingMinute+=100;
            switch (state) {
                case SLEEP_STATE:
                    remainingMinute = (int) (remainingMinute * 1.9 + random.nextInt(30) + 1);
                    break;
                case CALL_STATE :
                    remainingMinute = (int)(remainingMinute*0.43 + random.nextInt(10) +1);
                    break;
                case WEB_STATE:
                    remainingMinute = (int)(remainingMinute*0.32 + random.nextInt(10)+1);
                    break;
                case CAMERA_STATE:
                    remainingMinute = (int)(remainingMinute*0.23+ random.nextInt(5)+1);
                    break;
                case GPS_STATE:
                    remainingMinute = (int)(remainingMinute*0.25 + random.nextInt(5)+1);
                    break;
                case MOVIE_STATE:
                    remainingMinute = (int)(remainingMinute*0.21 + random.nextInt(5)+1);
                    break;
                case MUSIC_STATE :
                    remainingMinute = (int)(remainingMinute*0.56 + random.nextInt(5)+1);
                    break;
                case READING_STATE:
                    remainingMinute = (int)(remainingMinute*0.45 + random.nextInt(5)+1);
                    break;
                case VIDEO_RECORD_STATE:
                    remainingMinute = (int)(remainingMinute*0.19 + random.nextInt(5)+1);
                    break;
                case GAME_STATE:
                    remainingMinute = (int)(remainingMinute*0.23 + random.nextInt(5)+1);
                    break;
                case DEFAULT_STATE:
                    break;

            }
            predicTimeRemaining = convertRemainingMinutes(remainingMinute);
        }else {
            if (remainingMinute>60) {
                predicTimeRemaining = convertRemainingMinutes(remainingMinute - 30);
            }else {
                predicTimeRemaining = convertRemainingMinutes(remainingMinute);
            }
        }
        return predicTimeRemaining;
    }

    public Spanned predictorTimeRemainingState(BatteryInfo info, int state){
        Spanned  predicTimeRemaining = null;
        BatteryInfo.RelativeTime predicted = info.prediction.last_rtime;
        int remainingMinute = predicted.days * 24 * 60 + predicted.hours * 60 + predicted.minutes;
        if (info.plugged == BatteryInfo.PLUGGED_UNPLUGGED) {
            Random random = new Random();
            remainingMinute+=100;
            switch (state) {
                case SLEEP_STATE:
                    remainingMinute = (int) (remainingMinute * 1.9 + random.nextInt(30) + 1);
                    break;
                case CALL_STATE :
                    remainingMinute = (int)(remainingMinute*0.43 + random.nextInt(10) +1);
                    break;
                case WEB_STATE:
                    remainingMinute = (int)(remainingMinute*0.32 + random.nextInt(10)+1);
                    break;
                case CAMERA_STATE:
                    remainingMinute = (int)(remainingMinute*0.23+ random.nextInt(5)+1);
                    break;
                case GPS_STATE:
                    remainingMinute = (int)(remainingMinute*0.25 + random.nextInt(5)+1);
                    break;
                case MOVIE_STATE:
                    remainingMinute = (int)(remainingMinute*0.21 + random.nextInt(5)+1);
                    break;
                case MUSIC_STATE :
                    remainingMinute = (int)(remainingMinute*0.56 + random.nextInt(5)+1);
                    break;
                case READING_STATE:
                    remainingMinute = (int)(remainingMinute*0.45 + random.nextInt(5)+1);
                    break;
                case VIDEO_RECORD_STATE:
                    remainingMinute = (int)(remainingMinute*0.19 + random.nextInt(5)+1);
                    break;
                case GAME_STATE:
                    remainingMinute = (int)(remainingMinute*0.23 + random.nextInt(5)+1);
                    break;
                case DEFAULT_STATE:
                    break;

            }
            predicTimeRemaining = convertRemainingMinutesState(remainingMinute);
        }else {
            if (remainingMinute>60) {
                predicTimeRemaining = convertRemainingMinutesState(remainingMinute - 30);
            }else {
                predicTimeRemaining = convertRemainingMinutesState(remainingMinute);
            }
        }
        return predicTimeRemaining;
    }
    public Spanned convertRemainingMinutesState(int minuteCount){
        int day = 0;
        int hour = 0;
        int minute = 0;
        hour = minuteCount/60;
        minute = minuteCount%60;
        if (hour >= 24) {
            if (minute >= 30) hour += 1;

            day = hour / 24;
            hour = hour % 24;
        }
        if (day > 0)
            return android.text.Html.fromHtml("<font color=\"#ffffff\">" + day + "d</font> " +
                    "<font color=\"#ffffff\"><small>" + hour + "h</small></font>");
        else if (hour> 0)
            return android.text.Html.fromHtml("<font color=\"#ffffff\">" + hour + "h</font> " +
                    "<font color=\"#ffffff\"><small>" + minute + "m</small></font>");

        else
            return android.text.Html.fromHtml("<font color=\"#ffffff\"><small>" + minute + " mins</small></font>");
    }
    public Spanned convertRemainingMinutes(int minuteCount){
        int day = 0;
        int hour = 0;
        int minute = 0;
        hour = minuteCount/60;
        minute = minuteCount%60;
        if (hour >= 24) {
            if (minute >= 30) hour += 1;

            day = hour / 24;
            hour = hour % 24;
        }
        if (day > 0)
            return android.text.Html.fromHtml("<font color=\"#6fc14b\">" + day + "d</font> " +
                    "<font color=\"#33b5e5\"><small>" + hour + "h</small></font>");
        else if (hour> 0)
            return android.text.Html.fromHtml("<font color=\"#6fc14b\">" + hour + "h</font> " +
                    "<font color=\"#33b5e5\"><small>" + minute + "m</small></font>");

        else
            return android.text.Html.fromHtml("<font color=\"#33b5e5\"><small>" + minute + " mins</small></font>");
    }
    public String untilWhat(BatteryInfo info) {
        if (info.prediction.what == BatteryInfo.Prediction.NONE)
            return "";
        else if (info.prediction.what == BatteryInfo.Prediction.UNTIL_CHARGED)
            return res.getString(R.string.activity_until_charged);
        else
            return res.getString(R.string.activity_until_drained);
    }
}
