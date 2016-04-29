package com.example.sleeptracker.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.sleeptracker.MainActivity;

import java.util.Date;

public class PreferenceManagerUtil {
    private static final String LAST_START_TIME = "lastStartTime";
    private static final String LAST_STOP_TIME = "lastStopTime";
    private static final String SLEEP_START_TIME = "sleepStartTime";
    private static final String IN_SLEEP = "sleeping";
    private static final String NUM_INTERRUPTS_SO_FAR = "numInterrupts";
    private static final String IS_STARTED = "isStarted";

    public static long getLastStartTime(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getLong(LAST_START_TIME, 0);
    }

    public static void setLastStartTime(long value, Context context) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putLong(LAST_START_TIME, value);
        editor.apply();
    }

    public static long getLastStopTime(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getLong(LAST_STOP_TIME, 0);
    }

    public static void setLastStopTime(long value, Context context) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putLong(LAST_STOP_TIME, value);
        editor.apply();
    }

    public static boolean isSleeping(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(IN_SLEEP, false);
    }

    public static long getSleepStartTime(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getLong(SLEEP_START_TIME, 0);
    }

    public static void setSleeping(boolean value, Context context) {
        long lastStopTime = getLastStopTime(context);
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putBoolean(IN_SLEEP, value);
        editor.putInt(NUM_INTERRUPTS_SO_FAR, 0);
        if (value) {
            editor.putLong(SLEEP_START_TIME, lastStopTime);
        }
        editor.apply();
        updateSleepRecord(context);

    }

    private static void updateSleepRecord(Context context) {
        long sleepStartTime = getSleepStartTime(context);
        long lastStartTime = getLastStartTime(context);
        if ((lastStartTime - sleepStartTime) > MainActivity.MIN_SLEEP_DURATION) {
            DBHelper.getInstance(context).insertIntoSleepInfo(
                    new Date(sleepStartTime).toString(), new Date(lastStartTime).toString());
        }
    }

    public static int getInterruptCount(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt(NUM_INTERRUPTS_SO_FAR, 0);
    }

    public static void increamentInterrupts(Context context) {
        int currentInterruptCount = getInterruptCount(context);
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putInt(NUM_INTERRUPTS_SO_FAR, currentInterruptCount + 1);
        editor.apply();
    }

    public static boolean getIsStarted(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(IS_STARTED, false);
    }

    public static void setIsStarted(Boolean value, Context context) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putBoolean(IS_STARTED, value);
        editor.apply();
    }
}
