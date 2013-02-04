/**
 * 
 */
package il.ac.shenkar.todo.model.dao.tasks.impl.sqlite;

import il.ac.shenkar.todo.config.ToDo;
import il.ac.shenkar.todo.model.dao.tasks.logic.ITasksDAO;
import il.ac.shenkar.todo.utilities.DateTimeUtils;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.google.api.client.util.DateTime;
import com.google.api.services.tasks.model.Task;

/**
 * @author ran
 *
 */
public class TasksDAO implements ITasksDAO {

	/**
	 * Logger's tag.
	 */
	private static final String TAG = "sqlite.TasksDAO";
	
	/**
	 * Holds the content resolver for the Tasks content provider.
	 */
	private ContentResolver contentResolver = null;
	
	/**
	 * Full constructor.
	 * 
	 * @param context
	 */
	public TasksDAO(Context context) {
		super();
		
		// Logger
		Log.d(TAG, "TasksDAO(Context context)");
		
		this.contentResolver = context.getContentResolver();
	}
	
	/* (non-Javadoc)
	 * @see il.ac.shenkar.todo.model.dao.tasks.logic.ITasksDAO#insert(com.google.api.services.tasks.model.Task)
	 */
	@Override
	public Task insert(Task task) {
		// Logger
		Log.d(TAG, "insert(Task task)");
		
		ContentValues values = new ContentValues();
		values.putNull(ToDo.Tasks.COLUMN_NAME_CLIENT_ID);
		values.put(ToDo.Tasks.COLUMN_NAME_KIND, task.getKind());
		values.put(ToDo.Tasks.COLUMN_NAME_SERVER_ID, (String) task.get(ToDo.Tasks.COLUMN_NAME_SERVER_ID));
		values.put(ToDo.Tasks.COLUMN_NAME_TITLE, task.getTitle());
		if (task.getUpdated() == null) {
			values.put(ToDo.Tasks.COLUMN_NAME_UPDATED, DateTimeUtils.getDateTimeNow().toStringRfc3339());
		} else {
			values.put(ToDo.Tasks.COLUMN_NAME_UPDATED, task.getUpdated().toStringRfc3339());
		}
		values.put(ToDo.Tasks.COLUMN_NAME_SELF_LINK, task.getSelfLink());
		values.put(ToDo.Tasks.COLUMN_NAME_PARENT, task.getParent());
		values.put(ToDo.Tasks.COLUMN_NAME_POSITION, task.getPosition());
		values.put(ToDo.Tasks.COLUMN_NAME_NOTES, task.getNotes());
		values.put(ToDo.Tasks.COLUMN_NAME_STATUS, task.getStatus());
		if (task.getDue() == null) {
			values.putNull(ToDo.Tasks.COLUMN_NAME_DUE);
		} else {
			values.put(ToDo.Tasks.COLUMN_NAME_DUE, task.getDue().toStringRfc3339());
		}
		if (task.getCompleted() == null) {
			values.putNull(ToDo.Tasks.COLUMN_NAME_COMPLETED);
		} else {
			values.put(ToDo.Tasks.COLUMN_NAME_COMPLETED, task.getCompleted().toStringRfc3339());
		}
		values.put(ToDo.Tasks.COLUMN_NAME_DELETED, task.getDeleted());
		values.put(ToDo.Tasks.COLUMN_NAME_HIDDEN, task.getHidden());
		if ((String) task.get(ToDo.Tasks.COLUMN_NAME_TASKLIST_ID) == null) {
			values.put(ToDo.Tasks.COLUMN_NAME_TASKLIST_ID, "@default");
		} else {
			values.put(ToDo.Tasks.COLUMN_NAME_TASKLIST_ID, (String) task.get(ToDo.Tasks.COLUMN_NAME_TASKLIST_ID));
		}
		values.put(ToDo.Tasks.COLUMN_NAME_DATETIME_REMINDER, (String) task.get(ToDo.Tasks.COLUMN_NAME_DATETIME_REMINDER));
		values.put(ToDo.Tasks.COLUMN_NAME_LOCATION_REMINDER, (String) task.get(ToDo.Tasks.COLUMN_NAME_LOCATION_REMINDER));
		
		Uri insertedTaskUri = contentResolver.insert(ToDo.Tasks.CONTENT_URI, values);
		
		long insertedTaskClientId = ContentUris.parseId(insertedTaskUri);
		task.set(ToDo.Tasks.COLUMN_NAME_CLIENT_ID, insertedTaskClientId);
		
		return task;
	}

