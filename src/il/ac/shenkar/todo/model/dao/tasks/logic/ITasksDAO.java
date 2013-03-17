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
	 * Lists all the tasks from local database.
	 * 
	 * @return 				Cursor list of all the tasks
	 */
	public Cursor listAll();
	
	/**
	 * Lists all the tasks from a given task list
	 * from local database.
	 * 
	 * @param taskListId	Task list client identifier
	 * 
	 * @return 				Cursor list of tasks
	 */
	public Cursor list(long taskListClientId);

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
	 * @param taskClientId		Task to get client identifier
	 * 
	 * @return Task				The task to get
	 */
	public Task get(long taskClientId);
	
	/**
	 * Returns the task from the cursor in its current position.
	 * 
	 * @param cursor	Cursor to get task from
	 * 
	 * @return	Task	The task to get
	 */
	public Task get(Cursor cursor);
	
	/**
	 * Updates a task at local database.
	 * 
	 * @param task			Task to update
	 */
	public void update(Task task);
	
	/**
	 * Updates a task from local database as deleted.
	 * 
	 * @param task		Task to delete
	 */
	public void delete(Task task);
	
	/**
	 * Deletes all tasks from local database,
	 * By given task list client id.
	 * 
	 * @param taskListClinetId		Long task list client id
	 */
	public void delete(long taskListClientId);
	
	/**
	 * Moves a task from its origion position to target position.
	 * 
	 * @param task			Task to move
	 * @param parent		Parent task client identifier
	 * @param previous		Previous task client identifier
	 * @param post			Post task client identifier
	 */
	public void move(Task task, Integer parent, Integer previous, Integer post);
	
}
