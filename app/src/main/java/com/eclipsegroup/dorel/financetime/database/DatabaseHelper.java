package com.eclipsegroup.dorel.financetime.database;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DatabaseHelper extends SQLiteOpenHelper{

    public static final String DATABASE_NAME = "financetimedb";
    public static final String FAVORITE_TABLE = "FAVORITE_TABLE";
    public static final String MAIN_TABLE = "MAIN_TABLE";
    public static final String UID = "_id";
    public static final String SYMBOL = "Symbol";
    public static final String TYPE = "Type";
    public static final String NAME = "Name";
    private static final String TABLE_CREATE = "CREATE TABLE " + FAVORITE_TABLE +
            " (" + UID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            " Symbol VARCHAR(255), Type VARCHAR(255), Name VARCHAR(255));";
    private static final int DATABASE_VERSION = 3;
    public Context context;


    public DatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context =  context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        try{
            db.execSQL(TABLE_CREATE);
        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        try{
            db.execSQL(TABLE_CREATE);
        }
        catch (SQLException e){
            e.printStackTrace();
        }

    }

    public void copyDataBase() throws IOException {

        String db_path = context.getApplicationInfo().dataDir;

        InputStream myInput = context.getAssets().open(DATABASE_NAME);
        OutputStream myOutput = new FileOutputStream(db_path + "/databases/" + DATABASE_NAME);
        copyFile(myInput, myOutput);
        getWritableDatabase().close();
    }

    public static void copyFile(InputStream fromFile, OutputStream toFile) throws IOException {
        // transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;

        try {
            while ((length = fromFile.read(buffer)) > 0) {
                toFile.write(buffer, 0, length);
            }
        }
        // Close the streams
        finally {
            try {
                if (toFile != null) {
                    try {
                        toFile.flush();
                    } finally {
                        toFile.close();
                    }
                }
            } finally {
                if (fromFile != null) {
                    fromFile.close();
                }
            }
        }
    }

    public boolean checkDataBase() {

        String db_path = context.getApplicationInfo().dataDir;

        SQLiteDatabase checkDB = null;
        try {
            checkDB = SQLiteDatabase.openDatabase(db_path + "/databases/" + DATABASE_NAME, null,
                    SQLiteDatabase.OPEN_READONLY);
            checkDB.close();
        } catch (SQLiteException e) {
            // database doesn't exist yet.
        }
        return checkDB != null;
    }
}
