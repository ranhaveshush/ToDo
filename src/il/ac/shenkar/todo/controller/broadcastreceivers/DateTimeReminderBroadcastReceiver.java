/**
 * 
 */
package il.ac.shenkar.todo.controller.broadcastreceivers;

import il.ac.shenkar.todo.R;
import il.ac.shenkar.todo.controller.activities.TaskEditorActivity;
import il.ac.shenkar.todo.model.contentproviders.ToDo;

import java.util.HashMap;
import java.util.Map;

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
	@SuppressLint("NewApi")
	@Override
	public void onReceive(Context context, Intent intent) {
		// Logger
		Log.d(TAG, "onReceive(Context context, Intent intent)");
		
		// Gets the task to remind info from the recived intent
		long taskToRemindId = intent.getLongExtra(ToDo.Extras.EXTRA_TASK_ID, -1);
		String taskTitle = intent.getStringExtra(ToDo.Extras.EXTRA_TASK_TITLE);
		String taskDescription = intent.getStringExtra(ToDo.Extras.EXTRA_TASK_DESCRIPTION);
		
		// Creates pending intent to invoke when the notification clicked
		Intent myIntent = new Intent(context, TaskEditorActivity.class);
		myIntent.setAction(ToDo.Actions.ACTION_EDIT_EXISTING_TASK);
		myIntent.putExtra(ToDo.Extras.EXTRA_TASK_ID, taskToRemindId);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, ((Long)taskToRemindId).intValue(), myIntent, 0);
		
		Notification notification = new Notification(R.drawable.ic_launcher, taskTitle, System.currentTimeMillis());
		notification.setLatestEventInfo(context, taskTitle, taskDescription, pendingIntent);
		notification.defaults |= Notification.DEFAULT_SOUND;
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(((Long)taskToRemindId).intValue(), notification);
	}

}
