package com.example.nguyenduylong.pin.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nguyenduylong.pin.R;
import com.example.nguyenduylong.pin.progress.WaterWaveProgress;
import com.example.nguyenduylong.pin.service.BatteryInfoService;
import com.example.nguyenduylong.pin.util.Str;
import com.gc.materialdesign.views.ButtonFlat;
import com.vstechlab.easyfonts.EasyFonts;

/**
 * Created by nguyen duy long on 4/22/2016.
 */
public class ActivityAboutMe extends AppCompatActivity {
    Toolbar toolBar;
    ButtonFlat rateButt, likeButt,shareApkButt,shareLinkButt;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_me);
        toolBar = (Toolbar) findViewById(R.id.charge_tool_bar);
        rateButt = (ButtonFlat)findViewById(R.id.rate_butt);
        likeButt = (ButtonFlat)findViewById(R.id.like_butt);
        shareApkButt =(ButtonFlat)findViewById(R.id.share_butt);
        shareLinkButt = (ButtonFlat)findViewById(R.id.share_link_butt);
        ActivityAboutMe.this.setSupportActionBar(toolBar);
        toolBar.setNavigationIcon(R.drawable.sliding_menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((TextView)findViewById(R.id.sub_title)).setTypeface(EasyFonts.caviarDreamsItalic(this));
        rateButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(android.content.Intent.ACTION_VIEW);
                i.setData(Uri.parse("https://play.google.com/store/apps/details?id=gpaddy.gpaddyshare.filetransfer"));
                startActivity(i);
            }
        });
        likeButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(android.content.Intent.ACTION_VIEW);
                i.setData(Uri.parse("https://www.facebook.com/gpaddygroup/?fref=ts"));
                startActivity(i);
            }
        });
        shareApkButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(android.content.Intent.ACTION_VIEW);
                i.setData(Uri.parse("https://play.google.com/store/apps/details?id=gpaddy.gpaddyshare.filetransfer"));
                startActivity(i);
            }
        });
        shareLinkButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT,"https://play.google.com/store/apps/details?id=gpaddy.gpaddyshare.filetransfer" );
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
            }
        });
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause() {
        super.onPause();

    }


}
