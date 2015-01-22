
package com.example.waveview;

import android.app.Activity;
import android.os.Bundle;

import com.example.waveview.entity.WaveView;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        //add by xixi
        WaveView wv = (WaveView) findViewById(R.id.wave_view);
        //set the circle center first
        wv.setCenterPoint(500, 500);
        //then start the wave
        wv.startWaves();
        
    }

}
