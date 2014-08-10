package no.hiof.hiofcommuting.util;

import java.io.IOException;
import java.lang.ref.WeakReference;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.facebook.widget.ProfilePictureView;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.ImageView;

public class Util {
	
	public static void showFragment(int fragmentIndex, FragmentManager fm, Fragment[] fragments, String title, WeakReference<Activity> weakActivity) {
		FragmentTransaction transaction = fm.beginTransaction();
		for (int i = 0; i < fragments.length; i++) {
			if (i == fragmentIndex) {
				transaction.show(fragments[i]);
			} else {
				transaction.hide(fragments[i]);
			}
		}
		Activity activity = weakActivity.get();
		activity.setTitle(title);
		transaction.commit();
	}
	
	public static Bitmap rotateBitmap(String imagePath, Bitmap bitmap) {
		Bitmap rotatedBitmap = null;
		try {
			ExifInterface exif = new ExifInterface(imagePath);
			int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
			//System.out.println("EXIF" + exif +  "orientation");
			
			Matrix matrix = new Matrix();
			if (orientation == 3) {
                matrix.postRotate(180);
            }
            else if (orientation == 6) {
                matrix.postRotate(90);
            }
            else if (orientation == 8) {
                matrix.postRotate(270);
            }
			rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true); // rotating bitmap
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rotatedBitmap;
	}	
	
	public static void makeMeRequest(final Session session) {
		// Make an API call to get user data and define a
		// new callback to handle the response.
		Request request = Request.newMeRequest(session,
				new Request.GraphUserCallback() {
					@Override
					public void onCompleted(GraphUser user, Response response) {
						// If the response is successful
						if (session == Session.getActiveSession()) {
							if (user != null) {
								// Set the id for the ProfilePictureView
								// view that in turn displays the profile
								// picture.
								//profilePictureView.setProfileId(user.getId());
							}
						}
						if (response.getError() != null) {
							// Handle errors, will do so later.
						}
					}
				});
		request.executeAsync();
	}
	
	public static void uploadImageToServer() {
		//http://sunil-android.blogspot.no/2013/03/image-upload-on-server.html
	}
}
