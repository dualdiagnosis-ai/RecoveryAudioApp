package com.stevenmwesigwa.musicapp.fragments;


import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import java.util.Optional;

public class FavoriteFragment extends Fragment {
    private EchoDatabase echoDatabase = null;
    private TextView noFavoritesFavFrag = null;
    private RelativeLayout hiddenBottomBarMainScreen = null;
    private TextView songTitleMainScreen = null;
    private ImageButton playPauseButtonMainScreen = null;
    private RecyclerView favoriteRecyclerFavFrag = null;
    private int trackPosition = 0;
    private ArrayList<Songs> getSongListFromDbFavFrag = null;

    public FavoriteFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorite, container, false);

        noFavoritesFavFrag = view.findViewById(R.id.noFavoritesFavFrag);
        hiddenBottomBarMainScreen = view.findViewById(R.id.hiddenBottomBarMainScreen);
        songTitleMainScreen = view.findViewById(R.id.songTitleMainScreen);
        playPauseButtonMainScreen = view.findViewById(R.id.playPauseButtonMainScreen);
        favoriteRecyclerFavFrag = view.findViewById(R.id.favoriteRecyclerFavFrag);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle("Favorites");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        echoDatabase = new EchoDatabase(getActivity());
        displayFavoritesBySearching();
        bottomBarSetup();

        if (SongPlayingFragment.mediaPlayer != null) {
            boolean isPaused = !SongPlayingFragment.mediaPlayer.isPlaying() && SongPlayingFragment.mediaPlayer.getCurrentPosition() > 1;
            if (isPaused) {
                playPauseButtonMainScreen.setBackgroundResource(R.drawable.play_icon);
            }
        }
    }

    private ArrayList<Songs> getSongsFromDevice() {
        ArrayList<Songs> songsList = new ArrayList<>();
        // Create 'ContentResolver' to access the database
        ContentResolver contentResolver = getActivity().getContentResolver();
        // Create a 'Uri' so that you would fetch a specific song. i.e searching for songs
        Uri songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        /*
         * Make a 'Cursor'
         * Use the 'ContentResolver' to query the database
         * 'projection' arg is null - because we want to return ALL columns and not a specific one
         * 'queryArgs' arg is null - because we don't want any arguments inside our query
         */
        Cursor songCursor = contentResolver.query(songUri, null, null, null);
        // We have to get the columns and data that is fetched only when the cursor is not 'null'
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
            songTitleMainScreen.setText(SongPlayingFragment.getCurrentSongHelper().getSongTitle());
            // Change text when song is completed
            SongPlayingFragment.mediaPlayer.setOnCompletionListener(
                    view -> {
                        // Update song Title in the hiddenBottomBarMainScreen
                        songTitleMainScreen.setText(SongPlayingFragment.getCurrentSongHelper().getSongTitle());
                    }
            );
            // Set up visibility of the 'now playing' bottom bar
            boolean isPaused = !SongPlayingFragment.mediaPlayer.isPlaying() && SongPlayingFragment.mediaPlayer.getCurrentPosition() > 1;
            if (SongPlayingFragment.mediaPlayer.isPlaying() || isPaused) {
                hiddenBottomBarMainScreen.setVisibility(View.VISIBLE);
            } else {
                hiddenBottomBarMainScreen.setVisibility(View.INVISIBLE);
            }

        } catch (Exception e) {
            e.printStackTrace();
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
                    bundle.putString("songArtist", SongPlayingFragment.getCurrentSongHelper().getSongArtist());
                    bundle.putString("songTitle", SongPlayingFragment.getCurrentSongHelper().getSongTitle());
                    bundle.putLong("songId", SongPlayingFragment.getCurrentSongHelper().getSongId());
                    bundle.putString("songData", SongPlayingFragment.getCurrentSongHelper().getSongData());
                    bundle.putLong("songDateAdded", SongPlayingFragment.getCurrentSongHelper().getSongDateAdded());
                    bundle.putInt("songPosition", SongPlayingFragment.getCurrentSongHelper().getCurrentPosition());
                    bundle.putParcelableArrayList("songsList", SongPlayingFragment.getSongsList());

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

        playPauseButtonMainScreen.setOnClickListener(v -> {
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
        });
    }

    private void displayFavoritesBySearching() {
        // Updated lis to be fed into the Adapter
        ArrayList<Songs> refreshSongListFavFrag;
        if ((echoDatabase != null) && echoDatabase.rowCount() > 0) {
            refreshSongListFavFrag = new ArrayList<>();
            getSongListFromDbFavFrag = echoDatabase.get();
            ArrayList<Songs> getSongListFromDeviceFavFrag = getSongsFromDevice();

            if (getSongListFromDeviceFavFrag != null && (getSongListFromDbFavFrag != null)) {
                for (int j = 0; j < getSongListFromDbFavFrag.size(); j++) {
                    final int dbSongPosition = j;
                    Optional<Songs> hasSong = getSongListFromDeviceFavFrag.stream()
                            .filter(e -> e.getSongId().equals(getSongListFromDbFavFrag.get(dbSongPosition).getSongId()))
                            .findAny();
                    if (hasSong.isPresent()) {
                        refreshSongListFavFrag.add(getSongListFromDbFavFrag.get(dbSongPosition));
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
            if (refreshSongListFavFrag.size() == 0) {
                favoriteRecyclerFavFrag.setVisibility(View.INVISIBLE);
                noFavoritesFavFrag.setVisibility(View.VISIBLE);
            } else {
                FavoriteScreenAdapter favoriteScreenAdapter = new FavoriteScreenAdapter(refreshSongListFavFrag, getActivity());
                // Setup LayoutManager - Is responsible for measuring and positioning 'item views' with in a recycler view
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
                favoriteRecyclerFavFrag.setLayoutManager(layoutManager);
                favoriteRecyclerFavFrag.setItemAnimator(new DefaultItemAnimator());
                favoriteRecyclerFavFrag.setHasFixedSize(true);
                /**
                 * Set the adapter to the Recycler View so that the Recycler View will get synced
                 * with the Adapter.
                 */
                favoriteRecyclerFavFrag.setAdapter(favoriteScreenAdapter);
            }

        } else {
            favoriteRecyclerFavFrag.setVisibility(View.INVISIBLE);
            noFavoritesFavFrag.setVisibility(View.VISIBLE);
        }
    }
}
