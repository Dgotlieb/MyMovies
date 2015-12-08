package com.example.daniel.mymovies;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class EnlargedPoster extends AppCompatActivity {
    private ImageView enlargedPhoto;
    private Intent enlarged;
    private String photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enlarged_poster);


        enlarged = getIntent();
        photo = enlarged.getStringExtra("poster");

        EnlargePhotoTask downloadTask = new EnlargePhotoTask(EnlargedPoster.this);
        downloadTask.execute(photo);

        enlargedPhoto = (ImageView)findViewById(R.id.enlarged_pic);
        enlargedPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            finish();
            }
        });


    }
}
class EnlargePhotoTask extends AsyncTask<String, Integer,Bitmap>
{

    private Activity mActivity;
    private ProgressDialog mDialog;

    EnlargePhotoTask(Activity activity) {
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
        ImageView imageView = (ImageView) mActivity.findViewById(R.id.enlarged_pic);
        imageView.setImageBitmap(null);
        // Reset the progress bar
        mDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mDialog.setCancelable(true);//case you would like to cancel the dialog
        mDialog.setMessage(mActivity.getResources().getString(R.string.Loading));
        mDialog.setProgress(0);
        TextView errorMsg = (TextView) mActivity.findViewById(R.id.errorTextView);
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
        ImageView imageView= null;
        try {


            if (result != null) {
                 imageView = (ImageView) mActivity.findViewById(R.id.enlarged_pic);
                imageView.setImageBitmap(result);
            } else {
                TextView errorMsg = (TextView) mActivity.findViewById(R.id.errorMsg);
                errorMsg.setVisibility(View.VISIBLE);
                errorMsg.setText(mActivity.getResources().getString(R.string.problemDownloadImage));
            }
            // Close the progress dialog
            mDialog.dismiss();
        }catch (NullPointerException e){
            e.printStackTrace();
        }
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