package com.voidonaut.batterytrends;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * Created by Pagan Winter on 1/1/15.
 */
public class Utility {
    public static void appendLog(String text, String file) {
        File logFile = new File("sdcard/"+file);
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            }
            catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        try {
            //BufferedWriter for performance, true to set append to file flag
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            buf.append(text);
            buf.newLine();
            buf.close();
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static String readLog(String file) {
        File logFile = new File("sdcard/"+file);
        String text = "";
        if (!logFile.exists()) {
            try {
//                logFile.createNewFile();
            }
            catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        try {
            //BufferedWriter for performance, true to set append to file flag
            BufferedReader buf = new BufferedReader(new FileReader(logFile));
            text = buf.readLine();
            buf.close();
            return text;
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return text;
        }
    }


/*
    public void writeToFile(String data, String file) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(this.context.getApplicationContext().openFileOutput(file, Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    private void writeStat(String log_text) {
        try {
            FileWriter out = null;
            if (log_file != null) {
                try {
                    out = new FileWriter(log_file, true);
                }
                catch (Exception e) {}
            }
            if (out == null) {
//                File sdCard = Environment.getExternalStorageDirectory();
                File sdCard = Environment.getDataDirectory();
                if (sdCard == null) {
                    throw new Exception("external storage dir not found");
                }
                log_file = new File(sdCard, "BattTrendt/batttrend.csv");
                boolean fileExists = log_file.exists();
                if (!fileExists) {
                    log_file.getParentFile().mkdirs();
                    log_file.createNewFile();
                }
                if (!log_file.exists())
                    throw new Exception("creation of file '"+log_file.toString()+"' failed");
                if (!log_file.canWrite())
                    throw new Exception("file '"+log_file.toString()+"' is not writable");
                out = new FileWriter(log_file, true);
                if (!fileExists) {
//    					String header = "TIMESTAMP(ms)|LEVEL(%)|PLUGGED(AC/USB)|STATUS(CHRG)|VOLTAGE(mV)|TEMPERATURE(C)|SCREEN|BRIGHTNESS(%)";
//    					out.write(header);
//    					out.write("\n");
                }
            }
            String battStatRecord = log_text;
            out.write(battStatRecord);
            out.flush();
            out.close();
        } catch (Exception e) {
            Log.e("Exception", e.getMessage(), e);
//            Log.e(TAG, e.getMessage(),e);
        }
        Log.e("Exception", "Finished writing");
//        Log.e(TAG, "Finished writing");
    }
*/

}
