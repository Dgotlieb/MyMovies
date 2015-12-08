package com.example.daniel.mymovies;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;



public class DBHelper extends SQLiteOpenHelper {
    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }
        //This method creating the db according to the values below (table name, columns,type..)
    @Override
    public void onCreate(SQLiteDatabase db) {

        String movies = "CREATE TABLE movies (_id INTEGER PRIMARY KEY, title TEXT, year TEXT, imdb_id TEXT, type TEXT, poster TEXT, plot TEXT, rating TEXT, watched_box TEXT ); ";

        try {
            db.execSQL(movies);
        }catch (SQLiteException e){
            e.getMessage();
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

