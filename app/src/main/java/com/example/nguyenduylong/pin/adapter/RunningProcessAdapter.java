package com.example.nguyenduylong.pin.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nguyenduylong.pin.R;
import com.example.nguyenduylong.pin.library.SquareImageView;
import com.example.nguyenduylong.pin.model.AppProcessInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nguyen duy long on 4/14/2016.
 */
public class RunningProcessAdapter extends BaseAdapter {
    private ArrayList<ApplicationInfo> runningProcessInfoList;
    private Context context;
    private List<ProcessHolder> holders;

    public RunningProcessAdapter(Context mContext, ArrayList<ApplicationInfo> mAppProcessInfo)
    {
        context = mContext;
        runningProcessInfoList = mAppProcessInfo;

        holders = new ArrayList<ProcessHolder>();
        for(int i = 0; i < mAppProcessInfo.size(); i++)
        {
            holders.add(new ProcessHolder(null));
        }
    }

    @Override
    public int getCount() {
        return runningProcessInfoList.size();
    }


    @Override
    public ApplicationInfo getItem(int position) {
        return runningProcessInfoList.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View root = convertView;
        ProcessHolder holder;

        if(root == null)
        {
            LayoutInflater inflater = LayoutInflater.from(context);

            root = inflater.inflate(R.layout.process_item, parent, false);
            holder = new ProcessHolder(root);

            root.setTag(holder);

        }
        else
        {
            holder = (ProcessHolder) root.getTag();
        }

        holder.setContentView(runningProcessInfoList.get(position));
        Animation animation = AnimationUtils.loadAnimation(context,R.anim.anim_appear);
        root.startAnimation(animation);
        return root;
    }

    class ProcessHolder
    {
        private SquareImageView imageIcon;
        public ProcessHolder(View view)
        {

            if(view == null)
            {
                return;
            }

            imageIcon = (SquareImageView)view.findViewById(R.id.img_icon);
        }

        public void setContentView(ApplicationInfo object) {
            imageIcon.setImageDrawable(context.getPackageManager().getApplicationIcon(object));
        }
    }
}
