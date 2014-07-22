package pirate3d.buccaneer.ti;

import org.json.JSONException;
import org.json.JSONObject;

public class TIProduct {

	int productId;
	public String productName;
	public String description;
	String imageSmall;
	String imageRegular;
	public String imageSquare;
	
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
		JSONObject jsonUser = product.getJSONObject("user");
		this.user = new TIUser(jsonUser);
		}catch(JSONException e)
		{
			return;
		}
	}
	
}

