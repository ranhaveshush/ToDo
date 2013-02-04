package il.ac.shenkar.todo.controller.activities;

import il.ac.shenkar.todo.R;
import il.ac.shenkar.todo.config.ToDo;
import il.ac.shenkar.todo.controller.adapters.TasksCursorAdapter;
import il.ac.shenkar.todo.model.dao.tasks.TasksDAOFactory;
import il.ac.shenkar.todo.model.dao.tasks.logic.ITasksDAO;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.api.services.tasks.model.Task;

// FIXME: work with assersion insted of of code conditionals
/**
 * @author ran
 *
 */
public class TasksListActivity extends FragmentActivity implements LoaderManager.LoaderCallbacks<Cursor> {
	
	/**
	 * Logger tag.
	 */
	private static final String TAG = "TaskListActivity";
	
	/**
	 * Represents the milliseconds in a day.
	 */
	//private static final long DAY_MILLISECONDS_INTERVAL = 5000;//1000 * 60 * 60 * 24;
	
	/**
	 * Request code for voice recognition activity for result.
	 */
	private static final int REQUEST_VOICE_RECOGNITION_REQUEST_CODE = 3;
	
	/**
	 * Holds the cursor adapter for the tasks list view.
	 */
	private TasksCursorAdapter tasksCursorAdapter = null;
	
	/**
	 * Holds the list view.
	 */
	private ListView listView = null;
	
	/**
	 * Menu action bar.
	 */
	private Menu menu = null;
	
	/**
	 * Holds the dao to the tasks resouce.
	 */
	private ITasksDAO tasksDAO = null;
	

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Logger
		Log.d(TAG, "onCreate(Bundle savedInstanceState)");

		setContentView(R.layout.activity_tasks_list);
		
		// Checks if the user selected a sync with google account
		SharedPreferences prefsFileAuth = getApplicationContext()
				.getSharedPreferences(ToDo.Prefs.PREFS_FILE_AUTH, Context.MODE_PRIVATE);
		String accountName = prefsFileAuth.getString(ToDo.Prefs.PREF_ACCOUNT_NAME, null);
		
		// If the user not selected an account, fire account selection
		if (accountName == null) {
			Intent intent = new Intent(TasksListActivity.this, CredentialActivity.class);
			startActivity(intent);
		}
		
		tasksDAO = TasksDAOFactory.getFactory(TasksListActivity.this, TasksDAOFactory.SQLITE).getTasksDAO();
		
