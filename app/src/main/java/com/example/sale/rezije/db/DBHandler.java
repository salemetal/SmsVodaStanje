package com.example.sale.rezije.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.sale.rezije.WaterStatus;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


/**
 * Created by Sale on 10.4.2016..
 */
public class DBHandler extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 4;
    // Database Name
    private static final String DATABASE_NAME = "rezijeDB";

    /**********VODA SNS TABLE***********************************/
    // VodaStanjeSms table name
    private static final String TABLE_VODA_SMS = "voda_stanje_sms";
    // VodaStanjeSms Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_WC = "wc_stanje";
    private static final String KEY_KUPAONA = "kupaona_stanje";
    private static final String KEY_DATE = "datum_unosa";
    /**************************************************************/

    public DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        /**********VODA SMS TABLE***********************************/
        String CREATE_VODASMS_TABLE = "CREATE TABLE " + TABLE_VODA_SMS + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_WC + " INTEGER NOT NULL,"
                + KEY_KUPAONA + " INTEGER NOT NULL,"
                + KEY_DATE + " DATETIME)";
        db.execSQL(CREATE_VODASMS_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_VODA_SMS);
        // Creating tables again
        onCreate(db);
    }

    //Adding water status
    public void addWaterStatus(WaterStatus waterStatus)
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_WC, waterStatus.wcVal);
        values.put(KEY_KUPAONA, waterStatus.kupaonaVal);
        values.put(KEY_DATE, dateFormat.format(date));

        // Inserting Row
        db.insert(TABLE_VODA_SMS, null, values);
        db.close();
    }

    //Get last water status
    public WaterStatus getLastWaterStatus()
    {
        String selectQuery = "SELECT * FROM " + TABLE_VODA_SMS +
                " WHERE " + KEY_ID + " = (SELECT MAX(" + KEY_ID + ") " + "FROM " + TABLE_VODA_SMS + ")";

        WaterStatus waterStatusLast = new WaterStatus();

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if(cursor.getCount() > 0) {
            cursor.moveToFirst();
            waterStatusLast.kupaonaVal = cursor.getInt(cursor.getColumnIndex(KEY_KUPAONA));
            waterStatusLast.wcVal = cursor.getInt(cursor.getColumnIndex(KEY_WC));

        }

            return waterStatusLast;
    }

    public ArrayList<Cursor> getData(String Query){
        //get writable database
        SQLiteDatabase sqlDB = this.getWritableDatabase();
        String[] columns = new String[] { "mesage" };
        //an array list of cursor to save two cursors one has results from the query
        //other cursor stores error message if any errors are triggered
        ArrayList<Cursor> alc = new ArrayList<Cursor>(2);
        MatrixCursor Cursor2= new MatrixCursor(columns);
        alc.add(null);
        alc.add(null);


        try{
            String maxQuery = Query ;
            //execute the query results will be save in Cursor c
            Cursor c = sqlDB.rawQuery(maxQuery, null);


            //add value to cursor2
            Cursor2.addRow(new Object[] { "Success" });

            alc.set(1,Cursor2);
            if (null != c && c.getCount() > 0) {


                alc.set(0,c);
                c.moveToFirst();

                return alc ;
            }
            return alc;
        } catch(SQLException sqlEx){
            Log.d("printing exception", sqlEx.getMessage());
            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[] { ""+sqlEx.getMessage() });
            alc.set(1,Cursor2);
            return alc;
        } catch(Exception ex){

            Log.d("printing exception", ex.getMessage());

            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[] { ""+ex.getMessage() });
            alc.set(1,Cursor2);
            return alc;
        }
    }
}
