package il.ac.shenkar.todo.model.dao.logic;

import il.ac.shenkar.todo.model.dto.Task;

import java.util.ArrayList;

/**
 * @author ran
 *
 */
public interface ITaskDAO {
	
	/**
	 * Create a new task record in the database.
	 * 
	 * @param task Task to add to the database
	 */
	public void createTask(Task task);
	
	/**
	 * Read a task record from the database.
	 * 
	 * @param taskId Task id to read
	 * @return Task object represents the task to read
	 */
	public Task readTask(long taskId);
	
	/**
	 * Read all tasks records from the database.
	 * 
	 * @return ArrayList<Task> object represents all the tasks in the database
	 */
	public ArrayList<Task> readAllTasks();
	
	/**
	 * Update a task record in the database.
	 * 
	 * @param task task object to update
	 */
	public void updateTask(Task task);
	
	/**
	 * Delete a tadk record from the database.
	 * 
	 * @param taskId Task id to delete from the database
	 */
	public void deleteTask(long taskId);

}
