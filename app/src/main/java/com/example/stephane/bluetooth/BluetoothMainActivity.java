package com.example.stephane.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;

public class BluetoothMainActivity extends AppCompatActivity {

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice bluetoothDevice;
    private BluetoothSocket bluetoothSocket;

    private static final int SELECT_PAIRED_DEVICES = 1;
    private static final int REQUEST_ENABLE_BLUETOOTH = 2;

    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_main);

        checkDeviceSupportBluetooth();

        checkIfBluetoothIsEnable();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        try {

            bluetoothSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case REQUEST_ENABLE_BLUETOOTH:
                if (resultCode == RESULT_OK){

                    Intent openListPairedDevices = new Intent(BluetoothMainActivity.this, ListDevicePaired.class);
                    startActivityForResult(openListPairedDevices, SELECT_PAIRED_DEVICES);

                }else{

                    Toast.makeText(getApplicationContext(), "You cannot continue without enable bluetooth", Toast.LENGTH_SHORT).show();
                }
            break;

            case SELECT_PAIRED_DEVICES:

                if(requestCode == RESULT_OK){

                    String addressMac = data.getStringExtra("addressMac");

                    bluetoothDevice = bluetoothAdapter.getRemoteDevice(addressMac);

                    try{

                        bluetoothSocket = bluetoothDevice.createInsecureRfcommSocketToServiceRecord(MY_UUID);
                        bluetoothSocket.connect();

                    }catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            break;
        }

    }

    private void checkDeviceSupportBluetooth(){

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (bluetoothAdapter == null){
            Toast.makeText(getApplicationContext(), "Device doesn't support Bluetooth", Toast.LENGTH_LONG).show();
        }
    }

    private void checkIfBluetoothIsEnable(){

        if (!bluetoothAdapter.isEnabled()){

            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetooth, REQUEST_ENABLE_BLUETOOTH);
        }
    }


}
