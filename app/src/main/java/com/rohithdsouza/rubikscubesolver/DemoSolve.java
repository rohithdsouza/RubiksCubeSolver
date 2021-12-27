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
import com.yarolegovich.lovelydialog.LovelyInfoDialog;

public class DemoSolve extends AppCompatActivity {
    AnimCube animCube;
    Button btnSolve;
    TextView txtMoves;
    TextView txtStateLabel;

    private static final String scrambledCube = "DUUBULDBFRBFRRULLLBRDFFFBLURDBFDFDRFRULBLUFDURRBLBDUDL";
    private static String shortestSolve;

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

        String cubeState = Min2PhaseToCubeMapping.colorMapping(scrambledCube);

        animCube.setCubeModel(cubeState);
        Log.d("CubeState",cubeState);

        shortestSolve = findShorterSolutions(scrambledCube);

        btnSolve.setOnClickListener(v -> {

            txtMoves.setText(shortestSolve);
            txtStateLabel.setText(R.string.solution_state);

            Handler handler = new Handler();
            handler.postDelayed(() -> {
                animCube.setMoveSequence(shortestSolve);
                animCube.animateMoveSequence();
            }, 500);
        });
    }
}
