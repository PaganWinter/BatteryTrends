/*
 * Copyright (C) 2011 asksven
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

//package com.asksven.android.common.privateapiproxies;
package com.voidonaut.batterytrends.utils;


import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.SystemClock;
import android.util.Log;
import android.util.SparseArray;

/*
import com.asksven.android.common.CommonLogSettings;
import com.asksven.android.common.nameutils.UidInfo;
import com.asksven.android.common.nameutils.UidNameResolver;
import com.asksven.android.common.utils.DateUtils;
import com.asksven.android.system.AndroidVersion;
*/


/**
 * A proxy to the non-public API BatteryStats
 * http://grepcode.com/file/repository.grepcode.com/java/ext/com.google.android/android/2.3.3_r1/android/os/BatteryStats.java/?v=source
 * @author sven
 *
 */
public class BatteryStatsProxy {
	/*
	 * Instance of the BatteryStatsImpl
	 */
	private Object m_Instance = null;
	@SuppressWarnings("rawtypes")
	private Class m_ClassDefinition = null;
	
	private static final String TAG = "BatteryStatsProxy";

    /**
     * Type to be passed to getNetworkActivityCount for different
     * stats.
     */
    private static final int NETWORK_MOBILE_RX_BYTES = 0;   // received bytes using mobile data
    private static final int NETWORK_MOBILE_TX_BYTES = 1;   // transmitted bytes using mobile data
    private static final int NETWORK_WIFI_RX_BYTES = 2;     // received bytes using wifi
    private static final int NETWORK_WIFI_TX_BYTES = 3;     // transmitted bytes using wifi

	/*
	 * The UID stats are kept here as their methods / data can not be accessed
	 * outside of this class due to non-public types (Uid, Proc, etc.)
	 */
	private SparseArray<? extends Object> m_uidStats = null;
	
	/** 
	 * An instance to the UidNameResolver 
	 */
	private static BatteryStatsProxy m_proxy = null;
	
	synchronized public static BatteryStatsProxy getInstance(Context ctx) {
		if (m_proxy == null) {
			m_proxy = new BatteryStatsProxy(ctx);
		}
		
		return m_proxy;
	}
	
	public void invalidate()
	{
		m_proxy = null;
	}
	
