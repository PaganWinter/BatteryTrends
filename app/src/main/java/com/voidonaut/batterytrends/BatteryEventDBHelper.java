package com.voidonaut.batterytrends;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Pagan Winter on 1/1/15.
 */
public class BatteryEventDBHelper extends SQLiteOpenHelper {
    Context context;

    private final static int DB_VERSION = 1;
    private final static String DB_NAME = "battery_trends.db";

    public static final String TABLE_BATTERY_EVENT = "battery_event";

    public static final String COLUMN_ID            = "_id";
    public static final String COLUMN_START_TIME    = "start_time";
    public static final String COLUMN_END_TIME      = "end_time";
    public static final String COLUMN_DURATION      = "duration";
    public static final String COLUMN_EVENT_TYPE    = "event_type";
    public static final String COLUMN_CHARGE_STATE  = "charge_state"; // TODO
    public static final String COLUMN_CHARGE_STATUS = "charge_status";
    public static final String COLUMN_CHARGE_TYPE   = "charge_type";
    public static final String COLUMN_CHARGE_LEVEL  = "charge_level";
    public static final String COLUMN_LEVEL_CHANGE  = "level_change";

    public static final String[] COLUMNS = { COLUMN_ID, COLUMN_START_TIME, COLUMN_END_TIME,
            COLUMN_DURATION, COLUMN_EVENT_TYPE, COLUMN_CHARGE_STATE, COLUMN_CHARGE_STATUS,
            COLUMN_CHARGE_TYPE, COLUMN_CHARGE_LEVEL, COLUMN_LEVEL_CHANGE };

    // Database creation sql statement
    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_BATTERY_EVENT + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_START_TIME + " INTEGER NOT NULL, " +
            COLUMN_END_TIME + " INTEGER, " +
            COLUMN_DURATION + " INTEGER, " +
            COLUMN_EVENT_TYPE + " INTEGER NOT NULL, " +
            COLUMN_CHARGE_STATE + " INTEGER NOT NULL, " +
            COLUMN_CHARGE_STATUS + " INTEGER NOT NULL, " +
            COLUMN_CHARGE_TYPE + " INTEGER NOT NULL, " +
            COLUMN_CHARGE_LEVEL + " INTEGER NOT NULL, " +
            COLUMN_LEVEL_CHANGE + " INTEGER" +
            ");";

    public BatteryEventDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        // Store the context for later use
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(TABLE_CREATE);
        Log.d(Constants.LOG, "Created Table: " + TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(Constants.LOG, "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
        // TODO: Export DB to SD Card
//        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BATTERY_EVENT);
//        onCreate(db);
    }

}
