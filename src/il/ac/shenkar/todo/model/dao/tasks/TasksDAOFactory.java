/**
 * 
 */
package il.ac.shenkar.todo.model.dao.tasks;

import il.ac.shenkar.todo.model.dao.tasks.logic.ITaskListsDAO;
import il.ac.shenkar.todo.model.dao.tasks.logic.ITasksDAO;
import android.content.Context;

/**
 * @author ran
 *
 */
public abstract class TasksDAOFactory {

	// List of DAO types supported by the factory
	public static final int SQLITE = 1;
	
	// There will be a method for each DAO that can be created.
	// The conrete factories will have to implement there methods.
	public abstract ITasksDAO getTasksDAO();
	public abstract ITaskListsDAO getTaskListsDAO();
	
	public static TasksDAOFactory getFactory(Context context, int whichFactory) {
		switch (whichFactory) {
		case SQLITE:
			return new TasksSqliteDAOFactory(context);
		default:
			return null;
		}
	}

}
