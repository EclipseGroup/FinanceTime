package com.eclipsegroup.dorel.financetime.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.eclipsegroup.dorel.financetime.models.Index;

import java.util.ArrayList;
import java.util.List;

public class Database{

    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;
    private Context context;

    private static final String DATABASE_NAME = "financetimedb";
    private static final String TABLE_NAME = "FAVORITE_TABLE";
    private static final String UID = "_id";


    public Database(DatabaseHelper dbHelper){
        this.dbHelper = dbHelper;
        context = dbHelper.context;
        db = dbHelper.getWritableDatabase();
    }

    public Boolean insertFavorite(String symbol, String type, String name){

        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.SYMBOL, symbol);
      /*  contentValues.put(DatabaseHelper.TYPE, type); */
        contentValues.put(DatabaseHelper.NAME, name);

        if(db.insert(DatabaseHelper.TABLE_NAME, null, contentValues) == -1){
            Toast.makeText(context, "Unable to insetFavorite",Toast.LENGTH_SHORT).show();
            return false;
        }
        else
            return true;
    }

    public Boolean isFavorite(String symbol){

        String [] columns = {DatabaseHelper.SYMBOL};
        String selection = DatabaseHelper.SYMBOL + " = '" + symbol + "'";
        Cursor cursor = db.query(DatabaseHelper.TABLE_NAME, columns, selection, null, null, null, null);

        int symbolIndex = cursor.getColumnIndex(DatabaseHelper.SYMBOL);

        if (!cursor.moveToFirst())
            return false;

        String givenSymbol = cursor.getString(symbolIndex);

        if (symbol.compareTo(givenSymbol) == 0)
            return true;
        else
            return false;
    }

    public ArrayList<String> getList(String type){

        ArrayList<String> list = new ArrayList<String>();
        String symbol;

        String [] columns = {DatabaseHelper.SYMBOL};
        String selection = DatabaseHelper.TYPE + " = '" + type + "'";
        Cursor cursor = db.query(DatabaseHelper.TABLE_NAME, columns, selection, null, null, null, null);

        if (!cursor.moveToFirst())
            return null;

        int symbolIndex = cursor.getColumnIndex(DatabaseHelper.SYMBOL);
      /*  int nameIndex = cursor.getColumnIndex(DatabaseHelper.NAME); */

        cursor.moveToPrevious();
        while (cursor.moveToNext()){
            list.add(cursor.getString(symbolIndex));
        }

        return list;
    }

    public void deleteFavorite(String symbol){
        String[] whereArgs = {symbol};
        db.delete(DatabaseHelper.TABLE_NAME, DatabaseHelper.SYMBOL + "=?", whereArgs);
    }

}
