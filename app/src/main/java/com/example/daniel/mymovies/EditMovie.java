package com.example.daniel.mymovies;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Movie;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.listeners.ActionClickListener;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class EditMovie extends AppCompatActivity {


    private boolean isNewMovie = true;
    private Toolbar toolbar;
    private ImageView thumb;
    private CheckBox watchedBox;
    private RatingBar ratingBar;
    private ImageView moviePhoto;
    private EditText Title;
    private EditText Year;
    private EditText Plot;
    private EditText Poster;
    private EditText Type;
    private EditText IMDB_Id;
    private Intent intent;
    private int id;
    private String checked;
    private String movieRating;
    private ImageButton refresh;
    private Button cancel;
    Context context;
    private Button clear;
    private Button Save;
    private String checkedIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_movie);
        toolbar = (Toolbar) findViewById(R.id.appBar);
        setSupportActionBar(toolbar);


        Save = (Button) findViewById(R.id.SaveButton);
        Title = (EditText) findViewById(R.id.TitleEditView);
        Year = (EditText) findViewById(R.id.YearEditView);
        Type = (EditText) findViewById(R.id.typEdiText);
        IMDB_Id = (EditText) findViewById(R.id.imdb_id_ediText);
        Poster = (EditText) findViewById(R.id.poster_ediText);
        Plot = (EditText) findViewById(R.id.PlotEditView);
        ratingBar = (RatingBar) findViewById(R.id.RatingBar);
        watchedBox = (CheckBox) findViewById(R.id.watchedCheckBox);
        thumb = (ImageView) findViewById(R.id.thumb);
        moviePhoto = (ImageView) findViewById(R.id.poster);
        cancel = (Button) findViewById(R.id.CancelButton);
        clear = (Button) findViewById(R.id.ClearButton);


        //getting a movie to edit

        intent = getIntent();
        id = intent.getIntExtra("Id", 0);
        isNewMovie = intent.getBooleanExtra(Constants.INTENT_BOOLEAN_IS_NEW, false);
        if (isNewMovie) {
            if (intent.hasExtra(Constants.INTENT_INTERNET)) {
                String title = intent.getStringExtra(Constants.INTENT_TITLE);
                String year = intent.getStringExtra(Constants.INTENT_YEAR);
                String imdb_id = intent.getStringExtra(Constants.INTENT_IMDB_ID);
                String type = intent.getStringExtra(Constants.INTENT_TYPE);
                String poster = intent.getStringExtra(Constants.INTENT_POSTER);
                intent.getBooleanExtra(Constants.INTENT_BOOLEAN_IS_FROM_INTERNET, true);

                Title.setText(title);
                Year.setText(year);
                IMDB_Id.setText(imdb_id);
                Type.setText(type);
                Poster.setText(poster);


                //task for showing the poster using the poster location


                DownloadImageTask downloadTask = new DownloadImageTask(EditMovie.this);
                String imageUrl = Poster.getText().toString();
                downloadTask.execute(imageUrl);


                //task for retrieving the plot using the imdb-id

                String url = "http://www.omdbapi.com/?i=" + imdb_id + "&plot=short";
                new PlotTask().execute(url);

            }

            //if the movie is from the main Activity

        }
        if (intent.hasExtra("froMain")) {

            String identity = String.valueOf(intent.getIntExtra("Id", 0));
            Movies movie = new Movies();
            DBHandler handler = new DBHandler(EditMovie.this);
            movie = handler.getMovie(identity);
            Title.setText(movie.getTitle());
            Year.setText(movie.getYear());
            IMDB_Id.setText(movie.getImdbID());
            Type.setText(movie.getType());
            Poster.setText(movie.getPoster());
            Plot.setText(movie.getPlot());

            DownloadImageTask downloadTask = new DownloadImageTask(EditMovie.this);
            String imageUrl = Poster.getText().toString();
            downloadTask.execute(imageUrl);


            try {
                if (movie.getWatched().equals("checked")) {
                    watchedBox.setChecked(true);
                    checked = "checked";
                }
            } catch (NullPointerException n) {
                n.printStackTrace();
            }

            //setting thumbs up/down and watched or not..
            if (movie.getRating() != null) {
                movieRating = movie.getRating();

                if (Float.valueOf(movieRating) >= 5) {
                    thumb.setImageResource(R.drawable.thumb_up);
                    ratingBar.setRating(Float.valueOf(movieRating));
                } else {
                    thumb.setImageResource(R.drawable.thumb_down);
                    ratingBar.setRating(Float.valueOf(movieRating));

                }
            }
        }

        //getting result from rating bar
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
//                ratingBar.setNumStars(10);
                movieRating = String.valueOf(rating);
                    Toast.makeText(EditMovie.this, String.valueOf(rating), Toast.LENGTH_SHORT).show();

            }
        });

        //checking if checkBox is Checked or not

        watchedBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (watchedBox.isChecked()) {
                    checked = "checked";
                } else {
                    checked = "notChecked";
                }
            }
        });

        refresh = (ImageButton) findViewById(R.id.loadImage);
        refresh.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                DownloadImageTask downloadTask = new DownloadImageTask(EditMovie.this);
                String imageUrl = Poster.getText().toString();
                downloadTask.execute(imageUrl);

            }
        });


        moviePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent enlarge = new Intent(EditMovie.this, EnlargedPoster.class);
                    enlarge.putExtra("poster", Poster.getText().toString());
                    startActivity(enlarge);
                }catch (NullPointerException n){
                    n.printStackTrace();
                    Toast.makeText(EditMovie.this, "Can't enlarge photo", Toast.LENGTH_SHORT).show();
                }

            }
        });


        //saving movie info and sending it to database and listView at MainActivity

        Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Title.getText()!=null){
                String title = Title.getText().toString();
                String year = Year.getText().toString();
                String imdb_id = IMDB_Id.getText().toString();
                String type = Type.getText().toString();
                String poster = Poster.getText().toString();
                String plot = Plot.getText().toString();

                if (!title.equals("") || !year.equals("")) {

                    if (isNewMovie) {
                        //creating a new notification for adding a new movie..
                        Intent editIntent = new Intent();
                        PendingIntent pendingIntent = PendingIntent.getActivity(EditMovie.this, (int) System.currentTimeMillis(), editIntent, 0);
                        Notification notification = new NotificationCompat.Builder(EditMovie.this)
                                .setContentTitle("Your new movie")
                                .setContentText(title + '\n' + year)
                                .setSmallIcon(R.drawable.film)
                                .setContentIntent(pendingIntent)
                                .setAutoCancel(true)
                                .setContentIntent(pendingIntent)
                                .setVisibility(BIND_ABOVE_CLIENT).build();

                        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                        notificationManager.notify(0, notification);

                    }


                    Intent intent1 = new Intent(EditMovie.this, MainActivity.class);
                    startActivity(intent1);

                    DBHandler handler = new DBHandler(EditMovie.this);
                    if (isNewMovie) {

//                        Bitmap bitmapOrg = BitmapFactory.decodeResource(getResources(),  R.drawable.film);
//                        ByteArrayOutputStream bao = new ByteArrayOutputStream();
//                        bitmapOrg.compress(Bitmap.CompressFormat.JPEG, 100, bao);
//                        byte [] ba = bao.toByteArray();
//                        String ba1=Base64.encodeToString(ba, Base64.DEFAULT);

                        Movies movie = new Movies(title, year, imdb_id, type, poster, plot, movieRating, checked);
                        handler.addMovie(movie);

                    } else {
                        Movies updatedMovie = new Movies(title, year, imdb_id, type, plot, poster, id, movieRating, checked);
                        handler.updateMovie(updatedMovie);
                    }

                    finish();
                    SnackbarManager.show(Snackbar.with(EditMovie.this).text(getResources().getString(R.string.movieAdded)).actionLabel("Undo").actionColor(Color.YELLOW).actionListener(new ActionClickListener() {
                        @Override
                        public void onActionClicked(Snackbar snackbar) {

                        }

                    }));
                }

                } else {


                    AlertDialog.Builder builder = new AlertDialog.Builder(EditMovie.this);
                    builder.setTitle(R.string.error);
                    builder.setMessage(R.string.mustFill);
                    builder.setCancelable(true);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    builder.setNegativeButton("Cancel",null);
                    builder.show();
                }
            }

        });
        //clearing all fields button
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(EditMovie.this);
                builder.setTitle(" Warning ");
                builder.setMessage("by pressing ok data will get lost");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Title.setText(null);
                        Year.setText(null);
                        Plot.setText(null);
                        Poster.setText(null);
                        Type.setText(null);
                        IMDB_Id.setText(null);
                        watchedBox.setChecked(false);
                        ratingBar.setRating(0);
                        SnackbarManager.show(Snackbar.with(EditMovie.this).text(getResources().getString(R.string.deletedSucess)));
                    }
                });
                builder.setNeutralButton("cancel", null);
                builder.setNegativeButton("No", null);
                builder.show();


            }
        });






        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.custom, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.send_by_email) {



                Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
                String[] recipients = new String[]{"", "",};
                emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, recipients);
                emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Your new Movie " );
                emailIntent.putExtra(android.content.Intent.EXTRA_TEXT,   "Title: " + Title.getText().toString()+'\n'+"Year: "+Year.getText().toString()+'\n'+"Plot: "+Plot.getText().toString()+'\n'+"IMDB Id: "+IMDB_Id.getText().toString()+'\n'+"Poster link: "+Poster.getText().toString());
                emailIntent.setType("message/rfc822");
                startActivity(Intent.createChooser(emailIntent, "Send Email"));


        }



        if (id == R.id.send_by_whatsApp) {
                Movies m = new Movies();
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, ""+ "My movie: " + Title.getText().toString());
                sendIntent.setType("text/plain");
                startActivity(sendIntent);


        }
        if (id == R.id.send_by_message) {

            final EditText phoneNum = new EditText(EditMovie.this);

                AlertDialog.Builder messageDialog = new AlertDialog.Builder(EditMovie.this);
                messageDialog.setTitle(" Send by message  ");
                messageDialog.setMessage("please enter a phone number to send to");

                messageDialog.setView(phoneNum);
                messageDialog.setPositiveButton("Send", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String number = phoneNum.getText().toString();
                        SmsManager manager = SmsManager.getDefault();
                        manager.sendTextMessage(number, null, Title.getText().toString(), null, null);
                        SnackbarManager.show(Snackbar.with(EditMovie.this).text(getResources().getString(R.string.sucessfullySent)));
                    }
                });
                messageDialog.setNeutralButton("cancel", null);
                messageDialog.show();


        }

        return super.onOptionsItemSelected(item);
    }


    //an async task for downloading poster
    public class DownloadImageTask extends AsyncTask<String, Integer,Bitmap>
    {

        private Activity mActivity;
        private ProgressDialog mDialog;

        DownloadImageTask(Activity activity) {
            mActivity = activity;
            mDialog = new ProgressDialog(mActivity);
        }
            //Code performing long running operation goes in this method.  When onClick method is executed on click of button,
            // it calls execute method which accepts parameters and automatically calls
            // doInBackground method with the parameters passed.
        protected Bitmap doInBackground(String... urls) {
            Log.d("doInBackground", "starting download of image");
            Bitmap image = downloadImage(urls[0]);
            return image;
        }
        //This method is called before doInBackground method is called.
        protected void onPreExecute() {
            ImageView imageView = (ImageView) mActivity.findViewById(R.id.poster);
            imageView.setImageBitmap(null);
            // Reset the progress bar
            mDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mDialog.setCancelable(true);//case you would like to cancel the dialog
            mDialog.setMessage(getResources().getString(R.string.Loading));
            mDialog.setProgress(0);
            TextView errorMsg = (TextView) mActivity.findViewById(R.id.errorMsg);
            errorMsg.setVisibility(View.INVISIBLE);
        }
            //This method is invoked by calling publishProgress anytime from doInBackground call this method.
        protected void onProgressUpdate(Integer... progress) {
            mDialog.show();
            mDialog.setProgress(progress[0]);
        }
        //This method is called after doInBackground method completes processing.
        // Result from doInBackground is passed to this method.
        protected void onPostExecute(Bitmap result) {
            if (result != null) {
                ImageView imageView = (ImageView)mActivity.findViewById(R.id.poster);
                imageView.setImageBitmap(result);


            }
            else {
                TextView errorMsg = (TextView)mActivity.findViewById(R.id.errorMsg);
                errorMsg.setVisibility(View.VISIBLE);
                errorMsg.setText(getResources().getString(R.string.problemDownloadImage));
            }
            // Close the progress dialog
            mDialog.dismiss();
        }

        private Bitmap downloadImage(String urlString) {
            java.net.URL url;
            try {
                url = new URL(urlString);
                HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
                InputStream is = httpCon.getInputStream();
                int fileLength = httpCon.getContentLength();
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                int nRead, totalBytesRead = 0;
                byte[] data = new byte[2048];
                mDialog.setMax(fileLength);
                // Read the image bytes in chunks of 2048 bytes
                while ((nRead = is.read(data, 0, data.length)) != -1) {
                    buffer.write(data, 0, nRead);
                    totalBytesRead += nRead;
                    publishProgress(totalBytesRead);
                }
                buffer.flush();
                byte[] image = buffer.toByteArray();
                Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
                return bitmap;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

    }
        // An asyncTask for retrieving plot by sending imdb-id
    class PlotTask extends AsyncTask<String,Void,String> {


        @Override
        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(String... strings) {

            String response = sendHttpRequest(strings[0]);

            return response;
        }

        @Override
        protected void onPostExecute(String s) {



                try {
                    JSONObject object = new JSONObject(s);
                    final String plot = object.getString("Plot");

                    EditText Plot = (EditText) findViewById(R.id.PlotEditView);
                    Plot.setText(plot);


                } catch (JSONException e) {
                    e.printStackTrace();
                }

        }



        //This method sends the the HTTP request to the server
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

