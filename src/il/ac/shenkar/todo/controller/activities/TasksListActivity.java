package il.ac.shenkar.todo.controller.activities;

import il.ac.shenkar.todo.R;
import il.ac.shenkar.todo.controller.adapters.TasksCursorAdapter;
import il.ac.shenkar.todo.model.contentproviders.ToDo;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v4.widget.CursorAdapter;
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

/**
 * @author ran
 * 
 */
public class TasksListActivity extends ListActivity {
	
	/**
	 * Logger tag.
	 */
	private static final String TAG = "TaskListActivity";
	
	/**
	 * Request code for voice recognition activity for result.
	 */
	private static final int VOICE_RECOGNITION_REQUEST_CODE = 1;

	/**
	 * Menu action bar.
	 */
	private Menu menu;
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Logger
		Log.d(TAG, "onCreate(Bundle savedInstanceState)");

		setContentView(R.layout.activity_tasks_list);

		Cursor cursor = getContentResolver().query(
				ToDo.Tasks.CONTENT_URI,
				new String[] { ToDo.Tasks._ID, ToDo.Tasks.COLUMN_NAME_TITLE },
				null, null, null);
		TasksCursorAdapter cursorAdapter = new TasksCursorAdapter(
				TasksListActivity.this,
				cursor, CursorAdapter.FLAG_AUTO_REQUERY);
		
		// Gets list view widget
		ListView listView = getListView();
		listView.setAdapter(cursorAdapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				editExistingTask(id);
			}
		});
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
				showToastMessage("Voice recognition is not available", Toast.LENGTH_SHORT);
			}
			return true;
		case R.id.menu_item_add_task:
			addTask();
			return true;
		case R.id.menu_item_edit_task:
			editNewTask();
			return true;
		case R.id.menu_item_settings:
			// inflate settings menu
			return true;
		default:
            return super.onOptionsItemSelected(item);
		}
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
	 */
	@SuppressLint("NewApi")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// Logger
		Log.d(TAG, "onActivityResult(int requestCode, int resultCode, Intent data)");
		
		// If the result is from the recognizer intent
		if (requestCode == VOICE_RECOGNITION_REQUEST_CODE) {
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
				showToastMessage("Audio Error", Toast.LENGTH_SHORT);
			} else if(resultCode == RecognizerIntent.RESULT_CLIENT_ERROR) {
				showToastMessage("Client Error", Toast.LENGTH_SHORT);
			} else if(resultCode == RecognizerIntent.RESULT_NETWORK_ERROR) {
				showToastMessage("Network Error", Toast.LENGTH_SHORT);
			} else if(resultCode == RecognizerIntent.RESULT_NO_MATCH) {
				showToastMessage("No Match", Toast.LENGTH_SHORT);
			} else if(resultCode == RecognizerIntent.RESULT_SERVER_ERROR) {
				showToastMessage("Server Error", Toast.LENGTH_SHORT);
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
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
		
		// Logger
		Log.d(TAG, "getPackageName(): " + getPackageName());
		Log.d(TAG, "getClass().getPackage().getName(): " + getClass().getPackage().getName());
		
		// Specify the calling package to indentify your application
		intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());
		
		MenuItem menuItemTaskTitle = menu.findItem(R.id.menu_item_task_title);
		EditText editTextTaskTitle = (EditText) menuItemTaskTitle.getActionView();
		
		// Display a hint to the user about what he should say
		intent.putExtra(RecognizerIntent.EXTRA_PROMPT, editTextTaskTitle.getText().toString());
		
		// Gives an hint to the recognizer about what the user is going to say
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		
		// Start the Voice recognizer activity for result
		startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
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
		
		// Logger
		Log.d(TAG, "taskTitle: " + taskTitle);
		
		// A new task record's values
		ContentValues values = new ContentValues();
		values.putNull(ToDo.Tasks._ID);
		values.put(ToDo.Tasks.COLUMN_NAME_TITLE, taskTitle);
		values.putNull(ToDo.Tasks.COLUMN_NAME_DESCRIPTION);
		values.putNull(ToDo.Tasks.COLUMN_NAME_DATETIME);
		
		// Presists the new task
		getContentResolver().insert(ToDo.Tasks.CONTENT_URI, values);
		
		// Resets menu item task title
		editTextTaskTitle.setText("");
	}
	
	/**
	 * Redirects to TaskEditorActivity to edit a new task.
	 */
	@SuppressLint("NewApi")
	public void editNewTask() {
		// Logger
		Log.d(TAG, "editNewTask()");
		
		// Gets menu item task title
		MenuItem menuItemTaskTitle = menu.findItem(R.id.menu_item_task_title);
		// Gets menu item task title edit text view
		EditText editTextTaskTitle = (EditText) menuItemTaskTitle.getActionView();
		// Gets the text out of the task title edit text view
		String taskTitle = editTextTaskTitle.getText().toString();
		
		// Logger
		Log.d(TAG, "taskTitle: " + taskTitle);
		
		// Reset menu item task title
		editTextTaskTitle.setText("");
		
		// Redirect to TaskEditorActivity
		Intent intent = new Intent(TasksListActivity.this, TaskEditorActivity.class);
		intent.setAction(ToDo.Actions.ACTION_EDIT_NEW_TASK);
		intent.putExtra(ToDo.Extras.EXTRA_TASK_TITLE, taskTitle);
		startActivity(intent);
	}
	
	/**
	 * Redirects to TaskEditorActivity to edit an existing task.
	 */
	public void editExistingTask(long taskId) {
		// Logger
		Log.d(TAG, "editExistingTask(long taskId)");
		
		// Logger
		Log.d(TAG, "taskId: " + taskId);
		
		// Redirect to TaskEditorActivity
		Intent intent = new Intent(TasksListActivity.this, TaskEditorActivity.class);
		intent.setAction(ToDo.Actions.ACTION_EDIT_EXISTING_TASK);
		intent.putExtra(ToDo.Extras.EXTRA_TASK_ID, taskId);
		startActivity(intent);
	}
	
	/**
	 * Shows a toast message.
	 * 
	 * @param message String the message to toast
	 */
	private void showToastMessage(String text, int duration) {
		// Logger
		Log.d(TAG, "showToastMessage(String text, int duration)");
		
		Toast.makeText(TasksListActivity.this, text, duration).show();
	}

	
}
