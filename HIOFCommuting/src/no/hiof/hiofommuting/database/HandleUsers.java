package no.hiof.hiofommuting.database;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import no.hiof.hiofcommuting.hiofcommuting.MainActivity;
import no.hiof.hiofcommuting.objects.Filter;
import no.hiof.hiofcommuting.objects.User;
import no.hiof.hiofcommuting.util.HTTPClient;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;

import com.facebook.Session;

public class HandleUsers {
	private static List<User> userList = new ArrayList<User>();
	private static Bitmap bitmapImageFromServer;
	private static Bitmap bitmapImageFromFacebook;

	public static List<User> getAllUsers(Context context, User userLoggedIn,
			Filter filter, boolean force) {
		String urlUser = MainActivity.SERVER_URL + "/usr.py?q=allusrs";
		JSONArray arrayUser = new JsonParser().getJsonArray(urlUser,
				HandleLogin.getCookie(context));
		if (force || userList.isEmpty() || userList.size() == arrayUser.length()) {
			if (userList.isEmpty())
				userList.clear();
			for (int i = 0; i < arrayUser.length(); i++) {
				JSONObject objectUser;
				try {
					// USER OBJECT
					objectUser = arrayUser.getJSONObject(i);
					int user_id = objectUser.getInt("user_id");
					int study_id = objectUser.getInt("study_id");
					String firstname = objectUser.getString("firstname");
					String surname = objectUser.getString("surname");
					String point = objectUser.getString("latlon")
							.replace("POINT(", "").replace(")", "");
					String[] latlon = point.split(" ");
					double lat = Double.parseDouble(latlon[0]);
					double lon = Double.parseDouble(latlon[1]);
					int iscar = objectUser.getInt("car");
					boolean car = false;
					if (iscar == 1)
						car = true;
					int startingYear = objectUser.getInt("starting_year");
					double distance = distFrom(userLoggedIn.getLat(),
							userLoggedIn.getLon(), lat, lon);
					String institution = objectUser
							.getString("institution_name");
					String campus = objectUser.getString("campus_name");
					String department = objectUser.getString("department_name");
					String study = objectUser.getString("name_of_study");
					String photoUrl = firstname + lat + lon;
					String fbId = objectUser.getString("facebook_id");
					// ADD USER OBJECT TO USERLIST
					if (user_id != userLoggedIn.getUserid()) {
						userList.add(new User(user_id, study_id, firstname,
								surname, lat, lon, distance, institution,
								campus, department, study, startingYear, car,
								photoUrl, fbId, "", 0));
					}

				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			if (userList != null) {
				// Sort the list on distance from userLoggedIn to other users
				Collections.sort(userList, new Comparator<User>() {
					public int compare(User s1, User s2) {
						return Double.compare(s1.getDistance(),
								s2.getDistance());
					}
				});
			}
		} else {
			//System.out.println("ArrayList already contain all users");
		}

		if (filter == null) {
			//System.out.println("return userlist");
			return userList;
		} else {
			//System.out.println("return FILTERED userlist");

			return (filterList(userList, userLoggedIn, filter));
		}
	}

	public static List<User> filterList(List<User> list, User userLoggedIn,
			Filter f) {
		List<User> userListFiltered = new ArrayList<User>();

		for (int i = 0; i < list.size(); i++) {
			User usr = list.get(i);
			boolean shouldWeAddUser = true;

			// If user only want people from same institution
			if (f.isInstitution()) {
				if (!userLoggedIn.getInstitution().equals(usr.getInstitution()))
					shouldWeAddUser = false;
			}
			// If user only want people from same campus
			if (f.isCampus()) {
				if (!userLoggedIn.getCampus().equals(usr.getCampus()))
					shouldWeAddUser = false;
			}
			// If user only want people from same department
			if (f.isDepartment()) {
				if (!userLoggedIn.getDepartment().equals(usr.getDepartment()))
					shouldWeAddUser = false;
			}
			// If user only want people from same institution
			if (f.isStudy()) {
				if (userLoggedIn.getStudyid() != usr.getStudyid())
					shouldWeAddUser = false;
			}
			// If user started studying the same year
			if (f.isStartingYear()) {
				if (userLoggedIn.getStartingYear() != usr.getStartingYear())
					shouldWeAddUser = false;
			}
			// If user only want people with car
			if (f.isCar()) {
				if (!usr.hasCar())
					shouldWeAddUser = false;
			}
			// If user only want people within a distance
			if (f.getDistance() > 0.0) {
				if (usr.getDistance() > f.getDistance())
					shouldWeAddUser = false;
			}

			// If user is within whats filtered, add to list
			if (shouldWeAddUser) {
				userListFiltered.add(usr);
			}
		}

		return userListFiltered;
	}

	public static double[] getLatLon(Context context, String address,
			int postalCode) {
		Geocoder coder = new Geocoder(context);
		List<Address> addressList;

		try {
			// addressList = coder.getFromLocationName(address, 1);
			addressList = coder.getFromLocationName(address + " " + postalCode,
					1);
			if (address == null) {
				return null;
			}
			Address location = addressList.get(0);
			double[] latLon = { location.getLatitude(), location.getLongitude() };
			return latLon;
		} catch (Exception e) {
			return getLatLng(getLocationFromGoogle(address + " " + postalCode));
		}
	}

	public static JSONObject getLocationFromGoogle(String placesName) {

		try {
			placesName = URLEncoder.encode(placesName, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		HttpGet httpGet = new HttpGet(
				"http://maps.google.com/maps/api/geocode/json?address="
						+ placesName + "+norway&ka&sensor=false");
		//System.out.print(httpGet.getURI().toString());
		HttpClient client = new DefaultHttpClient();
		HttpResponse response;
		StringBuilder stringBuilder = new StringBuilder();

		try {
			response = client.execute(httpGet);
			HttpEntity entity = response.getEntity();
			InputStream stream = entity.getContent();
			int b;
			while ((b = stream.read()) != -1) {
				stringBuilder.append((char) b);
			}
		} catch (ClientProtocolException e) {
		} catch (IOException e) {
		}

		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject = new JSONObject(stringBuilder.toString());
		} catch (JSONException e) {

			e.printStackTrace();
		}

		/*
		 * JSONArray ja = new JsonParser().getJsonArray(
		 * "http://maps.google.com/maps/api/geocode/json?address="+ placesName +
		 * "&ka&sensor=false",null);
		 * 
		 * try { return ja.getJSONObject(0); } catch (JSONException e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); return null; }
		 */
		return jsonObject;
	}

	public static double[] getLatLng(JSONObject jsonObject) {

		Double lon = new Double(0);
		Double lat = new Double(0);

		try {

			lon = ((JSONArray) jsonObject.get("results")).getJSONObject(0)
					.getJSONObject("geometry").getJSONObject("location")
					.getDouble("lng");

			lat = ((JSONArray) jsonObject.get("results")).getJSONObject(0)
					.getJSONObject("geometry").getJSONObject("location")
					.getDouble("lat");

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return new double[] { lat, lon };

	}

	public static double distFrom(double lat1, double lng1, double lat2,
			double lng2) {
		double earthRadius = 3958.75;
		double dLat = Math.toRadians(lat2 - lat1);
		double dLng = Math.toRadians(lng2 - lng1);
		double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
				+ Math.cos(Math.toRadians(lat1))
				* Math.cos(Math.toRadians(lat2)) * Math.sin(dLng / 2)
				* Math.sin(dLng / 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		double dist = earthRadius * c;

		int meterConversion = 1609;

		return (double) (dist * meterConversion) / 1000;
	}
	
	public static void insertEmailUserToDb(final int studyId,
			final String firstName, final String surName, final double lat,
			final double lon, final double distance, final String institution,
			final String campus, final String department, final String study,
			final int startingYear, final boolean car,
			final ArrayList<String> registerData, final Context context) {

		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				HTTPClient.insertEmailUser(studyId, firstName, surName, lat,
						lon, distance, institution, campus, department, study,
						startingYear, car, registerData, context);
			}
		});

		t.start();
	}

	public static void insertFacebookUserToDb(final int studyId,
			final String firstName, final String surName, final double lat,
			final double lon, final double distance, final String institution,
			final String campus, final String department, final String study,
			final int startingYear, final boolean car, final String fbId,
			final Context context, final Session session) {

		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				HTTPClient.insertFacebookUser(studyId, firstName, surName, lat,
						lon, distance, institution, campus, department, study,
						startingYear, car, fbId, context, session);
			}
		});

