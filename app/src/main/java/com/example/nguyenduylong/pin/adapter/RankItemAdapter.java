package com.example.nguyenduylong.pin.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nguyenduylong.pin.model.AppProcessInfo;
import com.example.nguyenduylong.pin.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nguyen duy long on 4/1/2016.
 */
public class RankItemAdapter  extends BaseAdapter{
    private List<AppProcessInfo> appProcessInfoList;
    private Context context;
    private List<RankHolder> holders;

    public RankItemAdapter(Context mContext, List<AppProcessInfo> mAppProcessInfo)
    {
        context = mContext;
        appProcessInfoList = mAppProcessInfo;

        holders = new ArrayList<RankHolder>();
        for(int i = 0; i < mAppProcessInfo.size(); i++)
        {
            holders.add(new RankHolder(null));
        }
    }

    @Override
    public int getCount() {
        return appProcessInfoList.size();
    }


    @Override
    public Object getItem(int position) {
        return appProcessInfoList.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View root = convertView;
        RankHolder holder;

        if(root == null)
        {
            LayoutInflater inflater = LayoutInflater.from(context);

            root = inflater.inflate(R.layout.rank_item, parent, false);
            holder = new RankHolder(root);

            root.setTag(holder);

        }
        else
        {
            holder = (RankHolder) root.getTag();
        }

        holder.setContentView(appProcessInfoList.get(position));

        return root;
    }

    class RankHolder
    {
        private static final String SCHEME = "package";

        private static final String APP_PKG_NAME_21 = "com.android.settings.ApplicationPkgName";

        private static final String APP_PKG_NAME_22 = "pkg";

        private static final String APP_DETAILS_PACKAGE_NAME = "com.android.settings";

        private static final String APP_DETAILS_CLASS_NAME = "com.android.settings.InstalledAppDetails";
        private ImageView imageIcon;
        private TextView textName;
        private TextView stopButt;
        private TextView textSize;
        public RankHolder(View view)
        {

            if(view == null)
            {
                return;
            }

            imageIcon = (ImageView)view.findViewById(R.id.img_app_icon);
            textName = (TextView)view.findViewById(R.id.text_app_name);
            stopButt = (TextView)view.findViewById(R.id.stop_butt);
            textSize = (TextView)view.findViewById(R.id.text_app_size);
        }

        public void setContentView(final AppProcessInfo object)
        {
            imageIcon.setImageDrawable(object.getAppIcon());
            textName.setText(object.getAppName());
            textSize.setText(object.getSizeDisplay());
            if (object.isRunning()) {
                stopButt.setText(context.getString(R.string.stop_butt));
                stopButt.setTextColor(Color.parseColor("#18cc13"));
            }else{
                stopButt.setText(context.getString(R.string.detail_butt));
                stopButt.setTextColor(Color.parseColor("#d1cfcf"));
            }
            stopButt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    final int apiLevel = Build.VERSION.SDK_INT;
                    if (apiLevel >= 9) { // above 2.3
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts(SCHEME, object.getPackageName(), null);
                        intent.setData(uri);
                    } else { // below 2.3
                        final String appPkgName = (apiLevel == 8 ? APP_PKG_NAME_22
                                : APP_PKG_NAME_21);
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setClassName(APP_DETAILS_PACKAGE_NAME,
                                APP_DETAILS_CLASS_NAME);
                        intent.putExtra(appPkgName, object.getPackageName());
                    }
                    context.startActivity(intent);
                }
            });
        }
    }
}
