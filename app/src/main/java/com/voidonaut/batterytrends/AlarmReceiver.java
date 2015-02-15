package com.voidonaut.batterytrends;

import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Pagan Winter on 1/17/15.
 */
public class AlarmReceiver extends BroadcastReceiver {
    private Context context;

    @Override
    public void onReceive(Context context, Intent intentEvent) {
        this.context = context;
        Log.d(Constants.LOG, "Alarm triggered");
        Toast.makeText(context, "Alarm triggered", Toast.LENGTH_SHORT).show();
    }
}