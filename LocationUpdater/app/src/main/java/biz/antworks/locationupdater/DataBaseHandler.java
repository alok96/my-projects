package biz.antworks.locationupdater;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by jagadish on 20-04-2015.
 */

 class DataBaseHandler extends SQLiteOpenHelper {

    Context context;
    SQLiteDatabase db;
    Cursor c;
    Extras extras;


    private String TAG="DBH";
    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "/mnt/sdcard/LocationUpdater.db";
    private static final String DISPATCH_DETAILS_TABLE = "Dispatch_Details_Table";


    private String CREDENTIALS_TABLE="credentials_Table";
    private static final String CLIENT_ID="client_Id";
    private static final String USER_ID="user_Id";
    private static final String PASS="password";

    String CREATE_CREDENTIALS_TABLE = "CREATE TABLE IF NOT EXISTS " + CREDENTIALS_TABLE + "( " + SINO + " INTEGER PRIMARY KEY AUTOINCREMENT , " +
            CLIENT_ID + " TEXT , "+
            USER_ID  + " TEXT , "+
            PASS + " TEXT);";


    private static final String SINO="si_no";

    private static final String DISPATCH_ID = "dispatch_id";
    private static final String DEVICE_ID ="device_id";
    private static final String VEHICLE_NO = "vehicle_no";
    private static final String START_PLACE ="start_place";
    protected static final String START_LAT = "start_lat";
    protected static final String START_LON= "start_lon";
    private static final String DEST_PLACE = "destin_place";
    protected static final String DEST_LAT = "dest_lat";
    protected static final String DEST_LON= "dest_lon";
    private static final String REG_DATE = "reg_date_time";
    protected static final String STATUS = "status";


    //private static final String LAST_TRIP_TIME = "last_trip_time";

    int sino;
    String dispatch_id;
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

    int[] sino2=new int[4];
    String[] disp=new String[4];
    String[] vNo=new String[4];
    String[] start=new String[4];
    String[] dest=new String[4];
    String[] dateNtime=new String[4];
    int[] status=new int[4];


    //String DROP_VEHICLE_TABLE = "DROP TABLE " + DISPATCH_DETAILS_TABLE + ";";
    String CREATE_DISPATCH_DETAILS_TABLE = "CREATE TABLE IF NOT EXISTS " + DISPATCH_DETAILS_TABLE + "( " + SINO + " INTEGER PRIMARY KEY AUTOINCREMENT , " +
            DISPATCH_ID + " TEXT , "+
            DEVICE_ID + " TEXT , "+
            VEHICLE_NO + " TEXT , "+

            START_PLACE + " TEXT , "+
            START_LAT + " TEXT , "+
            START_LON + " TEXT , "+

            DEST_PLACE + " TEXT , "+
            DEST_LAT + " TEXT , "+
            DEST_LON + " TEXT , "+

            REG_DATE + " TEXT , "+
            STATUS + " INTEGER );";




    protected static final String TABLE_LOCATIONS = "Locations_Table";
    protected static final String DISPATCH_ID1 = "dispatch_id";
    protected static final String LATITUDE = "latitude";
    protected static final String LONGITUDE= "longitude";
    protected static final String DATE_TIME = "date_time";
    protected static final String STATUS1 = "status";
    protected static final String FLAG = "flag";

    int msino;
    String mdispatchId,mlat, mlon ,mdate_time ,mstatus ;

    //String DROP_LOCATIONS_TABLE = "DROP TABLE " + TABLE_NAME + ";";
    String CREATE_LOCATIONS_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_LOCATIONS + "( " + SINO + " INTEGER PRIMARY KEY AUTOINCREMENT , " +
            DISPATCH_ID1 + " TEXT , "+
            LATITUDE + " TEXT, "+
            LONGITUDE + " TEXT, "+
            DATE_TIME  + " TEXT, "+
            STATUS1  + " TEXT, "+
            FLAG + " INTEGER);";




    protected static final String TABLE_STATUS_TIME = "Status_Time_Table";
    protected static final String DISPATCH_ID2 = "dispatch_id";
    protected static final String STATUS2 = "status";
    protected static final String DATE_TIME2 = "date_time";

    String dateTime2[]=new String[4];

    String CREATE_STATUS_TIME_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_STATUS_TIME + "( " + SINO + " INTEGER PRIMARY KEY AUTOINCREMENT , " +
            DISPATCH_ID2 + " TEXT , "+
            STATUS2  + " INTEGER , "+
            DATE_TIME2 + " TEXT);";




    public DataBaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context=context;

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_DISPATCH_DETAILS_TABLE);
        db.execSQL(CREATE_LOCATIONS_TABLE);
        db.execSQL(CREATE_STATUS_TIME_TABLE);
        db.execSQL(CREATE_CREDENTIALS_TABLE);
        Log.i(TAG ,"DATABASE AND TABLEs CREATED ");

    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + DISPATCH_DETAILS_TABLE);
        //db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRIP);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATIONS);
        db.execSQL("DROP TABLE IF EXISTS " +TABLE_STATUS_TIME);
        onCreate(db);  // Create tables again
    }

    protected void doRegister(String subscriptionId, String dispatchId, String vno, String start, String startLat, String startLon, String end, String endLat, String endLon, int status) {
        try {

       /* c = db.rawQuery("SELECT * FROM " + DISPATCH_DETAILS_TABLE + " WHERE DISPATCH_ID = " + dispatchId + " ORDER BY "+SINO+" DESC LIMIT 1;", null);

        if (c != null) {
            Log.i("FROM DBH : "," Registered already");
            Toast.makeText(context, "Dispatch already registered ",Toast.LENGTH_SHORT).show();
        } else {*/

            ContentValues values = new ContentValues();

            values.put(DISPATCH_ID, dispatchId);
            values.put(DEVICE_ID, subscriptionId);
            values.put(VEHICLE_NO, vno);

            values.put(START_PLACE, start);        //get (start place lat lon), (dest place lat lon) as params
            values.put(START_LAT, startLat);
            values.put(START_LON, startLon);

            values.put(DEST_PLACE, end);
            values.put(DEST_LAT, endLat);
            values.put(DEST_LON, endLon);

            values.put(REG_DATE, getDateTime());
            values.put(STATUS, status);

            Log.i(TAG, " doREG() ");//1
            db = dbOpen();
            db.insert(DISPATCH_DETAILS_TABLE, null, values);
            Log.i(TAG, " ROW inserted into Dispatch Details Table ");
            // }
            db.close();

            insertStatusTable(dispatchId, status);

        }catch(Exception e){
            e.printStackTrace();
        }

    }



    protected void insertLocations( Location locations, String status) {
        try {
            int flag;
            getDispatchData();
            mdispatchId = dispatch_id; //getRecentDispatchId();
            if (status != null)
                flag = 1;
            else
                flag = 0;
            ContentValues values = new ContentValues();
            values.put(DISPATCH_ID1, mdispatchId);
            values.put(LATITUDE, locations.getLatitude());
            values.put(LONGITUDE, locations.getLongitude());
            values.put(DATE_TIME, getLocDateTime());
            values.put(STATUS1, status);
            values.put(FLAG, flag);
            Log.i(TAG, " INsertLoca() ");//1
            db = dbOpen();
            db.insert(TABLE_LOCATIONS, null, values);
            db.close();
            Log.i(TAG, " ROW inserted into Locations TAble ");
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    protected void insertStatusTable(String disp, int state ){

        try {
            if (disp != null) {
                ContentValues values = new ContentValues();
                values.put(DISPATCH_ID2, disp);
                values.put(STATUS2, state);
                values.put(DATE_TIME2, getLocDateTime());
                Log.i(TAG, " INsertstatusTable() "+disp);//1
                db = dbOpen();
                db.insert(TABLE_STATUS_TIME, null, values);
                db.close();
                Log.i(TAG, " ROW inserted into Locations TAble ");
            }else{
                Log.i(TAG,"insertStatusTable(): dispId"+disp);
            }

        }catch(Exception e){
            e.printStackTrace();
        }
    }



    protected void insertCredentialsTable(String cId,String uId, String pwd ){

        try {
            if (cId != null) {
                ContentValues values = new ContentValues();
                values.put(CLIENT_ID, cId);
                values.put(USER_ID, uId);
                values.put(PASS, pwd);
                Log.i(TAG, " INsertCredsTable() "+cId);//1
                db = dbOpen();
                db.insert(CREDENTIALS_TABLE, null, values);
                db.close();
                Log.i(TAG, " ROW inserted into Credentials TAble ");
            }else{
                Log.i(TAG,"insert_Creds_Table() fails: cId : "+cId);
            }

        }catch(Exception e){
            e.printStackTrace();
        }
    }


    protected String[] getStatusTime(String desp){

        int i=0;
        try {
            db =dbOpen();
            c = db.rawQuery("SELECT "+DATE_TIME2+" FROM "+TABLE_STATUS_TIME+" WHERE "+DISPATCH_ID2+" = "+desp+" ORDER BY "+SINO+";", null);

            if (c != null) {
                c.moveToFirst();
                Log.i("Cursor Object", DatabaseUtils.dumpCursorToString(c));
                do{
                    /*sino2[i]=c.getInt(c.getColumnIndex(SINO));
                    start[i] = c.getString(c.getColumnIndex(START_PLACE));
                    dest[i] = c.getString(c.getColumnIndex(DEST_PLACE));
                    date[i] = c.getString(c.getColumnIndex(REG_DATE));*/
                    dateTime2[i] = c.getString(c.getColumnIndex(DATE_TIME2));
                    Log.i(TAG, "values Retrieved "+i+" is " + dateTime2[i] );
                    // x= " "+(i+1)+"   " + start + "   " + dest + "  " + t;
                    i=i+1;
                } while (c.moveToNext());
                dbClose();
                return dateTime2;
            }
            else{
                Log.i(TAG," NO RECORDS");
                Toast.makeText(context, " NO RECORDS ", Toast.LENGTH_SHORT).show();
            }
        }catch(Exception e){
            Log.i(TAG,"EXCEPTION in getStatusTime() : "+e);
        }
        return dateTime2;
    }


    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy",Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    private String getLocDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }
    protected void getDispatchData(){
        int i=0;
        try {
            db =dbOpen();
            c = db.rawQuery("SELECT * FROM "+DISPATCH_DETAILS_TABLE+" ORDER BY "+SINO+" DESC LIMIT 1;", null);
           /* if(c.getCount()==0)
                return null;*/

            if (c != null) {
                c.moveToFirst();
                Log.i("Cursor Object", DatabaseUtils.dumpCursorToString(c));
                do{
                    sino=c.getInt(c.getColumnIndex(SINO));
                    device_id = c.getString(c.getColumnIndex(DEVICE_ID));
                    vehicle_no = c.getString(c.getColumnIndex(VEHICLE_NO));
                    dispatch_id = c.getString(c.getColumnIndex(DISPATCH_ID));

                    start_place = c.getString(c.getColumnIndex(START_PLACE));
                    start_lat = c.getString(c.getColumnIndex(START_LAT));
                    start_lon = c.getString(c.getColumnIndex(START_LON));

                    destin_place = c.getString(c.getColumnIndex(DEST_PLACE));
                    dest_lat = c.getString(c.getColumnIndex(DEST_LAT));
                    dest_lon = c.getString(c.getColumnIndex(DEST_LON));

                    reg_date_time = c.getString(c.getColumnIndex(REG_DATE));
                    statusVal = c.getInt(c.getColumnIndex(STATUS));

                    Log.i("DBH : ", "values Retrieved one " + sino +" " +  device_id + " Date: " +vehicle_no +" "+dispatch_id +" "
                            +start_place +" "
                            + start_lat +" "
                            +start_lon +" "

                            +destin_place +" "
                            +dest_lat +" "
                            +dest_lon +" "

                            +reg_date_time+" "
                            + status );

                    // x= " "+(i+1)+"   " + start + "   " + dest + "  " + t;
                    i=i+1;
                } while (c.moveToNext());
            }
            else{
                Log.i("FROM DBH : "," NO RECORDS");
                Toast.makeText(context," NO RECORDS ",Toast.LENGTH_SHORT).show();
            }
        }catch(Exception e){
            Log.i(TAG,"EXCEPTION in getDispatchData() : "+e);
        }
        dbClose();

    }


    protected void getLast4Dispatches(){
        int i=0;
        try {
            db =dbOpen();
            c = db.rawQuery("SELECT * FROM "+DISPATCH_DETAILS_TABLE+" ORDER BY "+SINO+" DESC LIMIT 4;", null);

            if (c != null) {
                c.moveToFirst();
                Log.i("Cursor Object", DatabaseUtils.dumpCursorToString(c));
                do{
                    sino2[i]=c.getInt(c.getColumnIndex(SINO));
                    disp[i]=c.getString(c.getColumnIndex(DISPATCH_ID));
                    vNo[i]=c.getString(c.getColumnIndex(VEHICLE_NO));
                    start[i] = c.getString(c.getColumnIndex(START_PLACE));
                    dest[i] = c.getString(c.getColumnIndex(DEST_PLACE));
                    dateNtime[i] = c.getString(c.getColumnIndex(REG_DATE));
                    status[i] = c.getInt(c.getColumnIndex(STATUS));
                    Log.i(TAG, "values Retrieved one " + sino2[i] +" " + start[i] + " to " +  dest[i] + " Date: " +dateNtime[i]);
                    // x= " "+(i+1)+"   " + start + "   " + dest + "  " + t;
                    i=i+1;
                } while (c.moveToNext());
                dbClose();
            }
            else{
                Log.i("FROM DBH : "," NO RECORDS");
                Toast.makeText(context, " NO RECORDS ", Toast.LENGTH_SHORT).show();
            }
        }catch(Exception e){
            Log.i("DATABASE HANDLER","EXCEPTION in getData() : "+e);
        }


    }



    protected void getLocationsData(){
        int i=0;
        try {
            db =dbOpen();
            c = db.rawQuery("SELECT * FROM "+TABLE_LOCATIONS+" ORDER BY "+SINO+" ;", null);         /*DESC LIMIT 1*/

            if (c != null) {
                c.moveToFirst();
                Log.i("Cursor Object", DatabaseUtils.dumpCursorToString(c));
                do{
                    msino=c.getInt(c.getColumnIndex(SINO));
                    mdispatchId = c.getString(c.getColumnIndex(DISPATCH_ID1));
                    mlat = c.getString(c.getColumnIndex(LATITUDE));
                    mlon= c.getString(c.getColumnIndex(LONGITUDE));
                    mdate_time = c.getString(c.getColumnIndex(DATE_TIME));
                    mstatus = c.getString(c.getColumnIndex(STATUS));

                    Log.i(TAG, "values Retrieved " + msino +" " + mdispatchId + " "+ mlat+" "+mlon+" "+ mdate_time + "  " +mstatus);
                    // x= " "+(i+1)+"   " + start + "   " + dest + "  " + t;
                    i=i+1;
                } while (c.moveToNext());
            }
            else{
                Log.i(TAG," NO RECORDS");
                Toast.makeText(context, " NO RECORDS ", Toast.LENGTH_SHORT).show();
            }
        }catch(Exception e){
            Log.i(TAG,"EXCEPTION in getData() : "+e);
        }
        dbClose();

    }

    private SQLiteDatabase dbOpen()
    {
        return this.getWritableDatabase();
    }

    private void dbClose()    {

        db.close();
        c.close();
    }


    protected JSONObject getDetailsFromDispatchId(String dsptch) {
        //get latest dispatchId from DISPATCH_DETAILS_TABLE
        String disp_id="", stat;
        JSONObject jsonObject=new JSONObject();
        int i=0;
        try {
            db =dbOpen();
            c = db.rawQuery("SELECT * FROM "+DISPATCH_DETAILS_TABLE+" WHERE "+ DISPATCH_ID + " = "+dsptch+";", null);

            if (c != null) {
                c.moveToFirst();
                do{
                    jsonObject.put("start_place", c.getString(c.getColumnIndex(START_PLACE)));
                    jsonObject.put("destin_place",c.getString(c.getColumnIndex(DEST_PLACE)));
                    jsonObject.put("status",c.getInt(c.getColumnIndex(STATUS)));
                    jsonObject.put("dispatch_id",c.getString(c.getColumnIndex(DISPATCH_ID)));
                    jsonObject.put("vehicle_no",c.getString(c.getColumnIndex(VEHICLE_NO)));

                    Log.i(TAG, "values Retrieved \n" + c.getString(c.getColumnIndex(START_PLACE)) +" \n" +c.getString(c.getColumnIndex(DEST_PLACE)) + " \n "+c.getString(c.getColumnIndex(DISPATCH_ID))+" \n "+c.getInt(c.getColumnIndex(STATUS))+"\n"+ c.getString(c.getColumnIndex(VEHICLE_NO)) + "  " +mstatus);
                    // x= " "+(i+1)+"   " + start + "   " + dest + "  " + t;
                    i=i+1;
                } while (c.moveToNext());
                dbClose();
                return jsonObject;

            }
            else{
                Log.i("FROM DBH : ", " NO RECORDS");
                Toast.makeText(context," NO RECORDS/DispatchId ",Toast.LENGTH_SHORT).show();
            }
        }catch(Exception e){
            Log.i("DATABASE HANDLER","EXCEPTION in getDispatchId() : "+e);
        }

        return jsonObject;
    }


    protected JSONObject getCredentials(/*String cId*/) {
        //get credentials from CREDENTIALS_TABLE
        String disp_id="", stat;
        JSONObject jsonObject=new JSONObject();
        int i=0;
        try {
            db =dbOpen();
            c = db.rawQuery("SELECT * FROM "+CREDENTIALS_TABLE+";", null); /*+" WHERE "+ DISPATCH_ID + " = "+dsptch*/

            if (c != null) {
                c.moveToFirst();
                do{
                    jsonObject.put("cId", c.getString(c.getColumnIndex(CLIENT_ID)));
                    jsonObject.put("uId",c.getString(c.getColumnIndex(USER_ID)));
                    jsonObject.put("pwd",c.getString(c.getColumnIndex(PASS)));
                    Log.i(TAG, "getCredentials --- values Retrieved \n" + c.getString(c.getColumnIndex(CLIENT_ID)) +" \n" +c.getString(c.getColumnIndex(USER_ID)) + " \n "+c.getString(c.getColumnIndex(PASS)));
                    // x= " "+(i+1)+"   " + start + "   " + dest + "  " + t;
                    i=i+1;
                } while (c.moveToNext());
                dbClose();
                return jsonObject;

            }
            else{
                Log.i(TAG, " getCredentials----NO RECORDS");
                Toast.makeText(context," NO Credentials ",Toast.LENGTH_SHORT).show();

            }
        }catch(Exception e){
            Log.i(TAG,"EXCEPTION in getCreds() : "+e);
        }

        return jsonObject;
    }




    protected void checkLocationsStatus(){
       int i=0;
       String stat;
       try {
           db =dbOpen();
           c = db.rawQuery("SELECT * FROM "+TABLE_LOCATIONS+" WHERE "+FLAG+"="+0+" ORDER BY "+SINO+ ";", null);  // DESC LIMIT 1

           if (c!= null) {
               c.moveToFirst();
               Log.i("Cursor Object", DatabaseUtils.dumpCursorToString(c));
                c.getCount();
               stat = c.getString(c.getColumnIndex(STATUS));
               Log.i(TAG, "values Retrieved status = "+stat);
               // x= " "+(i+1)+"   " + start + "   " + dest + "  " + t;
               i=i+1;
               dbClose();

           }
           else{
               Log.i(TAG, " NO Locations with null status");

           }
       }catch(Exception e){
           Log.i("DATABASE HANDLER","EXCEPTION in checkLocationsStatus() : "+e);
       }


   }


    public void updateStatusToDb(int stat) {
        try{

            getDispatchData();
            mdispatchId=dispatch_id;
            //mdispatchId=getRecentDispatchId();
            db=dbOpen();
            c=db.rawQuery(" UPDATE "+DISPATCH_DETAILS_TABLE+" SET "+STATUS+" = '"+stat+"' WHERE "+DISPATCH_ID+" = '"+mdispatchId+"';" ,null);
            c.moveToFirst();
            Log.i(TAG," Status Updated to Table"+DISPATCH_DETAILS_TABLE);
            Toast.makeText(context, "Status updated to "+stat,Toast.LENGTH_SHORT).show();
            dbClose();
        }catch(Exception e){
            e.printStackTrace();
        }
    }



}
