package com.example.nguyenduylong.pin.model;

import android.graphics.drawable.Drawable;

import java.text.DecimalFormat;

/**
 * Created by nguyen duy long on 3/30/2016.
 */
public class AppProcessInfo {
    private  long size;
    private String appName;
    private Drawable appIcon;
    private boolean isSystemApp;
    private String packageName;
    private boolean isRunning;
    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public Drawable getAppIcon() {
        return appIcon;
    }

    public void setAppIcon(Drawable appIcon) {
        this.appIcon = appIcon;
    }

    public boolean isSystemApp() {
        return isSystemApp;
    }

    public void setIsSystemApp(boolean isSystemApp) {
        this.isSystemApp = isSystemApp;
    }


    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setIsRunning(boolean isRunning) {
        this.isRunning = isRunning;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getSizeDisplay(){
        DecimalFormat df = new DecimalFormat("###.##");
        String sizeDisplay = "";

        long mSize = size;

        if(mSize>=1024)
        {
            mSize/=1024;
            if(mSize>=1024)
            {
                mSize/=1024;
                if (mSize>=1024)
                {
                    mSize/=1024;
                    sizeDisplay = df.format(mSize)+"GB";
                }
                else {
                    sizeDisplay =df.format(mSize)+"MB";
                }
            }
            else {
                sizeDisplay = df.format(mSize)+"KB";
            }
        }
        else {
            sizeDisplay = mSize+"B";
        }

        return sizeDisplay;
    }
}
