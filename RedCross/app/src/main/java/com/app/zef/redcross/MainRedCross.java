package com.app.zef.redcross;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainRedCross extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_red_cross);

        Button exit = (Button)findViewById(R.id.exit);
        Button registerDonor = (Button)findViewById(R.id.registerDonor);

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                System.exit(0);
            }
        });


    }
    public void gotoActivity(View v){
        Intent intent=new Intent(this,Registration.class);
        startActivity(intent);

    }
}
