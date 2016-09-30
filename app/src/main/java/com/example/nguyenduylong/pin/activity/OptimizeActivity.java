package com.example.nguyenduylong.pin.activity;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.antonionicolaspina.revealtextview.RevealTextView;
import com.example.nguyenduylong.pin.R;
import com.example.nguyenduylong.pin.adapter.RankItemAdapter;
import com.example.nguyenduylong.pin.adapter.RunningProcessAdapter;
import com.example.nguyenduylong.pin.library.RadarScanView;
import com.example.nguyenduylong.pin.library.RandomTextView;
import com.example.nguyenduylong.pin.model.AppProcessInfo;
import com.example.nguyenduylong.pin.util.Str;
import com.fenjuly.library.ArrowDownloadButton;
import com.gc.materialdesign.views.ButtonRectangle;
import com.vstechlab.easyfonts.EasyFonts;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by nguyen duy long on 4/14/2016.
 */
public class OptimizeActivity extends AppCompatActivity {
    ButtonRectangle superOptimizeButt;
    ButtonRectangle optimizeButt;
    GridView processGridView;
    ActivityManager activityManager ;
    ArrowDownloadButton okbutt ;
    LinearLayout finishLayout;
    RevealTextView finishText;
    SharedPreferences lastOptimize ;
    long lastTime;
    int progress = 0;
    Toolbar toolBar;
    ArrayList<ApplicationInfo> listRunningApp = new ArrayList<ApplicationInfo>();
    public static final String LAST_OPTIMIZE = "last_optimize";
    public static final String LAST_TIME = "last_time";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_optimize);
        toolBar = (Toolbar) findViewById(R.id.charge_tool_bar);
        OptimizeActivity.this.setSupportActionBar(toolBar);
        toolBar.setNavigationIcon(R.drawable.sliding_menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activityManager = (ActivityManager)getSystemService(ACTIVITY_SERVICE);
        lastOptimize = getSharedPreferences(LAST_OPTIMIZE, MODE_PRIVATE);
        lastTime = lastOptimize.getLong(LAST_TIME, 0);
        superOptimizeButt =(ButtonRectangle)findViewById(R.id.soptimize_button);
        optimizeButt = (ButtonRectangle)findViewById(R.id.optimize_button);
        finishLayout = (LinearLayout)findViewById(R.id.finish_layout);
        finishText = (RevealTextView)findViewById(R.id.text_finish);
        okbutt = (ArrowDownloadButton)findViewById(R.id.arrow_button);
        processGridView = (GridView)findViewById(R.id.list_process);
        ((TextView)findViewById(R.id.time_add_title)).setTypeface(EasyFonts.robotoLight(this));
        if ((System.currentTimeMillis() - lastTime)>300000) {
            final ProcessAsyncTask asyncTask = new ProcessAsyncTask(OptimizeActivity.this);
            asyncTask.execute();
        }else {
            ((LinearLayout)findViewById(R.id.optimize_layout)).setVisibility(View.GONE);
            ((LinearLayout)findViewById(R.id.process_layout)).setVisibility(View.GONE);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    ((RadarScanView)findViewById(R.id.radar_view)).setVisibility(View.GONE);
                    ((LinearLayout)findViewById(R.id.time_add_layout)).setVisibility(View.GONE);
                    finishLayout.setVisibility(View.VISIBLE);
                    okbutt.startAnimating();
                    progress = 0;
                    Timer timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progress = progress + 1;
                                    okbutt.setProgress(progress);
                                }
                            });
                        }

                    }, 200, 5);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finishText.replayAnimation();
                            finishText.setVisibility(View.VISIBLE);

                        }
                    }, 800);

                }
            }, 2000);
        }
        superOptimizeButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(android.content.Intent.ACTION_VIEW);
                i.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.gppady.cleaner"));
                startActivity(i);
            }
        });
        optimizeButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < listRunningApp.size(); i++) {
                    activityManager.killBackgroundProcesses(listRunningApp.get(i).packageName);
                    processGridView.getChildAt(i).startAnimation(AnimationUtils.loadAnimation(OptimizeActivity.this, R.anim.anim_kill));
                    processGridView.getChildAt(i).setVisibility(View.GONE);
                }

                ((LinearLayout)findViewById(R.id.optimize_layout)).setVisibility(View.GONE);
                ((LinearLayout)findViewById(R.id.process_layout)).setVisibility(View.GONE);
                finishLayout.setVisibility(View.VISIBLE);
                okbutt.startAnimating();
                progress=0;
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progress = progress + 1;
                                okbutt.setProgress(progress);
                            }
                        });
                    }

                }, 600, 15);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finishText.replayAnimation();
                        finishText.setVisibility(View.VISIBLE);
                    }
                }, 2400);
                SharedPreferences.Editor editTor = lastOptimize.edit();
                editTor.putLong(LAST_TIME,System.currentTimeMillis());
                editTor.commit();
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private class ProcessAsyncTask extends AsyncTask<Void,ActivityManager.RunningAppProcessInfo,Integer> {
        Activity context;
        GridView processList;
        RadarScanView radarView ;
        TextView scanningProcess;
        TextView timeAdd;
        RunningProcessAdapter adapter;
        LinearLayout optimizeLayout;
        LinearLayout processLayout;
        Animation slideup,fadein,killAnim;
        ButtonRectangle  optimizeButt;

        ArrayList<ApplicationInfo> listofgridview = new ArrayList<ApplicationInfo>();
        public ProcessAsyncTask(Activity mcontext){
            this.context = mcontext;
            processList = (GridView)context.findViewById(R.id.list_process);
            radarView = (RadarScanView)context.findViewById(R.id.radar_view);
            scanningProcess = (TextView)context.findViewById(R.id.scanning_process);
            optimizeLayout = (LinearLayout)context.findViewById(R.id.optimize_layout);
            processLayout = (LinearLayout)context.findViewById(R.id.process_layout);
            timeAdd = (TextView)context.findViewById(R.id.text_add_time);
            optimizeLayout.setVisibility(View.GONE);
            processLayout.setVisibility(View.GONE);
            radarView.setVisibility(View.VISIBLE);
            adapter = new RunningProcessAdapter(context,listofgridview);
            processList.setAdapter(adapter);
            slideup = AnimationUtils.loadAnimation(context, R.anim.slide_up);
            fadein = AnimationUtils.loadAnimation(context,R.anim.fade_in);
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected Integer doInBackground(Void... params) {
            List<ActivityManager.RunningAppProcessInfo> procInfos = activityManager.getRunningAppProcesses();
            for (int i=0;i<procInfos.size();i++) {
                SystemClock.sleep(150);
                publishProgress(procInfos.get(i));
            }
            return adapter.getCount();
        }
        @Override
        protected void onProgressUpdate(ActivityManager.RunningAppProcessInfo... values) {
            super.onProgressUpdate(values);
            ActivityManager.RunningAppProcessInfo processinfo = values[0];
            scanningProcess.setText(processinfo.processName);
            try {
                ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo(processinfo.processName, 0);
                if (!applicationInfo.packageName.equals(context.getPackageName())) {

                    if (context.getPackageManager().getLaunchIntentForPackage(applicationInfo.packageName) != null) {
                        if ((applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) == 1) {

                        } else if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 1) {
                            // không lấy các app hệ thống
                        } else {
                            // lấy các app cài đặt
                            // thêm phần tử AppInfo vào list , lấy các thuộc tính : packagename , path, tên ứng dựng , icon
                            listofgridview.add(applicationInfo);
                            listRunningApp.add(applicationInfo);
                        }
                    }
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

        }

        @Override
        protected void onPostExecute(Integer i) {
            super.onPostExecute(i);
            radarView.setVisibility(View.GONE);
            scanningProcess.setText(context.getString(R.string.complete));
            processLayout.setVisibility(View.VISIBLE);
            optimizeLayout.startAnimation(slideup);
            optimizeLayout.setVisibility(View.VISIBLE);
            adapter.notifyDataSetChanged();
            timeAdd.setText((new Str(context.getResources())).convertRemainingMinutes(adapter.getCount() * 7));
            timeAdd.startAnimation(fadein);
        }
    }




}
