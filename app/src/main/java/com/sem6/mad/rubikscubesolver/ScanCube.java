package com.sem6.mad.rubikscubesolver;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

public class ScanCube extends AppCompatActivity {


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cube_scan);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.cam_layout, CameraPreviewFragment.class, null)
                .setReorderingAllowed(true)
                .commit();

        FragmentManager fragmentManager2 = getSupportFragmentManager();
        fragmentManager2.beginTransaction()
                .replace(R.id.cube_layout, CubeFragment.class, null)
                .setReorderingAllowed(true)
                .commit();

    }
}