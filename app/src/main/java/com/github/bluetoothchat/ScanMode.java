package com.github.bluetoothchat;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ScanMode extends AppCompatActivity {

    Button btScanMode;
    TextView tvScanMode;
    IntentFilter scanIntentFilter = new IntentFilter(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scan_mode);

        btScanMode = (Button) findViewById(R.id.btScanMode);
        tvScanMode = (TextView) findViewById(R.id.tvScanMode);

        btScanMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //tvScanMode.setText("");
                Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 5);
                startActivity(discoverableIntent);
            }
        });

        registerReceiver(scanModeReceiver, scanIntentFilter);
    }

    BroadcastReceiver scanModeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED)){
                int modeValue = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, BluetoothAdapter.ERROR);

                if(modeValue==BluetoothAdapter.SCAN_MODE_CONNECTABLE){
                    tvScanMode.setText("The device is not in discoverable mode but can still receive connections");
                } else if (modeValue==BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE){
                    tvScanMode.setText("The device is in discoverable Mode");
                } else if (modeValue==BluetoothAdapter.SCAN_MODE_NONE){
                    tvScanMode.setText("The device is neither in discoverable mode nor receiving connections");
                } else {
                    tvScanMode.setText("Error");
                }
            }
        }
    };
}