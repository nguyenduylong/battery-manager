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
import com.vstechlab.easyfonts.EasyFonts;

import me.itangqi.waveloadingview.WaveLoadingView;

/**
 * Created by nguyen duy long on 3/19/2016.
 */
public class ChargeActivity extends AppCompatActivity {
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


    //widget
    private TextView remainingTimeTxt,remainingTxt;
    private TextView chargeTypeTxt;
    private ImageView chargeImage;
    private WaveLoadingView percentProgress;
    private ImageView speedState , continuousState , trickleState;
    private TextView speed_continuous ,continuous_trickle;
    //tool bar
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
        setContentView(R.layout.activity_charge);
        remainingTimeTxt = (TextView)findViewById(R.id.text_time_remaining);
        remainingTxt  = (TextView)findViewById(R.id.text_remaining);
        percentProgress  = (WaveLoadingView)findViewById(R.id.percent_progress);
        chargeTypeTxt = (TextView)findViewById(R.id.text_charge_type);
        chargeImage = (ImageView)findViewById(R.id.charge_image);
        speedState = (ImageView)findViewById(R.id.img_speed_state);
        continuousState = (ImageView)findViewById(R.id.img_continuous_state);
        trickleState = (ImageView)findViewById(R.id.img_trickle_state);
        speed_continuous = (TextView)findViewById(R.id.view_speed_continuous);
        continuous_trickle = (TextView)findViewById(R.id.view_continuous_trickle);
        toolBar = (Toolbar) findViewById(R.id.charge_tool_bar);
        ChargeActivity.this.setSupportActionBar(toolBar);
        toolBar.setNavigationIcon(R.drawable.sliding_menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final Animation chargingAnimation = AnimationUtils.loadAnimation(ChargeActivity.this, R.anim.anim_charging);
        chargeImage.startAnimation(chargingAnimation);
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
            percentProgress.setProgressValue(info.percent);
            percentProgress.setCenterTitle(info.percent + "%");
            remainingTxt.setTypeface(EasyFonts.robotoMedium(this));
            remainingTimeTxt.setText(str.predictorTimeRemaining(info,Str.DEFAULT_STATE));
            if (info.plugged == BatteryInfo.PLUGGED_UNPLUGGED){
                remainingTxt.setText(res.getString(R.string.time_remaining));
                chargeTypeTxt.setVisibility(View.GONE);
                chargeImage.setVisibility(View.GONE);
                trickleState.setImageResource(R.drawable.charge_trickle);
                continuousState.setImageResource(R.drawable.charge_continuous);
                speedState.setImageResource(R.drawable.charge_speed);
                speed_continuous.setBackgroundResource(R.color.white);
                continuous_trickle.setBackgroundResource(R.color.white);
            }else{
                chargeImage.setVisibility(View.VISIBLE);
                chargeTypeTxt.setVisibility(View.VISIBLE);
                remainingTxt.setText(res.getString(R.string.charge_time_remaining));
                if (info.plugged == BatteryInfo.PLUGGED_AC){
                    chargeTypeTxt.setText(res.getString(R.string.ac_type));
                }else if (info.plugged==BatteryInfo.PLUGGED_USB){
                    chargeTypeTxt.setText(res.getString(R.string.usb_type));
                }else if (info.plugged==BatteryInfo.PLUGGED_WIRELESS){
                    chargeTypeTxt.setText(res.getString(R.string.wireless_type));
                }
                if (info.percent <=40){
                    trickleState.setImageResource(R.drawable.charge_trickle);
                    continuousState.setImageResource(R.drawable.charge_continuous);
                    speedState.setImageResource(R.drawable.charge_speed_on);
                    speed_continuous.setBackgroundResource(R.color.white);
                    continuous_trickle.setBackgroundResource(R.color.white);
                }else if ((info.percent>40)&&(info.percent<100)){
                    trickleState.setImageResource(R.drawable.charge_trickle);
                    continuousState.setImageResource(R.drawable.charge_continuous_on);
                    speedState.setImageResource(R.drawable.charge_speed_on);
                    speed_continuous.setBackgroundResource(R.color.battery_color);
                    continuous_trickle.setBackgroundResource(R.color.white);
                }else if (info.percent==100){
                    trickleState.setImageResource(R.drawable.charge_trickle_on);
                    continuousState.setImageResource(R.drawable.charge_continuous_on);
                    speedState.setImageResource(R.drawable.charge_speed_on);
                    speed_continuous.setBackgroundResource(R.color.battery_color);
                    continuous_trickle.setBackgroundResource(R.color.battery_color);
                }
            }

    }

}
