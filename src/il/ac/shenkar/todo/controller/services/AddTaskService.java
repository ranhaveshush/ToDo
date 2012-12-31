/**
 * 
 */
package il.ac.shenkar.todo.controller.services;

import il.ac.shenkar.todo.utilities.Utils;
import il.ac.shenkar.todo.config.ToDo;
import il.ac.shenkar.todo.model.dto.Task;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.widget.Toast;

/**
 * This class adds a random task to the tasks content provider
 * from given URL.
 * 
 * @author ran
 *
 */
public class AddTaskService extends IntentService {

	/**
	 * Default constructor.
	 */
	public AddTaskService() {
		super("AddTaskService");
	}

	/* (non-Javadoc)
	 * @see android.app.IntentService#onHandleIntent(android.content.Intent)
	 */
	@Override
	protected void onHandleIntent(Intent intent) {
		try {
			// Gets the random task from a given URL
			URL url = new URL(Utils.URL_ADDRESS);
			Task randomTask = Utils.featchRandomTaskFromUrl(url);
			
			// Saves the values fo the random task
			ContentValues values = new ContentValues();
			values.putNull(ToDo.Tasks._ID);
			values.put(ToDo.Tasks.COLUMN_NAME_TITLE, randomTask.getTitle());
			values.put(ToDo.Tasks.COLUMN_NAME_DESCRIPTION, randomTask.getDescription());
			values.putNull(ToDo.Tasks.COLUMN_NAME_DATETIME);
			
			// Persists the random task to tasks table through the content provider
			getContentResolver().insert(ToDo.Tasks.CONTENT_URI, values);
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
