package bachelor.register;

import java.util.Properties;

import android.content.Intent;
import android.os.Bundle;
import android.service.textservice.SpellCheckerService.Session;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bachelor.hiofcommuting.R;
import com.google.android.gms.internal.em;

public class ForgotPwFragment extends Fragment {
	
	Button resetPasswordbtn;
	EditText userEmail;
	String emailString;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	        Bundle savedInstanceState) {
	        // Inflate the layout for this fragment
	        return inflater.inflate(R.layout.fragment_forgotpw, container, false);
	    }

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		resetPasswordbtn = (Button) getView().findViewById(R.id.resetPasswordbtn);
		userEmail = (EditText) getView().findViewById(R.id.forgotEmail_edittext);
		final Editable email = userEmail.getText();
		//emailString = userEmail.getText().toString();

		
		resetPasswordbtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				//TODO - Valider at det er epost, at den ikke er tom etc.
				Toast.makeText(getActivity().getApplicationContext(),"En epost er sendt til " + email +" for tilbakestilling av passord",Toast.LENGTH_LONG).show();
				sendMailToUser(email);
			}
		});
	}
	
	private void sendMailToUser(Editable email) {
		//hiofcommuting@gmail.com
		//http://www.tiemenschut.com/how-to-send-e-mail-directly-from-android-application/
	}
}
