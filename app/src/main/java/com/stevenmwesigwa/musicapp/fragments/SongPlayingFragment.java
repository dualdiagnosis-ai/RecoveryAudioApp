package com.stevenmwesigwa.musicapp.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

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

public class SongPlayingFragment extends Fragment {
    // Controls playback of audio or video files
    public static MediaPlayer mediaPlayer;
    private static String MY_PREFS_SHUFFLE = "Shuffle feature";
    private static String MY_PREFS_LOOP = "Loop feature";
    private static CurrentSongHelper currentSongHelper = new CurrentSongHelper();
    private static ArrayList<Songs> songsList = null;
    // Lets you access your device's sensors
    private SensorManager sensorManager = null;
    //Enables you receive notifications from the 'Sensor Manager'
    // when sensor values are changed.
    private SensorEventListener sensorEventListener = null;
    private TextView songTitleNowPlaying = null;
    private TextView songArtistNowPlaying = null;
    private SeekBar seekBarNowPlaying = null;
    private TextView startTimeSeekBarNowPlaying = null;
    private TextView endTimeSeekBarNowPlaying = null;
    private ImageButton shuffleButtonNowPlaying = null;
    private ImageButton previousButtonNowPlaying = null;
    private ImageButton playPauseButtonNowPlaying = null;
    private ImageButton nextButtonNowPlaying = null;
    private ImageButton loopButtonNowPlaying = null;
    private ImageView favoriteIconNowPlaying = null;
    private EchoDatabase echoDatabaseFavorite = null;
    private Runnable updateSongTime = new Runnable() {
        @Override
        public void run() {
            final Handler handler = new Handler();
            int getDuration = mediaPlayer.getDuration();
            int getCurrentPosition = mediaPlayer.getCurrentPosition();
            startTimeSeekBarNowPlaying.setText(String.format(Locale.US, "%d:%d",
                    TimeUnit.MILLISECONDS.toMinutes(getCurrentPosition),
                    TimeUnit.MILLISECONDS.toSeconds(getCurrentPosition) - TimeUnit.MILLISECONDS.toSeconds(TimeUnit.MILLISECONDS.toMinutes(getCurrentPosition))));
            handler.postDelayed(this, 1000);

            // Set max seek ar length
            seekBarNowPlaying.setMax(getDuration);
            seekBarNowPlaying.setProgress(getCurrentPosition);
        }
    };
    private Integer currentPosition = null;
    private static String My_PREFS_NAME = "ShakeFeature";
    private Float accelaration = 0f;
    private Float accelarationCurrent = 0f;
    private Float accelarationLast = 0f;
    private AudioVisualization audioVisualization = null;
    private GLAudioVisualizationView glAudioVisualizationView = null;

    public SongPlayingFragment() {
        // Required empty public constructor
    }

    static CurrentSongHelper getCurrentSongHelper() {
        return currentSongHelper;
    }

    static ArrayList<Songs> getSongsList() {
        return songsList;
    }

