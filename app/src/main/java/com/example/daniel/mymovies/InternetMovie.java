package com.example.daniel.mymovies;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class InternetMovie extends AppCompatActivity {

    private EditText searchBar;
    private ArrayList<Movies> mov_arr = new ArrayList<>();
    private ListView lv;
    private ArrayAdapter<Movies> adapter;
    private int moviePosition= 0 ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_internet_movie);
        /////////////////Delete me //////////////////

        searchBar = (EditText)findViewById(R.id.editSearch);
        Toolbar toolbar = (Toolbar) findViewById(R.id.appBar);
        setSupportActionBar(toolbar);
        lv = (ListView)findViewById(R.id.listView);
        adapter = new ArrayAdapter<Movies>(InternetMovie.this,android.R.layout.simple_expandable_list_item_1,mov_arr);
        lv.setAdapter(adapter);


        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //This button is for searching movies in API

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchBar = (EditText)findViewById(R.id.editSearch);
                String search = searchBar.getText().toString().trim();
                try {
                    search = URLEncoder.encode(search, "utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                String url = "http://www.omdbapi.com/?s="+search+"&y=&plot=full&r=json";
                MyTask task = new MyTask();
                task.execute(new String[]{url});


            }
        });


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_internet, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.exit:
                System.exit(0);
            }



        return super.onOptionsItemSelected(item);
    }

    class MyTask extends AsyncTask<String,Void,String> {


        @Override
        protected void onPreExecute() {
            ProgressBar bar = (ProgressBar)findViewById(R.id.progressBar);
            bar.setVisibility(ProgressBar.VISIBLE);


        }

        @Override
        protected String doInBackground(String... strings) {

            String response = sendHttpRequest(strings[0]);

            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            ProgressBar bar = (ProgressBar)findViewById(R.id.progressBar);
            bar.setVisibility(ProgressBar.INVISIBLE);

            //a loop for retrieving all movies related to the search and getting the back the info(title,year,imdb-id,type,poster)

            try {
                JSONObject object = new JSONObject(s);
                JSONArray array = object.getJSONArray("Search");

                for (int i = 0; i <array.length() ; i++) {
                    final String Title  =  array.getJSONObject(i).getString("Title");
                    final String Year  =  array.getJSONObject(i).getString("Year");
                    final String imdbID  =  array.getJSONObject(i).getString("imdbID");
                    final String Type  =  array.getJSONObject(i).getString("Type");
                    final String Poster  =  array.getJSONObject(i).getString("Poster");


                    Movies movie = new Movies(Title,Year,imdbID,Type,Poster);
                    mov_arr.add(movie);
                    adapter.notifyDataSetChanged();

                    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            moviePosition = position;
                            Movies movie = mov_arr.get(position);

                            Intent internetIntent = new Intent(InternetMovie.this,EditMovie.class);
                            internetIntent.putExtra(Constants.INTENT_TITLE, movie.getTitle());
                            internetIntent.putExtra(Constants.INTENT_YEAR, movie.getYear());
                            internetIntent.putExtra(Constants.INTENT_IMDB_ID,movie.getImdbID());
                            internetIntent.putExtra(Constants.INTENT_TYPE,movie.getType());
                            internetIntent.putExtra(Constants.INTENT_POSTER,movie.getPoster());
                            internetIntent.putExtra(Constants.INTENT_INTERNET,Constants.INTENT_INTERNET);
                            internetIntent.putExtra(Constants.INTENT_BOOLEAN_IS_NEW, true);
                            internetIntent.putExtra(Constants.INTENT_BOOLEAN_IS_FROM_INTERNET,true);
                            startActivity(internetIntent);

                        }
                    });


                }



            } catch (JSONException e) {
                e.printStackTrace();

            }

        }




        private String sendHttpRequest(String urlString) {
            BufferedReader input = null;
            HttpURLConnection httpCon = null;
            InputStream input_stream =null;
            InputStreamReader input_stream_reader = null;
            StringBuilder response = new StringBuilder();
            try{
                URL url = new URL(urlString);
                httpCon = (HttpURLConnection)url.openConnection();
                if(httpCon.getResponseCode()!=HttpURLConnection.HTTP_OK){
                    return null;
                }

                input_stream = httpCon.getInputStream();
                input_stream_reader = new InputStreamReader(input_stream);
                input = new BufferedReader(input_stream_reader);
                String line ;
                while ((line = input.readLine())!= null){
                    response.append(line +"\n");
                }



            }catch (Exception e) {
                e.printStackTrace();
            }finally{
                if(input!=null){
                    try {
                        input_stream_reader.close();
                        input_stream.close();
                        input.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if(httpCon != null){
                        httpCon.disconnect();
                    }
                }
            }
            return response.toString();
        }
    }

}
