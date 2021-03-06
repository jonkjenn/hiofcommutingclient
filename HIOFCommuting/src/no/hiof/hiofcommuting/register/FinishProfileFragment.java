package no.hiof.hiofcommuting.register;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpCookie;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import no.hiof.hiofcommuting.R;
import no.hiof.hiofcommuting.hiofcommuting.MainActivity;
import no.hiof.hiofcommuting.objects.Department;
import no.hiof.hiofcommuting.objects.Institution;
import no.hiof.hiofcommuting.objects.Study;
import no.hiof.hiofcommuting.objects.User;
import no.hiof.hiofcommuting.util.FileUploader;
import no.hiof.hiofcommuting.util.HTTPClient;
import no.hiof.hiofcommuting.util.UserInputValidator;
import no.hiof.hiofommuting.database.HandleLogin;
import no.hiof.hiofommuting.database.HandleUsers;
import no.hiof.hiofommuting.database.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;

public class FinishProfileFragment extends Fragment {

	ImageView choosenPic;
	private Spinner institutionSpinner, departmentSpinner, studySpinner,
			startingyearSpinner;
	private Button finishButton;
	boolean userHaveCar = false;
	boolean readConditions = false;
	boolean facebookUser = false;
	private boolean userInputIsValidated = false;
	ToggleButton carQstButton, readConditionsToggleButton;
	EditText addressEditText, postalCodeEditText;
	String address, postalCode, institution, campus, department, study,
			startingYear;
	ArrayList<String> finishProfileData = new ArrayList<String>();
	String fbFirstName, fbSurName, fbId;
	private List<Institution> institutionObjects = new ArrayList<Institution>();
	private List<Department> departmentObjects = new ArrayList<Department>();
	private List<Study> studyObjects = new ArrayList<Study>();
	private FinishProfileFragment fragment = this;
	ArrayList<String> registerData;
	private int nextAvailableUserId;
	private User u;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_finish_profile, container,
				false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		departmentSpinner = (Spinner) getView().findViewById(
				R.id.departmentSpinner);
		studySpinner = (Spinner) getView().findViewById(R.id.studySpinner);
		startingyearSpinner = (Spinner) getView().findViewById(
				R.id.startingyearSpinner);

//		choosenPic = (ImageView) getView()
//				.findViewById(R.id.choosenPictureView);
		readConditionsToggleButton = (ToggleButton) getView().findViewById(
				R.id.readConditionsToggleButton);
		finishButton = (Button) getView().findViewById(R.id.finishbtn);
		carQstButton = (ToggleButton) getView().findViewById(
				R.id.carqstToggleButton);
		addressEditText = (EditText) getView().findViewById(R.id.address);
		postalCodeEditText = (EditText) getView().findViewById(R.id.postal);
		addOnClickListeners();
		addDataToStartingYearSpinner();

	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		// TODO Auto-generated method stub
		super.onHiddenChanged(hidden);
		if (!hidden
				&& (institutionObjects.size() < 1
						|| departmentObjects.size() < 1 || studyObjects.size() < 1)) {
			new Read().execute();
		}
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);

		if (isVisible()) {
			if (institutionObjects.size() < 1) {
				Toast.makeText(getActivity(), "Laster data...",
						Toast.LENGTH_SHORT);
				new Read().execute();
			}
		}
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	public void makeMeRequest(final Session session) {
		// Make an API call to get user data and define a
		// new callback to handle the response.
		Request request = Request.newMeRequest(session,
				new Request.GraphUserCallback() {
					@Override
					public void onCompleted(GraphUser user, Response response) {
						// If the response is successful
						if (session == Session.getActiveSession()) {
							if (user != null) {
								fbFirstName = user.getFirstName();
								fbSurName = user.getLastName();
								fbId = user.getId();
								// System.out.println("facebook id " +
								// user.getId());
								// System.out.println("facebook username " +
								// user.getUsername());
								navigateToMap();
							}
						}
					}
				});
		request.executeAsync();
	}

	public ArrayList<String> getFinishProfileList() {
		return finishProfileData;
	}

	public void setFinishProfileList(String address, String postalCode,
			String institution, String campus, String department, String study,
			String startingYear, boolean hasCar) {
		finishProfileData.add(address);
		finishProfileData.add(postalCode);
		finishProfileData.add(institution);
		finishProfileData.add(campus);
		finishProfileData.add(department);
		finishProfileData.add(study);
		finishProfileData.add(startingYear);
		if (hasCar)
			finishProfileData.add("Ja");
		else
			finishProfileData.add("Nei");
	}

	public void navigateToMap() {
		new GetLatLong().execute();
		// User user = createUserObject();

	}

	private void afterInsert(String email, String password) {
		Intent intent = new Intent(getActivity(), EmailLoginActivity.class);
		intent.putExtra("email", email);
		intent.putExtra("password", password);
		startActivity(intent);
		// Intent intent = new Intent(getActivity(),
		// no.hiof.hiofcommuting.tab.TabListenerActivity.class);
		// intent.putExtra("CURRENT_USER", u);
		//
		// // if (facebookUser) {
		// // Session session;
		// // session = ((MainActivity) getActivity()).getFacebookSession();
		// // intent.putExtra("FACEBOOK_SESSION", session);
		// // }
		// startActivity(intent);
		getActivity().finish();
	}

	private void getLatLon() {
		new AsyncTask<Void, Void, double[]>() {

			@Override
			protected double[] doInBackground(Void... params) {
				// TODO Auto-generated method stub
				return null;
			}
		};
	}

	private class GetLatLong extends AsyncTask<Void, Void, double[]> {

		@Override
		protected double[] doInBackground(Void... params) {
			int postalCode = Integer.parseInt(finishProfileData.get(1));
			double[] latlon = HandleUsers.getLatLon(getActivity()
					.getApplicationContext(), finishProfileData.get(0),
					postalCode);
			return latlon;
		}

		@Override
		protected void onPostExecute(double[] result) {
			super.onPostExecute(result);
			insertUserToDb(result[0], result[1]);
		}
	}

	private void insertUserToDb(final double lat, final double lon) {
		final String firstName, surName;
		final String photoUrl;
		if (!facebookUser) {
			firstName = registerData.get(0);
			surName = registerData.get(1);
			photoUrl = firstName + lat + lon;
			fbId = "";
		} else {
			firstName = fbFirstName;
			surName = fbSurName;
			// TODO: FIX photoUrl
			photoUrl = "";
		}
		// System.out.println("lat1 : " + lat);
		// System.out.println("lon1 : " + lon);
		final double distance = 0.0;
		final String institution = finishProfileData.get(2);
		final String campus = finishProfileData.get(3);
		final String department = finishProfileData.get(4);
		final String study = finishProfileData.get(5);
		int studyId = 0;
		// System.out.println("studyid 1" + study);
		for (int i = 0; i < studyObjects.size(); i++) {
			if (studyObjects.get(i).getStudyName().equals(study)) {
				// System.out.println("studyid 2" +
				// studyObjects.get(i).getStudyName());
				studyId = studyObjects.get(i).getStudyId();
				// System.out.println("studyid 3" + studyId);
				break;
			}
		}
		final int fstudyId = studyId;
		final int startingYear = Integer.parseInt(finishProfileData.get(6));
		// System.out.println("starting yr : " + startingYear);
		final boolean car;
		if (finishProfileData.get(7).equals("Ja")) {
			car = true;
		} else {
			car = false;
		}
		if (facebookUser) {
			// Session session = ((MainActivity) getActivity())
			// .getFacebookSession();
			// HandleUsers.insertFacebookUserToDb(studyId, firstName, surName,
			// lat, lon, distance, institution, campus, department, study,
			// startingYear, car, fbId, getActivity(), session);
		} else {

			new AsyncTask<Void, Void, Boolean>() {
				@Override
				protected Boolean doInBackground(Void... params) {
					return HTTPClient.insertEmailUser(fstudyId, firstName,
							surName, lat, lon, distance, institution, campus,
							department, study, startingYear, car, registerData,
							FinishProfileFragment.this.getActivity());
				}

				@Override
				protected void onPostExecute(Boolean result) {
					// TODO Auto-generated method stub
					super.onPostExecute(result);
					if (result) {
						createUserObj(fstudyId, firstName, surName, lat, lon,
								distance, startingYear, car, photoUrl, "", 0);
						afterInsert(registerData.get(2), registerData.get(3));
					} else {
						Toast.makeText(getActivity(),
								"Feil ved registrering. Vennligst prøv igjen.",
								Toast.LENGTH_LONG).show();
					}
				}
			}.execute();

		}

	}

	public void createUserObj(int studyId, String firstName, String surName,
			double lat, double lon, double distance, int startingYearInt,
			boolean car, String photoUrl, String gcmId, int gcmVersion) {
		u = new User(nextAvailableUserId, studyId, firstName, surName, lat,
				lon, distance, institution, campus, department, study,
				startingYearInt, car, photoUrl, fbId, gcmId, gcmVersion);
	}

	public void addDataToStartingYearSpinner() {
		Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		List<Integer> startingYearList = new ArrayList<Integer>();
		for (int i = 0; i < 6; i++) {
			startingYearList.add(year - i);
		}
		ArrayAdapter<Integer> adapter = new ArrayAdapter<Integer>(
				getActivity(), android.R.layout.simple_spinner_item,
				startingYearList);
		startingyearSpinner.setAdapter(adapter);
	}

	public void addItemsOnSpinner() {
		if (institutionObjects != null) {
			if (getView() == null) {
				return;
			}
			institutionSpinner = (Spinner) getView().findViewById(
					R.id.institutionSpinner);
			ArrayAdapter<Institution> adapter = new ArrayAdapter<Institution>(
					getActivity(), android.R.layout.simple_spinner_item,
					institutionObjects);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			institutionSpinner.setAdapter(adapter);
			addInstitutionSpinnerListener();
			addCampusSpinnerListener();
		}
	}

	public void addOnClickListeners() {
		// CarQuestion
		carQstButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				boolean on = ((ToggleButton) view).isChecked();

				if (on) {
					userHaveCar = true;
				} else {
					userHaveCar = false;
				}
			}
		});
		// ReadConditions
		readConditionsToggleButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				boolean on = ((ToggleButton) view).isChecked();

				if (on) {
					readConditions = true;
				} else {
					readConditions = false;
				}
			}
		});
		// FinishButton
		// TODO: Noen verdier fra db i spinner er ugyldig
		finishButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				address = addressEditText.getText().toString().trim();
				postalCode = postalCodeEditText.getText().toString().trim();
				institution = String.valueOf(institutionSpinner
						.getSelectedItem());
				department = String.valueOf(departmentSpinner.getSelectedItem());
				study = String.valueOf(studySpinner.getSelectedItem());
				startingYear = String.valueOf(startingyearSpinner
						.getSelectedItem());
				String activity = getActivity().toString();

				// DEBUG REMOVE FROM APPP!!!!!!!!!!!!!!11
