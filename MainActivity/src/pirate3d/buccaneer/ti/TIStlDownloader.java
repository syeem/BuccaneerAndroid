package pirate3d.buccaneer.ti;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.android.effectivenavigation.PrintPreviewActivity;

import android.os.AsyncTask;
import android.os.Environment;

public class TIStlDownloader extends AsyncTask<Void, Void, Integer> {

	InputStream is = null;
	String result = "";
	int id;

	public TIStlDownloader(int id) {
		this.id = id;
	}

	protected void onPostExecute(Integer result) {
		PrintPreviewActivity.view.setText("finished downloading");
	}

	@Override
	protected Integer doInBackground(Void... params) {
		// HTTP
		try {
			HttpClient httpclient = new DefaultHttpClient(); // for port 80
																// requests!
			HttpGet httppost = new HttpGet("http://www.treasure.is/v1/d/"
					+ this.id);
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
			String stlUrl = jsonObject.getString("url");
			DownloadFile(stlUrl);
		} catch (JSONException e) {
			return 0;
		}
		return 1;
	}

	private void DownloadFile(String imageURL) {
		try {
			String fileName = imageURL.substring(imageURL.lastIndexOf('/') + 1,
					imageURL.length());
			
			URL url = new URL(imageURL);

			// Open a connection to that URL.
			URLConnection ucon = url.openConnection();

			// this timeout affects how long it takes for the app to realize
			// there's a connection problem
			ucon.setReadTimeout(5000);
			ucon.setConnectTimeout(30000);

			// Define InputStreams to read from the URLConnection.
			// uses 3KB download buffer
			InputStream is = ucon.getInputStream();
			BufferedInputStream inStream = new BufferedInputStream(is, 1024 * 5);
			File file = new File(Environment.getExternalStorageDirectory()
					.getPath() + "/Buccaneer/");
			file.mkdirs();

			File outputFile = new File(file, fileName);
			FileOutputStream outStream = new FileOutputStream(outputFile);
			byte[] buff = new byte[5 * 1024];

			// Read bytes (and store them) until there is nothing more to
			// read(-1)
			int len;
			while ((len = inStream.read(buff)) != -1) {
				outStream.write(buff, 0, len);
			}

			// clean up
			outStream.flush();
			outStream.close();
			inStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
