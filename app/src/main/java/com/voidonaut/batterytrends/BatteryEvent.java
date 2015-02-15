package com.voidonaut.batterytrends;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Pagan Winter on 1/10/15.
 */
public class BatteryEvent {
    protected long mEventId;
    protected long mStartTime;
    protected long mEndTime;
    protected long mDuration;
    protected int mEventType;
    protected int mChargeState;
    protected int mChargeStatus;
    protected int mChargeType;
    protected int mChargeLevel;
//    private int mChargeDelta;
    protected int mLevelChange;

    // constructor
    public BatteryEvent() {
    }

    // constructor
    public BatteryEvent(long startTime, int eventType, int chargeState, int chargeStatus, int chargeType, int chargeLevel) {
        this.mStartTime     = startTime;
        this.mEventType     = eventType;
        this.mChargeState   = chargeState;
        this.mChargeStatus  = chargeStatus;
        this.mChargeType    = chargeType;
        this.mChargeLevel   = chargeLevel;
    }

    // constructor
    public BatteryEvent(long eventId, long startTime, int eventType, int chargeState, int chargeStatus, int chargeType, int chargeLevel) {
        this.mEventId       = eventId;
        this.mStartTime     = startTime;
        this.mEventType     = eventType;
        this.mChargeState   = chargeState;
        this.mChargeStatus  = chargeStatus;
        this.mChargeType    = chargeType;
        this.mChargeLevel   = chargeLevel;
    }

    // constructor
    public BatteryEvent(long eventId, long startTime, long endTime, long duration, int eventType, int chargeState, int chargeStatus, int chargeType, int chargeLevel, int levelChange) {
        this.mEventId       = eventId;
        this.mStartTime     = startTime;
        this.mEndTime       = endTime;
        this.mDuration      = duration;
        this.mEventType     = eventType;
        this.mChargeState   = chargeState;
        this.mChargeStatus  = chargeStatus;
        this.mChargeType    = chargeType;
        this.mChargeLevel   = chargeLevel;
        this.mLevelChange   = levelChange;
    }

    /*
    public void setEvent(long startTime, int eventType, int chargeState, int chargeStatus, int chargeType, int chargeLevel) {
        this.mStartTime     = startTime;
        this.mEventType     = eventType;
        this.mChargeState   = chargeState;
        this.mChargeStatus  = chargeStatus;
        this.mChargeType    = chargeType;
        this.mChargeLevel   = chargeLevel;
    }
*/
    public void setEventId(long eventId) {
        this.mEventId = eventId;
    }
    public void setEndTime(long endTime) {
        this.mEndTime = endTime;
    }
    public void setDuration(long duration) {
        this.mDuration = duration;
    }
    public void setLevelChange(int levelChange) {
        this.mLevelChange = levelChange;
    }

    public long getId() {
        return this.mEventId;
    }
    public long getStartTime() {
        return this.mStartTime;
    }
    public long getEndTime() {
        return this.mEndTime;
    }
    public long getDuration() {
        return this.mDuration;
    }
    public int getEventType() {
        return this.mEventType;
    }
    public int getChargeState() {
        return this.mChargeState;
    }
    public int getChargeStatus() {
        return this.mChargeStatus;
    }
    public int getChargeType() {
        return this.mChargeType;
    }
    public int getChargeLevel() {
        return this.mChargeLevel;
    }
    public int getLevelChange() {
        return this.mLevelChange;
    }


    public String toString() {
        return "BatteryEvent [\n"
                + "\teventId="+this.mEventId+",\n"
                + "\tstartTime="+this.mStartTime + " " + Utils.formatDateTime(this.mStartTime)+",\n"
                + "\tendTime="+this.mEndTime + " " + Utils.formatDateTime(this.mEndTime)+",\n"
                + "\tduration="+this.mDuration + " " + String.format("%d:%02d", (this.mDuration)/3600, ((this.mDuration)%3600)/60) +",\n"
                + "\teventType="+this.mEventType + " " + Constants.EVENT_TYPE_MAP[this.mEventType] +",\n"
                + "\tchargeState="+this.mChargeState + " " + Constants.CHARGE_STATE_MAP[this.mChargeState] +",\n"
                + "\tchargeStatus="+this.mChargeStatus + " " + Constants.CHARGE_STATE_MAP[this.mChargeStatus] +",\n"
                + "\tchargeType="+this.mChargeType + " " + Constants.CHARGE_TYPE_MAP[this.mChargeType] +",\n"
                + "\tchargeLevel="+this.mChargeLevel+",\n"
                + "\tlevelChange="+this.mLevelChange+"\n"
                + "]";
    }


    public static BatteryEvent getPrevEvent() {
        BatteryEvent bePrevEvent = new BatteryEvent();
        /*
        SELECT *
        FROM {EVENTS_TABLE}
        ORDER BY {_ID} DESC

        bePrevEvent.mEventId        = _id;
        bePrevEvent.mStartTime      = start_time;
        bePrevEvent.mEventType      = event_type;
        bePrevEvent.mChargeStatus   = charge_status;
        bePrevEvent.mChargeType     = charge_type;
        bePrevEvent.mChargeLevel    = charge_level;
        */
        bePrevEvent.mEventId        = -1;
        bePrevEvent.mStartTime      = -1;
        bePrevEvent.mEventType      = -1;
        bePrevEvent.mChargeStatus   = -1;
        bePrevEvent.mChargeType     = -1;
        bePrevEvent.mChargeLevel    = -1;

        return bePrevEvent;
    }

    public void updateEvent(long currTime, int currLevel) {
        this.mEndTime = currTime;
        this.mDuration = this.mEndTime - this.mStartTime;
        this.mLevelChange = currLevel - this.mChargeLevel;
    }
}
