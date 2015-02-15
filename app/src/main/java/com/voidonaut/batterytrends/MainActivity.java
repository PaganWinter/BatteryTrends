package com.voidonaut.batterytrends;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.voidonaut.batterytrends.utils.*;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
        String message = intent.getStringExtra(PowerConnectionReceiver.EXTRA_MESSAGE);

        TextView textViewMsg = (TextView) findViewById(R.id.battery_message2);
        TextView textViewDisp = (TextView) findViewById(R.id.battery_message);

        textViewMsg.setText(message);

        String display = "";

        BatteryEventDAO beDB = new BatteryEventDAO(this);
        beDB.open();

        BatteryHelper battery = BatteryHelper.getBatteryInfo(this);
        long levelCurr = battery.level;
        long timeCurr = System.currentTimeMillis()/1000;

        display = display + "Battery: " + levelCurr + " % ("+Utils.formatDateTime(timeCurr)+")\n";

/* Current Cycle Stats */
        BatteryEvent beLastDisc = beDB.getlastDiscEvent(this);
        long levelUsedCurr = beLastDisc.mChargeLevel - levelCurr;
        long timeUsedCurr = timeCurr - beLastDisc.mStartTime;
        if (levelUsedCurr <= 0) {
            // Assuming 1% used to avoid divide by zero
            levelUsedCurr = 1;
        }
        BatteryStats bsCurr = new BatteryStats(levelUsedCurr, timeUsedCurr, levelCurr, timeCurr).computeStats();

        display = display + "Disc. at " + beLastDisc.mChargeLevel + " % (" + Utils.formatDateTime(beLastDisc.mStartTime) + ")\n";
        display = display + "Used " + (beLastDisc.mChargeLevel - levelCurr) + " % in " + Utils.formatDuration(timeCurr - beLastDisc.mStartTime) + " (" + Utils.formatDecimal(bsCurr.ratePerHour) + " %/h)\n\n";

        display = display + "Empty in " + Utils.formatDuration(bsCurr.timeLeft) + " (" + Utils.formatDateTime(bsCurr.timeEmptyAt) + ") [Current Cycle]\n";

/* All Time Stats */
        long levelUsedTotal = beDB.getDischargeLevelTotal(this);
        long timeUsedTotal  = beDB.getDischargeTimeTotal(this);
        BatteryStats bsTotal = new BatteryStats(levelUsedTotal, timeUsedTotal, levelCurr, timeCurr).computeStats();

        display = display + "Empty in " + Utils.formatDuration(bsTotal.timeLeft) + " (" + Utils.formatDateTime(bsTotal.timeEmptyAt) + ") [All Time]\n";