	/* (non-Javadoc)
	 * @see il.ac.shenkar.todo.model.dao.tasks.logic.ITasksDAO#get(long)
	 */
	@Override
	public Task get(long taskClientId) {
		// Logger
		Log.d(TAG, "get(long taskClientId)");
		
		Task task = null;
		
		String selection = ToDo.Tasks.COLUMN_NAME_CLIENT_ID + "=?";
		String[] selectionArgs = {String.valueOf(taskClientId)};
		
		Cursor cursor = contentResolver.query(
				ToDo.Tasks.CONTENT_URI,
				ToDo.Tasks.PROJECTION,
				selection, selectionArgs, null);

		if (cursor != null && cursor.moveToFirst()) {
			String taskKind = cursor.getString(ToDo.Tasks.COLUMN_POSITION_KIND);
			String taskServerId = cursor.getString(ToDo.Tasks.COLUMN_POSITION_SERVER_ID);
			String taskTitle = cursor.getString(ToDo.Tasks.COLUMN_POSITION_TITLE);
			String taskUpdated = cursor.getString(ToDo.Tasks.COLUMN_POSITION_UPDATED);
			String taskSelfLink = cursor.getString(ToDo.Tasks.COLUMN_POSITION_SELF_LINK);
			String taskParent = cursor.getString(ToDo.Tasks.COLUMN_POSITION_PARENT);
			String taskPosition = cursor.getString(ToDo.Tasks.COLUMN_POSITION_POSITION);
			String taskNotes = cursor.getString(ToDo.Tasks.COLUMN_POSITION_NOTES);
			String taskStatus = cursor.getString(ToDo.Tasks.COLUMN_POSITION_STATUS);
			String taskDue = cursor.getString(ToDo.Tasks.COLUMN_POSITION_DUE);
			String taskCompleted = cursor.getString(ToDo.Tasks.COLUMN_POSITION_COMPLETED);
			int taskDeleted = cursor.getInt(ToDo.Tasks.COLUMN_POSITION_DELETED);
			int taskHidden = cursor.getInt(ToDo.Tasks.COLUMN_POSITION_HIDDEN);
			String taskListId = cursor.getString(ToDo.Tasks.COLUMN_POSITION_TASKLIST_ID);
			String datetimeReminder = cursor.getString(ToDo.Tasks.CLOUMN_POSITION_DATETIME_REMINDER);
			String locationReminder = cursor.getString(ToDo.Tasks.CLOUMN_POSITION_LOCATION_REMINDER);
			
			cursor.close();
			
			task = new Task();
			task.set(ToDo.Tasks.COLUMN_NAME_CLIENT_ID, taskClientId);
			task.setKind(taskKind);
			task.setId(taskServerId);
			task.setTitle(taskTitle);
			if (taskUpdated != null) {
				task.setUpdated(DateTime.parseRfc3339(taskUpdated));
			}
			task.setSelfLink(taskSelfLink);
			task.setParent(taskParent);
			task.setPosition(taskPosition);
			task.setNotes(taskNotes);
			task.setStatus(taskStatus);
			if (taskDue != null) {
				task.setDue(DateTime.parseRfc3339(taskDue));
			}
			if (taskCompleted != null) {
				task.setCompleted(DateTime.parseRfc3339(taskCompleted));
			}
			task.setDeleted((taskDeleted == ToDo.Tasks.COLUMN_VALUE_FALSE) ? false : true);
			task.setHidden((taskHidden == ToDo.Tasks.COLUMN_VALUE_FALSE) ? false : true);
			task.set(ToDo.Tasks.COLUMN_NAME_TASKLIST_ID, taskListId);
			task.set(ToDo.Tasks.COLUMN_NAME_DATETIME_REMINDER, datetimeReminder);
			task.set(ToDo.Tasks.COLUMN_NAME_LOCATION_REMINDER, locationReminder);
		}
		
		return task;
	}

