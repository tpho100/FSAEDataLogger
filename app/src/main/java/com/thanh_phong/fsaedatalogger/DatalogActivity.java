package com.thanh_phong.fsaedatalogger;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;

public class DatalogActivity extends AppCompatActivity {



    ArrayList<String> dataArray = new ArrayList<String>();
    Button changeViewButton2;
    PE3ECUCANBus pe3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_datalog);
        pe3 = PE3ECUCANBus.getInstance();

        String[] dataArray1 = {"RPM: "+pe3.getRPM(), "TPS: "+pe3.getTPS(), "Fuel Open Time: "+pe3.getFuelOpenTime(), "Ignition Angle: "+pe3.getIgnitionAngle()};
        String[] dataArray2 = {"Barometer: "+pe3.getBarometer(), "MAP: "+pe3.getMAP(), "Lambda: "+pe3.getLambda()};
        String[] dataArray3 = {"Analog Input #1: "+pe3.getAnalogInput1(),"Analog Input #2: "+pe3.getAnalogInput2(),"Analog Input #3: "+pe3.getAnalogInput3(),"Analog Input #4: "+pe3.getAnalogInput4()};
        String[] dataArray4 = {"Analog Input #5: "+pe3.getAnalogInput5(),"Analog Input #6: "+pe3.getAnalogInput6(),"Analog Input #7: "+pe3.getAnalogInput7(),"Analog Input #8: "+pe3.getAnalogInput8()};
        String[] dataArray5 = {"Frequency 1: "+pe3.getFrequency1(),"Frequency 2: "+pe3.getFrequency2(),"Frequency 3: "+pe3.getFrequency3(),"Frequency 4: "+pe3.getFrequency4()};
        String[] dataArray6 = {"Battery Volt: "+pe3.getBatteryVolt(), "Air Temp: "+pe3.getAirTemp(), "Coolant Temp: "+pe3.getCoolantTemp()};

        Collections.addAll(dataArray, dataArray1);
        Collections.addAll(dataArray,dataArray2);
        Collections.addAll(dataArray,dataArray3);
        Collections.addAll(dataArray,dataArray4);
        Collections.addAll(dataArray,dataArray5);
        Collections.addAll(dataArray,dataArray6);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.listview_textview,dataArray);
        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);

        changeViewButton2 = (Button) findViewById(R.id.changeViewButton2);
        changeViewButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("clicking...");
                startActivity(new Intent(DatalogActivity.this, MainActivity.class));
            }
        });

    }


}