    /**
	 * Default cctor
	 */
	private BatteryStatsProxy(Context context)  {
		/*
		 * As BatteryStats is a service we need to get a binding using the IBatteryStats.Stub.getStatistics()
		 * method (using reflection).
		 * If we would be using a public API the code would look like:
		 * @see com.android.settings.fuelgauge.PowerUsageSummary.java 
		 * protected void onCreate(Bundle icicle) {
         *  super.onCreate(icicle);
		 *	
         *  mStats = (BatteryStatsImpl)getLastNonConfigurationInstance();
		 *
         *  addPreferencesFromResource(R.xml.power_usage_summary);
         *  mBatteryInfo = IBatteryStats.Stub.asInterface(
         *       ServiceManager.getService("batteryinfo"));
         *  mAppListGroup = (PreferenceGroup) findPreference("app_list");
         *  mPowerProfile = new PowerProfile(this);
    	 * }
		 *
		 * followed by
		 * private void load() {
         *	try {
         *   byte[] data = mBatteryInfo.getStatistics();
         *   Parcel parcel = Parcel.obtain();
         *   parcel.unmarshall(data, 0, data.length);
         *   parcel.setDataPosition(0);
         *   mStats = com.android.internal.os.BatteryStatsImpl.CREATOR
         *           .createFromParcel(parcel);
         *   mStats.distributeWorkLocked(BatteryStats.STATS_SINCE_CHARGED);
         *  } catch (RemoteException e) {
         *   Log.e(TAG, "RemoteException:", e);
         *  }
         * }
		 */
		try {
	          ClassLoader cl = context.getClassLoader();
	          
	          m_ClassDefinition = cl.loadClass("com.android.internal.os.BatteryStatsImpl");
	          
	          // get the IBinder to the "batteryinfo" service
	          @SuppressWarnings("rawtypes")
			  Class serviceManagerClass = cl.loadClass("android.os.ServiceManager");
	          
	          // parameter types
	          @SuppressWarnings("rawtypes")
			  Class[] paramTypesGetService= new Class[1];
	          paramTypesGetService[0]= String.class;
	          
	          @SuppressWarnings("unchecked")
			  Method methodGetService = serviceManagerClass.getMethod("getService", paramTypesGetService);
	          
	          String service = "";
	          if (Build.VERSION.SDK_INT >= 19) {
	        	  // kitkat and following
	        	  service = "batterystats";
	          }
	          else {
	        	  service = "batteryinfo";
	          }
//TODO
              service = "batterystats";
	          // parameters
	          Object[] paramsGetService= new Object[1];
	          paramsGetService[0] = service;

	          if (LogSettings.DEBUG) {
	        	  Log.i(TAG, "invoking android.os.ServiceManager.getService(\"batteryinfo\")");
	          }

              IBinder serviceBinder = (IBinder) methodGetService.invoke(serviceManagerClass, paramsGetService);

	          if (LogSettings.DEBUG) {
	        	  Log.i(TAG, "android.os.ServiceManager.getService(\"batteryinfo\") returned a service binder");
	          }

	          // now we have a binder. Let's us that on IBatteryStats.Stub.asInterface
	          // to get an IBatteryStats
	          // Note the $-syntax here as Stub is a nested class
	          @SuppressWarnings("rawtypes")
			  Class iBatteryStatsStub = cl.loadClass("com.android.internal.app.IBatteryStats$Stub");

	          //Parameters Types
	          @SuppressWarnings("rawtypes")
			  Class[] paramTypesAsInterface= new Class[1];
	          paramTypesAsInterface[0]= IBinder.class;

	          @SuppressWarnings("unchecked")
			  Method methodAsInterface = iBatteryStatsStub.getMethod("asInterface", paramTypesAsInterface);

	          // Parameters
	          Object[] paramsAsInterface= new Object[1];
	          paramsAsInterface[0] = serviceBinder;
	          
	          if (LogSettings.DEBUG) {
	        	  Log.i(TAG, "invoking com.android.internal.app.IBatteryStats$Stub.asInterface");
	          }
              Object iBatteryStatsInstance = methodAsInterface.invoke(iBatteryStatsStub, paramsAsInterface);
	          
	          // and finally we call getStatistics from that IBatteryStats to obtain a Parcel
	          @SuppressWarnings("rawtypes")
			  Class iBatteryStats = cl.loadClass("com.android.internal.app.IBatteryStats");
	          
	          @SuppressWarnings("unchecked")
	          Method methodGetStatistics = iBatteryStats.getMethod("getStatistics");
	          
	          if (LogSettings.DEBUG) {
	        	  Log.i(TAG, "invoking getStatistics");
	          }
	          byte[] data = (byte[]) methodGetStatistics.invoke(iBatteryStatsInstance);
	          
	          if (LogSettings.DEBUG) {
	        	  Log.i(TAG, "retrieving parcel");
	          }
	          
	          Parcel parcel = Parcel.obtain();
	          parcel.unmarshall(data, 0, data.length);
	          parcel.setDataPosition(0);
	          
	          @SuppressWarnings("rawtypes")
			  Class batteryStatsImpl = cl.loadClass("com.android.internal.os.BatteryStatsImpl");

	          if (LogSettings.DEBUG) {
	        	  Log.i(TAG, "reading CREATOR field");
	          }
	          Field creatorField = batteryStatsImpl.getField("CREATOR");
	          
	          // From here on we don't need reflection anymore
	          @SuppressWarnings("rawtypes")
			  Parcelable.Creator batteryStatsImpl_CREATOR = (Parcelable.Creator) creatorField.get(batteryStatsImpl); 
	          
	          m_Instance = batteryStatsImpl_CREATOR.createFromParcel(parcel);        
	    }
		catch( Exception e ) {
			if (e instanceof InvocationTargetException && e.getCause() != null) {
				Log.e(TAG, "An exception occured in BatteryStatsProxy(). Message: " + e.getCause().getMessage());
			} else {
				Log.e(TAG, "An exception occured in BatteryStatsProxy(). Message: " + e.getMessage());
			}
	    	m_Instance = null;
	    }
	}



