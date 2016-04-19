package biz.antworks.locationupdater;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

import android.content.Context;

import android.content.SharedPreferences;
import android.location.Location;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;



import com.goebl.david.Request;
import com.goebl.david.Response;
import com.goebl.david.Webb;
import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;

import com.google.api.client.auth.oauth2.CredentialStore;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;

import com.google.api.client.json.Json;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONException;
import org.json.JSONObject;


public class RequestHelper {

    /** Global instance of the HTTP transport. */
    private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();

    /** Global instance of the JSON factory. */
    private static final JsonFactory JSON_FACTORY = new JacksonFactory();

    private final CredentialStore credentialStore;

    private AuthorizationCodeFlow flow;

    protected String cmd;

    protected String status;
    protected Location location;
    protected String address;
    private String TAG="RequestHelper ";

    protected String deviceUniqueId;
//    protected String vehicleNumber;
    protected String dispatchId;
    protected String subscriberID;
    protected JSONObject response;

    protected String appURL;
    protected String appID;
    protected String appUser;
    protected String appUserID;
    protected String appDefaultMessage;
    protected SharedPreferences prefs;
    protected String requestEntity;
    Context context;
    StoreLocationsDataBase sldb;
    DataBaseHandler dataBaseHandler;
    int statusVal;
    private String disp;
    private String uId, pwd, cId;


    public RequestHelper(SharedPreferences sharedPreferences, String command, String request, Context con) {
        this.prefs = sharedPreferences;
        this.credentialStore = new SharedPreferencesCredentialStore(sharedPreferences);
        this.location = null;
        this.cmd = command;
        this.requestEntity = request;
        context=con;
        Log.i(TAG,"(prefs, cmd, req) DispID==null " );
    }
    public SharedPreferences getPreference() {
        Log.i(TAG,"getPreference() " );
        return this.prefs;
    }

    public String executeApiCall() throws IOException, JSONException {
        Log.i(TAG ,"context inside executeAPICall() : "+context);
       dataBaseHandler=new DataBaseHandler(context);
        if (this.cmd.equalsIgnoreCase("Register")) {
            return executeRegisterGetCall();
        }
        else
            return executeUpdateLocationPostCall();

    }


    public String executeApiGetCall() throws IOException {
        Log.i(Constants.TAG,"Executing GET API call at url " + this.appURL);
        return ""; // HTTP_TRANSPORT.createRequestFactory(loadCredential()).buildGetRequest(new GenericUrl(this.requestParams.getApiUrl())).execute().parseAsString();
    }

    private String executeUpdateLocationPostCall() throws IOException, JSONException {
        String requestEntit="LocationEntity";

        Log.i(TAG,"\n\n executeUpdateLocationPostCall() dispatchId:"+dispatchId);
        dataBaseHandler.getDispatchData();
        dispatchId=dataBaseHandler.dispatch_id;
        if (dispatchId == null)
            return new JSONObject("").toString();
        getCreds();
       // byte[] credentials = (/*cId + ":" +*/ uId + ":" + pwd).getBytes("UTF-8");
        Log.i(TAG,"executeUpdateLocationPostCall() " );
       // String auth = "Basic " + Base64.encodeToString(credentials, 0);
        Webb webb = Webb.create();

       // webb.setDefaultHeader(Webb.HDR_AUTHORIZATION, auth);
        String now1 = new DateTime(new Date()).toString();

        Response<JSONObject> response = webb.post(appURL+requestEntity)
                .param("dispatchId", dispatchId)
                .param("lat", location.getLatitude())
                .param("lon", location.getLongitude())
                .param("datetimeArrived", now1)
                .param("authTenantId", cId)
                .param("authUsername", uId)
                .param("authPassword", pwd)
                .ensureSuccess()
                .asJsonObject();
        JSONObject sync = response.getBody();
        String dId=null;
        if(sync!=null && location!=null) {
            dId = sync.getString("locationId");
            Log.i(TAG, "sync.toString did: " + dId);
            dataBaseHandler.insertLocations(location, dId);
        }else if(location!=null) {
            dId = null;
            dataBaseHandler.insertLocations(location, dId);
        }
        //dispatchId=dId;
        return sync.toString();
    }

    private void getCreds()  {
       /* prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences prfs = PreferenceManager.getSharedPreferences("",);*/
      //  SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this.context);
       // String Astatus = prfs.getString("Authentication_Status", "");
        try {
            dataBaseHandler=new DataBaseHandler(context);
            JSONObject jsonOb = dataBaseHandler.getCredentials();
            Log.i(TAG, "-------josn OBJECT----- : " + jsonOb);
            cId = jsonOb.getString("cId");
            pwd = jsonOb.getString("pwd");
            uId = jsonOb.getString("uId");
            Log.i(TAG, "-------CID----- : " + cId);
            Log.i(TAG, "-------uID----- : " + uId);
            Log.i(TAG, "-------pwD----- : " + pwd);
        }catch (Exception e){
            e.printStackTrace();

        }
    }

