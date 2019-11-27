package com.stevenmwesigwa.musicapp.adapters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.stevenmwesigwa.musicapp.R;
import com.stevenmwesigwa.musicapp.Songs;
import com.stevenmwesigwa.musicapp.fragments.SongPlayingFragment;

import java.util.ArrayList;

public class MainScreenAdapter extends RecyclerView.Adapter<MainScreenAdapter.MainScreenViewHolder> {
    private LayoutInflater mInflater;
    private ArrayList<Songs> mSongsList;
    private Context mContext;

    // data is passed into the constructor
    public MainScreenAdapter(ArrayList<Songs> songsList, Context context) {
        this.mInflater = LayoutInflater.from(context);
        this.mSongsList = songsList;
        this.mContext = context;
    }

    /*
     * Provide a reference to the views for each data item
     * Complex data items may need more than one view per item, and
     * you provide access to all the views for a data item in a view holder
     */
    @NonNull
    @Override
    public MainScreenAdapter.MainScreenViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // We inflate the xml which gives us a View object
        View view = mInflater.inflate(R.layout.row_custom_mainscreen_adapter, parent, false);
        return new MainScreenAdapter.MainScreenViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MainScreenAdapter.MainScreenViewHolder holder, int position) {
        Songs song = mSongsList.get(position);

        holder.trackTitle.setText(song.getSongTitle());
        holder.trackArtist.setText(song.getSongArtist());
        /*
         * Implement the onClickListener on the 'navdrawerItemContentHolder' to open the respective
         * Fragment on click.
         */
        holder.contentRowSongList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final SongPlayingFragment songPlayingFragment = new SongPlayingFragment();
                Bundle bundle = new Bundle();
                bundle.putString("songArtist", song.getSongArtist());
                bundle.putString("songTitle", song.getSongTitle());
                bundle.putLong("songId", song.getSongId());
                bundle.putString("songData", song.getSongData());
                bundle.putLong("songDateAdded", song.getSongDateAdded());
                bundle.putInt("songPosition", position);
                bundle.putParcelableArrayList("songsList", mSongsList);

                //Link values with the songPlayingFragment
                songPlayingFragment.setArguments(bundle);
                /*
                 * Let's begin the transaction
                 * We will invoke this Adapter through our FragmentActivity.java file.
                 */
                FragmentActivity fragmentActivity = (FragmentActivity) mContext;
                fragmentActivity.getSupportFragmentManager()
                        .beginTransaction()

                        // Replace the the already added fragment from MainActivity.java
                        .replace(R.id.detailsFragment, songPlayingFragment)
                        .addToBackStack("SongPlayingFragment")
                        .commit();

                /*
                 *   Stop the currently playing track and starting a new one when a song is clicked upon,
                 *   rather than starting a new one over top of the existing one
                 */
                if ((SongPlayingFragment.mediaPlayer != null) && SongPlayingFragment.mediaPlayer.isPlaying()) {
                    SongPlayingFragment.mediaPlayer.stop();
                }


            }
        });
    }

    @Override
    public int getItemCount() {

        if (mSongsList == null) {

            return 0;
        } else {
            return mSongsList.size();
        }

    }


    // This is where we will initialize our views
    class MainScreenViewHolder extends RecyclerView.ViewHolder {
        TextView trackTitle;
        TextView trackArtist;
        RelativeLayout contentRowSongList;

        MainScreenViewHolder(View view) {
            super(view);
            trackTitle = view.findViewById(R.id.trackTitle);
            trackArtist = view.findViewById(R.id.trackArtist);
            contentRowSongList = view.findViewById(R.id.contentRowSongList);

        }
    }


}
