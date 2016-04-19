package biz.antworks.locationupdater;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by jagadish on 07-07-2015.
 */
public class Extras {

    protected Context context;
    String TAG="EXTRAS";
    private LocationManager lm;

    private boolean provider_enabled;
    private String provider;

    protected Extras(Context context){
        this.context = context;
    }

    protected boolean checkInternetConnection(Context context) {



        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        // if no network is available networkInfo will be null
        // otherwise check if we are connected
        if (networkInfo != null && networkInfo.isConnected()) {
            Toast.makeText(context, "Internet is connected",Toast.LENGTH_SHORT).show();
            Log.i(TAG, "connected");
            return true;
        }
        else {
            Toast.makeText(context, "not connected to internet",Toast.LENGTH_SHORT).show();
            Log.i(TAG,"No NET access");
            return false;
        }
    }

   /* private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }*/





    protected boolean checkGps(Context context) {

        lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        criteria.setCostAllowed(true);
        criteria.setSpeedRequired(true);

        provider = lm.getBestProvider(criteria, true);
        provider_enabled = lm.isProviderEnabled(provider);
        if (provider_enabled) {
            if (provider == lm.GPS_PROVIDER)
                Log.i("CHECK GPS: ", " Running");
                return true;

        }

        return false;
    }

    protected boolean isMyServiceRunning(Class<?> serviceClass) {

        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ( ( serviceClass.getName().equals(service.service.getClassName() ) ) ||
                    ( serviceClass.equals(service.process.getClass()) )  ) {
                return true;
            }
        }
        return false;
    }

    public void turnGPSOn()
    {
        Intent intent = new Intent("android.location.GPS_ENABLED_CHANGE");
        intent.putExtra("enabled", true);
        this.context.sendBroadcast(intent);

        String provider = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        if(!provider.contains("gps"))
        {
            //if gps is disabled
            final Intent poke = new Intent();
            poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
            poke.setData(Uri.parse("3"));
            this.context.sendBroadcast(poke);
        }
    }

    public void turnGPSOff()
    {
        String provider = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        if(provider.contains("gps")){ //if gps is enabled
            final Intent poke = new Intent();
            poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
            poke.setData(Uri.parse("3"));
            this.context.sendBroadcast(poke);
        }
    }

    protected String mapStatus(int value){

        String status=null;
        if(value==1)
            status= "got trip";
        if(value==4)
            status="started";
        if(value==6)
            status="reached";
        if(value==8)
            status="finished";
        return status;
    }


    public String mapStatusToServer(int statusValues) {

        String s=null;
        if(statusValues==1)
            s= "DISP_SCHEDULED";
        if(statusValues==4)
            s= "DISP_IN_TRANSIT";
        if(statusValues==6)
            s= "DISP_ARRIVED";
        if(statusValues==8)
            s= "DISP_COMPLETED";


        return s;
    }
}
