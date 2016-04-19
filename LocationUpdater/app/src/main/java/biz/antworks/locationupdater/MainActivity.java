package biz.antworks.locationupdater;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.ActionBarActivity;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.google.android.gms.common.api.Api;
import com.google.android.gms.location.LocationRequest;
import com.google.api.client.json.Json;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import biz.antworks.locationupdater.LocationUpdateService;

/**
 * Getting Location Updates.
 *
 * Demonstrates how to use the Fused Location Provider API to get updates about a device's
 * location. The Fused Location Provider is part of the Google Play services location APIs.
 *
 * For a simpler example that shows the use of Google Play services to fetch the last known location
 * of a device, see
 * https://github.com/googlesamples/android-play-location/tree/master/BasicLocation.
 *
 * This sample uses Google Play services, but it does not require authentication. For a sample that
 * uses Google Play services for authentication, see
 * https://github.com/googlesamples/android-google-accounts/tree/master/QuickStart.
 */
public class MainActivity extends ActionBarActivity {


    Button ok, cancel, get_trip_btn,start_trip_btn, reached_btn ,finished_btn;
    TextView et1;
    EditText cId, uId,pwd;
    Button cred_Ok, cred_cancel;
    EditText et3;
    EditText et4;
    String s1,s2,s3,s4;
    Dialog reg , confirm, credentials;
    TextView lastReg, vehicle_no;
    TextView address;
    TextView tvConfirm;
    Button confirmBtn, cancelBtn;
    TextView subId;
    TextView tv1,tv2,tv3,tv4,tv5,tv6,tv7;
    int statusValue;
    int priority=-17;
    ImageView image,t1,t2,t3,t4,f1,f2,f3,f4, option1;

    JSONObject jsonResponse= new JSONObject();
    String startPlace, destPlace ;
    int status;


    int[] get;
    int[] start;
    Resources reach;
    Resources finish;//start_trip_btn.getResources();


    /*LocationsRequesterService lrs;
    LocationsDBHandler ldbh;*/
    MyReceiver myreceiver=new MyReceiver(this);
    Extras extras=new Extras(this);
    DataBaseHandler dbh;
    private Timer timer = new Timer();
    private MyTimerTask myTimerTask;
    StoreLocationsDataBase storeLocationsDataBase;



    private int counter = 0, incrementby = 1;

    protected static final String TAG = "MainActivity";

    // Keys for storing activity state in the Bundle.
    protected final static String REQUESTING_LOCATION_UPDATES_KEY = "requesting-location-updates-key";
    protected final static String LOCATION_KEY = "location-key";
    protected final static String LAST_UPDATED_TIME_STRING_KEY = "last-updated-time-string-key";

    /**
     * Stores parameters for requests to the FusedLocationProviderApi.
     */
    protected LocationRequest mLocationRequest;

    /**
     * Represents a geographical location.
     */
    protected Location mCurrentLocation;

    // UI Widgets.
    protected Button mStartUpdatesButton;
    protected Button mStopUpdatesButton;
    protected TextView mLastUpdateTimeTextView;
    protected TextView mLatitudeTextView;
    protected TextView mLongitudeTextView;
    protected TextView textStatus;

    /**
     * Tracks the status of the location updates request. Value changes when the user presses the
     * Start Updates and Stop Updates buttons.
     */
    protected Boolean mRequestingLocationUpdates;

    protected String appURL;
    protected String appID;
    protected String appUser;
    protected String appUserID;
    protected String appDefaultMessage;
    protected String mSubscriberId;
    private String dispatchId;
    private String mVehicleNo;
    private int statusValues;
    Intent newIntent;

    Bundle instanceState=new Bundle();
    /**
     * Time when the location was updated represented as a String.
     */
    protected String mLastUpdateTime;

    LocationUpdateService myService = null;
    boolean mIsBound;

