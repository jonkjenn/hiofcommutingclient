package no.hiof.hiofommuting.database;

import java.io.IOException;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import no.hiof.hiofcommuting.util.HTTPClient;

import org.json.JSONArray;
import org.json.JSONException;


public class JsonParser {

	public boolean saveCookie = false;
	public HttpCookie cookie;

	/*public JSONArray getJsonArray(String url, Cookie cookie) {
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
	}*/
	
	public JSONArray getJsonArray(String url, HttpCookie cookie) {
	    try {
	    	
	    	if(cookie != null)
	    	{
	    		HTTPClient.addCookie(cookie);
	    	}
	    	
	    	
	        URL u = new URL(url);
			HttpURLConnection c = (HttpURLConnection) u.openConnection();
			c.setRequestProperty("Content-Type",
					"application/json;charset=utf-8");
			c.setRequestProperty("Connection", "close");

	        int status = c.getResponseCode();

	        switch (status) {
	            case 200:
	            	String response = HTTPClient.readResponse(c);
	            	
	                if(saveCookie)
	                {
	                	saveCookie(HTTPClient.getCookie("hccook"));
	                }
				try {
					return new JSONArray(response);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        }

	    } catch (MalformedURLException ex) {
	    	return null;
	    } catch (IOException ex) {
	    	return null;
	    }
	    return null;
	}

	private void saveCookie(HttpCookie c) {
		this.cookie = c;
	}
}
