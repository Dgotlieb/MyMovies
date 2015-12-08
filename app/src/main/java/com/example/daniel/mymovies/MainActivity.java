package com.example.daniel.mymovies;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.cocosw.bottomsheet.BottomSheet;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.listeners.ActionClickListener;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;
import com.txusballesteros.bubbles.BubbleLayout;
import com.txusballesteros.bubbles.BubblesManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import me.drakeet.materialdialog.MaterialDialog;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private ArrayList<Movies> moviesList;
    private ArrayAdapter<Movies> moviesArrayAdapter;
    private boolean isNewMovie = true;
    private int moviePosition;
    private SharedPreferences colorsPrefrences;
    private SharedPreferences languagePreferences;
    private Toolbar toolbar;
    private SwipeRefreshLayout SRL;
    private com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton fab;
    private ListView listView;
//    private ImageButton ToolBarSearch;
    private ImageButton ToolBarDeleteAll;
    private ImageButton ToolBarExit;
    private NavigationFragment  drawerFragment;
    private BubblesManager bubblesManager;
    private BubbleLayout bubbleView;
    private int flagBubble=1;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



//set the default language to english
        change("en");
        setContentView(R.layout.main);



//declaring on a the floating button
        ImageView mainFab = new ImageView(MainActivity.this);
        mainFab.setImageResource(R.drawable.film);

        fab = new com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton.Builder(this)
                .setContentView(mainFab).
                        build();
//refresh layout to update listView
        SRL = (SwipeRefreshLayout)findViewById(R.id.swipeLayout);
        SRL.setOnRefreshListener(MainActivity.this);
        if (SRL.isRefreshing()){
            SRL.setRefreshing(false);
        }

        ImageView addFab = new ImageView(MainActivity.this);
        addFab.setImageResource(R.drawable.add_button);
        ImageView refreahFab = new ImageView(MainActivity.this);
        refreahFab.setImageResource(R.drawable.refresh);
        ImageView sortFab = new ImageView(MainActivity.this);
        sortFab.setImageResource(R.drawable.sortaz);

        //sub action buttons

        SubActionButton.Builder itemBuilder = new SubActionButton.Builder(MainActivity.this);
        SubActionButton button_add= itemBuilder.setContentView(addFab).build();
        SubActionButton button_refresh= itemBuilder.setContentView(refreahFab).build();
        final SubActionButton button_sort= itemBuilder.setContentView(sortFab).build();
        FloatingActionMenu actionMenu = new FloatingActionMenu.Builder(this)
                .addSubActionView(button_add)
                .addSubActionView(button_refresh)
        .addSubActionView(button_sort).attachTo(fab).build();


        //replacing the default toolbar in mine
        toolbar = (Toolbar)findViewById(R.id.appBar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_drawer);


        //declaring navigation drawer (fragment)


        drawerFragment = (NavigationFragment)getSupportFragmentManager().findFragmentById(R.id.navigation_fragment);
        drawerFragment.setup(R.id.navigation_fragment,(DrawerLayout)findViewById(R.id.drawer_layout),toolbar);

        //colors&languge shared Prefrences
        //asking the user to pick background color to show up in next runs

        settings();

        moviesList = new ArrayList<Movies>();
         listView = (ListView) findViewById(R.id.listView);
