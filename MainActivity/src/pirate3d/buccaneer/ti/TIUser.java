package pirate3d.buccaneer.ti;

import org.json.JSONException;
import org.json.JSONObject;

public class TIUser {
	int id;
	String userName;
	String avatar;

	public TIUser(JSONObject user) {
		if (user != null) {
			try {
				this.id = user.getInt("id");
				this.userName = user.getString("username");
				this.avatar = user.getString("avatar");
			} catch (JSONException e) {
				return;
			}
		}
	}
}
