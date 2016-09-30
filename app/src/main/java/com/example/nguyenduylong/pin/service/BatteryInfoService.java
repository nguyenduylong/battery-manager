/*
    Copyright (c) 2009-2013 Darshan-Josiah Barber

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.
*/

package com.example.nguyenduylong.pin.service;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.example.nguyenduylong.pin.activity.ChargeActivity;
import com.example.nguyenduylong.pin.activity.MainActivity;
import com.example.nguyenduylong.pin.R;
import com.example.nguyenduylong.pin.activity.OptimizeActivity;
import com.example.nguyenduylong.pin.model.BatteryInfo;
import com.example.nguyenduylong.pin.model.Predictor;
import com.example.nguyenduylong.pin.activity.SettingsActivity;
import com.example.nguyenduylong.pin.util.SettingUtils;
import com.example.nguyenduylong.pin.util.Str;

import java.util.Date;

public class BatteryInfoService extends Service {
    private final IntentFilter batteryChanged = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
    private final IntentFilter userPresent    = new IntentFilter(Intent.ACTION_USER_PRESENT);
    private PendingIntent mainWindowPendingIntent;
    private PendingIntent updatePredictorPendingIntent;

    private NotificationManager mNotificationManager;
    private AlarmManager alarmManager;
    private static SharedPreferences settings;
    private static SharedPreferences sp_store;
    private static SharedPreferences states;
    private static SharedPreferences.Editor sps_editor;

    private Context context;
    private Resources res;
    private Str str;
    private BatteryInfo info;
    private long now;
    private boolean updated_lasts;
    private static java.util.HashSet<Messenger> clientMessengers;
    private static Messenger messenger;
    private static final String LOG_TAG = "BatteryInfoService";

    private static final int NOTIFICATION_PRIMARY      = 1;
    public static final String KEY_PREVIOUS_CHARGE = "previous_charge";
    public static final String KEY_PREVIOUS_TEMP = "previous_temp";
    public static final String KEY_PREVIOUS_HEALTH = "previous_health";
    public static final String KEY_DISABLE_LOCKING = "disable_lock_screen";
    public static final String KEY_SERVICE_DESIRED = "serviceDesired";
    public static final String KEY_SHOW_NOTIFICATION = "show_notification";
    public static final String LAST_SDK_API = "last_sdk_api";
    private static final String EXTRA_UPDATE_PREDICTOR = "com.darshancomputing.BatteryBot.EXTRA_UPDATE_PREDICTOR";


    public static final String STATE_PREFERENCE = "state_preference";
    public static final String WIFISTATE = "wifi";
    public static final String BRIGHTNESSSTATE = "brightness";
    public static final String RINGERMODESTATE ="ringermode";
    public static final String SCREENOFFTIME = "screenoff";
    public static final String SYNCSTATE = "syncstate";
    public static final String HAPTICSTATE = "haptic";
    public static final String BLUETOOTHSTATE = "bluetooth";
    public static final String VIBRATERINGINGSTATE = "vibrateringing";
     private boolean speedState  , continuousState, trickleState, chargeFinish;
    private Notification mainNotification;

    private Predictor predictor;

    private final Handler mHandler = new Handler();

    private final Runnable mNotify = new Runnable() {
        public void run() {
            startForeground(NOTIFICATION_PRIMARY, mainNotification);
            mHandler.removeCallbacks(mNotify);
        }
    };
    @Override
    public void onCreate() {
        res = getResources();
        str = new Str(res);
        context = getApplicationContext();

        info = new BatteryInfo();

        messenger = new Messenger(new MessageHandler());
        clientMessengers = new java.util.HashSet<Messenger>();

        predictor = new Predictor(context);

        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        loadSettingsFiles(context);
        states = getSharedPreferences(STATE_PREFERENCE,MODE_PRIVATE);
        sdkVersioning();

        Intent mainWindowIntent = new Intent(context, MainActivity.class);
        mainWindowPendingIntent = PendingIntent.getActivity(context, 0, mainWindowIntent, 0);

        Intent updatePredictorIntent = new Intent(context, BatteryInfoService.class);
        updatePredictorIntent.putExtra(EXTRA_UPDATE_PREDICTOR, true);
        updatePredictorPendingIntent = PendingIntent.getService(context, 0, updatePredictorIntent, 0);
        Intent bc_intent = registerReceiver(mBatteryInfoReceiver, batteryChanged);
        info.load(bc_intent, sp_store);
    }

    @Override
    public void onDestroy() {
        alarmManager.cancel(updatePredictorPendingIntent);
        unregisterReceiver(mBatteryInfoReceiver);
        mHandler.removeCallbacks(mNotify);
        mNotificationManager.cancelAll();
        stopForeground(true);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO: Do I need a filter, or is it okay to just update(null) every time?
        //if (intent != null && intent.getBooleanExtra(EXTRA_UPDATE_PREDICTOR, false))
        update(null);

        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return messenger.getBinder();
    }

