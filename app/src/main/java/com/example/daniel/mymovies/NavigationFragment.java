package com.example.daniel.mymovies;


import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.nispok.snackbar.SnackbarManager;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class NavigationFragment extends Fragment {

    private RecyclerView recyclerView;

    private ActionBarDrawerToggle drawerToggle;
    private DrawerLayout drawerLayout;
    private RecyclerAdapter adapter;
    private boolean userLearnedDrawer;
    private boolean fromSavedInstanceState;
    private View containerView;
    public static final String PREF_FILE_NAME = "fragmentFrefs";
    public static final String KEY_USER_LEARNED_DRAWER = "userLearnedDrawer";
    public NavigationFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userLearnedDrawer= Boolean.valueOf(readFromPreferences(getActivity(), KEY_USER_LEARNED_DRAWER, "false"));
        if(savedInstanceState!=null){
            fromSavedInstanceState=true;

        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View layout = inflater.inflate(R.layout.fragment_navigation, container, false);
        recyclerView =(RecyclerView) layout.findViewById(R.id.RecyclerFragment);
        adapter = new RecyclerAdapter(getActivity(),getData());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                switch (position) {
                    case 0:
                        Intent imdb = new Intent(Intent.ACTION_VIEW);
                        imdb.setData(Uri.parse("http://www.imdb.com"));
                        startActivity(imdb);
                        break;
                    case 1:
                        Intent youtube = new Intent(Intent.ACTION_VIEW);
                        youtube.setData(Uri.parse("http://www.youtube.com"));
                        startActivity(youtube);
                        break;

                }

            }

            @Override
            public void onLongClick(View view, int position) {
                SnackbarManager.show(com.nispok.snackbar.Snackbar.with(getContext()).text(String.valueOf(position)));

            }
        }));
        return layout;
    }



    public static List<Information> getData(){

        List<Information> data= new ArrayList<>();
        int[] icons = {R.drawable.imdb,R.drawable.youtube};
        String[] titles = {"IMDB","YouTube"};
        for (int i = 0; i<titles.length&& i<icons.length;i++){
            Information current = new Information();
            current.iconId=icons[i];
            current.title= titles[i];
            data.add(current);
        }
        return data;
    }




    public void setup(int fragmentId, DrawerLayout drawer, Toolbar toolbar){
        containerView= getActivity().findViewById(fragmentId);
    drawerLayout = drawer;
        drawerToggle = new ActionBarDrawerToggle(getActivity(),drawer,toolbar,R.string.drawer_open,R.string.drawer_close){
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
               if(!userLearnedDrawer){
                   userLearnedDrawer=true;
                   saveToPreferences(getActivity(),KEY_USER_LEARNED_DRAWER,userLearnedDrawer+"");
               }


                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                getActivity().invalidateOptionsMenu();
                if(!userLearnedDrawer&& !fromSavedInstanceState){
                    drawerLayout.openDrawer(containerView);
                    drawerLayout.post(new Runnable() {
                        @Override
                        public void run() {
                            drawerToggle.syncState();
                        }
                    });
                }


            }
        };
        drawerLayout.setDrawerListener(drawerToggle);
    }
    public static void saveToPreferences(Context context, String PreferenceName, String PreferenceValue){
        SharedPreferences sharedPreferences= context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PreferenceName,PreferenceValue);
        editor.apply();
    }
    public static String readFromPreferences(Context context, String PreferenceName, String DefaultValue){
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_FILE_NAME,context.MODE_PRIVATE);
        return sharedPreferences.getString(PreferenceName,DefaultValue);
    }

    static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener{

        private GestureDetector gestureDetector;
        private ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ClickListener clickListener){
            this.clickListener = clickListener;
        gestureDetector = new GestureDetector(context,new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                View child= recyclerView.findChildViewUnder(e.getX(), e.getY());
                if (child!=null&&clickListener!=null){
                    clickListener.onLongClick(child,recyclerView.getChildAdapterPosition(child));
                }
            }
        });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View child= rv.findChildViewUnder(e.getX(), e.getY());
            if(child!=null&& clickListener!=null&&gestureDetector.onTouchEvent(e)){
                clickListener.onClick(child,rv.getChildAdapterPosition(child));
            }

            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {

        }
        public static interface ClickListener{
            public void onClick(View view,int position);
            public void onLongClick(View view,int position);
        }
    }


}
