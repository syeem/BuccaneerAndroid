package pirate3d.buccaneer.ti;

import java.net.URL;

import com.example.android.effectivenavigation.PrintPreviewActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

public class TIImageDownloader extends AsyncTask<Void,Void,Integer>{

	String imageUrl;
	ImageView imageView;
	Bitmap bmp;
	
	public TIImageDownloader(String imageUrl, ImageView imageView)
	{
		this.imageUrl = imageUrl;
		this.imageView = imageView;
	}
	
	protected void onPostExecute(Integer result) {
		this.imageView.setImageBitmap(bmp);
	}

	@Override
	protected Integer doInBackground(Void... params) {
		if(imageUrl !=null)
		{
			try{
			URL url = new URL(this.imageUrl);
			bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
			//move back to UI thread using callback functions
			
			return 1;
			}catch(Exception e)
			{
				e.printStackTrace();
				return 0;
			}
		}
		return 0;
	}
}
