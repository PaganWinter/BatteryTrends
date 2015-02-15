package com.voidonaut.batterytrends;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by Pagan Winter on 12/28/14.
 */
public class PowerConnectionReceiver extends BroadcastReceiver {
    public final static String EXTRA_MESSAGE = "com.voidonaut.batterytrends.MESSAGE"; // TODO
    private Handler mHandler = new Handler();
    private Context context;

    private boolean mPowerConnected;
    private boolean mCharging;
//    private boolean mChargeUSB, mChargeAC;
    private long mEventTime;    // System.currentTimeMillis();

    private int mChargeStatus, mChargeType, mBatteryLevel, mBatteryScale;
    private String mActionPowerConn, mChargeStatusStr, mChargeTypeStr, mEventTimeStr;

    private final static String LOG_FILE = "BatteryTrends.log";
    private File log_file;


    @Override
    public void onReceive(Context context, Intent intentPowerConn) {
        this.context = context;
        mEventTime = System.currentTimeMillis();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        mEventTimeStr = df.format(new Date(mEventTime)).toString();

        mActionPowerConn = intentPowerConn.getAction();

        if (mActionPowerConn.equals(Intent.ACTION_POWER_CONNECTED)) {
            mPowerConnected = true;
        }
        if (mActionPowerConn.equals(Intent.ACTION_POWER_DISCONNECTED)) {
            mPowerConnected = false;
        }

        mHandler.postDelayed(new Runnable() {
            public void run() {
                fetchBatteryInfo();
            }
        }, 3000);


    }

    private void fetchBatteryInfo() {
        final Context context = this.context;
        String[] mapChargeStatus = {"NA", "Unknown", "Charging", "Discharging", "Not Charging", "Full"};
        String[] mapChargeType = {"Battery", "AC", "USB"};

        final Intent intentBattChange = context.getApplicationContext().registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        mBatteryLevel    = intentBattChange.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        mBatteryScale    = intentBattChange.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        mChargeStatus    = intentBattChange.getIntExtra(BatteryManager.EXTRA_STATUS, -1); // 1 = Unknown, 2 = Charging, 3 = Discharging, 4 = Not Charging, 5 = Full
        mChargeType      = intentBattChange.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1); // 0 = battery, 1 = AC, 2 = USB

        mBatteryLevel    = (mBatteryLevel * 100) / mBatteryScale;

        mCharging        = mChargeStatus == BatteryManager.BATTERY_STATUS_CHARGING ||
                           mChargeStatus == BatteryManager.BATTERY_STATUS_FULL;
//        mChargeUSB  = mChargeType == BatteryManager.BATTERY_PLUGGED_USB;
//        mChargeAC   = mChargeType == BatteryManager.BATTERY_PLUGGED_AC;

        mChargeStatusStr    = mapChargeStatus[mChargeStatus];
        mChargeTypeStr      = mapChargeType[mChargeType];

        String log_str = mEventTime + "," + mEventTimeStr + "," + mBatteryLevel + "," + mPowerConnected + "," + mCharging + "," + mChargeStatus + "," + mChargeType + "," + mChargeStatusStr + "," + mChargeTypeStr;
        Utility.appendLog(log_str, LOG_FILE);

        String message =
                "PowerConnected" + mPowerConnected +
                        " isCharging:" + mCharging +
                        " Status:" + mChargeStatus + "(" + mChargeStatusStr + ")" +
                        " Type:" + mChargeType + "(" + mChargeTypeStr + ")" +
                        " Level:" + mBatteryLevel;
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();

        message = "EventTime,EventTimeStr,BatteryLevel,PowerConnected,Charging,ChargeStatus,ChargeType,ChargeStatusStr,ChargeTypeStr\n" + log_str;

        // Send data to UI
        Intent nIntent = new Intent(context, MainActivity.class);
        nIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        nIntent.putExtra(EXTRA_MESSAGE, message);
        context.startActivity(nIntent);
    }


}
