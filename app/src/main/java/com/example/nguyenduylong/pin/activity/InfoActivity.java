package com.example.nguyenduylong.pin.activity;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.nguyenduylong.pin.R;
import com.example.nguyenduylong.pin.model.BatteryInfo;
import com.example.nguyenduylong.pin.progress.WaterWaveProgress;
import com.example.nguyenduylong.pin.service.BatteryInfoService;
import com.example.nguyenduylong.pin.util.Str;
import com.vstechlab.easyfonts.EasyFonts;

import me.itangqi.waveloadingview.WaveLoadingView;

/**
 * Created by nguyen duy long on 4/1/2016.
 */
public class InfoActivity extends AppCompatActivity {
    private long startMillis;
    public Context context;
    public Resources res;
    public Str str;
    public SharedPreferences settings;
    public SharedPreferences sp_store;
    private Intent biServiceIntent;
    private Messenger serviceMessenger;
    private final Messenger messenger = new Messenger(new MessageHandler());
    private BatteryInfoService.RemoteConnection serviceConnection;
    private boolean serviceConnected;
    private BatteryInfo info = new BatteryInfo();
    private static final Intent batteryUseIntent = new Intent(Intent.ACTION_POWER_USAGE_SUMMARY)
            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    private static final IntentFilter batteryChangedFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
    private static final String LOG_TAG = "BatteryBot";

    private Toolbar toolBar;

    private TextView timeRemainingtxt ,remainingTypeTxt;
    private TextView sleepTime , callTime , webTime , videoTime , musicTime , gameTime, gpsTime ,readingTime,cameraTime,recordTime;

    private WaveLoadingView batteryPercent;
    public void bindService() {
        if (! serviceConnected) {
            context.bindService(biServiceIntent, serviceConnection, 0);
            serviceConnected = true;
        }
    }

    public class MessageHandler extends Handler {
        @Override
        public void handleMessage(Message incoming) {
            if (! serviceConnected) {
                Log.i(LOG_TAG, "serviceConected is false; ignoring message: " + incoming);
                return;
            }

            switch (incoming.what) {
                case BatteryInfoService.RemoteConnection.CLIENT_SERVICE_CONNECTED:
                    serviceMessenger = incoming.replyTo;
                    sendServiceMessage(BatteryInfoService.RemoteConnection.SERVICE_REGISTER_CLIENT);
                    break;
                case BatteryInfoService.RemoteConnection.CLIENT_BATTERY_INFO_UPDATED:
                    info.loadBundle(incoming.getData());
                    handleUpdatedBatteryInfo(info);
                    break;
                default:
                    super.handleMessage(incoming);
            }
        }
    }

    private void sendServiceMessage(int what) {
        Message outgoing = Message.obtain();
        outgoing.what = what;
        outgoing.replyTo = messenger;
        try { if (serviceMessenger != null) serviceMessenger.send(outgoing); } catch (android.os.RemoteException e) {}
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        toolBar = (Toolbar) findViewById(R.id.charge_tool_bar);
        InfoActivity.this.setSupportActionBar(toolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //info
        remainingTypeTxt = (TextView)findViewById(R.id.text_remaining_type);
        timeRemainingtxt = (TextView)findViewById(R.id.text_remaining_time);
        sleepTime = (TextView)findViewById(R.id.sleep_time);
        callTime = (TextView)findViewById(R.id.call_time);
        webTime = (TextView)findViewById(R.id.web_time);
        videoTime = (TextView)findViewById(R.id.video_time);
        musicTime = (TextView)findViewById(R.id.music_time);
        gameTime = (TextView)findViewById(R.id.game_time);
        cameraTime = (TextView)findViewById(R.id.camera_time);
        recordTime = (TextView)findViewById(R.id.record_time);
        gpsTime = (TextView)findViewById(R.id.gps_time);
        readingTime = (TextView)findViewById(R.id.reading_time);
        batteryPercent = (WaveLoadingView)findViewById(R.id.battery_percent);
        startMillis = System.currentTimeMillis();
        context = getApplicationContext();
        res = getResources();
        str = new Str(res);

        settings = context.getSharedPreferences(SettingsActivity.SETTINGS_FILE, Context.MODE_MULTI_PROCESS);
        sp_store = context.getSharedPreferences(SettingsActivity.SP_STORE_FILE, Context.MODE_MULTI_PROCESS);

        serviceConnection = new BatteryInfoService.RemoteConnection(messenger);
        biServiceIntent = new Intent(context, BatteryInfoService.class);
        context.startService(biServiceIntent);
        bindService();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (serviceConnected) {
            context.unbindService(serviceConnection);
            serviceConnected = false;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (serviceMessenger != null)
            sendServiceMessage(BatteryInfoService.RemoteConnection.SERVICE_REGISTER_CLIENT);

        Intent bc_intent = context.registerReceiver(null, batteryChangedFilter);
        info.load(bc_intent);
        info.load(sp_store);
        handleUpdatedBatteryInfo(info);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause() {
        super.onPause();

        if (serviceMessenger != null)
            sendServiceMessage(BatteryInfoService.RemoteConnection.SERVICE_UNREGISTER_CLIENT);
    }


    private void handleUpdatedBatteryInfo(BatteryInfo info) {
        if (info.plugged == BatteryInfo.PLUGGED_UNPLUGGED){
            remainingTypeTxt.setText(res.getString(R.string.time_remaining));
            sleepTime.setText(str.predictorTimeRemainingState(info, Str.SLEEP_STATE));
            callTime.setText(str.predictorTimeRemainingState(info, Str.CALL_STATE));
            webTime.setText(str.predictorTimeRemainingState(info, Str.WEB_STATE));
            videoTime.setText(str.predictorTimeRemainingState(info, Str.MOVIE_STATE));
            musicTime.setText(str.predictorTimeRemainingState(info, Str.MUSIC_STATE));
            gameTime.setText(str.predictorTimeRemainingState(info, Str.GAME_STATE));
            cameraTime.setText(str.predictorTimeRemainingState(info, Str.CAMERA_STATE));
            recordTime.setText(str.predictorTimeRemainingState(info, Str.VIDEO_RECORD_STATE));
            gpsTime.setText(str.predictorTimeRemainingState(info, Str.GPS_STATE));
            readingTime.setText(str.predictorTimeRemainingState(info, Str.READING_STATE));
        }else {
            remainingTypeTxt.setText(res.getString(R.string.charge_time_remaining));
        }
        batteryPercent.setProgressValue(info.percent);
        batteryPercent.setCenterTitle(info.percent+"%");
        timeRemainingtxt.setText(str.predictorTimeRemaining(info,Str.DEFAULT_STATE));
    }

}
