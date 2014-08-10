package no.hiof.hiofcommuting.chat;

import no.hiof.hiofcommuting.R;
import no.hiof.hiofcommuting.objects.User;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

public class ChatActivity extends FragmentActivity {

	private User userLoggedIn;
	private User userToChatWith;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);

		try{
			setUserLoggedIn((User) getIntent().getSerializableExtra("CURRENT_USER"));
			setUserToChatWith((User) getIntent().getSerializableExtra("SELECTED_USER"));
			//System.out.println("Du heter "+userLoggedIn.getFirstName());
			//System.out.println("Du chatter med "+userToChatWith.getFirstName());
			setTitle(userToChatWith.getFirstName()+" " + userToChatWith.getSurname());
		}catch(NullPointerException e){
			
		}

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction().add(R.id.container, new ChatFragment()).commit();
			FragmentManager fm = getSupportFragmentManager();
			FragmentTransaction transaction = fm.beginTransaction();
			ChatFragment pf = new ChatFragment();
			transaction.add(R.id.container, pf);
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.chat, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public User getUserLoggedIn() {
		return userLoggedIn;
	}

	public void setUserLoggedIn(User userLoggedIn) {
		this.userLoggedIn = userLoggedIn;
	}

	public User getUserToChatWith() {
		return userToChatWith;
	}

	public void setUserToChatWith(User userToChatWith) {
		this.userToChatWith = userToChatWith;
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
			View rootView = inflater.inflate(R.layout.fragment_chat, container,
					false);
			return rootView;
		}
	}
}
