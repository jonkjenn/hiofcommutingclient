package no.hiof.hiofommuting.database;

import java.net.HttpCookie;
import java.util.Random;

import no.hiof.hiofcommuting.hiofcommuting.MainActivity;
import no.hiof.hiofcommuting.objects.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;

public class HandleLogin {

	public static HttpCookie cookie;

	public static boolean checkUnAndPw(String email, String password,
			Context context) {
		/*
		 * Using -100 as error code for wrong/misspelled/missing email address.
		 * Using -200 as error code for wrong/misspelled/missing password.
		 */
		JsonParser jp = new JsonParser();
		jp.saveCookie = true;
		JSONArray emailAndPw;
		emailAndPw = jp.getJsonArray(MainActivity.SERVER_URL
				+ "/email.py?q=login&email=" + email + "&pass=" + password,
				HandleLogin.getCookie(context));
		if (emailAndPw == null) {
			return false;
		}
		cookie = jp.cookie;

		try {
			JSONObject emailAndPwObj = (JSONObject) emailAndPw.get(0);
			String uId = emailAndPwObj.getString("user_id");
			//System.out.println("userid: " + uId);
			if (uId.equals("-100") || uId.equals("-200")) {
				return false;
			} else {
				return true;
			}
		} catch (JSONException e) {
			e.printStackTrace();
			return false;
		}
	}

	public static User getCurrentEmailUserLoggedIn(String email, Context context) {
		User userLoggedIn = null;
		JsonParser jp = new JsonParser();
		JSONArray emailUser;
		emailUser = jp.getJsonArray(MainActivity.SERVER_URL
				+ "/usr.py?q=emailUser&email=" + email, getCookie(context));

		try {
			JSONObject obj = (JSONObject) emailUser.get(0);
			int userId, studyId, startingYear, gcmVersion;
			String firstname, surname, institution, campus, department, study, gcmId;
			double lat, lon, distance;
			boolean car;
			userId = Integer.parseInt(obj.getString("user_id"));
			studyId = Integer.parseInt(obj.getString("study_id"));
			startingYear = Integer.parseInt(obj.getString("starting_year"));
			firstname = obj.getString("firstname");
			surname = obj.getString("surname");
			institution = obj.getString("institution_name");
			campus = obj.getString("campus_name");
			department = obj.getString("department_name");
			study = obj.getString("name_of_study");
			gcmId = obj.getString("gcm_id");
			gcmVersion = Integer.parseInt(obj.getString("gcm_version"));
			String point = obj.getString("latlon").replace("POINT(", "")
					.replace(")", "");
			String[] latlon = point.split(" ");
			lat = Double.parseDouble(latlon[0]);
			lon = Double.parseDouble(latlon[1]);
			String photoUrl = firstname + lat + lon;
			//System.out.println("lat2 : " + lat);
			//System.out.println("lon2 : " + lon);
			distance = 0.0;
			String carString = obj.getString("car");
			if (carString.equals("1")) {
				car = true;
			} else {
				car = false;
			}
			String fbId = "";
			userLoggedIn = new User(userId, studyId, firstname, surname, lat,
					lon, distance, institution, campus, department, study,
					startingYear, car, photoUrl, fbId, gcmId, gcmVersion);
			return userLoggedIn;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return userLoggedIn;
	}

	public static User getCurrentFacebookUserLoggedIn(JSONObject obj) {
		User userLoggedIn;
		try {
			//System.out.println("lager user");
			int userId, studyId, startingYear, gcmVersion;
			String firstname, surname, institution, campus, department, study, gcmId;
			double lat, lon, distance;
			boolean car;
			userId = Integer.parseInt(obj.getString("user_id"));
			studyId = Integer.parseInt(obj.getString("study_id"));
			startingYear = Integer.parseInt(obj.getString("starting_year"));
			firstname = obj.getString("firstname");
			surname = obj.getString("surname");
			institution = obj.getString("institution_name");
			campus = obj.getString("campus_name");
			department = obj.getString("department_name");
			study = obj.getString("name_of_study");
			gcmId = obj.getString("gcm_id");
			gcmVersion = Integer.parseInt(obj.getString("gcm_version"));
			String point = obj.getString("latlon").replace("POINT(", "")
					.replace(")", "");
			String[] latlon = point.split(" ");
			lat = Double.parseDouble(latlon[0]);
			lon = Double.parseDouble(latlon[1]);
			String photoUrl = firstname + lat + lon;
			//System.out.println("lat2 : " + lat);
			//System.out.println("lon2 : " + lon);
			distance = 0.0;
			String carString = obj.getString("car");
			if (carString.equals("1")) {
				car = true;
			} else {
				car = false;
			}
			String fbId = "";
			userLoggedIn = new User(userId, studyId, firstname, surname, lat,
					lon, distance, institution, campus, department, study,
					startingYear, car, photoUrl, fbId, gcmId, gcmVersion);
			return userLoggedIn;
		} catch (Exception e) {
			return null;
		}
	}

	// SEND EPOST TIL BRUKEREN FOR TILBAKESTILLING AV PASSORD
	public static boolean resetPassword(String str) {
		// Random tall 0 eller 1 for � illustrere random feilmelding. Byttes ut
		// med fail/false for sending av epost
		Random r = new Random();
		int error;
		error = r.nextInt(1);

		if (error == 0) {
			return true;
		} else if (error == 1) {
			return false;
		}
		return false;
	}

	public static HttpCookie getCookie(Context context) {
		SharedPreferences prefs = context.getSharedPreferences("hccook",
				Context.MODE_PRIVATE);

		if (!prefs.contains("name")) {
			return null;
		}

		HttpCookie c = new HttpCookie(
				prefs.getString("name", ""), prefs.getString("value", ""));
		c.setDomain(prefs.getString("domain", ""));
		c.setPath(prefs.getString("path", ""));
		c.setVersion(prefs.getInt("version", 0));
		return c;
	}

	public static void deleteCookie(Context context) {
		SharedPreferences prefs = context.getSharedPreferences("hccook",
				Context.MODE_PRIVATE);
		SharedPreferences.Editor se = prefs.edit();
		se.clear();
		se.commit();
	}

	public static void saveCookie(HttpCookie c, Context context) {
		SharedPreferences prefs = context.getSharedPreferences("hccook",
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString("name", c.getName());
		editor.putString("value", c.getValue());
		editor.putString("domain", c.getDomain());
		editor.putString("expiry", c.getMaxAge() == 0 ? "" : Long.toString(c.getMaxAge()));
		editor.putString("path", c.getPath());
		editor.putInt("version", c.getVersion());
		editor.apply();
	}

}
