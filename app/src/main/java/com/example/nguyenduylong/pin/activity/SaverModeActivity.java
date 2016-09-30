package com.example.nguyenduylong.pin.activity;

import android.app.Dialog;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nguyenduylong.pin.R;
import com.example.nguyenduylong.pin.adapter.SaverModeAdapter;
import com.example.nguyenduylong.pin.model.SaverModeInfo;
import com.example.nguyenduylong.pin.util.SettingUtils;
import com.gc.materialdesign.views.ButtonFlat;
import com.gc.materialdesign.views.ButtonFloat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import static com.example.nguyenduylong.pin.activity.CreatSaverModeActivity.*;

/**
 * Created by nguyen duy long on 4/5/2016.
 */
public class SaverModeActivity extends AppCompatActivity {
    Toolbar toolBar;
    ButtonFloat addButt;
    int selectedPosition = 0;
    Bundle bundle = null;
    SaverModeInfo newInfo = null;
    List<SaverModeInfo> infoList;
    public static final String LIST_JSON = "list_json";
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
    private static final String MODE_AUTO_BRIGHTNESS = "mode_atuo_brightness";
    Dialog savemodeDialog;
    SaverModeAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_savermode);
        newInfo = getIntent().getParcelableExtra(NEW_MODE);
        infoList = new ArrayList<SaverModeInfo>();
        if (newInfo != null) {
            infoList.add(newInfo);
        }
        readJSon();
        if (MainActivity.firstSaveMode) {
            addDefault();
            MainActivity.firstSaveMode = false;
        }
        selectedPosition = getCurrentPosition();
        toolBar = (Toolbar) findViewById(R.id.mode_tool_bar);
        SaverModeActivity.this.setSupportActionBar(toolBar);
        toolBar.setNavigationIcon(R.drawable.sliding_menu);
        addButt = (ButtonFloat) findViewById(R.id.add_butt);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ListView listView = (ListView) findViewById(R.id.list_savemode);
        adapter = new SaverModeAdapter(SaverModeActivity.this, infoList);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SaverModeInfo selectObject = infoList.get(position);
                savemodeDialog = new Dialog(SaverModeActivity.this);
                savemodeDialog.setContentView(R.layout.dialog_save_mode);
                initDialog(selectObject,position);
                savemodeDialog.show();
            }
        });
        addButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addIntent = new Intent(SaverModeActivity.this, CreatSaverModeActivity.class);
                startActivity(addIntent);
                saveToJson();
                finish();
            }
        });

    }
    public void initDialog(final SaverModeInfo info, final int position){
        TextView titleTxt, brightnessTxt,screenoffTxt,ringermodeTxt,wifiTxt,bluetoothTxt,syncTxt,hapticTxt;
        ButtonFlat cancleButt,applyButt;
        titleTxt = (TextView)savemodeDialog.findViewById(R.id.text_title);
        brightnessTxt =(TextView)savemodeDialog.findViewById(R.id.text_brightness);
        screenoffTxt= (TextView)savemodeDialog.findViewById(R.id.text_screen_off);
        ringermodeTxt=(TextView)savemodeDialog.findViewById(R.id.text_ringer_mode);
        wifiTxt = (TextView)savemodeDialog.findViewById(R.id.text_wifi);
        bluetoothTxt =(TextView)savemodeDialog.findViewById(R.id.text_bluetooth);
        syncTxt = (TextView)savemodeDialog.findViewById(R.id.text_sync);
        hapticTxt = (TextView)savemodeDialog.findViewById(R.id.text_haptic);
        cancleButt = (ButtonFlat)savemodeDialog.findViewById(R.id.cancle_butt);
        applyButt = (ButtonFlat)savemodeDialog.findViewById(R.id.apply_butt);
        titleTxt.setText("Chế độ " + info.getName());
        if (info.isWifi()){
            wifiTxt.setText(getString(R.string.switch_on));
        }else {
            wifiTxt.setText(getString(R.string.switch_off));
        }
        if (info.isBluetooth()){
            bluetoothTxt.setText(getString(R.string.switch_on));
        }else {
            bluetoothTxt.setText(getString(R.string.switch_off));
        }
        if (info.isSyncState()){
            syncTxt.setText(getString(R.string.switch_on));
        }else {
            syncTxt.setText(getString(R.string.switch_off));
        }
        if (info.isHapticState()){
            hapticTxt.setText(getString(R.string.switch_on));
        }else {
            hapticTxt.setText(getString(R.string.switch_off));
        }
        if (info.isAutoBrightness()){
            brightnessTxt.setText(getString(R.string.auto));
        }
        if (info.getBirgthness()==0.05){
            brightnessTxt.setText(getString(R.string.butt1));
        }else if (info.getBirgthness()==0.4){
            brightnessTxt.setText(getString(R.string.butt2));
        } else if (info.getBirgthness()==0.6){
            brightnessTxt.setText(getString(R.string.butt3));
        } else if (info.getBirgthness()==1.0){
            brightnessTxt.setText(getString(R.string.butt4));
        }
        if (info.getRingerMode()==0){
            ringermodeTxt.setText(getString(R.string.rbutt1));
        }else if (info.getRingerMode()==1){
            ringermodeTxt.setText(getString(R.string.rbutt2));
        }else {
            ringermodeTxt.setText(getString(R.string.rbutt3));
        }
        if (info.getScreenOffTime()==15){
            screenoffTxt.setText(getString(R.string.sbutt1));
        }else if(info.getScreenOffTime()==30){
            screenoffTxt.setText(getString(R.string.sbutt2));
        }else if(info.getScreenOffTime()==60){
            screenoffTxt.setText(getString(R.string.sbutt3));
        }else if(info.getScreenOffTime()==120){
            screenoffTxt.setText(getString(R.string.sbutt4));
        }else if(info.getScreenOffTime()==600){
            screenoffTxt.setText(getString(R.string.sbutt5));
        }else if(info.getScreenOffTime()==1800){
            screenoffTxt.setText(getString(R.string.sbutt6));
        }
        cancleButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savemodeDialog.dismiss();
            }
        });
        applyButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyMode(info);
                if (selectedPosition != position) {
                    infoList.get(position).setIsSelected(true);
                    infoList.get(selectedPosition).setIsSelected(false);
                    selectedPosition = position;
                    adapter.notifyDataSetChanged();
                }else {
                    infoList.get(position).setIsSelected(true);
                    selectedPosition = position;
                    adapter.notifyDataSetChanged();
                }
                savemodeDialog.dismiss();
            }
        });
    }

    public void applyMode(SaverModeInfo info){
        SettingUtils.setWifiState(SaverModeActivity.this,info.isWifi());
        SettingUtils.setBluetoothState(SaverModeActivity.this, info.isBluetooth());
        SettingUtils.setHapticFeedbackMode(SaverModeActivity.this, info.isHapticState() ? 1 : 0);
        SettingUtils.setSyncState(SaverModeActivity.this, info.isSyncState());
        SettingUtils.setSoundProfile(SaverModeActivity.this,info.getRingerMode());
        if (info.isAutoBrightness()){
            SettingUtils.setAutoBrightness(SaverModeActivity.this);
        }else {
            SettingUtils.setBrightness(SaverModeActivity.this, (int) (info.getBirgthness()*255));
        }
        SettingUtils.setScreenOffTime(info.getScreenOffTime()*1000,SaverModeActivity.this);
    }
    @Override
    protected void onDestroy() {
        saveToJson();
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void readJSon(){
        try {
            File yourFile = new File(Environment.getExternalStorageDirectory(), "/GPaddyBattery/saveMode.txt");
            FileInputStream stream = new FileInputStream(yourFile);
            String jsonStr = null;
            try {
                FileChannel fc = stream.getChannel();
                MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());

                jsonStr = Charset.forName("UTF-8").decode(bb).toString();
            }
            finally {
                stream.close();
            }

            JSONObject jsonObj = new JSONObject(jsonStr);


            JSONArray data  = jsonObj.getJSONArray(LIST_JSON);

            // looping through All nodes
            for (int i = 0; i < data.length(); i++) {
                JSONObject c = data.getJSONObject(i);
                SaverModeInfo info = new SaverModeInfo(c.getString(MODE_NAME));
                info.setDetail(c.getString(MODE_DETAIL));
                info.setAutoBrightness(c.getBoolean(MODE_AUTO_BRIGHTNESS));
                info.setDefaultMode(c.getBoolean(MODE_DEFAULT));
                info.setScreenOffTime(c.getInt(MODE_SCREENOFF));
                info.setIsSelected(c.getBoolean(MODE_SELECT));
                info.setBirgthness(c.getInt(MODE_BRIGHTNESS));
                info.setBluetooth(c.getBoolean(MODE_BLUETOOTH));
                info.setWifi(c.getBoolean(MODE_WIFI));
                info.setHapticState(c.getBoolean(MODE_HAPTIC));
                info.setRingerMode(c.getInt(MODE_RINGERMODE));
                info.setSyncState(c.getBoolean(MODE_SYNC));
                infoList.add(info);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void addDefault(){
        SaverModeInfo deepSleepInfo = new SaverModeInfo(getString(R.string.deep_sleep));
        deepSleepInfo.setDetail(getString(R.string.deep_sleep_detail));
        deepSleepInfo.setSyncState(false);
        deepSleepInfo.setHapticState(false);
        deepSleepInfo.setBluetooth(false);
        deepSleepInfo.setRingerMode(0);
        deepSleepInfo.setAutoBrightness(false);
        deepSleepInfo.setBirgthness(0.05);
        deepSleepInfo.setDefaultMode(true);
        deepSleepInfo.setScreenOffTime(15);
        deepSleepInfo.setWifi(false);
        infoList.add(deepSleepInfo);
        SaverModeInfo defaultInfo = new SaverModeInfo(getString(R.string.default_mode));
        defaultInfo.setDetail(getString(R.string.default_detail));
        defaultInfo.setSyncState(true);
        defaultInfo.setHapticState(true);
        defaultInfo.setBluetooth(false);
        defaultInfo.setRingerMode(2);
        defaultInfo.setAutoBrightness(true);
        defaultInfo.setDefaultMode(true);
        defaultInfo.setScreenOffTime(15);
        defaultInfo.setWifi(true);
        infoList.add(defaultInfo);
        SaverModeInfo balanceInfo = new SaverModeInfo(getString(R.string.balance));
        balanceInfo.setDetail(getString(R.string.balance_detail));
        balanceInfo.setSyncState(false);
        balanceInfo.setHapticState(false);
        balanceInfo.setBluetooth(false);
        balanceInfo.setRingerMode(1);
        balanceInfo.setAutoBrightness(true);
        balanceInfo.setDefaultMode(true);
        balanceInfo.setScreenOffTime(15);
        balanceInfo.setWifi(true);
        infoList.add(balanceInfo);
    }
    public int getCurrentPosition(){
        for (int i=0;i<infoList.size();i++){
            if (infoList.get(i).isSelected()){
                return i;
            }
        }
        return 0;
    }

    public void saveToJson() {
        String modeJSon = null;
        JSONArray modeList = new JSONArray();
        for (int i = 0; i < infoList.size(); i++) {
            SaverModeInfo info = infoList.get(i);
                JSONObject object = new JSONObject();
                try {
                    object.put(MODE_NAME, info.getName());
                    object.put(MODE_DETAIL, info.getDetail());
                    object.put(MODE_BRIGHTNESS, info.getBirgthness());
                    object.put(MODE_RINGERMODE, info.getRingerMode());
                    object.put(MODE_HAPTIC, info.isHapticState());
                    object.put(MODE_WIFI, info.isWifi());
                    object.put(MODE_SYNC, info.isSyncState());
                    object.put(MODE_BLUETOOTH, info.isBluetooth());
                    object.put(MODE_SCREENOFF, info.getScreenOffTime());
                    object.put(MODE_SELECT, info.isSelected());
                    object.put(MODE_DEFAULT, info.isDefaultMode());
                    object.put(MODE_AUTO_BRIGHTNESS, info.isAutoBrightness());
                    modeList.put(object);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

        }
        JSONObject listObject = new JSONObject();
        try {
            listObject.put(LIST_JSON, modeList);
            modeJSon = listObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (modeJSon != null){
            File appDirectory = new File(Environment.getExternalStorageDirectory()+"/GPaddyBattery/");
            if (!(appDirectory.exists())) {
                try {
                    appDirectory.mkdirs();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            File jsonFile = new File(Environment.getExternalStorageDirectory()+"/GPaddyBattery/saveMode.txt");
            if (!(jsonFile.exists())){
                try {
                    jsonFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Writer out = null;
                try {
                    out = new BufferedWriter(new OutputStreamWriter(
                            new FileOutputStream(jsonFile), "UTF-8"));
                    out.write(modeJSon);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }finally {
                    try {
                        out.flush();
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }else {
                Writer out = null;
                PrintWriter writer = null;
                try {
                    writer = new PrintWriter(jsonFile);
                    writer.print("");
                    writer.close();
                    out = new BufferedWriter(new OutputStreamWriter(
                                new FileOutputStream(jsonFile), "UTF-8"));
                    out.write(modeJSon);
                    } catch (FileNotFoundException e) {
                          e.printStackTrace();
                    } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }finally {
                    try {
                        out.flush();
                        out.close();
                     } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            }
        }
    }
}
