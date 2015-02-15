package com.voidonaut.batterytrends;

/**
 * Created by Pagan Winter on 1/10/15.
 */
public interface Constants {
    public static final String LOG = "com.voidonaut.batterytrends";

/*
    public static final int BATTERY_EVENT_CONNECT       = 1;
    public static final int BATTERY_EVENT_DISCONNECT    = 2;
    public static final int BATTERY_EVENT_BOOTUP        = 3;
    public static final int BATTERY_EVENT_SHUTDOWN      = 4;
    public static final int BATTERY_EVENT_FULLCHARGE    = 5;
*/

    public static final int BATTERY_EVENT_SHUTDOWN      = 0;
    public static final int BATTERY_EVENT_BOOTUP        = 1;
    public static final int BATTERY_EVENT_CONNECT       = 2;
    public static final int BATTERY_EVENT_DISCONNECT    = 3;
    public static final int BATTERY_EVENT_RESERVED      = 4;
    public static final int BATTERY_EVENT_FULLCHARGE    = 5;

    public static final int BATTERY_STATE_OFF           = 0;
    public static final int BATTERY_STATE_UNKNOWN       = 1;
    public static final int BATTERY_STATE_CHARGING      = 2;
    public static final int BATTERY_STATE_DISCHARGING   = 3;
    public static final int BATTERY_STATE_NOT_CHARGING  = 4;
    public static final int BATTERY_STATE_FULL          = 5;

    public static final String[] EVENT_TYPE_MAP = {"Shutdown", "Bootup", "Connect", "Disconnect", "NA", "Full Charge"};
    public static final String[] CHARGE_STATE_MAP = {"Off", "Unknown", "Charging", "Discharging", "Not Charging", "Full"};
    public static final String[] CHARGE_TYPE_MAP = {"Battery", "AC", "USB"};

}
