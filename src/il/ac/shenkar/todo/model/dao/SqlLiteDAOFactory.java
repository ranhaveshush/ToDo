/**
 * 
 */
package il.ac.shenkar.todo.model.dao;

import il.ac.shenkar.todo.model.dao.impl.sqllite.TaskDAO;
import il.ac.shenkar.todo.model.dao.logic.ITaskDAO;

/**
 * @author ran
 *
 */
public class SqlLiteDAOFactory extends DAOFactory {

	@Override
	public ITaskDAO getTaskDAO() {
		return new TaskDAO();
	}

}
