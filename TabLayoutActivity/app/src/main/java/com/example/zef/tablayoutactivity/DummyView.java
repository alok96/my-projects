package com.example.zef.tablayoutactivity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.zef.iq.R;

/**
 * Created by zef on 5/4/16.
 */
public class DummyView extends LinearLayout {
    TextView tv;
    public DummyView(Context context) {
        super(context);
        View root = LayoutInflater.from(context).inflate(R.layout.dummy_layout, this);
        tv = (TextView) root.findViewById(R.id.text);
    }

    public void setText(String text){
        tv.setText(text);
    }
}
