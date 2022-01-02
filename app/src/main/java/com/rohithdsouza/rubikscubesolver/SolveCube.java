package com.rohithdsouza.rubikscubesolver;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.catalinjurjiu.animcubeandroid.AnimCube;
import com.ornach.nobobutton.NoboButton;

import min2phase.Search;

public class SolveCube extends AppCompatActivity {
    AnimCube animCube;
    NoboButton btnSolve;
    TextView txtMoves;
    TextView txtState;
    TextView txtNoOfMoves1;
    TextView txtNoOfMoves2;

    private static  String scrambledCube = "" ;
    private static  String shortestSolve = "";
    private static  String noOfMoves = "";

    String cubeString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(1);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getWindow().setStatusBarColor(Color.TRANSPARENT);

        setContentView(R.layout.activity_solve);

        animCube = findViewById(R.id.animcube);
        txtMoves = findViewById(R.id.txt_moves);
        txtState = findViewById(R.id.txt_initial_state);
        btnSolve = findViewById(R.id.button);
        txtNoOfMoves1 = findViewById(R.id.txt_No_of_moves);
        txtNoOfMoves2 = findViewById(R.id.txt_No_of_moves2);

        Intent intent = getIntent();

        //Obtain Scanned Cube
        if(intent != null){
          cubeString = intent.getStringExtra("cubeString");
            scrambledCube = getScrambledCube(cubeString);
            Log.d("Solve Cube: Scrambledcube-",scrambledCube);
        }

        String cubeState = Min2PhaseToCubeMapping.colorMapping(scrambledCube);
        animCube.setCubeModel(cubeState);
        Log.d("Solve Cube: cubeState-",cubeState);

        shortestSolve = findShorterSolutions(scrambledCube);
        Log.d("Solve Cube: shortestSolve-", shortestSolve);

        noOfMoves = "" + numberOfMoves(shortestSolve);

        btnSolve.setOnClickListener(v -> {
            txtMoves.setText(shortestSolve);
            txtState.setText(R.string.solution_state);

            if (shortestSolve.charAt(0) == 'E') {
                txtNoOfMoves1.setText("You have not Scanned the cube properly, please scan the cube again");
            }

            else {
                txtNoOfMoves1.setText("Number of Moves :");
                txtNoOfMoves2.setText(noOfMoves);
            }


            Handler handler = new Handler();
            handler.postDelayed(() -> {
                animCube.setMoveSequence(shortestSolve);
                animCube.animateMoveSequence();
            }, 500);
        });

    }
    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        startActivity(new Intent(SolveCube.this, MainActivity.class));
        finish();
    }

    // Map Scanned Cube (Color) to Color invariant (Min2Phase) eg: "RWR.." to "URU.."
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

    public static String findShorterSolutions(String scrambledCube) {
        //Find shorter solutions (try more probes even a solution has already been found)
        //In this example, we try AT LEAST 10000 phase2 probes to find shorter solutions.
        return new Search().solution(scrambledCube, 21, 100000000, 10000, 0);
        //Eg: L2 U  D2 R' B  U2 L  F  U  R2 D2 F2 U' L2 U  B  D  R'
    }

    public static int numberOfMoves(String moves ) {
        int count = 0;
        // StringBuilder moves = new StringBuilder(movesX);
        if (moves.charAt(0) == 'E')
            return count;
        else {
            for (int i = 0; i < moves.length() - 1; i++) {
                if(i ==0 && moves.charAt(0)==' ')
                    continue;
                if (moves.charAt(i) == ' ' && moves.charAt(i + 1) != ' ')
                    count++;
            }

            return count + 1;
        }
    }



}