//				address = "nystredet 8";
//				postalCode = "1772";
//				institution = "1";
//				department = "2";
//				study = "Bachelorstudium i ingeniørfag - data";
//				startingYear = "2014";
				// DEBUG REMOVE FROM APPP!!!!!!!!!!!!!!11

				// if (activity
				// .startsWith("no.hiof.hiofcommuting.hiofcommuting.MainActivity"))
				// {
				// // facebookUser = true;
				// // Session session;
				// // session = ((MainActivity) getActivity())
				// // .getFacebookSession();
				// // makeMeRequest(session);
				// }
				if (activity
						.startsWith("no.hiof.hiofcommuting.register.EmailLoginActivity")) {
					registerData = ((EmailLoginActivity) getActivity())
							.getRegistrationList();
				}

				// Validating user input
				UserInputValidator validator = new UserInputValidator();

				if (validator
						.isAddressValid(fragment, address, addressEditText)
						&& validator.isPostalCodeValid(fragment, postalCode,
								postalCodeEditText)
						&& validator.isConditionsRead(fragment, readConditions,
								readConditionsToggleButton)) {

					setFinishProfileList(address, postalCode, institution,
							campus, department, study, startingYear,
							userHaveCar);

					if (!facebookUser) {
						userInputIsValidated = true;
						// System.out.println("Ikke facebook user");
						// Uploading picture
//						Thread t = new Thread(new Runnable() {
//							@Override
//							public void run() {
//								FileUploader
//										.upload(saveScaledImage(RegisterFragment
//												.getBitmap()));
//
//								if (FileUploader.serverResponseCode != 200) {
//									getActivity().runOnUiThread(new Runnable() {
//										@Override
//										public void run() {
//											Toast.makeText(getActivity(),
//													"Noe gikk galt",
//													Toast.LENGTH_SHORT);
//										}
//									});
//									Intent restart = new Intent(getActivity(),
//											MainActivity.class);
//									startActivity(restart);
//								}
//							}
//						});
//						t.start();
					}
				}

				if (!facebookUser && userInputIsValidated)
					navigateToMap();
				else {
					Thread t = new Thread(new Runnable() {
						@Override
						public void run() {
							getActivity().runOnUiThread(new Runnable() {
								@Override
								public void run() {
									Toast.makeText(getActivity(),
											"Vennligst fullfør registrering",
											Toast.LENGTH_SHORT).show();
								}
							});
						}
					});
					t.start();
				}
			}
		});
	}

	// TODO: Insert facebook user_id
	private String saveScaledImage(Bitmap bitmap) {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 40, bytes);

		double[] latlon = HandleUsers.getLatLon(getActivity()
				.getApplicationContext(), finishProfileData.get(0), 0);

		File f = new File(Environment.getExternalStorageDirectory()
				+ File.separator + registerData.get(0) + latlon[0] + latlon[1]
				+ ".jpg");

		try {
			f.createNewFile();
			FileOutputStream fo = new FileOutputStream(f);
			fo.write(bytes.toByteArray());
			fo.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return f.getPath();
	}

	/*
	 * private int getUserID() { double[] latlon =
	 * HandleUsers.getLatLon(getActivity().getApplicationContext(),
	 * finishProfileData.get(0), 0);
	 * 
	 * String url = MainActivity.SERVER_URL + "/usrid.py?fname=" +
	 * registerData.get(0) + "&lat=" + latlon[0] + "&lon=" + latlon[1];
	 * 
	 * int id = 0; try { JSONArray jsonArray = new
	 * JsonParser().getJsonArray(url); JSONObject obj =
	 * jsonArray.getJSONObject(0); id = obj.getInt("user_id");
	 * 
	 * }catch (NullPointerException e){ e.printStackTrace(); }catch
	 * (JSONException e) { e.printStackTrace(); }
	 * 
	 * return id; }
	 */

	public void addInstitutionSpinnerListener() {
		institutionSpinner
				.setOnItemSelectedListener(new OnItemSelectedListener() {
					@Override
					public void onItemSelected(AdapterView<?> parentView,
							View selectedItemView, int position, long id) {

						institution = String.valueOf(institutionSpinner
								.getSelectedItem());
						int currentInstitutionId = 0;
						List<Department> currentList = new ArrayList<Department>();
						ArrayAdapter<Department> adapter = new ArrayAdapter<Department>(
								parentView.getContext(),
								android.R.layout.simple_spinner_item,
								currentList);
						adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

						for (int i = 0; i < institutionObjects.size(); i++) {
							if (institutionObjects.get(i).getInstitutionName() == institution) {
								currentInstitutionId = institutionObjects
										.get(i).getInstitutionId();
							}
						}

						for (int i = 0; i < departmentObjects.size(); i++) {
							if (departmentObjects.get(i).getInstitutionId() == currentInstitutionId) {
								currentList.add(departmentObjects.get(i));
							}
						}

						departmentSpinner.setAdapter(adapter);
					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
						// TODO Auto-generated method stub
					}
				});
	}

	public void addCampusSpinnerListener() {
		departmentSpinner
				.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> parentView,
							View selectedItemView, int position, long id) {

						campus = String.valueOf(departmentSpinner
								.getSelectedItem());
						int currentDepartmentId = 0;
						List<Study> currentList = new ArrayList<Study>();
						ArrayAdapter<Study> adapter = new ArrayAdapter<Study>(
								parentView.getContext(),
								android.R.layout.simple_spinner_item,
								currentList);
						adapter.setDropDownViewResource(R.layout.multiline_spinner_dropdown_item);

						for (int i = 0; i < departmentObjects.size(); i++) {
							if (departmentObjects.get(i).getDepartmentName() == campus) {
								currentDepartmentId = departmentObjects.get(i)
										.getDepartmentId();
							}
						}

						for (int i = 0; i < studyObjects.size(); i++) {
							if (studyObjects.get(i).getDepartmentId() == currentDepartmentId) {
								currentList.add(studyObjects.get(i));
							}
						}

						studySpinner.setAdapter(adapter);
					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
						// TODO Auto-generated method stub
					}
				});
	}

	public class Read extends AsyncTask<Void, Integer, Boolean> {

		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO Auto-generated method stub
			try {
				// System.out.println("Getting institutions from server");
				JsonParser jp = new JsonParser();
				JSONArray jsonInstArr, jsonDepartmentArr, jsonStudArr, jsonUserArr;
				// System.out.println("Got array");
				// Cookie c = HandleLogin.getCookie(getActivity());
				// if(c == null){return false;}

				HttpCookie c = new HttpCookie("nothing", "");
				jsonInstArr = jp.getJsonArray(MainActivity.SERVER_URL
						+ "/institution.py", c);
				for (int i = 0; i < jsonInstArr.length(); i++) {
					institutionObjects.add(new Institution(jsonInstArr
							.getJSONObject(i)));
				}

				// System.out.println("Getting departments from server");
				jsonDepartmentArr = jp.getJsonArray(MainActivity.SERVER_URL
						+ "/department.py",
						HandleLogin.getCookie(getActivity()));
				// System.out.println("Got array");
				for (int i = 0; i < jsonDepartmentArr.length(); i++) {
					departmentObjects.add(new Department(jsonDepartmentArr
							.getJSONObject(i)));
				}

				// System.out.println("Getting studies from server");
				jsonStudArr = jp.getJsonArray(MainActivity.SERVER_URL
						+ "/study.py?q=getAllStudies",
						HandleLogin.getCookie(getActivity()));
				// System.out.println("Got array");
				for (int i = 0; i < jsonStudArr.length(); i++) {
					studyObjects.add(new Study(jsonStudArr.getJSONObject(i)));
				}

				// //System.out.println("Getting users from server");
				// jsonUserArr = jp.getJsonArray("http://"
				// + MainActivity.SERVER_URL + "/usr.py?q=usr",
				// HandleLogin.getCookie(getActivity()));
				// //System.out.println("Got array");
				// JSONObject lastUserObj =
				// jsonUserArr.getJSONObject(jsonUserArr
				// .length() - 1);
				// nextAvailableUserId = lastUserObj.getInt("user_id");
				// nextAvailableUserId++;
				// //System.out.println("Next : " + nextAvailableUserId);
				return true;
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (result) {
				// TODO kjør første spinner herifra
				addItemsOnSpinner();
			}
		}
	}
}
