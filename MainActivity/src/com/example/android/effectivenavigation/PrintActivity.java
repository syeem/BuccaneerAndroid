package com.example.android.effectivenavigation;

import java.util.ArrayList;

import pirate3d.buccaneer.ti.TIProduct;
import pirate3d.buccaneer.ti.TIProductDetail;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SlidingPaneLayout.LayoutParams;
import android.text.method.ScrollingMovementMethod;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListPopupWindow;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnDismissListener;
import android.widget.TextView;
import android.widget.Toast;

public class PrintActivity extends FragmentActivity {

	static TIProduct product;
	public static ArrayList<String> stlFileNames;
	public static Context appContext;
	public static ViewPager pager;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.print_activity);
		PrintActivity.appContext = getApplicationContext();
		// Specify the color of the action/tab bar
		final ActionBar actionBar = getActionBar();
		actionBar.setBackgroundDrawable(new ColorDrawable(Color
				.parseColor("#ffffff")));
		actionBar.setStackedBackgroundDrawable(new ColorDrawable(Color
				.parseColor("#ffffff")));

		// Specify that the Home button should show an "Up" caret, indicating
		// that touching the
		// button will take the user one step up in the application's hierarchy.
		//actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.hide();
		
		PrintActivity.pager = (ViewPager) findViewById(R.id.pager);
		String position = (String) getIntent().getSerializableExtra(
				"selectedPosition");
		int p = Integer.parseInt(position);
		PrintActivity.product = TIConnection.productCollection.get(p);

		TextView view = (TextView) findViewById(R.id.descriptionText);
		view.setMovementMethod(new ScrollingMovementMethod());
		view.setText(PrintActivity.product.description);
		/*ImageView imgView = (ImageView) findViewById(R.id.imageView1);

		TIImageDownloader downloader = new TIImageDownloader(
				PrintActivity.product.imageRegular, imgView);
		downloader.execute();
*/
		stlFileNames = new ArrayList<>();
		TIProductDetail productDetail = new TIProductDetail(
				PrintActivity.product);
		productDetail.execute();

		final ListPopupWindow popupWindow = new ListPopupWindow(
				getApplicationContext());
		popupWindow.setAnchorView(view);
		popupWindow.setWidth(ListPopupWindow.WRAP_CONTENT);
		popupWindow.setHeight(ListPopupWindow.WRAP_CONTENT);
		popupWindow.setModal(true);

		popupWindow
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						// start new activity for printing the selected file
						if (PrintActivity.stlFileNames != null) {
							Intent intent = new Intent(getApplicationContext(),
							PrintPreviewActivity.class);
							int stlId = PrintActivity.product.printObjects.get(position).id;
							intent.putExtra("id", Integer.toString(stlId));
							startActivity(intent);
						}
					}
				});

		findViewById(R.id.Print).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				// display a list of stl to select from if there are multiple
				// stl files
				// send to printer for printing

				ArrayAdapter<String> adapter = new ArrayAdapter<String>(
						getApplicationContext(),
						android.R.layout.simple_list_item_1,
						PrintActivity.stlFileNames);
				popupWindow.setAdapter(adapter);
				popupWindow.show();
			}
		});

		findViewById(R.id.BtnColorPickerCancel).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						// do something
					}
				});
		
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

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// Check which request we're responding to

	}

}
