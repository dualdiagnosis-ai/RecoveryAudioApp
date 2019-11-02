package com.stevenmwesigwa.musicapp;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;

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

    }


    @Override
    protected void onStart() {
        super.onStart();
    }
}
