package no.hiof.hiofcommuting.hiofcommuting;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.gcm.GoogleCloudMessaging;

public class GcmBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

		NotificationManager manager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);

		Bundle extras = intent.getExtras();
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);

		String messageType = gcm.getMessageType(intent);

		if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
			
        Intent i = new Intent(context,
							no.hiof.hiofcommuting.hiofcommuting.MainActivity.class);
        
        String sender_id = extras.getString("sender_id");
        String sender_firstname = extras.getString("sender_firstname");
        String sender_surname = extras.getString("sender_surname");
        
        i.putExtra("sender_id", sender_id);
        i.putExtra("sender_firstname", sender_firstname);
        i.putExtra("sender_surname", sender_surname);

        PendingIntent pi = PendingIntent.getActivity(context, 0, i, 0);
			
			NotificationCompat.Builder nb = new NotificationCompat.Builder(
					context)
					.setSmallIcon(
							no.hiof.hiofcommuting.R.drawable.campuskjoring_72)
					.setContentTitle(
							"Melding fra " + sender_firstname + " " + sender_surname)
					.setContentText(extras.getString("message"))
					.setContentIntent(pi)
					.setAutoCancel(true);
			manager.notify(Integer.parseInt(sender_id),nb.build());
		}
		
	setResultCode(Activity.RESULT_OK);
	}
}
