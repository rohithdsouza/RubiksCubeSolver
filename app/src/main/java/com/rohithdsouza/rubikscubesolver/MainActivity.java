package com.rohithdsouza.rubikscubesolver;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    CardView card1,card2;
    private static final int PERMISSION_REQUEST_CODE = 200; //camera permission
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(1);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getWindow().setStatusBarColor(Color.TRANSPARENT);

        setContentView(R.layout.activity_main);

        card1 = findViewById(R.id.card1);
        card2 = findViewById(R.id.card2);

        card1.setOnClickListener( view -> {
            Intent intent = new Intent(MainActivity.this,
                    LiveDetectionActivity.class);
            startActivity(intent);
        });

        card2.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this,
                    DemoSolve.class);
            startActivity(intent);
        });


        cameraPermissionCheck();
    }

    ////////////////////////////////// FUNCTIONS /////////////////////////////////////

    // Check Camera permission
    public void cameraPermissionCheck()
    {
        if (!checkPermission())
        {
            requestPermission();
        }
    }

    // requestPermission -> calls onRequestPermissionResult [returns grantResult empty if denied]
    public boolean  checkPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA},
                PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode)
        {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_SHORT).show();

                } else
                    {
                    Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_SHORT).show();

                    //Permission Denied -Show AlertDialog to pick allow
                        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                                != PackageManager.PERMISSION_GRANTED)
                        {
                            showMessageOKCancel("The app requires camera permission to Scan Cube, Please allow Camera access by clicking 'Allow",
                                    (dialog, which) -> requestPermission());
                        }
                    }
                break;
                }
        }

        //AlertDialog after permission denied
    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

}