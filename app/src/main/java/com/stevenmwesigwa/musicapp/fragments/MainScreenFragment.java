package com.stevenmwesigwa.musicapp.fragments;


import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.stevenmwesigwa.musicapp.R;
import com.stevenmwesigwa.musicapp.Songs;
import com.stevenmwesigwa.musicapp.adapters.MainScreenAdapter;

import java.util.ArrayList;
import java.util.Collections;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainScreenFragment extends Fragment {
    RelativeLayout hiddenBottomBarMainScreen = null;
    ImageButton playPauseButtonMainScreen = null;
    TextView songTitleMainScreen = null;
    // Contains 'RecyclerView' and the 'bottombar'
    RelativeLayout visibleLayout = null;
    // The view that will get displayed when there're no songs
    RelativeLayout noSongsMainScreen = null;
    RecyclerView contentMainRecyclerView = null;
    // Holds the 'context'
    Activity activity = null;
    //Instantiate MainScreenAdapter
    MainScreenAdapter mainScreenAdapter = null;
    ArrayList<Songs> getSongsList = null;
    private int trackPosition = 0;

    public MainScreenFragment() {
        // Required empty public constructor
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getSongsList = getSongsFromDevice();
        SharedPreferences sharedPreferencesEdit2 = activity.getSharedPreferences("action_sort", Context.MODE_PRIVATE);
        String actionSortRecent = sharedPreferencesEdit2.getString("action_sort_recent", "false");
        String actionSortAscending = sharedPreferencesEdit2.getString("action_sort_ascending", "true");
        bottomBarSetup();

        if (getSongsList != null) {
            if (actionSortAscending.equalsIgnoreCase("true")) {
                Collections.sort(getSongsList, Songs.sortBySongTItle);
                mainScreenAdapter = new MainScreenAdapter(getSongsList, activity);
                mainScreenAdapter.notifyDataSetChanged();

            } else if (actionSortRecent.equalsIgnoreCase("true")) {
                Collections.sort(getSongsList, Songs.sortBySongDateAdded);
                mainScreenAdapter = new MainScreenAdapter(getSongsList, activity);
                mainScreenAdapter.notifyDataSetChanged();

            }
        }

        if (getSongsList == null) {
            visibleLayout.setVisibility(View.INVISIBLE);
            noSongsMainScreen.setVisibility(View.VISIBLE);
        } else {
            mainScreenAdapter = new MainScreenAdapter(getSongsList, activity);

            /**
             * Setup LayoutManager - Is responsible for measuring and positioning 'item views' with in a recycler view
             */
            // use a linear layout manager
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(activity);
            contentMainRecyclerView.setLayoutManager(layoutManager);
            contentMainRecyclerView.setItemAnimator(new DefaultItemAnimator());
            /**
             * Set the adapter to the Recycler View so that the Recycler View will get synced with the
             * Adapter.
             */
            contentMainRecyclerView.setAdapter(mainScreenAdapter);
        }

    }


    /**
     * Initialize the contents of the Fragment host's standard options menu.  You
     * should place your menu items in to <var>menu</var>.  For this method
     * to be called, you must have first called {@link #setHasOptionsMenu}.  See
     * {@link Activity#onCreateOptionsMenu(Menu) Activity.onCreateOptionsMenu}
     * for more information.
     *
     * @param menu     The options menu in which you place your items.
     * @param inflater
     * @see #setHasOptionsMenu
     * @see #onPrepareOptionsMenu
     * @see #onOptionsItemSelected
     */
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        //Clear previous menu items
        menu.clear();
        // Create custom menu
        inflater.inflate(R.menu.main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int switcher = item.getItemId();
        if (switcher == R.id.actionSorAscending) {
            SharedPreferences.Editor sharedPreferencesEdit = activity.getSharedPreferences("action_sort", Context.MODE_PRIVATE).edit();
            sharedPreferencesEdit.putString("action_sort_ascending", "true");
            sharedPreferencesEdit.putString("action_sort_recent", "false");
            sharedPreferencesEdit.apply();
            if (getSongsList != null) {
                Collections.sort(getSongsList, Songs.sortBySongTItle);
            }
            mainScreenAdapter.notifyDataSetChanged();
            return false;
        } else if (switcher == R.id.actionSortRecent) {
            SharedPreferences.Editor sharedPreferencesEdit2 = activity.getSharedPreferences("action_sort", Context.MODE_PRIVATE).edit();
            sharedPreferencesEdit2.putString("action_sort_recent", "true");
            sharedPreferencesEdit2.putString("action_sort_ascending", "false");
            sharedPreferencesEdit2.apply();
            if (getSongsList != null) {
                Collections.sort(getSongsList, Songs.sortBySongDateAdded);
            }
            mainScreenAdapter.notifyDataSetChanged();
            return false;

        }
        return false;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity.setTitle("All Songs");
//        activity.getActionBar().setIcon(R.drawable.navigation_allsongs);

//        activity.getActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main_screen, container, false);
        //Display Menu
        setHasOptionsMenu(true);
        hiddenBottomBarMainScreen = view.findViewById(R.id.hiddenBottomBarMainScreen);
        playPauseButtonMainScreen = view.findViewById(R.id.playPauseButtonMainScreen);
        songTitleMainScreen = view.findViewById(R.id.songTitleMainScreen);
        visibleLayout = view.findViewById(R.id.visibleLayout);
        noSongsMainScreen = view.findViewById(R.id.noSongsMainScreen);
        contentMainRecyclerView = view.findViewById(R.id.contentMainRecyclerView);
        setHasOptionsMenu(true);
        activity.setTitle("All Songs");

        return view;
    }


    /**
     * Gets called when the 'MainScreenFragment' is attached to the 'Activity'
     *
     * @param context
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        activity = (Activity) context;
    }

    /**
     * @param activity
     */
    @Override
    public void onAttach(@NonNull Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
    }

    private ArrayList<Songs> getSongsFromDevice() {
        ArrayList songsList = new ArrayList();
        // Create 'ContentResolver' to access the database
        ContentResolver contentResolver = activity.getContentResolver();
        // Create a 'Uri' so that you would fetch a specific song. i.e searching for songs
        Uri songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        // Make a 'Cursor'
        // Use the 'ContentResolver' to query the database
// 'projection' arg is null - because we want to return ALL columns and not a specific one
        // 'queryArgs' arg is null - because we don't want any arguments inside our query
        Cursor songCursor = contentResolver.query(songUri, null, null, null);
//We have to get the columns and data that is fetched only when the cursor is not 'null'
        if ((songCursor != null) && songCursor.moveToFirst()) {
            final int id = songCursor.getColumnIndex(MediaStore.Audio.Media._ID);
            final int title = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            final int artist = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            final int data = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA);
            final int dateAdded = songCursor.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED);
// Get data inside those columns. Add to the 'songsList'
            while (songCursor.moveToNext()) {
                final Long songId = songCursor.getLong(id);
                final String songTitle = songCursor.getString(title);
                final String songArtist = songCursor.getString(artist);
                final String songData = songCursor.getString(data);
                final Long songDateAdded = songCursor.getLong(dateAdded);
                songsList.add(new Songs(songId, songTitle, songArtist, songData, songDateAdded));
            }
        }

        return songsList;
    }

    private void bottomBarSetup() {
        try {
            bottomBarClickHandler();
            songTitleMainScreen.setText(SongPlayingFragment.currentSongHelper.getSongTitle());
// Change text when song is completed
            SongPlayingFragment.mediaPlayer.setOnCompletionListener(
                    view -> {

                        songTitleMainScreen.setText(SongPlayingFragment.currentSongHelper.getSongTitle());
                        //Change Song
                        SongPlayingFragment.onSongComplete();
                    }
            );
// Set up visibility of the 'now playing' bottom bar
            boolean isPaused = !SongPlayingFragment.mediaPlayer.isPlaying() && SongPlayingFragment.mediaPlayer.getCurrentPosition() > 1;
            if (SongPlayingFragment.mediaPlayer.isPlaying() || isPaused) {
                hiddenBottomBarMainScreen.setVisibility(View.VISIBLE);
            } else {
                hiddenBottomBarMainScreen.setVisibility(View.INVISIBLE);

            }
            if (isPaused) {
                playPauseButtonMainScreen.setBackgroundResource(R.drawable.play_icon);
            }


        } catch (Exception e) {

        }

    }

    private void bottomBarClickHandler() {
        hiddenBottomBarMainScreen.setOnClickListener(
                view -> {
                    /*
                     *When a user clicks on the bottom bar OR List item in the Fav Frag RecyclerView, they
                     * get redirected to the "SongPlaying" screen
                     */
                    final SongPlayingFragment songPlayingFragment = new SongPlayingFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("songArtist", SongPlayingFragment.currentSongHelper.getSongArtist());
                    bundle.putString("songTitle", SongPlayingFragment.currentSongHelper.getSongTitle());
                    bundle.putLong("songId", SongPlayingFragment.currentSongHelper.getSongId());
                    bundle.putString("songData", SongPlayingFragment.currentSongHelper.getSongData());
                    bundle.putLong("songDateAdded", SongPlayingFragment.currentSongHelper.getSongDateAdded());
                    bundle.putInt("songPosition", SongPlayingFragment.currentSongHelper.getCurrentPosition());
                    bundle.putParcelableArrayList("songsList", SongPlayingFragment.songsList);

                    /*
                     * Let the 'SongPlayingFragment' know that the trigger that has occurred,
                     * the one that lead to the opening of the "SongPlayingFragment" screen
                     * is done through this "FavoriteFragment"
                     */
                    bundle.putString("favoriteFragBottomBar", "success");

//Link values with the songPlayingFragment
                    songPlayingFragment.setArguments(bundle);

                    getFragmentManager()
                            .beginTransaction()
                            .replace(R.id.detailsFragment, songPlayingFragment)
                            /*
                             * Make fragment not to get destroyed and is pushed below the current
                             *  appearing fragment
                             */
                            .addToBackStack("MainScreenFragment")
                            .commit();

                }
        );

        playPauseButtonMainScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SongPlayingFragment.mediaPlayer.isPlaying()) {
                    SongPlayingFragment.mediaPlayer.pause();
                    trackPosition = SongPlayingFragment.mediaPlayer.getCurrentPosition();
                    playPauseButtonMainScreen.setBackgroundResource(R.drawable.play_icon);
                } else {
                    SongPlayingFragment.mediaPlayer.seekTo(trackPosition);
                    // User wants to resume the song
                    SongPlayingFragment.mediaPlayer.start();
                    playPauseButtonMainScreen.setBackgroundResource(R.drawable.pause_icon);
                }
            }
        });
    }
}
