package com.example.android.effectivenavigation;

import pirate3d.buccaneer.ti.TIImageDownloader;
import pirate3d.buccaneer.ti.TIProduct;
import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class PrintActivity extends FragmentActivity {

	TIProduct product;
	

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.print_activity);

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
		String position = (String)getIntent().getSerializableExtra("selectedPosition");
		int p = Integer.parseInt(position);
		this.product = TIConnection.productCollection.get(p);
		TextView view = (TextView) findViewById(R.id.textview1);
		view.setText(this.product.description);
		ImageView imgView = (ImageView)findViewById(R.id.imageView1);
		TIImageDownloader downloader = new TIImageDownloader(this.product.imageSquare, imgView);
		downloader.execute();
		
		findViewById(R.id.BtnColorPickerOk).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						// do something

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
