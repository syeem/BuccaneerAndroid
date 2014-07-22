package pirate3d.buccaneer.ti;

import org.json.JSONException;
import org.json.JSONObject;

public class TIPhotos {
	
	int id;
	String s3_key;
	String small;
	String square;
	String regular;
	
	public TIPhotos(JSONObject obj)
	{
		try{
		this.id = obj.getInt("id");
		this.s3_key = obj.getString("s3_key");
		this.small = obj.getString("small");
		this.regular = obj.getString("regular");
		this.square = obj.getString("square");
		}catch(JSONException e)
		{
			e.printStackTrace();
		}
	}
}
