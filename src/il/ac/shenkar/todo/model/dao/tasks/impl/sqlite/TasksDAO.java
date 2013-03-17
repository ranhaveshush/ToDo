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
	 * Holds the minimum long value as a string.
	 */
	private static String MIN_LONG = String.valueOf(0L);
	
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
	 * @see il.ac.shenkar.todo.model.dao.tasks.logic.ITasksDAO#list()
	 */
	@Override
	public Cursor listAll() {
		// Logger
		Log.d(TAG, "list()");
		
		return contentResolver.query(
				ToDo.Tasks.CONTENT_URI,
				ToDo.Tasks.PROJECTION,
				null, null, null);
	}
	
	/* (non-Javadoc)
	 * @see il.ac.shenkar.todo.model.dao.tasks.logic.ITasksDAO#list(long)
	 */
	@Override
	public Cursor list(long taskListClientId) {
		// Logger
		Log.d(TAG, "list(long taskListClientId)");
		
		String selection = ToDo.Tasks.COLUMN_NAME_TASK_LIST_CLIENT_ID + "=? AND "
				+ ToDo.Tasks.COLUMN_NAME_DELETED + "=?";
		String[] selectionArgs = new String[] {
				String.valueOf(taskListClientId),
				String.valueOf(ToDo.Tasks.COLUMN_VALUE_FALSE) };
		
		return contentResolver.query(
				ToDo.Tasks.CONTENT_URI,
				ToDo.Tasks.PROJECTION,
				selection, selectionArgs, null);
	}
	
	/* (non-Javadoc)
	 * @see il.ac.shenkar.todo.model.dao.tasks.logic
	 * .ITasksDAO#insert(com.google.api.services.tasks.model.Task)
	 */
	@Override
	public Task insert(Task task) {
		// Logger
		Log.d(TAG, "insert(Task task)");
		
		// Gets the task list client id
		long taskListClientId = (Long) task.get(ToDo.Tasks.COLUMN_NAME_TASK_LIST_CLIENT_ID);
		
		ContentValues values = new ContentValues();
		values.putNull(ToDo.Tasks.COLUMN_NAME_CLIENT_ID);
		values.put(ToDo.Tasks.COLUMN_NAME_SERVER_ID, (String) task.get(ToDo.Tasks.COLUMN_NAME_SERVER_ID));
		values.put(ToDo.Tasks.COLUMN_NAME_KIND, task.getKind());
		values.put(ToDo.Tasks.COLUMN_NAME_TITLE, task.getTitle());
		DateTime taskUpdated = task.getUpdated();
		if (taskUpdated == null) {
			values.put(ToDo.Tasks.COLUMN_NAME_UPDATED, DateTimeUtils.getDateTimeNow().toStringRfc3339());
		} else {
			values.put(ToDo.Tasks.COLUMN_NAME_UPDATED, taskUpdated.toStringRfc3339());
		}
		values.put(ToDo.Tasks.COLUMN_NAME_SELF_LINK, task.getSelfLink());
		values.put(ToDo.Tasks.COLUMN_NAME_PARENT, task.getParent());
		values.put(ToDo.Tasks.COLUMN_NAME_PREVIOUS, (String) task.get(ToDo.Tasks.COLUMN_NAME_PREVIOUS));
		if (task.getPosition() == null) {
			task.setPosition(getNewPosition(taskListClientId));
		}
		values.put(ToDo.Tasks.COLUMN_NAME_POSITION, task.getPosition());
		values.put(ToDo.Tasks.COLUMN_NAME_NOTES, task.getNotes());
		values.put(ToDo.Tasks.COLUMN_NAME_STATUS, task.getStatus());
		DateTime taskDue = task.getDue();
		if (taskDue == null) {
			values.putNull(ToDo.Tasks.COLUMN_NAME_DUE);
		} else {
			values.put(ToDo.Tasks.COLUMN_NAME_DUE, taskDue.toStringRfc3339());
		}
		DateTime taskCompleted = task.getCompleted();
		if (taskCompleted == null) {
			values.putNull(ToDo.Tasks.COLUMN_NAME_COMPLETED);
		} else {
			values.put(ToDo.Tasks.COLUMN_NAME_COMPLETED, taskCompleted.toStringRfc3339());
		}
		Integer taskMoved = (Integer) task.get(ToDo.Tasks.COLUMN_NAME_MOVED);
		if (taskMoved != null && taskMoved == ToDo.Tasks.COLUMN_VALUE_TRUE) {		
			values.put(ToDo.Tasks.COLUMN_NAME_MOVED, ToDo.Tasks.COLUMN_VALUE_TRUE);
		} else {
			values.put(ToDo.Tasks.COLUMN_NAME_MOVED, ToDo.Tasks.COLUMN_VALUE_FALSE);
		}
		Boolean taskDeleted = task.getDeleted();
		if (taskDeleted != null) {
			values.put(ToDo.Tasks.COLUMN_NAME_DELETED, taskDeleted);
		} else {
			values.put(ToDo.Tasks.COLUMN_NAME_DELETED, false);
		}
		values.put(ToDo.Tasks.COLUMN_NAME_HIDDEN, task.getHidden());
		values.put(ToDo.Tasks.COLUMN_NAME_TASK_LIST_CLIENT_ID, taskListClientId);
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
		
		String selection = ToDo.Tasks.COLUMN_NAME_CLIENT_ID + "=?";
		String[] selectionArgs = {String.valueOf(taskClientId)};
		
		Cursor cursor = contentResolver.query(
				ToDo.Tasks.CONTENT_URI,
				ToDo.Tasks.PROJECTION,
				selection, selectionArgs, null);
		
		Task task = null;
		if (cursor != null && cursor.moveToFirst()) {
			task = get(cursor);
			cursor.close();
		}
		
		return task;
	}

	/* (non-Javadoc)
	 * @see il.ac.shenkar.todo.model.dao.tasks.logic.ITasksDAO#get(android.database.Cursor)
	 */
	@Override
	public Task get(Cursor cursor) {
		long taskClientId = cursor.getLong(ToDo.Tasks.COLUMN_POSITION_CLIENT_ID);
		String taskServerId = cursor.getString(ToDo.Tasks.COLUMN_POSITION_SERVER_ID);
		String taskKind = cursor.getString(ToDo.Tasks.COLUMN_POSITION_KIND);
		String taskTitle = cursor.getString(ToDo.Tasks.COLUMN_POSITION_TITLE);
		String taskUpdated = cursor.getString(ToDo.Tasks.COLUMN_POSITION_UPDATED);
		String taskSelfLink = cursor.getString(ToDo.Tasks.COLUMN_POSITION_SELF_LINK);
		String taskParent = cursor.getString(ToDo.Tasks.COLUMN_POSITION_PARENT);
		String taskPrevious = cursor.getString(ToDo.Tasks.COLUMN_POSITION_PREVIOUS);
		String taskPosition = cursor.getString(ToDo.Tasks.COLUMN_POSITION_POSITION);
		String taskNotes = cursor.getString(ToDo.Tasks.COLUMN_POSITION_NOTES);
		String taskStatus = cursor.getString(ToDo.Tasks.COLUMN_POSITION_STATUS);
		String taskDue = cursor.getString(ToDo.Tasks.COLUMN_POSITION_DUE);
		String taskCompleted = cursor.getString(ToDo.Tasks.COLUMN_POSITION_COMPLETED);
		int taskMoved = cursor.getInt(ToDo.Tasks.COLUMN_POSITION_MOVED);
		int taskDeleted = cursor.getInt(ToDo.Tasks.COLUMN_POSITION_DELETED);
		int taskHidden = cursor.getInt(ToDo.Tasks.COLUMN_POSITION_HIDDEN);
		long taskListClientId = cursor.getLong(ToDo.Tasks.COLUMN_POSITION_TASK_LIST_CLIENT_ID);
		String taskDateTimeReminder = cursor.getString(ToDo.Tasks.COLUMN_POSITION_DATETIME_REMINDER);
		String taskLocationReminder = cursor.getString(ToDo.Tasks.COLUMN_POSITION_LOCATION_REMINDER);
		
		Task task  = new Task();
		task.set(ToDo.Tasks.COLUMN_NAME_CLIENT_ID, taskClientId);
		task.setId(taskServerId);
		task.setKind(taskKind);
		task.setTitle(taskTitle);
		if (taskUpdated != null) {
			task.setUpdated(DateTime.parseRfc3339(taskUpdated));
		}
		task.setSelfLink(taskSelfLink);
		task.setParent(taskParent);
		task.set(ToDo.Tasks.COLUMN_NAME_PREVIOUS, taskPrevious);
		task.setPosition(taskPosition);
		task.setNotes(taskNotes);
		task.setStatus(taskStatus);
		if (taskDue != null) {
			task.setDue(DateTime.parseRfc3339(taskDue));
		}
		if (taskCompleted != null) {
			task.setCompleted(DateTime.parseRfc3339(taskCompleted));
		}
		task.set(ToDo.Tasks.COLUMN_NAME_MOVED, taskMoved);
		task.setDeleted((taskDeleted == ToDo.Tasks.COLUMN_VALUE_FALSE) ? false : true);
		task.setHidden((taskHidden == ToDo.Tasks.COLUMN_VALUE_FALSE) ? false : true);
		task.set(ToDo.Tasks.COLUMN_NAME_TASK_LIST_CLIENT_ID, taskListClientId);
		task.set(ToDo.Tasks.COLUMN_NAME_DATETIME_REMINDER, taskDateTimeReminder);
		task.set(ToDo.Tasks.COLUMN_NAME_LOCATION_REMINDER, taskLocationReminder);
		
		return task;
	}

	/* (non-Javadoc)
	 * @see il.ac.shenkar.todo.model.dao.tasks.logic
	 * .ITasksDAO#update(com.google.api.services.tasks.model.Task)
	 */
	@Override
	public void update(Task task) {
		// Logger
		Log.d(TAG, "update(Task task)");
		
		// Gets the task client id
		long taskClientId = (Long) task.get(ToDo.Tasks.COLUMN_NAME_CLIENT_ID);
		
		ContentValues values = new ContentValues();
		values.put(ToDo.Tasks.COLUMN_NAME_SERVER_ID, task.getId());
		values.put(ToDo.Tasks.COLUMN_NAME_KIND, task.getKind());
		values.put(ToDo.Tasks.COLUMN_NAME_TITLE, task.getTitle());
		values.put(ToDo.Tasks.COLUMN_NAME_UPDATED, task.getUpdated().toStringRfc3339());
		values.put(ToDo.Tasks.COLUMN_NAME_SELF_LINK, task.getSelfLink());
		values.put(ToDo.Tasks.COLUMN_NAME_PARENT, task.getParent());
		values.put(ToDo.Tasks.COLUMN_NAME_PREVIOUS, (String) task.get(ToDo.Tasks.COLUMN_NAME_PREVIOUS));
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
		Integer taskMoved = (Integer) task.get(ToDo.Tasks.COLUMN_NAME_MOVED);
		if (taskMoved != null && taskMoved == ToDo.Tasks.COLUMN_VALUE_TRUE) {
			values.put(ToDo.Tasks.COLUMN_NAME_MOVED, ToDo.Tasks.COLUMN_VALUE_TRUE);
		} else {
			values.put(ToDo.Tasks.COLUMN_NAME_MOVED, ToDo.Tasks.COLUMN_VALUE_FALSE);
		}
		Boolean taskDeleted = task.getDeleted();
		if (taskDeleted != null) {
			values.put(ToDo.Tasks.COLUMN_NAME_DELETED, taskDeleted);
		} else {
			values.put(ToDo.Tasks.COLUMN_NAME_DELETED, false);
		}
		values.put(ToDo.Tasks.COLUMN_NAME_HIDDEN, task.getHidden());
		values.put(ToDo.Tasks.COLUMN_NAME_TASK_LIST_CLIENT_ID, (Long) task.get(ToDo.Tasks.COLUMN_NAME_TASK_LIST_CLIENT_ID));
		values.put(ToDo.Tasks.COLUMN_NAME_DATETIME_REMINDER, (String) task.get(ToDo.Tasks.COLUMN_NAME_DATETIME_REMINDER));
		values.put(ToDo.Tasks.COLUMN_NAME_LOCATION_REMINDER, (String) task.get(ToDo.Tasks.COLUMN_NAME_LOCATION_REMINDER));
		
		String selection = ToDo.Tasks.COLUMN_NAME_CLIENT_ID + "=?";
		String[] selectionArgs = {String.valueOf(taskClientId)};
		
		contentResolver.update(ToDo.Tasks.CONTENT_URI, values, selection, selectionArgs);
	}

	/* (non-Javadoc)
	 * @see il.ac.shenkar.todo.model.dao.tasks.logic
	 * .ITasksDAO#delete(com.google.api.services.tasks.model.Task)
	 */
	@Override
	public void delete(Task task) {
		// Logger
		Log.d(TAG, "delete(Task task)");
		
		task.setDeleted(true);
		
		task.setUpdated(DateTimeUtils.getDateTimeNow());
		update(task);
	}
	
	/* (non-Javadoc)
	 * @see il.ac.shenkar.todo.model.dao.tasks.logic.ITasksDAO#delete(long)
	 */
	@Override
	public void delete(long taskListClientId) {
		// Logger
		Log.d(TAG, "delete(long taskListClientId)");
		
		String selection = ToDo.Tasks.COLUMN_NAME_TASK_LIST_CLIENT_ID + "=?";
		String[] selectionArgs = { String.valueOf(taskListClientId) };
		
		contentResolver.delete(ToDo.Tasks.CONTENT_URI, selection, selectionArgs);
	}
	
	/* (non-Javadoc)
	 * @see il.ac.shenkar.todo.model.dao.tasks.logic
	 * .ITasksDAO#move(com.google.api.services.tasks.model.Task, java.lang.Integer, java.lang.Integer)
	 */
	@Override
	public void move(Task task, Integer parent, Integer previous, Integer post) {
		// Logger
		Log.d(TAG, "move(Task task, Integer parent, Integer previous)");
		
		task.set(ToDo.Tasks.COLUMN_NAME_MOVED, ToDo.Tasks.COLUMN_VALUE_TRUE);
		task.setParent(String.valueOf(parent));
		task.set(ToDo.Tasks.COLUMN_NAME_PREVIOUS, String.valueOf(previous));
		
		String prevPosStr = null;
		String postPosStr = null;
		String newPosStr = null;
		long prevPos, postPos, newPos;
		
		// If moved to the middle of the list
		if (previous != null && post != null) {
			prevPosStr = get(previous).getPosition();
			postPosStr = get(post).getPosition();
			prevPos = Long.parseLong(prevPosStr);
			postPos = Long.parseLong(postPosStr);
			newPos = prevPos + (postPos - prevPos) / 2;
			newPosStr = String.valueOf(newPos);
		// If moved to the end of the list
		} else if (previous != null) {
			newPosStr = getNewPosition((Long) task.get(ToDo.Tasks.COLUMN_NAME_TASK_LIST_CLIENT_ID));
		// If moved to the begining of the list
		} else if (post != null) {
			postPosStr = get(post).getPosition();
			postPos = Long.parseLong(postPosStr);
			newPos = postPos / 2;
			newPosStr = String.valueOf(newPos);
		}
		
		task.setPosition(newPosStr);
		
		task.setUpdated(DateTimeUtils.getDateTimeNow());
		update(task);
	}
	
	/**
	 * Returns the new position at the end of the list,
	 * To append the new task to the list in this position.
	 * 
	 * @param taskListClientId	The task list client identifier
	 * @return					The new position in the given list
	 */
	private String getNewPosition(long taskListClientId) {
		// Logger
		Log.d(TAG, "getNewPosition(long taskListClientId)");
		
		String lastPosStr = MIN_LONG;
		String currPosStr;
		
		Cursor cursor = list(taskListClientId);
		while (cursor.moveToNext()) {
			currPosStr = cursor.getString(ToDo.Tasks.COLUMN_POSITION_POSITION);
			if (lastPosStr.compareTo(currPosStr) < 0) {
				lastPosStr = currPosStr;
			}
		}
		
		long lastPos = Long.parseLong(lastPosStr);
		long newPos = lastPos + (Long.MAX_VALUE - lastPos) / 2;
		
		Log.d(TAG, "NEW POS " + String.valueOf(newPos));
		
		return String.valueOf(newPos);
	}

}
