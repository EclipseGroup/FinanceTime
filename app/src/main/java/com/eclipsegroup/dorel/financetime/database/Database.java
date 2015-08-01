package com.eclipsegroup.dorel.financetime.database;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

public class Database{

    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;
    private Context context;

    public static final String PREF_FILE_NAME = "db";
    public static final String KEY_DATA_BASE_CREATED ="data_base_created";

    private static final String DATABASE_NAME = "financetimedb";
    private static final String TABLE_NAME = "FAVORITE_TABLE";
    private static final String UID = "_id";

    private static final int MAIN_PAGE = 0;
    private static final int FAVORITES = 1;


    public Database(DatabaseHelper dbHelper){
        this.dbHelper = dbHelper;
        context = dbHelper.context;

        boolean dataBaseCreated = Boolean.valueOf(readFromPreferences(context, KEY_DATA_BASE_CREATED, "false"));
        if(!dataBaseCreated){
            try {
                dbHelper.copyDataBase();
                saveToPreferences(context, KEY_DATA_BASE_CREATED, "true");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        db = dbHelper.getWritableDatabase();
    }

    public Boolean insertFavorite(String symbol, Integer type, String name){

        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.SYMBOL, symbol);
        contentValues.put(DatabaseHelper.TYPE, type);
      /*  contentValues.put(DatabaseHelper.NAME, name); */

        if(db.insert(DatabaseHelper.FAVORITE_TABLE, null, contentValues) == -1){
            Toast.makeText(context, "Unable to insetFavorite",Toast.LENGTH_SHORT).show();
            return false;
        }
        else
            return true;
    }

    public Boolean isFavorite(String symbol){

        String [] columns = {DatabaseHelper.SYMBOL};
        String selection = DatabaseHelper.SYMBOL + " = '" + symbol + "'";
        Cursor cursor = db.query(DatabaseHelper.FAVORITE_TABLE, columns, selection, null, null, null, null);

        int symbolIndex = cursor.getColumnIndex(DatabaseHelper.SYMBOL);

        if (!cursor.moveToFirst())
            return false;

        String givenSymbol = cursor.getString(symbolIndex);

        if (symbol.compareTo(givenSymbol) == 0)
            return true;
        else
            return false;
    }

    public ArrayList<String> getListType(Integer pageType, Integer type){

        ArrayList<String> list = new ArrayList<String>();
        String [] columns = {DatabaseHelper.SYMBOL};
        String selection= DatabaseHelper.TYPE + " = '" + pageType + "'";
        Cursor cursor;
        if(type == MAIN_PAGE)
            cursor = db.query(DatabaseHelper.MAIN_TABLE, columns, selection, null, null, null, null);
        else
            cursor = db.query(DatabaseHelper.FAVORITE_TABLE, columns, selection, null, null, null, null);

        if (!cursor.moveToFirst())
            return null;

        int symbolIndex = cursor.getColumnIndex(DatabaseHelper.SYMBOL);

        cursor.moveToPrevious();
        while (cursor.moveToNext()){
            list.add(cursor.getString(symbolIndex));
        }

        return list;
    }


    public void deleteFavorite(String symbol){
        String[] whereArgs = {symbol};
        db.delete(DatabaseHelper.FAVORITE_TABLE, DatabaseHelper.SYMBOL + "=?", whereArgs);
    }

    public static void saveToPreferences(Context context, String preferenceName, String preferenceValue){
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(preferenceName, preferenceValue);
        editor.apply();
    }

    public static String readFromPreferences(Context context, String preferenceName, String defaultValue){
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(preferenceName, defaultValue);
    }

}
