package com.kavita.quiztest.UI.Fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.kavita.quiztest.R;


public class ProfileFragment extends Fragment {

    public final static String TAG_PROFILE = "My Profile";
    private TextView email, name;
    private Button btn_profile;
    private final String TAG = getClass().getSimpleName();

    String mEmail, mName;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_profile, container, false);

        email = view.findViewById(R.id.profile_email);
        name = view.findViewById(R.id.profile_name);
        btn_profile = view.findViewById(R.id.btn_profile);
        getActivity().setTitle(this.TAG_PROFILE);

        Log.d(TAG, "onCreateView: NAME" + mName);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
        email.setText(sharedPreferences.getString("email", ""));
        name.setText(sharedPreferences.getString("name", ""));


        btn_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppCompatActivity activity = (AppCompatActivity) view.getContext();
                Fragment myFragment = new DashboardFragment();
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, myFragment).commit();

            }
        });

        return view;

    }
}