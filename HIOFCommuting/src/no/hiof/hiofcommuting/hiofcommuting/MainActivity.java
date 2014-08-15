package no.hiof.hiofcommuting.hiofcommuting;

import java.lang.ref.WeakReference;
import java.net.CookieHandler;
import java.net.CookieManager;

import no.hiof.hiofcommuting.R;
import no.hiof.hiofcommuting.objects.User;
import no.hiof.hiofcommuting.register.EmailLoginActivity;
import no.hiof.hiofcommuting.util.Util;
import no.hiof.hiofommuting.database.HandleLogin;
import no.hiof.hiofommuting.database.JsonParser;

import org.apache.http.cookie.Cookie;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

/*import com.facebook.Request;
 import com.facebook.Response;
 import com.facebook.Session;
 import com.facebook.SessionState;
 import com.facebook.UiLifecycleHelper;
 import com.facebook.model.GraphUser;*/

public class MainActivity extends FragmentActivity {
	

	private static final int SPLASH = 0;
	private static final int FINISH = 1;
	private Fragment[] fragments = new Fragment[2];
	private boolean isResumed = false;
	private MenuItem settings;
	FragmentManager fm = getSupportFragmentManager();
	WeakReference<Activity> weakActivity = new WeakReference<Activity>(this);
	private String fbId;
	private String token;
	// public static String SERVER_URL = "http://192.168.1.4:8888";
	// public static String SERVER_URL = "192.168.0.104:8888";
	// public static String SERVER_URL = "85.255.34.167:8888";
	public static String SERVER_URL = "https://android.hiof.no";
	public static Cookie cookie;
	private String sender_id = null;
	private String sender_firstname = null;
	private String sender_surname = null;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		CookieManager cm = new CookieManager();
		CookieHandler.setDefault(cm);

		if (getIntent().getExtras() != null) {
			sender_id = getIntent().getExtras().getString("sender_id");
			sender_firstname = getIntent().getExtras().getString(
					"sender_firstname");
			sender_surname = getIntent().getExtras()
					.getString("sender_surname");
		}

		/*
		 * uiHelper = new UiLifecycleHelper(this, callback);
		 * uiHelper.onCreate(savedInstanceState);
		 */

		setContentView(R.layout.activity_main);
		fragments[SPLASH] = fm.findFragmentById(R.id.splashFragment);
		fragments[FINISH] = fm.findFragmentById(R.id.finishProfileFragment);

		FragmentTransaction transaction = fm.beginTransaction();
		for (int i = 0; i < fragments.length; i++) {
			transaction.hide(fragments[i]);
		}
		transaction.commit();

