package com.stevenmwesigwa.musicapp;

public class CurrentSongHelper {
    private String songData = null;
    private String songTitle = null;
    private String songArtist = null;
    private Long songId = null;
    private Long songDateAdded = null;
    private int currentPosition;
    private int trackPosition;
    private Boolean isPlaying = false;
    private Boolean isLoopFeatureEnabled = false;
    private  Boolean isShuffleFeatureEnabled = false;

    public String getSongData() {
        return songData;
    }

    public void setSongData(String songData) {
        this.songData = songData;
    }

    public String getSongTitle() {
        return songTitle;
    }

    public void setSongTitle(String songTitle) {
        this.songTitle = songTitle;
    }

    public String getSongArtist() {
        return songArtist;
    }

    public void setSongArtist(String songArtist) {
        this.songArtist = songArtist;
    }

    public Long getSongId() {
        return songId;
    }

    public void setSongId(Long songId) {
        this.songId = songId;
    }

    public Long getSongDateAdded() {
        return songDateAdded;
    }

    public void setSongDateAdded(Long songDateAdded) {
        this.songDateAdded = songDateAdded;
    }

    public int getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(int currentPosition) {
        this.currentPosition = currentPosition;
    }

    public int getTrackPosition() {
        return trackPosition;
    }

    public void setTrackPosition(int trackPosition) {
        this.trackPosition = trackPosition;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    public boolean isLoopFeatureEnabled() {
        return isLoopFeatureEnabled;
    }

    public void setLoopFeatureEnabled(boolean loopFeatureEnabled) {
        isLoopFeatureEnabled = loopFeatureEnabled;
    }

    public boolean isShuffleFeatureEnabled() {
        return isShuffleFeatureEnabled;
    }

    public void setShuffleFeatureEnabled(boolean shuffleFeatureEnabled) {
        isShuffleFeatureEnabled = shuffleFeatureEnabled;
    }
}
