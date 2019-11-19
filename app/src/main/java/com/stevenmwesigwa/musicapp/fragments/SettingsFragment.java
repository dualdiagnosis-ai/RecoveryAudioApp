package com.stevenmwesigwa.musicapp.fragments;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.stevenmwesigwa.musicapp.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment {
    private Activity activity = null;
    private static String My_PREFS_NAME = "ShakeFeature";
    private Switch switchShakeSettingFrag = null;


    public SettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        switchShakeSettingFrag = view.findViewById(R.id.switchShakeSettingFrag);


        return view;
    }

    /**
     * Called to do initial creation of a fragment.  This is called after
     * {@link #onAttach(Activity)} and before
     * {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     *
     * <p>Note that this can be called while the fragment's activity is
     * still in the process of being created.  As such, you can not rely
     * on things like the activity's content view hierarchy being initialized
     * at this point.  If you want to do work once the activity itself is
     * created, see {@link #onActivityCreated(Bundle)}.
     *
     * <p>Any restored child fragments will be created before the base
     * <code>Fragment.onCreate</code> method returns.</p>
     *
     * @param savedInstanceState If the fragment is being re-created from
     *                           a previous saved state, this is the state.
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity.setTitle("Settings");
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
        this.activity = (Activity) context;

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
       final SharedPreferences preferenceSettingFrag = activity.getSharedPreferences(My_PREFS_NAME, Context.MODE_PRIVATE);
       final boolean isAllowedFeatureSettingFrag = preferenceSettingFrag.getBoolean("feature", false);
       if(isAllowedFeatureSettingFrag) {
switchShakeSettingFrag.setChecked(true);
       } else {
           switchShakeSettingFrag.setChecked(true);

       }

       switchShakeSettingFrag.setOnCheckedChangeListener(
               (CompoundButton buttonView, boolean isChecked) -> {
if(isChecked) {
    final SharedPreferences.Editor sharedPreferencesEditor = activity.getSharedPreferences(My_PREFS_NAME, Context.MODE_PRIVATE).edit();
    sharedPreferencesEditor.putBoolean("feature", true);
    sharedPreferencesEditor.apply();
} else {
    final SharedPreferences.Editor sharedPreferencesEditor = activity.getSharedPreferences(My_PREFS_NAME, Context.MODE_PRIVATE).edit();
    sharedPreferencesEditor.putBoolean("feature", false);
    sharedPreferencesEditor.apply();

}
               }
       );

    }

    /**
     * Prepare the Fragment host's standard options menu to be displayed.  This is
     * called right before the menu is shown, every time it is shown.  You can
     * use this method to efficiently enable/disable items or otherwise
     * dynamically modify the contents.  See
     * {@link Activity#onPrepareOptionsMenu(Menu) Activity.onPrepareOptionsMenu}
     * for more information.
     *
     * @param menu The options menu as last shown or first initialized by
     *             onCreateOptionsMenu().
     * @see #setHasOptionsMenu
     * @see #onCreateOptionsMenu
     */
    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
    }
}