    /*
     * If song is playing and is among favorite songs
     * change 'favorite icon'
     */
    private void changeFavoriteIconNowPlaying() {

        if (echoDatabaseFavorite.ifSongIdExists(currentSongHelper.getSongId().intValue())) {
            favoriteIconNowPlaying.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.favorite_on));
        } else {
            favoriteIconNowPlaying.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.favorite_off));
        }

    }

    private void onSongComplete() {
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
                    mediaPlayer.setDataSource(getActivity(), Uri.parse(currentSongHelper.getSongData()));
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                    updateSeekBarStartEndTime(mediaPlayer);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                /*
                 * When the song has finished / completed playing
                 * "getCurrentPosition()" was a couple of milliseconds off very
                 * close to the "getDuration".
                 *  The difference seemed to be consistent on a couple of songs which was
                 *  5 milliseconds for my case. For safely I've decided to go with 30 milliseconds OFF.
                 */
                int playerOnCompleteMillisecondsOffset = 30;

                if (mediaPlayer.getCurrentPosition() >= mediaPlayer.getDuration() - playerOnCompleteMillisecondsOffset) {
                    playNext("PlayNextNormal");
                    currentSongHelper.setPlaying(true);
                }
            }
            changeFavoriteIconNowPlaying();
        }

    }

    private void displayToastMessage(String toastMessage, int toastDuration) {
        Toast toast = Toast.makeText(getContext(), toastMessage, toastDuration);
        toast.setGravity(Gravity.TOP | Gravity.LEFT, 0, 0);
        toast.show();
    }


    private void playNext(String check) {
        int nextSongPosition = currentPosition + 1;
        currentPosition++;
        if ((songsList.size() == 1)) {
            currentPosition = 0;
        }
        if (nextSongPosition == songsList.size() - 1) {
            displayToastMessage("You have reached the end of the playlist", Toast.LENGTH_SHORT);
            currentPosition = songsList.size() - 1;
        } else if (nextSongPosition > songsList.size() - 1) {
            // If song List is finished / Complete, move to first song in the list
            currentPosition = 0;
        } else if (check.equalsIgnoreCase("PlayNextNormal")) {
            currentPosition = nextSongPosition;
        }
        if (check.equalsIgnoreCase("PlayNextLikeNormalShuffle")) {
            Random random = new Random();
            int nextRandomSongPosition = random.nextInt(songsList.size() + 1);
            if (nextRandomSongPosition < songsList.size()) {
                currentPosition = nextRandomSongPosition;
            }
        }

        changePlayPauseButton(currentSongHelper);

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
            mediaPlayer.setDataSource(getActivity(), Uri.parse(currentSongHelper.getSongData()));
            mediaPlayer.prepare();
            mediaPlayer.start();
            updateSeekBarStartEndTime(mediaPlayer);
            if (nextSongPosition > songsList.size() - 1) {
                // If song List is finished / Complete, Force Pause current Song to let user
                // Know that the list is Finished.
                playPauseButtonNowPlaying.performClick();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        changeFavoriteIconNowPlaying();

    }

    private void updateTextViews(String songTitleNowPlaying, String songArtistNowPlaying) {
        this.songTitleNowPlaying.setText(songTitleNowPlaying);
        this.songArtistNowPlaying.setText(songArtistNowPlaying);
    }

    private void updateSeekBarStartEndTime(MediaPlayer mediaPlayer) {
        int finalTime = mediaPlayer.getDuration();
        int startTime = mediaPlayer.getCurrentPosition();
        startTimeSeekBarNowPlaying.setText(String.format(Locale.US, "%d:%d",
                TimeUnit.MILLISECONDS.toMinutes(startTime),
                TimeUnit.MILLISECONDS.toSeconds(startTime) - TimeUnit.MILLISECONDS.toSeconds(TimeUnit.MILLISECONDS.toMinutes(startTime))));

        endTimeSeekBarNowPlaying.setText(String.format(Locale.US, "%d:%d",
                TimeUnit.MILLISECONDS.toMinutes(finalTime),
                TimeUnit.MILLISECONDS.toSeconds(finalTime) - TimeUnit.MILLISECONDS.toSeconds(TimeUnit.MILLISECONDS.toMinutes(finalTime))));

        final Handler handler = new Handler();
        handler.postDelayed(updateSongTime, 1000);
    }

    private void changePlayPauseButton(CurrentSongHelper currentSongHelper) {
        if (currentSongHelper.isPlaying()) {
            playPauseButtonNowPlaying.setBackgroundResource(R.drawable.pause_icon);
        } else {
            playPauseButtonNowPlaying.setBackgroundResource(R.drawable.play_icon);

        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle("Song Playing");

        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
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
        songTitleNowPlaying = view.findViewById(R.id.songTitleNowPlaying);
        songArtistNowPlaying = view.findViewById(R.id.songArtistNowPlaying);
        seekBarNowPlaying = view.findViewById(R.id.seekBarNowPlaying);
        startTimeSeekBarNowPlaying = view.findViewById(R.id.startTimeSeekBarNowPlaying);
        endTimeSeekBarNowPlaying = view.findViewById(R.id.endTimeSeekBarNowPlaying);
        shuffleButtonNowPlaying = view.findViewById(R.id.shuffleButtonNowPlaying);
        previousButtonNowPlaying = view.findViewById(R.id.previousButtonNowPlaying);
        playPauseButtonNowPlaying = view.findViewById(R.id.playPauseButtonNowPlaying);
        nextButtonNowPlaying = view.findViewById(R.id.nextButtonNowPlaying);
        loopButtonNowPlaying = view.findViewById(R.id.loopButtonNowPlaying);
        favoriteIconNowPlaying = view.findViewById(R.id.favoriteIconNowPlaying);
        glAudioVisualizationView = view.findViewById(R.id.visualizerViewNowPlaying);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        audioVisualization = glAudioVisualizationView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        echoDatabaseFavorite = new EchoDatabase(getActivity());
        currentSongHelper = new CurrentSongHelper();
        currentSongHelper.setPlaying(true);
        currentSongHelper.setShuffleFeatureEnabled(false);
        currentSongHelper.setLoopFeatureEnabled(false);
        String songData = null;
        String songTitle;
        String songArtist;
        Long songId;
        Long songDateAdded;
        try {
            songData = getArguments().getString("songData");
            currentSongHelper.setSongData(songData);
            songArtist = getArguments().getString("songArtist");
            currentSongHelper.setSongArtist(songArtist);
            songTitle = getArguments().getString("songTitle");
            currentSongHelper.setSongTitle(songTitle);
            songDateAdded = getArguments().getLong("songDateAdded");
            currentSongHelper.setSongDateAdded(songDateAdded);
            currentPosition = getArguments().getInt("songPosition");
            currentSongHelper.setCurrentPosition(currentPosition);
            songId = getArguments().getLong("songId");
            currentSongHelper.setSongId(songId);
            songsList = getArguments().getParcelableArrayList("songsList");
            updateTextViews(songTitle, songArtist);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Object fromFavBottomBarScreen = getArguments().get("favoriteFragBottomBar");
            if (fromFavBottomBarScreen == null) {
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                try {
                    mediaPlayer.setDataSource(getActivity(), Uri.parse(songData));
                    mediaPlayer.prepare();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mediaPlayer.reset();
                mediaPlayer.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        updateSeekBarStartEndTime(mediaPlayer);
        changePlayPauseButton(currentSongHelper);

        if (mediaPlayer != null) {
            boolean isPaused = !SongPlayingFragment.mediaPlayer.isPlaying() && SongPlayingFragment.mediaPlayer.getCurrentPosition() > 1;

            if (isPaused) {
                playPauseButtonNowPlaying.setBackgroundResource(R.drawable.play_icon);
            }
            try {
                mediaPlayer.setDataSource(getActivity(), Uri.parse(currentSongHelper.getSongData()));
                mediaPlayer.prepare();
                mediaPlayer.start();
                currentSongHelper.setPlaying(true);
                updateSeekBarStartEndTime(mediaPlayer);
                changePlayPauseButton(currentSongHelper);
                mediaPlayer.setOnCompletionListener(
                        view -> onSongComplete()
                );
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        clickHandler();

        // set audio visualization handler. This will REPLACE previously set speech recognizer handler
        VisualizerDbmHandler vizualizerHandler = DbmHandler.Factory.newVisualizerHandler(getActivity(), 0);
        audioVisualization.linkTo(vizualizerHandler);

        // For shuffle
        SharedPreferences prefsForShuffle = getActivity().getSharedPreferences(SongPlayingFragment.MY_PREFS_SHUFFLE, Context.MODE_PRIVATE);
        boolean isShuffleAllowed = prefsForShuffle.getBoolean("feature", false);

        if (isShuffleAllowed) {
            currentSongHelper.setShuffleFeatureEnabled(true);
            currentSongHelper.setLoopFeatureEnabled(false);
            shuffleButtonNowPlaying.setBackgroundResource(R.drawable.shuffle_icon);
            loopButtonNowPlaying.setBackgroundResource(R.drawable.loop_white_icon);

        } else {
            currentSongHelper.setShuffleFeatureEnabled(false);
            shuffleButtonNowPlaying.setBackgroundResource(R.drawable.shuffle_white_icon);
        }
        // For loop
        SharedPreferences prefsForLoop = getActivity().getSharedPreferences(SongPlayingFragment.MY_PREFS_LOOP, Context.MODE_PRIVATE);
        boolean isLoopAllowed = prefsForLoop.getBoolean("feature", false);

        if (isLoopAllowed) {
            currentSongHelper.setLoopFeatureEnabled(true);
            currentSongHelper.setShuffleFeatureEnabled(false);
            shuffleButtonNowPlaying.setBackgroundResource(R.drawable.shuffle_white_icon);
            loopButtonNowPlaying.setBackgroundResource(R.drawable.loop_icon);

        } else {
            currentSongHelper.setLoopFeatureEnabled(false);
            loopButtonNowPlaying.setBackgroundResource(R.drawable.loop_white_icon);
        }
        changeFavoriteIconNowPlaying();
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

        // Unregister the Listener
        sensorManager.unregisterListener(sensorEventListener);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        //Clear previous menu items
        menu.clear();
        // Create custom menu
        inflater.inflate(R.menu.song_playing_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);

    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        //Display a certain menu item on a specific screen
        final MenuItem menuItem = menu.findItem(R.id.actionRedirect);
        menuItem.setVisible(true);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.actionRedirect) {
            getActivity().onBackPressed();
            return false;
        }
        return false;
    }

    /* When user leaves screen with audio visualization view,
     * don't forget to free resources and call release() method.
     */
    @Override
    public void onDestroyView() {
        audioVisualization.release();
        super.onDestroyView();
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

                        favoriteIconNowPlaying.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.favorite_off));
                        echoDatabaseFavorite.delete(songId);
                        Toast.makeText(getActivity(), "Removed from Favorites List", Toast.LENGTH_SHORT).show();
                    } else {
                        favoriteIconNowPlaying.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.favorite_on));
                        echoDatabaseFavorite.insert(songId, songArtist, songTitle, songPath);
                        Toast.makeText(getActivity(), "Added to Favorites List", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        shuffleButtonNowPlaying.setOnClickListener(
                view -> {
                    SharedPreferences.Editor editShuffle = getActivity().getSharedPreferences(SongPlayingFragment.MY_PREFS_SHUFFLE, Context.MODE_PRIVATE).edit();
                    SharedPreferences.Editor editLoop = getActivity().getSharedPreferences(SongPlayingFragment.MY_PREFS_LOOP, Context.MODE_PRIVATE).edit();

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
                    SharedPreferences.Editor editShuffle = getActivity().getSharedPreferences(SongPlayingFragment.MY_PREFS_SHUFFLE, Context.MODE_PRIVATE).edit();
                    SharedPreferences.Editor editLoop = getActivity().getSharedPreferences(SongPlayingFragment.MY_PREFS_LOOP, Context.MODE_PRIVATE).edit();

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

        seekBarNowPlaying.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    int seekBarProgress = 0;

                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        seekBarProgress = progress;
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        seekBar.setProgress(seekBarProgress);
                        mediaPlayer.seekTo(seekBarProgress);
                    }
                }
        );
    }

    private void playPrevious() {
        int previousSongPosition = currentPosition - 1;
        currentPosition--;
        if (previousSongPosition < 1) {
            displayToastMessage("You have reached the beginning of the playlist", Toast.LENGTH_SHORT);
            currentPosition = 0;
        }
        changePlayPauseButton(currentSongHelper);
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
            mediaPlayer.setDataSource(getActivity(), Uri.parse(currentSongHelper.getSongData()));
            mediaPlayer.prepare();
            mediaPlayer.start();
            updateSeekBarStartEndTime(mediaPlayer);

        } catch (Exception e) {
            e.printStackTrace();
        }
        changeFavoriteIconNowPlaying();

    }

    private void bindShakeListener() {
        sensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                final float xFirst = event.values[0];
                final float ySecond = event.values[1];
                final float zThird = event.values[2];
                accelarationLast = accelarationCurrent;
                accelarationCurrent = (float) Math.sqrt(xFirst * xFirst + ySecond * ySecond + zThird * zThird);
                final float delta = accelarationCurrent - accelarationLast;
                accelaration = accelaration * 0.9f + delta;

                if (accelaration > 12) {
                    final SharedPreferences sharedPreferencesEditor = getActivity().getSharedPreferences(My_PREFS_NAME, Context.MODE_PRIVATE);
                    final boolean isAllowed = sharedPreferencesEditor.getBoolean("feature", false);
                    if (isAllowed) {
                        playNext("PlayNextNormal");

                    }
                }


            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
    }
}
