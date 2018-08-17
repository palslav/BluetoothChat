package com.github.bluetoothchat;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class PairedDevices extends AppCompatActivity{

    Button buttonShowPaired, buttonListen, buttonSend;
    ListView pairedListView;
    EditText etWriteMsg;
    TextView tvStatus, tvShowMessage;

    BluetoothAdapter myBluetoothAdapter;
    BluetoothDevice[] btArray;

    SendReceive sendReceive;

    static final int STATE_LISTENING = 1;
    static final int STATE_CONNECTING = 2;
    static final int STATE_CONNECTED = 3;
    static final int STATE_CONNECTION_FAILED = 4;
    static final int STATE_MESSAGE_RECEIVED = 5;

    private static final String APP_NAME = "BTChat";
    private static final UUID MY_UUID = UUID.fromString("0c1f2b86-a0a7-11e8-98d0-529269fb1459");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.paired_devices);

        buttonShowPaired = (Button) findViewById(R.id.btShowPaired);
        buttonListen = (Button) findViewById(R.id.btListen);
        buttonSend = (Button) findViewById(R.id.btSendMsg);
        pairedListView = (ListView) findViewById(R.id.btPairedListView);
        etWriteMsg = (EditText) findViewById(R.id.btWriteMsg);
        tvStatus = (TextView) findViewById(R.id.btStatus);
        tvShowMessage = (TextView) findViewById(R.id.btShowMsg);

        myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        bluetoothImplementListeners();
    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {

            switch(message.what){
                case STATE_LISTENING:
                    tvStatus.setText("Listening");
                    break;
                case STATE_CONNECTING:
                    tvStatus.setText("Connecting");
                    break;
                case STATE_CONNECTED:
                    tvStatus.setText("Connected");
                    break;
                case STATE_CONNECTION_FAILED:
                    tvStatus.setText("Connection Failed");
                    break;
                case STATE_MESSAGE_RECEIVED:
                    byte[] readBuffer = (byte[]) message.obj;
                    String msg = new String(readBuffer, 0, message.arg1);
                    String showmsg = (String)tvShowMessage.getText();
                    showmsg += "\n" + msg;
                    tvShowMessage.setText(showmsg);
                    break;
            }
            return true;
        }
    });

    private void bluetoothImplementListeners() {
        buttonShowPaired.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            if (myBluetoothAdapter != null) {
                if (myBluetoothAdapter.isEnabled()) {

                    Set<BluetoothDevice> bluetoothDevices = myBluetoothAdapter.getBondedDevices();
                    String[] bondedDevices = new String[bluetoothDevices.size()];
                    btArray = new BluetoothDevice[bluetoothDevices.size()];
                    int index = 0;

                    if (bluetoothDevices.size() > 0) {
                        for (BluetoothDevice device : bluetoothDevices) {
                            btArray[index] = device;
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

        buttonListen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ServerClass serverClass = new ServerClass();
                serverClass.start();
            }
        });

        pairedListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ClientClass clientClass = new ClientClass(btArray[i]);
                clientClass.start();

                tvStatus.setText("Connecting");
            }
        });

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String string = String.valueOf(etWriteMsg.getText());
                sendReceive.write(string.getBytes());
            }
        });
    }

    private class ServerClass extends Thread{
        private BluetoothServerSocket serverSocket;

        public ServerClass(){
            try {
                serverSocket = myBluetoothAdapter.listenUsingRfcommWithServiceRecord(APP_NAME, MY_UUID);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run(){
            BluetoothSocket socket = null;

            while(socket==null){
                try{
                    Message message = Message.obtain();
                    message.what = STATE_CONNECTING;
                    handler.sendMessage(message);

                    socket = serverSocket.accept();
                } catch (IOException e){
                    e.printStackTrace();

                    Message message = Message.obtain();
                    message.what=STATE_CONNECTION_FAILED;
                    handler.sendMessage(message);
                }

                if (socket!=null){

                    Message message = Message.obtain();
                    message.what = STATE_CONNECTED;
                    handler.sendMessage(message);

                    sendReceive = new SendReceive(socket);
                    sendReceive.start();

                    break;
                }
            }
        }
    }

    private class ClientClass extends Thread{
        private BluetoothDevice device;
        private BluetoothSocket socket;

        public ClientClass(BluetoothDevice device){
            this.device = device;

            try {
                socket = this.device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run(){
            try{
                socket.connect();

                Message message = Message.obtain();
                message.what = STATE_CONNECTED;
                handler.sendMessage(message);

                sendReceive = new SendReceive(socket);
                sendReceive.start();
            } catch (IOException e) {
                e.printStackTrace();

                Message message = Message.obtain();
                message.what = STATE_CONNECTION_FAILED;
                handler.sendMessage(message);
            }
        }
    }

    private class SendReceive extends Thread{
        private final BluetoothSocket bluetoothSocket;
        private final InputStream inputStream;
        private final OutputStream outputStream;

        public SendReceive(BluetoothSocket socket){
            this.bluetoothSocket = socket;
            InputStream ins = null;
            OutputStream outs = null;

            try{
                ins = bluetoothSocket.getInputStream();
                outs = bluetoothSocket.getOutputStream();
            } catch (IOException e){
                e.printStackTrace();
            }

            inputStream = ins;
            outputStream = outs;
        }

        public void run(){
            byte[] buffer = new byte[1024];
            int bytes;

            while (true){
                try{
                    bytes = inputStream.read(buffer);

                    handler.obtainMessage(STATE_MESSAGE_RECEIVED, bytes, -1, buffer).sendToTarget();
                } catch (IOException e){
                    e.printStackTrace();
                    break;
                }
            }
        }

        public void write(byte[] bytes){
            try {
                outputStream.write(bytes);
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }
}
