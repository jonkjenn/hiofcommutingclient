package no.hiof.hiofcommuting.register;

import no.hiof.hiofcommuting.R;
import no.hiof.hiofcommuting.objects.User;
import no.hiof.hiofommuting.database.HandleLogin;

import org.apache.http.cookie.Cookie;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginFragment extends Fragment {
	TextView response;
	protected int attempts = 5;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_login, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Button login = (Button)getView().findViewById(R.id.loginButton);

        (getView().findViewById(R.id.login_editText_email)).requestFocus();

		login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	// Henter brukerinput
        		String email = ((EditText) getView().findViewById(R.id.login_editText_email))
        						.getText().toString();
        		String password = ((EditText) getView().findViewById(R.id.login_editText_passord))
        						.getText().toString();
                validateUserInput(email, password);
            }
        });
		
		
        new ValidateUser().execute(new String[]{"",""});
	}

	public void validateUserInput(String email, String password) {
		//Forbereder toast-melding
		CharSequence toastMessage = null;

		// Sjekker om brukeren har fyllt inn data
		if (!email.isEmpty() && !password.isEmpty()) {
			// Sjekker om brukeren har prøvd å logge inn med feil passord for
			// mange ganger
			if (attempts > 0) {
				String[] userInput = {email, password};
				new ValidateUser().execute(userInput);
			} else {
				toastMessage = "Glemt passord?";
			}
		} else {
			toastMessage = "Fyll inn brukernavn og passord";
		}

		// Skriver ut toast om noe gikk galt
		if (toastMessage != null) {
			Context context = getActivity().getApplicationContext();
			int duration = Toast.LENGTH_SHORT;
			Toast.makeText(context, toastMessage, duration).show();
		}
	}
	
	/**
	 * 
	 * @author Martin Validerer brukerinput opp mot database. Kjøres i AsyncTask
	 *         da dette er en tyngre oppgave.
	 */

	
	public class ValidateUser extends
			AsyncTask<String[], Void, Boolean> {

		private ProgressDialog Dialog = new ProgressDialog(getActivity());
		private String errorMessage;
		private User userLoggedIn;

		@Override
		protected void onPreExecute() {
			Dialog.setMessage("Logger inn...");
			Dialog.show();
		}

		@Override
		protected Boolean doInBackground(String[]... params) {
			System.out.println("Validating");
			String[] userInput = params[0];
			String email = userInput[0];
			String password = userInput[1];

			Boolean authenticated = HandleLogin.checkUnAndPw(email,password, getActivity());
			if(HandleLogin.cookie != null){
				saveCookie(HandleLogin.cookie);
				HandleLogin.cookie = null;
			}
			if (authenticated) {
				userLoggedIn = HandleLogin.getCurrentEmailUserLoggedIn(email, getActivity());
				return true;
			} else {
				HandleLogin.deleteCookie(getActivity());
				errorMessage = "Feil brukernavn eller passord. " + (--attempts) + " forsøk igjen.";
			}
			return false;
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			//ValiderBrukerFerdig(result);
			Dialog.dismiss();
			if(result){
				Intent intent = new Intent(getActivity(), no.hiof.hiofcommuting.tab.TabListenerActivity.class);
				intent.putExtra("CURRENT_USER", userLoggedIn);
				startActivity(intent);
				getActivity().finish();
			}else {
				Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
			}
		}
		
		private void saveCookie(Cookie c)
		{
			SharedPreferences prefs = getActivity().getSharedPreferences("hccook", Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = prefs.edit();
			editor.putString("name", c.getName());			
			editor.putString("value", c.getValue());
			editor.putString("domain", c.getDomain());
			editor.putString("expiry", c.getExpiryDate()==null?"":c.getExpiryDate().toString());
			editor.putString("path", c.getPath());
			editor.putInt("version", c.getVersion());
			editor.apply();
		}
	}
}