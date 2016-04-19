package com.app.zef.intentexample;

import android.content.Intent;;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    final  int requestCodeSecondActivty =10;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final EditText editText1= (EditText)findViewById(R.id.editText1);
        Button button1= (Button)findViewById(R.id.button1);

        button1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String text = editText1.getText().toString();
                Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                intent.putExtra(Constants.Key_msg, text);
                startActivityForResult(intent, requestCodeSecondActivty);

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == requestCodeSecondActivty){
            String msg = data.getStringExtra(Constants.Key_msg);
            Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
        }

    }
}