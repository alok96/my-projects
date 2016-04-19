package biz.antworks.locationupdater;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.Toast;

import java.util.Calendar;


public class MyReceiver extends BroadcastReceiver {

    private static final String TAG = "MyReceiver";
    private Boolean gpsEnabled;
    boolean isMyServiceRunning;
    private Boolean internetEnabled;
    protected Context context;
    Extras extras;
    AlarmManager alarm_manager;
    PendingIntent pi;
    DataBaseHandler dbh;
    public MyReceiver()
    {
        super();
    }
    public MyReceiver(Context context){
        this.context=context;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();
        dbh=new DataBaseHandler(context);
        extras = new Extras(context);
        //Toast.makeText(context, "MyReceiver Started"+intent.getAction(),Toast.LENGTH_SHORT).show();
        Log.v("Info Message", "in Broadcast receiver : " + intent.getAction());


        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())/* || android.intent.action.*/) {

            try {
                dbh.getDispatchData();
                if( (dbh.statusVal==1) ||(dbh.statusVal==4) || (dbh.statusVal==6)) {
                    Log.v("Info Message", "in Broadcast receiver" + intent.getAction());
                   /* Intent newIntent = new Intent(this, LocationUpdateService.class);
                    startService(newIntent);*/
                    Intent myIntent=new Intent(context,LocationUpdateService.class);
                    myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    Calendar cal = Calendar.getInstance();
                    isMyServiceRunning = extras.isMyServiceRunning(LocationUpdateService.class);
                    cal.add(Calendar.SECOND, 45);  // Start  after 45sec of getting intent
                    if(isMyServiceRunning) {
                        Log.v(TAG, "service Running ");

                    }else {
                        ConnectivityManager cm =
                                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

                        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                        boolean isConnected = activeNetwork != null &&
                                activeNetwork.isConnectedOrConnecting();
                        if(isConnected)
                            context.startService(myIntent);
                        Log.v(TAG, "service started from Myreceiver");
                    }

                }

            }
            catch(Exception e){
                e.printStackTrace();
            }



          /*  Calendar cur_cal = Calendar.getInstance();
            cur_cal.setTimeInMillis(System.currentTimeMillis());
            cur_cal.add(Calendar.MINUTE,1);
            //    Log.i("Testing", "Calender Set time:" + cur_cal.getTime());
            pi=PendingIntent.getService(context,0,intent,0);
            alarm_manager=(AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Log.i(TAG," repeat this ");
            alarm_manager.set(AlarmManager.RTC,cur_cal.getTimeInMillis(),pi);
*/      }

       // }


    }





      /*  internetEnabled=extras.checkInternetConnection(context);
        gpsEnabled=extras.checkGps(context);

        if(internetEnabled || gpsEnabled) {

           if (intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION) ||

                    intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION) ||

                    intent.getAction().equals(gpsEnabled) || intent.getAction().equals(internetEnabled) ||

                    intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED) )

           {

           }
        }

        serviceStart();
      */





    private void serviceStart(){

        isMyServiceRunning = extras.isMyServiceRunning(LocationUpdateService.class);

        if (isMyServiceRunning) {
            Log.i("From MyReceiver ", " isMyServiceActive  " + isMyServiceRunning);
            Toast.makeText(context, "  isMyServiceActive = " + isMyServiceRunning, Toast.LENGTH_LONG).show();

        }
        else {

            Toast.makeText(context, "  isMyServiceActive  = " + isMyServiceRunning, Toast.LENGTH_LONG).show();

            Log.i("From MyReceiver ", " isMyServiceAvctive " + isMyServiceRunning);

            Intent myIntent = new Intent(context, LocationUpdateService.class);
            myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.SECOND, 45);  // Start  after 45sec of boot completed
            context.startService(myIntent);
        }


    }




}