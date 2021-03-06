/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.bluetoothlegatt;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


/**
 * Activity for scanning and displaying available Bluetooth LE devices.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class DeviceScanActivity extends ListActivity {
    private final static String TAG = DeviceScanActivity.class.getSimpleName();

    private LeDeviceListAdapter mLeDeviceListAdapter;
    private BluetoothAdapter mBluetoothAdapter;
    private boolean mScanning;
    private Handler mHandler;

    private BluetoothLeScanner mBluetoothLeScanner;

    private static final int REQUEST_ENABLE_BT = 1;
    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 10000;


    private ScanCallback mScanCallback;

    double distance;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setTitle(R.string.title_devices);
        mHandler = new Handler();



        // Use this check to determine whether BLE is supported on the device.  Then you can
        // selectively disable BLE-related features.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        1);
            }
        }

// Device scan callback.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                mScanCallback = new ScanCallback() {
                    @Override
                    public void onScanResult(int callbackType, ScanResult result) {
                        super.onScanResult(callbackType, result);

                        int txPower = result.getScanRecord().getTxPowerLevel();
                        String str = ""+txPower;
                        str = ""+str.charAt(0)+str.charAt(1)+str.charAt(2);
                        txPower = Integer.parseInt(str);
                        Log.d(TAG, "Rssi : "+result.getRssi()+"txPower1 : "+str);
                        double rssi = result.getRssi();
                        Log.d(TAG, "Rssi : "+result.getRssi()+"txPower : "+txPower);
                        double num = calculateAccuracy(-65, -59);
                        Log.d(TAG, "num:"+num);
                        distance =  calculateAccuracy(-65, -59);
                        Log.d(TAG, "distance : "+calculateAccuracy(-65, -59));
                        Log.d(TAG, "distance : "+distance);
                        Log.d(TAG, result.getScanRecord().toString());

                        mLeDeviceListAdapter.addDevice(result.getDevice());
                        mLeDeviceListAdapter.notifyDataSetChanged();
                    }
                };
            }
        }

        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        if (mBluetoothAdapter.getState() == BluetoothAdapter.STATE_ON) {
//            mSwipeRefreshLayout.setRefreshing(true);
//            mLeDeviceListAdapter.clear();
            mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
            if(ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION ) == PackageManager.PERMISSION_GRANTED) {
                mBluetoothLeScanner.startScan(mScanCallback);
            }
        }


//    // Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
//        // BluetoothAdapter through BluetoothManager.
//        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
//        mBluetoothAdapter = bluetoothManager.getAdapter();
//
//        // Checks if Bluetooth is supported on the device.
//        if (mBluetoothAdapter == null) {
//            Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
//            finish();
//            return;
//        }
//
//        // Ensures Bluetooth is enabled on the device.  If Bluetooth is not currently enabled,
//        // fire an intent to display a dialog asking the user to grant permission to enable it.
//        if (!mBluetoothAdapter.isEnabled()) {
//            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
//        }
//
//        mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
//
//        //Check if Bluetooth LE Scanner is available.
//        if(mBluetoothLeScanner == null){
//            Toast.makeText(this, "Can not find BLE Scanner", Toast.LENGTH_SHORT).show();
//            if (!mBluetoothAdapter.isEnabled()) {
//                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
//            }
//            mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
////            return;
//        }
    }

    // reference : http://developer.radiusnetworks.com/2014/12/04/fundamentals-of-beacon-ranging.html
    protected static double calculateAccuracy(int txPower, double rssi) {
        if (rssi == 0) {
            return -1.0; // if we cannot determine distance, return -1.
        }

        double ratio = rssi*1.0/txPower;
        if (ratio < 1.0) {
            return Math.pow(ratio,10);
        }
        else {
            double accuracy =  (0.89976)*Math.pow(ratio,7.7095) + 0.111;
            return accuracy;
        }

//        if(rssi == 0.0D) {
//            return -1.0D;
//        } else {
//            double ratio = rssi * 1.0D / (double)txPower;
//            if(ratio < 1.0D) {
//                return Math.pow(ratio, 10.0D);
//            } else {
//                double accuracy = 0.89976D * Math.pow(ratio, 7.7095D) + 0.111D;
//                return accuracy;
//            }
//        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        if (!mScanning) {
            menu.findItem(R.id.menu_stop).setVisible(false);
            menu.findItem(R.id.menu_scan).setVisible(true);
            menu.findItem(R.id.menu_refresh).setActionView(null);
        } else {
            menu.findItem(R.id.menu_stop).setVisible(true);
            menu.findItem(R.id.menu_scan).setVisible(false);
            menu.findItem(R.id.menu_refresh).setActionView(
                    R.layout.actionbar_indeterminate_progress);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_scan:
                mLeDeviceListAdapter.clear();
                scanLeDevice(true);
                break;
            case R.id.menu_stop:
                scanLeDevice(false);
                break;
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();


        // Initializes list view adapter.
        mLeDeviceListAdapter = new LeDeviceListAdapter();
        setListAdapter(mLeDeviceListAdapter);
        scanLeDevice(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // User chose not to enable Bluetooth.
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            finish();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPause() {
        super.onPause();
        scanLeDevice(false);
        mLeDeviceListAdapter.clear();
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        final BluetoothDevice device = mLeDeviceListAdapter.getDevice(position);
        if (device == null) return;
        final Intent intent = new Intent(this, DeviceControlActivity.class);
        intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_NAME, device.getName());
        intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_ADDRESS, device.getAddress());
        if (mScanning) {
//            mBluetoothAdapter.stopLeScan(mLeScanCallback);
            mBluetoothLeScanner.stopScan(mScanCallback);
            mScanning = false;
        }
        startActivity(intent);
    }

    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    mBluetoothLeScanner.stopScan(mScanCallback);
                    invalidateOptionsMenu();
                }
            }, SCAN_PERIOD);

            mScanning = true;
            mBluetoothLeScanner.startScan(mScanCallback);

        } else {
            mScanning = false;
            mBluetoothLeScanner.stopScan(mScanCallback);

        }
        invalidateOptionsMenu();
    }

    // Adapter for holding devices found through scanning.
    private class LeDeviceListAdapter extends BaseAdapter {
        private ArrayList<BluetoothDevice> mLeDevices;
        private LayoutInflater mInflator;

        public LeDeviceListAdapter() {
            super();
            mLeDevices = new ArrayList<BluetoothDevice>();
            mInflator = DeviceScanActivity.this.getLayoutInflater();
        }

        public void addDevice(BluetoothDevice device) {
            if(!mLeDevices.contains(device)) {
                mLeDevices.add(device);
            }
        }

        public BluetoothDevice getDevice(int position) {
            return mLeDevices.get(position);
        }

        public void clear() {
            mLeDevices.clear();
        }

        @Override
        public int getCount() {
            return mLeDevices.size();
        }

        @Override
        public Object getItem(int i) {
            return mLeDevices.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            // General ListView optimization code.
            if (view == null) {
                view = mInflator.inflate(R.layout.listitem_device, null);
                viewHolder = new ViewHolder();
                viewHolder.deviceAddress = (TextView) view.findViewById(R.id.device_address);
                viewHolder.deviceName = (TextView) view.findViewById(R.id.device_name);
                viewHolder.deviceDistance = (TextView) view.findViewById(R.id.device_ditance);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            BluetoothDevice device = mLeDevices.get(i);
            final String deviceName = device.getName();
            if (deviceName != null && deviceName.length() > 0) {
                viewHolder.deviceName.setText(deviceName);
                viewHolder.deviceDistance.setText(String.format("distance : (%.2fm)", distance));
            }
            else {
                viewHolder.deviceName.setText(R.string.unknown_device);
                viewHolder.deviceDistance.setText(R.string.unknown_device);
            }
            viewHolder.deviceAddress.setText(device.getAddress());

            return view;
        }
    }

//    private ScanCallback mScanCallback = new ScanCallback() {
//        @Override
//        public void onScanResult(int callbackType, ScanResult result) {
//           processResult(result);
//        }
//
//        @Override
//        public void onBatchScanResults(List<ScanResult> results) {
//            for(ScanResult result : results){
//                processResult(result);
//            }
//        }
//
//        @Override
//        public void onScanFailed(int errorCode) {
//        }
//
//        private void processResult(final ScanResult result){
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    int txPower = result.getScanRecord().getTxPowerLevel();
//                    Log.d(TAG, "txPower : "+txPower);
//                    mLeDeviceListAdapter.addDevice(result.getDevice());
//                    mLeDeviceListAdapter.notifyDataSetChanged();
//                }
//            });
//        }
//
//
//    };


//
//    // Device scan callback.
//    private BluetoothAdapter.LeScanCallback mLeScanCallback =
//        new BluetoothAdapter.LeScanCallback() {
//
//        @Override
//        public void onLeScan(final BluetoothDevice device, int rssi, final byte[] scanRecord) {
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//
//
//                    int startByte = 2;
//                    boolean patternFound = false;
//                    while (startByte <= 5) {
//                        if (    ((int) scanRecord[startByte + 2] & 0xff) == 0x02 && //Identifies an iBeacon
//                                ((int) scanRecord[startByte + 3] & 0xff) == 0x15) { //Identifies correct data length
//                            patternFound = true;
//                            break;
//                        }
//                        startByte++;
//                    }
//
//                    if (patternFound) {
//                        //Convert to hex String
//                        byte[] uuidBytes = new byte[16];
//                        System.arraycopy(scanRecord, startByte+4, uuidBytes, 0, 16);
//                        String hexString = bytesToHex(uuidBytes);
//
//                        //Here is your UUID
//                        String uuid =  hexString.substring(0,8) + "-" +
//                                hexString.substring(8,12) + "-" +
//                                hexString.substring(12,16) + "-" +
//                                hexString.substring(16,20) + "-" +
//                                hexString.substring(20,32);
//
//                        //Here is your Major value
//                        int major = (scanRecord[startByte+20] & 0xff) * 0x100 + (scanRecord[startByte+21] & 0xff);
//
//                        //Here is your Minor value
//                        int minor = (scanRecord[startByte+22] & 0xff) * 0x100 + (scanRecord[startByte+23] & 0xff);
//
//
//
//                        Log.d(TAG, "uuid : "+uuid);
//                        Log.d(TAG, "major : "+major);
//                        Log.d(TAG, "minor : "+minor);
//                        Log.d(TAG, "txPower : "+getTxPowerLevel(scanRecord));
//
//
//                    }
//
//
//
//                    mLeDeviceListAdapter.addDevice(device);
//                    mLeDeviceListAdapter.notifyDataSetChanged();
//                }
//            });
//        }
//    };

//    public static Integer getTxPowerLevel(byte[] scanRecord) {
//
//        final byte TXPOWER = 0x0A;
//
//        // Check for BLE 4.0 TX power
//        int pos = findCodeInBuffer(scanRecord, TXPOWER);
//        if (pos > 0) {
//            return Integer.valueOf(scanRecord[pos]);
//        }
//        return null;
//    }
//    private static int findCodeInBuffer(byte[] buffer, byte code) {
//        final int length = buffer.length;
//        int i = 0;
//        while (i < length - 2) {
//            int len = buffer[i];
//            if (len < 0) {
//                return -1;
//            }
//
//            if (i + len >= length) {
//                return -1;
//            }
//
//            byte tcode = buffer[i + 1];
//            if (tcode == code) {
//                return i + 2;
//            }
//
//            i += len + 1;
//        }
//
//        return -1;
//    }
//
//
//    static final char[] hexArray = "0123456789ABCDEF".toCharArray();
//    private static String bytesToHex(byte[] bytes) {
//        char[] hexChars = new char[bytes.length * 2];
//        for ( int j = 0; j < bytes.length; j++ ) {
//            int v = bytes[j] & 0xFF;
//            hexChars[j * 2] = hexArray[v >>> 4];
//            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
//        }
//        return new String(hexChars);
//    }

    static class ViewHolder {
        TextView deviceName;
        TextView deviceAddress;
        TextView deviceDistance;
    }

}
