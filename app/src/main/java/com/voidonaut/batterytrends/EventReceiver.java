package com.voidonaut.batterytrends;

        import android.app.AlarmManager;
        import android.app.PendingIntent;
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
 * Created by Pagan Winter on 1/10/15.
 */
public class EventReceiver extends BroadcastReceiver {
    private Context context;

    private int chargeState = -1;
    private long eventTime;
    private int eventType;
    private int waitTime = 0;
    private String eventTimeStr, eventAction;

    public final static String EXTRA_MESSAGE = "com.voidonaut.batterytrends.MESSAGE"; // TODO
    private Handler mHandler = new Handler();

    private final static String LOG_FILE = "BatteryTrends.log";
    private File log_file;


    @Override
    public void onReceive(Context context, Intent intentEvent) {
        this.context = context;

        eventTime = System.currentTimeMillis()/1000;
        eventTimeStr = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss").format(new Date(eventTime * 1000));

        /* Determine Event Type */
        eventAction = intentEvent.getAction();
        Log.d(Constants.LOG, "Battery Event (" + eventAction + ") generated at " + eventTimeStr);

        switch (eventAction) {
            case Intent.ACTION_POWER_CONNECTED :
                eventType = Constants.BATTERY_EVENT_CONNECT;
                chargeState = Constants.BATTERY_STATE_CHARGING;
                waitTime = 2000;
                break;
            case Intent.ACTION_POWER_DISCONNECTED :
                eventType = Constants.BATTERY_EVENT_DISCONNECT;
                chargeState = Constants.BATTERY_STATE_DISCHARGING;
                waitTime = 2000;
                break;
            case Intent.ACTION_BOOT_COMPLETED :
                eventType = Constants.BATTERY_EVENT_BOOTUP;
                waitTime = 3000;
                break;
            case Intent.ACTION_SHUTDOWN :
                eventType = Constants.BATTERY_EVENT_SHUTDOWN;
                chargeState = Constants.BATTERY_STATE_OFF;
                waitTime = 0;
                break;
/*
             case Intent.ACTION_FULLY_CHARGED :
                 eventType = Constants.BATTERY_EVENT_FULLCHARGE;
                 chargeState = Constants.BATTERY_STATE_FULL;
                 waitTime = 0;
                 break;
*/
        }

        mHandler.postDelayed(new Runnable() {
            public void run() {
                logBatteryEvent();
            }
        }, waitTime);

        if (eventAction == Intent.ACTION_BOOT_COMPLETED) {
            // start Alarm to log hourly stats
//            setBatteryAlarm();
        }

    }

