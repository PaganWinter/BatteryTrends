package com.voidonaut.batterytrends;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

//import java.sql.SQLException;
import android.database.SQLException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pagan Winter on 1/11/15.
 */
public class BatteryEventDAO {
    private SQLiteDatabase database;
    private BatteryEventDBHelper dbHelper;
    private String[] allColumns = BatteryEventDBHelper.COLUMNS;

    public BatteryEventDAO(Context context) {
        dbHelper = new BatteryEventDBHelper(context);
    }
    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }
    public void close() {
        dbHelper.close();
    }

    public void insertEvent(BatteryEvent event) {
        ContentValues values = new ContentValues();
        values.put(BatteryEventDBHelper.COLUMN_START_TIME, event.getStartTime());
        values.put(BatteryEventDBHelper.COLUMN_EVENT_TYPE, event.getEventType());
        values.put(BatteryEventDBHelper.COLUMN_CHARGE_STATE, event.getChargeState());
        values.put(BatteryEventDBHelper.COLUMN_CHARGE_STATUS, event.getChargeStatus());
        values.put(BatteryEventDBHelper.COLUMN_CHARGE_TYPE, event.getChargeType());
        values.put(BatteryEventDBHelper.COLUMN_CHARGE_LEVEL, event.getChargeLevel());

        long insertId = database.insert(BatteryEventDBHelper.TABLE_BATTERY_EVENT, null, values);
        event.setEventId(insertId);
/*
        Cursor cursor = database.query(BatteryEventDBHelper.TABLE_BATTERY_EVENT,
                allColumns, BatteryEventDBHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        BatteryEvent newEvent = cursorToComment(cursor);
        cursor.close();
        return newEvent;
*/
    }

    public long updateEvent(BatteryEvent event) {
        long i = -1;
        ContentValues values = new ContentValues();
        values.put(BatteryEventDBHelper.COLUMN_END_TIME, event.getEndTime());
        values.put(BatteryEventDBHelper.COLUMN_DURATION, event.getDuration());
        values.put(BatteryEventDBHelper.COLUMN_LEVEL_CHANGE, event.getLevelChange());

//        Log.d(Constants.LOG, "updateEvent updating event with id: " + String.valueOf(event.getId()) + " with values: " + values.toString());
        database.beginTransaction();

        try {
    //        String selectQuery = "UPDATE battery_event SET duration="+event.getDuration()+" WHERE _id='"+event.getId()+"'";
            String updateQuery = "UPDATE battery_event SET duration=666 WHERE _id=25";
            database.execSQL(updateQuery);

            i = database.update(
                    BatteryEventDBHelper.TABLE_BATTERY_EVENT,
                    values,
                    BatteryEventDBHelper.COLUMN_ID + "=?",
                    new String[] { String.valueOf(event.getId()) });
            database.setTransactionSuccessful();
    //        Log.d(Constants.LOG, "updateEvent (i): " + i);
            return i;
        } catch (Exception e) {
            Log.e(Constants.LOG, "Error in transaction: " + e.toString());
            return i;
        } finally {
            database.endTransaction();
            return i;
        }
    }

    public void deleteEvent(BatteryEvent event) {
        database.delete(
                BatteryEventDBHelper.TABLE_BATTERY_EVENT,
                BatteryEventDBHelper.COLUMN_ID + " = ?",
                new String[] { String.valueOf(event.getId()) });
        Log.d( Constants.LOG, "Comment deleted with id: " + String.valueOf(event.getId()) );
    }

    // Getting single Event
    public BatteryEvent getEvent(int id) {
        Cursor cursor = database.query(
                BatteryEventDBHelper.TABLE_BATTERY_EVENT,
                allColumns,
                BatteryEventDBHelper.COLUMN_ID + "=?",
                new String[] { String.valueOf(id) },
                null,
                null,
                null);

        if (cursor != null)
            cursor.moveToFirst();
        BatteryEvent be = new BatteryEvent(
                cursor.getLong(cursor.getColumnIndexOrThrow(BatteryEventDBHelper.COLUMN_ID)),
                cursor.getLong(cursor.getColumnIndexOrThrow(BatteryEventDBHelper.COLUMN_START_TIME)),
                cursor.getInt(cursor.getColumnIndexOrThrow(BatteryEventDBHelper.COLUMN_EVENT_TYPE)),
                cursor.getInt(cursor.getColumnIndexOrThrow(BatteryEventDBHelper.COLUMN_CHARGE_STATE)),
                cursor.getInt(cursor.getColumnIndexOrThrow(BatteryEventDBHelper.COLUMN_CHARGE_STATUS)),
                cursor.getInt(cursor.getColumnIndexOrThrow(BatteryEventDBHelper.COLUMN_CHARGE_TYPE)),
                cursor.getInt(cursor.getColumnIndexOrThrow(BatteryEventDBHelper.COLUMN_CHARGE_LEVEL)));
/*
                Integer.parseInt(cursor.getString(0)),
                cursor.getString(1),
                cursor.getString(2));
*/
        return be;
    }

    public BatteryEvent getLastEvent() {
        Cursor cursor = database.query(
                BatteryEventDBHelper.TABLE_BATTERY_EVENT,
                allColumns,
                null,
                null,
                null,
                null,
                BatteryEventDBHelper.COLUMN_ID + " DESC"
        );

        cursor.moveToFirst();
        if (!cursor.isAfterLast()) {
            BatteryEvent be = new BatteryEvent(
                    cursor.getLong(cursor.getColumnIndexOrThrow(BatteryEventDBHelper.COLUMN_ID)),
                    cursor.getLong(cursor.getColumnIndexOrThrow(BatteryEventDBHelper.COLUMN_START_TIME)),
                    cursor.getLong(cursor.getColumnIndexOrThrow(BatteryEventDBHelper.COLUMN_END_TIME)),
                    cursor.getLong(cursor.getColumnIndexOrThrow(BatteryEventDBHelper.COLUMN_DURATION)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(BatteryEventDBHelper.COLUMN_EVENT_TYPE)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(BatteryEventDBHelper.COLUMN_CHARGE_STATE)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(BatteryEventDBHelper.COLUMN_CHARGE_STATUS)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(BatteryEventDBHelper.COLUMN_CHARGE_TYPE)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(BatteryEventDBHelper.COLUMN_CHARGE_LEVEL)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(BatteryEventDBHelper.COLUMN_LEVEL_CHANGE)));

            cursor.close();
            return be;
        }
        else {
            cursor.close();
            return null;
        }

/*
        if (cursor != null) {
            cursor.moveToFirst();
        }
*/
    }

    public List<BatteryEvent> getAllEvents() {
        List<BatteryEvent> events = new ArrayList<BatteryEvent>();
        Cursor cursor = database.query(
                BatteryEventDBHelper.TABLE_BATTERY_EVENT,
                allColumns,
                null,
                null,
                null,
                null,
                null
        );

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            BatteryEvent event = new BatteryEvent(
                    cursor.getLong(cursor.getColumnIndexOrThrow(BatteryEventDBHelper.COLUMN_ID)),
                    cursor.getLong(cursor.getColumnIndexOrThrow(BatteryEventDBHelper.COLUMN_START_TIME)),
                    cursor.getLong(cursor.getColumnIndexOrThrow(BatteryEventDBHelper.COLUMN_END_TIME)),
                    cursor.getLong(cursor.getColumnIndexOrThrow(BatteryEventDBHelper.COLUMN_DURATION)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(BatteryEventDBHelper.COLUMN_EVENT_TYPE)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(BatteryEventDBHelper.COLUMN_CHARGE_STATE)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(BatteryEventDBHelper.COLUMN_CHARGE_STATUS)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(BatteryEventDBHelper.COLUMN_CHARGE_TYPE)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(BatteryEventDBHelper.COLUMN_CHARGE_LEVEL)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(BatteryEventDBHelper.COLUMN_LEVEL_CHANGE)));
            events.add(event);
            cursor.moveToNext();
        }
        cursor.close();

        return events;

