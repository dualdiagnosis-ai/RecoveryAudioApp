package com.stevenmwesigwa.musicapp;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Comparator;

public class Songs implements Parcelable {
    private Long songId;
    private String songTitle;
    private String songArtist;
    private String songData;
    private Long songDateAdded;

    public Songs(Long songId, String songTitle, String songArtist, String songData, Long songDateAdded) {
        this.songId = songId;
        this.songTitle = songTitle;
        this.songArtist = songArtist;
        this.songData = songData;
        this.songDateAdded = songDateAdded;
    }

    public Long getSongId() {
        return songId;
    }

    public void setSongId(Long songId) {
        this.songId = songId;
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

    public String getSongData() {
        return songData;
    }

    public void setSongData(String songData) {
        this.songData = songData;
    }

    public Long getSongDateAdded() {
        return songDateAdded;
    }

    public void setSongDateAdded(Long songDateAdded) {
        this.songDateAdded = songDateAdded;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeLong(songId);
        parcel.writeString(songTitle);
        parcel.writeString(songArtist);
        parcel.writeString(songData);
        parcel.writeLong(songDateAdded);
    }

    public static final Parcelable.Creator<Songs> CREATOR
            = new Parcelable.Creator<Songs>() {
        @Override
        public Songs createFromParcel(Parcel parcel) {
            return new Songs(parcel);
        }

        @Override
        public Songs[] newArray(int size) {
            return new Songs[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    public static Creator<Songs> getCREATOR() {
        return CREATOR;
    }

    private Songs(Parcel parcel) {
        songId = parcel.readLong();
        songTitle = parcel.readString();
        songArtist = parcel.readString();
        songData = parcel.readString();
        songDateAdded = parcel.readLong();
    }

   public static Comparator<Songs> sortBySongTItle =  (Songs o1, Songs o2) -> o1.getSongTitle().compareToIgnoreCase(o2.getSongTitle());

    public static Comparator<Songs> sortBySongDateAdded =  (Songs o1, Songs o2) -> o1.getSongDateAdded().compareTo(o2.getSongDateAdded());
}
