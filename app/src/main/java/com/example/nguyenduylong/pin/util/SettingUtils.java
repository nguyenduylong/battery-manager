package com.example.nguyenduylong.pin.util;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.widget.Toast;

import com.example.nguyenduylong.pin.R;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DecimalFormat;

/**
 * Created by nguyen duy long on 2/19/2016.
 */
public class SettingUtils {

    private static int  getBrightnessState(Context context){

        return Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE,0);

    }

    public  static String getCurrentBrightness(Context context){
        String status = null;
        int state = getBrightnessState(context);
        if (state == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC){
            status = "auto";
        }else{
            try {
                int curBrightnessValue= Settings.System.getInt(
                        context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
                if (curBrightnessValue ==10){
                    status = 0.04+"";
                }else  if (curBrightnessValue == 102){
                    status = 0.4 +"";
                }else if (curBrightnessValue ==153){
                    status = 0.6+"";
                }else if (curBrightnessValue==255){
                    status = 1+"";
                }
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }

        }
        return status;
    }
    public  static void setBrightness(Context context,int brightness){
        android.provider.Settings.System.putInt(context.getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
        android.provider.Settings.System.putInt(context.getContentResolver(),
                android.provider.Settings.System.SCREEN_BRIGHTNESS,brightness);
    }
    public static void setAutoBrightness(Context context){
        android.provider.Settings.System.putInt(context.getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);
    }
    public static void setBrightnessString(Context context,String brightness){
        if (brightness.equals("auto")){
            android.provider.Settings.System.putInt(context.getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);
        }else {
            if (brightness.equals("0.04")){
                android.provider.Settings.System.putInt(context.getContentResolver(),
                        Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
                android.provider.Settings.System.putInt(context.getContentResolver(),
                        android.provider.Settings.System.SCREEN_BRIGHTNESS,10);

            }else if (brightness.equals("0.4")){
                android.provider.Settings.System.putInt(context.getContentResolver(),
                        Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
                android.provider.Settings.System.putInt(context.getContentResolver(),
                        android.provider.Settings.System.SCREEN_BRIGHTNESS, (int) (0.4*255));
            }else if (brightness.equals("0.6")){
                android.provider.Settings.System.putInt(context.getContentResolver(),
                        Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
                android.provider.Settings.System.putInt(context.getContentResolver(),
                        android.provider.Settings.System.SCREEN_BRIGHTNESS, (int) (0.6*255));
            }else if (brightness.equals("1")){
                android.provider.Settings.System.putInt(context.getContentResolver(),
                        Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
                android.provider.Settings.System.putInt(context.getContentResolver(),
                        android.provider.Settings.System.SCREEN_BRIGHTNESS, (int) 255);
            }
        }
    }
    public static void switchBrightness(Context context){
        if (getCurrentBrightness(context).equals("auto")){
            android.provider.Settings.System.putInt(context.getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
            android.provider.Settings.System.putInt(context.getContentResolver(),
                    android.provider.Settings.System.SCREEN_BRIGHTNESS,10);
            Toast.makeText(context,context.getString(R.string.brightness_0),Toast.LENGTH_SHORT).show();
        }else {
            if (getCurrentBrightness(context).equals("0.04")){
                android.provider.Settings.System.putInt(context.getContentResolver(),
                        android.provider.Settings.System.SCREEN_BRIGHTNESS, 102);
                Toast.makeText(context,context.getString(R.string.brightness_1),Toast.LENGTH_SHORT).show();
            }else if (getCurrentBrightness(context).equals("0.4")){
                android.provider.Settings.System.putInt(context.getContentResolver(),
                        android.provider.Settings.System.SCREEN_BRIGHTNESS, 153);
                Toast.makeText(context,context.getString(R.string.brightness_2),Toast.LENGTH_SHORT).show();
            }else if (getCurrentBrightness(context).equals("0.6")){
                android.provider.Settings.System.putInt(context.getContentResolver(),
                        android.provider.Settings.System.SCREEN_BRIGHTNESS, 255);
                Toast.makeText(context,context.getString(R.string.brightness_3),Toast.LENGTH_SHORT).show();
            }else if (getCurrentBrightness(context).equals("1")){
                android.provider.Settings.System.putInt(context.getContentResolver(),
                        Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);
                Toast.makeText(context,context.getString(R.string.brightness_auto),Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static int getScreenOffTime(Context context){
        try {
            return Settings.System.getInt(context.getContentResolver(),Settings.System.SCREEN_OFF_TIMEOUT);
        } catch (Settings.SettingNotFoundException e) {
            return -1;
        }
    }


    public static void setScreenOffTime(int milliTime,Context context){
        Settings.System.putInt(context.getContentResolver(),
                Settings.System.SCREEN_OFF_TIMEOUT, milliTime);
    }

    public static void switchScreenOffTime(Context context){
            if (getScreenOffTime(context)==15000){
                setScreenOffTime(30000,context);
                Toast.makeText(context,context.getString(R.string.switch_time_out)+"30s",Toast.LENGTH_SHORT).show();
            } else if (getScreenOffTime(context)==30000){
                setScreenOffTime(60000,context);
                Toast.makeText(context,context.getString(R.string.switch_time_out)+"1m",Toast.LENGTH_SHORT).show();
            } else if (getScreenOffTime(context)==60000){
                setScreenOffTime(120000,context);
                 Toast.makeText(context,context.getString(R.string.switch_time_out)+"2m",Toast.LENGTH_SHORT).show();
            }else if (getScreenOffTime(context)==120000){
                setScreenOffTime(600000,context);
                Toast.makeText(context,context.getString(R.string.switch_time_out)+"10m",Toast.LENGTH_SHORT).show();
            }else if (getScreenOffTime(context)==600000){
                setScreenOffTime(1800000,context);
                Toast.makeText(context,context.getString(R.string.switch_time_out)+"30m",Toast.LENGTH_SHORT).show();
            }else if (getScreenOffTime(context)==1800000){
                setScreenOffTime(15000,context);
                Toast.makeText(context,context.getString(R.string.switch_time_out)+"30m",Toast.LENGTH_SHORT).show();
            }

    }
    public static int getVibrateWhenRinging(Context context){
        return Settings.System.getInt(context.getContentResolver(),Settings.System.VIBRATE_WHEN_RINGING,0);
    }

    public static void setVibrateWhenRinging(Context context ,int state){
            Settings.System.putInt(context.getContentResolver(), Settings.System.VIBRATE_WHEN_RINGING, state);
    }
    public static void switchVibrateWhenRinging(Context context){
        if (getVibrateWhenRinging(context)== 0){
            setVibrateWhenRinging(context,1);
            Toast.makeText(context,context.getString(R.string.on_vibrate),Toast.LENGTH_SHORT).show();
        }else {
            setVibrateWhenRinging(context,0);
            Toast.makeText(context,context.getString(R.string.off_vibrate),Toast.LENGTH_SHORT).show();
        }
    }


    public static boolean getWifiOnOff(Context context){
        WifiManager wifi = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        return wifi.isWifiEnabled();
    }


    public static void setWifiState(Context context , boolean state){
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if(state){
            if(!getWifiOnOff(context)){
                wifiManager.setWifiEnabled(true);
            }
        }
        if (!state){
            if (getWifiOnOff(context)){
                wifiManager.setWifiEnabled(false);
            }
        }
    }

    public static void switchWifiState(Context context){
            if (getWifiOnOff(context)){
                setWifiState(context,false);
                Toast.makeText(context,context.getString(R.string.off_wifi),Toast.LENGTH_SHORT).show();
            }else {
                setWifiState(context,true);
                Toast.makeText(context,context.getString(R.string.on_wifi),Toast.LENGTH_SHORT).show();
            }
    }
    public static Boolean getBluetoothState(Context context){
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return mBluetoothAdapter.isEnabled();
    }

    public static void setBluetoothState(Context context, boolean state){
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (state){
            if (!mBluetoothAdapter.isEnabled()){
                mBluetoothAdapter.enable();
            }
        }
        if (!state){
            if (mBluetoothAdapter.isEnabled()){
                mBluetoothAdapter.disable();
            }
        }
    }
    public static void switchBluetooth(Context context){
        if (getBluetoothState(context)){
            setBluetoothState(context,false);
            Toast.makeText(context,context.getString(R.string.off_bluetooth),Toast.LENGTH_SHORT).show();
        }else {
            setBluetoothState(context,true);
            Toast.makeText(context,context.getString(R.string.on_bluetooth),Toast.LENGTH_SHORT).show();
        }
    }
    public static boolean getDataMobileState(Context context){
        boolean mobileDataEnabled = false; // Assume disabled
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
            Class cmClass = Class.forName(cm.getClass().getName());
            Method method = cmClass.getDeclaredMethod("getMobileDataEnabled");
            method.setAccessible(true);
            mobileDataEnabled = (Boolean)method.invoke(cm);
        } catch (Exception e) {
            // Some problem accessible private API
            // TODO do whatever error handling you want here
        }
        return mobileDataEnabled;
    }

    public static void setMobileDataEnabled(Context context, boolean enabled) {
        final ConnectivityManager conman = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final Class conmanClass;
        try {
            conmanClass = Class.forName(conman.getClass().getName());
            final Field iConnectivityManagerField = conmanClass.getDeclaredField("mService");
            iConnectivityManagerField.setAccessible(true);
            final Object iConnectivityManager = iConnectivityManagerField.get(conman);
            final Class iConnectivityManagerClass = Class.forName(iConnectivityManager.getClass().getName());
            final Method setMobileDataEnabledMethod = iConnectivityManagerClass.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
            setMobileDataEnabledMethod.setAccessible(true);

            setMobileDataEnabledMethod.invoke(iConnectivityManager, enabled);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

    }

    public static void switchDataConnection(Context context){
        if (getDataMobileState(context)){
            setMobileDataEnabled(context,false);
            Toast.makeText(context,context.getString(R.string.off_dataconnect),Toast.LENGTH_SHORT).show();
        }else {
            setMobileDataEnabled(context,true);
            Toast.makeText(context,context.getString(R.string.on_dataconnect),Toast.LENGTH_SHORT).show();
        }
    }

    public static void setSoundProfile(Context context, int type){
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        audioManager.setRingerMode(type);
    }
     public static int getSoundProfileState(Context context){
         AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
         return audioManager.getRingerMode();
     }
    public static void switchSoundProfile(Context context){
        if (getSoundProfileState(context)==AudioManager.RINGER_MODE_NORMAL){
            setSoundProfile(context,AudioManager.RINGER_MODE_VIBRATE);
            Toast.makeText(context,context.getString(R.string.switch_vibrate),Toast.LENGTH_SHORT).show();
        }else  if (getSoundProfileState(context)==AudioManager.RINGER_MODE_SILENT) {
            setSoundProfile(context, AudioManager.RINGER_MODE_NORMAL);
            Toast.makeText(context,context.getString(R.string.switch_normal),Toast.LENGTH_SHORT).show();
        }else  if (getSoundProfileState(context)==AudioManager.RINGER_MODE_VIBRATE) {
            setSoundProfile(context, AudioManager.RINGER_MODE_SILENT);
            Toast.makeText(context,context.getString(R.string.switch_silent),Toast.LENGTH_SHORT).show();
        }
    }

    public static void setAutoOrientationEnabled(Context context, boolean enabled)
    {
        Settings.System.putInt( context.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, enabled ? 1 : 0);
    }

    public static boolean getAutoOrientationState(Context context){
        if (android.provider.Settings.System.getInt(context.getContentResolver(),
                Settings.System.ACCELEROMETER_ROTATION, 0) == 1){
                return  true;
        }
        else{
            return false;
        }
    }

    public static void switchAutoOrientation(Context context){
        if (getAutoOrientationState(context)){
            setAutoOrientationEnabled(context,false);
            Toast.makeText(context,context.getString(R.string.off_autorotate),Toast.LENGTH_SHORT).show();
        }else {
            setAutoOrientationEnabled(context,true);
            Toast.makeText(context,context.getString(R.string.on_autorotate),Toast.LENGTH_SHORT).show();
        }
    }

    public static void setHapticFeedbackMode(Context context,int bOn)
    {
        Settings.System.putInt(context.getContentResolver(),
                Settings.System.HAPTIC_FEEDBACK_ENABLED, bOn);
    }
    public static int getHapticFeedbackMode(Context context){
        int state = -1;
        try {
           state =  Settings.System.getInt(context.getContentResolver(),Settings.System.HAPTIC_FEEDBACK_ENABLED);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return state;
    }
    public static void switchHapticFeedbackMode(Context context){
        if (getHapticFeedbackMode(context)==1){
            setHapticFeedbackMode(context,0);
            Toast.makeText(context,context.getString(R.string.off_haptic),Toast.LENGTH_SHORT).show();
        }else {
            setHapticFeedbackMode(context,1);
            Toast.makeText(context,context.getString(R.string.on_haptic),Toast.LENGTH_SHORT).show();
        }
    }
    public static boolean getSyncState(Context context){
        return context.getContentResolver().getMasterSyncAutomatically();
    }

    public static void setSyncState(Context context , boolean on){
        context.getContentResolver().setMasterSyncAutomatically(on);
    }

    public static void switchSyncState(Context context){
        if (getSyncState(context)){
            setSyncState(context,false);
            Toast.makeText(context,context.getString(R.string.off_sync),Toast.LENGTH_SHORT).show();
        }else {
            setSyncState(context,true);
            Toast.makeText(context,context.getString(R.string.on_sync),Toast.LENGTH_SHORT).show();
        }
    }

    public static boolean getGPSState(Context context){
        LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE );
        boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return statusOfGPS;
    }

    public static void turnGPSOn(Context context){
        String provider = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

        if(!provider.contains("gps")){ //if gps is disabled
            final Intent poke = new Intent();
            poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
            poke.setData(Uri.parse("3"));
            context.sendBroadcast(poke);
        }
    }

    public static void turnGPSOff(Context context){
        String provider = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

        if(provider.contains("gps")){ //if gps is enabled
            final Intent poke = new Intent();
            poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
            poke.setData(Uri.parse("3"));
            context.sendBroadcast(poke);
        }
    }

    public static void switchGPSState(Context context){
        if (getGPSState(context)){
            turnGPSOff(context);
            Toast.makeText(context,context.getString(R.string.off_gps),Toast.LENGTH_SHORT).show();
        }else {
            turnGPSOn(context);
            Toast.makeText(context,context.getString(R.string.on_gps),Toast.LENGTH_SHORT).show();
        }
    }
}
