package pirate3d.buccaneer.ti;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

public class TIProduct {

	int productId;
	public String productName;
	public String description;
	String imageSmall;
	String imageRegular;
	public String hash;
	public String imageSquare;
	public ArrayList<TIPrintObject> printObjects;
	public ArrayList<TIPhotos> photos;
	TIUser user;
	
	public TIProduct(JSONObject product)
	{
		try{
		this.productId = product.getInt("id");
		this.productName = product.getString("name");
		this.description = product.getString("description");
		this.imageSmall  = product.getString("image_small");
		this.imageRegular  = product.getString("image_regular");
		this.imageSquare  = product.getString("image_square");
		this.hash = product.getString("hash");
		JSONObject jsonUser = product.getJSONObject("user");
		this.user = new TIUser(jsonUser);
		}catch(JSONException e)
		{
			e.printStackTrace();
			return;
		}
	}
	
}

