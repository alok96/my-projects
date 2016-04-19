package biz.antworks.locationupdater;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;


public class DispatchDetailsActivity extends ActionBarActivity {

    String from, to, dispatchId, vNo;
    String status;

    Bundle bundleFromDisaptchActivity;
    private String TAG="DispatchDetailsActivity ";
    TextView dispatch_Id, vehicleNum,start_place, dest_place, status_value;
    TextView get_trip_time, start_trip_time, reached_time, finished_time ;
    DataBaseHandler dbh;
    Extras extras;
    JSONObject json=new JSONObject();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dispatch_details);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.rgb(192, 159, 26)));


        dbh=new DataBaseHandler(this);
        extras=new Extras(this);

        dispatch_Id=(TextView) findViewById(R.id.DispId);
        vehicleNum=(TextView) findViewById(R.id.vehicleNum);
        start_place=(TextView) findViewById(R.id.start_view);
        dest_place=(TextView) findViewById(R.id.dest_view);
        status_value=(TextView) findViewById(R.id.status_v);


        get_trip_time=(TextView) findViewById(R.id.get_trip_view);
        start_trip_time=(TextView) findViewById(R.id.start_trip_view);
        reached_time=(TextView) findViewById(R.id.reached_view);
        finished_time=(TextView) findViewById(R.id.finished_view);

        bundleFromDisaptchActivity = getIntent().getExtras();
        Log.i(TAG, "onCreate() GEtINTENT() " + getIntent().getPackage() + " \n " + getIntent().getExtras());
        if(getIntent().getExtras()!= null)
        {
            dispatchId=bundleFromDisaptchActivity.getString("dispId");
            /*from=bundleFromDisaptchActivity.getString("from");
            to=bundleFromDisaptchActivity.getString("to");
            status=bundleFromDisaptchActivity.getString("status");

            vNo=bundleFromDisaptchActivity.getString("vNo");*/
        }

        json=dbh.getDetailsFromDispatchId(dispatchId);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_dispatch_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if(id== R.id.home) {
            Intent i=new Intent(this,MainActivity.class);
            i.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP); //Intent.FLAG_ACTIVITY_CLEAR_TOP |
            finish();
            startActivity(i);
        }


        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onResume(){
        super.onResume();
        Log.i(TAG, "The onResume() event");
        try {
            display();
            //dispatch_Display();
        }catch(Exception e){
            Log.i(TAG," NO RECENT TRIPS Exception :"+e);
            Toast.makeText(this, " NO RECENT TRIPS", Toast.LENGTH_SHORT).show();
        }
    }


    private void display(){
        int count;
        try {
            if(json!=null) {
                start_place.setText(json.getString("start_place"));
                dest_place.setText(json.getString("destin_place"));
                status_value.setText(extras.mapStatus(json.getInt("status")));
                dispatch_Id.setText("DispatchId: " + json.getString("dispatch_id"));
                vehicleNum.setText(" VehicleNo:" + json.getString("vehicle_no"));


                String stat[] = dbh.getStatusTime(dispatchId);
                Log.i(TAG, "display() stat[].length= " + stat.length);

                get_trip_time.setText(stat[0]);
                start_trip_time.setText(stat[1]);
                reached_time.setText(stat[2]);
                finished_time.setText(stat[3]);
            }else{
                Log.i("","NO DIspatchID");
            }

        }catch(Exception e){
            e.printStackTrace();
        }
    }

   /* String dispatch_id;
    String device_id;
    String vehicle_no;
    String start_place;
    String start_lat;
    String start_lon;
    String destin_place;
    String dest_lat;
    String dest_lon;
    String reg_date_time;
    int statusVal;
*/



}
