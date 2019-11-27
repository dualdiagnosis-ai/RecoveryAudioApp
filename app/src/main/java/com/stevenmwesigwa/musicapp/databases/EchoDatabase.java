package com.stevenmwesigwa.musicapp.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.stevenmwesigwa.musicapp.Songs;

import java.util.ArrayList;

public class EchoDatabase extends SQLiteOpenHelper {
    final static private String DB_NAME = "FavoriteDatabase";
    final static private int DB_VERSION = 1;
    final static private String TABLE_NAME = "FavoriteTable";
    final static private String COLUMN_ID = "SongID";
    final static private String COLUMN_SONG_TITLE = "SongTitle";
    final static private String COLUMN_SONG_ARTIST = "SongArtist";
    final static private String COLUMN_SONG_PATH = "SongPath";
    private ArrayList<Songs> songsArrayList = null;
    private Context context = null;

    public EchoDatabase(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public EchoDatabase(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }

    /**
     * Called when the database is created for the first time. This is where the
     * creation of tables and the initial population of the tables should happen.
     *
     * @param echoSongSqliteDb The database.
     */
    @Override
    public void onCreate(SQLiteDatabase echoSongSqliteDb) {
        final String createTableSchema = "CREATE TABLE " + TABLE_NAME + " ( " + COLUMN_ID + " INTEGER, " + COLUMN_SONG_ARTIST + " STRING, " + COLUMN_SONG_TITLE + " STRING, " + COLUMN_SONG_PATH + " STRING)";
        echoSongSqliteDb.execSQL(createTableSchema);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insert(int songId, String songArtist, String songTitle, String songPath) {
        final SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_ID, songId);
        contentValues.put(COLUMN_SONG_ARTIST, songArtist);
        contentValues.put(COLUMN_SONG_TITLE, songTitle);
        contentValues.put(COLUMN_SONG_PATH, songPath);

        sqLiteDatabase.insert(TABLE_NAME, null, contentValues);
        sqLiteDatabase.close();

    }

    public ArrayList<Songs> get() {
        ArrayList<Songs> songsArrayList = new ArrayList<>();
        try {
            final SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
            final String getQuery = "SELECT * FROM " + TABLE_NAME;
            Cursor cursor = sqLiteDatabase.rawQuery(getQuery, null);

            if (cursor.moveToFirst()) {
                do {
                    int songId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
                    String songArtist = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SONG_ARTIST));
                    String songTitle = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SONG_TITLE));
                    String songPath = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SONG_PATH));
                    songsArrayList.add(new Songs((long) songId, songTitle, songArtist, songPath, (long) 0));
                } while (cursor.moveToNext());
            } else {
                return songsArrayList;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return songsArrayList;

    }

    public boolean ifSongIdExists(int trackId) {
        boolean songIdExists = true;
        int songId = 0;
        try {
            final SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
            final String getQuery = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + " = " + trackId;
            Cursor cursor = sqLiteDatabase.rawQuery(getQuery, null);

            if (cursor.moveToFirst()) {
                do {
                    songId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));

                } while (cursor.moveToNext());
            } else {
                songIdExists = false;
                return songIdExists;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return songId != 0;
    }


    public void delete(int songId) {
        final SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        final String whereClause = COLUMN_ID + "=" + songId;
        sqLiteDatabase.delete(TABLE_NAME, whereClause, null);
        sqLiteDatabase.close();
    }

    public int rowCount() {
        int totalRowCount = 0;
        try {
            final SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
            final String getQuery = "SELECT * FROM " + TABLE_NAME;
            Cursor cursor = sqLiteDatabase.rawQuery(getQuery, null);

            if (cursor.moveToFirst()) {
                do {
                    totalRowCount++;
                } while (cursor.moveToNext());
            } else {
                return 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return totalRowCount;
    }


}