/*
        if (cursor != null) {
            cursor.moveToFirst();
        }
*/
    }


    /* Event Stats */

    public long getDischargeTimeTotal(Context context) {
        String queryStr = "";
        Cursor cursor;
        queryStr = "SELECT SUM(" + BatteryEventDBHelper.COLUMN_DURATION + ") AS totalDischargeTime " +
                "FROM " + BatteryEventDBHelper.TABLE_BATTERY_EVENT + " " +
                "WHERE " + BatteryEventDBHelper.COLUMN_CHARGE_STATE + " = ? AND duration NOT NULL AND duration > " + (60*60);
        cursor = database.rawQuery(queryStr, new String[] {Integer.toString(Constants.BATTERY_STATE_DISCHARGING)});

        if (cursor != null) {
            cursor.moveToFirst();
            long totalDischargeTime = cursor.getLong(cursor.getColumnIndexOrThrow("totalDischargeTime"));
            cursor.close();
            return totalDischargeTime;
        }
        else {
            return -1;
        }
    }

    public long getDischargeLevelTotal(Context context) {
        String queryStr = "";
        Cursor cursor;
        queryStr = "SELECT SUM(" + BatteryEventDBHelper.COLUMN_LEVEL_CHANGE + ") AS totalDischargeLevel " +
                "FROM " + BatteryEventDBHelper.TABLE_BATTERY_EVENT + " " +
                "WHERE " + BatteryEventDBHelper.COLUMN_CHARGE_STATE + " = ? AND duration NOT NULL AND duration > " + (60*60);
        cursor = database.rawQuery(queryStr, new String[] {Integer.toString(Constants.BATTERY_STATE_DISCHARGING)});

        if (cursor != null) {
            cursor.moveToFirst();
            if (!cursor.isAfterLast()) {
                long totalDischargeLevel = -1 * cursor.getLong(cursor.getColumnIndexOrThrow("totalDischargeLevel"));
                cursor.close();
                return totalDischargeLevel;
            }
            else {
                return 1;
            }
        }
        else {
            return -1;
        }
    }

    public long getDischargeTimeMonth(Context context) {
        String queryStr = "";
        Cursor cursor;
        queryStr = "SELECT SUM(" + BatteryEventDBHelper.COLUMN_DURATION + ") AS totalDischargeTime " +
                "FROM " + BatteryEventDBHelper.TABLE_BATTERY_EVENT + " " +
                "WHERE " + BatteryEventDBHelper.COLUMN_CHARGE_STATE + " = ? AND duration NOT NULL AND " + BatteryEventDBHelper.COLUMN_START_TIME + ">" + (System.currentTimeMillis()/1000 - 60*60*24*30) + " AND duration > " + (60*60);
        Log.d(Constants.LOG, "queryStr: "+queryStr);
        cursor = database.rawQuery(queryStr, new String[] {Integer.toString(Constants.BATTERY_STATE_DISCHARGING)});

        if (cursor != null) {
            cursor.moveToFirst();
            if (!cursor.isAfterLast()) {
                long totalDischargeTime = cursor.getLong(cursor.getColumnIndexOrThrow("totalDischargeTime"));
                cursor.close();
                return totalDischargeTime;
            }
            else {
                return 1;
            }
        }
        else {
            return -1;
        }
    }

    public long getDischargeLevelMonth(Context context) {
        String queryStr = "";
        Cursor cursor;
        queryStr = "SELECT SUM(" + BatteryEventDBHelper.COLUMN_LEVEL_CHANGE + ") AS totalDischargeLevel " +
                "FROM " + BatteryEventDBHelper.TABLE_BATTERY_EVENT + " " +
                "WHERE " + BatteryEventDBHelper.COLUMN_CHARGE_STATE + " = ? AND duration NOT NULL AND " + BatteryEventDBHelper.COLUMN_START_TIME + ">" + (System.currentTimeMillis()/1000 - 60*60*24*30) + " AND duration > " + (60*60);
        Log.d(Constants.LOG, "queryStr: "+queryStr);
        cursor = database.rawQuery(queryStr, new String[] {Integer.toString(Constants.BATTERY_STATE_DISCHARGING)});

        if (cursor != null) {
            cursor.moveToFirst();
            long totalDischargeLevel = -1 * cursor.getLong(cursor.getColumnIndexOrThrow("totalDischargeLevel"));
            cursor.close();
            return totalDischargeLevel;
        }
        else {
            return -1;
        }
    }

