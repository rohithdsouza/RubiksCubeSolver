package com.rohithdsouza.rubikscubesolver;

import static com.rohithdsouza.rubikscubesolver.SolveCube.findShorterSolutions;

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
import com.yarolegovich.lovelydialog.LovelyInfoDialog;

public class DemoSolve extends AppCompatActivity {
    AnimCube animCube;
    NoboButton btnSolve;
    TextView txtMoves;
    TextView txtStateLabel;
    TextView txtNoOfMoves1;
    TextView txtNoOfMoves2;

    private static final String scrambledCube = "DUUBULDBFRBFRRULLLBRDFFFBLURDBFDFDRFRULBLUFDURRBLBDUDL";
    private static String shortestSolve;
    private static  String noOfMoves = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(1);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getWindow().setStatusBarColor(Color.TRANSPARENT);

        // Tutorial Dialog on how to solve cube
        new LovelyInfoDialog(this)
                .setTopColorRes(R.color.teal_200)
                .setNotShowAgainOptionEnabled(0)
                .setNotShowAgainOptionChecked(false)
                .setTitle(R.string.info_title)
                .setMessage(R.string.info_message)
                .show();

        setContentView(R.layout.activity_solve);

        animCube = findViewById(R.id.animcube);
        txtMoves = findViewById(R.id. txt_moves);
        txtStateLabel = findViewById(R.id.txt_initial_state);
        btnSolve = findViewById(R.id.button);
        txtNoOfMoves1 = findViewById(R.id.txt_No_of_moves);
        txtNoOfMoves2 = findViewById(R.id.txt_No_of_moves2);

        String cubeState = Min2PhaseToCubeMapping.colorMapping(scrambledCube);

        animCube.setCubeModel(cubeState);
        Log.d("CubeState",cubeState);

        shortestSolve = findShorterSolutions(scrambledCube);
        noOfMoves = ""+ numberOfMoves(shortestSolve);

        btnSolve.setOnClickListener(v -> {

            txtMoves.setText(shortestSolve);
            txtStateLabel.setText(R.string.solution_state);
            txtNoOfMoves1.setText("Number of Moves :");
            txtNoOfMoves2.setText(noOfMoves);

            Handler handler = new Handler();
            handler.postDelayed(() -> {
                animCube.setMoveSequence(shortestSolve);
                animCube.animateMoveSequence();
            }, 500);
        });
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
