/**
 * 
 */
package il.ac.shenkar.todo.model.dao.impl.sqllite;

import il.ac.shenkar.todo.model.dao.logic.ITaskDAO;
import il.ac.shenkar.todo.model.database.TasksOpenHelper;
import il.ac.shenkar.todo.model.dto.Task;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * @author ran
 * 
 */
public class TaskDAO implements ITaskDAO {

	/**
	 * 
	 */
	private Context context;

	/**
	 * 
	 */
	private static final String TAG = "TaskDAO";

	/**
	 * Full contsructor.
	 * 
	 * @param context
	 */
	public TaskDAO(Context context) {
		this.context = context;
	}

	@Override
	public void createTask(Task task) {
		// Logger
		Log.d(TAG, "createTask("+task+")");
		
		// Get database connection
		TasksOpenHelper tasksOpenHelper = new TasksOpenHelper(context);
		Log.d(TAG, "getWriteableDB");
		SQLiteDatabase db = tasksOpenHelper.getWritableDatabase();

		// Create task record values
		ContentValues values = new ContentValues();
		values.putNull(TasksOpenHelper.KEY_ID);
		values.put(TasksOpenHelper.KEY_DESCRIPTION, task.getDescription());

		// Insert task record to the database
		db.insert(TasksOpenHelper.TABLE_TASKS, null, values);

		// Close database connection
		db.close();
		Log.d(TAG, "closeWriteableDB");

		// Close SQLiteOpenHelpter
		tasksOpenHelper.close();
	}

	@Override
	public Task readTask(long taskId) {
		// Logger
		Log.d(TAG, "readTask("+taskId+")");
		
		Task task = null;

		// Get database connection
		TasksOpenHelper tasksOpenHelper = new TasksOpenHelper(context);
		Log.d(TAG, "getReadableDB");
		SQLiteDatabase db = tasksOpenHelper.getReadableDatabase();

		// Read task record from the database
		Cursor cursor = db
				.query(TasksOpenHelper.TABLE_TASKS,
						new String[] { TasksOpenHelper.KEY_ID,
								TasksOpenHelper.KEY_DESCRIPTION },
						TasksOpenHelper.KEY_ID + "=?",
						new String[] { String.valueOf(taskId) }, null, null,
						null, null);

		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			task = new Task(cursor.getLong(0), cursor.getString(1));
		}

		// Close cursor
		cursor.close();

		// Close database connection
		db.close();
		Log.d(TAG, "closeReadableDB");

		// Close SQLiteOpenHelpter
		tasksOpenHelper.close();

		return task;
	}

	@Override
	public ArrayList<Task> readAllTasks() {
		// Logger
		Log.d(TAG, "readAllTasks()");
		
		ArrayList<Task> allTasks = null;

		// Get database connetction
		TasksOpenHelper tasksOpenHelper = new TasksOpenHelper(context);
		Log.d(TAG, "getReadableDB");
		SQLiteDatabase db = tasksOpenHelper.getReadableDatabase();

		// Read all tasks records
		String sql = "SELECT * FROM " + TasksOpenHelper.TABLE_TASKS;
		Cursor cursor = db.rawQuery(sql, null);

		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			allTasks = new ArrayList<Task>();
			do {
				Task task = new Task();
				task.setId(cursor.getLong(0));
				task.setDescription(cursor.getString(1));
				// Add read task to tasks list
				allTasks.add(task);
			} while (cursor.moveToNext());
		}

		// Close cursor
		cursor.close();

		// Close database connection
		db.close();
		Log.d(TAG, "closeReadableDB");

		// Close SQLiteOpenHelpter
		tasksOpenHelper.close();

		return allTasks;
	}

	@Override
	public void updateTask(Task task) {
		// Logger
		Log.d(TAG, "updateTask("+task+")");
		
		// get database connection
		TasksOpenHelper tasksOpenHelper = new TasksOpenHelper(context);
		Log.d(TAG, "getWriteableDB");
		SQLiteDatabase db = tasksOpenHelper.getWritableDatabase();

		// Create task record values
		ContentValues values = new ContentValues();
		values.put(TasksOpenHelper.KEY_ID, task.getId());
		values.put(TasksOpenHelper.KEY_DESCRIPTION, task.getDescription());

		// Update task record from the database
		db.update(TasksOpenHelper.TABLE_TASKS, values, TasksOpenHelper.KEY_ID
				+ "=?", new String[] { String.valueOf(task.getId()) });

		// Close database connection
		db.close();
		Log.d(TAG, "closeWriteableDB");

		// Close SQLiteOpenHelpter
		tasksOpenHelper.close();
	}

	@Override
	public void deleteTask(long taskId) {
		// Logger
		Log.d(TAG, "deleteTask("+taskId+")");
		
		// Get database connection
		TasksOpenHelper tasksOpenHelper = new TasksOpenHelper(context);
		Log.d(TAG, "getWriteableDB");
		SQLiteDatabase db = tasksOpenHelper.getWritableDatabase();

		// Delete task record from the database
		db.delete(TasksOpenHelper.TABLE_TASKS, TasksOpenHelper.KEY_ID + "=?",
				new String[] { String.valueOf(taskId) });

		// Close database connection
		db.close();
		Log.d(TAG, "closeWriteableDB");

		// Close SQLiteOpenHelpter
		tasksOpenHelper.close();
	}

}
