package com.example.daniel.mymovies;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Movie;

import java.util.ArrayList;


public class DBHandler {
    private  DBHelper helper ;


    public DBHandler(Context context) {
        helper = new DBHelper(context,Constants.DATABASE_NAME,null,Constants.DATABASE_VERSION);
    }
    //This method is adding a new movie to db
    public void addMovie(Movies movies){

        SQLiteDatabase db = helper.getWritableDatabase();

        try {
            ContentValues values = new ContentValues();
            values.put(Constants.COLUMN_TITLE,movies.getTitle());
            values.put(Constants.COLUMN_YEAR,movies.getYear());
            values.put(Constants.COLUMN_IMDB_ID,movies.getImdbID());
            values.put(Constants.COLUMN_TYPE,movies.getType());
            values.put(Constants.COLUMN_POSTER,movies.getPoster());
            values.put(Constants.COLUMN_PLOT,movies.getPlot());
            values.put(Constants.COLUMN_RATING,movies.getRating());
            values.put(Constants.COLUMN_WATCHED_BOX,movies.getWatched());


            db.insertOrThrow(Constants.TABLE_NAME, null, values);

        }catch (SQLiteException e){
            e.getMessage();
        }finally {
            if(db.isOpen())
                db.close();
        }


    }
    //This method is removing a movie by getting it's id
    public void removeMovie(int id) {

        SQLiteDatabase db = helper.getWritableDatabase();
        try {

            db.delete(Constants.TABLE_NAME, "_id=?", new String[]{String.valueOf(id)});

        } catch (SQLiteException e) {
            e.getMessage();
        } finally {
            if (db.isOpen())
                db.close();


        }
    }
    //This method is removing all movies
    public void removeAllMovies(){

        SQLiteDatabase db = helper.getWritableDatabase();

        try {

            db.delete(Constants.TABLE_NAME, null, null);



        }catch (SQLiteException e){
            e.getMessage();
        }finally {
            if(db.isOpen())
                db.close();
        }
    }
    //This method is updating a movie by getting it's id
    public void updateMovie( Movies movies){
        SQLiteDatabase db = helper.getWritableDatabase();
        try {

            ContentValues values = new ContentValues();
            values.put(Constants.COLUMN_TITLE,movies.getTitle());
            values.put(Constants.COLUMN_YEAR,movies.getYear());
            values.put(Constants.COLUMN_IMDB_ID,movies.getImdbID());
            values.put(Constants.COLUMN_TYPE,movies.getType());
            values.put(Constants.COLUMN_PLOT,movies.getPlot());
            values.put(Constants.COLUMN_POSTER,movies.getPoster());
            values.put("_id",movies.getId());
            values.put(Constants.COLUMN_RATING,movies.getRating());
            values.put(Constants.COLUMN_WATCHED_BOX,movies.getWatched());
            db.update(Constants.TABLE_NAME, values, "_id=?", new String[]{String.valueOf(movies.getId())});

        }catch (SQLiteException e){
            e.getMessage();
        }finally {
            if(db.isOpen())
                db.close();
        }
    }
    //This cursor method is go through db, and return all details
    public Cursor showAllMovies(){
        Cursor cursor = null;
        SQLiteDatabase db = helper.getReadableDatabase();
        try {
            cursor = db.query(Constants.TABLE_NAME,null,null,null,null,null,null,null);
        }catch (SQLiteException e){
        e.printStackTrace();
        }
        return cursor;
    }



    public ArrayList<Movies> showAllMoviesArrayListAndSort(){
        ArrayList<Movies> moviesList = new ArrayList<>();
        Cursor cursor = null;
        SQLiteDatabase db = helper.getReadableDatabase();
        try {
            cursor = db.query(Constants.TABLE_NAME,null,null,null,null,null,Constants.COLUMN_TITLE+" COLLATE NOCASE");
        }catch (SQLiteException e){
            e.printStackTrace();
        }

        if (cursor != null ) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(0);
                String title = cursor.getString(cursor.getColumnIndex(Constants.COLUMN_TITLE));
                String year = cursor.getString(cursor.getColumnIndex(Constants.COLUMN_YEAR));
                String imdb_id = cursor.getString(cursor.getColumnIndex(Constants.COLUMN_IMDB_ID));
                String type = cursor.getString(cursor.getColumnIndex(Constants.COLUMN_TYPE));
                String poster = cursor.getString(cursor.getColumnIndex(Constants.COLUMN_POSTER));
                String rating = cursor.getString(cursor.getColumnIndex(Constants.COLUMN_RATING));
                String checked = cursor.getString(cursor.getColumnIndex(Constants.COLUMN_WATCHED_BOX));
                String plot = cursor.getString(cursor.getColumnIndex(Constants.COLUMN_PLOT));

                moviesList.add(new Movies(title, year, imdb_id, type, poster, rating, checked, plot, id));

            }
        }
        return moviesList;
    }

    //This cursor method is for finding a spesific movie
    public Movies getMovie(String id){
        Cursor cursor = null;
        Movies mov = new Movies();
        SQLiteDatabase db = helper.getReadableDatabase();
        try {
            cursor = db.query(Constants.TABLE_NAME,null,"_id=?",new String[]{id},null,null,null,null);


            cursor.moveToFirst();
            mov.setId(cursor.getInt(0));
            mov.setTitle(cursor.getString(cursor.getColumnIndex(Constants.COLUMN_TITLE)));
            mov.setYear(cursor.getString(cursor.getColumnIndex(Constants.COLUMN_YEAR)));
            mov.setImdbID(cursor.getString(cursor.getColumnIndex(Constants.COLUMN_IMDB_ID)));
            mov.setType(cursor.getString(cursor.getColumnIndex(Constants.COLUMN_TYPE)));
            mov.setPoster(cursor.getString(cursor.getColumnIndex(Constants.COLUMN_POSTER)));
            mov.setRating(cursor.getString(cursor.getColumnIndex(Constants.COLUMN_RATING)));
            mov.setWatched(cursor.getString(cursor.getColumnIndex(Constants.COLUMN_WATCHED_BOX)));
            mov.setPlot(cursor.getString(cursor.getColumnIndex(Constants.COLUMN_PLOT)));

        }catch (SQLiteException e){
            e.printStackTrace();
        }finally {
             if(db.isOpen())
                 db.close();
         }



            return mov;

    }




}