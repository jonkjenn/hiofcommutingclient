package no.hiof.hiofcommuting.register;

import java.net.HttpCookie;

import no.hiof.hiofcommuting.R;
import no.hiof.hiofcommuting.objects.User;
import no.hiof.hiofommuting.database.HandleLogin;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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
		Button login = (Button) getView().findViewById(R.id.loginButton);

		(getView().findViewById(R.id.login_editText_email)).requestFocus();

		login.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// Henter brukerinput
				String email = ((EditText) getView().findViewById(
						R.id.login_editText_email)).getText().toString();
				String password = ((EditText) getView().findViewById(
						R.id.login_editText_passord)).getText().toString();
				validateUserInput(email, password);
			}
		});

		HttpCookie c = HandleLogin.getCookie(getActivity());

		if (c != null) {

			new ValidateUser().execute(new String[] { "", "" });
		} else {

			Bundle e = getActivity().getIntent().getExtras();

			if (e != null && e.containsKey("email")) {

				new ValidateUser().execute(new String[] { e.getString("email"),
						e.getString("password") });
			}
		}
	}

	public void validateUserInput(String email, String password) {
		// Forbereder toast-melding
		CharSequence toastMessage = null;

		// Sjekker om brukeren har fyllt inn data
		if (!email.isEmpty() && !password.isEmpty()) {
			// Sjekker om brukeren har prøvd å logge inn med feil passord for
			// mange ganger
			if (attempts > 0) {
				String[] userInput = { email, password };
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

	public class ValidateUser extends AsyncTask<String[], Void, Boolean> {

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
			// System.out.println("Validating");
			String[] userInput = params[0];
			String email = userInput[0];
			String password = userInput[1];

			Boolean authenticated = HandleLogin.checkUnAndPw(email, password,
					getActivity());
			if (HandleLogin.cookie != null) {
				HandleLogin.saveCookie(HandleLogin.cookie, getActivity());
				HandleLogin.cookie = null;
			}
			if (authenticated) {
				userLoggedIn = HandleLogin.getCurrentEmailUserLoggedIn(email,
						getActivity());
				return true;
			} else {
				HandleLogin.deleteCookie(getActivity());
				errorMessage = "Feil brukernavn eller passord. " + (--attempts)
						+ " forsøk igjen.";
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			// ValiderBrukerFerdig(result);
			Dialog.dismiss();
			if (result) {
				Intent intent = new Intent(getActivity(),
						no.hiof.hiofcommuting.tab.TabListenerActivity.class);
				intent.putExtra("CURRENT_USER", userLoggedIn);

                Bundle e = getActivity().getIntent().getExtras();
				if (e != null && e.containsKey("sender_id")) {
					intent.putExtra("sender_id", e.getString("sender_id"));
					intent.putExtra("sender_firstname",
							e.getString("sender_firstname"));
					intent.putExtra("sender_surname",
							e.getString("sender_surname"));
				}

				startActivity(intent);
				getActivity().finish();
			} else {
				Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT)
						.show();
			}
		}

	}
}