		Util.showFragment(SPLASH, fm, fragments, "HIOFCommuting", weakActivity);
	}

	@Override
	public void onBackPressed() {
		if (fragments[FINISH].isVisible()) {
			Util.showFragment(SPLASH, fm, fragments, "HIOFCommuting",
					weakActivity);
			// Session session = Session.getActiveSession();
			// session.closeAndClearTokenInformation();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		// uiHelper.onResume();
		isResumed = true;
	}

	@Override
	public void onPause() {
		super.onPause();
		// uiHelper.onPause();
		isResumed = false;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == 9999)
		{
			startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=com.google.android.gms")));
		}
		// uiHelper.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// uiHelper.onDestroy();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		// uiHelper.onSaveInstanceState(outState);
	}

	// private void onSessionStateChange(Session session, SessionState state,
	// Exception exception) {
	// // Only make changes if the activity is visible
	// if (isResumed) {
	// FragmentManager manager = getSupportFragmentManager();
	// // Get the number of entries in the back stack
	// int backStackSize = manager.getBackStackEntryCount();
	// // Clear the back stack
	// for (int i = 0; i < backStackSize; i++) {
	// manager.popBackStack();
	// }
	//
	// if (state.isOpened()) {
	// // If the session state is open:
	// makeMeRequest(session);
	// } else if (state.isClosed()) {
	// // If the session state is closed:
	// Util.showFragment(SPLASH, fm, fragments, "HIOFCommuting",
	// weakActivity);
	// }
	// }
	// }

	// public void makeMeRequest(final Session session) {
	// // Make an API call to get user data and define a
	// // new callback to handle the response.
	// Request request = Request.newMeRequest(session,
	// new Request.GraphUserCallback() {
	// @Override
	// public void onCompleted(GraphUser user, Response response) {
	// // If the response is successful
	// if (session == Session.getActiveSession()) {
	// if (user != null) {
	// fbId = user.getId();
	// token = session.getAccessToken();
	// System.out.println("fbsession" + fbId);
	// new AuthenticateUser().execute(fbId, session);
	// }
	// }
	// }
	// });
	// request.executeAsync();
	// }

	// public Session getFacebookSession() {
	// Session session = Session.getActiveSession();
	// return session;
	// }

	public void loggInnMedEpost(View view) {
		Intent intent = new Intent(this, EmailLoginActivity.class);
		startActivity(intent);
	}

	@Override
	protected void onResumeFragments() {
		super.onResumeFragments();
		// Session session = Session.getActiveSession();

		// if (session != null && session.isOpened()) {
		// // if the session is already open,
		// // AUTHENTICATE USER IN OUR DATABASE
		// makeMeRequest(session);

		int result = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		if(result  != ConnectionResult.SUCCESS)
		{
			GooglePlayServicesUtil.getErrorDialog(result, this, 9999).show();
			return;
		}

		Intent intent = new Intent(this, EmailLoginActivity.class);
		if (sender_id != null) {
			intent.putExtra("sender_id", sender_id);
			intent.putExtra("sender_firstname", sender_firstname);
			intent.putExtra("sender_surname", sender_surname);
		}
		startActivity(intent);
	}

	// private UiLifecycleHelper uiHelper;
	// private Session.StatusCallback callback = new Session.StatusCallback() {
	// @Override
	// public void call(Session session, SessionState state,
	// Exception exception) {
	// onSessionStateChange(session, state, exception);
	// }
	// };

	private class AuthenticateUser extends AsyncTask<Object, Void, JSONObject> {

		@Override
		protected JSONObject doInBackground(Object... params) {
			try {
				JsonParser jp = new JsonParser();
				JSONArray jsonFbArr;
				jp.saveCookie = true;
				jsonFbArr = jp.getJsonArray(MainActivity.SERVER_URL
						+ "/usr.py?q=fbusrid&fbid=" + fbId + "&token=" + token,
						HandleLogin.getCookie(MainActivity.this));
				if (jsonFbArr == null) {
					return null;
				}
				if (jp.cookie != null) {
					HandleLogin.saveCookie(jp.cookie, MainActivity.this);
				}
				JSONObject jsonObj = (JSONObject) jsonFbArr.get(0);
				return jsonObj;
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		}

		@Override
		protected void onPostExecute(JSONObject obj) {
			try {
				if (obj.getString("user_id").equals("-100")) {
					// System.out.println("User er ikke registrert i systemet fra før");
					Util.showFragment(FINISH, fm, fragments, "Fullfør profil",
							weakActivity);
				} else {
					// System.out.println("User ER registrert i systemet fra før");
					User userLoggedIn = HandleLogin
							.getCurrentFacebookUserLoggedIn(obj);
					// System.out.println(userLoggedIn + " er null");
					// Session session = Session.getActiveSession();
					Intent intent = new Intent(MainActivity.this,
							no.hiof.hiofcommuting.tab.TabListenerActivity.class);
					intent.putExtra("CURRENT_USER", userLoggedIn);
					// intent.putExtra("FACEBOOK_SESSION", session);
					if (sender_id != null) {
						intent.putExtra("sender_id", sender_id);
						intent.putExtra("sender_firstname", sender_firstname);
						intent.putExtra("sender_surname", sender_surname);
					}
					startActivity(intent);
					finish();
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
	}
	
}
