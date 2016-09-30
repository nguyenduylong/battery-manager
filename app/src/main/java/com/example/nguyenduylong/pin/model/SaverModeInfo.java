package com.example.nguyenduylong.pin.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by nguyen duy long on 4/5/2016.
 */
public class SaverModeInfo implements Parcelable {
    private boolean wifi, bluetooth, syncState, hapticState;
    private  int screenOffTime;
    private boolean autoBrightness;
    private double birgthness;
    private int ringerMode;
    private String name;
    private String detail;
    private boolean defaultMode;
    private boolean isSelected;
    public SaverModeInfo(String mName){
        this.name= mName;
    }

    protected SaverModeInfo(Parcel in) {
        wifi = in.readByte() != 0;
        bluetooth = in.readByte() != 0;
        syncState = in.readByte() != 0;
        hapticState = in.readByte() != 0;
        screenOffTime = in.readInt();
        autoBrightness = in.readByte() != 0;
        birgthness = in.readDouble();
        ringerMode = in.readInt();
        name = in.readString();
        detail = in.readString();
        defaultMode = in.readByte() != 0;
        isSelected = in.readByte() != 0;
    }

    public static final Creator<SaverModeInfo> CREATOR = new Creator<SaverModeInfo>() {
        @Override
        public SaverModeInfo createFromParcel(Parcel in) {
            return new SaverModeInfo(in);
        }

        @Override
        public SaverModeInfo[] newArray(int size) {
            return new SaverModeInfo[size];
        }
    };

    public int getScreenOffTime() {
        return screenOffTime;
    }

    public void setScreenOffTime(int screenOffTime) {
        this.screenOffTime = screenOffTime;
    }

    public boolean isHapticState() {
        return hapticState;
    }

    public void setHapticState(boolean hapticState) {
        this.hapticState = hapticState;
    }

    public boolean isBluetooth() {
        return bluetooth;
    }

    public void setBluetooth(boolean bluetooth) {
        this.bluetooth = bluetooth;
    }

    public double getBirgthness() {
        return birgthness;
    }

    public void setBirgthness(double birgthness) {
        this.birgthness = birgthness;
    }

    public boolean isAutoBrightness() {
        return autoBrightness;
    }

    public void setAutoBrightness(boolean autoBrightness) {
        this.autoBrightness = autoBrightness;
    }

    public int getRingerMode() {
        return ringerMode;
    }

    public void setRingerMode(int ringerMode) {
        this.ringerMode = ringerMode;
    }

    public boolean isSyncState() {
        return syncState;
    }

    public void setSyncState(boolean syncState) {
        this.syncState = syncState;
    }

    public boolean isWifi() {
        return wifi;
    }

    public void setWifi(boolean wifi) {
        this.wifi = wifi;
    }

    public boolean isDefaultMode() {
        return defaultMode;
    }

    public void setDefaultMode(boolean defaultMode) {
        this.defaultMode = defaultMode;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (wifi ? 1 : 0));
        dest.writeByte((byte) (bluetooth ? 1 : 0));
        dest.writeByte((byte) (syncState ? 1 : 0));
        dest.writeByte((byte) (hapticState ? 1 : 0));
        dest.writeInt(screenOffTime);
        dest.writeByte((byte) (autoBrightness ? 1 : 0));
        dest.writeDouble(birgthness);
        dest.writeInt(ringerMode);
        dest.writeString(name);
        dest.writeString(detail);
        dest.writeByte((byte) (defaultMode ? 1 : 0));
        dest.writeByte((byte) (isSelected ? 1 : 0));
    }
}
