package com.example.nguyenduylong.pin.activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.nguyenduylong.pin.R;
import com.example.nguyenduylong.pin.model.BatteryInfo;
import com.example.nguyenduylong.pin.service.BatteryInfoService;
import com.example.nguyenduylong.pin.util.SettingUtils;
import com.example.nguyenduylong.pin.util.Str;
import com.gc.materialdesign.views.ButtonFlat;
import com.rey.material.widget.RadioButton;
import com.rey.material.widget.Switch;
import com.vstechlab.easyfonts.EasyFonts;

import java.util.Locale;

/**
 * Created by nguyen duy long on 3/1/2016.
 */
public class SettingsActivity extends AppCompatActivity {
    public static final String SETTINGS_FILE = "com.gpaddy.GpaddyBattery";
    public static final String SP_STORE_FILE = "sp_store";
    public static final String KEY_AUTOSTART = "autostart";
    public static final String KEY_USE_SYSTEM_NOTIFICATION_LAYOUT = "use_system_notification_layout";
    public static final String NOTIFI_STATE = "notifi_state";
    public static final String CHARGE_STATE = "charge_state";
    public static final String SOUND_STATE = "sound_state";
    public static final String  TEMP_TYPE = "temp_type";
    public static final String LANG_TYPE = "lang_type";

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

