package org.example.ndk;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Main activity.
 * 
 * @author Lorensius W. L. T <lorenz@londatiga.net>
 * 
 */
public class MainActivity extends Activity {

	private boolean isConnected;

	private TextView mStatusTv;
	private Button mActivateBtn;
	private Button mPairedBtn;
	private Button mScanBtn;
	private Button mControlBtn;

	private ProgressDialog mProgressDlg;

	private ArrayList<BluetoothDevice> mDeviceList = new ArrayList<BluetoothDevice>();

	private BluetoothAdapter mBluetoothAdapter;
	private BluetoothSocket socket_nxt1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		mStatusTv = (TextView) findViewById(R.id.tv_status);
		mActivateBtn = (Button) findViewById(R.id.btn_enable);
		mPairedBtn = (Button) findViewById(R.id.btn_view_paired);
		mScanBtn = (Button) findViewById(R.id.btn_scan);
		mControlBtn = (Button) findViewById(R.id.btn_control);

		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		isConnected = false;

		mProgressDlg = new ProgressDialog(this);

		mProgressDlg.setMessage("Scanning...");
		mProgressDlg.setCancelable(false);
		mProgressDlg.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();

						mBluetoothAdapter.cancelDiscovery();
					}
				});

		if (mBluetoothAdapter == null) {
			showUnsupported();
		} else {
			mPairedBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Set<BluetoothDevice> pairedDevices = mBluetoothAdapter
							.getBondedDevices();

					if (pairedDevices == null || pairedDevices.size() == 0) {
						showToast("No Paired Devices Found");
					} else {
						ArrayList<BluetoothDevice> list = new ArrayList<BluetoothDevice>();

						list.addAll(pairedDevices);

						Intent intent = new Intent(MainActivity.this,
								DeviceListActivity.class);

						intent.putParcelableArrayListExtra("device.list", list);

						startActivity(intent);
					}
				}
			});

			mScanBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					mBluetoothAdapter.startDiscovery();
				}
			});

			mActivateBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (mBluetoothAdapter.isEnabled()) {
						mBluetoothAdapter.disable();

						showDisabled();
					} else {
						Intent intent = new Intent(
								BluetoothAdapter.ACTION_REQUEST_ENABLE);

						startActivityForResult(intent, 1000);
					}
				}
			});
			/*
			 * Blutooth Paring
			 */
			mControlBtn.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					/*
					 * Intent intent = new Intent(MainActivity.this,
					 * ControlActivity.class); startActivity(intent);
					 */
					if (!connectToNXTs() && !isConnected)
						Toast.makeText(MainActivity.this, "FAIL",
								Toast.LENGTH_SHORT).show();
					try {
						Constant.out = new OutputStreamWriter(socket_nxt1
								.getOutputStream());
						Constant.in = new InputStreamReader(socket_nxt1
								.getInputStream());

					} catch (IOException e) {
						e.printStackTrace();
					}

					/*
					 * If board is paring with arduino, go control activity
					 */

					if (Constant.out == null) {
						Toast.makeText(MainActivity.this, "Must do Pairing",
								Toast.LENGTH_SHORT).show();
					} else {
						Intent intent = new Intent(MainActivity.this,
								ControlActivity.class);
						startActivity(intent);
					}
				}
			});
			/*
			 * Select Mode Activity ( Freeriding , Control )
			 */

			if (mBluetoothAdapter.isEnabled()) {
				showEnabled();
			} else {
				showDisabled();
			}
		}

		IntentFilter filter = new IntentFilter();

		filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
		filter.addAction(BluetoothDevice.ACTION_FOUND);
		filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
		filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);

		registerReceiver(mReceiver, filter);

	}

	@Override
	public void onPause() {
		if (mBluetoothAdapter != null) {
			if (mBluetoothAdapter.isDiscovering()) {
				mBluetoothAdapter.cancelDiscovery();
			}
		}

		super.onPause();
	}

	@Override
	public void onDestroy() {
		unregisterReceiver(mReceiver);

		super.onDestroy();
	}

	private void showEnabled() {
		mStatusTv.setText("Bluetooth is On");
		mStatusTv.setTextColor(Color.BLUE);

		mActivateBtn.setText("Disable");
		mActivateBtn.setEnabled(true);

		mPairedBtn.setEnabled(true);
		mScanBtn.setEnabled(true);
	}

	private void showDisabled() {
		mStatusTv.setText("Bluetooth is Off");
		mStatusTv.setTextColor(Color.RED);

		mActivateBtn.setText("Enable");
		mActivateBtn.setEnabled(true);

		mPairedBtn.setEnabled(false);
		mScanBtn.setEnabled(false);
	}

	private void showUnsupported() {
		mStatusTv.setText("Bluetooth is unsupported by this device");

		mActivateBtn.setText("Enable");
		mActivateBtn.setEnabled(false);

		mPairedBtn.setEnabled(false);
		mScanBtn.setEnabled(false);
	}

	private void showToast(String message) {
		Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT)
				.show();
	}

	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();

			if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
				final int state = intent.getIntExtra(
						BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);

				if (state == BluetoothAdapter.STATE_ON) {
					showToast("Enabled");

					showEnabled();
				}
			} else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
				mDeviceList = new ArrayList<BluetoothDevice>();

				mProgressDlg.show();
			} else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED
					.equals(action)) {
				mProgressDlg.dismiss();

				Intent newIntent = new Intent(MainActivity.this,
						DeviceListActivity.class);

				newIntent.putParcelableArrayListExtra("device.list",
						mDeviceList);

				startActivity(newIntent);
			} else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				BluetoothDevice device = (BluetoothDevice) intent
						.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

				mDeviceList.add(device);

				showToast("Found device " + device.getName());
			}
		}
	};

	private BluetoothDevice getPairedDevice() {

		Set<BluetoothDevice> pairedDevices = mBluetoothAdapter
				.getBondedDevices();

		// If there are paired devices

		if (pairedDevices != null && pairedDevices.size() > 0) {
			// Loop through paired devices

			for (BluetoothDevice device : pairedDevices) {
				/*
				 * String deviceName = device.getName(); String deviceAddress =
				 * device.getAddress();
				 */
				return device;
			}

			/*
			 * boolean isDuplicate = false; for (DeviceData deviceData :
			 * mInfoList) { if (deviceData.getAddress().equals(deviceAddress)) {
			 * isDuplicate = true; } } if (!isDuplicate) { DeviceData data = new
			 * DeviceData(); data.setName(deviceName);
			 * data.setAddress(deviceAddress);
			 * 
			 * mInfoList.add(data); mAdapter.notifyDataSetChanged(); }
			 */
		}
		return null;
	}

	// connect to both NXTs
	private boolean connectToNXTs() {

		// get the BluetoothDevice of the NXT
		// try to connect to the nxt
		boolean success;

		try {
			socket_nxt1 = getPairedDevice().createRfcommSocketToServiceRecord(
					UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
			socket_nxt1.connect();
			success = true;
			isConnected = true;
		} catch (IOException e) {
			Log.d("Bluetooth", "Err: Device not found or cannot connect");
			success = false;
		}
		return success;
	}
}