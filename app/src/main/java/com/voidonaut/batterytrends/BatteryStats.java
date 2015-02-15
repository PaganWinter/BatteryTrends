package com.voidonaut.batterytrends;

import android.database.Cursor;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Pagan Winter on 2/1/15.
 */
public class BatteryStats {
    protected long levelCurr;
    protected long timeCurr;
//    protected long levelLeft;

//    protected long levelDisc;
//    protected long timeDisc;

    protected long timeUsed;
    protected long levelUsed;

    protected double rate, ratePerHour;
    protected long timePerLevel;
    protected long batteryLife;
    protected long timeLeft;
    protected long timeEmptyAt;

    /* Constructors */
    public BatteryStats() {
    }

    public BatteryStats(long levelUsed, long timeUsed) {
        this.levelUsed = levelUsed;
        this.timeUsed = timeUsed;
    }

    public BatteryStats(long levelUsed, long timeUsed, long levelCurr, long timeCurr) {
        this.levelCurr = levelCurr;
        this.timeCurr = timeCurr;
        this.levelUsed = levelUsed;
        this.timeUsed = timeUsed;
    }

    public BatteryStats(long levelUsed, long timeUsed, long levelCurr, long timeCurr, long levelDisc, long timeDisc) {
        this.levelCurr = levelCurr;
        this.timeCurr = timeCurr;
        this.levelUsed = levelUsed;
        this.timeUsed = timeUsed;

//        this.levelDisc = levelDisc;
//        this.timeDisc = timeDisc;
    }


    public String toString() {
        String out = "";
        out = "BatteryStats [" +"\n"+
                "\tlevelCurr: "+levelCurr+" %\n"+
                "\ttimeCurr: "+timeCurr+" ("+Utils.formatDateTime(timeCurr)+")\n"+
//                "\tlevelLeft: "+levelLeft+" %\n"+
//                "\tlevelDisc: "+levelDisc+" %\n"+
//                "\ttimeDisc: "+timeDisc+" ("+Utils.formatDateTime(timeDisc)+")\n"+
                "\ttimeUsed: "+timeUsed+" ("+Utils.formatDuration(timeUsed)+")\n"+
                "\tlevelUsed: "+levelUsed+" %\n"+
                "\trate: "+rate+" "+String.format("%.3f", rate)+" %/s\n"+
                "\tratePerHour: "+ratePerHour+" "+Utils.formatDecimal(ratePerHour)+" %/h\n"+
                "\ttimePerLevel: "+timePerLevel+" s/% ("+(timePerLevel/60)+" min/%)\n"+
                "\tbatteryLife: "+batteryLife+" "+Utils.formatDuration(batteryLife)+"\n"+
//                "\tbatteryLife: "+batteryLife+" "+(batteryLife/3600)+" hours\n"+
                "\ttimeLeft: "+timeLeft+" "+Utils.formatDuration(timeLeft)+"\n"+
                "\ttimeEmptyAt: "+timeEmptyAt+" "+Utils.formatDateTime(timeEmptyAt)+"\n"+
                "]";
        return out;
    }

    public BatteryStats computeStats() {
//        this.levelLeft      = 100 - this.levelCurr;
        this.rate           = (double)this.levelUsed / (double)this.timeUsed; // %/sec
        this.ratePerHour    = this.rate * 60 * 60; // %/hour
        this.timePerLevel   = (long) (this.timeUsed / this.levelUsed); // sec/%
        this.batteryLife    = this.timePerLevel * 100; // sec
        this.timeLeft       = this.timePerLevel * this.levelCurr; // sec
        this.timeEmptyAt    = this.timeCurr + this.timeLeft; // hh:mm
        return this;
    }

/*
    protected long totalDischargeTime, avgLifePerCharge;
    protected float totalDischargeLevel;
    protected float avgDischargeRate, currDischargeRate;
    protected float avgLifeLeft, currLifeLeft;
    protected int currLevel, discLevel, chargeLeft, chargeSinceDisc;
    protected long currTime, discTime, timeSinceDisc;


    public String toStringOld() {
        String out = "";
        out = "BatteryStats [" +"\n"+
                "currLevel: "+currLevel+"%,\n"+
                "totalDischargeTime: "+totalDischargeTime+" "+String.format("%d:%02d", (totalDischargeTime)/3600, ((totalDischargeTime)%3600)/60)+",\n"+
                "totalDischargeLevel: "+totalDischargeLevel+",\n"+
                "avgDischargeRate: "+String.format("%.3f", avgDischargeRate)+"%/h"+",\n"+
                "avgLifePerCharge: "+String.format("%d:%02d", (avgLifePerCharge)/3600, ((avgLifePerCharge)%3600)/60)+" hours"+",\n"+
                "discTime: "+discTime+" "+new SimpleDateFormat("dd/MMM HH:mm").format(new Date(discTime))+",\n"+
                "discLevel: "+discLevel+"%"+",\n"+
                "chargeSinceDisc: "+chargeSinceDisc+",\n"+
                "timeSinceDisc: "+timeSinceDisc+" "+String.format("%d:%02d", (timeSinceDisc)/3600, ((timeSinceDisc)%3600)/60)+",\n"+
                "currDischargeRate: "+currDischargeRate+",\n"+
                "avgLifeLeft: "+avgLifeLeft+" hours"+",\n"+
                "currLifeLeft: "+currLifeLeft+" hours"+"\n"+
              "]";
        return out;
    }
*/
}
