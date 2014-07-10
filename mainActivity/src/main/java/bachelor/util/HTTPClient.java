package bachelor.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import bachelor.MainActivity;

public class HTTPClient {

	public static boolean sent = false;

	public static void post(String operation, int sender, int receiver,
			String message) {
		final String URL = MainActivity.SERVER_URL + "/hcserv.py?q=";
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(URL);

		if (operation.equals("read")) {
			final List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
					3);
			nameValuePairs.add(new BasicNameValuePair("q", operation));
			nameValuePairs.add(new BasicNameValuePair("user_id_sender", String
					.valueOf(sender)));
			nameValuePairs.add(new BasicNameValuePair("user_id_receiver",
					String.valueOf(receiver)));

			try {
				httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs,
						"utf-8"));
				HttpResponse httpResponse = httpClient.execute(httpPost);

				if (httpResponse.getStatusLine().getStatusCode() == 200)
					sent = true;

			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("melding" + message);
			final List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
					4);
			nameValuePairs.add(new BasicNameValuePair("q", operation));
			nameValuePairs.add(new BasicNameValuePair("user_id_sender", String
					.valueOf(sender)));
			nameValuePairs.add(new BasicNameValuePair("user_id_receiver",
					String.valueOf(receiver)));
			nameValuePairs.add(new BasicNameValuePair("message", message));

			try {
				httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs,
						"utf-8"));
				HttpResponse httpResponse = httpClient.execute(httpPost);

				if (httpResponse.getStatusLine().getStatusCode() == 200)
					sent = true;

			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void insertEmailUser(final int studyId,
			final String firstName, final String surName, final double lat,
			final double lon, final double distance, final String institution,
			final String campus, final String department, final String study,
			final int startingYear, final boolean car,
			ArrayList<String> registerData) {
		final String URL = MainActivity.SERVER_URL + "/regusr.py?q=";
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(URL);

		String q = "emailUser";
		String email = registerData.get(2);
		// TODO : Hash password
		String pw = registerData.get(3);
		String carString;
		if (car) {
			carString = "true";
		} else {
			carString = "false";
		}

		final List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
				10);
		nameValuePairs.add(new BasicNameValuePair("q", q));
		nameValuePairs.add(new BasicNameValuePair("sid", String
				.valueOf(studyId)));
		nameValuePairs.add(new BasicNameValuePair("fname", String
				.valueOf(firstName)));
		nameValuePairs.add(new BasicNameValuePair("sname", String
				.valueOf(surName)));
		nameValuePairs.add(new BasicNameValuePair("lon", String.valueOf(lon)));
		nameValuePairs.add(new BasicNameValuePair("lat", String.valueOf(lat)));
		nameValuePairs.add(new BasicNameValuePair("car", String
				.valueOf(carString)));
		nameValuePairs.add(new BasicNameValuePair("starting_year", String
				.valueOf(startingYear)));
		nameValuePairs.add(new BasicNameValuePair("email", String
				.valueOf(email)));
		nameValuePairs.add(new BasicNameValuePair("pw", String.valueOf(pw)));

		try {
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "utf-8"));
			HttpResponse httpResponse = httpClient.execute(httpPost);

			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				sent = true;
			}
			System.out.println("sendt " + sent);

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void insertFacebookUser(final int studyId,
			final String firstName, final String surName, final double lat,
			final double lon, final double distance, final String institution,
			final String campus, final String department, final String study,
			final int startingYear, final boolean car, final String fbId) {
		final String URL = MainActivity.SERVER_URL + "/regfbusr.py?q=";
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(URL);

		String q = "facebookUser";
		String carString;
		if (car) {
			carString = "true";
		} else {
			carString = "false";
		}
		System.out.println("parameters : q=" + q + "&sid=" + studyId
				+ "&fname=" + firstName + "&sname=" + surName + "&lon=" + lon
				+ "&lat=" + lat + "&car=" + car + "&starting_year="
				+ startingYear + "&fbid=" + fbId);
		final List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
				9);
		nameValuePairs.add(new BasicNameValuePair("q", q));
		nameValuePairs.add(new BasicNameValuePair("sid", String
				.valueOf(studyId)));
		nameValuePairs.add(new BasicNameValuePair("fname", String
				.valueOf(firstName)));
		nameValuePairs.add(new BasicNameValuePair("sname", String
				.valueOf(surName)));
		nameValuePairs.add(new BasicNameValuePair("lon", String.valueOf(lon)));
		nameValuePairs.add(new BasicNameValuePair("lat", String.valueOf(lat)));
		nameValuePairs.add(new BasicNameValuePair("car", String
				.valueOf(carString)));
		nameValuePairs.add(new BasicNameValuePair("starting_year", String
				.valueOf(startingYear)));
		nameValuePairs
				.add(new BasicNameValuePair("fbid", String.valueOf(fbId)));

		try {
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "utf-8"));
			HttpResponse httpResponse = httpClient.execute(httpPost);

			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				sent = true;
			}
			System.out.println("sendt " + sent);

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Bitmap getProfilePicturesFromServer(String source, String urlExtension, boolean showLarge) {
		String urlString = "";
        String pictureSize = "small";

        if (showLarge)
            pictureSize = "400";

		if(source.equalsIgnoreCase("email")) {
			urlString = "http://www.frostbittmedia.com/upload/files/" + urlExtension + ".jpg";
		}
		else if(source.equalsIgnoreCase("facebook")) {
            urlString = "https://graph.facebook.com/" + urlExtension + "/picture?width=" + pictureSize + "&height=" + pictureSize;
		}
		
		InputStream in = null;
		int response = -1;

		Bitmap bitmap = null;
		try {
			URLConnection urLConn = new URL(urlString).openConnection();
			HttpURLConnection httpConn = (HttpURLConnection) urLConn;
			httpConn.setAllowUserInteraction(false);
			httpConn.setInstanceFollowRedirects(false);
			httpConn.setRequestMethod("GET");
			httpConn.connect();
			if (!(urLConn instanceof HttpURLConnection)) {
				return null;
			}
			response = httpConn.getResponseCode();
			System.out.println("Response : " +response + httpConn.getResponseCode() + httpConn.getURL() + HttpURLConnection.getFollowRedirects() + httpConn.getHeaderField("Location"));
			if (response == HttpURLConnection.HTTP_OK) {
				in = httpConn.getInputStream();
				bitmap = BitmapFactory.decodeStream(in);
				in.close();
				bitmap = Bitmap.createScaledBitmap(bitmap, 400, 400, false);
				return bitmap;
			}
			//If we got redirected from the page
			else if (response == 302) {
				String newLocationUrl = httpConn.getHeaderField("Location");
				URLConnection con = new URL(newLocationUrl).openConnection();
				HttpURLConnection httpC = (HttpURLConnection) con;
				httpC.setAllowUserInteraction(false);
				httpC.setInstanceFollowRedirects(false);
				httpC.setRequestMethod("GET");
				httpC.connect();
				InputStream is = httpC.getInputStream();
				bitmap = BitmapFactory.decodeStream(is);
				is.read();
				is.close();
				bitmap = Bitmap.createScaledBitmap(bitmap, 400, 400, false);
				return bitmap;
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return null;
	}
}