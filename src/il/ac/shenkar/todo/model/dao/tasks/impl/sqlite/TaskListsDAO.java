/**
 * 
 */
package il.ac.shenkar.todo.model.dao.tasks.impl.sqlite;

import il.ac.shenkar.todo.config.ToDo;
import il.ac.shenkar.todo.model.dao.tasks.TasksDAOFactory;
import il.ac.shenkar.todo.model.dao.tasks.logic.ITaskListsDAO;
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
import com.google.api.services.tasks.model.TaskList;

/**
 * @author ran
 *
 */
public class TaskListsDAO implements ITaskListsDAO {
	
	/**
	 * Logger's tag.
	 */
	private static final String TAG = "sqlite.TaskListsDAO";
	
	/**
	 * Holds the content resolver for the Tasks content provider.
	 */
	private ContentResolver contentResolver = null;
	
	/**
	 * Holds the task list dao.
	 */
	private ITasksDAO tasksDAO = null;
	
	/**
	 * Full constructor.
	 * 
	 * @param context
	 */
	public TaskListsDAO(Context context) {
		super();
		
		// Logger
		Log.d(TAG, "TaskListsDAO(Context context)");
		
		this.contentResolver = context.getContentResolver();
		tasksDAO = TasksDAOFactory.getFactory(context, TasksDAOFactory.SQLITE).getTasksDAO();
	}
	
	/* (non-Javadoc)
	 * @see il.ac.shenkar.todo.model.dao.tasks.logic.ITaskListsDAO#list(il.ac.shenkar.todo.model.dao.tasks.logic.ITaskListsDAO.ListMode)
	 */
	@Override
	public Cursor list(ListMode listMode) {
		// Logger
		Log.d(TAG, "list(ListMode listMode)");
		
		String selection = null;
		String[] selectionArgs = null;
		
		switch (listMode) {
		case MODE_ALL:
			break;
		case MODE_NOT_DELETED:
			selection = ToDo.TaskLists.COLUMN_NAME_DELETED + "=?";
			selectionArgs = new String[] { String.valueOf(ToDo.TaskLists.COLUMN_VALUE_FALSE) };
			break;
		case MODE_DELETED:
			selection = ToDo.TaskLists.COLUMN_NAME_DELETED + "=?";
			selectionArgs = new String[] { String.valueOf(ToDo.TaskLists.COLUMN_VALUE_TRUE) };
			break;
		default:
			throw new IllegalArgumentException("list(ListMode listMode), listMode invalid argument.");
		}
		
		return contentResolver.query(
				ToDo.TaskLists.CONTENT_URI,
				ToDo.TaskLists.PROJECTION,
				selection, selectionArgs, null);
	}

	/* (non-Javadoc)
	 * @see il.ac.shenkar.todo.model.dao.tasks.logic.ITaskListsDAO#insert(com.google.api.services.tasks.model.TaskList)
	 */
	@Override
	public TaskList insert(TaskList taskList) {
		// Logger
		Log.d(TAG, "insert(TaskList taskList)");
		
		ContentValues values = new ContentValues();
		values.putNull(ToDo.TaskLists.COLUMN_NAME_CLIENT_ID);
		values.put(ToDo.TaskLists.COLUMN_NAME_SERVER_ID, (String) taskList.get(ToDo.TaskLists.COLUMN_NAME_SERVER_ID));
		values.put(ToDo.TaskLists.COLUMN_NAME_KIND, taskList.getKind());
		values.put(ToDo.TaskLists.COLUMN_NAME_TITLE, taskList.getTitle());
		DateTime taskListUpdated = taskList.getUpdated();
		if (taskListUpdated == null) {
			values.put(ToDo.TaskLists.COLUMN_NAME_UPDATED, DateTimeUtils.getDateTimeNow().toStringRfc3339());
		} else {
			values.put(ToDo.TaskLists.COLUMN_NAME_UPDATED, taskListUpdated.toStringRfc3339());
		}
		values.put(ToDo.TaskLists.COLUMN_NAME_SELF_LINK, taskList.getSelfLink());
		Integer taskListDeleted = (Integer) taskList.get(ToDo.TaskLists.COLUMN_NAME_DELETED);
		if (taskListDeleted != null && taskListDeleted == ToDo.TaskLists.COLUMN_VALUE_TRUE) {
			values.put(ToDo.TaskLists.COLUMN_NAME_DELETED, ToDo.TaskLists.COLUMN_VALUE_TRUE);
		} else {
			values.put(ToDo.TaskLists.COLUMN_NAME_DELETED, ToDo.TaskLists.COLUMN_VALUE_FALSE);
		}
		Uri insertedTaskListUri = contentResolver.insert(ToDo.TaskLists.CONTENT_URI, values);
		
		long insertedTaskListClientId = ContentUris.parseId(insertedTaskListUri);
		taskList.set(ToDo.TaskLists.COLUMN_NAME_CLIENT_ID, insertedTaskListClientId);
		
		return taskList;
	}

