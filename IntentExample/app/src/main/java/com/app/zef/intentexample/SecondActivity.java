package com.app.zef.intentexample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by zef on 2/2/16.
 */
public class SecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.secondactivity);
        final Intent intent=getIntent();
        final String message=intent.getStringExtra(Constants.Key_msg);
        TextView textView=(TextView)findViewById(R.id.textView2);
        textView.setText(message);

        Button b = (Button) findViewById(R.id.button2);

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i =new Intent();
                i.putExtra(Constants.Key_msg,"back from seconsActivity");
                setResult(RESULT_OK, i);
                finish();
            }
        });

    }


}