/*
    public BatteryStats computeStatsOld(Context context) {
        BatteryStats bs = new BatteryStats();
        String queryStr = "";
        Cursor cursor;

        BatteryHelper battery = BatteryHelper.getBatteryInfo(context);
        bs.currLevel = battery.level;
        bs.currTime = System.currentTimeMillis()/1000;
        bs.chargeLeft = 100 - bs.currLevel;

        queryStr = "SELECT SUM(" + BatteryEventDBHelper.COLUMN_DURATION + ") AS totalDischargeTime, SUM(" + BatteryEventDBHelper.COLUMN_LEVEL_CHANGE + ") AS totalDischargeLevel " +
                "FROM " + BatteryEventDBHelper.TABLE_BATTERY_EVENT + " " +
                "WHERE " + BatteryEventDBHelper.COLUMN_CHARGE_STATE + " = ? AND duration NOT NULL";
        cursor = database.rawQuery(queryStr, new String[] {Integer.toString(Constants.BATTERY_STATE_DISCHARGING)});

        if (cursor != null) {
            cursor.moveToFirst();
            bs.totalDischargeTime   = cursor.getLong(cursor.getColumnIndexOrThrow("totalDischargeTime"));
            bs.totalDischargeLevel  = cursor.getLong(cursor.getColumnIndexOrThrow("totalDischargeLevel"));

//            bs.avgDischargeRate     = bs.totalDischargeLevel/(bs.totalDischargeTime/3600000);
//            bs.avgDischargeRate     = bs.totalDischargeLevel/bs.totalDischargeTime; // per second
            bs.avgDischargeRate     = (bs.totalDischargeLevel * 3600)/bs.totalDischargeTime; // per hour
//            bs.avgLifePerCharge     = (bs.totalDischargeTime/3600000)/bs.totalDischargeLevel * 100;
//            bs.avgLifePerCharge     = -1 * bs.totalDischargeTime/(36000 * bs.totalDischargeLevel);
//            bs.avgLifePerCharge     = (long) (-1 * (100/bs.avgDischargeRate));
            bs.avgLifePerCharge     = (long) (-1 * (bs.totalDischargeTime/bs.totalDischargeLevel) * 100); // seconds per %;

            Log.d(Constants.LOG, "totalDischargeTime: " + bs.totalDischargeTime);
            Log.d(Constants.LOG, "totalDischargeLevel: " + bs.totalDischargeLevel);
            Log.d(Constants.LOG, "avgDischargeRate: " + bs.avgDischargeRate);
            Log.d(Constants.LOG, "avgLifePerCharge: " + bs.avgLifePerCharge);

            cursor.close();
        }

        queryStr = "SELECT " + BatteryEventDBHelper.COLUMN_START_TIME + ", " + BatteryEventDBHelper.COLUMN_CHARGE_LEVEL + " " +
                "FROM " + BatteryEventDBHelper.TABLE_BATTERY_EVENT + " " +
                "WHERE " + BatteryEventDBHelper.COLUMN_EVENT_TYPE + " IN (?) " +
                "ORDER BY " + BatteryEventDBHelper.COLUMN_START_TIME + " DESC " +
                "LIMIT 1";
        cursor = database.rawQuery(queryStr, new String[] {Integer.toString(Constants.BATTERY_EVENT_DISCONNECT)});

        if (cursor != null) {
            cursor.moveToFirst();
            bs.discTime     = cursor.getLong(cursor.getColumnIndexOrThrow(BatteryEventDBHelper.COLUMN_START_TIME));
            bs.discLevel    = cursor.getInt(cursor.getColumnIndexOrThrow(BatteryEventDBHelper.COLUMN_CHARGE_LEVEL));

            bs.chargeSinceDisc = bs.discLevel - bs.currLevel;
            bs.timeSinceDisc = bs.currTime - bs.discTime;
//            bs.currDischargeRate = (bs.chargeSinceDisc * 3600000)/bs.timeSinceDisc;
//            bs.currDischargeRate    = bs.chargeSinceDisc/bs.timeSinceDisc; // per second
            bs.currDischargeRate    = (bs.chargeSinceDisc * 3600)/bs.timeSinceDisc; // per hour

            bs.avgLifeLeft = (bs.avgLifePerCharge * bs.currLevel)/100;
            if (bs.chargeSinceDisc != 0) {
//                bs.currLifeLeft = (bs.timeSinceDisc/3600000)/bs.chargeSinceDisc * bs.currLevel;
                bs.currLifeLeft = bs.currLevel/bs.currDischargeRate;
            }

            Log.d(Constants.LOG, "discTime: " + bs.discTime);
            Log.d(Constants.LOG, "discLevel: " + bs.discLevel);
            Log.d(Constants.LOG, "chargeSinceDisc: " + bs.chargeSinceDisc);
            Log.d(Constants.LOG, "timeSinceDisc: " + bs.timeSinceDisc);
            Log.d(Constants.LOG, "currDischargeRate: " + bs.currDischargeRate);
            Log.d(Constants.LOG, "avgLifeLeft: " + bs.avgLifeLeft);
            Log.d(Constants.LOG, "currLifeLeft: " + bs.currLifeLeft);

            cursor.close();
        }

        return bs;
    }
*/


    public BatteryEvent getlastDiscEvent(Context context) {
        Cursor cursor = database.query(
                BatteryEventDBHelper.TABLE_BATTERY_EVENT,
                allColumns,
                BatteryEventDBHelper.COLUMN_EVENT_TYPE + " IN (?) ",
                new String[] {Integer.toString(Constants.BATTERY_STATE_DISCHARGING)},
                null,
                null,
                BatteryEventDBHelper.COLUMN_START_TIME + " DESC"
        );

        long levelDisc = -1;
        long timeDisc = -1;

//        String queryStr = "SELECT " + BatteryEventDBHelper.COLUMN_START_TIME + " AS timeDisc, " + BatteryEventDBHelper.COLUMN_CHARGE_LEVEL + " AS levelDisc " +
          String queryStr = "SELECT * " +
                "FROM " + BatteryEventDBHelper.TABLE_BATTERY_EVENT + " " +
                "WHERE " + BatteryEventDBHelper.COLUMN_EVENT_TYPE + " IN (?) " +
                "ORDER BY " + BatteryEventDBHelper.COLUMN_START_TIME + " DESC " +
                "LIMIT 1";
//        Cursor cursor = database.rawQuery(queryStr, new String[] {Integer.toString(Constants.BATTERY_STATE_DISCHARGING)});

        if (cursor != null) {
            cursor.moveToFirst();
            if (!cursor.isAfterLast()) {
//                levelDisc = cursor.getLong(cursor.getColumnIndexOrThrow("levelDisc"));
//                timeDisc = cursor.getLong(cursor.getColumnIndexOrThrow("timeDisc"));

                BatteryEvent be = new BatteryEvent(
                        cursor.getLong(cursor.getColumnIndexOrThrow(BatteryEventDBHelper.COLUMN_ID)),
                        cursor.getLong(cursor.getColumnIndexOrThrow(BatteryEventDBHelper.COLUMN_START_TIME)),
                        cursor.getLong(cursor.getColumnIndexOrThrow(BatteryEventDBHelper.COLUMN_END_TIME)),
                        cursor.getLong(cursor.getColumnIndexOrThrow(BatteryEventDBHelper.COLUMN_DURATION)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(BatteryEventDBHelper.COLUMN_EVENT_TYPE)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(BatteryEventDBHelper.COLUMN_CHARGE_STATE)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(BatteryEventDBHelper.COLUMN_CHARGE_STATUS)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(BatteryEventDBHelper.COLUMN_CHARGE_TYPE)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(BatteryEventDBHelper.COLUMN_CHARGE_LEVEL)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(BatteryEventDBHelper.COLUMN_LEVEL_CHANGE)));

                cursor.close();
                Log.d(Constants.LOG, be.toString());
                return be;
            }
            else {
                cursor.close();
                return null;
            }
        }
        return null;
    }

    public BatteryStats computeStatsCurr(Context context, long levelCurr, long timeCurr) {
        long levelDisc = -1;
        long timeDisc = -1;

        String queryStr = "SELECT " + BatteryEventDBHelper.COLUMN_START_TIME + " AS timeDisc, " + BatteryEventDBHelper.COLUMN_CHARGE_LEVEL + " AS levelDisc " +
                "FROM " + BatteryEventDBHelper.TABLE_BATTERY_EVENT + " " +
                "WHERE " + BatteryEventDBHelper.COLUMN_EVENT_TYPE + " IN (?) " +
                "ORDER BY " + BatteryEventDBHelper.COLUMN_START_TIME + " DESC " +
                "LIMIT 1";
        Cursor cursor = database.rawQuery(queryStr, new String[] {Integer.toString(Constants.BATTERY_STATE_DISCHARGING)});

        if (cursor != null) {
            cursor.moveToFirst();
            if (!cursor.isAfterLast()) {
                levelDisc = cursor.getLong(cursor.getColumnIndexOrThrow("levelDisc"));
                timeDisc = cursor.getLong(cursor.getColumnIndexOrThrow("timeDisc"));
            }
        }

        /* Current Cycle Stats */
        long levelUsedCurr = levelDisc - levelCurr;
        long timeUsedCurr = timeCurr - timeDisc;
        BatteryStats bsCurr = new BatteryStats(levelUsedCurr, timeUsedCurr, levelCurr, timeCurr, levelDisc, timeDisc);
//        BatteryStats bsCurr = new BatteryStats();
        bsCurr = bsCurr.computeStats();
        return bsCurr;
    }


    public BatteryStats computeStatsTotal(Context context, long levelCurr, long timeCurr) {
        long levelDisc = -1;
        long timeDisc = -1;

        String queryStr = "SELECT " + BatteryEventDBHelper.COLUMN_START_TIME + " AS timeDisc, " + BatteryEventDBHelper.COLUMN_CHARGE_LEVEL + " AS levelDisc " +
                "FROM " + BatteryEventDBHelper.TABLE_BATTERY_EVENT + " " +
                "WHERE " + BatteryEventDBHelper.COLUMN_EVENT_TYPE + " IN (?) " +
                "ORDER BY " + BatteryEventDBHelper.COLUMN_START_TIME + " DESC " +
                "LIMIT 1";
        Cursor cursor = database.rawQuery(queryStr, new String[] {Integer.toString(Constants.BATTERY_STATE_DISCHARGING)});

        if (cursor != null) {
            cursor.moveToFirst();
            if (!cursor.isAfterLast()) {
                levelDisc = cursor.getLong(cursor.getColumnIndexOrThrow("levelDisc"));
                timeDisc = cursor.getLong(cursor.getColumnIndexOrThrow("timeDisc"));
            }
        }

        /* All Time Stats */
        long levelUsedTotal = 0;
        long timeUsedTotal = 0;
        BatteryStats bsCurr = new BatteryStats(levelUsedTotal, timeUsedTotal, levelCurr, timeCurr, levelDisc, timeDisc);
//        BatteryStats bsCurr = new BatteryStats();
        bsCurr = bsCurr.computeStats();
        return bsCurr;
    }

