package com.example.android.effectivenavigation;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pirate3d.buccaneer.ti.TIProduct;
import pirate3d.buccaneer.ui.ItemAdapter;
import pirate3d.buccaneer.ui.RowItem;
import android.os.AsyncTask;
import android.view.View;

class TIConnection extends AsyncTask<Void, Void, Integer> {

	InputStream is = null;
	String result = "";
	JSONObject jsonObject = null;
	JSONArray arr = null;

	static ArrayList<TIProduct> productCollection;
	boolean ProductsAvailable = false;

	
	List<RowItem> rowItems;
	
	protected void onPostExecute(Integer result) {
		this.ProductsAvailable = true;

		String[] products = GetProductNames();
		if (products == null)
			return;
		
		rowItems = new ArrayList<RowItem>();
		for (int i = 0; i < products.length; i++) {
			TIProduct p = productCollection.get(i);
            RowItem item = new RowItem(p.imageSquare, products[i], p.description);
            rowItems.add(item);
        }
 
        /*CustomListViewAdapter adapter = new CustomListViewAdapter(MainActivity.appContext,
                R.layout.list_item, rowItems);*/
		
		ItemAdapter adapter = new ItemAdapter(MainActivity.appContext,
                R.layout.list_item, rowItems);
        if(MainActivity.TI_listview!=null)
        	MainActivity.TI_listview.setAdapter(adapter);
        MainActivity.TIspinner.setVisibility(View.GONE);
	}

	@Override
	protected Integer doInBackground(Void... params) {
		// HTTP
		try {
			HttpClient httpclient = new DefaultHttpClient(); // for port 80
																// requests!
			HttpGet httppost = new HttpGet(
					"http://www.treasure.is/v1/products/");
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			is = entity.getContent();
		} catch (Exception e) {
			return 0;
		}

		// Read response to string
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					is, "utf-8"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			is.close();
			result = sb.toString();
		} catch (Exception e) {
			return 0;
		}

		// Convert string to object
		try {
			TIConnection.productCollection = new ArrayList<>();

			jsonObject = new JSONObject(result);
			arr = jsonObject.getJSONArray("results");

			for (int i = 0; i < arr.length(); i++) {
				JSONObject obj = arr.getJSONObject(i);
				TIProduct product = new TIProduct(obj);
				TIConnection.productCollection.add(product);
			}
		} catch (JSONException e) {
			return 0;
		}
		return 1;

	}

	public String[] GetProductNames() {
		String[] products = new String[productCollection.size()];

		for (int i = 0; i < productCollection.size(); i++) {
			TIProduct p = productCollection.get(i);
			products[i] = p.productName;
		}
		return products;
	}
}
