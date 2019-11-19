package com.stevenmwesigwa.musicapp.fragments;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.net.sip.SipSession;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cleveroad.audiovisualization.AudioVisualization;
import com.cleveroad.audiovisualization.DbmHandler;
import com.cleveroad.audiovisualization.GLAudioVisualizationView;
import com.cleveroad.audiovisualization.VisualizerDbmHandler;
import com.stevenmwesigwa.musicapp.CurrentSongHelper;
import com.stevenmwesigwa.musicapp.R;
import com.stevenmwesigwa.musicapp.Songs;
import com.stevenmwesigwa.musicapp.databases.EchoDatabase;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.TimeUnit;


/**
 * A simple {@link Fragment} subclass.
 */
public class SongPlayingFragment extends Fragment {
    private static String My_PREFS_NAME = "ShakeFeature";
    private Float accelaration = 0f;
    private Float accelarationCurrent = 0f;
    private Float accelarationLast = 0f;
    public static Activity activity;
    // Controls playback of audio or video files
    public static MediaPlayer mediaPlayer;

    // Lets you access your device's sensors
    public static SensorManager sensorManager = null;

    //Enables you receive notifications from the 'Sensor Manager'
    // when sensor values are changed.
    public static SensorEventListener sensorEventListener = null;
    private RelativeLayout songInformationNowPlaying = null;
    public static TextView songTitleNowPlaying = null;
    public static TextView songArtistNowPlaying = null;
    public static RelativeLayout seekBarLayoutNowPlaying = null;
    public static SeekBar seekBarNowPlaying = null;
    public static TextView startTimeSeekBarNowPlaying = null;
    public static TextView endTimeSeekBarNowPlaying = null;
    public static RelativeLayout controlPanelNowPlaying = null;
    public static ImageButton shuffleButtonNowPlaying = null;
    public static ImageButton previousButtonNowPlaying = null;
    public static ImageButton playPauseButtonNowPlaying = null;
    public static ImageButton nextButtonNowPlaying = null;
    public static ImageButton loopButtonNowPlaying = null;
    public static ImageButton favoriteIconNowPlaying = null;
    public static EchoDatabase echoDatabaseFavorite = null;

    private AudioVisualization audioVisualization = null;
    private GLAudioVisualizationView glAudioVisualizationView = null;

    public static String MY_PREFS_SHUFFLE = "Shuffle feature";
    public static String MY_PREFS_LOOP = "Loop feature";

    public static Runnable updateSongTime = new Runnable() {
        @Override
        public void run() {
            final Handler handler = new Handler();
            int getCurrentPosition = mediaPlayer.getCurrentPosition();
            startTimeSeekBarNowPlaying.setText(String.format(Locale.US, "%d:%d",
                    TimeUnit.MILLISECONDS.toMinutes(getCurrentPosition),
                    TimeUnit.MILLISECONDS.toSeconds(getCurrentPosition) - TimeUnit.MILLISECONDS.toSeconds(TimeUnit.MILLISECONDS.toMinutes(getCurrentPosition))));
            handler.postDelayed(this, 1000);
        }
    };


    public static CurrentSongHelper currentSongHelper = null;

    public static Integer currentPosition = null;
    public static ArrayList<Songs> songsList = null;

