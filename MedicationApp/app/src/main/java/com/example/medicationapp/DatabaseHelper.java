package com.example.medicationapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "MedicationSystem";
    private static final String ACTIVE_REQUESTS_TABLE_NAME = "activeRequests";
    private static final String MEDICATION_DATA_TABLE_NAME = "activeRequests";
    private static final String[] COLUMN_NAMES = {"medicationID", "userID", "dateTime", "pillOneAmount", "pillTwoAmount", "pillThreeAmount", "status"};
    private static final String sqlActiveRequests = "CREATE TABLE activeRequests(medicationID INTEGER PRIMARY KEY, userID INTEGER, dateTime datetime, pillOneAmount INTEGER, pillTwoAmount INTEGER, pillThreeAmount INTEGER, status TEXT)";

    DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }




    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(sqlActiveRequests);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void addActiveRequest(MedicationData medication) {
        // Put medication data into ContentValues object to insert into database
        ContentValues row = new ContentValues();
        row.put(this.COLUMN_NAMES[0], medication.medicationID);
        row.put(this.COLUMN_NAMES[1], medication.userID);
        row.put(this.COLUMN_NAMES[2], medication.dateTime);
        row.put(this.COLUMN_NAMES[3], medication.pillOneAmount);
        row.put(this.COLUMN_NAMES[4], medication.pillTwoAmount);
        row.put(this.COLUMN_NAMES[5], medication.pillThreeAmount);
        row.put(this.COLUMN_NAMES[6], medication.status);


        // Get writable database
        SQLiteDatabase db = this.getWritableDatabase();

        // insert medication details
        db.insert(ACTIVE_REQUESTS_TABLE_NAME, null, row);
        db.close();

    }

        public ArrayList<MedicationData> getActiveRequest() {
        // Get readable database
        SQLiteDatabase db = this.getReadableDatabase();

        // Query the database for all medication requests
        Cursor result = db.rawQuery("SELECT * FROM activeRequests", null);

        // Create list of medication data objects
        ArrayList<MedicationData> medication = new ArrayList<MedicationData>();

        // For number of medication retrieved create a medication object
        for (int i = 0; i < result.getCount(); i++) {
            result.moveToPosition(i);
            medication.add(new MedicationData(result.getString(0), result.getString(1), result.getString(2), result.getString(3), result.getString(4), result.getString(5), result.getString(6)));
        }
        return medication;
    }

    public void removeByID(Integer medID) {
        // Get writable database
        SQLiteDatabase db = this.getWritableDatabase();


        // Remove row from table with with matching medID.
        String whereClause = "medicationID = '" + medID + "'";
        db.delete(ACTIVE_REQUESTS_TABLE_NAME, whereClause, null);
    }


}
