package bachelor;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.bachelor.hiofcommuting.R;

import bachelor.database.HandleMessages;
import bachelor.objects.User;

public class ChatService extends IntentService {
	
	private Thread checkNewMessagesThread, notifyUserThread;
	private boolean stopped = false;

	public ChatService() {
		super("ChatService");
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		checkForNewMessages(intent);
		
	}
	
	private void checkForNewMessages(final Intent intent) {
		checkNewMessagesThread = new Thread(new Runnable() {
			@Override
			public void run() {
				
				if(stopped) {
					checkNewMessagesThread.interrupt();
				}
				
				User userLoggedIn = (User)intent.getSerializableExtra("CURRENT_USER");
				boolean newMessage = HandleMessages.newMessage(userLoggedIn);
				if(newMessage)
					notifyUser();
			}
		});

		checkNewMessagesThread.start();
		try {
			checkNewMessagesThread.sleep(10000);
			checkForNewMessages(intent);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/*User userLoggedIn = (User)intent.getSerializableExtra("CURRENT_USER");
	boolean newMessage = HandleMessages.newMessage(userLoggedIn);
	if(newMessage)
		notifyUser();*/

	private void notifyUser() {
		notifyUserThread = new Thread(new Runnable() {
				@Override
				public void run() {
					// Intent invoked when user press the notification
					Intent intent = new Intent(getApplicationContext(),
							MainActivity.class);
					PendingIntent pIntent = PendingIntent.getActivity(
							getApplicationContext(), 0, intent, 0);

					// Makes a notification
					Notification n = new NotificationCompat.Builder(
							getApplicationContext())
							.setContentTitle("HiofCommuting")
							.setContentText(
									"Du har en ny melding")
							.setSmallIcon(R.drawable.hiofcommutinglogo).setContentIntent(pIntent)
							.build();

					n.flags = Notification.FLAG_AUTO_CANCEL;
					NotificationManager nManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
					nManager.notify(0, n);
				}
			});
		notifyUserThread.start();
		}
}