	/* (non-Javadoc)
	 * @see il.ac.shenkar.todo.model.dao.tasks.logic.ITaskListsDAO#get(long)
	 */
	@Override
	public TaskList get(long taskListClientId) {
		// Logger
		Log.d(TAG, "get(long taskListClientId)");
		
		String selection = ToDo.TaskLists.COLUMN_NAME_CLIENT_ID + "=?";
		String[] selectionArgs = {String.valueOf(taskListClientId)};
		
		Cursor cursor = contentResolver.query(
				ToDo.TaskLists.CONTENT_URI,
				ToDo.TaskLists.PROJECTION,
				selection, selectionArgs, null);

		TaskList taskList = null;
		if (cursor != null && cursor.moveToFirst()) {
			taskList = get(cursor);
			cursor.close();
		}
		
		return taskList;
	}
	
	/* (non-Javadoc)
	 * @see il.ac.shenkar.todo.model.dao.tasks.logic.ITaskListsDAO#get(android.database.Cursor)
	 */
	@Override
	public TaskList get(Cursor cursor) {
		long taskListClientId = cursor.getLong(ToDo.TaskLists.COLUMN_POSITION_CLIENT_ID);
		String taskListServerId = cursor.getString(ToDo.TaskLists.COLUMN_POSITION_SERVER_ID);
		String taskListKind = cursor.getString(ToDo.TaskLists.COLUMN_POSITION_KIND);
		String taskListTitle = cursor.getString(ToDo.TaskLists.COLUMN_POSITION_TITLE);
		String taskListUpdated = cursor.getString(ToDo.TaskLists.COLUMN_POSITION_UPDATED);
		String taskListSelfLink = cursor.getString(ToDo.TaskLists.COLUMN_POSITION_SELF_LINK);
		int taskListDeleted = cursor.getInt(ToDo.TaskLists.COLUMN_POSITION_DELETED);
		
		TaskList taskList = new TaskList();
		taskList.set(ToDo.TaskLists.COLUMN_NAME_CLIENT_ID, taskListClientId);
		taskList.setId(taskListServerId);
		taskList.setKind(taskListKind);
		taskList.setTitle(taskListTitle);
		taskList.setUpdated(DateTime.parseRfc3339(taskListUpdated));
		taskList.setSelfLink(taskListSelfLink);
		taskList.set(ToDo.TaskLists.COLUMN_NAME_DELETED, taskListDeleted);
		
		return taskList;
	}

	/* (non-Javadoc)
	 * @see il.ac.shenkar.todo.model.dao.tasks.logic.ITaskListsDAO#getTaskListClientId(java.lang.String)
	 */
	@Override
	public long getTaskListClientId(String taskListServerId) {
		// Logger
		Log.d(TAG, "getTaskListClientId(String taskListServerId)");
		
		long taskListClientId = -1L;
		
		String[] projection = new String[] { ToDo.TaskLists.COLUMN_NAME_CLIENT_ID };
		String selection = ToDo.TaskLists.COLUMN_NAME_SERVER_ID + "=?";
		String[] selectionArgs = {taskListServerId};
		
		Cursor cursor = contentResolver.query(
				ToDo.TaskLists.CONTENT_URI,
				projection,
				selection, selectionArgs, null);
		
		if (cursor != null && cursor.moveToFirst()) {
			taskListClientId = cursor.getLong(0);
			cursor.close();
		}
		
		return taskListClientId;
	}

	/* (non-Javadoc)
	 * @see il.ac.shenkar.todo.model.dao.tasks.logic.ITaskListsDAO#update(com.google.api.services.tasks.model.TaskList)
	 */
	@Override
	public void update(TaskList taskList) {
		// Logger
		Log.d(TAG, "update(TaskList taskList)");
		
		long taskListClientId = (Long) taskList.get(ToDo.TaskLists.COLUMN_NAME_CLIENT_ID);
		
		ContentValues values = new ContentValues();
		values.put(ToDo.TaskLists.COLUMN_NAME_SERVER_ID, taskList.getId());
		values.put(ToDo.TaskLists.COLUMN_NAME_KIND, taskList.getKind());
		values.put(ToDo.TaskLists.COLUMN_NAME_TITLE, taskList.getTitle());
		values.put(ToDo.TaskLists.COLUMN_NAME_UPDATED, taskList.getUpdated().toStringRfc3339());
		values.put(ToDo.TaskLists.COLUMN_NAME_SELF_LINK, taskList.getSelfLink());
		values.put(ToDo.TaskLists.COLUMN_NAME_DELETED, (Integer) taskList.get(ToDo.TaskLists.COLUMN_NAME_DELETED));
		
		String selection = ToDo.TaskLists.COLUMN_NAME_CLIENT_ID + "=?";
		String[] selectionArgs = {String.valueOf(taskListClientId)};
		
		contentResolver.update(ToDo.TaskLists.CONTENT_URI, values, selection, selectionArgs);
	}

	/* (non-Javadoc)
	 * @see il.ac.shenkar.todo.model.dao.tasks.logic.ITaskListsDAO#delete(long)
	 */
	@Override
	public void delete(long taskListClientId) {
		// Logger
		Log.d(TAG, "delete(long taskListClientId)");
		
		// Deletes all decending tasks from this task list		
		tasksDAO.delete(taskListClientId);
		
		// Deletes task list from local db
		String selection = ToDo.TaskLists.COLUMN_NAME_CLIENT_ID + "=?";
		String[] selectionArgs = {String.valueOf(taskListClientId)};
		
		contentResolver.delete(ToDo.TaskLists.CONTENT_URI, selection, selectionArgs);
	}

}