//         ToolBarSearch = (ImageButton)findViewById(R.id.toolBarSearch);
         ToolBarDeleteAll = (ImageButton)findViewById(R.id.toolBarDelete);
         ToolBarExit = (ImageButton)findViewById(R.id.toolBarExit);


                //loading everything that is at database
               // moviesArrayAdapter.notifyDataSetChanged();
            cursorMethod();
            moviesArrayAdapter = new ArrayAdapter<Movies>(this, R.layout.row, moviesList);
            listView.setAdapter(moviesArrayAdapter);



        button_sort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Movies mov = new Movies();
                DBHandler handler = new DBHandler(MainActivity.this);
                moviesList = handler.showAllMoviesArrayListAndSort();

                moviesArrayAdapter = new ArrayAdapter<Movies>(MainActivity.this, R.layout.row, moviesList);
                listView.setAdapter(moviesArrayAdapter);

            }
            });


        //add movie button who gives the options: manually/internet

        button_add.setOnClickListener(new View.OnClickListener() {
             MaterialDialog mMaterialDialog = new MaterialDialog(MainActivity.this);

            @Override
            public void onClick(View v) {

                mMaterialDialog.setTitle("MaterialDialog");
                mMaterialDialog.setPositiveButton(getResources().getString(R.string.Manually), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, EditMovie.class);
                        intent.putExtra(Constants.INTENT_BOOLEAN_IS_NEW, true);
                        intent.putExtra("manual", "manual");
                        startActivity(intent);
                        mMaterialDialog.dismiss();


                    }
                });
                mMaterialDialog.setNegativeButton(getResources().getString(R.string.InternetAdd), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent internetAdd = new Intent(MainActivity.this, InternetMovie.class);
                        startActivity(internetAdd);
                        mMaterialDialog.dismiss();
                    }

                });

                mMaterialDialog.show();
                mMaterialDialog.setTitle(getResources().getString(R.string.addMovieDialog));
                mMaterialDialog.show();
                mMaterialDialog.setMessage(getResources().getString(R.string.dialogNessage));

            }
        });


        //delete all your movies button

        ToolBarDeleteAll.setOnClickListener(new View.OnClickListener() {
            MaterialDialog mMaterialDialog = new MaterialDialog(MainActivity.this);

            @Override
            public void onClick(View v) {
                mMaterialDialog.setTitle(getResources().getString(R.string.deleteAllMovies));
                mMaterialDialog.setPositiveButton(getResources().getString(R.string.DeleteAllButton), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DBHandler handler = new DBHandler(MainActivity.this);
                        handler.removeAllMovies();
                        moviesList.clear();
                        mMaterialDialog.dismiss();
                        moviesArrayAdapter.notifyDataSetChanged();




                    }
                });
                mMaterialDialog.setNegativeButton(getResources().getString(R.string.cancel), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mMaterialDialog.dismiss();
                    }
                });



                mMaterialDialog.show();

                mMaterialDialog.show();
                mMaterialDialog.setMessage(getResources().getString(R.string.sureDeleteAll));




            }
        });

        ToolBarExit.setOnClickListener(new View.OnClickListener() {
            MaterialDialog mMaterialDialog = new MaterialDialog(MainActivity.this);

            @Override
            public void onClick(View v) {
                mMaterialDialog.setTitle(getResources().getString(R.string.SureToExit));
                mMaterialDialog.setMessage("");
                mMaterialDialog.setPositiveButton(getResources().getString(R.string.Exit), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        System.exit(0);
                    }
                });
                mMaterialDialog.setNegativeButton(getResources().getString(R.string.cancel), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mMaterialDialog.dismiss();
                    }
                });


                mMaterialDialog.show();

                mMaterialDialog.show();


            }
        });

        //refresh your movie list button- checks through cursor which movies exist

        button_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                cursorMethod();
            }
        });

        // edit each item by clicking on it
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                moviePosition = position;
                Intent intent = new Intent(MainActivity.this, EditMovie.class);
                Movies movie = moviesList.get(position);
                intent.putExtra("Id", movie.getId());
                intent.putExtra(Constants.INTENT_BOOLEAN_IS_NEW,false);
                intent.putExtra("froMain","froMain");
                startActivity(intent);
            }
        });

        //opening bottom sheet menu by clicking long click on item
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, final long id) {

                final Movies movie = moviesList.get(position);

                new BottomSheet.Builder(MainActivity.this).title("options").sheet(R.menu.bottom_menu).listener(new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case R.id.menu_cancel:
                                break;

                            //send movie by email
                            case R.id.menu_send_by_email:
                                String emailMessage = moviesList.get(position).toString();
                                Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
                                String[] recipients = new String[]{"", "",};
                                emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, recipients);
                                emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "New Movie ");
                                emailIntent.putExtra(android.content.Intent.EXTRA_TEXT,   "Title: " + movie.getTitle()+'\n'+"Year: "+movie.getYear()+'\n'+"Plot: "+movie.getPlot()+'\n'+"IMDB Id: "+movie.getImdbID()+'\n'+"Poster link: "+movie.getPoster()+ "Rating: " +movie.getRating());
                                emailIntent.setType("message/rfc822");
                                startActivity(Intent.createChooser(emailIntent, "Send Email"));
                                break;
                            //send movie by whatsapp
                            case R.id.menu_send_by_whatsapp:
                                String whatsAppMessage = moviesList.get(position).toString();
                                Intent sendIntent = new Intent();
                                sendIntent.setAction(Intent.ACTION_SEND);
                                sendIntent.putExtra(Intent.EXTRA_TEXT, ""+ "My movie: " + whatsAppMessage);
                                sendIntent.setType("text/plain");
                                startActivity(sendIntent);
                                break;
                            //send movie by message
                            case R.id.menu_send_by_message:
                                AlertDialog.Builder sms = new AlertDialog.Builder(MainActivity.this);
                                final EditText numToSend = new EditText(MainActivity.this);
                                sms.setTitle("send Movie by sms");
                                sms.setMessage("please enter a number to send to");
                                sms.setView(numToSend);
                                sms.setPositiveButton("send", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        String message = moviesList.get(position).toString();
                                        SmsManager manager = SmsManager.getDefault();
                                        manager.sendTextMessage(numToSend.getText().toString(), null, message, null, null);
                                        SnackbarManager.show(Snackbar.with(MainActivity.this).text(getResources().getString(R.string.sucessfullySent)));
                                    }
                                });
                                sms.setNeutralButton("cancel", null);
                                sms.show();

                                break;
                            //send movie to edit movie activity
                            case R.id.menu_edit:
                                moviePosition = position;
                                moviePosition = position;
                                Intent intent = new Intent(MainActivity.this, EditMovie.class);
                                Movies movie = moviesList.get(position);
                                intent.putExtra("Id", movie.getId());
                                intent.putExtra(Constants.INTENT_BOOLEAN_IS_NEW,false);
                                intent.putExtra("froMain","froMain");
                                startActivity(intent);
                                break;
                            //delete movie from list
                            case R.id.menu_delete:

                                new Movies().setYear(moviesList.get(position).toString());
                                DBHandler handler = new DBHandler(MainActivity.this);
                                int selected_id = moviesList.get(position).getId();
                                handler.removeMovie(selected_id);
                                moviesList.remove(position);
                                moviesArrayAdapter.notifyDataSetChanged();
                                SnackbarManager.show(Snackbar.with(MainActivity.this).text(getResources().getString(R.string.deletedSucess)).actionLabel("undo").actionColor(Color.RED).actionListener(new ActionClickListener() {
                                    @Override
                                    public void onActionClicked(Snackbar snackbar) {

                                    }
                                }));
                                break;
                        }
                    }
                }).show();

                final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle(" Operation ");
                builder.setMessage("what would you like to do?");
                builder.setNeutralButton("cancel", null);
                builder.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final AlertDialog.Builder b = new AlertDialog.Builder(MainActivity.this);
                        b.setTitle("warning");
                        b.setMessage("by pressing ok data will be deleted!");
                        b.setNeutralButton("cancel", null);
                        b.setPositiveButton("delete anyways", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DBHandler handler = new DBHandler(MainActivity.this);
                                int selected_id = moviesList.get(position).getId();
                                handler.removeMovie(selected_id);
                                moviesList.remove(position);
                                moviesArrayAdapter.notifyDataSetChanged();
                                SnackbarManager.show(Snackbar.with(MainActivity.this).text(getResources().getString(R.string.deletedSucess)).actionLabel("undo").actionColor(Color.RED).actionListener(new ActionClickListener() {
                                    @Override
                                    public void onActionClicked(Snackbar snackbar) {

                                    }
                                }));
                            }
                        });
                    }

                });



                return false;
            }
        });
    }

