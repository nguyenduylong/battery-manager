package com.example.nguyenduylong.pin.activity;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import com.example.nguyenduylong.pin.model.AppProcessInfo;
import com.example.nguyenduylong.pin.R;
import com.example.nguyenduylong.pin.adapter.RankItemAdapter;
import com.gc.materialdesign.views.CheckBox;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nguyen duy long on 3/26/2016.
 */
public class RankActivity extends AppCompatActivity {
    PackageManager packageManager=null;
    private Toolbar toolBar;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        packageManager = getApplicationContext().getPackageManager();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rank);
        toolBar = (Toolbar) findViewById(R.id.rank_tool_bar);
        RankActivity.this.setSupportActionBar(toolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final ProcessAsyncTask asyncTask = new ProcessAsyncTask(RankActivity.this);
        asyncTask.execute();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    private boolean isAppIsInRunning(String packageName,Context context) {
        ActivityManager activityManager = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> procInfos = activityManager.getRunningAppProcesses();
        for (int i = 0; i < procInfos.size(); i++) {
            if (procInfos.get(i).processName.equals(packageName)) {
                return true;
            }
        }
        return false;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private class ProcessAsyncTask extends AsyncTask<Void,AppProcessInfo,Integer>{
        Activity context;
        ListView listView;
        TextView countText;
        RankItemAdapter rankAdapter;
        List<AppProcessInfo> listofListview = new ArrayList<AppProcessInfo>();
        public ProcessAsyncTask(Activity mcontext){
            this.context = mcontext;
            listView = (ListView)mcontext.findViewById(R.id.list_app);
            countText =(TextView)findViewById(R.id.app_count);
            rankAdapter = new RankItemAdapter(RankActivity.this,listofListview);
            listView.setAdapter(rankAdapter);
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected Integer doInBackground(Void... params) {
            listofListview.clear();
            List<ApplicationInfo> packages = context.getPackageManager().getInstalledApplications(PackageManager.GET_META_DATA);
            for (int i =0;i<packages.size();i++){
                AppProcessInfo info = new AppProcessInfo();
                ApplicationInfo applicationInfo = packages.get(i);
                info.setPackageName(applicationInfo.packageName);
                info.setAppIcon(applicationInfo.loadIcon(getPackageManager()));
                info.setAppName((String) getPackageManager().getApplicationLabel(applicationInfo));
                if (packageManager.getLaunchIntentForPackage(applicationInfo.packageName) != null) {
                    if ((applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) == 1) {

                    } else if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 1) {
                        // không lấy các app hệ thống
                    } else {
                        // lấy các app cài đặt
                        // thêm phần tử AppInfo vào list , lấy các thuộc tính : packagename , path, tên ứng dựng , icon
                        info.setIsSystemApp(false);
                        info.setIsRunning(isAppIsInRunning(info.getPackageName(), RankActivity.this));
                        info.setSize(new File(applicationInfo.sourceDir).length());
                        publishProgress(info);
                    }
                }

            }
            return rankAdapter.getCount();
        }
        @Override
        protected void onProgressUpdate(AppProcessInfo... values) {
            super.onProgressUpdate(values);
            AppProcessInfo info = values[0];
            if (info.isRunning()){
                listofListview.add(0, info);
            }else {
                listofListview.add(info);

            }
            rankAdapter.notifyDataSetChanged();

        }

        @Override
        protected void onPostExecute(Integer i) {
            super.onPostExecute(i);
            countText.setText(i+"");
        }
    }
}
