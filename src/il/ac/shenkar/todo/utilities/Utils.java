/**
 * 
 */
package il.ac.shenkar.todo.utilities;

import il.ac.shenkar.todo.model.dto.Task;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author ran
 *
 */
public abstract class Utils {
	
	/**
	 * URL from which to fetch a random task info.
	 */
	public static final String URL_ADDRESS = "http://mobile1-tasks-dispatcher.herokuapp.com/task/random";
	
	/**
	 * Json field name of the random task topic (title)
	 */
	public static final String JSON_TASK_TITLE_FEILD_NAME = "topic";
	
	/**
	 * Json field name of the random task description
	 */
	public static final String JSON_TASK_DESCRIPTION_FEILD_NAME = "description";
	
	/**
	 * Fetches a random task info from the given url.
	 * 
	 * @param url
	 */
	public static Task featchRandomTaskFromUrl(URL url) throws IOException, JSONException {
		// Opens connection to the url
		HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
		InputStream is = new BufferedInputStream(urlConnection.getInputStream());
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
		StringBuilder sb = new StringBuilder();
		// Fetches text from the http response
		for (String line = br.readLine(); line != null; line = br.readLine()) {
			sb.append(line);
		}
		String response = sb.toString();
		
		// Parse the response text into json object
		JSONObject jsonResponse = new JSONObject(response);
		// Fetches random task data from the json object
		String randomTaskTitle = jsonResponse.getString(JSON_TASK_TITLE_FEILD_NAME);
		String randomTaskDescription = jsonResponse.getString(JSON_TASK_DESCRIPTION_FEILD_NAME);
		
		// Returns the random task
		return new Task(randomTaskTitle, randomTaskDescription);
	}

}
