package com.stevenmwesigwa.musicapp.fragments;


import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
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
import com.stevenmwesigwa.musicapp.adapters.FavoriteScreenAdapter;
import com.stevenmwesigwa.musicapp.databases.EchoDatabase;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class FavoriteFragment extends Fragment {
    private static EchoDatabase echoDatabase = null;
    private Activity activity = null;
    private TextView noFavoritesFavFrag = null;
    private RelativeLayout hiddenBottomBarMainScreen = null;
    private TextView songTitleMainScreen = null;
    private ImageButton playPauseButtonMainScreen = null;
    private RecyclerView favoriteRecyclerFavFrag = null;
    private int trackPosition = 0;
    // Updated lis to be fed into the Adapter
    private ArrayList<Songs> refreshSongListFavFrag = null;
    private ArrayList<Songs> getSongListFromDbFavFrag = null;

    public FavoriteFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_favorite, container, false);

        noFavoritesFavFrag = view.findViewById(R.id.noFavoritesFavFrag);
        hiddenBottomBarMainScreen = view.findViewById(R.id.hiddenBottomBarMainScreen);
        songTitleMainScreen = view.findViewById(R.id.songTitleMainScreen);
        playPauseButtonMainScreen = view.findViewById(R.id.playPauseButtonMainScreen);
        favoriteRecyclerFavFrag = view.findViewById(R.id.favoriteRecyclerFavFrag);
        return view;
    }

    /**
     * Called when a fragment is first attached to its context.
     * {@link #onCreate(Bundle)} will be called after this.
     *
     * @param context
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.activity = (Activity) context;
    }

    /**
     * Called when a fragment is first attached to its activity.
     * {@link #onCreate(Bundle)} will be called after this.
     *
     * @param activity
     * @deprecated See {@link #onAttach(Context)}.
     */
    @Override
    public void onAttach(@NonNull Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
    }

    /**
     * Called to do initial creation of a fragment.  This is called after
     * {@link #onAttach(Activity)} and before
     * {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     *
     * <p>Note that this can be called while the fragment's activity is
     * still in the process of being created.  As such, you can not rely
     * on things like the activity's content view hierarchy being initialized
     * at this point.  If you want to do work once the activity itself is
     * created, see {@link #onActivityCreated(Bundle)}.
     *
     * <p>Any restored child fragments will be created before the base
     * <code>Fragment.onCreate</code> method returns.</p>
     *
     * @param savedInstanceState If the fragment is being re-created from
     *                           a previous saved state, this is the state.
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity.setTitle("Favorites");
    }

    /**
     * Called when the fragment's activity has been created and this
     * fragment's view hierarchy instantiated.  It can be used to do final
     * initialization once these pieces are in place, such as retrieving
     * views or restoring state.  It is also useful for fragments that use
     * {@link #setRetainInstance(boolean)} to retain their instance,
     * as this callback tells the fragment when it is fully associated with
     * the new activity instance.  This is called after {@link #onCreateView}
     * and before {@link #onViewStateRestored(Bundle)}.
     *
     * @param savedInstanceState If the fragment is being re-created from
     *                           a previous saved state, this is the state.
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        echoDatabase = new EchoDatabase(activity);
        displayFavoritesBySearching();
        bottomBarSetup();

        if(SongPlayingFragment.mediaPlayer != null) {
            boolean isPaused = !SongPlayingFragment.mediaPlayer.isPlaying() && SongPlayingFragment.mediaPlayer.getCurrentPosition() > 1;
            if (isPaused) {
                playPauseButtonMainScreen.setBackgroundResource(R.drawable.play_icon);
            }
        }


    }

    /**
     * Called when the fragment is visible to the user and actively running.
     * This is generally
     * tied to {@link Activity#\onResume() Activity.onResume} of the containing
     * Activity's lifecycle.
     */
    @Override
    public void onResume() {
        super.onResume();
    }

    /**
     * Prepare the Fragment host's standard options menu to be displayed.  This is
     * called right before the menu is shown, every time it is shown.  You can
     * use this method to efficiently enable/disable items or otherwise
     * dynamically modify the contents.  See
     * {@link Activity#onPrepareOptionsMenu(Menu) Activity.onPrepareOptionsMenu}
     * for more information.
     *
     * @param menu The options menu as last shown or first initialized by
     *             onCreateOptionsMenu().
     * @see #setHasOptionsMenu
     * @see #onCreateOptionsMenu
     */
    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
    }

    private ArrayList<Songs> getSongsFromDevice() {
        ArrayList<Songs> songsList = new ArrayList<>();
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
                        //Change Song
                        SongPlayingFragment.onSongComplete();
                        // Update song Title in the hiddenBottomBarMainScreen
                        songTitleMainScreen.setText(SongPlayingFragment.currentSongHelper.getSongTitle());
                    }
            );
