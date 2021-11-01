package com.sem6.mad.rubikscubesolver;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class CubeFragment extends Fragment {

    public CubeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onCreate(savedInstanceState);
        View rootview = inflater.inflate(R.layout.fragment_cube, container, false);
        getActivity().setContentView(R.layout.fragment_cube);
        return rootview;
    }
}