    private String executeRegisterGetCall() throws IOException {
        try {
            getCreds();
            Log.i(TAG, "**********executeRegisterGetCall()************"+"uID : "+uId+" pwd: "+ pwd);
           // byte[] credentials = (uId + ":" + pwd).getBytes("UTF-8");
            JSONObject jo=new JSONObject();
            jo.put("authTenantId", cId);
            jo.put("uniqueId", deviceUniqueId);
            jo.put("authUsername", uId);
            jo.put("authPassword", pwd);
            Log.i(TAG, "executeRegisterGetCall() jo : " +jo);
           // String auth = "Basic " + Base64.encodeToString(credentials, 0);
            Webb webb = Webb.create();
           // webb.setDefaultHeader(Webb.HDR_AUTHORIZATION, auth);
            String now1 = new DateTime(new Date()).toString();
            Response<JSONObject> response = webb.post(appURL + requestEntity)
                     .param("uniqueId", deviceUniqueId)
                    .param("authTenantId", cId)
                    .param("authUsername", uId)
                    .param("authPassword", pwd)
//                .param("vehicleNumber", vehicleNumber)
                    .ensureSuccess()
                    .asJsonObject();
//            String sync=response.getBody();
            JSONObject sync = response.getBody();
            dispatchId = sync.getString("dispatchId");
            updateDispatchTable( sync);
            setResponse(sync);
//            Log.i(TAG," EXEREGGTCL() RESPONSE \n "+sync);
            Log.i(TAG," EXEREGGTCL() RESPONSE: dispatchID = "+dispatchId);
            dispatchId = dispatchId.indexOf('"') == 0 && dispatchId.lastIndexOf('"') == dispatchId.length()-1 ?
                    dispatchId.substring(1, dispatchId.length() - 1) :
                    dispatchId;

            if (dispatchId.equals("null"))
                return "";

        } catch (JSONException e) {
            Log.i("REQHELPER "," Invalid response response to GET call: " + e.getMessage());
            setResponse(null);
            return "";
        }
        return dispatchId;
    }

    public void updateDispatchTable(JSONObject sync) {

        try {
            if(sync.getString("statusId")!=null)
                statusVal=1;
            else
               statusVal=0;

            if(sync!=null && dispatchId!=null)
                dataBaseHandler.doRegister(deviceUniqueId, dispatchId, ""/*sync.getString("vehicleNumber")*/, sync.getString("startFacility"), sync.getString("startLat"),
                sync.getString("startLon"), sync.getString("endFacility"), sync.getString("endLat"), sync.getString("endLon"),statusVal);
            else
                Toast.makeText(context,"Null response from server",Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.i("REQHELPER "," Invalid response response to GET call: " + e.getMessage());
            setResponse(null);
        }
    }

    public void setAppMetaData(String url, String appID, String appUser, String appUserID, String appDefaultMessage) {
        this.appURL = url;
        this.appID = appID;
        this.appUser = appUser;
        this.appUserID = appUserID;
        this.appDefaultMessage = appDefaultMessage;
        Log.i("RequestHelper ","setAppMetaData " );
    }

    public void setCommand(String aD) {
        Log.i(TAG,"setcmd " );
        this.cmd = aD;
    }
    public String getCommand() {
        return cmd;
    }
    public boolean isRegister() {
        boolean val=false;
        try {
            if (this.cmd.equals("Register")) {
                Log.i(TAG, "isRegistered() true :  " + this.cmd);
                val= true;
            } else {
                Log.i(TAG, "isRegistered() false : " + this.cmd);
                val= false;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return val;
    }


    /*public void setSubscriberID(String aD) {

        Log.i(TAG,"setSubscriberid() " + aD);
        this.subscriberID = aD;
    }*/

    public void setLocation(Location aD) {
        Log.i(TAG,"setLoc() " + aD);
        this.location = aD;
    }

    public void setAddress(String aD) {
        this.address = aD;
    }


    public void setDeviceUniqueId(String aD) {
        Log.i("RequestHelper ","setDeviceUniqID() " + aD);
        this.deviceUniqueId = aD;
    }
    public void setDispatchId(String aD) {
        Log.i(TAG,"setDispID() " + aD);
        this.dispatchId = aD;
    }


    public String getDispatchId() {

        Log.i(TAG,"getDispID() " + this.dispatchId);
        return this.dispatchId;
    }

/*    public void setVehicleNumber(String aD) {
        this.vehicleNumber = aD;
    }
*/

    public void buildUrl(String str) {
        this.appURL = this.appURL + str;
    }

    public void setResponse(JSONObject obj) {
        Log.i(TAG,"setResponse "+obj );
        this.response = obj;
    }
    public JSONObject getResponse() {
        return response;
    }



    protected String executeUpdateStatus()  {

         String resp="";
        try {
                Log.i(TAG,"executeUpdateStatus : " );

           // byte[] credentials = (uId + ":" + pwd).getBytes("UTF-8");
            Log.i(TAG, "executeUpdateLocationPostCall() ");
           // String auth = "Basic " + Base64.encodeToString(credentials, 0);
            Webb webb = Webb.create();
            //webb.setDefaultHeader(Webb.HDR_AUTHORIZATION, auth);
            getCreds();
            Log.i(TAG, "**********executeUpdateStatus()************"+"uID : "+uId+" pwd: "+ pwd);
            Response<JSONObject> response = webb.post(appURL+requestEntity)
                    .param("authTenantId", cId)
                    .param("authUsername", uId)
                    .param("authPassword", pwd)
                    .param("dispatchId", disp)
                    .param("statusId", status)
                    .ensureSuccess()  //.asString();
                   .asJsonObject();
            JSONObject sync = response.getBody();
            Log.i(TAG,"----------------------executeUpdateStatus Response---------------:"+ sync);
            resp= sync.getString("dispatch");
            Log.i(TAG,"\n\n dispatch:"+resp);
            return resp;
        }catch(Exception e){
            e.printStackTrace();
        }
        /*HttpClient httpClient = new DefaultHttpClient();
            HttpContext localContext = new BasicHttpContext();
            HttpGet httpGet = new HttpGet(appURL+"GetNSetDispatchStatus?dispatchId="+dispId +"&statusId="+status);
            String text = null;

                HttpResponse response = httpClient.execute(httpGet, localContext);


                HttpEntity entity = response.getEntity();


                text = entity.toString();*/

        return resp;
    }


    public void setStatus(String status) {
        this.status=status;
    }

    public void setDispId(String disp) {
        this.disp=disp;
    }
}
