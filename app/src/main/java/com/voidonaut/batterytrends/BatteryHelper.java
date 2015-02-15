package com.voidonaut.batterytrends;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.widget.Toast;

/**
 * Created by Pagan Winter on 1/10/15.
 */
public class BatteryHelper {
    public int level, rawlevel, scale, chargestatus, chargetype, health, temperature, voltage;
    public boolean ischarging, chargeUSB, chargeAC, present;
    public String chargestatusstr, chargetypestr, technology;

    public static BatteryHelper getBatteryInfo(Context context) {
//        final Context context = this.context;

        String[] mapChargeStatus = {"NA", "Unknown", "Charging", "Discharging", "Not Charging", "Full"};
        String[] mapChargeType = {"Battery", "AC", "USB"};

        BatteryHelper battery = new BatteryHelper();

        final Intent intentBattChange = context.getApplicationContext().registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        battery.rawlevel   = intentBattChange.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        battery.scale   = intentBattChange.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        battery.chargestatus   = intentBattChange.getIntExtra(BatteryManager.EXTRA_STATUS, -1); // 1 = Unknown, 2 = Charging, 3 = Discharging, 4 = Not Charging, 5 = Full
        battery.chargetype     = intentBattChange.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1); // 0 = battery, 1 = AC, 2 = USB

        battery.health         = intentBattChange.getIntExtra(BatteryManager.EXTRA_HEALTH, -1);
        battery.temperature    = intentBattChange.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);
        battery.voltage        = intentBattChange.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1);

        battery.technology     = intentBattChange.getExtras().getString(BatteryManager.EXTRA_TECHNOLOGY, "");
        battery.present        = intentBattChange.getExtras().getBoolean(BatteryManager.EXTRA_PRESENT, false);


        battery.level   = (battery.rawlevel * 100) / battery.scale;
        battery.ischarging       = battery.chargestatus == BatteryManager.BATTERY_STATUS_CHARGING
                || battery.chargestatus == BatteryManager.BATTERY_STATUS_FULL;
        battery.chargeUSB       = battery.chargetype == BatteryManager.BATTERY_PLUGGED_USB;
        battery.chargeAC        = battery.chargetype == BatteryManager.BATTERY_PLUGGED_AC;
        battery.chargestatusstr = mapChargeStatus[battery.chargestatus];
        battery.chargetypestr   = mapChargeType[battery.chargetype];

        return battery;
    }

}
