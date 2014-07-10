package bachelor.tab;

import java.util.List;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentManager.BackStackEntry;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.NumberPicker;
import bachelor.database.HandleUsers;
import bachelor.objects.Filter;
import bachelor.objects.User;

import bachelor.ChatService;
import bachelor.MainActivity;
import com.bachelor.hiofcommuting.R;
import com.facebook.Session;

public class TabListenerActivity extends FragmentActivity implements
		ActionBar.TabListener {

	private User userLoggedIn;
	private Filter filter;
	

	private FilterUsersFragment filterUserFragment = null;

	Session session = null;

	// Bitmap profilePic;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Receive current user logged in
		setUserLoggedIn((User) getIntent().getSerializableExtra("CURRENT_USER"));
		// profilePic = (Bitmap)getIntent().getParcelableExtra("PROFILE_PIC");
		setContentView(R.layout.activity_tab_listener);

		try {
			session = (Session) getIntent().getSerializableExtra(
					"FACEBOOK_SESSION");
			System.out.println(session.getState());
			System.out.println("Logget in with Facebook");
		} catch (NullPointerException e) {
			System.out.println("Logget in with email");
		}
		
		String not = getIntent().getStringExtra("SERVICE");
		System.out.println("Service : " + not);
		System.out.println("Starter service");
		Intent chatServiceIntent = new Intent(this, ChatService.class);
		chatServiceIntent.putExtra("CURRENT_USER", userLoggedIn);
		startService(chatServiceIntent);
		//String notification = 
		System.out.println("User logged in is "
				+ getUserLoggedIn().getUserid());

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

		getUsersThread();
	}

	private void getUsersThread() {
		// Get all users from database
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					setUserList(HandleUsers.getAllUsers(getBaseContext(),
							getUserLoggedIn(), getFilter()));
					System.out.println("Get users done");
				} catch (NullPointerException e) {
					Log.e("ITCRssReader", e.getMessage());
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
		case R.id.newActivity:
			System.out.println("Logging out");
			performLogout();
			return true;
		case R.id.filter_user:
			showUserFilter();
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
			distance = (double)npdistance.getValue();

        Filter.isFilterSet = institution || campus || department || study || startingyear || car || cbdistance.isChecked();

		setFilter(new Filter(studyId, distance, institution, campus, department, study, startingyear, car));

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
		BackStackEntry previousFragment = fm.getBackStackEntryAt(stackCount-1);
		if(previousFragment.getName().equalsIgnoreCase("Map")){
			Fragment tm = new TabMapFragment();
			fm.beginTransaction().replace(R.id.fragment_tab_container, tm).commit();
		} 
		else if(previousFragment.getName().equalsIgnoreCase("List")) {
			Fragment tl = new TabListFragment();
			fm.beginTransaction().replace(R.id.fragment_tab_container, tl).commit();
		}
		else {
			Fragment ti = new TabInboxFragment();
			getSupportFragmentManager().beginTransaction().replace(R.id.fragment_tab_container, ti).addToBackStack("Chat").commit();
		}
	}

	private void performLogout() {
		if (session != null) {
			try {
				session = Session.getActiveSession();
				session.closeAndClearTokenInformation();
			} catch (NullPointerException e) {
				System.out
						.println("performLogout(): User was logged in with email");
			}
		}
		setUserLoggedIn(null);
		//userLoggedIn = null;
		User.userList.clear();
		//setUserList(null); //reset users in map/list because distance is related to the users logged in
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

	@Override
	public void onTabSelected(Tab tab, android.app.FragmentTransaction ft) {
		if (tab.getPosition() == 0) {
			Fragment tm = new TabMapFragment();
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.fragment_tab_container, tm).addToBackStack("Map").commit();
			System.out.println("Map");
			setTitle("Kart");
		}
		if (tab.getPosition() == 1) {
			Fragment tl = new TabListFragment();
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.fragment_tab_container, tl).addToBackStack("List").commit();
			System.out.println("Liste");
			setTitle("Liste");
		}
		if (tab.getPosition() == 2) {
			Fragment ti = new TabInboxFragment();
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.fragment_tab_container, ti).addToBackStack("Chat").commit();
			System.out.println("Inbox");
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


}