		// Gets list view widget
		tasksCursorAdapter = new TasksCursorAdapter(TasksListActivity.this, null, 0);
		// FIXME: fix resource id for listview is ok so "None tasks" wouldn't show
		listView = (ListView) findViewById(android.R.id.list);
		listView.setAdapter(tasksCursorAdapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				editExistingTask("@default", id);
			}
		});
		
		// Prepares the loader.
		// Either re-connect with an existing one, or start a new one.
		getSupportLoaderManager().initLoader(0, null, this);
		
		// Configure add random task from given URL service
		// FIXME: check if the functionality is OK
		/*Intent intent = new Intent(getApplicationContext(), AddTaskService.class);
		PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT);
		AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, new Date().getTime() + 10000, DAY_MILLISECONDS_INTERVAL, pendingIntent);*/
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//Logger
		Log.d(TAG, "onCreateOptionsMenu(Menu menu)");
		
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.activity_tasks_list, menu);
		this.menu = menu;
		return true;
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Logger
		Log.d(TAG, "onOptionsItemSelected(MenuItem item)");
		
		switch (item.getItemId()) {
		case R.id.menu_item_microphone:
			// Sets the microphone selected event behavior
			// according to the voice recognition availability
			if (isVoiceRecognitionEnabled()) {
				startVoiceRecognition();
			} else {
				Toast.makeText(TasksListActivity.this, "Voice recognition is not available", Toast.LENGTH_SHORT).show();
			}
			return true;
		case R.id.menu_item_add_task:
			addTask();
			return true;
		case R.id.menu_item_edit_task:
			editNewTask("@default");
			return true;
		case R.id.menu_item_accounts:
			// Redirects to the CredentialActivity
			Intent intent = new Intent(TasksListActivity.this, CredentialActivity.class);
			intent.setAction(ToDo.Actions.ACTION_SELECT_ACCOUNT);
			startActivity(intent);
			return true;
		case R.id.menu_item_settings:
			// inflate settings menu
			return true;
		default:
            return super.onOptionsItemSelected(item);
		}
	}
	
	/* (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onStart()
	 */
	@Override
	protected void onStart() {
		// Logger
		Log.d(TAG, "onStart()");
		
		super.onStart();
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onResume()
	 */
	@Override
	protected void onResume() {
		// Logger
		Log.d(TAG, "onResume()");
		
		super.onResume();
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
	 */
	@SuppressLint("NewApi")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// Logger
		Log.d(TAG, "onActivityResult(int requestCode, int resultCode, Intent data)");
		
		super.onActivityResult(requestCode, resultCode, data);
		
		switch (requestCode) {
		case REQUEST_VOICE_RECOGNITION_REQUEST_CODE:
			// If the voice recognition is successful then it returns RESULT_OK
			if (resultCode == RESULT_OK) {
				ArrayList<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
				// If there are results
				if (!results.isEmpty()) {
					// Sets the best matched result to the menu item task title 
					String bestMatch = results.get(0);
					MenuItem menuItemTaskTitle = menu.findItem(R.id.menu_item_task_title);
					EditText editTextTaskTitle = (EditText) menuItemTaskTitle.getActionView();
					editTextTaskTitle.setText(bestMatch);
				}
			// Result code for various error.
			} else if(resultCode == RecognizerIntent.RESULT_AUDIO_ERROR) {
				Toast.makeText(TasksListActivity.this, "Audio Error", Toast.LENGTH_SHORT).show();
			} else if(resultCode == RecognizerIntent.RESULT_CLIENT_ERROR) {
				Toast.makeText(TasksListActivity.this, "Client Error", Toast.LENGTH_SHORT).show();
			} else if(resultCode == RecognizerIntent.RESULT_NETWORK_ERROR) {
				Toast.makeText(TasksListActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
			} else if(resultCode == RecognizerIntent.RESULT_NO_MATCH) {
				Toast.makeText(TasksListActivity.this, "No Match", Toast.LENGTH_SHORT).show();
			} else if(resultCode == RecognizerIntent.RESULT_SERVER_ERROR) {
				Toast.makeText(TasksListActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
			}
			break;
		}
	}
	
	/**
	 * Checks if voice recognition is enabled.
	 * 
	 * @return boolean if voice recognition is enabled true, otherwise false
	 */
	private boolean isVoiceRecognitionEnabled() {
		// Logger
		Log.d(TAG, "isVoiceRecognitionEnabled()");
		
		PackageManager pm = getPackageManager();
		List<ResolveInfo> activities = pm.queryIntentActivities(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
		if (activities.size() == 0) {
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * Starts voice recognition service.
	 */
	@SuppressLint("NewApi")
	private void startVoiceRecognition() {
		// Logger
		Log.d(TAG, "startVoiceRecognition()");
		
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		
		// Specify the calling package to indentify your application
		intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());
		
		MenuItem menuItemTaskTitle = menu.findItem(R.id.menu_item_task_title);
		EditText editTextTaskTitle = (EditText) menuItemTaskTitle.getActionView();
		
		// Display a hint to the user about what he should say
		intent.putExtra(RecognizerIntent.EXTRA_PROMPT, editTextTaskTitle.getText().toString());
		
		// Gives an hint to the recognizer about what the user is going to say
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		
		// Start the Voice recognizer activity for result
		startActivityForResult(intent, REQUEST_VOICE_RECOGNITION_REQUEST_CODE);
	}
	
	/**
	 * Adds task.
	 */
	@SuppressLint("NewApi")
	private void addTask() {
		// Logger
		Log.d(TAG, "addTask()");
		
		// Gets menu item task title
		MenuItem menuItemTaskTitle = menu.findItem(R.id.menu_item_task_title);
		// Gets menu item task title edit text view
		EditText editTextTaskTitle = (EditText) menuItemTaskTitle.getActionView();
		// Gets the text out of the task title edit text view
		String taskTitle = editTextTaskTitle.getText().toString();
		
		// Resets menu item task title
		editTextTaskTitle.setText("");
		
		// Validates the task params
		if (taskTitle == null || taskTitle.isEmpty()) {
			// Notify the user
			Toast.makeText(TasksListActivity.this, "Task's title is empty", Toast.LENGTH_SHORT).show();
			return;
		}
		
		// Creates new task with the title
		Task task = new Task();
		task.setTitle(taskTitle);
		task.set(ToDo.Tasks.COLUMN_NAME_TASKLIST_ID, "@default");
		
		tasksDAO.insert(task);
		
		// Syncs to the cloud
		//SyncUtils.syncWithGoogleTasks();
	}
	
	/**
	 * Redirects to TaskEditorActivity to edit a new task.
	 * 
	 * @param taskListId	String represnets task list identifier
	 */
	@SuppressLint("NewApi")
	private void editNewTask(String taskListId) {
		// Logger
		Log.d(TAG, "editNewTask(String taskListId)");
		
		// Gets menu item task title
		MenuItem menuItemTaskTitle = menu.findItem(R.id.menu_item_task_title);
		// Gets menu item task title edit text view
		EditText editTextTaskTitle = (EditText) menuItemTaskTitle.getActionView();
		// Gets the text out of the task title edit text view
		String taskTitle = editTextTaskTitle.getText().toString();
		
		// Reset menu item task title
		editTextTaskTitle.setText("");
		
		// Redirect to TaskEditorActivity
		Intent intent = new Intent(TasksListActivity.this, TaskEditorActivity.class);
		intent.setAction(ToDo.Actions.ACTION_EDIT_NEW_TASK);
		intent.putExtra(ToDo.Extras.EXTRA_TASK_LIST_ID, taskListId);
		intent.putExtra(ToDo.Extras.EXTRA_TASK_TITLE, taskTitle);
		startActivity(intent);
	}
	
	/**
	 * Redirects to TaskEditorActivity to edit an existing task.
	 * 
	 * @param taskListId	String represents task list identifier
	 * @param taskId		long represents task client identifier
	 */
	private void editExistingTask(String taskListId, long taskId) {
		// Logger
		Log.d(TAG, "editExistingTask(String taskListId, int taskId)");
		
		// Redirects to TaskEditorActivity
		Intent intent = new Intent(TasksListActivity.this, TaskEditorActivity.class);
		intent.setAction(ToDo.Actions.ACTION_EDIT_EXISTING_TASK);
		intent.putExtra(ToDo.Extras.EXTRA_TASK_LIST_ID, taskListId);
		intent.putExtra(ToDo.Extras.EXTRA_TASK_ID, taskId);
		startActivity(intent);
	}

	/* (non-Javadoc)
	 * 
	 * Called when a new Loader needs to be created.
	 * 
	 * @see android.support.v4.app.LoaderManager.LoaderCallbacks#onCreateLoader(int, android.os.Bundle)
	 */
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		// Now create and return a CursorLoader that will take care of
        // creating a Cursor for the data being displayed
		String selection = ToDo.Tasks.COLUMN_NAME_DELETED + " ISNULL OR "
				+ ToDo.Tasks.COLUMN_NAME_DELETED + "=?";
		String[] selectionArgs = new String[] { String.valueOf(ToDo.Tasks.COLUMN_VALUE_FALSE) };
		
		return new CursorLoader(
				TasksListActivity.this,
				ToDo.Tasks.CONTENT_URI,
				ToDo.Tasks.PROJECTION,
				selection, selectionArgs, null);
	}

	/* (non-Javadoc)
	 * 
	 * Called when a previously created loader has finished loading.
	 * 
	 * @see android.support.v4.app.LoaderManager.LoaderCallbacks#onLoadFinished(android.support.v4.content.Loader, java.lang.Object)
	 */
	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		// Swap the new cursor in.  (The framework will take care of closing the
        // old cursor once we return.)
        tasksCursorAdapter.swapCursor(data);
	}

	/* (non-Javadoc)
	 * 
	 * Called when a previously created loader is reset, making the data unavailable.
	 * 
	 * @see android.support.v4.app.LoaderManager.LoaderCallbacks#onLoaderReset(android.support.v4.content.Loader)
	 */
	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		// This is called when the last Cursor provided to onLoadFinished()
        // above is about to be closed.  We need to make sure we are no
        // longer using it
		tasksCursorAdapter.swapCursor(null);
	}
	
}
