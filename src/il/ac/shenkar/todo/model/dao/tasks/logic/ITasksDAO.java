/**
 * 
 */
package il.ac.shenkar.todo.model.dao.tasks.logic;

import android.database.Cursor;

import com.google.api.services.tasks.model.Task;

/**
 * @author ran
 *
 */
public interface ITasksDAO {

	/**
	 * Inserts newly created task to local database.
	 * 
	 * @param task 			The task to be persisted
	 * 
	 * @return Task			The newly created task
	 */
	public Task insert(Task task);
	
	/**
	 * Returns a task from local database.
	 * 
	 * @param taskClientId		Task to get identifier
	 * 
	 * @return Task				The task to get
	 */
	public Task get(long taskClientId);
	
	/**
	 * Updates a task at local database.
	 * 
	 * @param task			Task to update
	 */
	public void update(Task task);
	
	/**
	 * Deletes a task from local database.
	 * 
	 * @param task		Task to delete
	 */
	public void delete(Task task);
	
	/**
	 * Lists all the tasks from a given task list
	 * from local database.
	 * 
	 * @param taskListId	Task list identifier
	 * 
	 * @return 				Cursor list of tasks
	 */
	public Cursor list(String taskListId);
	
}