    public class MessageHandler extends Handler {
        @Override
        public void handleMessage(Message incoming) {
            switch (incoming.what) {
            case RemoteConnection.SERVICE_CLIENT_CONNECTED:
                sendClientMessage(incoming.replyTo, RemoteConnection.CLIENT_SERVICE_CONNECTED);
                break;
            case RemoteConnection.SERVICE_REGISTER_CLIENT:
                clientMessengers.add(incoming.replyTo);
                sendClientMessage(incoming.replyTo, RemoteConnection.CLIENT_BATTERY_INFO_UPDATED, info.toBundle());

                break;
            case RemoteConnection.SERVICE_UNREGISTER_CLIENT:
                clientMessengers.remove(incoming.replyTo);
                break;
            case RemoteConnection.SERVICE_RELOAD_SETTINGS:
                reloadSettings(false);
                break;
            case RemoteConnection.SERVICE_CANCEL_NOTIFICATION_AND_RELOAD_SETTINGS:
                reloadSettings(true);
                break;
            default:
                super.handleMessage(incoming);
            }
        }
    }

    private static void sendClientMessage(Messenger clientMessenger, int what) {
        sendClientMessage(clientMessenger, what, null);
    }

    private static void sendClientMessage(Messenger clientMessenger, int what, Bundle data) {
        Message outgoing = Message.obtain();
        outgoing.what = what;
        outgoing.replyTo = messenger;
        outgoing.setData(data);
        try { clientMessenger.send(outgoing); } catch (android.os.RemoteException e) {}
    }

    public static class RemoteConnection implements ServiceConnection {
        // Messages clients send to the service
        public static final int SERVICE_CLIENT_CONNECTED = 0;
        public static final int SERVICE_REGISTER_CLIENT = 1;
        public static final int SERVICE_UNREGISTER_CLIENT = 2;
        public static final int SERVICE_RELOAD_SETTINGS = 3;
        public static final int SERVICE_CANCEL_NOTIFICATION_AND_RELOAD_SETTINGS = 4;

        // Messages the service sends to clients
        public static final int CLIENT_SERVICE_CONNECTED = 0;
        public static final int CLIENT_BATTERY_INFO_UPDATED = 1;

        public Messenger serviceMessenger;
        private Messenger clientMessenger;

        public RemoteConnection(Messenger m) {
            clientMessenger = m;
        }

        public void onServiceConnected(ComponentName name, IBinder iBinder) {
            serviceMessenger = new Messenger(iBinder);

            Message outgoing = Message.obtain();
            outgoing.what = SERVICE_CLIENT_CONNECTED;
            outgoing.replyTo = clientMessenger;
            try { serviceMessenger.send(outgoing); } catch (android.os.RemoteException e) {}
        }

        public void onServiceDisconnected(ComponentName name) {
            serviceMessenger = null;
        }
    }

    private static void loadSettingsFiles(Context context) {
        settings = context.getSharedPreferences(SettingsActivity.SETTINGS_FILE, Context.MODE_MULTI_PROCESS);
        sp_store = context.getSharedPreferences(SettingsActivity.SP_STORE_FILE, Context.MODE_MULTI_PROCESS);
    }

    private void reloadSettings(boolean cancelFirst) {
        loadSettingsFiles(context);

        str = new Str(res); // Language override may have changed

        if (cancelFirst) stopForeground(true);

        registerReceiver(mBatteryInfoReceiver, batteryChanged);
    }

