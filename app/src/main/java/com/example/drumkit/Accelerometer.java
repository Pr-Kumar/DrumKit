package com.example.drumkit;

import static android.hardware.SensorManager.DATA_X;
import static android.hardware.SensorManager.DATA_Y;
import static android.hardware.SensorManager.DATA_Z;
import static android.hardware.SensorManager.SENSOR_STATUS_ACCURACY_HIGH;
import static android.hardware.SensorManager.SENSOR_STATUS_ACCURACY_LOW;
import static android.hardware.SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM;
import static android.hardware.SensorManager.SENSOR_STATUS_UNRELIABLE;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

/**
 * Displays values from the accelerometer sensor.
 *
 */
public class Accelerometer
        extends Activity
        implements SensorEventListener, StepListener {
    private SensorManager sensorManager;
    private Sensor accelerometer;

    private TextView accuracyLabel;
    private TextView xLabel, yLabel, zLabel, absLabel;
    private TextView sensorName;
    private TextView sample;
    private ProgressBar filter;
    private ProgressBar filterBar1;
    protected SensorEventListener sensorEventListener;
    private float currentSample;
    private float currentFilter;
    private Timer samplingTimer;

    private float x, y, z;
    public int i = 0;
    private long lastUpdate = -1;

    private TextView textView;
    private StepDetector simpleStepDetector;
    private SensorManager sensorManager2;
    private Sensor accel;
    private static final String TEXT_NUM_STEPS = "Number of Steps: ";
    private int numSteps;TextView TvSteps;
    TextView BtnStart;
    TextView BtnStop;
    MediaPlayer mpsnare;
    MediaPlayer mpcrash;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        // Get an instance of the SensorManager
        sensorManager2 = (SensorManager) getSystemService(SENSOR_SERVICE);
        accel = sensorManager2.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        simpleStepDetector = new StepDetector();
        simpleStepDetector.registerListener(this);
        setContentView(R.layout.activity_main);
        findViews();
        //filterBar1.setVisibility(View.INVISIBLE);

        BtnStart.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                numSteps = 0;
                sensorManager2.registerListener(Accelerometer.this, accel, SensorManager.SENSOR_DELAY_FASTEST);

            }
        });


        BtnStop.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                sensorManager2.unregisterListener(Accelerometer.this);

            }
        });



    }


    public void postData(int i) {
        // Create a new HttpClient and Post Header
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("http://d232dcbb.ngrok.io/integers");
        Log.d("testPost","this kinda works");
        try {
            // Add your data
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("integers", ""+i));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httppost);

        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
        } catch (IOException e) {
            // TODO Auto-generated catch block
        }
    }

