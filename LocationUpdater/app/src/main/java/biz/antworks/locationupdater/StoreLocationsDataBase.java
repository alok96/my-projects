package biz.antworks.locationupdater;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by jagadish on 20-08-2015.
 */
public class StoreLocationsDataBase extends SQLiteOpenHelper {


    // Database Version
    protected static final int DATABASE_VERSION = 2;
    // Database Name
    protected static final String DATABASE_NAME = "/mnt/sdcard/Reg.db";
    protected static final String TABLE_NAME = "LocationsTable";
    protected static final String NO="si_no";
    protected static final String DISPATCH_ID = "dispatch_id";
    protected static final String LATITUDE = "lat";
    protected static final String LONGITUDE= "lon";
    protected static final String DATE_TIME = "date";
    protected static final String STATUS = "status";

    private String TAG="SLDB ";
    SQLiteDatabase db;
    Cursor c;
    int msino;
    int mlocation_id;
    String mlat, mlon ,mstatus ,mdate_time;
    DataBaseHandler dbh;
    //String dispatchId;


    Context context;

    String DROP_LOCATIONS_TABLE = "DROP TABLE " + TABLE_NAME + ";";
    String CREATE_LOCATIONS_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "( " + NO + " INTEGER PRIMARY KEY AUTOINCREMENT , " +
            DISPATCH_ID + " TEXT , "+
            LATITUDE + " TEXT, "+
            LONGITUDE + " TEXT, "+
            DATE_TIME  + " TEXT, "+
            STATUS+ " TEXT);";

    public StoreLocationsDataBase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context=context;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_LOCATIONS_TABLE);
        Log.i(TAG, "Locations TABLE CREATED ");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);  // Create tables again
    }
    protected void insertLocations( Location locations, String status) {
        dbh=new DataBaseHandler(context);
        dbh.getDispatchData();
        String dispatchId=dbh.dispatch_id; //getRecentDispatchId();
        ContentValues values = new ContentValues();

        values.put(DISPATCH_ID, dispatchId);
        values.put(LATITUDE, locations.getLatitude());
        values.put(LONGITUDE, locations.getLongitude());
        values.put(DATE_TIME, getDateTime());
        values.put(STATUS, status);
        Log.i(TAG, " INsertLoca() ");//1
        db=dbOpen();
        db.insert(TABLE_NAME, null, values);
        db.close();
        Log.i(TAG, " ROW inserted into TAble ");
    }

    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }


  /*  protected void getLocationsData(){
        int i=0;
        try {
            db =dbOpen();
            c = db.rawQuery("SELECT * FROM "+TABLE_NAME+" ORDER BY "+NO+" ;", null);                 *//*DESC LIMIT 1*//*

            if (c != null) {
                c.moveToFirst();
                Log.i("Cursor Object", DatabaseUtils.dumpCursorToString(c));
                do{
                    msino=c.getInt(c.getColumnIndex(NO));
                    mlocation_id = c.getInt(c.getColumnIndex(LOCATIONS_ID));
                    mlat = c.getString(c.getColumnIndex(LATITUDE));
                    mlon= c.getString(c.getColumnIndex(LONGITUDE));
                    mdate_time = c.getString(c.getColumnIndex(DATE_TIME));
                    mstatus = c.getString(c.getColumnIndex(STATUS));

                    Log.i(TAG, "values Retrieved " + msino +" " + mlocation_id + " "+ mlat+" "+mlon+" "+ mdate_time + "  " +mstatus);
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
*/
    private SQLiteDatabase dbOpen()
    {
        Log.i(TAG,"dbopen()"+this.getWritableDatabase());
        return this.getWritableDatabase();
    }

    private void dbClose()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.close();
        c.close();
    }


}
