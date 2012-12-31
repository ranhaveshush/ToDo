package il.ac.shenkar.todo.controller.activities;

import il.ac.shenkar.todo.R;
import il.ac.shenkar.todo.utilities.Utils;
import il.ac.shenkar.todo.config.ToDo;
import il.ac.shenkar.todo.controller.fragments.DatePickerFragment;
import il.ac.shenkar.todo.controller.fragments.DatePickerFragment.OnDatePickedListener;
import il.ac.shenkar.todo.controller.fragments.TimePickerFragment;
import il.ac.shenkar.todo.controller.fragments.TimePickerFragment.OnTimePickedListener;
import il.ac.shenkar.todo.model.dto.Task;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONException;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author ran
 *
 */
public class TaskEditorActivity extends FragmentActivity implements OnTimePickedListener, OnDatePickedListener {
	
	/**
	 * Logger tag.
	 */
	private static final String TAG = "TaskEditorActivity";
	
	/**
     * Time format string.
     */
    private static final String TimeFormat = "HH:mm"; 
    
    /**
     * Date format string.
     */
    private static final String DateFormat = "yyyy-MM-dd";
    
    /**
     * Date Time format string.
     */
    private static final String DateTimeFormat = "yyyy-MM-dd HH:mm";
    
    /**
     * Simple date formater represents time by locale format.
     */
    private static final SimpleDateFormat simpleDateFormatTime = new SimpleDateFormat(TimeFormat, Locale.getDefault());
    
    /**
     * Simple date formater represents date by locale format.
     */
    private static final SimpleDateFormat simpleDateFormatDate = new SimpleDateFormat(DateFormat, Locale.getDefault());
    
    /**
     * Simple date formater represents datetime by locale format.
     */
    private static final SimpleDateFormat simpleDateFormatDateTime = new SimpleDateFormat(DateTimeFormat, Locale.getDefault());
	
	/**
	 * Task Editor states enum.
	 * Private inner class representing all the possiable state the
	 * task editor may be in.
	 * 
	 * @author ran
	 *
	 */
	private enum State {
		EDIT_EXISTING_TASK,
    	EDIT_NEW_TASK
    };
    
    /**
     * Task editor state.
     */
    private State state;
    
    /**
     * Current task to edit id.
     * When -1 means task not existing (not persisted).
     */
    private long taskId = -1;
    
    /**
     * Edit text represents the task's title.
     */
    private EditText editTextTitle = null;
    
    /**
     * Edit text represents the task's description.
     */
    private EditText editTextDescription = null;
    
    /**
     * Text view represents the task's time.
     */
    private TextView textViewTime = null;
    
