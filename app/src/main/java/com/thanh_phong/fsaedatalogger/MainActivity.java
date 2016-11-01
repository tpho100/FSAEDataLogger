package com.thanh_phong.fsaedatalogger;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;

/**
 * Created by Thanh-Phong on 3/25/2016.
 */
public class MainActivity extends AppCompatActivity implements SensorEventListener,AdapterView.OnItemSelectedListener, LocationListener {

    //Main activity GUI objects
    FSAEDashboard dashboard;
    TextView myLabel;
    TextView linearAccel0;
    TextView linearAccel1;
    TextView linearAccel2;
    TextView gyro0;
    TextView gyro1;
    TextView gyro2;
    TextView gpsSpeed;

    Button connectButton;
    Button datalogButton;
    View customView;
    Spinner spinner;

    BluetoothArduinoHelper mBlue = BluetoothArduinoHelper.getInstance("HC-06");
    PE3ECUCANBus pe3;
    String value;
    int fileNumber = 0;
    String currentDriver = "Chris_Robinson";
    FileOutputStream outputStreamWriter;
    boolean loggingData = false;
    private final Handler handler = new Handler();

    private SensorManager mSensorManager;
    private SensorEventListener mSensorListener;

    private float currentX = 0;
    private float currentY = 0;
    private float currentZ = 0;

    private float deltaX = 0;
    private float deltaY = 0;
    private float deltaZ = 0;

    private float lastX = 0;
    private float lastY = 0;
    private float lastZ = 0;

    private String[] users = {"Chris_Robinson","Spencer_Lennon","Samir_Abusen","Michael_Austin","Jeffery_Bloomer","Matthew_Honickman"
            ,"David_Lasselle","Jacob_Lyon","Jacob_Muench","Wyatt_Wolf"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        pe3 = PE3ECUCANBus.getInstance();
        dashboard = new FSAEDashboard(this);
        setContentView(R.layout.activity_main);

        LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,this);

        mSensorManager = (SensorManager) this.getSystemService((Context.SENSOR_SERVICE));
        mSensorListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                Sensor sensor = event.sensor;
                if(sensor.getType() == Sensor.TYPE_ACCELEROMETER){
                    linearAccel0.setText(String.format("%.2f",event.values[0]));
                    linearAccel1.setText(String.format("%.2f",event.values[1]));
                    linearAccel2.setText(String.format("%.2f",event.values[2]));

                } else if(sensor.getType() == Sensor.TYPE_GYROSCOPE){

                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };

        connectButton = (Button) findViewById(R.id.connectButton);
        connectButton.setBackgroundColor(Color.RED);

        datalogButton = (Button) findViewById(R.id.startDatalogButton);
        datalogButton.setBackgroundColor(Color.RED);