    public SongPlayingFragment() {
        // Required empty public constructor
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

        sensorManager = (SensorManager) activity.getSystemService(Context.SENSOR_SERVICE);
        accelaration = 0.0f;
accelarationCurrent = SensorManager.GRAVITY_EARTH;
accelarationLast = SensorManager.GRAVITY_EARTH;
bindShakeListener();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_song_playing, container, false);
        //Display Menu
        setHasOptionsMenu(true);
        songInformationNowPlaying = view.findViewById(R.id.songInformationNowPlaying);
        songTitleNowPlaying = view.findViewById(R.id.songTitleNowPlaying);
        seekBarLayoutNowPlaying = view.findViewById(R.id.seekBarLayoutNowPlaying);
        seekBarNowPlaying = view.findViewById(R.id.seekBarNowPlaying);
        startTimeSeekBarNowPlaying = view.findViewById(R.id.startTimeSeekBarNowPlaying);
        endTimeSeekBarNowPlaying = view.findViewById(R.id.endTimeSeekBarNowPlaying);
        controlPanelNowPlaying = view.findViewById(R.id.controlPanelNowPlaying);
        shuffleButtonNowPlaying = view.findViewById(R.id.shuffleButtonNowPlaying);
        previousButtonNowPlaying = view.findViewById(R.id.previousButtonNowPlaying);
        playPauseButtonNowPlaying = view.findViewById(R.id.playPauseButtonNowPlaying);
        nextButtonNowPlaying = view.findViewById(R.id.nextButtonNowPlaying);
        loopButtonNowPlaying = view.findViewById(R.id.loopButtonNowPlaying);
        favoriteIconNowPlaying = view.findViewById(R.id.favoriteIconNowPlaying);
        glAudioVisualizationView = view.findViewById(R.id.visualizerViewNowPlaying);
        return view;
    }

    /**
     * Called immediately after {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}
     * has returned, but before any saved state has been restored in to the view.
     * This gives subclasses a chance to initialize themselves once
     * they know their view hierarchy has been completely created.  The fragment's
     * view hierarchy is not however attached to its parent at this point.
     *
     * @param view               The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        audioVisualization = glAudioVisualizationView;
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
        activity = (Activity) context;
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
        echoDatabaseFavorite = new EchoDatabase(activity);
        currentSongHelper = new CurrentSongHelper();
        currentSongHelper.setPlaying(true);
        currentSongHelper.setShuffleFeatureEnabled(false);
        currentSongHelper.setLoopFeatureEnabled(false);
        String songData = null;
        String songTitle = null;
        String songArtist = null;
        Long songId = null;
        Long songDateAdded = null;
        try {
            songData = getArguments().getString("songData");
            currentSongHelper.setSongData(songData);
            songArtist = getArguments().getString("songArtist");
            currentSongHelper.setSongArtist(songArtist);
            songTitle = getArguments().getString("songTitle");
            currentSongHelper.setSongArtist(songTitle);
            songDateAdded = getArguments().getLong("songDateAdded");
            currentSongHelper.setSongDateAdded(songDateAdded);
            currentPosition = getArguments().getInt("songPosition");
            currentSongHelper.setCurrentPosition(currentPosition);
            songId = getArguments().getLong("songId");
            currentSongHelper.setSongId(songId);
            songsList = getArguments().getParcelableArrayList("songsList");
            updateTextViews(currentSongHelper.getSongTitle(), currentSongHelper.getSongArtist());
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Object fromFavBottomBarScreen = getArguments().get("favoriteFragBottomBar");
            if (fromFavBottomBarScreen != null) {
                // To maintain the consistency of the instance
                mediaPlayer = FavoriteFragment.mediaPlayerFavFrag;
            } else {
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                try {
                    mediaPlayer.setDataSource(activity, Uri.parse(songData));
                    mediaPlayer.prepare();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mediaPlayer.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        processInformation(mediaPlayer);
        if (currentSongHelper.isPlaying()) {
            playPauseButtonNowPlaying.setBackgroundResource(R.drawable.pause_icon);
        } else {
            playPauseButtonNowPlaying.setBackgroundResource(R.drawable.play_icon);

        }

        mediaPlayer.setOnCompletionListener(
                view -> {
                    onSongComplete();
                }
        );

        clickHandler();

        // set audio visualization handler. This will REPLACE previously set speech recognizer handler
        VisualizerDbmHandler vizualizerHandler = DbmHandler.Factory.newVisualizerHandler(activity, 0);
        audioVisualization.linkTo(vizualizerHandler);

// For shuffle
        SharedPreferences prefsForShuffle = activity.getSharedPreferences(SongPlayingFragment.MY_PREFS_SHUFFLE, Context.MODE_PRIVATE);
        Boolean isShuffleAllowed = prefsForShuffle.getBoolean("feature", false);

        if (isShuffleAllowed) {
            currentSongHelper.setShuffleFeatureEnabled(true);
            currentSongHelper.setLoopFeatureEnabled(false);
            shuffleButtonNowPlaying.setBackgroundResource(R.drawable.shuffle_icon);
            loopButtonNowPlaying.setBackgroundResource(R.drawable.loop_white_icon);

        } else {
            currentSongHelper.setShuffleFeatureEnabled(false);
            shuffleButtonNowPlaying.setBackgroundResource(R.drawable.shuffle_white_icon);
//            loopButtonNowPlaying.setBackgroundResource(R.drawable.loop_white_icon);
        }

        // For Loop

        SharedPreferences prefsForLoop = activity.getSharedPreferences(SongPlayingFragment.MY_PREFS_LOOP, Context.MODE_PRIVATE);
        Boolean isLoopAllowed = prefsForLoop.getBoolean("feature", false);

        if (isLoopAllowed) {
            currentSongHelper.setLoopFeatureEnabled(true);
            currentSongHelper.setShuffleFeatureEnabled(false);
            shuffleButtonNowPlaying.setBackgroundResource(R.drawable.shuffle_white_icon);
            loopButtonNowPlaying.setBackgroundResource(R.drawable.loop_icon);

        } else {
            currentSongHelper.setLoopFeatureEnabled(false);
            loopButtonNowPlaying.setBackgroundResource(R.drawable.loop_white_icon);
//            loopButtonNowPlaying.setBackgroundResource(R.drawable.loop_white_icon);
        }
        changeFavoriteIconNowPlaying();
    }

    /*
     * If song is playing and is among favorite songs
     * change 'favorite icon'
     */
    public static void changeFavoriteIconNowPlaying() {

        if (echoDatabaseFavorite.ifSongIdExists(currentSongHelper.getSongId().intValue())) {
            favoriteIconNowPlaying.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.favorite_on));
        } else {
            favoriteIconNowPlaying.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.favorite_off));
        }

    }

    /*
        You must always call onPause method to pause visualization
        and stop wasting CPU resources for computations in vain.
        As soon as your view appears in sight of user, call onResume.
    */
    @Override
    public void onResume() {
        super.onResume();
        audioVisualization.onResume();
        //Register listener for the sensor manager
        sensorManager.registerListener(sensorEventListener,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onPause() {
        audioVisualization.onPause();
        super.onPause();
        /*
        * Unregister the Listener
         */
        sensorManager.unregisterListener(sensorEventListener);
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
        inflater.inflate(R.menu.song_playing_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);

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
        //Display a certain menu item on a specific screen
        final MenuItem menuItem = menu.findItem(R.id.actionRedirect);
        menuItem.setVisible(true);
    }

    /**
     * This hook is called whenever an item in your options menu is selected.
     * The default implementation simply returns false to have the normal
     * processing happen (calling the item's Runnable or sending a message to
     * its Handler as appropriate).  You can use this method for any items
     * for which you would like to do processing without those other
     * facilities.
     *
     * <p>Derived classes should call through to the base class for it to
     * perform the default menu handling.
     *
     * @param item The menu item that was selected.
     * @return boolean Return false to allow normal menu processing to
     * proceed, true to consume it here.
     * @see #onCreateOptionsMenu
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.actionRedirect) {
activity.onBackPressed();
return false;
        }
        return false;

    }

    /* When user leaves screen with audio visualization view,
                 don't forget to free resources and call release() method.
             */
    @Override
    public void onDestroyView() {
        audioVisualization.release();
        super.onDestroyView();
    }

    public static void onSongComplete() {
        if (currentSongHelper.isShuffleFeatureEnabled()) {
            playNext("PlayNextLikeNormalShuffle");
            currentSongHelper.setPlaying(true);
        } else {
            if (currentSongHelper.isLoopFeatureEnabled()) {
                currentSongHelper.setPlaying(true);
                Songs nextSong = songsList.get(currentPosition);
                currentSongHelper.setSongData(nextSong.getSongData());
                currentSongHelper.setSongArtist(nextSong.getSongArtist());
                currentSongHelper.setSongId(nextSong.getSongId());
                currentSongHelper.setSongTitle(nextSong.getSongTitle());
                currentSongHelper.setSongDateAdded(nextSong.getSongDateAdded());
                currentSongHelper.setCurrentPosition(currentPosition);
                updateTextViews(currentSongHelper.getSongTitle(), currentSongHelper.getSongArtist());

                mediaPlayer.reset();

                try {
                    mediaPlayer.setDataSource(activity, Uri.parse(currentSongHelper.getSongData()));
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                    processInformation(mediaPlayer);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                playNext("PlayNextNormal");
                currentSongHelper.setPlaying(true);
            }

            changeFavoriteIconNowPlaying();
        }
    }

    private void clickHandler() {

        favoriteIconNowPlaying.setOnClickListener(

                view -> {
                    int songId = currentSongHelper.getSongId().intValue();
                    String songArtist = currentSongHelper.getSongArtist();
                    String songTitle = currentSongHelper.getSongTitle();
                    String songPath = currentSongHelper.getSongData();

                    /*
                     * If it is already favorite, then that means
                     * the user wants it deleted.
                     */
                    if (echoDatabaseFavorite.ifSongIdExists(songId)) {

                        favoriteIconNowPlaying.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.favorite_off));
                        echoDatabaseFavorite.delete(songId);
                        Toast.makeText(activity, "Removed from Favorites List", Toast.LENGTH_SHORT).show();
                    } else {
                        favoriteIconNowPlaying.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.favorite_on));
                        echoDatabaseFavorite.insert(songId, songArtist, songTitle, songPath);
                        Toast.makeText(activity, "Added to Favorites List", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        shuffleButtonNowPlaying.setOnClickListener(
                view -> {
                    SharedPreferences.Editor editShuffle = activity.getSharedPreferences(SongPlayingFragment.MY_PREFS_SHUFFLE, Context.MODE_PRIVATE).edit();
                    SharedPreferences.Editor editLoop = activity.getSharedPreferences(SongPlayingFragment.MY_PREFS_LOOP, Context.MODE_PRIVATE).edit();

                    if (currentSongHelper.isShuffleFeatureEnabled()) {
                        shuffleButtonNowPlaying.setBackgroundResource(R.drawable.shuffle_white_icon);
                        currentSongHelper.setShuffleFeatureEnabled(false);
                        editShuffle.putBoolean("feature", false);
                        editShuffle.apply();

                    } else {
                        currentSongHelper.setShuffleFeatureEnabled(true);
                        currentSongHelper.setLoopFeatureEnabled(false);
                        shuffleButtonNowPlaying.setBackgroundResource(R.drawable.shuffle_icon);
                        loopButtonNowPlaying.setBackgroundResource(R.drawable.loop_white_icon);
                        editShuffle.putBoolean("feature", true);
                        editShuffle.apply();
                        editLoop.putBoolean("feature", false);
                        editLoop.apply();
                    }
                }

        );
        nextButtonNowPlaying.setOnClickListener(

                view -> {
                    currentSongHelper.setPlaying(true);
                    playPauseButtonNowPlaying.setBackgroundResource(R.drawable.pause_icon);
                    if (currentSongHelper.isShuffleFeatureEnabled()) {
                        playNext("PlayNextLikeNormalShuffle");
                    } else {
                        playNext("PlayNextNormal");

                    }
                }
        );
        previousButtonNowPlaying.setOnClickListener(

                view -> {
                    currentSongHelper.setPlaying(true);
                    if (currentSongHelper.isLoopFeatureEnabled()) {
                        loopButtonNowPlaying.setBackgroundResource(R.drawable.loop_white_icon);
                    }
                    playPrevious();
                }

        );
        loopButtonNowPlaying.setOnClickListener(

                view -> {
                    SharedPreferences.Editor editShuffle = activity.getSharedPreferences(SongPlayingFragment.MY_PREFS_SHUFFLE, Context.MODE_PRIVATE).edit();
                    SharedPreferences.Editor editLoop = activity.getSharedPreferences(SongPlayingFragment.MY_PREFS_LOOP, Context.MODE_PRIVATE).edit();

                    if (currentSongHelper.isLoopFeatureEnabled()) {
                        currentSongHelper.setLoopFeatureEnabled(false);
                        loopButtonNowPlaying.setBackgroundResource(R.drawable.loop_white_icon);
                        editShuffle.putBoolean("feature", false);
                        editShuffle.apply();

                    } else {
                        currentSongHelper.setLoopFeatureEnabled(true);
                        currentSongHelper.setShuffleFeatureEnabled(false);
                        loopButtonNowPlaying.setBackgroundResource(R.drawable.loop_icon);
                        shuffleButtonNowPlaying.setBackgroundResource(R.drawable.shuffle_white_icon);
                        editShuffle.putBoolean("feature", false);
                        editShuffle.apply();
                        editLoop.putBoolean("feature", true);
                        editLoop.apply();
                    }
                }

        );
        playPauseButtonNowPlaying.setOnClickListener(


                view -> {
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                        currentSongHelper.setPlaying(false);
                        playPauseButtonNowPlaying.setBackgroundResource(R.drawable.play_icon);
                    } else {
                        mediaPlayer.start();
                        currentSongHelper.setPlaying(true);
                        playPauseButtonNowPlaying.setBackgroundResource(R.drawable.pause_icon);
                    }

                }
        );
    }

    public static void playNext(String check) {
        if (check.equalsIgnoreCase("PlayNextNormal")) {
            currentPosition++;
        } else if (check.equalsIgnoreCase("PlayNextLikeNormalShuffle")) {
            Random random = new Random();
            currentPosition = random.nextInt(songsList.size() + 1);

        } else if (currentPosition == songsList.size()) {
            currentPosition = 0;

        }
        currentSongHelper.setLoopFeatureEnabled(false);
        Songs nextSong = songsList.get(currentPosition);
        currentSongHelper.setSongData(nextSong.getSongData());
        currentSongHelper.setSongArtist(nextSong.getSongArtist());
        currentSongHelper.setSongId(nextSong.getSongId());
        currentSongHelper.setSongTitle(nextSong.getSongTitle());
        currentSongHelper.setSongDateAdded(nextSong.getSongDateAdded());
        currentSongHelper.setCurrentPosition(currentPosition);
        updateTextViews(currentSongHelper.getSongTitle(), currentSongHelper.getSongArtist());
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(activity, Uri.parse(currentSongHelper.getSongData()));
            mediaPlayer.prepare();
            mediaPlayer.start();
            processInformation(mediaPlayer);
        } catch (Exception e) {
            e.printStackTrace();
        }

        changeFavoriteIconNowPlaying();
    }

    private void playPrevious() {
        currentPosition--;
        if (currentPosition == -1) {
            currentPosition = 0;
        } else if (currentSongHelper.isPlaying()) {
            playPauseButtonNowPlaying.setBackgroundResource(R.drawable.pause_icon);
        } else {
            playPauseButtonNowPlaying.setBackgroundResource(R.drawable.play_icon);
        }
        currentSongHelper.setLoopFeatureEnabled(false);
        Songs nextSong = songsList.get(currentPosition);
        currentSongHelper.setSongData(nextSong.getSongData());
        currentSongHelper.setSongArtist(nextSong.getSongArtist());
        currentSongHelper.setSongId(nextSong.getSongId());
        currentSongHelper.setSongTitle(nextSong.getSongTitle());
        currentSongHelper.setSongDateAdded(nextSong.getSongDateAdded());
        currentSongHelper.setCurrentPosition(currentPosition);
        updateTextViews(currentSongHelper.getSongTitle(), currentSongHelper.getSongArtist());

        mediaPlayer.reset();

        try {
            mediaPlayer.setDataSource(activity, Uri.parse(currentSongHelper.getSongData()));
            mediaPlayer.prepare();
            mediaPlayer.start();
            processInformation(mediaPlayer);

        } catch (Exception e) {
            e.printStackTrace();
        }

        changeFavoriteIconNowPlaying();

    }

    public static void updateTextViews(String songTitleNowPlaying, String songArtistNowPlaying) {
        SongPlayingFragment.songTitleNowPlaying.setText(songTitleNowPlaying);
        SongPlayingFragment.songArtistNowPlaying.setText(songArtistNowPlaying);
    }

    public static void processInformation(MediaPlayer mediaPlayer) {
        int finalTime = mediaPlayer.getDuration();
        int startTime = mediaPlayer.getCurrentPosition();
        // Set max seek ar length
        seekBarNowPlaying.setMax(finalTime);
        startTimeSeekBarNowPlaying.setText(String.format(Locale.US, "%d:%d",
                TimeUnit.MILLISECONDS.toMinutes(startTime),
                TimeUnit.MILLISECONDS.toSeconds(startTime) - TimeUnit.MILLISECONDS.toSeconds(TimeUnit.MILLISECONDS.toMinutes(startTime))));

        endTimeSeekBarNowPlaying.setText(String.format(Locale.US, "%d:%d",
                TimeUnit.MILLISECONDS.toMinutes(finalTime),
                TimeUnit.MILLISECONDS.toSeconds(finalTime) - TimeUnit.MILLISECONDS.toSeconds(TimeUnit.MILLISECONDS.toMinutes(finalTime))));


        seekBarNowPlaying.setProgress(startTime);
        final Handler handler = new Handler();
        handler.postDelayed(updateSongTime, 1000);
    }


    private void bindShakeListener() {
sensorEventListener = new SensorEventListener() {
    @Override
    public void onSensorChanged(SensorEvent event) {
final float xFirst  =event.values[0];
final float ySecond  =event.values[1];
final float zThird  =event.values[2];
accelarationLast = accelarationCurrent;
accelarationCurrent = (float) Math.sqrt(xFirst*xFirst + ySecond*ySecond + zThird*zThird);
final float delta = accelarationCurrent - accelarationLast;
accelaration = accelaration * 0.9f + delta;

if(accelaration > 12) {
    final SharedPreferences sharedPreferencesEditor = activity.getSharedPreferences(My_PREFS_NAME, Context.MODE_PRIVATE);
    final boolean isAllowed = sharedPreferencesEditor.getBoolean("feature", false);
    if(isAllowed) {
        playNext("PlayNextNormal");

    }
    }


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
};
    }

    public AudioVisualization getAudioVisualization() {
        return audioVisualization;
    }

    public void setAudioVisualization(AudioVisualization audioVisualization) {
        this.audioVisualization = audioVisualization;
    }
}
