package com.github.bluetoothchat;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Button buttonON, buttonOFF, buttonToPaired, buttonToShowAll, buttonMakeDiscoverable;
    BluetoothAdapter myBluetoothAdapter;
    Intent btEnableIntent;
    int requestCodeForEnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonON = (Button) findViewById(R.id.btON);
        buttonOFF = (Button) findViewById(R.id.btOFF);
        buttonToPaired = (Button) findViewById(R.id.btToPaired);
        buttonToShowAll = (Button) findViewById(R.id.btToShowAll);
        buttonMakeDiscoverable = (Button) findViewById(R.id.btMakeDiscoverable);

        myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        btEnableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        requestCodeForEnable = 1;

        bluetoothONMethod();
        bluetoothOFFMethod();
        bluetoothToShowAllMethod();
        bluetoothToPairedMethod();
        bluetoothMakeDiscoverable();
    }

    private void bluetoothMakeDiscoverable() {
        buttonMakeDiscoverable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
                startActivity(intent);
            }
        });
    }

    private void bluetoothToPairedMethod() {
        buttonToPaired.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), PairedDevices.class);
                startActivity(intent);
            }
        });
    }

    private void bluetoothToShowAllMethod() {
        buttonToShowAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ShowAllDevices.class);
                startActivity(intent);
            }
        });
    }

    private void bluetoothOFFMethod() {
        buttonOFF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (myBluetoothAdapter.isEnabled()) {
                    myBluetoothAdapter.disable();
                    Toast.makeText(getApplicationContext(), "Bluetooth is Disabled", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == requestCodeForEnable) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(getApplicationContext(), "Bluetooth is Enabled", Toast.LENGTH_LONG).show();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getApplicationContext(), "Unable to Enable Bluetooth", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void bluetoothONMethod() {
        buttonON.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (myBluetoothAdapter == null) {
                    Toast.makeText(getApplicationContext(), " Bluetooth is not supported", Toast.LENGTH_LONG).show();
                } else {
                    if (!myBluetoothAdapter.isEnabled()) {
                        startActivityForResult(btEnableIntent, requestCodeForEnable);
                    }
                }
            }
        });
    }
}
