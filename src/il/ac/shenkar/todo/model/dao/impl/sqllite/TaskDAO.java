/**
 * 
 */
package il.ac.shenkar.todo.model.dao.impl.sqllite;

import java.util.ArrayList;

import il.ac.shenkar.todo.model.dao.logic.ITaskDAO;
import il.ac.shenkar.todo.model.dto.Task;

/**
 * @author ran
 *
 */
public class TaskDAO implements ITaskDAO {
	
	// TODO: delete after connecting to database.
	public static ArrayList<Task> tasksListArray = new ArrayList<Task>();

	@Override
	public ArrayList<Task> readAllTasks() {
		return tasksListArray;
	}
	
}
