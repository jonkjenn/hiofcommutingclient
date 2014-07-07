package bachelor.database;

import java.util.Random;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import bachelor.objects.User;

public class HandleLogin {

	public static boolean checkUnAndPw(String email, String password) {
		/*
		Using -100 as error code for wrong/misspelled/missing email address.
  		Using -200 as error code for wrong/misspelled/missing password.
		 */
		JsonParser jp = new JsonParser();
		JSONArray emailAndPw;
		emailAndPw = jp.getJsonArray("http://frigg.hiof.no/bo14-g23/py/email.py?q=login&email=" + email + "&pass=" + password);
		
		try {
			JSONObject emailAndPwObj = (JSONObject) emailAndPw.get(0);
			String uId = emailAndPwObj.getString("user_id");
			System.out.println("userid: " + uId);
			if(uId.equals("-100") || uId.equals("-200")){
				return false;
			}
			else {
				return true;
			}
		} catch(JSONException e){
			e.printStackTrace();
			return false;
		}
	}

	public static User getCurrentEmailUserLoggedIn(String email){
		User userLoggedIn = null;
		JsonParser jp = new JsonParser();
		JSONArray emailUser;
		emailUser = jp.getJsonArray("http://frigg.hiof.no/bo14-g23/py/usr.py?q=emailUser&email=" + email);
		
		try {
			JSONObject obj = (JSONObject) emailUser.get(0);
			int userId, studyId, startingYear;
			String firstname, surname, institution, campus, department, study;
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
			String point = obj.getString("latlon").replace("POINT(", "").replace(")", "");
			String[] latlon = point.split(" ");
			lat = Double.parseDouble(latlon[0]);
			lon = Double.parseDouble(latlon[1]);
			String photoUrl = firstname + lat + lon;
			System.out.println("lat2 : " + lat);
			System.out.println("lon2 : " + lon);
			distance = 0.0;
			String carString = obj.getString("car");
			if(carString.equals("1")) {
				car = true;
			}
			else {
				car = false;
			}
			String fbId = "";
			userLoggedIn = new User(userId, studyId, firstname, surname, lat, lon, distance, institution, campus, department, study, startingYear, car, photoUrl, fbId);
			return userLoggedIn;
		} catch(JSONException e){
			e.printStackTrace();
		}
		return userLoggedIn;
	}
	
	public static User getCurrentFacebookUserLoggedIn(JSONObject obj){
		User userLoggedIn;
		try{
			System.out.println("lager user");
			int userId, studyId, startingYear;
			String firstname, surname, institution, campus, department, study;
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
			String point = obj.getString("latlon").replace("POINT(", "").replace(")", "");
			String[] latlon = point.split(" ");
			lat = Double.parseDouble(latlon[0]);
			lon = Double.parseDouble(latlon[1]);
			String photoUrl = firstname + lat + lon;
			System.out.println("lat2 : " + lat);
			System.out.println("lon2 : " + lon);
			distance = 0.0;
			String carString = obj.getString("car");
			if(carString.equals("1")) {
				car = true;
			}
			else {
				car = false;
			}
			String fbId = "";
			userLoggedIn = new User(userId, studyId, firstname, surname, lat, lon, distance, institution, campus, department, study, startingYear, car, photoUrl, fbId);
			return userLoggedIn;
		}catch(Exception e){
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
	
	
}
