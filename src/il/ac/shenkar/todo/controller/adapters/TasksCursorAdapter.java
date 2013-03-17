/**
 * 
 */
package il.ac.shenkar.todo.controller.adapters;

import il.ac.shenkar.todo.R;
import il.ac.shenkar.todo.config.ToDo;
import il.ac.shenkar.todo.model.dao.tasks.TasksDAOFactory;
import il.ac.shenkar.todo.model.dao.tasks.logic.ITasksDAO;
import il.ac.shenkar.todo.utilities.DateTimeUtils;
import il.ac.shenkar.todo.utilities.SyncUtils;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.LocationManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.google.api.client.util.DateTime;
import com.google.api.services.tasks.model.Task;
import com.mobeta.android.dslv.DragSortCursorAdapter;

/**
 * @author ran
 * 
 */
public class TasksCursorAdapter extends DragSortCursorAdapter {
	
	/**
	 * Logger's tag.
	 */
	private final String TAG = "TasksCursorAdapter";

	/**
	 * Holds the invoking context.
	 */
	private final Context context;

	/**
	 * LyoutInflater.
	 */
	private final LayoutInflater inflater;
	
	/**
	 * Holds the dao to the tasks resouce.
	 */
	private final ITasksDAO tasksDAO;
	
	/**
	 * Holds the click listener.
	 * 
	 * Notice:
	 * The OnCheckChangeListener dosen't suits here,
	 * Requery the local db and loading the list view.
	 * So I changed to OnClickListener to workaround the problem.
	 */
	private OnClickListener onClickListener = new OnClickListener() {
		@Override
		public void onClick(View view) {
			// Gets the task
			long taskClientId = (Long) view.getTag();
			Task task = tasksDAO.get(taskClientId);
			
			// If the checkbox is checked, (task completed)
			if (((CheckBox)view).isChecked()) {
				// Sets task completed datetime
				task.setStatus(ToDo.Tasks.COLUMN_STATUS_COMPLETED);
				task.setCompleted(DateTimeUtils.getDateTimeNow());
			// If the checkbox isn't checked, (task not completed)
			} else {
				// Sets task as not completed
				task.setStatus(ToDo.Tasks.COLUMN_STATUS_NEEDS_ACTION);
				task.setCompleted(null);
			}
			
			// Updates task
			task.setUpdated(DateTimeUtils.getDateTimeNow());
			tasksDAO.update(task);
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
		this.tasksDAO = TasksDAOFactory.getFactory(context, TasksDAOFactory.SQLITE).getTasksDAO();
	}
	
	/**
	 * Inner class holds the new view and the convert view widgets.
	 * Performance helper class to help fast render the list view.
	 * 
	 * @author ran
	 *
	 */
	private static class ViewHolder {
		CheckBox checkboxCompleted;
		TextView textViewTitle;
		TextView textViewDue;
	}
	
	/* (non-Javadoc)
	 * @see android.support.v4.widget.CursorAdapter#newView(android.content.Context, android.database.Cursor, android.view.ViewGroup)
	 */
	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		// Creates a new view with view holder as its tag
		View newView = inflater.inflate(R.layout.listview_item_task, parent, false);
		ViewHolder holder = new ViewHolder();
		holder.checkboxCompleted = (CheckBox) newView.findViewById(R.id.checkbox_task_completed);
		holder.checkboxCompleted.setOnClickListener(onClickListener);
		holder.textViewTitle = (TextView) newView.findViewById(R.id.text_view_task_title);
		holder.textViewDue = (TextView) newView.findViewById(R.id.text_view_task_due_date);
		newView.setTag(holder);
		
		return newView;
	}

	/* (non-Javadoc)
	 * @see android.support.v4.widget.CursorAdapter#bindView(android.view.View, android.content.Context, android.database.Cursor)
	 */
	/* (non-Javadoc)
	 * @see android.support.v4.widget.CursorAdapter#bindView(android.view.View, android.content.Context, android.database.Cursor)
	 */
	/* (non-Javadoc)
	 * @see android.support.v4.widget.CursorAdapter#bindView(android.view.View, android.content.Context, android.database.Cursor)
	 */
	@Override
	public void bindView(View convertView, Context context, Cursor cursor) {
		ViewHolder holder = (ViewHolder) convertView.getTag();

		// Gets the task data from the cursor
		long taskClientId = cursor.getLong(ToDo.Tasks.COLUMN_POSITION_CLIENT_ID);
		String taskCompleted = cursor.getString(ToDo.Tasks.COLUMN_POSITION_COMPLETED);
		String taskTitle = cursor.getString(ToDo.Tasks.COLUMN_POSITION_TITLE);
		String taskDue = cursor.getString(ToDo.Tasks.COLUMN_POSITION_DUE);
		
		// Sets the view widgets contents and functionality
		holder.checkboxCompleted.setTag(taskClientId);
		if (TextUtils.isEmpty(taskCompleted)) {
			holder.checkboxCompleted.setChecked(false);
			holder.textViewTitle.setTextAppearance(context, R.style.task_title_not_completed);
		} else {
			holder.checkboxCompleted.setChecked(true);
			holder.textViewTitle.setTextAppearance(context, R.style.task_title_completed);
			holder.textViewDue.setTextAppearance(context, R.style.task_due_completed);
		}
		holder.textViewTitle.setText(taskTitle);
		if (TextUtils.isEmpty(taskDue)) {
			holder.textViewDue.setText(null);
			holder.textViewDue.setVisibility(View.GONE);
		} else {
			DateTime taskDueDateTime = DateTime.parseRfc3339(taskDue);
			long taskDueDateTimeMillis = taskDueDateTime.getValue();
			DateTime todayDate = DateTimeUtils.getDateNow();
			long todayDateMillis = todayDate.getValue();
			// If due in future, style due text view to indicate future due
			if (todayDateMillis < taskDueDateTimeMillis) {
				holder.textViewDue.setTextAppearance(context, R.style.task_due_future);
			// If due in past, style due text view to indicate past due
			} else if (todayDateMillis > taskDueDateTimeMillis) {
				holder.textViewDue.setTextAppearance(context, R.style.task_due_past);
			// If due in present, style due text view to indicate present due
			} else {
				holder.textViewDue.setTextAppearance(context, R.style.task_due_present);
			}
			holder.textViewDue.setVisibility(View.VISIBLE);
			holder.textViewDue.setText("Due " + DateTimeUtils.getDateStrFromDateTime(taskDueDateTime));
		}
	}

