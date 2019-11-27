package com.stevenmwesigwa.musicapp.fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.stevenmwesigwa.musicapp.R;

// A simple {@link Fragment} subclass.

public class SettingsFragment extends Fragment {
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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle("Settings");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final SharedPreferences preferenceSettingFrag = getActivity().getSharedPreferences(My_PREFS_NAME, Context.MODE_PRIVATE);
        final boolean isAllowedFeatureSettingFrag = preferenceSettingFrag.getBoolean("feature", false);
        if (isAllowedFeatureSettingFrag) {
            switchShakeSettingFrag.setChecked(true);
        } else {
            switchShakeSettingFrag.setChecked(true);
        }

        switchShakeSettingFrag.setOnCheckedChangeListener(
                (CompoundButton buttonView, boolean isChecked) -> {
                    if (isChecked) {
                        final SharedPreferences.Editor sharedPreferencesEditor = getActivity().getSharedPreferences(My_PREFS_NAME, Context.MODE_PRIVATE).edit();
                        sharedPreferencesEditor.putBoolean("feature", true);
                        sharedPreferencesEditor.apply();
                    } else {
                        final SharedPreferences.Editor sharedPreferencesEditor = getActivity().getSharedPreferences(My_PREFS_NAME, Context.MODE_PRIVATE).edit();
                        sharedPreferencesEditor.putBoolean("feature", false);
                        sharedPreferencesEditor.apply();
                    }
                }
        );

    }
}