    Toolbar toolBar;
    Switch notifiSwitch , chargeSwitch, soundSwitch;
    LinearLayout tempButt,langButt;
    TextView tempType , langType;
    int currentLang =0, currentTemp=0;
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
        setContentView(R.layout.activity_setting);
        toolBar = (Toolbar) findViewById(R.id.charge_tool_bar);
        SettingsActivity.this.setSupportActionBar(toolBar);
        toolBar.setNavigationIcon(R.drawable.sliding_menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        notifiSwitch = (Switch)findViewById(R.id.notifi_switch);
        chargeSwitch  = (Switch)findViewById(R.id.charge_switch);
        soundSwitch = (Switch)findViewById(R.id.charge_sound_switch);
        tempButt = (LinearLayout)findViewById(R.id.temp_butt);
        langButt = (LinearLayout)findViewById(R.id.lang_butt);
        tempType = (TextView)findViewById(R.id.temp_type);
        langType = (TextView)findViewById(R.id.lang_type);
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

        SharedPreferences pre=getSharedPreferences (SETTINGS_FILE,MODE_PRIVATE);
        notifiSwitch.setChecked(pre.getBoolean(NOTIFI_STATE,true));
        chargeSwitch.setChecked(pre.getBoolean(CHARGE_STATE,true));
        soundSwitch.setChecked(pre.getBoolean(SOUND_STATE,true));
        currentTemp = pre.getInt(TEMP_TYPE,0);
        currentLang = pre.getInt(LANG_TYPE,0);
        if (pre.getInt(TEMP_TYPE,0)==0){
            tempType.setText(getString(R.string.c_temperature));
        }else {
            tempType.setText(getString(R.string.f_temperature));
        }
        if (pre.getInt(LANG_TYPE,0)==0){
            langType.setText(getString(R.string.vn_lang));
        }else {
            langType.setText(getString(R.string.en_lang));
        }
        notifiSwitch.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(Switch view, boolean checked) {
                updateNotification(checked);
            }
        });
        tempButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences pre = getSharedPreferences(SETTINGS_FILE, MODE_PRIVATE);
                final SharedPreferences.Editor edit = pre.edit();
                final Dialog dialog = new Dialog(SettingsActivity.this, R.style.FullHeightDialog);
                dialog.setContentView(R.layout.dialog_select_temp);
                dialog.setCancelable(true);
                RadioButton butt1, butt2;
                butt1 = (RadioButton) dialog.findViewById(R.id.butt1);
                butt2 = (RadioButton) dialog.findViewById(R.id.butt2);
                ButtonFlat cancleButt = (ButtonFlat) dialog.findViewById(R.id.cancle_butt);
                if (currentTemp == 0) {
                    butt1.setChecked(true);
                    butt2.setChecked(false);
                } else if (currentTemp == 1) {
                    butt1.setChecked(false);
                    butt2.setChecked(true);
                }
                butt1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        currentTemp = 0;
                        edit.putInt(TEMP_TYPE, currentTemp);
                        edit.commit();
                        tempType.setText(getString(R.string.c_temperature));
                        dialog.dismiss();
                    }
                });
                butt2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        currentTemp = 1;
                        edit.putInt(TEMP_TYPE, currentTemp);
                        edit.commit();
                        tempType.setText(getString(R.string.f_temperature));
                        dialog.dismiss();
                    }
                });
                cancleButt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });
        langButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences pre = getSharedPreferences(SETTINGS_FILE,MODE_PRIVATE);
                final SharedPreferences.Editor edit=pre.edit();
                final Dialog dialog = new Dialog(SettingsActivity.this, R.style.FullHeightDialog);
                dialog.setContentView(R.layout.dialog_select_language);
                dialog.setCancelable(true);
                RadioButton  butt1,butt2;
                butt1 = (RadioButton)dialog.findViewById(R.id.butt1);
                butt2 = (RadioButton)dialog.findViewById(R.id.butt2);
                ButtonFlat cancleButt = (ButtonFlat)dialog.findViewById(R.id.cancle_butt);
                if (currentLang ==0){
                    butt1.setChecked(true); butt2.setChecked(false);
                }else if (currentLang==1){
                    butt1.setChecked(false);
                    butt2.setChecked(true);
                }
                butt1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        currentLang = 0;
                        edit.putInt(LANG_TYPE, currentLang);
                        edit.commit();
                        langType.setText(getString(R.string.vn_lang));
                        setLanguage(SettingsActivity.this,"vi");
                        Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        finish();
                        startActivity(intent);
                        dialog.dismiss();
                    }
                });
                butt2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        currentLang =1;
                        edit.putInt(LANG_TYPE, currentLang);
                        edit.commit();
                        langType.setText(getString(R.string.en_lang));
                        setLanguage(SettingsActivity.this,"en");
                        Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        finish();
                        startActivity(intent);
                        dialog.dismiss();
                    }
                });
                cancleButt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();

            }
        });
        notifiSwitch.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(Switch view, boolean checked) {
                if (checked==false){
                    NotificationManager notifManager= (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    notifManager.cancelAll();
                    updateNotification(false);
                }else {
                    updateNotification(true);
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            SharedPreferences pre = getSharedPreferences(SETTINGS_FILE,MODE_PRIVATE);
            SharedPreferences.Editor edit=pre.edit();
            edit.putBoolean(NOTIFI_STATE,notifiSwitch.isChecked());
            edit.putBoolean(SOUND_STATE,soundSwitch.isChecked());
            edit.putBoolean(CHARGE_STATE, soundSwitch.isChecked());
            edit.putInt(TEMP_TYPE, currentTemp);
            edit.putInt(LANG_TYPE, currentLang);
            updateNotification(pre.getBoolean(NOTIFI_STATE,true));
            edit.commit();
            this.finish();
        }
        return super.onOptionsItemSelected(item);
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
    public void onPause() {
        super.onPause();

        if (serviceMessenger != null)
            sendServiceMessage(BatteryInfoService.RemoteConnection.SERVICE_UNREGISTER_CLIENT);
    }



    public  void setLanguage (Activity activity, String language) {
        Resources res = activity.getApplicationContext().getResources();
        DisplayMetrics displayMetrics = res.getDisplayMetrics();
        Configuration configuration = res.getConfiguration();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            configuration.setLocale(new Locale(language));
        } else {
            configuration.locale = new Locale(language);
        }
        res.updateConfiguration(configuration, displayMetrics);
    }

    public void updateNotification(boolean selected){
        Message outgoing = Message.obtain();
        outgoing.what = BatteryInfoService.RemoteConnection.SERVICE_CANCEL_NOTIFICATION_AND_RELOAD_SETTINGS;
        try {
            if (serviceMessenger != null)
                serviceMessenger.send(outgoing);
        } catch (android.os.RemoteException e)
        {

        }
    }

    private void handleUpdatedBatteryInfo(BatteryInfo info) {

    }

}