	/* (non-Javadoc)
	 * @see com.mobeta.android.dslv.DragSortListView.DropListener#drop(int, int)
	 */
	@Override
	// FIXME: handle previous and parent fields
	public void drop(int from, int to) {
		// Logger
		Log.d(TAG, "drop(from: "+from+", to: "+to+")");
		
		// If draged and droped at the same place,
		// Do nothing
		if (from == to) {
			return;
		}
		
		// Don't close this cursor,
		// Because it's the adapter's cursor
		Cursor cursor = getCursor();
		Integer prevTaskClientId = null;
		Integer postTaskClientId = null;
		int prevTaskPos, postTaskPos;
		
		// Gets the task's to move
		cursor.moveToPosition(from);
		Log.d(TAG, "MOVE TASK: " + cursor.getString(ToDo.Tasks.COLUMN_POSITION_TITLE));
		int taskToMoveClientId = cursor.getInt(ToDo.Tasks.COLUMN_POSITION_CLIENT_ID);
		Task taskToMove = tasksDAO.get(taskToMoveClientId);
		
		// If drag and drop (down from origion)
		if (from < to) {
			prevTaskPos = to;
			postTaskPos = to + 1;
		// If drag and drop (up from origion)
		} else {
			prevTaskPos = to - 1;
			postTaskPos = to;
		}
		
		// If there is a previous task
		if (prevTaskPos >= 0) {
			// Gets the previous task's client id
			cursor.moveToPosition(prevTaskPos);
			Log.d(TAG, "PREV TASK: " + cursor.getString(ToDo.Tasks.COLUMN_POSITION_TITLE));
			prevTaskClientId = cursor.getInt(ToDo.Tasks.COLUMN_POSITION_CLIENT_ID);
		}		
		// If there is a post task
		if (postTaskPos < cursor.getCount()) {
			// Gets the post task's client id
			cursor.moveToPosition(postTaskPos);
			Log.d(TAG, "POST TASK: " + cursor.getString(ToDo.Tasks.COLUMN_POSITION_TITLE));
			postTaskClientId = cursor.getInt(ToDo.Tasks.COLUMN_POSITION_CLIENT_ID);
		}
		
		// Moves the task to its new position
		// FIXME: implement parent reposition
		tasksDAO.move(taskToMove, null, prevTaskClientId, postTaskClientId);
		
		// Syncs with the cloud
		SyncUtils.syncWithGoogleTasks(context.getApplicationContext());
	}

	/* (non-Javadoc)
	 * @see com.mobeta.android.dslv.DragSortListView.DragListener#drag(int, int)
	 */
	@Override
	public void drag(int from, int to) {
		// Logger
		Log.d(TAG, "drag(from: "+from+", to: "+to+")");
		
	}

	/* (non-Javadoc)
	 * @see com.mobeta.android.dslv.DragSortListView.RemoveListener#remove(int)
	 */
	@Override
	// FIXME: handle previous and parent fields
	public void remove(int which) {
		// Logger
		Log.d(TAG, "remove(which: "+which+")");
		
		// Don't close this cursor,
		// Because it's the adapter's cursor
		Cursor cursor = getCursor();
		
		// Gets the task's to remove client id
		cursor.moveToPosition(which);
		Log.d(TAG, "remove task: " + cursor.getString(ToDo.Tasks.COLUMN_POSITION_TITLE));
		int taskToRemoveClientId = cursor.getInt(ToDo.Tasks.COLUMN_POSITION_CLIENT_ID);
		
		// Cancels the datetime reminder notification, if any
		Intent intent = new Intent(ToDo.Actions.ACTION_DATETIME_REMINDER_BROADCAST);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(
				context, taskToRemoveClientId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		alarmManager.cancel(pendingIntent);
		pendingIntent.cancel();
		
		// Cancels the location reminder notification, if any
		Intent locationIntent = new Intent(ToDo.Actions.ACTION_LOCATION_REMINDER_BROADCAST);
		PendingIntent locationPendingIntent = PendingIntent.getBroadcast(
				context, taskToRemoveClientId, locationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		locationManager.removeProximityAlert(locationPendingIntent);
		locationPendingIntent.cancel();
		
		
		// Deletes the task from local db
		Task taskToRemove = tasksDAO.get(taskToRemoveClientId);
		tasksDAO.delete(taskToRemove);
		
		// Syncs with the cloud
		SyncUtils.syncWithGoogleTasks(context.getApplicationContext());
	}

}
