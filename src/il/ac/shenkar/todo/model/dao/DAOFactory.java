/**
 * 
 */
package il.ac.shenkar.todo.model.dao;

import android.content.Context;
import il.ac.shenkar.todo.model.dao.logic.ITaskDAO;

/**
 * @author ran
 *
 */
public abstract class DAOFactory {
	
	public static final int SQL_LITE = 0;
	
	public static DAOFactory getDAOFactory(int whichDatabase) {
		switch (whichDatabase) {
		case SQL_LITE:
			return new SqlLiteDAOFactory();
		default:
			return null;
		}
	}
	
	public abstract ITaskDAO getTaskDAO(Context context);

}
