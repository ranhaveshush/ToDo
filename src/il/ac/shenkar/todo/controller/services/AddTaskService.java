/**
 * 
 */
package il.ac.shenkar.todo.controller.services;

import il.ac.shenkar.todo.config.ToDo;
import il.ac.shenkar.todo.model.dao.tasks.TasksDAOFactory;
import il.ac.shenkar.todo.model.dao.tasks.logic.ITasksDAO;
import il.ac.shenkar.todo.utilities.RandomTaskUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.google.api.services.tasks.model.Task;

/**
 * This class adds a random task to the tasks content provider
 * from given URL.
 * 
 * @author ran
 *
 */
// FIXME: delete this before submission
public class AddTaskService extends IntentService {
	
	/**
	 * Holds the dao to the tasks resouce.
	 */
	private final ITasksDAO tasksDAO;

	/**
	 * Default constructor.
	 */
	public AddTaskService(Context context) {
		super("AddTaskService");
		
		this.tasksDAO = TasksDAOFactory.getFactory(context, TasksDAOFactory.SQLITE).getTasksDAO();
	}

	/* (non-Javadoc)
	 * @see android.app.IntentService#onHandleIntent(android.content.Intent)
	 */
	@Override
	protected void onHandleIntent(Intent intent) {
		try {
			// Gets the random task from a given URL
			URL url = new URL(RandomTaskUtils.URL_ADDRESS);
			Task randomTask = RandomTaskUtils.featchRandomTaskFromUrl(url);
			
			// Sets the random task's task list id
			randomTask.set(ToDo.Tasks.COLUMN_NAME_TASKLIST_ID, "@default");
			
			// Persists the random task to tasks table through the content provider
			tasksDAO.insert(randomTask);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			Toast.makeText(getApplicationContext(), "Network connection problem", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

}
