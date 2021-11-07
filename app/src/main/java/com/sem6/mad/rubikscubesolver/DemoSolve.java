package com.sem6.mad.rubikscubesolver;

import static com.sem6.mad.rubikscubesolver.SolveCube.findShorterSolutions;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.catalinjurjiu.animcubeandroid.AnimCube;

public class DemoSolve extends AppCompatActivity {
    AnimCube animCube;
    Button btnSolve;
    TextView txtMoves;
    TextView txtMove1;
    // UUUUUUUUURRRRRRFFFFFFFFFLLLDDDDDDDDDLLLLLLBBBBBBBBBRRR
    // old - DUUBULDBFRBFRRULLLBRDFFFBLURDBFDFDRFRULBLUFDURRBLBDUDL

    private static final String scrambledCube = "DUUBULDBFRBFRRULLLBRDFFFBLURDBFDFDRFRULBLUFDURRBLBDUDL";
    private static final String simpleSolve = "R2 U2 B2 L2 F2 U' L2 R2 B2 R2 D  B2 F  L' F  U2 F' R' D' L2 R'";
    private static  String shortestSolve = "L2 U  D2 R' B  U2 L  F  U  R2 D2 F2 U' L2 U  B  D  R' ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(1);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getWindow().setStatusBarColor(Color.TRANSPARENT);


        setContentView(R.layout.solve_demo);

        animCube = findViewById(R.id.animcube);
        txtMoves = findViewById(R.id.textView2);
        txtMove1 = findViewById(R.id.textview1);
        btnSolve = findViewById(R.id.button);

        String cubeState = Min2PhaseToCubeMapping.colorMapping(scrambledCube);

       // cubeState = "012345012101234512212345012312345013412345014512345015";
       // cubeState = "000000000333333555555555222111111111222222444444444333";
        // cubeState = "000000000111111111222222222333333333444444444555555555";

        animCube.setCubeModel(cubeState);
        Log.d("CubeState",cubeState);

        shortestSolve = findShorterSolutions(scrambledCube);


        btnSolve.setOnClickListener(v -> {

            txtMoves.setText(shortestSolve);
            txtMove1.setText("Solution Moves");

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    animCube.setMoveSequence(shortestSolve);
                    animCube.animateMoveSequence();
                }
            }, 500);


        });

    }
}