    /**
     * Returns the current battery realtime in microseconds.
     *
     * @param curTime the amount of elapsed realtime in microseconds.
     */
//TODO    public Long getBatteryRealtime(long curTime) throws BatteryInfoUnavailableException {
    public Long getBatteryRealtime(long curTime) throws Exception {
        Long ret = new Long(0);
        try {
            //Parameters Types
            @SuppressWarnings("rawtypes")
            Class[] paramTypes= new Class[1];
            paramTypes[0]= long.class;
            @SuppressWarnings("unchecked")
            Method method = m_ClassDefinition.getMethod("getBatteryRealtime", paramTypes);
            //Parameters
            Object[] params= new Object[1];
            params[0]= new Long(curTime);
            ret= (Long) method.invoke(m_Instance, params);
        }
        catch( IllegalArgumentException e ) {
            throw e;
        }
        catch( Exception e ) {
            ret = new Long(0);
//TODO            throw new BatteryInfoUnavailableException();
            throw e;
        }

        return ret;
    }

    /**
     * Returns the current battery uptime in microseconds.
     *
     * @param curTime the amount of elapsed realtime in microseconds.
     */
    public Long getBatteryUptime(long curTime) throws Exception {
        Long ret = new Long(0);
        try {
            //Parameters Types
            @SuppressWarnings("rawtypes")
            Class[] paramTypes = new Class[1];
            paramTypes[0] = long.class;
            @SuppressWarnings("unchecked")
            Method method = m_ClassDefinition.getMethod("getBatteryUptime", paramTypes);
            //Parameters
            Object[] params = new Object[1];
            params[0]= new Long(curTime);
            ret= (Long) method.invoke(m_Instance, params);
        }
        catch( Exception e ) {
            ret = new Long(0);
            throw e;
        }
        return ret;
    }


    /**
     * Returns the total, last, or current battery realtime in microseconds.
     *
     * @param curTime the current elapsed realtime in microseconds.
     * @param iStatsType one of STATS_SINCE_CHARGED, STATS_SINCE_UNPLUGGED, or STATS_CURRENT.
     */
    public Long computeBatteryRealtime(long curTime, int iStatsType) throws Exception {
        Long ret = new Long(0);
        try {
            //Parameters Types
            @SuppressWarnings("rawtypes")
            Class[] paramTypes= new Class[2];
            paramTypes[0]= long.class;
            paramTypes[1]= int.class;

            @SuppressWarnings("unchecked")
            Method method = m_ClassDefinition.getMethod("computeBatteryRealtime", paramTypes);

            //Parameters
            Object[] params= new Object[2];
            params[0]= new Long(curTime);
            params[1]= new Integer(iStatsType);

            ret = (Long) method.invoke(m_Instance, params);

        }
        catch( Exception e ) {
            ret = new Long(0);
            throw e;
        }
        return ret;
    }

