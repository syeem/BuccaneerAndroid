package pirate3d.buccaneer.ti;

import org.json.JSONException;
import org.json.JSONObject;

public class TIPrintObject {

	public int id;
	public String fileName;
	String url;
	
	public TIPrintObject(JSONObject obj)
	{
		try{
		this.id = obj.getInt("id");
		this.fileName = obj.getString("original_name");
		}catch(JSONException e)
		{
			e.printStackTrace();
		}
	}
}
