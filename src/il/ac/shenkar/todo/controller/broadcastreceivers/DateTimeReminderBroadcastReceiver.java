/**
 * 
 */
package il.ac.shenkar.todo.controller.broadcastreceivers;

import il.ac.shenkar.todo.R;
import il.ac.shenkar.todo.config.ToDo;
import il.ac.shenkar.todo.controller.activities.TaskEditorFragmentActivity;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;



/**
 * @author ran
 *
 */
public class DateTimeReminderBroadcastReceiver extends BroadcastReceiver {
	
	/**
	 * Logger tag.
	 */
	private static final String TAG = "DateTimeReminderBroadcastRecevier";

	/* (non-Javadoc)
	 * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		// Logger
		Log.d(TAG, "onReceive(Context context, Intent intent)");
		
		// Gets the task to remind info from the recived intent
		int reminderId = intent.getIntExtra(ToDo.Extras.EXTRA_REMINDER_ID, -1);
		long taskClientId = intent.getLongExtra(ToDo.Extras.EXTRA_TASK_CLIENT_ID, -1);
		String taskTitle = intent.getStringExtra(ToDo.Extras.EXTRA_TASK_TITLE);
		String taskNotes = intent.getStringExtra(ToDo.Extras.EXTRA_TASK_NOTES);
		
		// Creates pending intent to invoke when the notification clicked
		Intent myIntent = new Intent(context, TaskEditorFragmentActivity.class);
		myIntent.setAction(ToDo.Actions.ACTION_EDIT_TASK);
		myIntent.putExtra(ToDo.Extras.EXTRA_TASK_CLIENT_ID, taskClientId);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, reminderId, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		
		/// Constructs the notification
		Notification notification = new Notification(R.drawable.ic_launcher, taskTitle, System.currentTimeMillis());
		notification.setLatestEventInfo(context, taskTitle, taskNotes, pendingIntent);
		notification.defaults |= Notification.DEFAULT_SOUND;
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(reminderId, notification);
	}

}
