package com.stevenmwesigwa.musicapp.activities;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;

import com.stevenmwesigwa.musicapp.R;
import com.stevenmwesigwa.musicapp.fragments.MainScreenFragment;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity {
    /**
     * Initialize our DrawerLayout object
     */
    private DrawerLayout drawerLayout = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**
         * Link this file with the xml layout file
         */
        setContentView(R.layout.activity_main);
        /**
         * Set up the Tool bar / Action bar / App bar
         */
       final Toolbar toolbar = findViewById(R.id.toolBar);
        /**
         * Set toolbar as the default toolbar
         */
        setSupportActionBar(toolbar);
        /**
         * Initialize our DrawerLayout object with a value
         */
        drawerLayout = findViewById(R.id.drawerLayout);
        /**
         * Make an object of ActionBarDrawerToggle class
         */
        final ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        /**
         * Set up the drawer layout to a listener so that whenever you click on the
         * 'hamburger icon' , the navigation drawer opens up.
         * To do that, we access the DrawerLayout object
         */
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        /**
         * Animate the 'hamburger icon' whenever it's clicked on. i.e from '3 lines' to an 'arrow'
         */
        actionBarDrawerToggle.syncState();

        /**
         * When implementing fragments over an activity, we need our "main fragment" that will get displayed
         * over the activity. For this case, it will be the 'mainscreenfragment' where the list of songs go
         *
         * Create 'mainscreenfragment' object
         */
        final MainScreenFragment mainScreenFragment = new MainScreenFragment();
        /**
         * A manager to manage different fragments, their inception, their layouts, animations if any
         *  and all the other attributes associated with the fragment.
         *  We access the FragmentManager by mentionng the context. (this)
         *
         *  To start a series of operations on the Fragments
         *  associated with this FragmentManager - beginTransaction().
         *
         *  To commit all / apply the changes made - commit()
         */
        this.getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.detailsFragment, mainScreenFragment, "MainScreenFragment")
        .commit();

       RecyclerView navigationRecyclerView = findViewById(R.id.navigationRecyclerView);
        /**
         * Setup LayoutManager - Is responsible for measuring and positioning 'item views' with in a recycler view
         */
        // use a linear layout manager
        RecyclerView.LayoutManager  layoutManager = new LinearLayoutManager(this);
        navigationRecyclerView.setLayoutManager(layoutManager);
        navigationRecyclerView.setItemAnimator(new DefaultItemAnimator());

        /**
         * Set up an adapter to show elements in a list
         */
    }


    @Override
    protected void onStart() {
        super.onStart();
    }
}


/**
 *
 */