package com.example.kaiwen.shakie;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.view.View.OnClickListener;
import android.widget.Button;
import java.lang.Math;
import android.widget.EditText;
import android.widget.Toast;
import android.media.*;
import java.io.IOException;

public class MainActivity extends AppCompatActivity implements SensorEventListener, OnClickListener {

    double x, y, z;
    double airP;

    // low pass filter
    private Sensor accelerometer;
    private Sensor barometer;
    private SensorManager sm;
    private TextView textView2;
    private TextView textView4;
    private TextView textView6;
    private EditText edit_message;
    Button button_start;
    Button button_stop;
    Button button_baro;
    double Threshold;

    MediaPlayer crash;
    MediaPlayer snare;
    MediaPlayer tom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button_start = (Button) findViewById(R.id.button_start);
        button_stop = (Button) findViewById(R.id.button_stop);
        button_baro = (Button) findViewById(R.id.button_baro);

        button_start.setOnClickListener(this);
        button_stop.setOnClickListener(this);
        button_baro.setOnClickListener(this);


        // create our sensor manager
        sm = (SensorManager) getSystemService(SENSOR_SERVICE);

        // accelerometer, barometer sensor
        accelerometer = sm.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        barometer = sm.getDefaultSensor(Sensor.TYPE_PRESSURE);

        // assign textView
        textView2 = (TextView)findViewById(R.id.textView2);
        textView4 = (TextView)findViewById(R.id.textView4);
        textView6 = (TextView)findViewById(R.id.textView6);

        Threshold = 1.0;

        if (crash != null) {
            return;
        }
        crash = MediaPlayer.create(getApplicationContext(), R.raw.crash1);

        snare = MediaPlayer.create(getApplicationContext(), R.raw.snare1);

        tom = MediaPlayer.create(getApplicationContext(), R.raw.tom1);
    }

    protected void onResume() {
        super.onResume();
        // register sensor listener
        if (accelerometer != null) {
            sm.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
        else {
            Toast.makeText(this, "Your accelerometer is not available", Toast.LENGTH_SHORT).show();
        }

        if (barometer != null) {
            sm.registerListener(this, barometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
        else {
            Toast.makeText(this, "Your barometer is not available", Toast.LENGTH_SHORT).show();
        }
    }

    protected void onPause() {
        super.onPause();
        sm.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //TODO Auto-generated method stub
        // not in use
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // TODO Auto-generated method stub
        Log.d("sensorchanged",event.toString());

        if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            x = event.values[0];
            y = event.values[1];
            z = event.values[2];
        }
        Log.d("sensor", "here");
        if (event.sensor.getType() == Sensor.TYPE_PRESSURE) {
            airP = event.values[0];
        }

        //Math.sqrt(Math.pow(x,2)+Math.pow(y,2)+Math.pow(z,2))
        double shake = Math.sqrt(x*x+y*y+z*z);

        textView2.setText("X: " + x + (x>=Threshold ? " (SHAKING)" : "")
                    + "\nY: " + y  + (y>=Threshold ? " (SHAKING)" : "")
                    + "\nZ: " + z  + (z>=Threshold ? " (SHAKING)" : "") );

        if (x>=Threshold) {
            play(snare);
        }
        if (y>=Threshold) {
            play(tom);
        }
        if (z>=Threshold) {
            play(crash);
        }
//        else if (shake < Threshold) {
//            textView4.setText("No Shake!");
//        }
//        else {
//            textView2.setText("X: " + x + "\nY: " + y + "\nZ: " + z);
//            textView4.setText("You Are Shaking!");
//        }

    }

    private void play(MediaPlayer m) {
        if (m.isPlaying()) {
            m.pause();
            m.seekTo(0);
        }
        m.start();
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.button_start:

                edit_message = (EditText) findViewById(R.id.edit_message);
                if (edit_message.getText().toString().trim().length() > 0) {
                    Threshold = Double.parseDouble(edit_message.getText().toString());
                }

                break;

            case R.id.button_baro:
                sm.registerListener(this, barometer, SensorManager.SENSOR_DELAY_NORMAL);
                Log.d("Presssure", ""+airP);
                textView6.setText("Air Pressure: " + airP);
        }
    }
}