    /**
     * Returns the total, last, or current battery uptime in microseconds.
     *
     * @param curTime the elapsed realtime in microseconds.
     * @param iStatsType one of STATS_SINCE_CHARGED, STATS_SINCE_UNPLUGGED, or STATS_CURRENT.
     */
    public Long computeBatteryUptime(long curTime, int iStatsType) throws Exception {
        Long ret = new Long(0);
        try {
            //Parameters Types
            @SuppressWarnings("rawtypes")
            Class[] paramTypes= new Class[2];
            paramTypes[0]= long.class;
            paramTypes[1]= int.class;

            @SuppressWarnings("unchecked")
            Method method = m_ClassDefinition.getMethod("computeBatteryUptime", paramTypes);

            //Parameters
            Object[] params= new Object[2];
            params[0]= new Long(curTime);
            params[1]= new Integer(iStatsType);

            ret = (Long) method.invoke(m_Instance, params);
        }
        catch( Exception e ) {
            ret = new Long(0);
            throw e;
        }
        return ret;
    }






    /**
     * Returns the time in microseconds that the screen has been on while the device was running on battery.
     *
     * @param batteryRealtime the battery realtime in microseconds (@see computeBatteryRealtime).
     * @param iStatsType one of STATS_TOTAL, STATS_LAST, or STATS_CURRENT.
     */
    public Long getScreenOnTime(long batteryRealtime, int iStatsType) throws Exception {
        Long ret = new Long(0);
        try {
            //Parameters Types
            @SuppressWarnings("rawtypes")
            Class[] paramTypes= new Class[2];
            paramTypes[0]= long.class;
            paramTypes[1]= int.class;

            @SuppressWarnings("unchecked")
            Method method = m_ClassDefinition.getMethod("getScreenOnTime", paramTypes);

            //Parameters
            Object[] params= new Object[2];
            params[0]= new Long(batteryRealtime);
            params[1]= new Integer(iStatsType);

            ret= (Long) method.invoke(m_Instance, params);
        }
        catch( Exception e ) {
            ret = new Long(0);
            throw e;
        }
        return ret;
    }




    /**
     * Returns the total, last, or current wifi on time in microseconds.
     *
     * @param batteryRealtime the battery realtime in microseconds (@see computeBatteryRealtime).
     * @param iStatsType one of STATS_TOTAL, STATS_LAST, or STATS_CURRENT.
     */
    public Long getWifiOnTime(long batteryRealtime, int iStatsType) throws Exception {
        Long ret = new Long(0);
        try {
            //Parameters Types
            @SuppressWarnings("rawtypes")
            Class[] paramTypes= new Class[2];
            paramTypes[0]= long.class;
            paramTypes[1]= int.class;

            @SuppressWarnings("unchecked")
            Method method = m_ClassDefinition.getMethod("getWifiOnTime", paramTypes);

            //Parameters
            Object[] params= new Object[2];
            params[0]= new Long(batteryRealtime);
            params[1]= new Integer(iStatsType);

            ret= (Long) method.invoke(m_Instance, params);

            if (LogSettings.DEBUG) {
                Log.i(TAG, "getWifiOnTime with params " + params[0] + " and " + params[1] +  " returned " + ret);
            }
        }
        catch( Exception e ) {
            ret = new Long(0);
            throw e;
        }
        return ret;
    }


    /**
     * Returns the total, last, or current phone on time in microseconds.
     *
     * @param batteryRealtime the battery realtime in microseconds (@see computeBatteryRealtime).
     * @param iStatsType one of STATS_TOTAL, STATS_LAST, or STATS_CURRENT.
     */
    public Long getPhoneOnTime(long batteryRealtime, int iStatsType) throws Exception {
        Long ret = new Long(0);
        try {
            //Parameters Types
            @SuppressWarnings("rawtypes")
            Class[] paramTypes= new Class[2];
            paramTypes[0]= long.class;
            paramTypes[1]= int.class;

            @SuppressWarnings("unchecked")
            Method method = m_ClassDefinition.getMethod("getPhoneOnTime", paramTypes);

            //Parameters
            Object[] params= new Object[2];
            params[0]= new Long(batteryRealtime);
            params[1]= new Integer(iStatsType);

            ret= (Long) method.invoke(m_Instance, params);
        }
        catch( Exception e ) {
            ret = new Long(0);
            throw e;
        }
        return ret;
    }




}

