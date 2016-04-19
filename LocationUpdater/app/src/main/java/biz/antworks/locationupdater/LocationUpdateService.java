package biz.antworks.locationupdater;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class LocationUpdateService extends Service implements
        ConnectionCallbacks, OnConnectionFailedListener, LocationListener {

    protected static Boolean started = false;
    AlarmManager alarm_manager;
    PendingIntent pi;
    Extras extras = new Extras(this);
    //  MainActivity mainActivity=new MainActivity();
    DataBaseHandler dbh;
    //
//
    private final IBinder myBinder = new MyLocalBinder();
    protected static final String TAG = "location-service";
    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 60000; //60secs

    /**
     * The fastest rate for active location updates. Exact. Updates will never be more frequent
     * than this value.
     */
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2; //30secs

    // Keys for storing activity state in the Bundle.
    protected final static String REQUESTING_LOCATION_UPDATES_KEY = "requesting-location-updates-key";
    protected final static String LOCATION_KEY = "location-key";
    protected final static String LAST_UPDATED_TIME_STRING_KEY = "last-updated-time-string-key";

    /**
     * Provides the entry point to Google Play services.
     */
    protected GoogleApiClient mGoogleApiClient;

    /**
     * Stores parameters for requests to the FusedLocationProviderApi.
     */
    protected LocationRequest mLocationRequest;

    /**
     * Represents last acquired geographical location.
     */
    protected Location mCurrentLocation;

    protected Location previousLocation;

    /**
     * Tracks the status of the location updates request. Value changes when the user presses the
     * Start Updates and Stop Updates buttons.
     */
    protected Boolean mRequestingLocationUpdates;

    /**
     * Tracks whether the user has requested an address. Becomes true when the user requests an
     * address and false when the address (or an error message) is delivered.
     * The user requests an address by pressing the Fetch Address button. This may happen
     * before GoogleApiClient connects. This activity uses this boolean to keep track of the
     * user's intent. If the value is true, the activity tries to fetch the address as soon as
     * GoogleApiClient connects.
     */
    protected boolean mAddressRequested;

    /**
     * The formatted location address.
     */
    protected String mAddressOutput;

    /**
     * Time when the location was updated represented as a String.
     */
    protected String mLastUpdateTime;
    protected String mTime;
    private SharedPreferences prefs;
    private RequestHelper requestHelper;
    private String mVehicleNumber;
    private String dispatchId;
    private String mSubscriptionId;

    protected String appURL;
    protected String appID;
    protected String appUser;
    protected String appUserID;
    protected String appDefaultMessage;
    protected String mSubscriberId;
    int startID;





    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        Log.i("Locationservice ", " Ibinder created ");
       // Toast.makeText(this, " service created ", Toast.LENGTH_LONG).show();
        return myBinder;
    }


    public class MyLocalBinder extends Binder {
        public LocationUpdateService getService() {
            Log.i("Locationservice ", " MyLocaLbinder created ");

            return LocationUpdateService.this;
        }
    }


    @Override
    public void onCreate() {
        super.onCreate();
        mRequestingLocationUpdates = false;
        mLastUpdateTime = "";
        dbh=new DataBaseHandler(getApplicationContext());

        //  locationUpdateService.updateValuesFromBundle(mainActivity.instanceState);
        this.prefs = PreferenceManager.getDefaultSharedPreferences(this);
        requestHelper = new RequestHelper(this.prefs, "LocationUpdater", "LocationEntity",this.getApplicationContext());
        //locationUpdateService = mainActivity.callReqHelp(locationUpdateService);
        //setDispatchId();
        mRequestingLocationUpdates = false;
        // requestHelper=mainActivity.appData(requestHelper);
        // get app config from manifest file
        // Kick off the process of building a GoogleApiClient and requesting the LocationServices
        // API.
         Log.i("service ", " onCreate() "  + " : ");
        //extras.turnGPSOn();
        buildGoogleApiClient();


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("service ", " Received start id " + startId + " : " + intent);

       // repeatThis(intent);
        startID=startId;
        mRequestingLocationUpdates = false;
        mLastUpdateTime = "";
        dbh=new DataBaseHandler(getApplicationContext());
        //  locationUpdateService.updateValuesFromBundle(mainActivity.instanceState);
        this.prefs = PreferenceManager.getDefaultSharedPreferences(this);
        requestHelper = new RequestHelper(this.prefs, "LocationUpdater", "LocationEntity", this.getApplicationContext());
        setDispatchId();
        requestHelper=appData(requestHelper);

        //locationUpdateService = mainActivity.callReqHelp(locationUpdateService);
        mRequestingLocationUpdates = false;
        // requestHelper=mainActivity.appData(requestHelper);
        // get app config from manifest file
        // Kick off the process of building a GoogleApiClient and requesting the LocationServices
        // API.
        Log.i("Locationservice ", " onStart() created ");
        //repeatThis(intent); //88
        buildGoogleApiClient(); //88
        return START_STICKY;
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
            dbh.getDispatchData();
            if(dbh.dispatch_id!=null){
                if(dbh.statusVal!=8){
                    TelephonyManager tel = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                    mSubscriberId = tel.getSubscriberId().toString();
                    Toast.makeText(this," Welcome ",Toast.LENGTH_LONG).show();
                }
            }


            Log.i(TAG, " appDATA() ");//5
            //requestHelper.setDeviceUniqueId(mSubscriberId);


        } catch (PackageManager.NameNotFoundException e) {
            Log.e(Constants.TAG, "Failed to load meta-data, NameNotFound: " + e.getMessage());
        } catch (NullPointerException e) {
            Log.e(Constants.TAG, "Failed to load meta-data, NullPointer: " + e.getMessage());
        }
        return requestHelper;
    }


 /*   protected void repatThis(Intent intent)
    {

    Log.i(TAG,"Location Update Service : "+LocationUpdateService.started);

    Calendar cur_cal = Calendar.getInstance();
    cur_cal.setTimeInMillis(System.currentTimeMillis());
    cur_cal.add(Calendar.MINUTE,1);
    //    Log.i("Testing", "Calender Set time:" + cur_cal.getTime());
    pi=PendingIntent.getService(this,0,intent,0);
    alarm_manager=(AlarmManager)  getSystemService(Context.ALARM_SERVICE);
        Log.i(TAG," repeat this ");
    alarm_manager.set(AlarmManager.RTC,cur_cal.getTimeInMillis(),pi);

       // buildGoogleApiClient(); //88

}
*/