        spinner = (Spinner) findViewById(R.id.userList);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.listview_textview,users);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        myLabel = (TextView) findViewById(R.id.label);
        linearAccel0 = (TextView) findViewById(R.id.linearAccel0);
        linearAccel1 = (TextView) findViewById(R.id.linearAccel1);
        linearAccel2 = (TextView) findViewById(R.id.linearAccel2);

        gyro0 = (TextView) findViewById(R.id.gyro0);
        gyro1 = (TextView) findViewById(R.id.gyro1);
        gyro2 = (TextView) findViewById(R.id.gyro2);

        gpsSpeed = (TextView) findViewById(R.id.gpsSpeed);
        String speed = String.valueOf(0);
        SpannableString ss1 = new SpannableString(speed+"MPH");
        ss1.setSpan(new RelativeSizeSpan(.25f), speed.length(), speed.length() + 3, 0);
        gpsSpeed.setText(ss1);

        customView = findViewById(R.id.customView);

        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    if(mBlue.isBluetoothEnabled()) {
                        mBlue.Connect();
                        if(mBlue.isAlive()){
                            Thread.sleep(500);
                            //myLabel.setText("ATTEMPTING TO LISTEN...");
                        }

                        Toast.makeText(MainActivity.this, "Connected!", Toast.LENGTH_SHORT).show();
                        Runnable runnable = new Runnable() {
                            @Override
                            public void run() {
                                while(mBlue.isAlive()){
                                    value = mBlue.msg;

                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {

                                            pe3.insertData(value);
                                            //myLabel.setText(value);
                                            customView.invalidate();

                                        }
                                    });
                                }
                            }
                        };
                        new Thread(runnable).start();
                        myLabel.setText("LISTENING");
                        connectButton.setBackgroundColor(Color.GREEN);
                    }


                } catch(Exception e) {
                    myLabel.setText("CONNECTION FAILED");
                    Toast.makeText(MainActivity.this, "Connection Failed :(", Toast.LENGTH_SHORT).show();
                    connectButton.setBackgroundColor(Color.RED);
                }
            }
        });

        datalogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(loggingData){
                    loggingData=false;
                    Toast.makeText(MainActivity.this, "Stopped Logging Data", Toast.LENGTH_LONG).show();
                    fileNumber++;
                    datalogButton.setBackgroundColor(Color.RED);
                    datalogButton.setText("START\nDATALOG");

                } else {
                    loggingData = true;
                    datalogButton.setBackgroundColor(Color.GREEN);
                    datalogButton.setText("STOP\nDATALOG");

                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            String filename = currentDriver+getTimeStamp() +"_"+ fileNumber + ".txt";
                            String dataToSave = "";
                            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),filename);

                            try{
                                outputStreamWriter = new FileOutputStream(file);
                                while(loggingData==true){
                                    //System.out.println("Logging............." + filename);
                                    dataToSave = String.format("%.2f",pe3.getRPM()) + "\t" +String.format("%.2f",pe3.getTPS())+ "\t" + String.format("%.2f",pe3.getFuelOpenTime()) +"\t"+
                                            String.format("%.2f",pe3.getIgnitionAngle())+ "\t" + String.format("%.2f",pe3.getMAP())+ "\t" + String.format("%.2f",pe3.getLambda()) +"\t"+
                                            String.format("%.2f",pe3.getAnalogInput5())+ "\t" + String.format("%.2f",pe3.getFrequency1())+ "\t" + String.format("%.2f",pe3.getBatteryVolt()) +"\t"+
                                            String.format("%.2f",pe3.getAirTemp())+"\t" + String.format("%.2f",pe3.getCoolantTemp()) +"\t"+getTimeStamp() + "\n";
                                    //System.out.println(dataToSave);
                                    outputStreamWriter.write(dataToSave.getBytes());
                                    Thread.sleep(100);

                                }
                                outputStreamWriter.close();

                            }catch(IOException e ){
                                datalogButton.setBackgroundColor(Color.RED);
                                datalogButton.setText("START\nDATALOG");
                                Toast.makeText(MainActivity.this, "Datalogging crashed: IOException Caught.", Toast.LENGTH_LONG).show();
                                loggingData = false;

                            } catch (InterruptedException e) {
                                e.printStackTrace();
                                datalogButton.setBackgroundColor(Color.RED);
                                datalogButton.setText("START\nDATALOG");
                                Toast.makeText(MainActivity.this, "Datalogging crashed: Interrupted Thread.", Toast.LENGTH_LONG).show();
                                loggingData = false;
                            }
                        }
                    };
                    new Thread(runnable).start();
                    Toast.makeText(MainActivity.this, "Started Logging Data", Toast.LENGTH_LONG).show();
                }


            }
        });


    }

    @Override
    public void onSensorChanged(SensorEvent e){

        linearAccel0.setText("ACCEL_X: " + String.format("%.2f",e.values[0]));
        linearAccel1.setText("ACCEL_Y: " + String.format("%.2f",e.values[1]));
        linearAccel2.setText("ACCEL_Z: " + String.format("%.2f",e.values[2]));

        deltaX = Math.abs(lastX-e.values[0]);
        deltaY = Math.abs(lastY-e.values[1]);
        deltaZ = Math.abs(lastZ-e.values[2]);

        if(deltaX < 2){
            deltaX = 0;
        }
        if(deltaY < 2){
            deltaY = 0;
        }
        if (deltaZ < 2){
            deltaZ = 0;
        }

        lastX = e.values[0];
        lastY = e.values[1];
        lastZ = e.values[2];
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public static String getTimeStamp() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss_SSS");
        String dateString = formatter.format(new java.util.Date());
        return dateString;
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        currentDriver = (String) parent.getItemAtPosition(position);
        System.out.println("NEW DRIVER== "+currentDriver);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onLocationChanged(Location location) {

        if(location == null){
            String speed = String.valueOf(0);
            SpannableString ss1 = new SpannableString(speed+"MPH");
            ss1.setSpan(new RelativeSizeSpan(.25f),speed.length(),speed.length()+3,0);
            gpsSpeed.setText(ss1);

        } else {
            float mCurrentSpeed = location.getSpeed()*2.23696F;
            int speedRounded = Math.round(mCurrentSpeed);
            String speed = String.valueOf(speedRounded);
            SpannableString ss1 = new SpannableString(speed+"MPH");
            ss1.setSpan(new RelativeSizeSpan(.25f),speed.length(),speed.length()+3,0);
            gpsSpeed.setText(ss1);

            //customView.invalidate();
            //System.out.println("GPS location changed: "+dashboard.GPSSpeed);
        }

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
