package com.example.stephane.bluetooth;

import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Set;


public class ListDevicePaired extends ListActivity {

    private BluetoothAdapter bluetoothAdapter;
    private Set<BluetoothDevice> pairedDevices;
    private ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        discoveringPairedDevices();
        discoveringDevices();

        this.setTitle("Select the device");
        setListAdapter(arrayAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        bluetoothAdapter.cancelDiscovery();
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        String selectedDevice = ((TextView) v ).getText().toString();

        String addressMac = selectedDevice.substring(selectedDevice.length() - 17);

        Intent returnMacAddress = new Intent();
        returnMacAddress.putExtra("addressMac", addressMac);
        setResult(RESULT_OK, returnMacAddress);

        finish();
    }

    private void discoveringPairedDevices(){

        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        pairedDevices = bluetoothAdapter.getBondedDevices();

        if(pairedDevices.size() > 0){

            for (BluetoothDevice device : pairedDevices){

                arrayAdapter.add(device.getName() + "\n" + device.getAddress());
            }
        }
    }

    private void discoveringDevices(){

        bluetoothAdapter.startDiscovery();

        IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(broadcastReceiver, intentFilter);
    }

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(action)){

                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                arrayAdapter.add(device.getName() + "\n" + device.getAddress());
            }
        }
    };
}
