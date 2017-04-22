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
    private boolean initialized;
    Button button_start;
    Button button_stop;
    Button button_baro;
    double Threshold;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        button_start = (Button) findViewById(R.id.button_start);
        button_stop = (Button) findViewById(R.id.button_stop);
        button_baro = (Button) findViewById(R.id.button_baro);

        button_start.setOnClickListener(this);
        button_stop.setOnClickListener(this);
        button_baro.setOnClickListener(this);
        initialized = false;


        // create our sensor manager
        sm = (SensorManager) getSystemService(SENSOR_SERVICE);

        // accelerometer, barometer sensor
        accelerometer = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        barometer = sm.getDefaultSensor(Sensor.TYPE_PRESSURE);

        // assign textView
        textView2 = (TextView)findViewById(R.id.textView2);
        textView4 = (TextView)findViewById(R.id.textView4);
        textView6 = (TextView)findViewById(R.id.textView6);
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

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
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

        if (!initialized){
            textView2.setText("0.0");
            textView4.setText("");
            textView6.setText("");
        }
        else if (shake < Threshold) {
            textView4.setText("No Shake!");
        }
        else {
            textView2.setText("X: " + x + "\nY: " + y + "\nZ: " + z);
            textView4.setText("You Are Shaking!");
        }

    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.button_start:

                edit_message = (EditText) findViewById(R.id.edit_message);
                initialized = true;


                if (edit_message.getText().toString().trim().length() > 0) {
                    Threshold = Double.parseDouble(edit_message.getText().toString());
                }
                else {
                    Threshold = 10;
                }

                sm.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
                break;

            case R.id.button_stop:
                sm.unregisterListener(this);
                textView4.setText("You Stopped Shakie Detector!");
                textView6.setText("");
                break;

            case R.id.button_baro:
                sm.registerListener(this, barometer, SensorManager.SENSOR_DELAY_NORMAL);
                Log.d("Presssure", ""+airP);
                textView6.setText("Air Pressure: " + airP);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}