//package com.sem6.mad.rubikscubesolver;
//
//import android.app.Activity;
//import android.os.Bundle;
//
//import io.fotoapparat.parameter.Size;
//
//import com.catalinjurjiu.rubikdetector.RubikDetector;
//import com.catalinjurjiu.rubikdetector.config.DrawConfig;
//import com.catalinjurjiu.rubikdetectorfotoapparatconnector.FotoApparatConnector;
//import com.catalinjurjiu.rubikdetectorfotoapparatconnector.view.RubikDetectorResultView;
//
//
//public class FotoApparatActivity extends Activity {
//
//    private static final String TAG = FotoApparatActivity.class.getSimpleName();
//    private Fotoapparat fotoapparat;
//    private RubikDetector rubikDetector;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_foto_apparat);
//
//        rubikDetector = new RubikDetector.Builder()
//                .drawConfig(DrawConfig.FilledCircles())
//                .debuggable(true)
//                .build();
//
//        RubikDetectorResultView rubikDetectorResultView = findViewById(R.id.rubik_results_view);
//
//        fotoapparat = FotoApparatConnector.configure(Fotoapparat.with(this.getBaseContext()), rubikDetector, rubikDetectorResultView)
//                .build();
//    }
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//        fotoapparat.start();
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        fotoapparat.stop();
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        rubikDetector.releaseResources();
//    }
//
//}
