package com.example.nguyenduylong.pin.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nguyenduylong.pin.R;
import com.example.nguyenduylong.pin.activity.CreatSaverModeActivity;
import com.example.nguyenduylong.pin.model.AppProcessInfo;
import com.example.nguyenduylong.pin.model.SaverModeInfo;
import com.rey.material.widget.RadioButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nguyen duy long on 4/5/2016.
 */
public class SaverModeAdapter extends BaseAdapter {
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
    private List<SaverModeInfo> saveModeList;
    private Context context;
    private List<SaveModeHolder> holders;

    public SaverModeAdapter(Context mContext, List<SaverModeInfo> mSaveModeInfo)
    {
        context = mContext;
        saveModeList = mSaveModeInfo;

        holders = new ArrayList<SaveModeHolder>();
        for(int i = 0; i < mSaveModeInfo.size(); i++)
        {
            holders.add(new SaveModeHolder(null));
        }
    }

    @Override
    public int getCount() {
        return saveModeList.size();
    }


    @Override
    public Object getItem(int position) {
        return saveModeList.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View root = convertView;
        SaveModeHolder holder;

        if(root == null)
        {
            LayoutInflater inflater = LayoutInflater.from(context);

            root = inflater.inflate(R.layout.save_mode_item, parent, false);
            holder = new SaveModeHolder(root);

            root.setTag(holder);

        }
        else
        {
            holder = (SaveModeHolder) root.getTag();
        }

        holder.setContentView(saveModeList.get(position));

        return root;
    }

    class SaveModeHolder
    {

        private ImageView editButt;
        private TextView textName;
        private TextView textDetail;
        private RadioButton selectButt;
        public SaveModeHolder(View view)
        {

            if(view == null)
            {
                return;
            }

            editButt = (ImageView)view.findViewById(R.id.edit_butt);
            textName = (TextView)view.findViewById(R.id.text_name);
            textDetail = (TextView)view.findViewById(R.id.text_detail);
            selectButt = (RadioButton)view.findViewById(R.id.select_butt);
            selectButt.setClickable(false);
            selectButt.setCheckedImmediately(false);
        }

        public void setContentView(final SaverModeInfo object)
        {
            editButt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent changeSaveMode = new Intent(context,CreatSaverModeActivity.class);
                    Bundle bundle = new Bundle();
                    changeSaveMode.putExtra(CreatSaverModeActivity.MODE_CHANGE, (Parcelable) object);
                    context.startActivity(changeSaveMode);
                    saveModeList.remove(object);
                    saveToJson();
                    ((Activity)context).finish();
                }
            });
            textName.setText(object.getName());
            textDetail.setText(object.getDetail());
            if (object.isDefaultMode()){
                editButt.setVisibility(View.GONE);
            }else {
                editButt.setVisibility(View.VISIBLE);
            }
            if (object.isSelected()){
                selectButt.setChecked(true);
            }else {
                selectButt.setChecked(false);
            }
        }
    }
    public void saveToJson() {
        String modeJSon = null;
        JSONArray modeList = new JSONArray();
        for (int i = 0; i < saveModeList.size(); i++) {
            SaverModeInfo info = saveModeList.get(i);
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
        if (modeJSon != null) {
            File appDirectory = new File(Environment.getExternalStorageDirectory() + "/GPaddyBattery/");
            if (!(appDirectory.exists())) {
                try {
                    appDirectory.mkdirs();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            File jsonFile = new File(Environment.getExternalStorageDirectory() + "/GPaddyBattery/saveMode.txt");
            if (!(jsonFile.exists())) {
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
                } finally {
                    try {
                        out.flush();
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            } else {
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
                } finally {
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
