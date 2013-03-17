/**
 * 
 */
package il.ac.shenkar.todo.model.dao.tasks.logic;

import android.database.Cursor;

import com.google.api.services.tasks.model.TaskList;

/**
 * @author ran
 *
 */
public interface ITaskListsDAO {
	
	/**
	 * Indicates the list mode.
	 * 
	 * @author ran
	 *
	 */
	public enum ListMode {
		MODE_ALL,
		MODE_NOT_DELETED,
		MODE_DELETED
	};
	
	/**
	 * Lists all the task lists from local database (excluded deleted).
	 * 
	 * @param listMode	ListMode represent the mode to list
	 * @return			Cursor list of task lists
	 */
	public Cursor list(ListMode listMode);

	/**
	 * Inserts newly created task list to local database.
	 * 
	 * @param taskList 		The task list to be persisted
	 * 
	 * @return TaskList		The newly created task list
	 */
	public TaskList insert(TaskList taskList);
	
	/**
	 * Returns a task list from local database.
	 * 
	 * @param taskListClientId		Task List to get client identifier
	 * 
	 * @return taskList				The task list to get
	 */
	public TaskList get(long taskListClientId);

	/**
	 * Returns the task list from the cursor in its current position.
	 * 
	 * @param cursor		Cursor to get task list from
	 * 
	 * @return TaskList		The task list to get
	 */
	public TaskList get(Cursor cursor);
	
	/**
	 * Returns a task list client id by given task list server id.
	 * 
	 * @param taskListServerId		String represents task list server id
	 * 
	 * @return						Long represents task list client id
	 */
	public long getTaskListClientId(String taskListServerId);
	
	/**
	 * Updates a task list at local database.
	 * 
	 * @param taskList		Task list to update
	 */
	public void update(TaskList taskList);
	
	/**
	 * Deletes a task list from local database.
	 * 
	 * @param taskListClientId		Task list client id to delete
	 */
	public void delete(long taskListClientId);

}