//    color and language prefs
    public void settings(){

        colorsPrefrences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        languagePreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);

        //asking the user to pick background color to show up in next runs
        boolean isFirst = colorsPrefrences.getBoolean("First_time", true);
        if (isFirst) {

            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle(getResources().getString(R.string.backgroundTitle));
            builder.setMessage(getResources().getString(R.string.whichColor));
            final LinearLayout ll = (LinearLayout) findViewById(R.id.linearId);
            builder.setPositiveButton(getResources().getString(R.string.blue), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {


                    ll.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.MaterialBlue));
                    SharedPreferences.Editor edit = colorsPrefrences.edit();
                    edit.putInt("color", 1);

                    edit.apply();

                }
            });
            builder.setNeutralButton(getResources().getString(R.string.green), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ll.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.MaterialGreen));
                    SharedPreferences.Editor edit = colorsPrefrences.edit();
                    edit.putInt("color", 2);

                    edit.apply();
                }
            });

            builder.setNegativeButton(getResources().getString(R.string.red), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ll.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.MaterialRed));
                    SharedPreferences.Editor edit = colorsPrefrences.edit();
                    edit.putInt("color", 3);

                    edit.apply();
                }
            });
            builder.show();



            SharedPreferences.Editor edit = colorsPrefrences.edit();

            edit.putBoolean("First_time", false);

            edit.apply();

            SnackbarManager.show(Snackbar.with(MainActivity.this).text(getResources().getString(R.string.colorsChange)).duration(2000));
        }
        int colorInt = colorsPrefrences.getInt("color", 0);
        int languageInt = languagePreferences.getInt("language", 0);
        LinearLayout ll = (LinearLayout) findViewById(R.id.linearId);

        if (colorInt == 1) {
            ll.setBackgroundColor(Color.BLUE);
        }
        if (colorInt == 2) {
            ll.setBackgroundColor(Color.GREEN);
        }
        if (colorInt == 3) {
            ll.setBackgroundColor(Color.RED);
        }
        if (languageInt == 8) {
            change("iw");
        }

        if (languageInt == 9) {
            change("en");
        }

        SharedPreferences.Editor edit2 = languagePreferences.edit();

        edit2.apply();
    }


    //build the bubble, set x y on screen + setting trash layout so user can remove bubble

    protected void bubbleCreator(){


        bubblesManager = new BubblesManager.Builder(this).setTrashLayout(R.layout.trash)
                .build();
        bubblesManager.initialize();

        bubbleView = (BubbleLayout) LayoutInflater
                .from(MainActivity.this).inflate(R.layout.bubble, null);
        bubblesManager.addBubble(bubbleView, 20, 20);

    }

    @Override
    protected void onDestroy() {
        try {
            if (flagBubble==0) {
                bubbleCreator();
                flagBubble=1;
            }
        }catch (IllegalArgumentException a){
            a.printStackTrace();
            Log.d("buuble","not working");
        }
        super.onDestroy();
    }

    //checks if user pressed home button in order to start bubble
    @Override
    protected void onUserLeaveHint() {
        try {
            if (flagBubble==1) {
                bubbleCreator();
                flagBubble=0;
            }
        }catch (IllegalArgumentException a){
            a.printStackTrace();
            Log.d("buuble","not working");
        }
        super.onUserLeaveHint();

    }



    //loading your menu file
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
        //checks which menu option you choose and return value
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final  LinearLayout ll = (LinearLayout)findViewById(R.id.linearId);

         SharedPreferences.Editor edit = colorsPrefrences.edit();
         SharedPreferences.Editor edit2 = languagePreferences.edit();


        switch (item.getItemId()) {

                //color change...
            case R.id.colorsChangeBlue:
                ll.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.MaterialBlue));
                SnackbarManager.show(Snackbar.with(MainActivity.this).text(getResources().getString(R.string.colorsChange)).duration(2000));
                edit.putInt("color", 1);
                edit.apply();
                break;

            case R.id.colorsChangeGreen:
                ll.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.MaterialGreen));
                SnackbarManager.show(Snackbar.with(MainActivity.this).text(getResources().getString(R.string.colorsChange)).duration(2000));
                edit.putInt("color", 2);
                edit.apply();
                break;
            case R.id.colorsChangeRed:
                ll.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.MaterialRed));
                SnackbarManager.show(Snackbar.with(MainActivity.this).text(getResources().getString(R.string.colorsChange)).duration(2000));
                edit.putInt("color", 3);
                edit.apply();
                break;
            case R.id.colorsChangeWhite:
                ll.setBackgroundColor(Color.WHITE);
                SnackbarManager.show(Snackbar.with(MainActivity.this).text(getResources().getString(R.string.colorsChange)).duration(2000));
                edit.putInt("color", 0);
                edit.apply();
                break;
            case R.id.exit:
                System.exit(0);
                break;

            //languages change..
            case R.id.hebrew:
                change("iw");

                SnackbarManager.show(Snackbar.with(MainActivity.this).text(getResources().getString(R.string.languageChange)).duration(2000));

                edit2.putInt("language", 8);
                edit2.apply();
                recreate();

                break;

            case R.id.english:

                change("en");

                SnackbarManager.show(Snackbar.with(MainActivity.this).text(getResources().getString(R.string.languageChange)).duration(2000));

                edit2.putInt("language", 9);
                edit2.apply();
                recreate();

                break;


        }


        int languageInt = languagePreferences.getInt("language", 0);
        int colorInt = colorsPrefrences.getInt("color", 0);

        if (colorInt == 1) {
            ll.setBackgroundColor(ContextCompat.getColor(MainActivity.this,R.color.MaterialBlue));
        }
        if (colorInt == 2) {
            ll.setBackgroundColor(ContextCompat.getColor(MainActivity.this,R.color.MaterialGreen));
        }
        if (colorInt == 3) {
            ll.setBackgroundColor(ContextCompat.getColor(MainActivity.this,R.color.MaterialRed));
        }
        if (colorInt == 0) {
            ll.setBackgroundColor(ContextCompat.getColor(MainActivity.this,R.color.White));
        }

        if (languageInt == 8) {
            change("iw");

        }

        if (languageInt == 9) {
            change("en");
        }
        return super.onOptionsItemSelected(item);

    }

        //changes language
    public void  change(String lang) {
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);

    }

    //refresh list by pulling down
    @Override
    public void onRefresh() {
        cursorMethod();
        SRL.setRefreshing(false);
            }


    public void cursorMethod(){
        DBHandler handler = new DBHandler(MainActivity.this);
        Cursor cursor = handler.showAllMovies();
        moviesList.clear();

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
    }
}