/* One Month Stats */
        long levelUsedMonth = beDB.getDischargeLevelMonth(this);
        long timeUsedMonth  = beDB.getDischargeTimeMonth(this);
        Log.d(Constants.LOG, levelUsedMonth + " " + timeUsedMonth);
        BatteryStats bsMonth = new BatteryStats(levelUsedMonth, timeUsedMonth, levelCurr, timeCurr).computeStats();

        display = display + "Empty in " + Utils.formatDuration(bsMonth.timeLeft) + " (" + Utils.formatDateTime(bsMonth.timeEmptyAt) + ") [Last 30 Days]\n";

        display = display + "-------------------\n";

        display = display + "\t\t\t\t\t\t\t\t\t\t\t\tCurrent\t\t\t\tMonth\t\t\t\t\tAll Time\n";
        display = display + "Empty at\t\t\t: " + Utils.formatTime(bsCurr.timeEmptyAt) + "\t\t| " + Utils.formatTime(bsMonth.timeEmptyAt) + "\t\t| " + Utils.formatTime(bsTotal.timeEmptyAt) + "\n";
        display = display + "Empty in\t\t\t: " + Utils.formatDuration(bsCurr.timeLeft) + "\t\t| " + Utils.formatDuration(bsMonth.timeLeft) + "\t\t| " + Utils.formatDuration(bsTotal.timeLeft) + "\n";
        display = display + "Battery Life\t: " + Utils.formatDuration(bsCurr.batteryLife) + "\t\t| " + Utils.formatDuration(bsMonth.batteryLife) + "\t\t| " + Utils.formatDuration(bsTotal.batteryLife) + "\n";
        display = display + "Rate\t\t\t\t\t\t\t: " + Utils.formatDecimal(bsCurr.ratePerHour) + " %/h\t\t| " + Utils.formatDecimal(bsMonth.ratePerHour) + " %/h\t\t| " + Utils.formatDecimal(bsTotal.ratePerHour) + " %/h" + "\n";

        display = display + "-------------------\nBatteryStats\n-------------------\n";
        BatteryStatsProxy mStats = BatteryStatsProxy.getInstance(getApplication().getBaseContext());
        int mStatsType = 3; //STATS_SINCE_UNPLUGGED;

        long mRawRealtimeMs = SystemClock.elapsedRealtime();
        long mRawUptimeMs   = SystemClock.uptimeMillis();
        long mRawRealtimeUs = mRawRealtimeMs * 1000;
        long mRawUptimeUs   = mRawUptimeMs * 1000;

        long mBatteryUptime = 0;
        long mBatteryRealtime   = 0;
        long mTypeBatteryUptime = 0;
        long mTypeBatteryRealtime   = 0;

        long mScreenOnTime = -1;
        long mWifiOnTime = -1;
        long mPhoneOnTime = -1;

        try {
            mBatteryUptime         = mStats.getBatteryUptime(mRawUptimeUs);
            mBatteryRealtime       = mStats.getBatteryRealtime(mRawRealtimeUs);
            mTypeBatteryUptime     = mStats.computeBatteryUptime(mRawUptimeUs, mStatsType);
            mTypeBatteryRealtime   = mStats.computeBatteryRealtime(mRawRealtimeUs, mStatsType);

            mScreenOnTime = mStats.getScreenOnTime(mRawUptimeUs, mStatsType);
            mWifiOnTime = mStats.getWifiOnTime(mRawUptimeUs, mStatsType);
            mPhoneOnTime = mStats.getPhoneOnTime(mRawUptimeUs, mStatsType);
        }
        catch(Exception e) {
            display = display + "\nError:\n" + e.toString() + "\n";
        }

        display = display + "mRawRealtimeMs: " + Utils.formatDuration(mRawRealtimeMs/1000) + " ("+mRawRealtimeMs/1000+")\n";
        display = display + "mRawUptimeMs: " + Utils.formatDuration(mRawUptimeMs/1000) + " ("+mRawUptimeMs/1000+")\n";

        display = display + "mBatteryUptime: " + Utils.formatDuration(mBatteryUptime/1000000) + "("+mBatteryUptime/1000000+")\n";
        display = display + "mBatteryRealtime: " + Utils.formatDuration(mBatteryRealtime/1000000) + "("+mBatteryRealtime/1000000+")\n";
        display = display + "mTypeBatteryUptime: " + Utils.formatDuration(mTypeBatteryUptime/1000000) + "("+mTypeBatteryUptime/1000000+")\n";
        display = display + "mTypeBatteryRealtime: " + Utils.formatDuration(mTypeBatteryRealtime/1000000) + "("+mTypeBatteryRealtime/1000000+")\n";

        display = display + "mScreenOnTime: " + Utils.formatDuration(mScreenOnTime/1000000) + " (" + mScreenOnTime/1000000 + ")\n";
        display = display + "mWifiOnTime: " + Utils.formatDuration(mWifiOnTime/1000000) + " (" + mWifiOnTime/1000000 + ")\n";
        display = display + "mPhoneOnTime: " + Utils.formatDuration(mPhoneOnTime/1000000) + " (" + mPhoneOnTime/1000000 + ")\n";


        display = display + "\n-------------------\n";
        display = display + beLastDisc.toString() + "\n";
        display = display + bsCurr.toString() + "\n";
        display = display + bsTotal.toString() + "\n";
        display = display + bsMonth.toString() + "\n";
        display = display + "-------------------\n\n";


/* Current Cycle Stats
        BatteryStats bsCurr = beDB.computeStatsCurr(this, levelCurr, timeCurr);
        display = display + "Disc. at " + bsCurr.levelDisc + " % (" + Utils.formatDateTime(bsCurr.timeDisc) + ")\n";
        display = display + "Used " + bsCurr.levelUsed + " % in " + Utils.formatDuration(bsCurr.timeUsed) + " (" + bsCurr.ratePerHour + " %/h)\n\n";

        display = display + "Current Cycle Stats:\n";
        display = display + "Left " + bsCurr.levelLeft + " % in " + Utils.formatDuration(bsCurr.timeLeft) + " (" + Utils.formatTime(bsCurr.timeEmptyAt) + ")\n\n";

        display = display + bsCurr.toString() + "\n\n";
*/

/* All Time Stats */
/*        BatteryStats bsTotal = beDB.computeStatsTotal(this, levelCurr, timeCurr);
        display = display + "All Time Stats:\n";
        display = display + "Left " + bsCurr.levelLeft + " % in " + Utils.formatDuration(bsTotal.timeLeft) + " (" + Utils.formatTime(bsTotal.timeEmptyAt) + ")\n\n";
        display = display + bsCurr.toString();

        display = display + bsCurr.toString() + "\n\n";
        display = display + bsTotal.toString() + "\n\n";
*/

