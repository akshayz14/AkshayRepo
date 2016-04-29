package com.example.sleeptracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.sleeptracker.util.DBHelper;
import com.example.sleeptracker.util.PreferenceManagerUtil;

import java.util.Calendar;
import java.util.Date;

public class ScreenReceiver extends BroadcastReceiver {
    private static final String TAG = ScreenReceiver.class.getName();

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            Calendar calendar = Calendar.getInstance();
            PreferenceManagerUtil.setLastStopTime(calendar.getTimeInMillis(), context);
            Log.d(TAG, "Time off " + PreferenceManagerUtil.getLastStopTime(context));
            if (PreferenceManagerUtil.isSleeping(context)) {
                if (isInterrupt(context)) {
                    PreferenceManagerUtil.increamentInterrupts(context);
                    if (PreferenceManagerUtil.getInterruptCount(context) > MainActivity.NUM_INTERRUPTS) {
                        PreferenceManagerUtil.setSleeping(false, context);
                    }
                } else {
                    PreferenceManagerUtil.setSleeping(false, context);
                }
            }
        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            Calendar calendar = Calendar.getInstance();
            PreferenceManagerUtil.setLastStartTime(calendar.getTimeInMillis(), context);
            Log.d(TAG, "Time on " + PreferenceManagerUtil.getLastStartTime(context));
            if (PreferenceManagerUtil.isSleeping(context)) {
                DBHelper.getInstance(context).insertIntoSleepInfo(
                        new Date(PreferenceManagerUtil.getSleepStartTime(context)).toString(),
                        new Date(PreferenceManagerUtil.getLastStartTime(context)).toString());
            } else if (startedSleeping(context)) {
                PreferenceManagerUtil.setSleeping(true, context);
            }
        }

        Log.d(TAG, "Records " + DBHelper.getInstance(context).getAll());
    }

    private boolean isInterrupt(Context context) {
        if (PreferenceManagerUtil.getLastStopTime(context) - PreferenceManagerUtil.getLastStartTime(context)
                <= MainActivity.MAX_INTERRUPT_DURATION) {
            return true;
        }
        return false;
    }

    private boolean startedSleeping(Context context) {
        if (PreferenceManagerUtil.getLastStopTime(context) != 0
                && (PreferenceManagerUtil.getLastStartTime(context) - PreferenceManagerUtil.getLastStopTime(context))
                >= MainActivity.MIN_SLEEP_DURATION) {
            return true;
        }
        return false;
    }
}