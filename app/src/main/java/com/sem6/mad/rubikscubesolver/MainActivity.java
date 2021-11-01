package com.sem6.mad.rubikscubesolver;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import org.opencv.android.OpenCVLoader;

public class MainActivity extends AppCompatActivity {

    static {

        if(OpenCVLoader.initDebug())
        {
            Log.d("MainActivity", "openCV Loaded");
        }
        else
        {
            Log.d("MainActivity", "openCV not loaded");
        }

    }


    CardView card1,card2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(1);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getWindow().setStatusBarColor(Color.TRANSPARENT);

        setContentView(R.layout.activity_main);

        card1 = findViewById(R.id.card1);
        card2 = findViewById(R.id.card2);

        card2.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this,
                    DemoSolve.class);
            startActivity(intent);
        });

        card1.setOnClickListener( view -> {
            Intent intent = new Intent(MainActivity.this,
                    LiveDetectionActivity.class);
            startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
        });
    }
}