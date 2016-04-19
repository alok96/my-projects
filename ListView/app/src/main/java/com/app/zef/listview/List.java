package com.app.zef.listview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

public class List extends AppCompatActivity {
    private ArrayAdapter<String>adapter;
    private ArrayList<String>arrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        final EditText editText=(EditText)findViewById(R.id.text1);
        Button btn=(Button)findViewById(R.id.add);
        ListView list=(ListView)findViewById(R.id.listView);

        arrayList=new ArrayList<String>();
        adapter=new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_spinner_item, arrayList);
        list.setAdapter(adapter);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                arrayList.add(editText.getText().toString());
                editText.setText("");
                adapter.notifyDataSetChanged();
            }
        });
    }
}
