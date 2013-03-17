/**
 * 
 */
package il.ac.shenkar.todo.controller.adapters;

import il.ac.shenkar.todo.R;
import il.ac.shenkar.todo.config.ToDo;
import il.ac.shenkar.todo.controller.activities.MainFragmentActivity;
import il.ac.shenkar.todo.model.dao.tasks.TasksDAOFactory;
import il.ac.shenkar.todo.model.dao.tasks.logic.ITaskListsDAO;
import il.ac.shenkar.todo.utilities.SyncUtils;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.api.services.tasks.model.TaskList;
import com.mobeta.android.dslv.DragSortCursorAdapter;

/**
 * @author ran
 *
 */
public class TaskListsCursorAdapter extends DragSortCursorAdapter {
	
	/**
	 * Logger's tag.
	 */
	private static final String TAG = "TaskListsCursorAdapter";

	/**
	 * Holds the invoking context.
	 */
	private final Context context;

	/**
	 * LyoutInflater.
	 */
	private final LayoutInflater inflater;
	
	/**
	 * Holds the dao to the task lists resouce.
	 */
	private final ITaskListsDAO taskListsDAO;
	
	/**
	 * Full constructor.
	 * 
	 * @param context
	 * @param cursor
	 * @param flags
	 */
	public TaskListsCursorAdapter(Context context, Cursor cursor, int flags) {
		super(context, cursor, flags);
		this.context = context;
		this.inflater = LayoutInflater.from(context);
		this.taskListsDAO = TasksDAOFactory.getFactory(context, TasksDAOFactory.SQLITE).getTaskListsDAO();
	}
	
	/**
	 * Inner class holds the new view and the convert view widgets.
	 * Performance helper class to help fast render the list view.
	 * 
	 * @author ran
	 *
	 */
	private static class ViewHolder {
		TextView textViewTitle;
	}

	/* (non-Javadoc)
	 * @see android.support.v4.widget.CursorAdapter#newView(android.content.Context, android.database.Cursor, android.view.ViewGroup)
	 */
	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		// Creates a new view with view holder as its tag
		View newView = inflater.inflate(R.layout.listview_item_task_list, parent, false);
		ViewHolder holder = new ViewHolder();
		holder.textViewTitle = (TextView) newView.findViewById(R.id.text_view_task_title);
		newView.setTag(holder);
				
		return newView;
	}
	
	/* (non-Javadoc)
	 * @see android.support.v4.widget.CursorAdapter#bindView(android.view.View, android.content.Context, android.database.Cursor)
	 */
	@Override
	public void bindView(View convertView, Context context, Cursor cursor) {
		ViewHolder holder = (ViewHolder) convertView.getTag();

		// Gets the task data from the cursor
		String taskTitle = cursor.getString(ToDo.Tasks.COLUMN_POSITION_TITLE);
		
		// Sets the view widgets contents and functionality
		holder.textViewTitle.setText(taskTitle);
	}

	/* (non-Javadoc)
	 * @see com.mobeta.android.dslv.DragSortListView.RemoveListener#remove(int)
	 */
	@Override
	public void remove(int which) {
		// Logger
		Log.d(TAG, "remove(which: "+which+")");
		
		// Don't close this cursor,
		// Because it's the adapter's cursor
		Cursor cursor = getCursor();
		
		// Gets the task list client id
		cursor.moveToPosition(which);
		Log.d(TAG, "remove task list: " + cursor.getString(ToDo.TaskLists.COLUMN_POSITION_TITLE));
		long taskListToRemoveClientId = cursor.getLong(ToDo.TaskLists.COLUMN_POSITION_CLIENT_ID);
		
		// Updates the task list from local db to deleted true
		// When this list will be synced with remote, it'll be deleted
		TaskList taskListToRemove = taskListsDAO.get(taskListToRemoveClientId);
		taskListToRemove.set(ToDo.TaskLists.COLUMN_NAME_DELETED, ToDo.TaskLists.COLUMN_VALUE_TRUE);
		taskListsDAO.update(taskListToRemove);
		
		// Handles the task list deleted event
		((MainFragmentActivity) context).onTaskListDeleted(taskListToRemoveClientId);
		
		// Syncs with the cloud
		SyncUtils.syncWithGoogleTasks(context.getApplicationContext());
	}

}