    private SharedPreferences prefs;
    private RequestHelper requestHelper;
    RequestHelper reqHelp;


    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            LocationUpdateService.MyLocalBinder binder = (LocationUpdateService.MyLocalBinder) service;
            myService = binder.getService();
            Log.i(TAG, " service COnnected ");//1
            if (myService != null) {
                Log.i(TAG, " service COnnected myservice!=null");//2
                myService=callReqHelp(myService);
            }
            mIsBound = true;
        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been unexpectedly disconnected - process crashed.
            myService = null;
            Log.i("MainActivity ", " service DisCOnnected "); //3
            textStatus.setText("Disconnected.");
        }
    };

    protected LocationUpdateService callReqHelp(LocationUpdateService locationUpdateService){
        locationUpdateService.getRequestHelper().setAppMetaData(appURL, appID, appUser, appUserID, appDefaultMessage);
       // locationUpdateService.getRequestHelper().setSubscriberID(mSubscriberId);
        Log.i(TAG, " callreqHElp ");//4
        return locationUpdateService;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        instanceState =savedInstanceState;
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.rgb(192, 159, 26)));

        android.os.Process.setThreadPriority(priority);

        // Locate the UI widgets.
        //mStartUpdatesButton = (Button) findViewById(R.id.start_updates_button);
        // mStopUpdatesButton = (Button) findViewById(R.id.stop_updates_button);
        ///mLatitudeTextView = (TextView) findViewById(R.id.latitude_text);
        //mLongitudeTextView = (TextView) findViewById(R.id.longitude_text);
        //mLastUpdateTimeTextView = (TextView) findViewById(R.id.last_update_time_text);
        address=(TextView) findViewById(R.id.address);
        //textStatus = (TextView) findViewById(R.id.textStatus);
        //subId=(TextView) findViewById(R.id.subId);



        //tv1=(TextView) findViewById(R.id.Trip_Details);

        tv2=(TextView) findViewById(R.id.start_place_label);
        tv3=(TextView) findViewById(R.id.start_place_view);

        tv4=(TextView) findViewById(R.id.dest_place_label);

        tv5=(TextView) findViewById(R.id.dest_place_view);

        tv6=(TextView) findViewById(R.id.status_label);
        tv7=(TextView) findViewById(R.id.status_view);


        get_trip_btn= (Button) findViewById(R.id.get_trip);
        get=get_trip_btn.getDrawableState();

        start_trip_btn= (Button) findViewById(R.id.start_trip);
        start=start_trip_btn.getDrawableState();

        reached_btn= (Button) findViewById(R.id.reached);
        finished_btn= (Button) findViewById(R.id.finished);

        mRequestingLocationUpdates = false;
        mLastUpdateTime = "";
        dispatchId = "";


        image=(ImageView) findViewById(R.id.imageView1);
        lastReg=(TextView) findViewById(R.id.lastReg);
        lastReg.setSelected(true);
        lastReg.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        lastReg.setSingleLine(true);

        vehicle_no=(TextView) findViewById(R.id.vehicle);
        vehicle_no.setSelected(true);
        vehicle_no.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        vehicle_no.setSingleLine(true);

        // Update values using data stored in the Bundle.
        updateValuesFromBundle(savedInstanceState);
