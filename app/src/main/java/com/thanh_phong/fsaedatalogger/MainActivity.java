package com.thanh_phong.fsaedatalogger;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import java.util.UUID;


public class MainActivity extends AppCompatActivity {

    //Main activity GUI objects
    FSAEDashboard dashboard;
    TextView myLabel;
    Button connectButton;

    // Standard UUID DO NOT EDIT UNLESS YOU KNOW WHAT YOU'RE DOING
    UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
    //EDIT THIS TO MATCH BLUETOOTH NAME
    String deviceName = "HC-06";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        dashboard = new FSAEDashboard(this);
        setContentView(R.layout.activity_main);

        connectButton = (Button) findViewById(R.id.connectButton);
        myLabel = (TextView) findViewById(R.id.label);

        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Start bluetooth connection
                //Start new thread to monitor connection
                //Update PE3ECUCANBus data structure
                //Redraw GUI upon update

            }
        });

        //imageView.setImageResource(R.drawable.coolanttemp1);
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
