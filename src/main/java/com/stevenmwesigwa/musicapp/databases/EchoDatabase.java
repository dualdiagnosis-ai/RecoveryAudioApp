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
    private  ArrayList<Songs> songsArrayList = null;
    final static private String DB_NAME = "FavoriteDatabase";
    final static private String TABLE_NAME = "FavoriteTable";
    final static private String COLUMN_ID = "SongID";
    final static private String COLUMN_SONG_TITLE = "SongTitle";
    final static private String COLUMN_SONG_ARTIST = "SongArtist";
    final static private String COLUMN_SONG_PATH = "SongPath";

    /**
     * Create a helper object to create, open, and/or manage a database.
     * This method always returns very quickly.  The database is not actually
     * created or opened until one of {@link #getWritableDatabase} or
     * {@link #getReadableDatabase} is called.
     *
     * @param context to use for locating paths to the the database
     * @param name    of the database file, or null for an in-memory database
     * @param factory to use for creating cursor objects, or null for the default
     * @param version number of the database (starting at 1); if the database is older,
     *                {@link #onUpgrade} will be used to upgrade the database; if the database is
     *                newer, {@link #onDowngrade} will be used to downgrade the database
     */
    public EchoDatabase(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
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

    /**
     * Called when the database needs to be upgraded. The implementation
     * should use this method to drop tables, add tables, or do anything else it
     * needs to upgrade to the new schema version.
     *
     * <p>
     * The SQLite ALTER TABLE documentation can be found
     * <a href="http://sqlite.org/lang_altertable.html">here</a>. If you add new columns
     * you can use ALTER TABLE to insert them into a live table. If you rename or remove columns
     * you can use ALTER TABLE to rename the old table, then create the new table and then
     * populate the new table with the contents of the old table.
     * </p><p>
     * This method executes within a transaction.  If an exception is thrown, all changes
     * will automatically be rolled back.
     * </p>
     *
     * @param db         The database.
     * @param oldVersion The old database version.
     * @param newVersion The new database version.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    private void insert(int songId, String songArtist, String songTitle, String songPath) {
        final SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_ID, songId);
        contentValues.put(COLUMN_SONG_ARTIST, songArtist);
        contentValues.put(COLUMN_SONG_TITLE, songTitle);
        contentValues.put(COLUMN_SONG_PATH, songPath);

        sqLiteDatabase.insert(TABLE_NAME, null, contentValues);
        sqLiteDatabase.close();

    }

    private ArrayList<Songs> get() {
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
                    songsArrayList.add(new Songs((long)songId, songTitle, songArtist, songPath,(long)0));
                } while (cursor.moveToNext());
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return songsArrayList;

    }

    private Long ifSongIdExists(int trackId) {
        int songId =0;
        try {
            final SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
            final String getQuery = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + " = " + trackId;
            Cursor cursor = sqLiteDatabase.rawQuery(getQuery, null);

            if (cursor.moveToFirst()) {
                do {
                     songId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));

                } while (cursor.moveToNext());
            } else {
                return (long) 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (long) songId;
    }
}
