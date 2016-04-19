package com.app.zef.imageview;

import android.nfc.Tag;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.EventLogTags;
import android.util.Log;

public class MainActivity extends AppCompatActivity {
    public final static String LOGTAG="MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(LOGTAG,"oncreate");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(LOGTAG,"onResume");
    }
}
