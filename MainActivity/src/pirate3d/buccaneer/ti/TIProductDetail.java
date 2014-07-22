package pirate3d.buccaneer.ti;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.android.effectivenavigation.PrintActivity;

import android.os.AsyncTask;

public class TIProductDetail extends AsyncTask <Void, Void, Integer>{
	
	TIProduct product;

	InputStream is = null;
	String result = "";
	
	public TIProductDetail(TIProduct p)
	{
		this.product = p;
	}
	
	
	protected void onPostExecute(Integer result){
		if(PrintActivity.stlFileNames==null)
			return;
		for(int i=0;i<this.product.printObjects.size();i++)
		{
			PrintActivity.stlFileNames.add(this.product.printObjects.get(i).fileName);
		}
	}
	
	@Override
	protected Integer doInBackground(Void... params) {
		
		// HTTP
		try {
			HttpClient httpclient = new DefaultHttpClient(); // for port 80
																// requests!
			HttpGet httppost = new HttpGet(
					"http://www.treasure.is/v1/product/" + this.product.hash);
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

			JSONObject jsonObject = new JSONObject(result);
			JSONArray arr = new JSONArray();

			arr = jsonObject.getJSONArray("printobs");
			this.product.printObjects = new ArrayList<TIPrintObject>();
			for (int i = 0; i < arr.length(); i++) {
				JSONObject obj = arr.getJSONObject(i);
				TIPrintObject o = new TIPrintObject(obj);
				this.product.printObjects.add(o);
			}

			arr = jsonObject.getJSONArray("photos");
			this.product.photos = new ArrayList<TIPhotos>();
			for (int i = 0; i < arr.length(); i++) {
				JSONObject obj = arr.getJSONObject(i);
				TIPhotos p = new TIPhotos(obj);
				this.product.photos.add(p);
			}
		} catch (JSONException e) {
			e.printStackTrace();
			return 0;
		}
		return 1;
	}
}
