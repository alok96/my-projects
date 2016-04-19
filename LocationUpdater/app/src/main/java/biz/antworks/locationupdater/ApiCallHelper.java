package biz.antworks.locationupdater;

import android.content.Context;
import android.content.SharedPreferences;

import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import android.widget.Toast;


/**
 * Created by Ashok on 18-04-2015.
 */
public class ApiCallHelper {

    private SharedPreferences prefs;
    private RequestHelper requestHelper;
    private RequestHelper reqHelpObj;
    private LocationUpdateService luService;
    private MainActivity mainActivity;
    private String TAG="ApiCallHelper ";
    private String dispId;
    String status;
    String decider=" ";
    Context context;
    public ApiCallHelper(biz.antworks.locationupdater.LocationUpdateService service) {
        luService = service;
    }

    public ApiCallHelper() {
        luService = null;
    }

    public  String doExecute(MainActivity ma, RequestHelper requestHelper, String deviceUniqueId) {
        mainActivity = ma;
        Log.i("ApicallHelper ","DoExecute(ma, ..,) " );
        return doExecute(requestHelper.getPreference(), requestHelper, deviceUniqueId);
    }

    public  String doExecute(SharedPreferences prefs, RequestHelper requestHelper, Location location, String address) {
        this.prefs = prefs;
        this.requestHelper = requestHelper;
        this.requestHelper.setLocation(location);
        this.requestHelper.setAddress(address);
        if (this.requestHelper.getDispatchId() == null) {
            Log.i(TAG,"DoExecute(... loc, address) DispID==null " );
            return "";
        }
        // Performs an authorized API call.
        Log.i(TAG,"DoExecute() DispID !=null " );
        return runApiCall();
    }

    public  String doExecute(SharedPreferences prefs, RequestHelper requestHelper, String deviceUniqueId) {
        this.prefs = prefs;
        this.requestHelper = requestHelper;
        this.requestHelper.setDeviceUniqueId(deviceUniqueId);
        this.requestHelper.setCommand("Register");
        Log.i(TAG,"DoExecute(... veh, device) DispID==null " );
        // Performs an authorized API call.
        return runApiCall();
    }

    public void doExecute(SharedPreferences prefs, RequestHelper requestHelper, String disp, String status) {

        this.prefs = prefs;
        this.reqHelpObj = requestHelper;
        this.reqHelpObj.setDispId(disp);
        this.reqHelpObj.setStatus(status);
        this.reqHelpObj.setCommand("updateStatus");
        Log.i(TAG,"DoExecute(.. disp, status " );
        decider="statusUpdate";
        // Performs an authorized API call.
         runApiCall();
    }

    /**
     * Performs an authorized API call.
     */
    private String runApiCall() {
        Log.i(TAG,"runApicall() " );
        ApiCallExecutor ace = new ApiCallExecutor();
        ace.execute();
        return ace.getResponse();
    }

    private class ApiCallExecutor extends AsyncTask<Uri, Void, String> {

        String apiResponse = "";

        public String getResponse() {
            return apiResponse;
        }

        @Override
        protected String doInBackground(Uri... params) {
            Log.i("ApicallHelper ","DoinBAckground " );
            try {

                if(decider.equalsIgnoreCase("statusUpdate")) {
                    reqHelpObj.executeUpdateStatus();
                    decider=" ";
                }
                else
                 apiResponse = requestHelper.executeApiCall();

                Log.i(TAG, "Received response from API : " + apiResponse);

            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return apiResponse;
        }

        @Override
        protected void onPostExecute(String result) {
            try {


                    Log.i(TAG, "onPostExecute() ");
                if(requestHelper!=null) {

                    if (requestHelper.isRegister())
                        requestHelper.setDispatchId(result);
                        if (mainActivity != null)
                            mainActivity.updateDispatch(result);
                            //requestHelper.updateDispatchTable(requestHelper.getResponse());
                           // showToast("Dispatch obtained: " + result + " updated database...");



                }
                    Log.i(TAG, "onPostExecute() finished ");

               // showToast("Location updated and saved - " + result);
            }catch(Exception e){
                e.printStackTrace();
            }
        }

        /**
         * Shows a toast with the given text.
         */
        protected void showToast(String text) {
            if (luService != null)
                Toast.makeText(luService, text, Toast.LENGTH_SHORT).show();
        }
    }
}
