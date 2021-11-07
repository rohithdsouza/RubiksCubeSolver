package com.sem6.mad.rubikscubesolver;

import android.content.Intent;
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
import com.catalinjurjiu.rubikdetector.model.RubikFacelet;

import min2phase.Search;

public class SolveCube extends AppCompatActivity {
    AnimCube animCube;
    Button btnSolve;
    TextView txtMoves;
    TextView txtMove1;

    private static  String scrambledCube = "" ;
    private static  String simpleSolve = "R2 U2 B2 L2 F2 U' L2 R2 B2 R2 D  B2 F  L' F  U2 F' R' D' L2 R'";
    private static  String shortestSolve = "L2 U  D2 R' B  U2 L  F  U  R2 D2 F2 U' L2 U  B  D  R' ";

    String cubeString , solveSteps;

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

        Intent intent = getIntent();

        if(intent != null){
          cubeString = intent.getStringExtra("cubeString");
            scrambledCube = getScrambledCube(cubeString);
            Log.d("broy",scrambledCube);
        }

        String cubeState = Min2PhaseToCubeMapping.colorMapping(scrambledCube);
        animCube.setCubeModel(cubeState);
        Log.d("brok",cubeState);

        shortestSolve = findShorterSolutions(scrambledCube);
        Log.d("broyzz", shortestSolve);

        btnSolve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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


            }
        });

    }

    public String getScrambledCube(String cubeString)
    {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < cubeString.length(); i++) {


                switch (cubeString.charAt(i)) {
                    case 'W':
                        stringBuilder.append("U");
                        break;
                    case 'R':
                        stringBuilder.append("R");
                        break;
                    case 'O':
                        stringBuilder.append("F");
                        break;
                    case 'G':
                        stringBuilder.append("D");
                        break;
                    case 'B':
                        stringBuilder.append("L");
                        break;
                    case 'Y':
                        stringBuilder.append("B");
                        break;

                }

            }

        return stringBuilder.toString();
    }

    public static String simpleSolve(String scrambledCube) {
        String result = new Search().solution(scrambledCube, 21, 100000000, 0, 0);
       return result;
        // R2 U2 B2 L2 F2 U' L2 R2 B2 R2 D  B2 F  L' F  U2 F' R' D' L2 R'
    }

    public static String findShorterSolutions(String scrambledCube) {
        //Find shorter solutions (try more probes even a solution has already been found)
        //In this example, we try AT LEAST 10000 phase2 probes to find shorter solutions.
        String result = new Search().solution(scrambledCube, 21, 100000000, 10000, 0);
        return result;
        // L2 U  D2 R' B  U2 L  F  U  R2 D2 F2 U' L2 U  B  D  R'
    }
}