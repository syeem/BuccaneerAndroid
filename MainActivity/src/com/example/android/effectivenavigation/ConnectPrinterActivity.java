package com.example.android.effectivenavigation;

import java.util.List;

import android.app.ActionBar;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

public class ConnectPrinterActivity extends FragmentActivity {

	WifiManager mainWifiObj;
	WifiScanPrinterReceiver wifiPrinterReceiver;
	WifiScanReceiver wifiReceiver;
	ProgressBar spinner;
	ListView list;
	static String wifis[];
	static String ssid;
	static String initialNetwork;

	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.connect_printer_activity);

		// Specify the color of the action/tab bar
		final ActionBar actionBar = getActionBar();
		actionBar.setBackgroundDrawable(new ColorDrawable(Color
				.parseColor("#ffffff")));
		actionBar.setStackedBackgroundDrawable(new ColorDrawable(Color
				.parseColor("#ffffff")));

		// Specify that the Home button should show an "Up" caret, indicating
		// that touching the
		// button will take the user one step up in the application's hierarchy.
		actionBar.setDisplayHomeAsUpEnabled(true);

		spinner = (ProgressBar) findViewById(R.id.spinner_networksList);
		list = (ListView) findViewById(R.id.list_networksList);
		initialNetwork = (String) getIntent().getSerializableExtra(
				"initialNetwork");
		
		// set up the click handlers for buttons
		findViewById(R.id.button_searchNetwork).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						// do something when the search button is pressed
						Refresh();
						return;
					}
				});

		findViewById(R.id.button_connectNetwork).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						// do something when the search button is pressed
						if (wifiPrinterReceiver != null) {
							Intent intent = new Intent(getApplicationContext(),
									CameraTestActivity.class);
							startActivityForResult(intent, 1);
							return;
						}else{
							//[call API] connect the printer to the selected network
							//connect the phone to the network it was initially connected to
							List<WifiConfiguration> list = mainWifiObj.getConfiguredNetworks();
							boolean result = false;
							mainWifiObj.disconnect();
							for (WifiConfiguration i : list) {
								if (i.SSID != null && i.SSID.equals("\"" + ConnectPrinterActivity.initialNetwork + "\"")) {
									mainWifiObj.disconnect();
									mainWifiObj.enableNetwork(i.networkId, true);
									result = mainWifiObj.reconnect();
									break;
								}
							}
						}
						
					}
				});

		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				ConnectPrinterActivity.ssid = (String) list
						.getItemAtPosition(position);

			}
		});
		// show a list of available networks that the printer can connect to

		mainWifiObj = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		wifiPrinterReceiver = new WifiScanPrinterReceiver();
		Intent n = registerReceiver(wifiPrinterReceiver, new IntentFilter(
				WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
		mainWifiObj.startScan();
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 1) {
			if (resultCode == RESULT_OK) {
				String result = data.getStringExtra("result");
				if (result != null && ConnectPrinterActivity.ssid != null) {
					ConnectToNetwork(ConnectPrinterActivity.ssid, result);
					unregisterReceiver(wifiPrinterReceiver);
					wifiPrinterReceiver = null;
					wifiReceiver = new WifiScanReceiver();
					Intent n = registerReceiver(wifiReceiver, new IntentFilter(
							WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
					Refresh();
				}
			}
			if (resultCode == RESULT_CANCELED) {
				// Write your code if there's no result
				unregisterReceiver(wifiReceiver);
				wifiReceiver = null;

				wifiPrinterReceiver = new WifiScanPrinterReceiver();
				Intent n = registerReceiver(wifiPrinterReceiver,
						new IntentFilter(
								WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
				Refresh();
			}
		}
	}// onActivityResult

	public void Refresh() {
		this.spinner.setVisibility(View.VISIBLE);
		this.mainWifiObj.startScan();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This is called when the Home (Up) button is pressed in the action
			// bar.
			// Create a simple intent that starts the hierarchical parent
			// activity and
			// use NavUtils in the Support Package to ensure proper handling of
			// Up.
			Intent upIntent = new Intent(this, MainActivity.class);
			if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
				// This activity is not part of the application's task, so
				// create a new task
				// with a synthesized back stack.
				TaskStackBuilder.from(this)
				// If there are ancestor activities, they should be added here.
						.addNextIntent(upIntent).startActivities();
				finish();
			} else {
				// This activity is part of the application's task, so simply
				// navigate up to the hierarchical parent activity.
				NavUtils.navigateUpTo(this, upIntent);
			}
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void ConnectToNetwork(String ssid, String pass) {
		// =>check if the network has been previously
		// connected to
		// =>use remembered password to connect to network
		// =>or launch QR code scan activity to get the
		// password

		String networkSSID = ssid;
		String networkPass = pass;

		WifiConfiguration conf = new WifiConfiguration();
		conf.SSID = "\"" + networkSSID + "\""; // Please
												// note the
												// quotes.
												// String
												// should
												// contain
												// ssid in
												// quotes
		conf.preSharedKey = "\"" + networkPass + "\"";
		mainWifiObj.addNetwork(conf);

		List<WifiConfiguration> list = mainWifiObj.getConfiguredNetworks();
		boolean result = false;
		for (WifiConfiguration i : list) {
			if (i.SSID != null && i.SSID.equals("\"" + networkSSID + "\"")) {
				mainWifiObj.disconnect();
				mainWifiObj.enableNetwork(i.networkId, true);
				result = mainWifiObj.reconnect();
				break;
			}
		}
		// =>connect to the network
		// =>display a toast message if successful
		if (result == true) {
			Toast.makeText(
					getApplicationContext(),
					"Connection Succeeded. Connected to "
							+ ConnectPrinterActivity.ssid, Toast.LENGTH_LONG)
					.show();

		} else {
			Toast.makeText(getApplicationContext(), "Connection Failed",
					Toast.LENGTH_LONG).show();
		}

	}

	class WifiScanPrinterReceiver extends BroadcastReceiver {
		public void onReceive(Context c, Intent intent) {
			List<ScanResult> wifiScanList = mainWifiObj.getScanResults();
			String temp_wifis[] = new String[wifiScanList.size()];
			int j = 0;
			if (wifiScanList != null) {
				for (int i = 0; i < wifiScanList.size(); i++) {
					String networkId = (wifiScanList.get(i)).SSID;
					if (networkId.contains("Buccaneer")) {
						temp_wifis[j] = ((wifiScanList.get(i)).SSID);
						j++;
					}
				}
				wifis = new String[j];
				for (int i = 0; i < j; i++)
					wifis[i] = temp_wifis[i];

				if (j == 0)
					Toast.makeText(getBaseContext(),
							"No printers are available on the network",
							Toast.LENGTH_LONG).show();

				ArrayAdapter<String> adapter = new ArrayAdapter<String>(
						getApplicationContext(),
						android.R.layout.simple_list_item_1, wifis);
				ListView lview = (ListView) findViewById(R.id.list_networksList);
				if (spinner != null)
					spinner.setVisibility(View.GONE);
				lview.setAdapter(adapter);
			}
		}
	}

	class WifiScanReceiver extends BroadcastReceiver {
		public void onReceive(Context c, Intent intent) {
			List<ScanResult> wifiScanList = mainWifiObj.getScanResults();
			String temp_wifis[] = new String[wifiScanList.size()];
			int j = 0;
			if (wifiScanList != null) {
				for (int i = 0; i < wifiScanList.size(); i++) {
					// String networkId = (wifiScanList.get(i)).SSID;
					temp_wifis[j] = ((wifiScanList.get(i)).SSID);
					j++;
				}
				wifis = new String[j];
				for (int i = 0; i < j; i++)
					wifis[i] = temp_wifis[i];

				if (j == 0)
					Toast.makeText(getBaseContext(),
							"No networks are available", Toast.LENGTH_LONG)
							.show();

				ArrayAdapter<String> adapter = new ArrayAdapter<String>(
						getApplicationContext(),
						android.R.layout.simple_list_item_1, wifis);
				ListView lview = (ListView) findViewById(R.id.list_networksList);
				if (spinner != null)
					spinner.setVisibility(View.GONE);
				lview.setAdapter(adapter);
			}
		}
	}
}
