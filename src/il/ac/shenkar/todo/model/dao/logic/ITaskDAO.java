package il.ac.shenkar.todo.model.dao.logic;

import il.ac.shenkar.todo.model.dto.Task;

import java.util.ArrayList;

/**
 * @author ran
 *
 */
public interface ITaskDAO {
	
	public ArrayList<Task> readAllTasks();

}