/*
Average Rate/Hour, Average Life:
--------------------------------
SELECT *
FROM {Events}
WHERE charge_state = discharging AND duration NOT NULL

SELECT SUM(duration) AS duration_sum, SUM(charge_delta) delta_sum
FROM {Events}
WHERE charge_state = discharging AND duration NOT NULL

Average Rate/Hour:
avgRatePerHour = delta_sum/(duration_sum/3600);

Average Battery Life:
avgLifePerCharge = (duration_sum/3600)/delta_sum * 100;


Since last charge:
------------------
SELECT start_time, charge_level
FROM {Events}
WHERE charge_event IN (disconnect)
ORDER BY start_time DESC LIMIT 1;

currLevel = new getBatteryInfo().getLevel();
chargeLeft = 100-currLevel;

changeSinceDisconnect = charge_level - currLevel;
timeSinceDisconnect = currentTime - start_time;

currRatePerHour = changeSinceDisconnect/(timeSinceDisconnect/3600);

avgLifeLeft = avgLifePerCharge/100 * chargeLeft;
currLifeLeft = (timeSinceDisconnect/3600)/changeSinceDisconnect * chargeLeft;


Time left (avg): {avgLifeLeft}
Time left (since last charge): {currLifeLeft}

Used {changeSinceDisconnect} in {timeSinceDisconnect}
({currRatePerHour}%/h)
 */
}