/* old stuff
        BatteryStats bs = beDB.computeStatsOld(this);

        Log.d(Constants.LOG, "BattStat: " + bs.toString());

        String currTime = new SimpleDateFormat("dd/MMM, HH:mm").format(new Date(bs.currTime * 1000));
        String discTime = new SimpleDateFormat("dd/MMM,  HH:mm").format(new Date(bs.discTime * 1000));
//        String timeSinceDisc = String.format("%d:%02d:%02d", (bs.timeSinceDisc/1000)/3600, ((bs.timeSinceDisc/1000)%3600)/60, ((bs.timeSinceDisc/1000)%60));

        String display = "";
        display = display + "Battery: " + bs.currLevel + " (" + currTime + ")\n";
//        display = display + "Charge Left (chargeLeft): " + bs.chargeLeft + "\n";
        display = display + "Last disconnected at " + bs.discLevel + "% on " + discTime + "\n";
        display = display + "Used " + bs.chargeSinceDisc + "% in " + durationFormatted(bs.timeSinceDisc) + " hours";
        display = display + " (" + String.format("%.2f", bs.currDischargeRate) + "%/h)\n\n";

//        display = display + "TotalDischargeTime: " + totalDischargeTime + " (" + (bs.totalDischargeTime) + ")\n";
//        display = display + "TotalDischargeLevel: " + (-1 * bs.totalDischargeLevel) + "\n\n";

        display = display + "Avg Discharge Rate: " + String.format("%.2f", bs.avgDischargeRate) + "%/h\n";
        display = display + "Avg Battery Life: " + durationFormatted(bs.avgLifePerCharge) + " hours\n\n";
//        display = display + "Avg Battery Life: " + String.format("%.3f", bs.avgLifePerCharge) + " hours\n\n";

        display = display + "Time left (avg) (avgLifeLeft): " + String.format("%.3f", bs.avgLifeLeft) + " hours\n";
        display = display + "Time left (since last charge) (currLifeLeft): " + bs.currLifeLeft + " hours\n\n";

        display = display + bs.toString();
/* old stuff */

        beDB.close();


        textViewDisp.setText(display);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public String durationFormattedFloat(float duration) {
        return String.format("%f:%02f", (duration/1000)/3600, ((duration/1000)%3600)/60);
    }
    public String durationFormatted(long duration) {
        return String.format("%d:%02d", (duration)/3600, ((duration)%3600)/60);
    }

    public void refreshMessage(View view) {

        final Intent intentBattChange = this.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        int mBatteryLevel    = intentBattChange.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int mBatteryScale    = intentBattChange.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        int mChargeStatus    = intentBattChange.getIntExtra(BatteryManager.EXTRA_STATUS, -1); // 1 = Unknown, 2 = Charging, 3 = Discharging, 4 = Not Charging, 5 = Full
        int mChargeType      = intentBattChange.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1); // 0 = battery, 1 = AC, 2 = USB

        mBatteryLevel    = (mBatteryLevel * 100) / mBatteryScale;

        boolean mCharging        = mChargeStatus == BatteryManager.BATTERY_STATUS_CHARGING ||
                mChargeStatus == BatteryManager.BATTERY_STATUS_FULL;
        String mChargeStatusStr    = Constants.CHARGE_STATE_MAP[mChargeStatus];
        String mChargeTypeStr      = Constants.CHARGE_TYPE_MAP[mChargeType];

        String log_str = mBatteryLevel + ",mPowerConnected," + mCharging + "," + mChargeStatus + "," + mChargeType + "," + mChargeStatusStr + "," + mChargeTypeStr;
        String message = "BatteryLevel,PowerConnected,Charging,ChargeStatus,ChargeType,ChargeStatusStr,ChargeTypeStr\n" + log_str;
        TextView textViewMsg = (TextView) findViewById(R.id.battery_message);
        textViewMsg.setText(message);

        // TODO
        String LOG_FILE = "BatteryTrendsDump.log";
        BatteryEventDAO beDB = new BatteryEventDAO(this);
        beDB.open();
        List<BatteryEvent> values = beDB.getAllEvents();
        for (int i = 0; i < values.size(); i++) {
            Utility.appendLog(values.get(i).toString(), LOG_FILE);
        }
        beDB.close();

//        setBatteryAlarm();
    }


    public void cancelAlarm(View view) {
        Toast.makeText(this, "Alarm cancelled", Toast.LENGTH_SHORT).show();
    }

    private void setBatteryAlarm() {
        final Context context = this;

        long firstTime;
        long intervalSecs = 60 * 60;
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
