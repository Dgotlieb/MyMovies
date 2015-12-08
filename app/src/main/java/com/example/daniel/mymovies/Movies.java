package com.example.daniel.mymovies;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;


public class Movies {

    private String Title;
    private String Year ;
    private String imdbID ;
    private String Type ;
    private String Poster ;
    private String Rating ;
    private String Plot;
    private int Id;


    public Movies(String title, String year, String imdbID, String type, String poster, String rating, String watched, String plot, int id) {
        Title = title;
        Year = year;
        this.imdbID = imdbID;
        Type = type;
        Poster = poster;
        Rating = rating;
             Watched = watched;
        Plot = plot;
        Id = id;
    }

    public Movies(String title, String year, String imdbID, String type, String poster) {
        this.Title = title;
        this.Year = year;
        this.imdbID = imdbID;
        this.Type = type;
        this.Poster = poster;
    }



    public Movies(String title, String year, String imdb_id, String type, String poster, String plot, String movieRating, String checked) {
        Title = title;
        Year = year;
        imdbID = imdb_id;
        Type = type;
        Poster = poster;
        Rating = movieRating;
        Watched = checked;
        Plot = plot;
    }

    public Movies(String title, String year, String imdb_id, String type, String plot, String poster, int id, String movieRating, String checked) {
        Title = title;
        Year = year;
        imdbID = imdb_id;
        Type = type;
        Plot = plot;
        Poster = poster;
        Id = id;
        Rating = movieRating;
        Watched = checked;
    }

    public String getWatched() {
        return Watched;
    }

    public void setWatched(String watched) {
        Watched = watched;
    }

    public String getRating() {
        return Rating;
    }

    public void setRating(String rating) {
        Rating = rating;
    }

    private String Watched ;


    public String getPlot() {
        return Plot;
    }

    public void setPlot(String plot) {
        Plot = plot;
    }


    public Movies() {
    }




    public String getYear() {
        return Year;
    }

    public void setYear(String year) {
        Year = year;
    }

    public String getImdbID() {
        return imdbID;
    }

    public void setImdbID(String imdbID) {
        this.imdbID = imdbID;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getPoster() {
        return Poster;
    }

    public void setPoster(String poster) {
        Poster = poster;
    }



    public int getId() {
        return Id;
    }

    public void setId(int id) {
        this.Id = id;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        this.Title = title;
    }

    @Override
    public String toString() {

     
        return  Title ;
    }
}
