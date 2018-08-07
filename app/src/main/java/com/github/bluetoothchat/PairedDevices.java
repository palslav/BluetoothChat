package com.github.bluetoothchat;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Set;

public class PairedDevices extends AppCompatActivity{

    Button buttonShowPaired;
    BluetoothAdapter myBluetoothAdapter;
    ListView pairedListView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.paired_devices);

        buttonShowPaired = (Button) findViewById(R.id.btShowPaired);
        pairedListView = (ListView) findViewById(R.id.btPairedListView);

        myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        bluetoothShowPairedMethod();
    }

    private void bluetoothShowPairedMethod() {
        buttonShowPaired.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (myBluetoothAdapter != null) {
                    if (myBluetoothAdapter.isEnabled()) {

                        Set<BluetoothDevice> bluetoothDevices = myBluetoothAdapter.getBondedDevices();
                        String[] bondedDevices = new String[bluetoothDevices.size()];
                        int index = 0;

                        if (bluetoothDevices.size() > 0) {
                            for (BluetoothDevice device : bluetoothDevices) {
                                bondedDevices[index++] = device.getName();
                            }
                        }

                        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, bondedDevices);
                        pairedListView.setAdapter(arrayAdapter);
                    } else {
                        Toast.makeText(getApplicationContext(), "Enable Bluetooth", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }
}
