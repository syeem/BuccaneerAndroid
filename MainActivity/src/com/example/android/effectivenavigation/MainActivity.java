/*
 * Copyright 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.effectivenavigation;

import java.util.List;

import android.animation.ObjectAnimator;
import android.app.ActionBar;
import android.app.FragmentTransaction;
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
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends FragmentActivity implements
		ActionBar.TabListener {

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the three primary sections of the app. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	AppSectionsPagerAdapter mAppSectionsPagerAdapter;

	static WifiManager mainWifiObj;
	WifiScanReceiver wifiReciever;
	ListView list;
	static String wifis[];
	static Context appContext;
	static ProgressBar spinner;

	static String ssid;
	static int tab;

	/**
	 * The {@link ViewPager} that will display the three primary sections of the
	 * app, one at a time.
	 */
	ViewPager mViewPager;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		MainActivity.appContext = getApplicationContext();
		// Create the adapter that will return a fragment for each of the three
		// primary sections
		// of the app.
		mAppSectionsPagerAdapter = new AppSectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the action bar.
		final ActionBar actionBar = getActionBar();

		// Specify that the Home/Up button should not be enabled, since there is
		// no hierarchical parent.
		actionBar.setHomeButtonEnabled(false);

		// Specify that we will be displaying tabs in the action bar.
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		// Specify the color of the action/tab bar
		actionBar.setBackgroundDrawable(new ColorDrawable(Color
				.parseColor("#ffffff")));
		actionBar.setStackedBackgroundDrawable(new ColorDrawable(Color
				.parseColor("#ffffff")));
		// Set up the ViewPager, attaching the adapter and setting up a listener
		// for when the
		// user swipes between sections.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mAppSectionsPagerAdapter);
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						// When swiping between different app sections, select
						// the corresponding tab.
						// We can also use ActionBar.Tab#select() to do this if
						// we have a reference to the
						// Tab.
						actionBar.setSelectedNavigationItem(position);
					}
				});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mAppSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter.
			// Also specify this Activity object, which implements the
			// TabListener interface, as the
			// listener for when this tab is selected.
			actionBar.addTab(actionBar.newTab()
					.setText(mAppSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}

		mainWifiObj = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		wifiReciever = new WifiScanReceiver();
		Intent n = registerReceiver(wifiReciever, new IntentFilter(
				WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
		mainWifiObj.startScan();

	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		MainActivity.tab = tab.getPosition();
		if (tab.getPosition() == 1) {
			spinner = (ProgressBar) findViewById(R.id.progressBar1);
			mainWifiObj = (WifiManager) getSystemService(Context.WIFI_SERVICE);
			wifiReciever = new WifiScanReceiver();
			Intent n = registerReceiver(wifiReciever, new IntentFilter(
					WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
			// spinner.setVisibility(View.VISIBLE);
			mainWifiObj.startScan();
		}

		if (tab.getPosition() == 0) {
			// bounce the sliding drawer button
			Button b = (Button) findViewById(R.id.handle);
			if (b != null) {
				ObjectAnimator animY = ObjectAnimator.ofFloat(b,
						"translationY", -100f, 0f);

				animY.setDuration(1000);// 1sec
				animY.setInterpolator(new BounceInterpolator());
				animY.setRepeatCount(0);
				animY.start();
			}
		}
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 1) {
			if (resultCode == RESULT_OK) {
				String result = data.getStringExtra("result");
				if(result != null && MainActivity.ssid != null)
					ConnectToNetwork(MainActivity.ssid, result);
			}
			if (resultCode == RESULT_CANCELED) {
				// Write your code if there's no result
			}
		}
	}// onActivityResult

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
			Toast.makeText(appContext, "Connection Succeeded",
					Toast.LENGTH_LONG).show();
		} else {
			Toast.makeText(appContext, "Connection Failed", Toast.LENGTH_LONG)
					.show();
		}

	}

	public static void Refresh() {
		spinner.setVisibility(View.VISIBLE);
		mainWifiObj.startScan();
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the primary sections of the app.
	 */
	public static class AppSectionsPagerAdapter extends FragmentPagerAdapter {

		public AppSectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int i) {
			switch (i) {
			case 0:
				// The first section of the app is the most interesting -- it
				// offers
				// a launchpad into the other demonstrations in this example
				// application.
				return new LaunchpadSectionFragment();

			case 1:
				return new PrintersTab();

			case 2:
				return new SettingsTab();

			default:
				// The other sections of the app are dummy placeholders.
				Fragment fragment = new DummySectionFragment();
				Bundle args = new Bundle();
				args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, i + 1);
				fragment.setArguments(args);
				return fragment;
			}
		}

		@Override
		public int getCount() {
			return 4;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			switch (position) {
			case 0:
				return "Treasure Island";
			case 1:
				return "Printers";
			case 2:
				return "Settings";
			default:
				return "Section " + (position + 1);
			}
		}
	}

	/**
	 * A fragment that launches other parts of the demo application.
	 */
	public static class LaunchpadSectionFragment extends Fragment {

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(
					R.layout.fragment_section_launchpad, container, false);

			// Demonstration of a collection-browsing activity.
			rootView.findViewById(R.id.demo_collection_button)
					.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View view) {
							Intent intent = new Intent(getActivity(),
									CollectionDemoActivity.class);
							startActivity(intent);
						}
					});

			// Demonstration of navigating to external activities.
			rootView.findViewById(R.id.demo_external_activity)
					.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View view) {
							// Create an intent that asks the user to pick a
							// photo, but using
							// FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET, ensures that
							// relaunching
							// the application from the device home screen does
							// not return
							// to the external activity.
							Intent externalActivityIntent = new Intent(
									Intent.ACTION_PICK);
							externalActivityIntent.setType("image/*");
							externalActivityIntent
									.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
							startActivity(externalActivityIntent);
						}
					});

			// Demonstration of a collection-browsing activity.
			rootView.findViewById(R.id.print_activity).setOnClickListener(
					new View.OnClickListener() {
						@Override
						public void onClick(View view) {
							Intent intent = new Intent(getActivity(),
									PrintActivity.class);
							intent.putExtra("wifis", wifis);
							startActivity(intent);
						}
					});

			rootView.findViewById(R.id.camera_activity_button)
					.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View view) {
							Intent intent = new Intent(getActivity(),
									CameraTestActivity.class);
							startActivity(intent);
						}
					});

			return rootView;
		}
	}

	public static class PrintersTab extends Fragment {

		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.printers, container,
					false);

			// Demonstration of a collection-browsing activity.
			rootView.findViewById(R.id.search).setOnClickListener(
					new View.OnClickListener() {
						@Override
						public void onClick(View view) {
							// do something when the search button is pressed
							MainActivity.Refresh();
							return;
						}
					});

			rootView.findViewById(R.id.connect_button).setOnClickListener(
					new View.OnClickListener() {
						@Override
						public void onClick(View view) {
							Intent intent = new Intent(getActivity(),
									CameraTestActivity.class);
							getActivity().startActivityForResult(intent, 1);
							return;
						}
					});

			final ListView lview = (ListView) rootView
					.findViewById(R.id.listView1);
			lview.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					MainActivity.ssid = (String) lview.getItemAtPosition(position);
				}
			});
			return rootView;
		}
	}

	public static class SettingsTab extends Fragment {
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.settings, container,
					false);
			return rootView;
		}
	}

	class WifiScanReceiver extends BroadcastReceiver {

		public void onReceive(Context c, Intent intent) {
			if(MainActivity.tab!=1)
				return;
			List<ScanResult> wifiScanList = mainWifiObj.getScanResults();
			wifis = new String[wifiScanList.size()];
			if (wifiScanList != null) {
				for (int i = 0; i < wifiScanList.size(); i++) {
					wifis[i] = ((wifiScanList.get(i)).SSID);
				}
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(
						getApplicationContext(),
						android.R.layout.simple_list_item_1, wifis);
				ListView lview = (ListView) findViewById(R.id.listView1);
				if (spinner != null)
					spinner.setVisibility(View.GONE);
				lview.setAdapter(adapter);
			}
		}
	}

	/**
	 * A dummy fragment representing a section of the app, but that simply
	 * displays dummy text.
	 */
	public static class DummySectionFragment extends Fragment {

		public static final String ARG_SECTION_NUMBER = "section_number";

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_section_dummy,
					container, false);
			Bundle args = getArguments();
			((TextView) rootView.findViewById(android.R.id.text1))
					.setText(getString(R.string.dummy_section_text,
							args.getInt(ARG_SECTION_NUMBER)));
			return rootView;
		}
	}
}
