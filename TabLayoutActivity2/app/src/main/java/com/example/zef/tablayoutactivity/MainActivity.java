package com.example.zef.tablayoutactivity;

import android.graphics.Color;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TabHost;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity implements TabHost.OnTabChangeListener{

    FragmentTabHost tabHost;
    public static int MIN_AMOUNT=1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//setting tab host
        tabHost = (FragmentTabHost) findViewById(R.id.tabhost);
        tabHost.setup(this, getSupportFragmentManager(), R.id.fragmenttabholder);

//Investment tab host
        tabHost.addTab(tabHost.newTabSpec("Investment").setIndicator("INVESTMENT"), Investment.class, null);
        TextView tI = (TextView) tabHost.getTabWidget().getChildAt(0).findViewById(android.R.id.title);
        tI.setTextSize(9);
        tI.setTextColor(Color.RED);

//Refund tab host
        tabHost.addTab(tabHost.newTabSpec("Refund").setIndicator("REFUND"), Refund.class, null);
        TextView  tR= (TextView) tabHost.getTabWidget().getChildAt(1).findViewById(android.R.id.title);
        tR.setTextSize(9);
        tR.setTextColor(Color.WHITE);

        tabHost.setOnTabChangedListener(this);
    }


//tab selection color change
    @Override
    public void onTabChanged(String tabId) {
        TextView tvInvestment =(TextView) tabHost.getTabWidget().getChildAt(0).findViewById(android.R.id.title);
        TextView tvRefund =(TextView) tabHost.getTabWidget().getChildAt(1).findViewById(android.R.id.title);
        boolean selected = "Investment".equals(tabId);

        tvInvestment.setTextColor(selected ? Color.RED: Color.WHITE);
        tvRefund.setTextColor(!selected ? Color.RED : Color.WHITE);

        }

//increase and decrease amount value on increase and decrease buttons clicks
    public void decreaseInteger(View view) {
        if (MIN_AMOUNT==1){
            MIN_AMOUNT=1;
        }
        else {
            MIN_AMOUNT = MIN_AMOUNT-1;
            display(MIN_AMOUNT);
        }
    }
     public void increaseInteger(View view) {
         MIN_AMOUNT=MIN_AMOUNT+1;
        display(MIN_AMOUNT);
    }
    private void display(int number){
        TextView displayAmount= (TextView) findViewById(R.id.textAmount);
        displayAmount.setText("$" + number);
    }
}