/*
    @Override
    protected void onHandleIntent(Intent intent) {

        Log.i(TAG,"onHandleIntent");

    }*/
    /**
     * Builds a GoogleApiClient. Uses the {@code #addApi} method to request the
     * LocationServices API.
     */
    protected synchronized void buildGoogleApiClient() {
        Log.i(TAG, "Building GoogleApiClient");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        createLocationRequest();
        Log.i("Locationservice "," BGAPICLIENT() " );
        mGoogleApiClient.connect();
    }




    /**
     * Sets up the location request. Android has two location request settings:
     * {@code ACCESS_COARSE_LOCATION} and {@code ACCESS_FINE_LOCATION}. These settings control
     * the accuracy of the current location. This sample uses ACCESS_FINE_LOCATION, as defined in
     * the AndroidManifest.xml.
     * <p/>
     * When the ACCESS_FINE_LOCATION setting is specified, combined with a fast update
     * interval (5 seconds), the Fused Location Provider API returns location updates that are
     * accurate to within a few feet.
     * <p/>
     * These settings are appropriate for mapping applications that show real-time location
     * updates.
     */
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();

        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        Log.i("Locationservice "," CreateLocationRequest()  " );
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }




    /**
     * Removes location updates from the FusedLocationApi.
     */
    protected void stopLocationUpdates() {
        // It is a good practice to remove location requests when the activity is in a paused or
        // stopped state. Doing so helps battery performance and is especially
        // recommended in applications that request frequent location updates.

        // The final argument to {@code requestLocationUpdates()} is a LocationListener
        // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    private String getDateTime()
    {
        SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy hh:mm", Locale.getDefault());
        Date date=new Date();

        return sdf.format(date);
    }

    /**
     * Runs when a GoogleApiClient object successfully connects.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        Log.i(TAG, "Connected to GoogleApiClient");

        // If the initial location was never previously requested, we use
        // FusedLocationApi.getLastLocation() to get it. If it was previously requested, we store
        // its value in the Bundle and check for it in onCreate(). We
        // do not request it again unless the user specifically requests location updates by pressing
        // the Start Updates button.
        //
        // Because we cache the value of the initial location in the Bundle, it means that if the
        // user launches the activity,
        // moves to a new location, and then changes the device orientation, the original location
        // is displayed as the activity is re-created.
        Log.i("Locationservice ","onConnected() " );
        try {
            if (mCurrentLocation == null) {
                mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
//            sendMessageToUI(1);
            }

            // If the user presses the Start Updates button before GoogleApiClient connects, we set
            // mRequestingLocationUpdates to true (see startUpdatesButtonHandler()). Here, we check
            // the value of mRequestingLocationUpdates and if it is true, we start location updates.
//        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }catch(Exception e){
            e.printStackTrace();
        }
//        }
    }
    /**
     * Requests location updates from the FusedLocationApi.
     */
    protected void startLocationUpdates() {
        // The final argument to {@code requestLocationUpdates()} is a LocationListener
        // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
        Log.i("Locationservice "," startLocationUpdates() " );
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
        makeRestCall(requestHelper, mCurrentLocation, mAddressOutput); //88
    }
    /**
     * Callback that fires when the location changes.
     */
    @Override
    public void onLocationChanged(Location location) {
        if(mCurrentLocation!=null)
             previousLocation=mCurrentLocation;
        else
            previousLocation=location;
        mCurrentLocation = location;
        mTime=mLastUpdateTime;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        Log.i(TAG," onLocatiuonChanged() " +location);
        //Log.i(TAG, "LOC LAT DIFF" +(previousLocation.getLatitude()-mCurrentLocation.getLatitude()));
        //Log.i(TAG, "LOC Lat math DIFF" + Math.abs(previousLocation.getLatitude()-mCurrentLocation.getLatitude()) );
       // Log.i(TAG, "LOC DIFF" + (previousLocation.getLongitude()-mCurrentLocation.getLongitude()) );
       /* if (Math.abs(previousLocation.getLongitude()-mCurrentLocation.getLongitude()) <=0.5) {
            Log.i(TAG, "LOC DIFF" + (previousLocation.getLongitude()-mCurrentLocation.getLongitude()) );
            if (Math.abs(previousLocation.getLatitude()-mCurrentLocation.getLatitude()) <=0.5) {
                Log.i(TAG, "LOC DIFF" +(previousLocation.getLatitude()-mCurrentLocation.getLatitude()));
                return;
            }
        }*/

        makeRestCall(requestHelper, mCurrentLocation, mAddressOutput);
//        showToast(getString(R.string.location_saved_message));
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i(TAG, "Connection suspended");
        Log.i("Locationservice "," onConnectionSuspended " );
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
        Log.i("Locationservice "," onConnectionFailed" );
    }

    /**
     * Shows a toast with the given text.
     */
    protected void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    private void makeRestCall(RequestHelper requestHelper, Location location, String address) {
        Log.i("Locationservice "," makeRestcall " );
        ApiCallHelper apiCallHelper=new ApiCallHelper(this);
        apiCallHelper.doExecute(PreferenceManager.getDefaultSharedPreferences(this), requestHelper, location, address);
    }

    public String getLastUpdateTime() {

        Log.i("Locationservice "," getLastUpdateTime() " );
        return mLastUpdateTime;
    }

    public Location getLastLocation() {

        Log.i("Locationservice "," getLastLocation() " );
        return mCurrentLocation;
    }
    public RequestHelper getRequestHelper() {
        Log.i("Locationservice "," getRequestHelper() " );
        return requestHelper;
    }

    public void setDispatchId(String id) {
        Log.i("Locationservice "," setDispatchId " );
        requestHelper.setDispatchId(id);
    }

    public void setDispatchId() {
        Log.i("Locationservice "," setDispatchId " );
        try{
            dbh.getDispatchData();
           // mVehicleNumber= dbh.vehicle_no;
            dispatchId = dbh.dispatch_id;
            requestHelper.setDispatchId(dispatchId);
        }catch(Exception e){
            e.printStackTrace();
        }

    }

 protected void stopUpdateService(Intent inten){

    stopLocationUpdates();
     stopSelf();
     stopSelf(startID);
     stopService(inten);
     Log.i(TAG,"stopUpdateService ");
 }


    @Override
    public void onDestroy()
    {
        // super.onDestroy();
        //extras.turnGPSOff();
        //Toast.makeText(this,  " service onDestroy ", Toast.LENGTH_LONG).show();
        Log.i("Locationservice "," Destroy() " );
    }
}
