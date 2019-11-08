package com.stevenmwesigwa.musicapp.fragments;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
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

import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.TimeUnit;


/**
 * A simple {@link Fragment} subclass.
 */
public class SongPlayingFragment extends Fragment {
    private Activity activity;
    // Controls playback of audio or video files
    private MediaPlayer mediaPlayer;

    RelativeLayout songInformationNowPlaying = null;
    private TextView songTitleNowPlaying = null;
    private TextView songArtistNowPlaying = null;
    RelativeLayout seekBarLayoutNowPlaying = null;
    private SeekBar seekBarNowPlaying = null;
    private TextView startTimeSeekBarNowPlaying = null;
    private TextView endTimeSeekBarNowPlaying = null;
    RelativeLayout controlPanelNowPlaying = null;
    private ImageButton shuffleButtonNowPlaying = null;
    private ImageButton previousButtonNowPlaying = null;
    private ImageButton playPauseButtonNowPlaying = null;
    private ImageButton nextButtonNowPlaying = null;
    private ImageButton loopButtonNowPlaying = null;

    private AudioVisualization audioVisualization = null;
    GLAudioVisualizationView glAudioVisualizationView =null;

    private Runnable updateSongTime = new Runnable() {
        @Override
        public void run() {
            final Handler handler = new Handler();
            int getCurrentPosition = mediaPlayer.getCurrentPosition();
            startTimeSeekBarNowPlaying.setText(String.format(Locale.US,"%d:%d",
                    TimeUnit.MILLISECONDS.toMinutes(getCurrentPosition),
                    TimeUnit.MILLISECONDS.toSeconds(getCurrentPosition)- TimeUnit.MILLISECONDS.toSeconds(TimeUnit.MILLISECONDS.toMinutes(getCurrentPosition))));
            handler.postDelayed(this, 1000);
        }
    };


    private CurrentSongHelper currentSongHelper = null;

    private Integer currentPosition = null;
    private ArrayList<Songs> songsList = null;

    public SongPlayingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_song_playing, container, false);
        RelativeLayout songInformationNowPlaying = view.findViewById(R.id.songInformationNowPlaying);
        TextView songTitleNowPlaying = view.findViewById(R.id.songTitleNowPlaying);
        RelativeLayout seekBarLayoutNowPlaying = view.findViewById(R.id.seekBarLayoutNowPlaying);
        SeekBar seekBarNowPlaying = view.findViewById(R.id.seekBarNowPlaying);
        TextView startTimeSeekBarNowPlaying = view.findViewById(R.id.startTimeSeekBarNowPlaying);
        TextView endTimeSeekBarNowPlaying = view.findViewById(R.id.endTimeSeekBarNowPlaying);
        RelativeLayout controlPanelNowPlaying = view.findViewById(R.id.controlPanelNowPlaying);
        ImageButton shuffleButtonNowPlaying = view.findViewById(R.id.shuffleButtonNowPlaying);
        ImageButton previousButtonNowPlaying = view.findViewById(R.id.previousButtonNowPlaying);
        ImageButton playPauseButtonNowPlaying = view.findViewById(R.id.playPauseButtonNowPlaying);
        ImageButton nextButtonNowPlaying = view.findViewById(R.id.nextButtonNowPlaying);
        ImageButton loopButtonNowPlaying = view.findViewById(R.id.loopButtonNowPlaying);
        GLAudioVisualizationView visualizerViewNowPlaying = view.findViewById(R.id.visualizerViewNowPlaying);
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
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mediaPlayer.setDataSource(activity, Uri.parse(songData));
            mediaPlayer.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }
        mediaPlayer.start();
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
    }

    @Override
    public void onPause() {
        audioVisualization.onPause();
        super.onPause();
    }

   /* When user leaves screen with audio visualization view,
    don't forget to free resources and call release() method.
*/
    @Override
    public void onDestroyView() {
        audioVisualization.release();
        super.onDestroyView();
    }

    private void onSongComplete() {
        if(currentSongHelper.isShuffleFeatureEnabled()) {
            playNext("PlayNextLikeNormalShuffle");
            currentSongHelper.setPlaying(true);
        } else {
            if(currentSongHelper.isLoopFeatureEnabled()) {
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
            } else  {
                playNext("PlayNextNormal");
                currentSongHelper.setPlaying(true);
            }
        }
    }

    private void clickHandler() {
        shuffleButtonNowPlaying.setOnClickListener(
                view -> {
if(currentSongHelper.isShuffleFeatureEnabled()) {
    shuffleButtonNowPlaying.setBackgroundResource(R.drawable.shuffle_white_icon);
} else {
    currentSongHelper.setShuffleFeatureEnabled(true);
    currentSongHelper.setLoopFeatureEnabled(false);
    shuffleButtonNowPlaying.setBackgroundResource(R.drawable.shuffle_icon);
    loopButtonNowPlaying.setBackgroundResource(R.drawable.loop_white_icon);
}
                }

        );
        nextButtonNowPlaying.setOnClickListener(

                view -> {
                    currentSongHelper.setPlaying(true);
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
if(currentSongHelper.isLoopFeatureEnabled()){
    currentSongHelper.setLoopFeatureEnabled(false);
    loopButtonNowPlaying.setBackgroundResource(R.drawable.loop_white_icon);
} else {
    currentSongHelper.setLoopFeatureEnabled(true);
    currentSongHelper.setShuffleFeatureEnabled(false);
    loopButtonNowPlaying.setBackgroundResource(R.drawable.loop_icon);
    shuffleButtonNowPlaying.setBackgroundResource(R.drawable.shuffle_white_icon);
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

    private void playNext(String check) {
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
    }

    private void updateTextViews(String songTitleNowPlaying, String songArtistNowPlaying) {
        this.songTitleNowPlaying.setText(songTitleNowPlaying);
        this.songArtistNowPlaying.setText(songArtistNowPlaying);
    }

    private  void processInformation(MediaPlayer mediaPlayer) {
        int finalTime = mediaPlayer.getDuration();
        int startTime = mediaPlayer.getCurrentPosition();
        // Set max seek ar length
        seekBarNowPlaying.setMax(finalTime);
        startTimeSeekBarNowPlaying.setText(String.format(Locale.US,"%d:%d",
                TimeUnit.MILLISECONDS.toMinutes(startTime),
                TimeUnit.MILLISECONDS.toSeconds(startTime)- TimeUnit.MILLISECONDS.toSeconds(TimeUnit.MILLISECONDS.toMinutes(startTime))));

       endTimeSeekBarNowPlaying.setText(String.format(Locale.US,"%d:%d",
                TimeUnit.MILLISECONDS.toMinutes(finalTime),
                TimeUnit.MILLISECONDS.toSeconds(finalTime)- TimeUnit.MILLISECONDS.toSeconds(TimeUnit.MILLISECONDS.toMinutes(finalTime))));


       seekBarNowPlaying.setProgress(startTime);
       final Handler handler = new Handler();
       handler.postDelayed(updateSongTime, 1000);
    }


    public AudioVisualization getAudioVisualization() {
        return audioVisualization;
    }

    public void setAudioVisualization(AudioVisualization audioVisualization) {
        this.audioVisualization = audioVisualization;
    }
}
