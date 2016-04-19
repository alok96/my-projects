package biz.antworks.locationupdater;


import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class DispatchActivity extends ActionBarActivity {


    static String[] date=new String[4] ;
    static String[] start=new String[4] ;
    static String[] dest=new String[4] ;
    static String[] status=new String[4];
    static String[] disp=new String[4] ;
    static String[] vNo=new String[4];

    ActionBar actionBar;
    private Spinner spinner1, spinner2;
    SQLiteDatabase db;
    static String spin1, spin2, selected_start, selected_dest,selected_status, selected_vNo, selected_dispId;
    ListView list;
    Button okButton;
    DataBaseHandler dbh;
    CustomList adapter;
    ImageView image;
    Intent i;
    String TAG="DispatchActivity";
    Extras extras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dispatch_activity);

        extras=new Extras(this);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.rgb(192,159,26)));

        dbh = new DataBaseHandler(this.getApplicationContext());
        list=(ListView)findViewById(R.id.list);

        image=(ImageView) findViewById(R.id.imageView1);
         //i=getIntent();
        dispatch_Display();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.dispatch_menu, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("MAINACTIVITY", "The onResume() event");
        try {
            dispatch_Display();
        }catch(Exception e){
            Log.i("MAIN "," NO RECENT TRIPS Exception :"+e);
            Toast.makeText(this," NO RECENT TRIPS",Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();
        switch (id) {
            /*case R.id.New:

                // create a Dialog component

                Context context = this;
                final Dialog new_trip = new Dialog(context);

                //tell the Dialog to use the new_trip.xml as it's layout description

                new_trip.setContentView(R.layout.new_trip);
                new_trip.setTitle("Select places");

                spinner1 = (Spinner) new_trip.findViewById(R.id.spinner_start);
                spinner2 = (Spinner) new_trip.findViewById(R.id.spinner_end);


                okButton = (Button) new_trip.findViewById(R.id.ok_Button);
                Button cancelButton = (Button) new_trip.findViewById(R.id.cancel_Button);
                if(selected_start!=null && selected_dest!=null) {
                    spinner1.setSelection(getIndex(spinner1, selected_start));
                    spinner2.setSelection(getIndex(spinner2, selected_dest));
                }

                okButton.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        spin1 = spinner1.getSelectedItem().toString();
                        spin2 = spinner2.getSelectedItem().toString();
                        Toast.makeText(DispatchActivity.this, "Result : " +
                                        "\nFrom " + spin1 +
                                        "\n to " + spin2,
                                Toast.LENGTH_SHORT).show();

                        dbh.addTrip();
                        dispatch_Display();
                        new_trip.dismiss();
                    }


                });

                cancelButton.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getApplicationContext(), "cancel button on dialogue", Toast.LENGTH_SHORT).show();
                        new_trip.dismiss();
                    }
                });
                new_trip.show();
                break;*/

            case R.id.home:
                    finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    private int getIndex(Spinner spinner, String myString)
    {
        int index = 0;

        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)){
                index = i;
                break;
            }
        }
        return index;
    }


    void  dispatch_Display() {
        try {
            dbh.getLast4Dispatches();
            if(dbh!=null) {
                date = dbh.dateNtime;
                start = dbh.start;
                dest = dbh.dest;

               // status=dbh.status;
                for(int i=0;i<dbh.status.length;i++)
                    status[i]=extras.mapStatus(dbh.status[i]);
                disp=dbh.disp;
                vNo=dbh.vNo;
                adapter = new CustomList(DispatchActivity.this, date, start, dest , status);
                list.setAdapter(adapter);

                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Toast.makeText(DispatchActivity.this, "You Clicked on " + start[+position] + " " + dest[+position] +
                                " " + +position, Toast.LENGTH_SHORT).show();

                        for (int a = 0; a < parent.getChildCount(); a++) {
                            parent.getChildAt(a).setBackgroundColor(Color.WHITE);
                        }

                        view.setBackgroundColor(Color.GRAY);
                        selected_start = start[position];
                        selected_dest = dest[position];
                        selected_status=status[position];
                        selected_dispId=disp[position];
                        selected_vNo=vNo[position];
                        Log.i(TAG, "Values selcted "+position+" : "+selected_status+" \n"+selected_dispId+"\n "+selected_vNo);

                        Intent i=new Intent(DispatchActivity.this, DispatchDetailsActivity.class);
                       /* i.putExtra("from",selected_start);
                        i.putExtra("to", selected_dest);
                        i.putExtra("status",selected_status);
                        i.putExtra("vNo", selected_vNo);*/
                        i.putExtra("dispId",selected_dispId);
                       // finish();
                        startActivity(i);
                       // Log.i("Dispatch ", " SELECTED start and dest: " + selected_start + " and " + selected_dest);

                        //Implement google maps code here  for selected Dispatches

                    }
                });
            }else {
                Log.i(TAG,"No Dispatches Created");
                Toast.makeText(this, "No Dispatches Created",Toast.LENGTH_SHORT).show();
            }
        }catch(Exception e) {
        e.printStackTrace();
        }


    }


}