//    @Override
//    public void onSensorChanged(SensorEvent event) {
//        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
//            simpleStepDetector.updateAccel(
//                    event.timestamp, event.values[0], event.values[1], event.values[2]);
//        }
//    }

    @Override
    public void step(long timeNs) {

        mpsnare = MediaPlayer.create(this, R.raw.snare);
        mpcrash = MediaPlayer.create(this, R.raw.crash);
        if(x < -6) {mpsnare.start(); i = 1;}
        if(x > 6 & z < -7) {mpcrash.start(); i = 2;}
        Log.d("values","\n-------\n");
        Log.d("Xvalue", Float.toString(x));
        Log.d("Yvalue", Float.toString(y));
        Log.d("Zvalue", Float.toString(z));

        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try  {
                    postData(i);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();


        numSteps++;
        TvSteps.setText(TEXT_NUM_STEPS + numSteps);
    }


    @Override
    protected void onResume() {
        super.onResume();
        String name = accelerometer.getName();
        sensorName.setText(name);

        //int rate = SensorManager.SENSOR_DELAY_NORMAL; // ~ 200-400 msec
        int rate = SensorManager.SENSOR_DELAY_FASTEST; // ~ 10 msec
        sensorManager.registerListener(this, accelerometer, rate);

        samplingTimer = new Timer();
        long delay = 0;
        long period = 83;
        samplingTimer.scheduleAtFixedRate(new Filter(), delay, period);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
        if (samplingTimer != null) {
            samplingTimer.cancel();
        }
    }

    @Override // SensorEventListener
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            switch (accuracy) {
                case SENSOR_STATUS_UNRELIABLE:
                    accuracyLabel.setText(R.string.accuracy_unreliable);
                    break;
                case SENSOR_STATUS_ACCURACY_LOW:
                    accuracyLabel.setText(R.string.accuracy_low);
                    break;
                case SENSOR_STATUS_ACCURACY_MEDIUM:
                    accuracyLabel.setText(R.string.accuracy_medium);
                    break;
                case SENSOR_STATUS_ACCURACY_HIGH:
                    accuracyLabel.setText(R.string.accuracy_high);
                    break;
            }
        }
    }

    @Override // SensorEventListener
    //public void onSensorChanged(SensorEvent sensorEvent, float[] values) {
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            long curTime = System.currentTimeMillis();
            // only allow one update every 100ms, otherwise updates
            // come way too fast and the phone gets bogged down
            // with garbage collection
            //if (lastUpdate == -1 || (curTime - lastUpdate) > 100) {
            lastUpdate = curTime;

            x = sensorEvent.values[DATA_X];
            y = sensorEvent.values[DATA_Y];
            z = sensorEvent.values[DATA_Z];
            float abs = new Float(Math.sqrt(x*x + y*y + z*z));
            currentSample = x;

            xLabel.setText(String.format("X: %+2.5f", x));
            yLabel.setText(String.format("Y: %+2.5f", y));
            zLabel.setText(String.format("Z: %+2.5f", z));
            absLabel.setText(String.format("ABS: %+2.5f ", abs));
            int progress = 100 * (int) (Math.sqrt(currentFilter * currentFilter));
            filter.setProgress(progress);
            //}
        }

        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            simpleStepDetector.updateAccel(
                    sensorEvent.timestamp, sensorEvent.values[0], sensorEvent.values[1], sensorEvent.values[2]);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem menu) {
        switch (menu.getItemId()) {
            case R.id.sensor_list:
                startActivity(new Intent(this, SensorListActivity.class));
        }
        return false;
    }
    protected void findViews() {
        sensorName = (TextView) findViewById(R.id.name_label);
        accuracyLabel = (TextView) findViewById(R.id.accuracy_label);
        xLabel = (TextView) findViewById(R.id.x_label);
        yLabel = (TextView) findViewById(R.id.y_label);
        zLabel = (TextView) findViewById(R.id.z_label);
        absLabel = (TextView) findViewById(R.id.abs_label);
        sample = (TextView) findViewById(R.id.sample_label);
        filter = (ProgressBar) findViewById(R.id.filter_label);
        //filterBar1 = (ProgressBar) findViewById(R.id.filter_bar_1);

        TvSteps = (TextView) findViewById(R.id.tv_steps);
        BtnStart = (Button) findViewById(R.id.btn_start);
        BtnStop = (Button) findViewById(R.id.btn_stop);

    }

    protected void displayRawData(float[] values) {
        x = values[DATA_X];
        y = values[DATA_Y];
        z = values[DATA_Z];
        float abs = new Float(Math.sqrt(x*x + y*y + z*z));

        xLabel.setText(String.format("X: %+2.5f", x));
        yLabel.setText(String.format("Y: %+2.5f", y));
        zLabel.setText(String.format("Z: %+2.5f", z));
        absLabel.setText(String.format("ABS: %+2.5f ", abs));
    }

    /**
     * A digital recursive band-pass filter with sampling frequency of 12 Hz,
     * center 3.6 Hz, bandwidth 3 Hz, low cut-off 2.1 Hz, high cut-off 5.1 Hz.
     *
     * Source: http://www.dspguide.com/ch19/3.htm
     */
    class Filter
            extends TimerTask {
        private final float a0 = (float) +0.535144118;
        private final float a1 = (float) +0.132788237;
        private final float a2 = (float) -0.402355882;
        private final float b1 = (float) -0.154508496;
        private final float b2 = (float) -0.062500000;

        private float x0, x1, x2;
        private float y0, y1, y2;

        @Override
        public void run() {
            x0 = currentSample;
            y0 = a0 * x0 + a1 * x1 + a2 * x2
                    + b1 * y1 + b2 * y2;
            currentFilter = y0;
            x2 = x1;
            x1 = x0;
            y2 = y1;
            y1 = y0;
        }
    }
}