//        myService.updateValuesFromBundle(savedInstanceState);

        this.prefs = PreferenceManager.getDefaultSharedPreferences(this);
        requestHelper = new RequestHelper(this.prefs, "Register", "GetDispatchAdvice",this.getApplicationContext());
        requestHelper.setDispatchId(dispatchId);
        mRequestingLocationUpdates = false;
        mLastUpdateTime = "";
        requestHelper=appData(requestHelper);
        reqHelp=new RequestHelper(this.prefs,"updateStatus","GetNSetDispatchStatus",this.getApplicationContext());
        reqHelp=appData(reqHelp);


        int tid=android.os.Process.myTid();

        Log.d(TAG,"priority before change = " + android.os.Process.getThreadPriority(tid));
        Log.d(TAG,"priority before change = "+Thread.currentThread().getPriority());
        Thread.currentThread().setPriority(8);




        //checkInternetState();

        LocationUpdateService.started = true;

        dbh=new DataBaseHandler(getApplicationContext());
      //  storeLocationsDataBase=new StoreLocationsDataBase(getApplicationContext());
        //extras.turnGPSOn();


        //display();
    }

    protected RequestHelper appData(RequestHelper requestHelper){
        try {
            ApplicationInfo ai = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            appURL = bundle.getString("app_url");
            appID = bundle.getString("app_id");
            appUserID = bundle.getString("app_user_id");
            appUser = bundle.getString("app_user");
            appDefaultMessage = bundle.getString("app_default_message");
            requestHelper.setAppMetaData(appURL, appID, appUser, appUserID, appDefaultMessage);
            TelephonyManager tel = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            mSubscriberId = tel.getSubscriberId().toString();
            //Toast.makeText(this," device ID : "+ mSubscriberId,Toast.LENGTH_LONG).show();
            //Toast.makeText(this," New Trip needs to be Assigned ",Toast.LENGTH_LONG).show();


            Log.i(TAG, " appDATA() ");//5
            //requestHelper.setSubscriberID(mSubscriberId);
            //Toast.makeText(this," device ID : "+ mSubscriberId,Toast.LENGTH_LONG).show();

        } catch (PackageManager.NameNotFoundException e) {
            Log.e(Constants.TAG, "Failed to load meta-data, NameNotFound: " + e.getMessage());
        } catch (NullPointerException e) {
            Log.e(Constants.TAG, "Failed to load meta-data, NullPointer: " + e.getMessage());
        }
        return requestHelper;
    }



    /**
     * Updates fields based on data stored in the bundle.
     *
     * @param savedInstanceState The activity state saved in the Bundle.
     */
    private void updateValuesFromBundle(Bundle savedInstanceState) {
        Log.i(TAG, "Updating values from bundle");
        if (savedInstanceState != null) {
            // Update the value of mRequestingLocationUpdates from the Bundle, and make sure that
            // the Start Updates and Stop Updates buttons are correctly enabled or disabled.
            if (savedInstanceState.keySet().contains(REQUESTING_LOCATION_UPDATES_KEY)) {
                mRequestingLocationUpdates = savedInstanceState.getBoolean(
                        REQUESTING_LOCATION_UPDATES_KEY);

            }

            // Update the value of mCurrentLocation from the Bundle and update the UI to show the
            // correct latitude and longitude.
            if (savedInstanceState.keySet().contains(LOCATION_KEY)) {
                // Since LOCATION_KEY was found in the Bundle, we can be sure that mCurrentLocation
                // is not null.
                mCurrentLocation = savedInstanceState.getParcelable(LOCATION_KEY);
            }

            // Update the value of mLastUpdateTime from the Bundle and update the UI.
            if (savedInstanceState.keySet().contains(LAST_UPDATED_TIME_STRING_KEY)) {
                mLastUpdateTime = savedInstanceState.getString(LAST_UPDATED_TIME_STRING_KEY);
            }
            updateUI();

        }
    }


    private void getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        Log.i(TAG,"get Date Time : "+dateFormat.format(date));
        //return dateFormat.format(date);
    }
    /*  protected void checkInternetState()
      {
          SharedPreferences pref=getSharedPreferences("isMyServiceRunning",0);

          if(extras.checkInternetConnection(getApplicationContext()) && extras.checkGps(getApplicationContext()) ) {
              if(LocationUpdateService.started = extras.isMyServiceRunning(LocationUpdateService.class)){
                  Toast.makeText(MainActivity.this, "Service running", Toast.LENGTH_SHORT).show();

                 SharedPreferences.Editor editor=pref.edit();
                  editor.putBoolean("started",true);
                  editor.commit();

              }
              else {
                  SharedPreferences.Editor editor=pref.edit();
                  editor.putBoolean("started",false);
                  editor.commit();
                  Toast.makeText(MainActivity.this, "Service isn't running", Toast.LENGTH_SHORT).show();
              }
          }
          else {
              Log.i(TAG, "NO NET CONNECTION OR Check GPS");
             // Toast.makeText(MainActivity.this, "NO NET CONNECTION OR Check GPS  and try again", Toast.LENGTH_SHORT).show();


          }

      }
  */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();
        switch (id) {

            case R.id.userCredentials:
                Context context = MainActivity.this;
                credentials = new Dialog(context);

                //tell the Dialog to use the login.xml as it's layout description
                try {
                    credentials.setContentView(R.layout.login);
                    TextView mTitle = (TextView) credentials.findViewById(android.R.id.title);
                    mTitle.setTextColor(Color.BLACK);
                    mTitle.setTypeface(null, Typeface.BOLD);
                    mTitle.setTextSize(20);
                    credentials.setTitle("Please Enter Credentials ");

                    cId = (EditText) credentials.findViewById(R.id.clientIDValue);
                    uId = (EditText) credentials.findViewById(R.id.usernameValue);
                    pwd = (EditText) credentials.findViewById(R.id.passwordValue);
                    cred_Ok = (Button) credentials.findViewById(R.id.cred_Ok);
                    cred_cancel = (Button) credentials.findViewById(R.id.cred_cancel);
                    try {
                        JSONObject js = dbh.getCredentials();
                        Log.i(TAG, "-----JSon -------   JS: " + js);
                        if (js.length()!=0) {

                            cId.setText(js.getString("cId"));
                            //cId.setEnabled(false);
                            uId.setText(js.getString("uId"));
                            // uId.setEnabled(false);
                            pwd.setText(js.getString("pwd"));
                            // pwd.setEnabled(false);
                            Log.i(TAG, "\t ----------- cId : " + cId + "\n uId : " + uId + "\n pwd :" + pwd);
                        }

                    }catch(Exception e){
                        e.printStackTrace();
                    }
                    cred_Ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            String clientId = String.valueOf(cId.getText());
                            String username = String.valueOf(uId.getText());
                            String password = String.valueOf(pwd.getText());
                            dbh.insertCredentialsTable(clientId, username, password);
                            Toast.makeText(MainActivity.this,"credentials saved \n Id : "+clientId+"\n username : "+username+"\n password :"+password,Toast.LENGTH_LONG).show();
                            /*JSONObject json = dbh.getCredentials(clientId);
                            if (json != null) {
                                try {
                                    if(json.getString("cId").equals(clientId)){
                                        if(json.getString("uId").equals(username)){
                                            if(json.getString("pwd").equals(password)) {
                                                //valid clientID, userId and Password
                                                Toast.makeText(getBaseContext(), "valid credentials", Toast.LENGTH_SHORT).show();


                                            }else
                                                Toast.makeText(getBaseContext(),"UserId or Password did not match",Toast.LENGTH_SHORT).show();

                                        }else
                                            Toast.makeText(getBaseContext(),"Invalid UserId or password",Toast.LENGTH_SHORT).show();

                                    } else
                                        Toast.makeText(getBaseContext(),"Invalid CLientId",Toast.LENGTH_SHORT).show();



                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }else{
                                dbh.insertCredentialsTable(clientId, username, password);
                            }*/


                            credentials.dismiss();
                            //blah blah blah
                            //credentials.dismiss();

                        }


                    });
                    //credentials.dismiss();

                }catch(Exception e){
                    Log.i(TAG,"Exception in settings dialog : "+e);
                    e.printStackTrace();
                }

                cred_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        credentials.dismiss();
                    }
                });

                credentials.show();
                break;

           /* case R.id.option1:
                Toast.makeText(MainActivity.this,"Select Other Options " , Toast.LENGTH_SHORT).show();
                break;*/
