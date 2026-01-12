package com.wjthinkbig.undrmapp;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.util.Log;

import java.lang.reflect.Method;

public class DeviceInfo {

    /**
     * DRM 키값 리턴
     * @param mContext
     * @return
     */
    public static String getDeviceIDForDRM(Context mContext) {
        String mac = "";
        if(Build.VERSION.SDK_INT >= 23){
            //Log.e("MBookLibs", "##### ANDROID M 이상");
//			mac = com.wjthinkbig.mbookcommonlibs.utils.Util.getMACAddress();

            try{
                Class systemProperties = mContext.getClassLoader().loadClass("android.os.SystemProperties");

                Class[] types = new Class[1];
                types[0] = String.class;
                Object[] params = new Object[1];

                if( Build.MODEL.equalsIgnoreCase("SM-T536") ){
                    params[0] = new String("ril.serialnumber");
                    Method get = systemProperties.getMethod("get", types);
                    mac = (String)get.invoke(systemProperties, params);
                }else if( Build.MODEL.toLowerCase().startsWith("LG-X760") ){
                    params[0] = new String("ro.dev.msn");
                    Method get = systemProperties.getMethod("get", types);
                    mac = (String)get.invoke(systemProperties, params);

                }else{
                    mac = Build.SERIAL;
                }

            }catch(Exception e){

            }
        }else{
            //Log.e("MBookLibs", "##### ANDROID M 이하");
            WifiManager mng = (WifiManager) mContext.getSystemService(mContext.WIFI_SERVICE);
            WifiInfo info = mng.getConnectionInfo();
            mac = info.getMacAddress();
        }

        Log.d("leesuk", "getDeviceIDForDRM : " + mac);
        return mac;
    }
}
