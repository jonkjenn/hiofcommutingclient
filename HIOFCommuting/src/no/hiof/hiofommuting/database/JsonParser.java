package no.hiof.hiofommuting.database;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;

public class JsonParser {

	public boolean saveCookie = false;
	public Cookie cookie;

	public JSONArray getJsonArray(String url, Cookie cookie) {
		DefaultHttpClient httpclient = new DefaultHttpClient();

		if (cookie != null) {
			httpclient.getCookieStore().addCookie(cookie);
		}

		HttpResponse response;
		try {
			response = httpclient.execute(new HttpGet(url));
			StatusLine statusLine = response.getStatusLine();
			if (statusLine.getStatusCode() == HttpStatus.SC_OK) {

				if (saveCookie) {
					List<Cookie> cook = httpclient.getCookieStore()
							.getCookies();
					for (Cookie c : cook) {
						if (c.getName().equals("hccook")) {
							saveCookie(c);
						}
					}
				}

				ByteArrayOutputStream out = new ByteArrayOutputStream();
				response.getEntity().writeTo(out);
				out.close();

				JSONArray arr = new JSONArray(out.toString());
				return arr;
			} else {
				// Closes the connection.
				response.getEntity().getContent().close();
				throw new IOException(statusLine.getReasonPhrase());
			}
		} catch (ClientProtocolException e) {
			// TODO Handle problems..
			return null;
		} catch (IOException e) {
			// TODO Handle problems..
			return null;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			return null;
		}
	}

	private void saveCookie(Cookie c) {
		this.cookie = c;
	}
}
