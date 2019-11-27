package com.stevenmwesigwa.musicapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.stevenmwesigwa.musicapp.R;
import com.stevenmwesigwa.musicapp.activities.MainActivity;
import com.stevenmwesigwa.musicapp.fragments.AboutUsFragment;
import com.stevenmwesigwa.musicapp.fragments.FavoriteFragment;
import com.stevenmwesigwa.musicapp.fragments.MainScreenFragment;
import com.stevenmwesigwa.musicapp.fragments.SettingsFragment;

import java.util.List;

public class NavigationDrawerAdapter extends RecyclerView.Adapter<NavigationDrawerAdapter.NavViewHolder> {
    private LayoutInflater mInflater;
    private List<String> mContentList;
    private int[] mGetImages;
    private Context mContext;

    // data is passed into the constructor
    public NavigationDrawerAdapter(List<String> contentList, int[] getImages, Context context) {
        this.mInflater = LayoutInflater.from(context);
        this.mContentList = contentList;
        this.mGetImages = getImages;
        this.mContext = context;
    }

    /*
     * Provide a reference to the views for each data item
     * Complex data items may need more than one view per item, and
     * you provide access to all the views for a data item in a view holder
     */
    @NonNull
    @Override
    public NavigationDrawerAdapter.NavViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // We inflate the xml which gives us a View object
        View view = mInflater.inflate(R.layout.row_custom_navigationdrawer, parent, false);
        return new NavigationDrawerAdapter.NavViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NavViewHolder holder, int position) {
        holder.imageView.setBackgroundResource(mGetImages[position]);
        holder.textView.setText(mContentList.get(position));
        /*
         * Implement the onClickListener on the 'navdrawerItemContentHolder' to open the respective
         * Fragment on click.
         */
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position == 0) {
                    final MainScreenFragment mainScreenFragment = new MainScreenFragment();
                    /*
                     * Let's begin the transaction
                     * We will invoke this Adapter through our MainActivity.java file.
                     */
                    MainActivity mainActivity = (MainActivity) mContext;
                    mainActivity.getSupportFragmentManager()
                            .beginTransaction()
                            // Replace the the already added fragment from MainActivity.java
                            .replace(R.id.detailsFragment, mainScreenFragment)
                            .commit();
                } else if (position == 1) {
                    final FavoriteFragment favoriteFragment = new FavoriteFragment();
                    /*
                     * Let's begin the transaction
                     * We will invoke this Adapter through our MainActivity.java file.
                     */
                    MainActivity mainActivity = (MainActivity) mContext;
                    mainActivity.getSupportFragmentManager()
                            .beginTransaction()
                            // Replace the the already added fragment from MainActivity.java
                            .replace(R.id.detailsFragment, favoriteFragment)
                            .commit();
                } else if (position == 2) {
                    final SettingsFragment settingsFragment = new SettingsFragment();
                    /*
                     * Let's begin the transaction
                     * We will invoke this Adapter through our MainActivity.java file.
                     */
                    MainActivity mainActivity = (MainActivity) mContext;
                    mainActivity.getSupportFragmentManager()
                            .beginTransaction()
                            // Replace the the already added fragment from MainActivity.java
                            .replace(R.id.detailsFragment, settingsFragment)
                            .commit();
                } else {
                    final AboutUsFragment aboutUsFragment = new AboutUsFragment();
                    /*
                     * Let's begin the transaction
                     * We will invoke this Adapter through our MainActivity.java file.
                     */
                    MainActivity mainActivity = (MainActivity) mContext;
                    mainActivity.getSupportFragmentManager()
                            .beginTransaction()
                            // Replace the the already added fragment from MainActivity.java
                            .replace(R.id.detailsFragment, aboutUsFragment)
                            .commit();
                }
                // (To automatically close the 'nav drawer' when an item is clicked)
                MainActivity.drawerLayout.closeDrawers();
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mContentList == null) {

            return 0;
        } else {
            return mContentList.size();
        }
    }

    class NavViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        ImageView imageView;
        RelativeLayout relativeLayout;

        NavViewHolder(View view) {
            super(view);
            textView = view.findViewById(R.id.textNavdrawer);
            imageView = view.findViewById(R.id.iconNavdrawer);
            relativeLayout = view.findViewById(R.id.navdrawerItemContentHolder);

        }
    }
}
