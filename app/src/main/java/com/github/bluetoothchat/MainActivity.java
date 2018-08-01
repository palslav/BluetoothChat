package com.github.bluetoothchat;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Button buttonON, buttonOFF;
    BluetoothAdapter myBluetoothAdapter;
    Intent btEnableIntent;
    int requestCodeForEnable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonON = (Button) findViewById(R.id.btON);
        buttonOFF = (Button) findViewById(R.id.btOFF);

        myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        btEnableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        requestCodeForEnable = 1;

        bluetoothONMethod();
        bluetoothOFFMethod();
    }

    private void bluetoothOFFMethod() {
        buttonOFF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(myBluetoothAdapter.isEnabled()){
                    myBluetoothAdapter.disable();
                    Toast.makeText(getApplicationContext(), "Bluetooth is Disabled", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==requestCodeForEnable){
            if(resultCode==RESULT_OK){
                Toast.makeText(getApplicationContext(), "Bluetooth is Enabled", Toast.LENGTH_LONG).show();
            } else if(resultCode==RESULT_CANCELED){
                Toast.makeText(getApplicationContext(), "Unable to Enable Bluetooth", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void bluetoothONMethod() {
        buttonON.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(myBluetoothAdapter==null){
                    Toast.makeText(getApplicationContext(), " Bluetooth is not supported", Toast.LENGTH_LONG).show();
                } else {
                    if(!myBluetoothAdapter.isEnabled()){
                        startActivityForResult(btEnableIntent, requestCodeForEnable);
                    }
                }
            }
        });
    }
}
