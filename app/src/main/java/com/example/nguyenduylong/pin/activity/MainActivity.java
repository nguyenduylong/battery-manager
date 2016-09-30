package com.example.nguyenduylong.pin.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.media.AudioManager;
import android.media.MediaActionSound;
import android.media.MediaPlayer;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.provider.Settings;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nguyenduylong.pin.R;
import com.example.nguyenduylong.pin.model.BatteryInfo;
import com.example.nguyenduylong.pin.service.BatteryInfoService;
import com.example.nguyenduylong.pin.util.SettingUtils;
import com.example.nguyenduylong.pin.util.Str;
import com.example.nguyenduylong.pin.slidingmenu.*;
import com.vstechlab.easyfonts.EasyFonts;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {



    private long startMillis;
    public Context context;
    public Resources res;
    public Str str;
    public SharedPreferences settings;
    public SharedPreferences sp_store;
    public SharedPreferences setting;
    private Intent biServiceIntent;
    private Messenger serviceMessenger;
    private final Messenger messenger = new Messenger(new MessageHandler());
    private BatteryInfoService.RemoteConnection serviceConnection;
    private boolean serviceConnected;
    private static final Intent batteryUseIntent = new Intent(Intent.ACTION_POWER_USAGE_SUMMARY)
            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    private static final IntentFilter batteryChangedFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
    private Button airPlane;
    private BatteryInfo info = new BatteryInfo();
    public static boolean firstSaveMode = true;


    private TextView tempTxt, volTxt , capacityTxt;

    // layout remaining
    private TextView percentTxt,remainingTimeTxt;
    private ImageView percentImage,chargingImage;
    private TextView remainingTypeTxt;
    //tool layout
    private LinearLayout wifiButt , dataButt, lightnessButt ,
            stateButt , vibrateButt , gpsButt , bluetoothButt ,
            airplaneButt , screenoffButt , rotateButt , syncButt;
    private ImageView wifiState , dataState , lightnessState , ringerState , vibrateState , gpsState , bluetoothState ,
            airplaneState , screenoffState , rotateState , syncState;
    //function
    private LinearLayout functionCharge, functionInfo , functionSaverMode , functionRank;
    private TextView optimizeButt;
    private static final String LOG_TAG = "BatteryBot";
    private ImageView menuImage;

    //sliding menu
    private Toolbar tool_bar;
    private static String TAG = MainActivity.class.getSimpleName();
    ListView mDrawerList;
    RelativeLayout mDrawerPane;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    ArrayList<NavItem> mNavItems = new ArrayList<NavItem>();
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
        setContentView(R.layout.activity_main);
        mNavItems.add(new NavItem(getString(R.string.setting),  R.drawable.menu_setting));
        mNavItems.add(new NavItem(getString(R.string.info),  R.drawable.menu_detail));
        mNavItems.add(new NavItem(getString(R.string.about_us), R.drawable.menu_about));

        // DrawerLayout
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        tool_bar = (Toolbar) findViewById(R.id.tool_bar);
        MainActivity.this.setSupportActionBar(tool_bar);
        tool_bar.setNavigationIcon(R.drawable.sliding_menu);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        tool_bar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.openDrawer(mDrawerPane);
            }
        });
        // Populate the Navigtion Drawer with options
        mDrawerPane = (RelativeLayout) findViewById(R.id.drawerPane);
        mDrawerList = (ListView) findViewById(R.id.navList);
        DrawerListAdapter adapter = new DrawerListAdapter(this, mNavItems);
        mDrawerList.setAdapter(adapter);

        // Drawer Item click listeners
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position==0){
                    Intent settingIntent = new Intent(MainActivity.this,SettingsActivity.class);
                    startActivity(settingIntent);
                }else if (position==1){
                        Intent detailInfo = new Intent(MainActivity.this,DetailInfoActivity.class);
                        startActivity(detailInfo);
                }else if (position==2){
                        Intent aboutIntent = new Intent(MainActivity.this,ActivityAboutMe.class);
                        startActivity(aboutIntent);
                }
                mDrawerLayout.closeDrawer(mDrawerPane);
            }
        });
        this.registerReceiver(this.WifiStateChangedReceiver,
                new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION));
        //function layout
        functionCharge = (LinearLayout)findViewById(R.id.function_charge);
        functionInfo = (LinearLayout)findViewById(R.id.function_info);
        functionSaverMode = (LinearLayout)findViewById(R.id.function_save_mode);
        functionRank = (LinearLayout)findViewById(R.id.function_rank);
        optimizeButt = (TextView)findViewById(R.id.optimize_butt);
        //remaining layout
        percentImage = (ImageView)findViewById(R.id.image_percent);
        percentTxt = (TextView)findViewById(R.id.text_percent);
        remainingTimeTxt = (TextView)findViewById(R.id.text_timeremaining);
        remainingTypeTxt = (TextView)findViewById(R.id.text_remaining_type);
        chargingImage =  (ImageView)findViewById(R.id.main_charging_status);
        //statistic layout
        volTxt = (TextView)findViewById(R.id.text_vol);
        tempTxt = (TextView)findViewById(R.id.text_temp);
        capacityTxt = (TextView)findViewById(R.id.text_capacity);


        //tool layout
        wifiButt = (LinearLayout)findViewById(R.id.wifi_butt);
        dataButt = (LinearLayout)findViewById(R.id.data_butt);
        stateButt = (LinearLayout)findViewById(R.id.state_butt);
        vibrateButt = (LinearLayout)findViewById(R.id.vibrate_butt);
        gpsButt = (LinearLayout)findViewById(R.id.gps_butt);
        bluetoothButt = (LinearLayout)findViewById(R.id.bluetooth_butt);
        airplaneButt = (LinearLayout)findViewById(R.id.airplane_butt);
        lightnessButt = (LinearLayout)findViewById(R.id.lighness_butt);
        screenoffButt = (LinearLayout)findViewById(R.id.screenoff_butt);
        rotateButt = (LinearLayout)findViewById(R.id.rotate_butt);
        syncButt = (LinearLayout)findViewById(R.id.sync_butt);
        screenoffButt = (LinearLayout)findViewById(R.id.screenoff_butt);
        //view state
        wifiState = (ImageView)findViewById(R.id.wifi_state);
        dataState = (ImageView)findViewById(R.id.data_state);
        ringerState = (ImageView)findViewById(R.id.ringtone_state);
        vibrateState = (ImageView)findViewById(R.id.vibrate_state);
        gpsState = (ImageView)findViewById(R.id.gps_state);
        bluetoothState = (ImageView)findViewById(R.id.bluetooth_state);
        airplaneState = (ImageView)findViewById(R.id.airplane_state);
        lightnessState = (ImageView)findViewById(R.id.lightness_state);
        rotateState = (ImageView)findViewById(R.id.rotate_state);
        syncState = (ImageView)findViewById(R.id.sync_state);
        screenoffState  = (ImageView)findViewById(R.id.screenoff_state);
        updateTool();
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


        optimizeButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent optimizeIntent = new Intent(MainActivity.this,OptimizeActivity.class);
                startActivity(optimizeIntent);
            }
        });

        functionCharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent chargeIntent = new Intent(MainActivity.this, ChargeActivity.class);
                startActivity(chargeIntent);
            }
        });

        functionRank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent rankIntent = new Intent(MainActivity.this, RankActivity.class);
                startActivity(rankIntent);
            }
        });

        functionInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent infoIntent = new Intent(MainActivity.this, InfoActivity.class);
                startActivity(infoIntent);
            }
        });

        functionSaverMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent  modeIntent = new Intent(MainActivity.this, SaverModeActivity.class);
                startActivity(modeIntent);
            }
        });
        //tool event
        wifiButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingUtils.switchWifiState(MainActivity.this);
                updateTool();
            }
        });
        dataButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
                    SettingUtils.switchDataConnection(MainActivity.this);
                    updateTool();
                }else{
                    startActivity(
                            new Intent(Settings.ACTION_SETTINGS));
                }
            }
        });
        stateButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingUtils.switchSoundProfile(MainActivity.this);
                updateTool();
            }
        });
        vibrateButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingUtils.switchVibrateWhenRinging(MainActivity.this);
                updateTool();
            }
        });
        lightnessButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingUtils.switchBrightness(MainActivity.this);
                updateTool();
            }
        });
        gpsButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingUtils.switchGPSState(MainActivity.this);
                updateTool();
            }
        });
        bluetoothButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingUtils.switchBluetooth(MainActivity.this);
                updateTool();
            }
        });
        rotateButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingUtils.switchAutoOrientation(MainActivity.this);
                updateTool();
            }
        });
        syncButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingUtils.switchSyncState(MainActivity.this);
                updateTool();
            }
        });
        airplaneButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(
                        new Intent(Settings.ACTION_SETTINGS));
            }
        });
        screenoffButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingUtils.switchScreenOffTime(MainActivity.this);
                updateTool();
            }
        });
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (serviceConnected) {
            context.unbindService(serviceConnection);
            serviceConnected = false;
        }
        unregisterReceiver(WifiStateChangedReceiver);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateTool();
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


    private void handleUpdatedBatteryInfo(BatteryInfo info) {
                percentTxt.setText(info.percent+"%");
                percentTxt.setTypeface(EasyFonts.robotoMedium(MainActivity.this));
                remainingTypeTxt.setTypeface(EasyFonts.robotoMedium(MainActivity.this));
                remainingTimeTxt.setText(str.predictorTimeRemaining(info, Str.DEFAULT_STATE));
                if (info.percent<=20) {
                    percentImage.setImageResource(R.drawable.battery20);
                }else if (info.percent>20 && info.percent <=50){
                    percentImage.setImageResource(R.drawable.battery40);
                }else if (info.percent>50 && info.percent <=75){
                    percentImage.setImageResource(R.drawable.battery60);
                }else if (info.percent>75 && info.percent <=95){
                    percentImage.setImageResource(R.drawable.battery80);
                }else if (info.percent>95){
                    percentImage.setImageResource(R.drawable.battery100);
                }


                if (info.plugged == BatteryInfo.PLUGGED_UNPLUGGED){
                    chargingImage.setVisibility(View.GONE);
                    remainingTypeTxt.setText(res.getString(R.string.time_remaining));
                }else {
                    chargingImage.setVisibility(View.VISIBLE);
                    remainingTypeTxt.setText(res.getString(R.string.charge_time_remaining));
                }
                tempTxt.setText(str.formatTemp(info.temperature, settings.getInt(SettingsActivity.TEMP_TYPE,0)==0?false:true));
                tempTxt.setTypeface(EasyFonts.robotoThin(this));
                capacityTxt.setTypeface(EasyFonts.robotoThin(this));
                volTxt.setTypeface(EasyFonts.robotoThin(this));
                volTxt.setText(str.formatVoltage(info.voltage));
                capacityTxt.setText((int)(info.percent*getBatteryCapacity()/100)+"mAh");
    }

    public void updateTool() {
        if (SettingUtils.getWifiOnOff(MainActivity.this)) {
            wifiState.setImageResource(R.drawable.tool_wifi_on);
        } else {
            wifiState.setImageResource(R.drawable.tool_wifi_off);
        }
        if (SettingUtils.getBluetoothState(MainActivity.this)) {
            bluetoothState.setImageResource(R.drawable.tool_bluetooth_on);
        } else {
            bluetoothState.setImageResource(R.drawable.tool_bluetooth_off);
        }
        if (SettingUtils.getAutoOrientationState(MainActivity.this)) {
            rotateState.setImageResource(R.drawable.tool_rotate_on);
        } else {
            rotateState.setImageResource(R.drawable.tool_rotate_off);
        }
        if (SettingUtils.getSoundProfileState(MainActivity.this) == AudioManager.RINGER_MODE_NORMAL) {
            ringerState.setImageResource(R.drawable.ringer_normal);
        } else if (SettingUtils.getSoundProfileState(MainActivity.this) == AudioManager.RINGER_MODE_VIBRATE) {
            ringerState.setImageResource(R.drawable.ringer_vibrate);
        } else {
            ringerState.setImageResource(R.drawable.ringer_slient);
        }
        if (SettingUtils.getVibrateWhenRinging(MainActivity.this) == 1) {
            vibrateState.setImageResource(R.drawable.tool_vibrate_on);
        } else {
            vibrateState.setImageResource(R.drawable.tool_vibrate_off);
        }
        if (SettingUtils.getSyncState(MainActivity.this)) {
            syncState.setImageResource(R.drawable.tool_sync_on);
        } else {
            syncState.setImageResource(R.drawable.tool_sync_off);
        }
        if (SettingUtils.getDataMobileState(MainActivity.this)) {
            dataState.setImageResource(R.drawable.tool_data_on);
        } else {
            dataState.setImageResource(R.drawable.tool_data_off);
        }
        if (SettingUtils.getScreenOffTime(MainActivity.this) == 15000) {
            screenoffState.setImageResource(R.drawable.screen_off15s);
        } else if (SettingUtils.getScreenOffTime(MainActivity.this) == 30000) {
            screenoffState.setImageResource(R.drawable.screen_off30s);
        } else if (SettingUtils.getScreenOffTime(MainActivity.this) == 60000) {
            screenoffState.setImageResource(R.drawable.screen_off1m);
        } else if (SettingUtils.getScreenOffTime(MainActivity.this) == 120000) {
            screenoffState.setImageResource(R.drawable.screen_off2m);
        } else if (SettingUtils.getScreenOffTime(MainActivity.this) == 600000) {
            screenoffState.setImageResource(R.drawable.screen_off10m);
        } else if (SettingUtils.getScreenOffTime(MainActivity.this) == 1800000) {
            screenoffState.setImageResource(R.drawable.screen_off30m);
        } else {
            screenoffState.setImageResource(R.drawable.screen_off30m);
        }
        if (SettingUtils.getGPSState(MainActivity.this)) {
            gpsState.setImageResource(R.drawable.tool_gps_on);
        } else {
            gpsState.setImageResource(R.drawable.tool_gps_off);
        }
            if (SettingUtils.getCurrentBrightness(MainActivity.this).equals("auto")) {
                lightnessState.setImageResource(R.drawable.lightnessauto);
            } else {
                if (SettingUtils.getCurrentBrightness(MainActivity.this).equals("0.04")) {
                    lightnessState.setImageResource(R.drawable.lightness0);
                } else if (SettingUtils.getCurrentBrightness(MainActivity.this).equals("0.4")) {
                    lightnessState.setImageResource(R.drawable.lightness1);
                } else if (SettingUtils.getCurrentBrightness(MainActivity.this).equals("0.6")) {
                    lightnessState.setImageResource(R.drawable.lightness2);
                } else if (SettingUtils.getCurrentBrightness(MainActivity.this).equals("1")) {
                    lightnessState.setImageResource(R.drawable.lightness3);
                }
            }
        }


    //broadcast wifi state change wifi on / wifi off
    private BroadcastReceiver WifiStateChangedReceiver
            = new BroadcastReceiver(){

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            int extraWifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE ,
                    WifiManager.WIFI_STATE_UNKNOWN);

            switch(extraWifiState){
                case WifiManager.WIFI_STATE_DISABLED:
                    wifiState.setImageResource(R.drawable.tool_wifi_off);
                    break;
                case WifiManager.WIFI_STATE_ENABLED:
                    wifiState.setImageResource(R.drawable.tool_wifi_on);
                    break;
            }

        }};



}
