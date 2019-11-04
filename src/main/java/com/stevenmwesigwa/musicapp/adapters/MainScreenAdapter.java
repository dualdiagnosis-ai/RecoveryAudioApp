package com.stevenmwesigwa.musicapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.stevenmwesigwa.musicapp.R;
import com.stevenmwesigwa.musicapp.Songs;
import com.stevenmwesigwa.musicapp.activities.MainActivity;
import com.stevenmwesigwa.musicapp.fragments.AboutUsFragment;
import com.stevenmwesigwa.musicapp.fragments.FavoriteFragment;
import com.stevenmwesigwa.musicapp.fragments.MainScreenFragment;
import com.stevenmwesigwa.musicapp.fragments.SettingsFragment;

import java.util.List;

public class MainScreenAdapter extends RecyclerView.Adapter<MainScreenAdapter.MainScreenViewHolder>  {
    /**
     * Define LayoutInflater
     */
    private LayoutInflater mInflater;
    /**
     * Define the data
     */
    private List<Songs> mSongsList;
    private Context mContext;

    // data is passed into the constructor
    public MainScreenAdapter(List<Songs> songsList, Context context) {
        this.mInflater = LayoutInflater.from(context);
        this.mSongsList = songsList;
        this.mContext = context;
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder

    /**
     * This is where we will initialize our views
     */
    public class MainScreenViewHolder extends RecyclerView.ViewHolder {
        TextView trackTitle;
        TextView trackArtist;
        RelativeLayout contentRowSongList;

        public MainScreenViewHolder(View view) {
            super(view);
            trackTitle = view.findViewById(R.id.trackTitle);
            trackArtist = view.findViewById(R.id.trackArtist);
            contentRowSongList = view.findViewById(R.id.contentRowSongList);

        }
    }

    @NonNull
    @Override
    public MainScreenAdapter.MainScreenViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        /* We inflate the xml which gives us a View object */
        View view = mInflater.inflate(R.layout.row_custom_mainscreen_adapter, parent, false);
        return new MainScreenAdapter.MainScreenViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MainScreenAdapter.MainScreenViewHolder holder, int position) {
        Songs song = mSongsList.get(position);

        holder.trackTitle.setText(song.getSongTitle());
        holder.trackArtist.setText(song.getSongArtist());
        /**
         * Implement the onClickListener on the 'navdrawerItemContentHolder' to open the respective
         * Fragment on click.
         */
        holder.contentRowSongList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, song.getSongTitle(),Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    public int getItemCount() {

        if(mSongsList == null) {

            return 0;
        } else {
            return mSongsList.size();
        }

    }


}