    private final BroadcastReceiver mBatteryInfoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context c, Intent intent) {
            if (! Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())) return;
            update(intent);
        }
    };

    // Does anything needed when SDK API level increases and sets LAST_SDK_API
    private void sdkVersioning(){
        SharedPreferences.Editor sps_editor = sp_store.edit();
        SharedPreferences.Editor settings_editor = settings.edit();

        if (sp_store.getInt(LAST_SDK_API, 0) < 21 && android.os.Build.VERSION.SDK_INT >= 21) {
            settings_editor.putBoolean(SettingsActivity.KEY_USE_SYSTEM_NOTIFICATION_LAYOUT, true);
        }

        sps_editor.putInt(LAST_SDK_API, android.os.Build.VERSION.SDK_INT);

        sps_editor.commit();
        settings_editor.commit();
    }

    private void update(Intent intent) {
        now = System.currentTimeMillis();
        sps_editor = sp_store.edit();
        updated_lasts = false;

        if (intent != null)
            info.load(intent, sp_store);
        if (settings.getBoolean(SettingsActivity.NOTIFI_STATE,true)) {
            CustomNotification(info);
        }
        predictor.update(info);
        setSpeedCharge(info);
        info.prediction.updateRelativeTime();

        if (statusHasChanged())
            handleUpdateWithChangedStatus();
        else
            handleUpdateWithSameStatus();

        syncSpsEditor(); // Important to sync after other Service code that uses 'lasts' but before sending info to client

        for (Messenger messenger : clientMessengers) {
            // TODO: Can I send the same message to multiple clients instead of sending duplicates?
            sendClientMessage(messenger, RemoteConnection.CLIENT_BATTERY_INFO_UPDATED, info.toBundle());
        }

        alarmManager.set(AlarmManager.ELAPSED_REALTIME, android.os.SystemClock.elapsedRealtime() + (2 * 60 * 1000), updatePredictorPendingIntent);
    }



    private void syncSpsEditor() {
        sps_editor.commit();

        if (updated_lasts) {
            info.last_status_cTM = now;
            info.last_status = info.status;
            info.last_percent = info.percent;
            info.last_plugged = info.plugged;
        }
    }
    public void CustomNotification(BatteryInfo info) {
        RemoteViews remoteViews = new RemoteViews(getPackageName(),
                R.layout.customnotification);


        // Open NotificationView Class on Notification Click
        Intent intent = new Intent(this, MainActivity.class);
        // Send data to NotificationView Class
        // Open NotificationView.java Activity
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        Intent cleanintent = new Intent(this, OptimizeActivity.class);
        // Send data to NotificationView Class
        // Open NotificationView.java Activity
        PendingIntent cleanIntent = PendingIntent.getActivity(this, 0, cleanintent,
                0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                // Set Icon
                .setSmallIcon(R.mipmap.ic_launcher)
                        // Set Ticker Message
                .setTicker(getString(R.string.app_full_name))
                        // Dismiss Notification
                .setAutoCancel(false)
                        // Set PendingIntent into Notification
                .setContentIntent(pIntent)
                        // Set RemoteViews into Notification
                .setOngoing(true)
                .setContent(remoteViews);

        // Locate and set the Text into customnotificationtext.xml TextViews
        if (info.plugged ==BatteryInfo.PLUGGED_UNPLUGGED){
            remoteViews.setTextViewText(R.id.title,res.getString(R.string.time_remaining));
        }else {
            remoteViews.setTextViewText(R.id.title,res.getString(R.string.charge_time_remaining));
        }
        if ((info.percent>=0)&&(info.percent<=20)){
            remoteViews.setImageViewResource(R.id.img_notifi_battery,R.drawable.notifi_pin1);
        }else if ((info.percent>20)&&(info.percent<=60)){
            remoteViews.setImageViewResource(R.id.img_notifi_battery,R.drawable.notifi_pin2);
        }else if ((info.percent>60)&&(info.percent<100)){
            remoteViews.setImageViewResource(R.id.img_notifi_battery,R.drawable.notifi_pin3);
        }else if (info.percent==100){
            remoteViews.setImageViewResource(R.id.img_notifi_battery,R.drawable.notifi_pin4);
        }
        remoteViews.setTextViewText(R.id.text,str.predictorTimeRemaining(info,Str.DEFAULT_STATE));
        remoteViews.setTextViewText(R.id.text_temp,str.formatTemp(info.temperature, false));
        remoteViews.setTextViewText(R.id.text_level,info.percent+"%");
        remoteViews.setOnClickPendingIntent(R.id.notifi_clean,cleanIntent);
        // Create Notification Manager
        NotificationManager notificationmanager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // Build Notification with Notification Manager
        notificationmanager.notify(0, builder.build());
    }
    private void doNotify() {
        mHandler.post(mNotify);
    }

    private boolean statusHasChanged() {
        int previous_charge = sp_store.getInt(KEY_PREVIOUS_CHARGE, 100);

        return (info.last_status != info.status ||
                info.last_status_cTM >= now ||
                info.last_plugged != info.plugged ||
                (info.plugged == BatteryInfo.PLUGGED_UNPLUGGED && info.percent > previous_charge + 20));
    }

    private void handleUpdateWithChangedStatus() {
        updated_lasts = true;
        sps_editor.putLong(BatteryInfo.KEY_LAST_STATUS_CTM, now);
        sps_editor.putInt(BatteryInfo.KEY_LAST_STATUS, info.status);
        sps_editor.putInt(BatteryInfo.KEY_LAST_PERCENT, info.percent);
        sps_editor.putInt(BatteryInfo.KEY_LAST_PLUGGED, info.plugged);
        sps_editor.putInt(KEY_PREVIOUS_CHARGE, info.percent);
        sps_editor.putInt(KEY_PREVIOUS_TEMP, info.temperature);
        sps_editor.putInt(KEY_PREVIOUS_HEALTH, info.health);
    }

    private void handleUpdateWithSameStatus() {
        if (info.percent % 10 == 0) {
            sps_editor.putInt(KEY_PREVIOUS_CHARGE, info.percent);
            sps_editor.putInt(KEY_PREVIOUS_TEMP, info.temperature);
            sps_editor.putInt(KEY_PREVIOUS_HEALTH, info.health);
        }
    }
    private String formatTime(Date d) {
        String format = android.provider.Settings.System.getString(getContentResolver(),
                                                                   android.provider.Settings.System.TIME_12_24);
        if (format == null || format.equals("12")) {
            return java.text.DateFormat.getTimeInstance(java.text.DateFormat.SHORT,
                                                        java.util.Locale.getDefault()).format(d);
        } else {
            return (new java.text.SimpleDateFormat("HH:mm")).format(d);
        }
    }

    private void setSpeedCharge(BatteryInfo info){
        SharedPreferences pre = getSharedPreferences(SettingsActivity.SETTINGS_FILE,MODE_PRIVATE);
        if (info.plugged == BatteryInfo.PLUGGED_UNPLUGGED){
            speedState = continuousState = trickleState = false;
        }else if(pre.getBoolean(SettingsActivity.CHARGE_STATE,true)){
            if ((info.percent<=40)&&(speedState ==false)){
                setSpeedState();
                speedState=true;
            }
            if ((info.percent>40)&&(info.percent <100)&&(continuousState==false)){
                speedState = false;
                continuousState=true;
                setContinuousState();
            }
            if ((info.percent==100)&&(!trickleState)){
                speedState = continuousState = false;
                trickleState = true;
                setTrickleState();
            }
        }
    }
    private  void setSpeedState(){
        SharedPreferences.Editor stateEdit = states.edit();
        stateEdit.putString(BRIGHTNESSSTATE,SettingUtils.getCurrentBrightness(context));
        stateEdit.putInt(RINGERMODESTATE, SettingUtils.getSoundProfileState(context));
        stateEdit.putInt(SCREENOFFTIME,SettingUtils.getScreenOffTime(context));
        stateEdit.putBoolean(WIFISTATE, SettingUtils.getWifiOnOff(context));
        stateEdit.putBoolean(BLUETOOTHSTATE,SettingUtils.getBluetoothState(context));
        stateEdit.putBoolean(SYNCSTATE,SettingUtils.getSyncState(context));
        stateEdit.putInt(VIBRATERINGINGSTATE, SettingUtils.getVibrateWhenRinging(context));
        stateEdit.putInt(HAPTICSTATE,SettingUtils.getHapticFeedbackMode(context));
        stateEdit.commit();
        SettingUtils.setHapticFeedbackMode(context, 0);
        SettingUtils.setBluetoothState(context, false);
        SettingUtils.setBrightness(context, 10);
        SettingUtils.setScreenOffTime(15000, context);
        SettingUtils.setSoundProfile(context, AudioManager.RINGER_MODE_SILENT);
        SettingUtils.setSyncState(context, false);
        SettingUtils.setVibrateWhenRinging(context,0);
        SettingUtils.setWifiState(context,false);
    }
    private  void setContinuousState(){
        SettingUtils.setHapticFeedbackMode(context,states.getInt(HAPTICSTATE,0));
        SettingUtils.setBluetoothState(context, states.getBoolean(BLUETOOTHSTATE, false));
        SettingUtils.setBrightnessString(context, states.getString(BRIGHTNESSSTATE, "auto"));
        SettingUtils.setScreenOffTime(states.getInt(SCREENOFFTIME,15000), context);
        SettingUtils.setSoundProfile(context, states.getInt(RINGERMODESTATE,0));
        SettingUtils.setSyncState(context,states.getBoolean(SYNCSTATE,false));
        SettingUtils.setVibrateWhenRinging(context,states.getInt(VIBRATERINGINGSTATE,0));
        SettingUtils.setWifiState(context, states.getBoolean(WIFISTATE, false));
    }
    private void setTrickleState(){
        new CountDownTimer(300000, 1000) {

            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                trickleState = false;
                SharedPreferences pre = getSharedPreferences(SettingsActivity.SETTINGS_FILE,MODE_PRIVATE);
                if (pre.getBoolean(SettingsActivity.SOUND_STATE,true)){
                    MediaPlayer mPlayer = MediaPlayer.create(context, R.raw.full_battery);
                    mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    mPlayer.start();
                }
               Toast.makeText(context,res.getString(R.string.charge_full),Toast.LENGTH_SHORT).show();
            }
        }.start();
    }

}