		t.start();
	}

	public static void deleteUser(Context context, int userId) {
		HttpCookie c = HandleLogin.getCookie(context);
		String delete = MainActivity.SERVER_URL + "/delusr.py?uid=" + userId;

		try {
			URL url = new URL(delete);
			HttpURLConnection uc;
			uc = HTTPClient.getHttpUrlConnection(url);
			int status = uc.getResponseCode();
			uc.disconnect();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static Bitmap getProfilePicture(final String urlExtension) {

		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				bitmapImageFromServer = HTTPClient
						.getProfilePicturesFromServer("email", urlExtension,
								false);
			}
		});

		t.start();

		if (bitmapImageFromServer != null) {
			return bitmapImageFromServer;
		} else {
			return null;
		}

	}

	public static Bitmap getProfilePicFromFb(final String urlExtension,
			final boolean showLarge) {
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				if (showLarge)
					bitmapImageFromFacebook = HTTPClient
							.getProfilePicturesFromServer("facebook",
									urlExtension, true);
				else
					bitmapImageFromFacebook = HTTPClient
							.getProfilePicturesFromServer("facebook",
									urlExtension, false);
			}
		});

		t.start();

		if (bitmapImageFromFacebook != null) {
			return bitmapImageFromFacebook;
		} else {
			return null;
		}

	}

}