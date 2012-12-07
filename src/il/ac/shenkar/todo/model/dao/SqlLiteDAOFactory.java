/**
 * 
 */
package il.ac.shenkar.todo.model.dao;

import android.content.Context;
import il.ac.shenkar.todo.model.dao.impl.sqllite.TaskDAO;
import il.ac.shenkar.todo.model.dao.logic.ITaskDAO;

/**
 * @author ran
 *
 */
public class SqlLiteDAOFactory extends DAOFactory {

	@Override
	public ITaskDAO getTaskDAO(Context context) {
		return new TaskDAO(context);
	}

}
