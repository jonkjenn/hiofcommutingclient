package no.hiof.hiofcommuting.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import no.hiof.hiofcommuting.hiofcommuting.MainActivity;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.facebook.Session;

public class HTTPClient {

	public static boolean sent = false;

	private static int post_data(String url, String data, HttpCookie cookie) {
		try {
			URL u = new URL(url);
			HttpURLConnection c = (HttpURLConnection) u.openConnection();
			c.setDoOutput(true);
			c.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded;charset=utf-8");
			c.setRequestProperty("Connection", "close");

			if (cookie != null) {
				try {
					((CookieManager) CookieHandler.getDefault())
							.getCookieStore().add(
									new URI(MainActivity.SERVER_URL), cookie);
				} catch (URISyntaxException e) {
					e.printStackTrace();
				}
			}

			c.setFixedLengthStreamingMode(data.getBytes("UTF-8").length);
			// c.setChunkedStreamingMode(1000);
			PrintWriter pw = new PrintWriter(new OutputStreamWriter(
					c.getOutputStream(), "UTF-8"));
			pw.write(data);
			pw.flush();
			pw.close();

			int code = c.getResponseCode();
			c.disconnect();
			return code;

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
	}

	public static void post(String operation, int sender, int receiver,
			String message, HttpCookie cookie) {
		final String URL = MainActivity.SERVER_URL + "/hcserv.py";

		final List<NameValuePair> nameValuePairs;

		if (operation.equals("read")) {
			nameValuePairs = new ArrayList<NameValuePair>(3);
			nameValuePairs.add(new BasicNameValuePair("q", operation));
			nameValuePairs.add(new BasicNameValuePair("user_id_sender", String
					.valueOf(sender)));
			nameValuePairs.add(new BasicNameValuePair("user_id_receiver",
					String.valueOf(receiver)));

		} else {
			nameValuePairs = new ArrayList<NameValuePair>(4);
			nameValuePairs.add(new BasicNameValuePair("q", operation));
			nameValuePairs.add(new BasicNameValuePair("user_id_sender", String
					.valueOf(sender)));
			nameValuePairs.add(new BasicNameValuePair("user_id_receiver",
					String.valueOf(receiver)));
			nameValuePairs.add(new BasicNameValuePair("message", message));
		}

		if (post_data(URL, createPost(nameValuePairs), cookie) == 200) {
			sent = true;
		}
	}
	
	public static boolean updateAddress(double lat, double lon)
	{
		final String URL = MainActivity.SERVER_URL + "/update_address.py";
		final List<NameValuePair> nvp = new ArrayList<NameValuePair>(2);		
		nvp.add(new BasicNameValuePair("lat", Double.toString(lat)));
		nvp.add(new BasicNameValuePair("lon", Double.toString(lon)));

		if (post_data(URL, createPost(nvp), null) == 200) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean insertEmailUser(final int studyId,
			final String firstName, final String surName, final double lat,
			final double lon, final double distance, final String institution,
			final String campus, final String department, final String study,
			final int startingYear, final boolean car,
			ArrayList<String> registerData, Context context) {
		final String URL = MainActivity.SERVER_URL + "/regusr.py";

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

		if (post_data(URL, createPost(nameValuePairs), null) == 200) {
			return true;
		} else {
			return false;
		}
	}

	public static void insertFacebookUser(final int studyId,
			final String firstName, final String surName, final double lat,
			final double lon, final double distance, final String institution,
			final String campus, final String department, final String study,
			final int startingYear, final boolean car, final String fbId,
			Context context, Session session) {
		final String URL = MainActivity.SERVER_URL + "/regfbusr.py";

		String q = "facebookUser";
		String carString;
		if (car) {
			carString = "true";
		} else {
			carString = "false";
		}
		// System.out.println("parameters : q=" + q + "&sid=" + studyId +
		// "&fname=" + firstName + "&sname=" + surName + "&lon=" + lon + "&lat="
		// + lat + "&car=" + car + "&starting_year=" + startingYear + "&fbid=" +
		// fbId);
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
		nameValuePairs.add(new BasicNameValuePair("token", session
				.getAccessToken()));

		if (post_data(URL, createPost(nameValuePairs), null) == 200) {
			sent = true;
		}
	}

	public static Bitmap getProfilePicturesFromServer(String source,
			String urlExtension, boolean showLarge) {
		String urlString = "";
		String pictureSize = "small";

		if (showLarge)
			pictureSize = "400";

		if (source.equalsIgnoreCase("email")) {
			urlString = "http://www.frostbittmedia.com/upload/files/"
					+ urlExtension + ".jpg";
		} else if (source.equalsIgnoreCase("facebook")) {
			urlString = "https://graph.facebook.com/" + urlExtension
					+ "/picture?width=" + pictureSize + "&height="
					+ pictureSize;
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
			// System.out.println("Response : " + response +
			// httpConn.getResponseCode() + httpConn.getURL() +
			// HttpURLConnection.getFollowRedirects() +
			// httpConn.getHeaderField("Location"));
			if (response == HttpURLConnection.HTTP_OK) {
				in = httpConn.getInputStream();
				bitmap = BitmapFactory.decodeStream(in);
				in.close();
				bitmap = Bitmap.createScaledBitmap(bitmap, 400, 400, false);
				return bitmap;
			}
			// If we got redirected from the page
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

	public static void insertGcmId(final String gcmId, HttpCookie cookie) {
		final String URL = MainActivity.SERVER_URL + "/reggcm.py?";

		final List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
				1);

		nameValuePairs.add(new BasicNameValuePair("gcmId", gcmId));

		if (post_data(URL, createPost(nameValuePairs), null) == 200) {
			sent = true;
		}
	}

	public static void addCookie(HttpCookie cookie) {

		try {
			if(CookieHandler.getDefault() == null){
				CookieHandler.setDefault(new CookieManager());
			}
			((CookieManager) CookieHandler.getDefault()).getCookieStore().add(
					new URI(MainActivity.SERVER_URL), cookie);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static HttpCookie getCookie(String name) {

		try {
			List<HttpCookie> cookies = ((CookieManager) CookieHandler
					.getDefault()).getCookieStore().get(
					new URI(MainActivity.SERVER_URL));

			for (HttpCookie co : cookies) {
				if (co.getName().equals(name)) {
					return co;
				}
			}
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	public static String readResponse(HttpURLConnection c) {
		BufferedReader br;
		try {
			br = new BufferedReader(new InputStreamReader(c.getInputStream()));
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = br.readLine()) != null) {
				sb.append(line + "\n");
			}
			br.close();
			return sb.toString();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

	}

	public static HttpURLConnection getHttpUrlConnection(URL url)
			throws IOException {

		HttpURLConnection c = (HttpURLConnection) url.openConnection();
		c.setRequestMethod("GET");
		//c.setRequestProperty("Content-length", "0");
        c.setRequestProperty("Connection", "close");
//		c.setUseCaches(false);
	//	c.setAllowUserInteraction(false);
		//c.setConnectTimeout(5000);
		//c.setReadTimeout(5000);
		c.connect();

		return c;
	}

	public static String createPost(List<NameValuePair> nameValuePairs) {
		String out = "";
		for (NameValuePair n : nameValuePairs) {
			if (out.length() > 0) {
				out = out + "&";
			}
			try {
				out = out + URLEncoder.encode(n.getName(), "UTF-8") + "="
						+ URLEncoder.encode(n.getValue(), "UTF-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return out;
	}

}