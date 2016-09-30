package com.example.nguyenduylong.pin.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import com.example.nguyenduylong.pin.R;
import com.example.nguyenduylong.pin.model.SaverModeInfo;
import com.gc.materialdesign.views.ButtonFlat;
import com.gc.materialdesign.views.ButtonFloat;
import com.gc.materialdesign.views.ButtonRectangle;
import com.rey.material.widget.RadioButton;
import com.rey.material.widget.Switch;

/**
 * Created by nguyen duy long on 4/5/2016.
 */
public class CreatSaverModeActivity extends AppCompatActivity {
    private static final String MODE_NAME = "mode_name";
    private static final String MODE_DETAIL = "mode_detail";
    private static final String MODE_BRIGHTNESS = "mode_brightness";
    private static final String MODE_SCREENOFF = "mode_screenoff";
    private static final String MODE_RINGERMODE = "mode_ringermode";
    private static final String MODE_WIFI = "mode_wifi";
    private static final String MODE_BLUETOOTH = "mode_bluetooth";
    private static final String MODE_SYNC= "mode_sync";
    private static final String MODE_HAPTIC = "mode_haptic";
    private static final String NEW_MODE = "new_mode";
    private static final String MODE_SELECT = "mode_seleted";
    private static final String MODE_DEFAULT = "mode_default";
    public static final String MODE_CHANGE = "mode_change";
    Toolbar toolBar;
    int selectedPosition = 0;
    double currentBirghtness = 0;
    int currentScreenOff = 15;
    int currentRingerMode = 0;
    Switch wifiSwitch,bluetoothSwitch,syncSwitch, hapticSwitch;
    TextView brightnessButt, ringerButt,screenoffButt;
    ButtonRectangle cancleButton , saveButton;
    EditText nameField , detailField;
    private TextInputLayout inputLayoutName;
    SaverModeInfo changeMode = null ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_savermode);
        toolBar = (Toolbar) findViewById(R.id.add_mode_tool_bar);
        CreatSaverModeActivity.this.setSupportActionBar(toolBar);
        toolBar.setNavigationIcon(R.drawable.sliding_menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //tool

        wifiSwitch = (Switch)findViewById(R.id.wifi_switch);
        bluetoothSwitch = (Switch)findViewById(R.id.bluetooth_switch);
        syncSwitch = (Switch)findViewById(R.id.sync_switch);
        hapticSwitch =(Switch)findViewById(R.id.haptic_switch);
        brightnessButt = (TextView)findViewById(R.id.brightness_butt);
        ringerButt = (TextView)findViewById(R.id.ringermode_butt);
        screenoffButt = (TextView)findViewById(R.id.screenoff_butt);
        cancleButton = (ButtonRectangle) findViewById(R.id.cancle_button);
        saveButton = (ButtonRectangle)findViewById(R.id.save_button);
        nameField =(EditText)findViewById(R.id.input_name);
        detailField = (EditText)findViewById(R.id.input_detail);
        inputLayoutName = (TextInputLayout) findViewById(R.id.input_layout_name);
        changeMode = getIntent().getParcelableExtra(MODE_CHANGE);
        if (changeMode!=null){
            nameField.setText(changeMode.getName());
            detailField.setText(changeMode.getDetail());
            wifiSwitch.setChecked(changeMode.isWifi());
            bluetoothSwitch.setChecked(changeMode.isBluetooth());
            syncSwitch.setChecked(changeMode.isSyncState());
            hapticSwitch.setChecked(changeMode.isHapticState());
            if (changeMode.isAutoBrightness()){
                brightnessButt.setText(getString(R.string.auto));
            }else {
                if (changeMode.getBirgthness()==0.05){
                    brightnessButt.setText(getString(R.string.butt1));
                }
                if (changeMode.getBirgthness()==0.4){
                    brightnessButt.setText(getString(R.string.butt2));
                }
                if (changeMode.getBirgthness()==0.6){
                    brightnessButt.setText(getString(R.string.butt3));
                }
                if (changeMode.getBirgthness()==1.0){
                    brightnessButt.setText(getString(R.string.butt4));
                }
            }
            if (changeMode.getScreenOffTime() ==15){
                screenoffButt.setText(getString(R.string.sbutt1));
            }
            if (changeMode.getScreenOffTime() ==30){
                screenoffButt.setText(getString(R.string.sbutt2));
            }
            if (changeMode.getScreenOffTime() ==60){
                screenoffButt.setText(getString(R.string.sbutt3));
            }
            if (changeMode.getScreenOffTime() ==120){
                screenoffButt.setText(getString(R.string.sbutt4));
            }
            if (changeMode.getScreenOffTime() ==600){
                screenoffButt.setText(getString(R.string.sbutt5));
            }
            if (changeMode.getScreenOffTime() ==1800){
                screenoffButt.setText(getString(R.string.sbutt6));
            }
            if (changeMode.getRingerMode()==0){
                ringerButt.setText(getString(R.string.rbutt1));
            }
            if (changeMode.getRingerMode()==1){
                ringerButt.setText(getString(R.string.rbutt2));
            }
            if (changeMode.getRingerMode()==2){
                ringerButt.setText(getString(R.string.rbutt3));
            }

        }
        brightnessButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(CreatSaverModeActivity.this, R.style.FullHeightDialog);
                dialog.setContentView(R.layout.dialog_select_brightness);
                dialog.setCancelable(true);
                RadioButton autoButt, butt1,butt2,butt3,butt4;
                autoButt = (RadioButton)dialog.findViewById(R.id.auto_butt);
                butt1 = (RadioButton)dialog.findViewById(R.id.butt1);
                butt2 = (RadioButton)dialog.findViewById(R.id.butt2);
                butt3 = (RadioButton)dialog.findViewById(R.id.butt3);
                butt4 = (RadioButton)dialog.findViewById(R.id.butt4);
                ButtonFlat cancleButt = (ButtonFlat)dialog.findViewById(R.id.cancle_butt);
                if (currentBirghtness ==0){
                    autoButt.setChecked(true); butt1.setChecked(false); butt2.setChecked(false);
                    butt3.setChecked(false); butt4.setChecked(false);
                }else if (currentBirghtness==0.05){
                    autoButt.setChecked(false);
                    butt1.setChecked(true);
                    butt2.setChecked(false);
                    butt3.setChecked(false);
                    butt4.setChecked(false);
                }else if (currentBirghtness==0.4){
                    autoButt.setChecked(false);
                    butt1.setChecked(false);
                    butt2.setChecked(true);
                    butt3.setChecked(false);
                    butt4.setChecked(false);
                }else if (currentBirghtness==0.6){
                    autoButt.setChecked(false);
                    butt1.setChecked(false);
                    butt2.setChecked(false);
                    butt3.setChecked(true);
                    butt4.setChecked(false);
                }else if (currentBirghtness==1.0){
                    autoButt.setChecked(false);
                    butt1.setChecked(false);
                    butt2.setChecked(false);
                    butt3.setChecked(false);
                    butt4.setChecked(true);
                }
                autoButt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        currentBirghtness=0;
                        brightnessButt.setText(getString(R.string.auto));
                        dialog.dismiss();
                    }
                });
                butt1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        currentBirghtness = 0.05;
                        brightnessButt.setText(getString(R.string.butt1));
                        dialog.dismiss();
                    }
                });
                butt2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        currentBirghtness=0.4;
                        brightnessButt.setText(getString(R.string.butt2));
                        dialog.dismiss();
                    }
                });
                butt3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        currentBirghtness=0.6;
                        brightnessButt.setText(getString(R.string.butt3));
                        dialog.dismiss();
                    }
                });
                butt4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        currentBirghtness=1.0;
                        brightnessButt.setText(getString(R.string.butt4));
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
        screenoffButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(CreatSaverModeActivity.this, R.style.FullHeightDialog);
                dialog.setContentView(R.layout.dialog_select_screen_off);
                dialog.setCancelable(true);
                RadioButton sbutt1,sbutt2,sbutt3,sbutt4,sbutt5,sbutt6;;
                sbutt1 = (RadioButton)dialog.findViewById(R.id.sbutt1);
                sbutt2 = (RadioButton)dialog.findViewById(R.id.sbutt2);
                sbutt3 = (RadioButton)dialog.findViewById(R.id.sbutt3);
                sbutt4 = (RadioButton)dialog.findViewById(R.id.sbutt4);
                sbutt5 = (RadioButton)dialog.findViewById(R.id.sbutt5);
                sbutt6 = (RadioButton)dialog.findViewById(R.id.sbutt6);
                ButtonFlat cancleButt = (ButtonFlat)dialog.findViewById(R.id.cancle_butt);
                if (currentScreenOff==15){
                    sbutt1.setChecked(true);
                    sbutt2.setChecked(false);
                    sbutt3.setChecked(false);
                    sbutt4.setChecked(false);
                    sbutt5.setChecked(false);
                    sbutt6.setChecked(false);
                }else if (currentScreenOff==30){
                    sbutt1.setChecked(false);
                    sbutt2.setChecked(true);
                    sbutt3.setChecked(false);
                    sbutt4.setChecked(false);
                    sbutt5.setChecked(false);
                    sbutt6.setChecked(false);
                }else if (currentScreenOff==60){
                    sbutt1.setChecked(false);
                    sbutt2.setChecked(false);
                    sbutt3.setChecked(true);
                    sbutt4.setChecked(false);
                    sbutt5.setChecked(false);
                    sbutt6.setChecked(false);
                }else if (currentScreenOff==120){
                    sbutt1.setChecked(false);
                    sbutt2.setChecked(false);
                    sbutt3.setChecked(false);
                    sbutt4.setChecked(true);
                    sbutt5.setChecked(false);
                    sbutt6.setChecked(false);
                }
                else if (currentScreenOff==600){
                    sbutt1.setChecked(false);
                    sbutt2.setChecked(false);
                    sbutt3.setChecked(false);
                    sbutt5.setChecked(true);
                    sbutt4.setChecked(false);
                    sbutt6.setChecked(false);
                }
                else if (currentScreenOff==1800){
                    sbutt1.setChecked(false);
                    sbutt2.setChecked(false);
                    sbutt3.setChecked(false);
                    sbutt6.setChecked(true);
                    sbutt5.setChecked(false);
                    sbutt4.setChecked(false);
                }
                sbutt1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        currentScreenOff=15;
                        screenoffButt.setText(getString(R.string.sbutt1));
                        dialog.dismiss();
                    }
                });
                sbutt2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        currentScreenOff=30;
                        screenoffButt.setText(getString(R.string.sbutt2));
                        dialog.dismiss();
                    }
                });
                sbutt3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        currentScreenOff=60;
                        screenoffButt.setText(getString(R.string.sbutt3));
                        dialog.dismiss();
                    }
                });
                sbutt4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        currentScreenOff=120;
                        screenoffButt.setText(getString(R.string.sbutt4));
                        dialog.dismiss();
                    }
                });
                sbutt5.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        currentScreenOff=600;
                        screenoffButt.setText(getString(R.string.sbutt5));
                        dialog.dismiss();
                    }
                });
                sbutt6.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        currentScreenOff=1800;
                        screenoffButt.setText(getString(R.string.sbutt6));
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
        ringerButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(CreatSaverModeActivity.this, R.style.FullHeightDialog);
                dialog.setContentView(R.layout.dialog_select_ringermode);
                dialog.setCancelable(true);
                RadioButton  butt1,butt2,butt3;
                butt1 = (RadioButton)dialog.findViewById(R.id.rbutt1);
                butt2 = (RadioButton)dialog.findViewById(R.id.rbutt2);
                butt3 = (RadioButton)dialog.findViewById(R.id.rbutt3);
                ButtonFlat cancleButt = (ButtonFlat)dialog.findViewById(R.id.cancle_butt);
                if (currentRingerMode ==0){
                     butt1.setChecked(true); butt2.setChecked(false);
                    butt3.setChecked(false);
                }else if (currentRingerMode==1){
                    butt2.setChecked(true);
                    butt1.setChecked(false);
                    butt3.setChecked(false);
                }else if (currentRingerMode==2){
                    butt1.setChecked(false);
                    butt3.setChecked(true);
                    butt2.setChecked(false);
                }
                butt1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        currentRingerMode=0;
                        ringerButt.setText(getString(R.string.rbutt1));
                        dialog.dismiss();
                    }
                });
                butt2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        currentRingerMode=1;
                        ringerButt.setText(getString(R.string.rbutt2));
                        dialog.dismiss();
                    }
                });
                butt3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        currentRingerMode=2;
                        ringerButt.setText(getString(R.string.rbutt3));
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



        cancleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent backIntent = new Intent(CreatSaverModeActivity.this, SaverModeActivity.class);
                startActivity(backIntent);
                finish();
            }
        });
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    if (nameField.getText().toString().trim().isEmpty()){
                        inputLayoutName.setError(getString(R.string.error_name));
                        nameField.requestFocus();
                    }else {
                        if (changeMode!=null){
                            changeMode.setDetail(detailField.getText().toString().trim());
                            changeMode.setWifi(wifiSwitch.isChecked());
                            changeMode.setAutoBrightness(currentBirghtness == 0 ? true : false);
                            changeMode.setBirgthness(currentBirghtness);
                            changeMode.setScreenOffTime(currentScreenOff);
                            changeMode.setRingerMode(currentRingerMode);
                            changeMode.setBluetooth(bluetoothSwitch.isChecked());
                            changeMode.setHapticState(hapticSwitch.isChecked());
                            changeMode.setSyncState(syncSwitch.isChecked());
                            Intent backIntent = new Intent(CreatSaverModeActivity.this, SaverModeActivity.class);
                            backIntent.putExtra(NEW_MODE, changeMode);
                            startActivity(backIntent);
                            finish();
                        }else {
                            SaverModeInfo newInfo = new SaverModeInfo(nameField.getText().toString().trim());
                            newInfo.setDetail(detailField.getText().toString().trim());
                            newInfo.setWifi(wifiSwitch.isChecked());
                            newInfo.setAutoBrightness(currentBirghtness == 0 ? true : false);
                            newInfo.setBirgthness(currentBirghtness);
                            newInfo.setScreenOffTime(currentScreenOff);
                            newInfo.setRingerMode(currentRingerMode);
                            newInfo.setBluetooth(bluetoothSwitch.isChecked());
                            newInfo.setHapticState(hapticSwitch.isChecked());
                            newInfo.setSyncState(syncSwitch.isChecked());
                            Intent backIntent = new Intent(CreatSaverModeActivity.this, SaverModeActivity.class);
                            backIntent.putExtra(NEW_MODE, newInfo);
                            startActivity(backIntent);
                            finish();
                        }
                    }
            }
        });
    }
    @Override
    public void onPause() {
        super.onPause();

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
