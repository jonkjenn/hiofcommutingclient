package no.hiof.hiofcommuting.tab;

import java.io.IOException;
import java.util.List;

import no.hiof.hiofcommuting.R;
import no.hiof.hiofcommuting.chat.ChatActivity;
import no.hiof.hiofcommuting.hiofcommuting.ChatService;
import no.hiof.hiofcommuting.hiofcommuting.GcmBroadcastReceiver;
import no.hiof.hiofcommuting.hiofcommuting.MainActivity;
import no.hiof.hiofcommuting.objects.Filter;
import no.hiof.hiofcommuting.objects.User;
import no.hiof.hiofcommuting.util.HTTPClient;
import no.hiof.hiofommuting.database.HandleLogin;
import no.hiof.hiofommuting.database.HandleUsers;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentManager.BackStackEntry;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.NumberPicker;

import com.facebook.Session;
import com.google.android.gms.gcm.GoogleCloudMessaging;

public class TabListenerActivity extends FragmentActivity implements
		ActionBar.TabListener {

	public static User userLoggedIn;
	public static Filter filter;

	public static final String PROPERTY_REG_ID = "registration_id";
	private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

	String SENDER_ID = "299457056241";

	static GoogleCloudMessaging gcm;
	SharedPreferences prefs;

	private FilterUsersFragment filterUserFragment = null;

	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			TabListenerActivity.this.receiveBroadCast(intent);
		}
	};

	Session session = null;

	// Bitmap profilePic;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Receive current user logged in
		if (getIntent().hasExtra("CURRENT_USER")) {
			setUserLoggedIn((User) getIntent().getSerializableExtra(
					"CURRENT_USER"));
			// profilePic =
			// (Bitmap)getIntent().getParcelableExtra("PROFILE_PIC");

			/*
			 * try { session = (Session) getIntent().getSerializableExtra(
			 * "FACEBOOK_SESSION"); // System.out.println(session.getState());
			 * // System.out.println("Logget in with Facebook"); } catch
			 * (NullPointerException e) { //
			 * System.out.println("Logget in with email"); }
			 */

			if (gcm == null) {
				gcm_setup();
			}

			if (User.userList == null || User.userList.size() == 0) {
				getUsersThread();
			}

			/*
			 * int convId = getIntent().getExtras().getInt("conversationid"); if
			 * (convId > 0) { onTabSelected(actionBar.getTabAt(2), null); }
			 */
		}

		setContentView(R.layout.activity_tab_listener);
		String not = getIntent().getStringExtra("SERVICE");
		Intent chatServiceIntent = new Intent(this, ChatService.class);
		chatServiceIntent.putExtra("CURRENT_USER", userLoggedIn);
		startService(chatServiceIntent);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
			FragmentManager fm = getSupportFragmentManager();
			FragmentTransaction transaction = fm.beginTransaction();
			PlaceholderFragment pf = new PlaceholderFragment();
			transaction.add(R.id.container, pf);
		}

		// Set up the action bar to show tabs.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// for each of the sections in the app, add a tab to the action bar.
		actionBar.addTab(actionBar.newTab().setText(R.string.tab_map)
				.setTabListener(this));
		actionBar.addTab(actionBar.newTab().setText(R.string.tab_list)
				.setTabListener(this));
		actionBar.addTab(actionBar.newTab().setText(R.string.tab_inbox)
				.setTabListener(this));

		startChatFromIntent(getIntent());
	}

	private void gcm_setup() {
		gcm = GoogleCloudMessaging.getInstance(this);
		try {
			int regversion = getPackageManager().getPackageInfo(
					getPackageName(), 0).versionCode;
			if (userLoggedIn.getGcmId().isEmpty()
					|| userLoggedIn.getGcmId().equals("null")
					|| userLoggedIn.getGcmVersion() < regversion) {
				registerInBackground();
			}
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void registerInBackground() {
		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				try {
					if (gcm == null) {
						gcm = GoogleCloudMessaging
								.getInstance(TabListenerActivity.this);
					}
					String regid = gcm.register(SENDER_ID);
					int regversion = 0;
					try {
						regversion = getPackageManager().getPackageInfo(
								getPackageName(), 0).versionCode;
					} catch (NameNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					HTTPClient.insertGcmId(regid, regversion,
							HandleLogin.getCookie(TabListenerActivity.this));

					userLoggedIn.setGcmId(regid);
				} catch (IOException ex) {

				}
				return null;
			}
		}.execute();
	}

	private void getUsersThread() {
		// Get all users from database
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					setUserList(HandleUsers.getAllUsers(getBaseContext(),
							getUserLoggedIn(), getFilter()));
					// System.out.println("Get users done");
				} catch (NullPointerException e) {
					// //Log.e("ITCRssReader", "Get all users failed" +
					// e.getMessage());
				}
			}
		});
		t.start();
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	public void onBackPressed() {
		// Do nothing
	}

	/*
	 * public Bitmap getProfilePic() { return profilePic; }
	 */

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		// menu.add(R.string.settings);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch (item.getItemId()) {
		case R.id.action_settings:
			return true;
		case R.id.endreAdresse:
			if (mapFragment != null) {
				mapFragment.endreAdresse();
			}
			return true;
		case R.id.newActivity:
			// System.out.println("Logging out");
			performLogout();
			return true;
		case R.id.filter_user:
			showUserFilter();
			return true;
		case R.id.avmelding:
			avmelding();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	// Show filter fragment
	private void showUserFilter() {
		FragmentTransaction fragmentTransaction = getSupportFragmentManager()
				.beginTransaction();
		fragmentTransaction.setCustomAnimations(R.anim.enter_from_bottom,
				R.anim.exit_to_bottom);
		if (filterUserFragment == null) {
			// Create fragment
			filterUserFragment = new FilterUsersFragment();
			fragmentTransaction.add(R.id.container, filterUserFragment);
			fragmentTransaction.commit();
		} else if (filterUserFragment.isHidden()) {
			// Show fragment
			fragmentTransaction.show(filterUserFragment);
			fragmentTransaction.commit();
		}
	}

	// Button commitAndHideUserFilter is clicked in filter fragment
	public void commitAndHideUserFilter(View v) {
		setFilter(null);
		CheckBox cbinstitution = (CheckBox) findViewById(R.id.checkbox_filter_institution);
		CheckBox cbcampus = (CheckBox) findViewById(R.id.checkbox_filter_campus);
		CheckBox cbstudy = (CheckBox) findViewById(R.id.checkbox_filter_study);
		CheckBox cbdepartment = (CheckBox) findViewById(R.id.checkbox_filter_department);
		CheckBox cbstartingyear = (CheckBox) findViewById(R.id.checkbox_filter_startingyear);
		CheckBox cbcar = (CheckBox) findViewById(R.id.checkbox_filter_car);
		CheckBox cbdistance = (CheckBox) findViewById(R.id.checkbox_filter_distance);
		NumberPicker npdistance = (NumberPicker) findViewById(R.id.numberpicker_filter_distance);

		int studyId = userLoggedIn.getStudyid();
		double distance = 0.0;
		boolean institution = false;
		boolean campus = false;
		boolean department = false;
		boolean study = false;
		boolean startingyear = false;
		boolean car = false;

		if (cbinstitution.isChecked())
			institution = true;
		if (cbcampus.isChecked())
			campus = true;
		if (cbdepartment.isChecked())
			department = true;
		if (cbstudy.isChecked())
			study = true;
		if (cbstartingyear.isChecked())
			startingyear = true;
		if (cbcar.isChecked())
			car = true;
		if (cbdistance.isChecked())
			distance = (double) npdistance.getValue();

		Filter.isFilterSet = institution || campus || department || study
				|| startingyear || car || cbdistance.isChecked();

		setFilter(new Filter(studyId, distance, institution, campus,
				department, study, startingyear, car));

		if (filterUserFragment != null && filterUserFragment.isVisible()) {
			// Hide fragment
			FragmentTransaction fragmentTransaction = getSupportFragmentManager()
					.beginTransaction();
			fragmentTransaction.setCustomAnimations(R.anim.enter_from_bottom,
					R.anim.exit_to_bottom);
			fragmentTransaction
					.setTransition(FragmentTransaction.TRANSIT_EXIT_MASK);
			fragmentTransaction.hide(filterUserFragment);
			fragmentTransaction.commit();
		}

		FragmentManager fm = getSupportFragmentManager();
		int stackCount = fm.getBackStackEntryCount();
		BackStackEntry previousFragment = fm
				.getBackStackEntryAt(stackCount - 1);
		if (previousFragment.getName().equalsIgnoreCase("Map")) {
			Fragment tm = new TabMapFragment();
			fm.beginTransaction().replace(R.id.fragment_tab_container, tm)
					.commit();
		} else if (previousFragment.getName().equalsIgnoreCase("List")) {
			Fragment tl = new TabListFragment();
			fm.beginTransaction().replace(R.id.fragment_tab_container, tl)
					.commit();
		} else {
			Fragment ti = new TabInboxFragment();
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.fragment_tab_container, ti)
					.addToBackStack("Chat").commit();
		}
	}

	private void performLogout() {

		HandleLogin.deleteCookie(this);

		if (session != null) {
			try {
				session = Session.getActiveSession();
				session.closeAndClearTokenInformation();
			} catch (NullPointerException e) {
				// System.out
				// .println("performLogout(): User was logged in with email");
			}
		}
		setUserLoggedIn(null);
		// userLoggedIn = null;
		User.userList.clear();
		// setUserList(null); //reset users in map/list because distance is
		// related to the users logged in
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
		finish();
	}

	public User getUserLoggedIn() {
		return userLoggedIn;
	}

	public void setUserLoggedIn(User userLoggedIn) {
		this.userLoggedIn = userLoggedIn;
	}

	public Filter getFilter() {
		return filter;
	}

	public void setFilter(Filter filter) {
		this.filter = filter;
	}

	public List<User> getUserList() {
		return User.userList;
	}

	public void setUserList(List<User> users) {
		User.userList = users;
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_tab_listener,
					container, false);
			return rootView;
		}
	}

	TabMapFragment mapFragment;

	@Override
	public void onTabSelected(Tab tab, android.app.FragmentTransaction ft) {
		if (tab.getPosition() == 0) {
			mapFragment = new TabMapFragment();
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.fragment_tab_container, mapFragment)
					.addToBackStack("Map").commit();
			// System.out.println("Map");
			setTitle("Kart");
		}
		if (tab.getPosition() == 1) {
			mapFragment = null;
			Fragment tl = new TabListFragment();
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.fragment_tab_container, tl)
					.addToBackStack("List").commit();
			// System.out.println("Liste");
			setTitle("Liste");
		}
		if (tab.getPosition() == 2) {
			mapFragment = null;
			Fragment ti = new TabInboxFragment();
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.fragment_tab_container, ti)
					.addToBackStack("Chat").commit();
			// System.out.println("Inbox");
			setTitle("Inbox");
		}

	}

	@Override
	public void onTabUnselected(Tab tab, android.app.FragmentTransaction ft) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onTabReselected(Tab tab, android.app.FragmentTransaction ft) {
		if (tab.getPosition() == 0) {

		}
	}

	private void avmelding() {
		AlertDialog.Builder ab = new AlertDialog.Builder(this);
		ab.setMessage("Bekreft at du vil slette din konto")
				.setPositiveButton("Slett konto", new OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						new AsyncTask<Void, Void, Void>() {

							@Override
							protected Void doInBackground(Void... params) {
								HandleUsers.deleteUser(
										TabListenerActivity.this,
										userLoggedIn.getUserid());
								return null;
							}

							protected void onPostExecute(Void result) {
								performLogout();
							}

						}.execute();
					}
				}).setNegativeButton("Avbryt", new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});

		ab.create().show();
	}

	@Override
	protected void onResume() {
		super.onResume();

		 IntentFilter iff = new IntentFilter();
		 iff.addAction("com.google.android.c2dm.intent.RECEIVE");
		 this.registerReceiver(mBroadcastReceiver, iff);

	}

	@Override
	protected void onPause() {
		super.onPause();
		 this.unregisterReceiver(mBroadcastReceiver);
	}

	public void receiveBroadCast(Intent intent) {

		if (GcmBroadcastReceiver.checkGCMMessage(this, intent)) {

			Intent i = new Intent(this,
					no.hiof.hiofcommuting.tab.TabListenerActivity.class);
			i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

			GcmBroadcastReceiver.generateNotification(this, intent, i);
			mBroadcastReceiver.abortBroadcast();
		}
	}

	private void startChatFromIntent(Intent intent) {
		if (intent != null && intent.getExtras().containsKey("sender_id")) {
			Intent i = new Intent(this, ChatActivity.class);
			i.putExtra("CURRENT_USER", userLoggedIn);
			i.putExtra(
					"SELECTED_USER",
					new User(Integer.parseInt(intent.getExtras().getString(
							"sender_id")), intent.getExtras().getString(
							"sender_firstname"), intent.getExtras().getString(
							"sender_surname")));
			startActivity(i);
		}
	}
}
