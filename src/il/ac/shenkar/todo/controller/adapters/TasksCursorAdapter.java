/**
 * 
 */
package il.ac.shenkar.todo.controller.adapters;

import il.ac.shenkar.todo.R;
import il.ac.shenkar.todo.config.ToDo;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * @author ran
 * 
 */
public class TasksCursorAdapter extends CursorAdapter {

	/**
	 * Context.
	 */
	private Context context = null;

	/**
	 * LyoutInflater.
	 */
	private LayoutInflater inflater = null;

	/**
	 * OnClickListener for the done button.
	 * Deletes the task.
	 */
	private OnClickListener buttonDoneOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View view) {
			// Gets the task's to delete id
			long taskToDeleteId = (Long) view.getTag();
			// Constructs the task to delete URI
			Uri taskToDeleteUri = ContentUris.withAppendedId(ToDo.Tasks.CONTENT_URI, taskToDeleteId);
			// Cancels the datetime reminder notification, if any
			Intent intent = new Intent(ToDo.Actions.ACTION_DATETIME_REMINDER_BROADCAST);
			PendingIntent pendingIntent = PendingIntent.getBroadcast(context, ((Long)taskToDeleteId).intValue(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
			AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
			alarmManager.cancel(pendingIntent);
			pendingIntent.cancel();
			// Deletes the task from the database
			context.getContentResolver().delete(taskToDeleteUri, null, null);
		}
	};

	/**
	 * Full constructor.
	 * 
	 * @param context
	 * @param cursor
	 * @param flags
	 */
	public TasksCursorAdapter(Context context, Cursor cursor, int flags) {
		super(context, cursor, flags);
		this.context = context;
		this.inflater = LayoutInflater.from(context);
	}

	/* (non-Javadoc)
	 * @see android.support.v4.widget.CursorAdapter#bindView(android.view.View, android.content.Context, android.database.Cursor)
	 */
	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		// Gets the view objects
		TextView textViewTitle = (TextView) view.findViewById(R.id.text_view_task_title);
		Button buttonDone = (Button) view.findViewById(R.id.button_done);

		// Sets the view objects contents and functionality
		// Sets the view text to present the task's title.
		String taskTitle = cursor.getString(ToDo.Tasks.COLUMN_POSITION_TITLE);
		textViewTitle.setText(taskTitle);
		// Sets the tag of the button to the task's id.
		// For later delete reference.
		buttonDone.setTag(cursor.getLong(ToDo.Tasks.COLUMN_POSITION_ID));
		buttonDone.setOnClickListener(buttonDoneOnClickListener);
	}

	/* (non-Javadoc)
	 * @see android.support.v4.widget.CursorAdapter#newView(android.content.Context, android.database.Cursor, android.view.ViewGroup)
	 */
	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		return inflater.inflate(R.layout.listview_item_task, parent, false);
	}

}
