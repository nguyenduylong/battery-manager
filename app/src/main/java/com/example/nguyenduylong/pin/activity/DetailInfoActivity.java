package com.example.nguyenduylong.pin.activity;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nguyenduylong.pin.R;
import com.example.nguyenduylong.pin.model.BatteryInfo;
import com.example.nguyenduylong.pin.progress.WaterWaveProgress;
import com.example.nguyenduylong.pin.service.BatteryInfoService;
import com.example.nguyenduylong.pin.util.Str;

/**
 * Created by nguyen duy long on 4/22/2016.
 */
public class DetailInfoActivity extends AppCompatActivity {
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
        setContentView(R.layout.activity_detail_info);
        toolBar = (Toolbar) findViewById(R.id.charge_tool_bar);
        DetailInfoActivity.this.setSupportActionBar(toolBar);
        toolBar.setNavigationIcon(R.drawable.sliding_menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
       TextView healthTxt,tempTxt,capacityTxt, currentPowerTxt,voltageTxt,technologyTxt;
        healthTxt = (TextView)findViewById(R.id.info_health);
        tempTxt = (TextView)findViewById(R.id.info_temp);
        capacityTxt = (TextView)findViewById(R.id.info_capacity);
        currentPowerTxt = (TextView)findViewById(R.id.info_current_power);
        voltageTxt = (TextView)findViewById(R.id.info_voltage);
        technologyTxt = (TextView)findViewById(R.id.info_technology);
        if (info.health == BatteryManager.BATTERY_HEALTH_GOOD){
                healthTxt.setText(getString(R.string.health_good));
        }else if (info.health == BatteryManager.BATTERY_HEALTH_COLD){
            healthTxt.setText(getString(R.string.health_cold));
        }else if (info.health == BatteryManager.BATTERY_HEALTH_DEAD){
            healthTxt.setText(getString(R.string.health_dead));
        }else if (info.health == BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE){
            healthTxt.setText(getString(R.string.health_overvoltage));
        }else if (info.health == BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE){
            healthTxt.setText(getString(R.string.health_failure));
        }
        tempTxt.setText(str.formatTemp(info.temperature,false));
        capacityTxt.setText((int)getBatteryCapacity()+" mAh");
        currentPowerTxt.setText((int)(info.percent*getBatteryCapacity()/100)+" mAh");
        voltageTxt.setText(str.formatVoltage(info.voltage));
        technologyTxt.setText(info.technology);

    }
    public double getBatteryCapacity() {
        Object mPowerProfile_ = null;

        final String POWER_PROFILE_CLASS = "com.android.internal.os.PowerProfile";

        try {
            mPowerProfile_ = Class.forName(POWER_PROFILE_CLASS)
                    .getConstructor(Context.class).newInstance(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        double batteryCapacity=0;
        try {
            batteryCapacity = (Double) Class
                    .forName(POWER_PROFILE_CLASS)
                    .getMethod("getAveragePower", java.lang.String.class)
                    .invoke(mPowerProfile_, "battery.capacity");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return batteryCapacity;
    }


}