    /**
     * Text view represents the task's date.
     */
    private TextView textViewDate = null;

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task_editor);
		
		// Gets all the widgets
		editTextTitle = (EditText) findViewById(R.id.edit_text_task_title);
		editTextDescription = (EditText) findViewById(R.id.edit_text_task_description);
		textViewTime = (TextView) findViewById(R.id.text_view_task_reminder_time);
		textViewTime.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				DialogFragment timePickerFragment = new TimePickerFragment();
				timePickerFragment.show(getSupportFragmentManager(), "timePicker");
			}
		});
		textViewDate = (TextView) findViewById(R.id.text_view_task_reminder_date);
		textViewDate.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				DialogFragment datePickerFragment = new DatePickerFragment();
				datePickerFragment.show(getSupportFragmentManager(), "datePicker");
			}
		});
		
		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		
		// Gets action
		String action = intent.getAction();
		// Sets the state
		if (ToDo.Actions.ACTION_EDIT_EXISTING_TASK.equals(action)) {
			state = State.EDIT_EXISTING_TASK;
		} else if(ToDo.Actions.ACTION_EDIT_NEW_TASK.equals(action)) {
			state = State.EDIT_NEW_TASK;
		}
		
		// Gets the state
		switch (state) {
		case EDIT_EXISTING_TASK:
			// Checks extras to find task to edit id
			if (extras != null && extras.containsKey(ToDo.Extras.EXTRA_TASK_ID)) {
				// Gets the existing task's id
				taskId = extras.getLong(ToDo.Extras.EXTRA_TASK_ID);
				
				// Reads the task from the content provider
				Task taskToEdit = readTaskById(taskId);
				
				if (taskToEdit != null) {
					// Sets edit text task's title
					editTextTitle.setText(taskToEdit.getTitle());
					// Sets edit text task's description
					editTextDescription.setText(taskToEdit.getDescription());
					long dateTimeMilliSec = taskToEdit.getDateTimeMilliSec();
					if (dateTimeMilliSec != 0) {
						// Gets datetime date object
						Date dateTime = new Date(dateTimeMilliSec);
						// Sets text view task reminder time
						textViewTime.setText(simpleDateFormatTime.format(dateTime));
						// Sets text view task reminder date
						textViewDate.setText(simpleDateFormatDate.format(dateTime));
					}
				}
			} else {
				// FIXME: handle error woth assersions
			}
			break;
		case EDIT_NEW_TASK:
			// Checks extras to find new taskToEdit title
			if (extras != null && extras.containsKey(ToDo.Extras.EXTRA_TASK_TITLE)) {
				// Gets the new task's title
				String taskToEditTitle = extras.getString(ToDo.Extras.EXTRA_TASK_TITLE);
				
				// Sets edit text task's title
				editTextTitle.setText(taskToEditTitle);
			} else {
				// FIXME: handle error woth assersions
			}
			break;
		}
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_task_editor, menu);
		return true;
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_item_save_task:
			saveTask();
			return true;
		case R.id.menu_item_random_task:
			URL url = null;
			try {
				url = new URL(Utils.URL_ADDRESS);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
			new GetRandomTaskFromUrl().execute(url);
			return true;
		case R.id.menu_item_delete_task:
			deleteTask();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * Saves a new task and returns to the parent activity (TasksListActivity).
	 */
	private void saveTask() {
		// Sets task's info after editing
		ContentValues values = new ContentValues();
		
		// Gets the task title
		Editable textTitle = editTextTitle.getText();
		// If title text isn't empty, persist title text
		if (textTitle != null) {
			values.put(ToDo.Tasks.COLUMN_NAME_TITLE, textTitle.toString());
		// If title text is empty, notify user
		} else {
			Toast.makeText(TaskEditorActivity.this, "Failed to edit task, fill Title field", Toast.LENGTH_SHORT).show();
			return;
		}
		
		// Gets the task description
		Editable textDescription = editTextDescription.getText();
		// If description text isn't empty, persist description text
		if (textDescription != null) {
			values.put(ToDo.Tasks.COLUMN_NAME_DESCRIPTION, textDescription.toString());
		// If description text is empty
		} else {
			values.putNull(ToDo.Tasks.COLUMN_NAME_DESCRIPTION);
		}

		// Gets the task reminder datetime
		String textTime = (String) textViewTime.getText();
		String textDate = (String) textViewDate.getText();
		Date dateTime = fromStringsToDate(textDate, textTime);
		// If datetime reminder isn't empty, persist datetime reminder
		if (dateTime != null) {
			values.put(ToDo.Tasks.COLUMN_NAME_DATETIME, dateTime.getTime());
		// If datetime reminder is empty
		} else {
			values.putNull(ToDo.Tasks.COLUMN_NAME_DATETIME);
		}

		switch (state) {
		case EDIT_EXISTING_TASK:
			// Persist task id
			values.put(ToDo.Tasks._ID, taskId);
			// Persists the changes to the existing task
			Uri taskToEditUri = ContentUris.withAppendedId(ToDo.Tasks.CONTENT_ID_URI_BASE, taskId);
			getContentResolver().update(taskToEditUri, values, null, null);
			break;
		case EDIT_NEW_TASK:
			// This null persistance will generate a new task id by sqlite
			values.putNull(ToDo.Tasks._ID);
			// Persists the new task and returns the generated uri with newly created task id
			Uri newlyCreatedTaskUri = getContentResolver().insert(ToDo.Tasks.CONTENT_URI, values);
			// Gets the newly created task id
			taskId = ContentUris.parseId(newlyCreatedTaskUri);
			break;
		}
		
		// If datetime reminder isn't empty, persist datetime reminder
		if (dateTime != null) {
			// Reads the task from the content provider
			Task task = readTaskById(taskId);
			if (task != null) {
				// Sets notifications for the task reminders
				Intent intent = new Intent(ToDo.Actions.ACTION_DATETIME_REMINDER_BROADCAST);
				intent.putExtra(ToDo.Extras.EXTRA_TASK_ID, taskId);
				intent.putExtra(ToDo.Extras.EXTRA_TASK_TITLE, task.getTitle());
				intent.putExtra(ToDo.Extras.EXTRA_TASK_DESCRIPTION, task.getDescription());
				PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), ((Long)taskId).intValue(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
				AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
				alarmManager.set(AlarmManager.RTC_WAKEUP, dateTime.getTime(), pendingIntent);
			}
		}

		// Close this activity
		finish();
	}
	
	/**
	 * Deletes a task and returns to the parent activity (TasksListActivity).
	 * There is no need in deleting the task because it was not presisted,
	 * So jsut redirects to the parent activity (TasksListActivity).
	 */
	private void deleteTask() {
		switch (state) {
		case EDIT_EXISTING_TASK:
			// Constructs the task to delete URI
			Uri taskToDeleteUri = ContentUris.withAppendedId(ToDo.Tasks.CONTENT_ID_URI_BASE, taskId);
			// Cancels the datetime reminder notification, if any
			Intent intent = new Intent(ToDo.Actions.ACTION_DATETIME_REMINDER_BROADCAST);
			PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), ((Long)taskId).intValue(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
			AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
			alarmManager.cancel(pendingIntent);
			pendingIntent.cancel();
			// Deletes persisted task
			getContentResolver().delete(taskToDeleteUri, null, null);
			break;
		case EDIT_NEW_TASK:
			// Do nothing, the task wasn't persisted 
			break;
		}
		
		// Close this activity
		finish();
	}

	/* (non-Javadoc)
	 * @see il.ac.shenkar.todo.controller.fragments.TimePickerFragment.OnTimePickedListener#onTimePicked(int, int)
	 */
	@Override
	public void onTimePicked(int hourOfDay, int minute) {
		// Gets the datetime picked
		String textTime = hourOfDay + ":" + minute;
		String textDate = (String) textViewDate.getText();
		
		// If date wasn't choosen, just set time
		if ("".equals(textDate)) {
			textViewTime.setText(hourOfDay + ":" + minute);
		// If date was choosen, validate the datetime
		} else {
			Date dateTime = fromStringsToDate(textDate, textTime);
			// If datetime is valid, in the future
			if (dateTime != null && dateTime.compareTo(new Date()) > 0) {
				// Sets the time picked
				textViewTime.setText(hourOfDay + ":" + minute);
			// If datetime isn't valid, in the present or past
			} else {
				// Notify the user
				Toast.makeText(TaskEditorActivity.this, "Invalid time", Toast.LENGTH_SHORT).show();
			}
		}
	}

	/* (non-Javadoc)
	 * @see il.ac.shenkar.todo.controller.fragments.DatePickerFragment.OnDatePickedListener#onDatePicked(int, int, int)
	 */
	@Override
	public void onDatePicked(int year, int monthOfYear, int dayOfMonth) {
		monthOfYear += 1;
		// Gets the datetime picked
		String textDate = year + "-" + monthOfYear + "-" + dayOfMonth;
		String textTime = (String) textViewTime.getText();
		
		// If time wasn't choosen, just set time
		if ("".equals(textTime)) {
			textViewDate.setText(year + "-" + monthOfYear + "-" + dayOfMonth);
		// If time was choosen, validate the datetime
		} else {
			Date dateTime = fromStringsToDate(textDate, textTime);
			// If datetime is valid, in the future
			if (dateTime != null && dateTime.compareTo(new Date()) > 0) {
				// Sets the date picked
				textViewDate.setText(year + "-" + monthOfYear + "-" + dayOfMonth);
			// If datetime isn't valid, in the present or past
			} else {
				// Notify the user
				Toast.makeText(TaskEditorActivity.this, "Invalid date", Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	/**
	 * Convert to Strings date and time to Date
	 * 
	 * @param date
	 * @param time
	 * @return
	 */
	private Date fromStringsToDate(String date, String time) {
		try {
			return simpleDateFormatDateTime.parse(date + " " + time);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Reads a task fromt the content provider by given task id.
	 * 
	 * @param taskId
	 * @return
	 */
	private Task readTaskById(long taskId) {
		// Builds URI to the task to edit
		Uri taskToEditUri = ContentUris.withAppendedId(ToDo.Tasks.CONTENT_URI, taskId);
		// Gets cursor to the task to edit
		Cursor cursor = getContentResolver().query(taskToEditUri, ToDo.Tasks.PROJECTION, null, null, null);
		
		// If task to edit was found
		if (cursor != null && cursor.moveToFirst()) {
			// Gets task title
			String taskTitle = cursor.getString(ToDo.Tasks.COLUMN_POSITION_TITLE);
			// Gets task description
			String taskDescription = cursor.getString(ToDo.Tasks.COLUMN_POSITION_DESCRIPTION);
			// Gets task datetime
			long dateTimeMilliSec = cursor.getLong(ToDo.Tasks.COLUMN_POSITION_DATETIME);
			
			return new Task(taskId, taskTitle, taskDescription, dateTimeMilliSec);
		} else {
			return null;
		}
	}
	
	/**
	 * Gets random task from given URL.
	 * 
	 * @author ran
	 *
	 */
	private class GetRandomTaskFromUrl extends AsyncTask<URL, Void, Task> {

		/* (non-Javadoc)
		 * Runs in background thread.
		 * Gets the random task from a URL.
		 * Sends the random task to onPostExecute(Task).
		 * 
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected Task doInBackground(URL... params) {
			Task randomTask = null;
			try {
				randomTask = Utils.featchRandomTaskFromUrl(params[0]);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				Toast.makeText(TaskEditorActivity.this, "Network connection problem", Toast.LENGTH_SHORT).show();
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return randomTask;
		}
		
		/* (non-Javadoc)
		 * Runs in UI thred.
		 * Gets the random task from doInBackground(URL).
		 * Renders the random task data.
		 * 
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(Task result) {
			// Sets edit text task's title
			editTextTitle.setText(result.getTitle());
			// Sets edit text task's description
			editTextDescription.setText(result.getDescription());
		}
		
	}
	
}