/*

            case R.id.option2:
                display();
                updateUI();
                Toast.makeText(MainActivity.this," Refreshed and Last RegNo Updated " , Toast.LENGTH_SHORT).show();
                break;
*/

            /*case R.id.regDevice:
                // create a Dialog component

               // getTripDialog();
                break;*/

            case R.id.dispatch:
                Toast.makeText(MainActivity.this," Dispatch Selected " , Toast.LENGTH_SHORT).show();
                Intent i=new Intent(this, DispatchActivity.class);
                startActivity(i);
                break;
        }

        return super.onOptionsItemSelected(item);

    }

    public void updateDispatch(String id) {
        if(id!=null) {
            dispatchId = id;
            lastReg.setText("Dispatch: " + dispatchId);
//        dbh.doRegister(mSubscriberId, id);
            myService.setDispatchId(id);
            display();
        }else{
            Toast.makeText(this,"Response from server ="+id,Toast.LENGTH_SHORT).show();
        }
    }

/*
    private void editTxt(){

        //To move cursor to next edit_text(et) exactly after giving input characters of specified nos.

        et1.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                et1.setTextColor(Color.rgb(1,1,6));
                Integer tl1 = et1.getText().length();

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
                s1=s.toString();
                if( (s1.length()==0)||(s1.length()==1)){
                    // Toast.makeText(MainActivity.this," Fields can not be empty ",Toast.LENGTH_SHORT).show();
                    f1.setVisibility(View.VISIBLE);
                    t1.setVisibility(View.INVISIBLE);
                }
                else if( isAlpha(s1)) {
                    // Toast.makeText(MainActivity.this," InCorrect Entry : " +s1,Toast.LENGTH_SHORT).show();
                    f1.setVisibility(View.VISIBLE);
                    t1.setVisibility(View.INVISIBLE);
                }
                else{
                    f1.setVisibility(View.INVISIBLE);
                    t1.setVisibility(View.VISIBLE);
                    et2.requestFocus();
                    if((et2.getText().toString()).equals("03"))
                        et2.setText("");
                }


            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub


            }
        });



        et2.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                et2.setTextColor(Color.rgb(1,1,6));

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
                s2=s.toString();
                if( (s2.length()==0)||(s2.length()==1)){
                    // Toast.makeText(MainActivity.this," Fields can not be empty ",Toast.LENGTH_SHORT).show();
                    f2.setVisibility(View.VISIBLE);
                    t2.setVisibility(View.INVISIBLE);
                }
                else{
                    f2.setVisibility(View.INVISIBLE);
                    t2.setVisibility(View.VISIBLE);
                    et3.requestFocus();
                    if((et3.getText().toString()).equals("US"))
                        et3.setText("");


                }

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub
            }
        });


        et3.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                et3.setTextColor(Color.rgb(1,1,6));


            }

            @Override
            public void afterTextChanged(Editable s) {
                s3=s.toString();
                if( s3.length()==0){
                    f3.setVisibility(View.VISIBLE);
                    t3.setVisibility(View.INVISIBLE);
                }
                else if( isAlpha(s3)) {
                    //Toast.makeText(MainActivity.this," InCorrect Entry : " +s3,Toast.LENGTH_SHORT).show();
                    f3.setVisibility(View.VISIBLE);
                    t3.setVisibility(View.INVISIBLE);
                }
                else{
                    f3.setVisibility(View.INVISIBLE);
                    t3.setVisibility(View.VISIBLE);
                    if(s3.length()==2) {
                        et4.requestFocus();
                        if((et4.getText().toString()).equals("0101")) {
                            et4.setText("");
                        }
                    }
                }

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub
            }
        });

        et4.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                et4.setTextColor(Color.rgb(1,1,6));

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
                s4 = s.toString();
                if (s4.length()==0){
                    //Toast.makeText(MainActivity.this," Fields can not be empty ",Toast.LENGTH_SHORT).show();
                    f4.setVisibility(View.VISIBLE);
                    t4.setVisibility(View.INVISIBLE);
                }
                else{
                    f4.setVisibility(View.INVISIBLE);
                    t4.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub
            }
        });


    }
*/


   /* private void validated(){

        String s1=et1.getText().toString();
        String s2=et2.getText().toString();
        String s3=et3.getText().toString();
        String s4=et4.getText().toString();

        if( ( s1.equals("KA"))&&(s2.equals("03"))&&( s3.equals("US"))&&( s4.equals("001")) ){
            Toast.makeText(MainActivity.this,"You have not entered any REG NO , TRY AGAIN " ,Toast.LENGTH_SHORT).show();

        }
        else if( ( s1.length()==0) ){ //||(s2.length()==0)||( s3.length()==0)||( s4.length()==0)

            Toast.makeText(MainActivity.this,"Device Id Cannot be EMPTY " ,Toast.LENGTH_SHORT).show();
            //Toast.makeText(MainActivity.this,"Please Enter Correct Vehicle Number " ,Toast.LENGTH_SHORT).show();

        }
        else if( ( isAlpha(s1) )  ||  ( isAlpha(s3) ) ){
            Toast.makeText(MainActivity.this," REG value you entered seems incorrect : "+ et1.getText()+ " "+et3.getText()  ,Toast.LENGTH_LONG).show();

        }
        else {
            mVehicleNumber= s1 + " " + s2 + " " + s3 + " " + s4;
            Toast.makeText(MainActivity.this, "REG value : " + et1.getText(), Toast.LENGTH_SHORT).show();
            getDate();
            TelephonyManager tel = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            mSubscriberId = String.valueOf(et1.getText());//tel.getSubscriberId();
            Toast.makeText(MainActivity.this, " mSubscriptionId : " + mSubscriberId, Toast.LENGTH_SHORT).show();
            reg.dismiss();
        }

   }*/


    public boolean isAlpha(String name) {
        char[] chars = name.toCharArray();

        for (char c : chars) {
            if(!Character.isLetter(c)) {
                return true;
            }
        }
        // s1.matches("[0-9]*");2
        return false;
    }

    private String getDate()
    {
        SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        Date date=new Date();

        return sdf.format(date);
    }


    void  display() {

        try {
            dbh.getDispatchData();
            if(dbh.dispatch_id!=null  ) {
           // if(dbh!=null) {
                dispatchId = dbh.dispatch_id;
                mVehicleNo = dbh.vehicle_no;
                startPlace = dbh.start_place;
                destPlace = dbh.destin_place;
                status = dbh.statusVal;
           // }

              /* if(dbh.statusVal!=1 || dbh.statusVal!=4 || dbh.statusVal!=6 || dbh.statusVal!=8){
                    setStatusValue(1);
                }*/

                if (dbh.statusVal==1) {
                    get_trip_btn.setEnabled(false);
                    get_trip_btn.setBackgroundResource(android.R.drawable.btn_default);
                    /*get_trip_btn.setTextColor(Color.GRAY);*/

                     startLocationUpdates();

                    start_trip_btn.setEnabled(true);
                    start_trip_btn.setTextColor(Color.BLACK);
                    //start_trip_btn.setAllCaps(true);
                    start_trip_btn.setBackgroundResource(R.drawable.btn_style);
                    //start_trip_btn.setBackgroundColor(0xf709af1a);
                }
                if (dbh.statusVal==4){
                    reached_btn.setEnabled(true);
                    reached_btn.setTextColor(Color.BLACK);

                        startLocationUpdates();
                    //reached_btn.setAllCaps(true);
                    reached_btn.setBackgroundResource(R.drawable.btn_style); //setBackgroundColor(Color.GREEN);
                }

                if (dbh.statusVal==6){
                    finished_btn.setEnabled(true);
                    finished_btn.setTextColor(Color.BLACK);

                        startLocationUpdates();
                    //finished_btn.setAllCaps(true);
                    finished_btn.setBackgroundResource(R.drawable.btn_style); //setBackgroundColor(0xf709af1a);
                }

                if (dbh.statusVal==8){
                    get_trip_btn.setEnabled(true);
                    get_trip_btn.setTextColor(Color.BLACK);
                   // get_trip_btn.setAllCaps(true);
                    get_trip_btn.setBackgroundResource(R.drawable.btn_style); //.setBackgroundColor(0xf709af1a);
                }
            }
            else {
                Toast.makeText(this, "Please Get a Trip",Toast.LENGTH_SHORT).show();
                get_trip_btn.setEnabled(true);
                get_trip_btn.setTextColor(Color.BLACK);
                //get_trip_btn.setAllCaps(true);
                get_trip_btn.setBackgroundResource(R.drawable.btn_style); //
                // .setBackgroundColor(0xf709af1a);
            }





            Log.i(TAG,"display(), DBh values"+dispatchId+" "+mVehicleNo+" "+startPlace+" "+destPlace+" "+status);

            lastReg.setText("Dispatch: " + dispatchId);
            vehicle_no.setText(" Vehicle No: " + mVehicleNo);
            tv3.setText(startPlace);
            tv5.setText(destPlace);
            tv7.setText(extras.mapStatus(status));
        }
        catch(Exception e){
            e.printStackTrace();
        }

    }

    /**
     * Handles the Start Updates button and requests start of location updates. Does nothing if
     * updates have already been requested.


     /**
     * Handles the Stop Updates button, and requests removal of location updates. Does nothing if
     * updates were not previously requested.
     */
    /**
     * Requests location updates from the FusedLocationApi.
     */

    /**
     * Ensures that only one button is enabled at any time. The Start Updates button is enabled
     * if the user is not requesting location updates. The Stop Updates button is enabled if the
     * user is requesting location updates.
     */

    /**
     * Updates the latitude, the longitude, and the last location time in the UI.
     */


    /**
     * Removes location updates from the FusedLocationApi.
     */
    protected void stopLocationUpdates() {
        doUnbindService();
    }

    @Override
    protected void onStart() {
        super.onStart();


      /*Intent myIntent = new Intent(this,LocationsRequesterService.class);
            startService(myIntent);*/
        //new Intent(this, LocationUpdateService.class);

        doBindService();

    }


    void doBindService() {

        bindService(new Intent(this, LocationUpdateService.class), mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;


    }


    @Override
    public void onResume() {
        super.onResume();

        Log.i("MAINACTIVITY", "The onResume() event");
        try {
            myTimerTask = new MyTimerTask();
            timer.schedule(myTimerTask, 10000, 20000);
             display();
            get_trip_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getTripDialog();

                }

            });

          //  if(bundleFromDisaptchActivity!=null)

           // display();
            start_trip_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getConfirmDialog(4);


                }
            });


            reached_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getConfirmDialog(6);

                }
            });

            finished_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getConfirmDialog(8);

                }
            });
        }catch(Exception e){

            Log.i("MAIN "," NO Last Vehicle REG_NO Exception :"+e);
            Toast.makeText(this," NO Vehicle's Registered",Toast.LENGTH_SHORT).show();
        }
    }


    protected void startLocationUpdates() {
        //   doBindService();
       /* dbh.getDispatchData();
        if( dbh.dispatch_id!=null) {*/
        Log.i(TAG, " #######################newIntent value : "+newIntent);
      if(newIntent==null) {
            newIntent = new Intent(this, LocationUpdateService.class);
            startService(newIntent);
      }
        Log.i(TAG, " ######################newIntent value : "+newIntent);
        //}
    }


    private void getTripDialog() {

        final MainActivity mainActivity = this;
        Context context = MainActivity.this;

        reg = new Dialog(context);

        //tell the Dialog to use the register.xml as it's layout description

        reg.setContentView(R.layout.register);
        TextView mTitle = (TextView) reg.findViewById(android.R.id.title);
        mTitle.setTextColor(Color.BLACK);
        mTitle.setTypeface(null, Typeface.BOLD);
        mTitle.setTextSize(20);
        reg.setTitle("Please confirm your device no");
        et1 = (TextView) reg.findViewById(R.id.deviceNo);
        ok = (Button) reg.findViewById(R.id.Register_Button);
        cancel = (Button) reg.findViewById(R.id.cancel);
        TextView ph=(TextView) reg.findViewById(R.id.Text);
        TelephonyManager tMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String mPhoneNumber = tMgr.getLine1Number();
        Log.i(TAG,"PHONE NUMBER : " +mPhoneNumber);
        ph.setText(mPhoneNumber);
        TelephonyManager tel = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        mSubscriberId = tel.getSubscriberId().toString();
        et1.setText(mSubscriberId);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                reg.dismiss();
                requestHelper.setDeviceUniqueId(mSubscriberId);
                ApiCallHelper ach = new ApiCallHelper();
                dispatchId = ach.doExecute(mainActivity, requestHelper, mSubscriberId);
                if(dispatchId!=null){

                    setStatusValue(1);
                   // startLocationUpdates();

                } else {
                    Toast.makeText(MainActivity.this, "Couldn't connect to server, please try after sometime", Toast.LENGTH_SHORT).show();
                }
                reg.dismiss();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                reg.dismiss();
            }
        });

        reg.show();

    }


    private void getConfirmDialog(int stv) {
        statusValues=stv;
            confirm = new Dialog(this);
            confirm.setContentView(R.layout.confirm);
        TextView mTitle1 = (TextView) confirm.findViewById(android.R.id.title);
        mTitle1.setTextColor(Color.BLACK);
        mTitle1.setTypeface(null, Typeface.BOLD);
            confirm.setTitle("Please Confirm");
            tvConfirm = (TextView) confirm.findViewById(R.id.confirm);
            confirmBtn = (Button) confirm.findViewById(R.id.confirm_btn);
            cancelBtn = (Button) confirm.findViewById(R.id.cancel_btn);
        String stval="";
        if(statusValues==4)
             stval="started";
        if(statusValues==6)
            stval="reached";
        if(statusValues==8)
            stval="finished";

            tvConfirm.setText("Are you sure you want to change this Trip status to \" "+ stval +"\"");
            cancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    confirm.dismiss();
                }
            });


        confirmBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    ApiCallHelper aCH = new ApiCallHelper();
                    aCH.doExecute(prefs,reqHelp,dispatchId,extras.mapStatusToServer(statusValues));


                  setStatusValue(statusValues);

                    confirm.dismiss();
                    if(statusValues==8){
                        Intent i=new Intent(MainActivity.this, DispatchDetailsActivity.class);
                        i.putExtra("dispId",dispatchId);
                        finish();
                        startActivity(i);
                    }

                }
            });
            confirm.show();

    }

    private void setStatusValue(int statusValues) {
        try {

            switch (statusValues) {
                case 1: {
                    dbh.updateStatusToDb(statusValues);
                    get_trip_btn.setEnabled(false);
                    get_trip_btn.setBackgroundResource(android.R.drawable.btn_default);
                    //get_trip_btn.setTextColor(Color.GRAY);
                    //get_trip_btn.setBackgroundResource(R.drawable.btn_style);


                   /* tv7.setText(statusValue);
                    start_trip_btn.setEnabled(true);
                    start_trip_btn.setTextColor(Color.BLACK);
                    start_trip_btn.setBackgroundResource(R.drawable.btn_style);//setBackgroundColor(0xf709af1a);*/
                  // display();
                }
                    break;

                case 4:// statusValue = 4;
                    dbh.updateStatusToDb(statusValues);
                    dbh.getDispatchData();
                    dbh.insertStatusTable(dbh.dispatch_id,statusValues);
                    start_trip_btn.setEnabled(false);
                    start_trip_btn.setBackgroundResource(android.R.drawable.btn_default);
                    //start_trip_btn.setBackgroundResource(R.drawable.btn_style);
                   /* reached_btn.setEnabled(true);
                    reached_btn.setTextColor(Color.BLACK);
                    reached_btn.setBackgroundResource(R.drawable.btn_style);   //.setBackgroundColor(0xf709af1a);
                    tv7.setText(statusValue);*/
                    display();
                    //confirm.dismiss();
                    break;

                case 6:
                    //statusValue = 6;
                    dbh.updateStatusToDb(statusValues);
                    dbh.getDispatchData();
                    dbh.insertStatusTable(dbh.dispatch_id,statusValues);
                    reached_btn.setEnabled(false);
                    reached_btn.setBackgroundResource(android.R.drawable.btn_default); //setBackgroundResource(R.drawable.btn_style);
                    /*finished_btn.setEnabled(true);
                    finished_btn.setTextColor(Color.BLACK);
                    finished_btn.setBackgroundResource(R.drawable.btn_style); //setBackgroundColor(0xf709af1a);
                    tv7.setText(statusValue);*/
                    display();
                    //confirm.dismiss();
                    break;

                case 8:
                    //statusValue = 8;
                    dbh.updateStatusToDb(statusValues);
                    dbh.getDispatchData();
                    dbh.insertStatusTable(dbh.dispatch_id,statusValues);
                    // GRN Form here
                    finished_btn.setEnabled(false);
                    finished_btn.setBackgroundResource(android.R.drawable.btn_default); //.setBackgroundResource(R.drawable.btn_style);
                   /* get_trip_btn.setEnabled(true);
                    get_trip_btn.setTextColor(Color.BLACK);
                    get_trip_btn.setBackgroundResource(R.drawable.btn_style); *///setBackgroundColor(0xf709af1a);

                    display();
                    myService.stopUpdateService(newIntent);

                    //confirm.dismiss();
                    break;

            }
        }catch(Exception e){
            e.printStackTrace();

        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        doUnbindService();
    }

    /**
     * Stores activity data in the Bundle.
     */
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean(REQUESTING_LOCATION_UPDATES_KEY, mRequestingLocationUpdates);
        savedInstanceState.putParcelable(LOCATION_KEY, mCurrentLocation);
        savedInstanceState.putString(LAST_UPDATED_TIME_STRING_KEY, mLastUpdateTime);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            doUnbindService();
            extras.turnGPSOff();

        }
        catch (Throwable t) {
            Log.e("MainActivity", "Failed to unbind from the service", t);
        }

    }


    void doUnbindService() {
        if (mIsBound) {
            // If we have received the service, and hence registered with it, then now is the time to unregister.
            if (myService != null) {

            }
            // Detach our existing connection.
            unbindService(mConnection);
            mIsBound = false;
        }
    }

    class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
            android.os.Process.setThreadPriority(priority);

            runOnUiThread(new Runnable(){

                @Override
                public void run() {
                    /*PendingIntent pi = PendingIntent.getService(this, 0, intent, 0);
                    alarm_manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    alarm_manager.set(AlarmManager.RTC, cur_cal.getTimeInMillis(),  pi);*/

                    if (myService != null) {
                        mCurrentLocation = myService.getLastLocation();
                        mLastUpdateTime = myService.getLastUpdateTime();
                        updateUI();

                    }
                }});
        }

    }


    private void updateUI() {
        if (mCurrentLocation != null) {
            // mLatitudeTextView.setText(String.valueOf(mCurrentLocation.getLatitude()));
            // mLongitudeTextView.setText(String.valueOf(mCurrentLocation.getLongitude()));
            //  mLastUpdateTimeTextView.setText(mLastUpdateTime);
            getDateTime();
            getAddress(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
        }
    }

    protected void getAddress(double latitude, double longitude){

        String add = "#", city = null,state = null,country,pin,name, area,sub;
        Geocoder geocoder = new Geocoder(this,Locale.getDefault());
        String result = null;
        try {

            List<Address> addressList = geocoder.getFromLocation(latitude, longitude, 2);
            if (addressList != null && addressList.size() > 0) {
                Address address = addressList.get(0);
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                    sb.append(address.getAddressLine(i)).append("\n");
                }
                sb.append(address.getCountryName());
                result = sb.toString();
                Log.i("Main getAddress() "," result : "+result);
            }
        }catch(Exception e){
            e.printStackTrace();
            Log.d("Main getAddress() "," Exception e: "+e);
        }

        address.setText(result);


    }



}