    private void logBatteryEvent() {
        final Context context = this.context;
        String message = "";

        BatteryEventDAO beDB = new BatteryEventDAO(context);
        beDB.open();
        Log.d(Constants.LOG, "Called logBatteryEvent()");

        /* Get current battery info */
        BatteryHelper battery = BatteryHelper.getBatteryInfo(context);

        /* If current event is bootup, set chargeState (connected or not) from chargestatus */
        if (chargeState == -1) {
            // Event was 'Bootup', else we can already determine chargeState
            chargeState = battery.chargestatus;
        }

        String log_str = eventTime + "," + eventTimeStr + "," + battery.level + ","
                + chargeState + "," + battery.ischarging + "," + battery.chargestatus + ","
                + battery.chargetype + "," + battery.chargestatusstr + "," + battery.chargetypestr;
        Utility.appendLog(log_str, LOG_FILE);
//        Toast.makeText(context, log_str, Toast.LENGTH_SHORT).show();
//        Log.d(Constants.LOG, "Curr Event: " + log_str);

        /* Create new Event obj */
        BatteryEvent beCurrEvent = new BatteryEvent(
                eventTime,
                eventType,
                chargeState,
                battery.chargestatus,
                battery.chargetype,
                battery.level);
        Log.d(Constants.LOG, "Curr Event: " + beCurrEvent.toString());
        Toast.makeText(context, beCurrEvent.toString(), Toast.LENGTH_SHORT).show();

        /* Get previous event from DB */
        BatteryEvent bePrevEvent = beDB.getLastEvent();
        if (bePrevEvent != null) {
            Log.d(Constants.LOG, "Prev Event: " + bePrevEvent.toString());

            /* For each combo of current and previous event type figure out if you can update previous event in DB */ // TODO
            boolean updatePrevEvent = false;
            int prevEventType = bePrevEvent.getEventType();
            int prevChargeState = bePrevEvent.getChargeState();

            switch (eventType) {
                case Constants.BATTERY_EVENT_CONNECT :
                    if ( (prevEventType == Constants.BATTERY_EVENT_DISCONNECT ||  prevEventType == Constants.BATTERY_EVENT_BOOTUP)
                       && prevChargeState == BatteryManager.BATTERY_STATUS_DISCHARGING)  {
                        updatePrevEvent = true;
                    }
                    break;
                case Constants.BATTERY_EVENT_DISCONNECT :
                    if ( (prevEventType == Constants.BATTERY_EVENT_CONNECT ||  prevEventType == Constants.BATTERY_EVENT_BOOTUP)
                       && prevChargeState == BatteryManager.BATTERY_STATUS_CHARGING)  {
                        updatePrevEvent = true;
                    }
                    break;
                case Constants.BATTERY_EVENT_BOOTUP :
                    if ( prevEventType == Constants.BATTERY_EVENT_SHUTDOWN)  {
                        updatePrevEvent = true;
                    }
                    break;
                case Constants.BATTERY_EVENT_SHUTDOWN :
                    if ( (prevEventType == Constants.BATTERY_EVENT_DISCONNECT ||  prevEventType == Constants.BATTERY_EVENT_BOOTUP)
                            && prevChargeState == BatteryManager.BATTERY_STATUS_DISCHARGING)  {
                        updatePrevEvent = true;
                    }
                    if ( (prevEventType == Constants.BATTERY_EVENT_CONNECT ||  prevEventType == Constants.BATTERY_EVENT_BOOTUP)
                       && prevChargeState == BatteryManager.BATTERY_STATUS_CHARGING)  {
                        updatePrevEvent = true; // TODO: Why shutdown if already charging?
                    }
                    break;
/*
                case Constants.BATTERY_EVENT_FULLCHARGE :
                    if ( (prevEventType == Constants.BATTERY_EVENT_CONNECT ||  prevEventType == Constants.BATTERY_EVENT_BOOTUP)
                            && prevChargeState == BatteryManager.BATTERY_STATUS_CHARGING)  {
                        updatePrevEvent = true;
                    }
                    break;
 */
            }

            if (updatePrevEvent) {
                Log.d(Constants.LOG, "Updating Previous Event.");
//              bePrevEvent.updateEvent(eventTime, battery.level);
                bePrevEvent.setEndTime(eventTime);
                bePrevEvent.setDuration(eventTime - bePrevEvent.getStartTime());
                bePrevEvent.setLevelChange(battery.level - bePrevEvent.getChargeLevel());

                long upd = beDB.updateEvent(bePrevEvent);
                Log.d(Constants.LOG, "Updated Prev Event: ("+upd+"): " + bePrevEvent.toString());

/*
                BatteryEvent bePrevEventTest = beDB.getLastEvent();
                message = bePrevEventTest.toString();
                Log.d(Constants.LOG, "Updated Prev Event: " + bePrevEventTest.toString());
*/
            }
            else {
                Log.w(Constants.LOG, "Previous Event not updated. Some in-between event was missed!");
            }
        }
        else {
            Log.d(Constants.LOG, "No Prev Events yet");
        }

        /* Write current event to DB */ // TODO
        beDB.insertEvent(beCurrEvent);
        Log.d(Constants.LOG, "Stored Curr Event in DB.");

        beDB.close();

/*
        message = message + "\n" + "EventTime,EventTimeStr,BatteryLevel,PowerConnected,Charging,ChargeStatus,ChargeType,ChargeStatusStr,ChargeTypeStr\n" + log_str;
        // Send data to UI
        Intent nIntent = new Intent(context, MainActivity.class);
        nIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        nIntent.putExtra(EXTRA_MESSAGE, message);
        context.startActivity(nIntent);
*/
    }


    private void setBatteryAlarm() {
        final Context context = this.context;

        long firstTime;
        long intervalSecs = 5;
        long intervalMillis = intervalSecs * 1000;

        Intent intentAlarm;
        PendingIntent piAlarm;
        AlarmManager amBattery;

        firstTime = System.currentTimeMillis();
        Log.d(Constants.LOG, "1] " + new java.util.Date(firstTime) + " " + firstTime);

        // Rounding up to next <interval>th minute (e.g. 04:37 becomes 04:45)
        firstTime = Math.round(firstTime/intervalMillis) * intervalMillis + intervalMillis;
        Log.d(Constants.LOG, "2] " + new java.util.Date(firstTime) + " " + firstTime + " (" + intervalMillis + ")");

        intentAlarm = new Intent(context, AlarmReceiver.class);
        piAlarm = PendingIntent.getBroadcast(context, 0, intentAlarm, 0);
//        piAlarm = PendingIntent.getService(context, 0, new Intent(context, AlarmReceiver.class), 0);

        amBattery = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        amBattery.setRepeating(AlarmManager.RTC_WAKEUP, firstTime, intervalMillis, piAlarm);
//            amBattery.setInexactRepeating(AlarmManager.RTC_WAKEUP, firstTime, intervalMillis, pIntentAlarm);
        Log.d(Constants.LOG, "Alarm Set");
        Toast.makeText(context, "Alarm Set", Toast.LENGTH_SHORT).show();

/*
        PendingIntent i = PendingIntent.getActivity(context, 0, new Intent(context,MainActivity.class), Intent.FLAG_ACTIVITY_NEW_TASK);
        AlarmManager mgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 20000, i);
*/

    }


}