	/* (non-Javadoc)
	 * @see il.ac.shenkar.todo.model.dao.tasks.logic.ITasksDAO#update(com.google.api.services.tasks.model.Task)
	 */
	@Override
	public void update(Task task) {
		// Logger
		Log.d(TAG, "update(Task task)");
		
		// Gets the task client id
		long taskClientId = (Long) task.get(ToDo.Tasks.COLUMN_NAME_CLIENT_ID);
		
		ContentValues values = new ContentValues();
		values.put(ToDo.Tasks.COLUMN_NAME_KIND, task.getKind());
		values.put(ToDo.Tasks.COLUMN_NAME_SERVER_ID, task.getId());
		values.put(ToDo.Tasks.COLUMN_NAME_TITLE, task.getTitle());
		values.put(ToDo.Tasks.COLUMN_NAME_UPDATED, task.getUpdated().toStringRfc3339());
		values.put(ToDo.Tasks.COLUMN_NAME_SELF_LINK, task.getSelfLink());
		values.put(ToDo.Tasks.COLUMN_NAME_PARENT, task.getParent());
		values.put(ToDo.Tasks.COLUMN_NAME_POSITION, task.getPosition());
		values.put(ToDo.Tasks.COLUMN_NAME_NOTES, task.getNotes());
		values.put(ToDo.Tasks.COLUMN_NAME_STATUS, task.getStatus());
		if (task.getDue() == null) {
			values.putNull(ToDo.Tasks.COLUMN_NAME_DUE);
		} else {
			values.put(ToDo.Tasks.COLUMN_NAME_DUE, task.getDue().toStringRfc3339());
		}
		if (task.getCompleted() == null) {
			values.putNull(ToDo.Tasks.COLUMN_NAME_COMPLETED);
		} else {
			values.put(ToDo.Tasks.COLUMN_NAME_COMPLETED, task.getCompleted().toStringRfc3339());
		}
		values.put(ToDo.Tasks.COLUMN_NAME_DELETED, task.getDeleted());
		values.put(ToDo.Tasks.COLUMN_NAME_HIDDEN, task.getHidden());
		if ((String) task.get(ToDo.Tasks.COLUMN_NAME_TASKLIST_ID) == null) {
			values.put(ToDo.Tasks.COLUMN_NAME_TASKLIST_ID, "@default");
		} else {
			values.put(ToDo.Tasks.COLUMN_NAME_TASKLIST_ID, (String) task.get(ToDo.Tasks.COLUMN_NAME_TASKLIST_ID));
		}
		values.put(ToDo.Tasks.COLUMN_NAME_DATETIME_REMINDER, (String) task.get(ToDo.Tasks.COLUMN_NAME_DATETIME_REMINDER));
		values.put(ToDo.Tasks.COLUMN_NAME_LOCATION_REMINDER, (String) task.get(ToDo.Tasks.COLUMN_NAME_LOCATION_REMINDER));
		
		String selection = ToDo.Tasks.COLUMN_NAME_CLIENT_ID + "=?";
		String[] selectionArgs = {String.valueOf(taskClientId)};
		
		contentResolver.update(ToDo.Tasks.CONTENT_URI, values, selection, selectionArgs);
	}

	/* (non-Javadoc)
	 * @see il.ac.shenkar.todo.model.dao.tasks.logic.ITasksDAO#delete(com.google.api.services.tasks.model.Task)
	 */
	@Override
	public void delete(Task task) {
		// Logger
		Log.d(TAG, "delete(Task task)");
		
		task.setUpdated(DateTimeUtils.getDateTimeNow());
		task.setDeleted(true);
		
		update(task);
	}

	/* (non-Javadoc)
	 * @see il.ac.shenkar.todo.model.dao.tasks.logic.ITasksDAO#list(java.lang.String)
	 */
	@Override
	public Cursor list(String taskListId) {
		// Logger
		Log.d(TAG, "list(String taskListId)");
		
		String selection = ToDo.Tasks.COLUMN_NAME_TASKLIST_ID + "=? AND ("
				+ ToDo.Tasks.COLUMN_NAME_DELETED + " IS NULL OR "
				+ ToDo.Tasks.COLUMN_NAME_DELETED + "=?)";
		String[] selectionArgs = new String[] { taskListId, String.valueOf(ToDo.Tasks.COLUMN_VALUE_FALSE) };
		
		return contentResolver.query(
				ToDo.Tasks.CONTENT_URI,
				ToDo.Tasks.PROJECTION,
				selection, selectionArgs, null);
	}

}
