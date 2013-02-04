/**
 * 
 */
package il.ac.shenkar.todo.model.dao.tasks;

import il.ac.shenkar.todo.model.dao.tasks.impl.sqlite.TaskListsDAO;
import il.ac.shenkar.todo.model.dao.tasks.impl.sqlite.TasksDAO;
import il.ac.shenkar.todo.model.dao.tasks.logic.ITaskListsDAO;
import il.ac.shenkar.todo.model.dao.tasks.logic.ITasksDAO;
import android.content.Context;

/**
 * @author ran
 *
 */
public class TasksSqliteDAOFactory extends TasksDAOFactory {

	/**
	 * Holds the context which invoked this dao.
	 */
	private Context context = null;
	
	/**
	 * Full constructor.
	 * 
	 * @param context
	 */
	public TasksSqliteDAOFactory(Context context) {
		this.context = context;
	}

	/* (non-Javadoc)
	 * @see il.ac.shenkar.todo.model.dao.DAOFactory#getTaskDAO()
	 */
	@Override
	public ITasksDAO getTasksDAO() {
		return new TasksDAO(context);
	}

	/* (non-Javadoc)
	 * @see il.ac.shenkar.todo.model.dao.DAOFactory#getTaskListDAO()
	 */
	@Override
	public ITaskListsDAO getTaskListsDAO() {
		return new TaskListsDAO(context);
	}

}