// Set up visibility of the 'now playing' bottom bar
            Boolean isPaused = !SongPlayingFragment.mediaPlayer.isPlaying() && SongPlayingFragment.mediaPlayer.getCurrentPosition() > 1;
            if (SongPlayingFragment.mediaPlayer.isPlaying() || isPaused) {
                hiddenBottomBarMainScreen.setVisibility(View.VISIBLE);
            } else {
                hiddenBottomBarMainScreen.setVisibility(View.INVISIBLE);
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
                            .addToBackStack("FavoriteFragment")
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

    private void displayFavoritesBySearching() {
        if ((echoDatabase != null) && echoDatabase.rowCount() > 0) {
            refreshSongListFavFrag = new ArrayList<>();
            getSongListFromDbFavFrag = echoDatabase.get();
            ArrayList<Songs> getSongListFromDeviceFavFrag = getSongsFromDevice();

            if (getSongListFromDeviceFavFrag != null && (getSongListFromDbFavFrag != null)) {
                for (int i = 0; i < getSongListFromDeviceFavFrag.size() - 1; i++) {
                    for (int j = 0; j < getSongListFromDbFavFrag.size() - 1; i++) {
                        if (getSongListFromDbFavFrag.get(j).getSongId().equals(getSongListFromDeviceFavFrag.get(i).getSongId())) {
                            refreshSongListFavFrag.add(getSongListFromDbFavFrag.get(j));

                        }

                    }

                }

            } else {
                favoriteRecyclerFavFrag.setVisibility(View.INVISIBLE);
                noFavoritesFavFrag.setVisibility(View.VISIBLE);
            }
            /*
             * If 'songsArrayList' is null, make `RecyclerView` disappear
             * and make 'noFavoritesFavFrag' appear.
             * Else set up Adapter so that all songs in the database
             * get displayed on the screen.
             */
            if (refreshSongListFavFrag == null) {
                favoriteRecyclerFavFrag.setVisibility(View.INVISIBLE);
                noFavoritesFavFrag.setVisibility(View.VISIBLE);
            } else {
                FavoriteScreenAdapter favoriteScreenAdapter = new FavoriteScreenAdapter(refreshSongListFavFrag, activity);
                /**
                 * Setup LayoutManager - Is responsible for measuring and positioning 'item views' with in a recycler view
                 */
                // use a linear layout manager
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(activity);
                favoriteRecyclerFavFrag.setLayoutManager(layoutManager);
                favoriteRecyclerFavFrag.setItemAnimator(new DefaultItemAnimator());
                favoriteRecyclerFavFrag.setHasFixedSize(true);
                /**
                 * Set the adapter to the Recycler View so that the Recycler View will get synced with the
                 * Adapter.
                 */
                favoriteRecyclerFavFrag.setAdapter(favoriteScreenAdapter);
            }


        } else {
            favoriteRecyclerFavFrag.setVisibility(View.INVISIBLE);
            noFavoritesFavFrag.setVisibility(View.VISIBLE);
        }
    }
}
