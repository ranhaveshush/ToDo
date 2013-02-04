package il.ac.shenkar.todo.controller.activities;

import il.ac.shenkar.todo.R;
import il.ac.shenkar.todo.config.ToDo;
import il.ac.shenkar.todo.controller.fragments.DatePickerFragment;
import il.ac.shenkar.todo.controller.fragments.DatePickerFragment.OnDatePickedListener;
import il.ac.shenkar.todo.controller.fragments.TimePickerFragment;
import il.ac.shenkar.todo.controller.fragments.TimePickerFragment.OnTimePickedListener;
import il.ac.shenkar.todo.model.dao.tasks.TasksDAOFactory;
import il.ac.shenkar.todo.model.dao.tasks.logic.ITasksDAO;
import il.ac.shenkar.todo.utilities.DateTimeUtils;
import il.ac.shenkar.todo.utilities.RandomTaskUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Locale;

import org.json.JSONException;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.client.util.DateTime;
import com.google.api.services.tasks.model.Task;

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
	 * Fragment's tag due date picker dialog fragment.
	 */
	private static final String FRAGMENT_TAG_DUE_DATE_PICKER = "DueDatePicker";
	
	/**
	 * Fragment's tag reminder date picker dialog fragment.
	 */
	private static final String FRAGMENT_TAG_REMINDER_DATE_PICKER = "ReminderDatePicker";
	
	/**
	 * Fragment's tag reminder time picker dialog fragment.
	 */
	private static final String FRAGMENT_TAG_REMINDER_TIME_PICKER = "ReminderTimePicker";
	
	/**
	 * The threshold of the autocomplete address.
	 */
	private static final int AUTO_COMPLETE_TEXT_VIEW_THRESHOLD = 3;
	
	/**
	 * The address locale.
	 */
	private static final Locale LOCALE = new Locale("IW", "IL");
	
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
     * Holds the task to edit info.
     */
    private Task taskToEdit = new Task();
    
    /**
     * Edit text represents the task's title.
     */
    private EditText editTextTitle = null;
    
    /**
     * Edit text represents the task's notes.
     */
    private EditText editTextNotes = null;
    
    /**
     * Text view represents the task's due date.
     */
    private TextView textViewDueDate = null;
    
    /**
     * Text view represents the task's reminder datetime.
     */
    private TextView textViewReminderDateTime = null;

	/**
	 * Autocomplete text view represents the task's reminder location (address).
	 */
	private AutoCompleteTextView autoCompleteTextViewAddress = null;

	/**
	 * Autocomplete dropdown list adapter.
	 */
	private ArrayAdapter<String> autoCompleteAdapter = null;
    
    /**
     * Holds the dao to the tasks resouce.
     */
    private ITasksDAO tasksDAO = null;

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// Logger
		Log.d(TAG, "onCreate(Bundle savedInstanceState)");
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task_editor);
		
		tasksDAO = TasksDAOFactory.getFactory(TaskEditorActivity.this, TasksDAOFactory.SQLITE).getTasksDAO();
		
		// Gets all the widgets
		editTextTitle = (EditText) findViewById(R.id.edit_text_task_title);
		editTextTitle.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				TaskEditorActivity.this.setTitle(s);
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}
			@Override
			public void afterTextChanged(Editable s) {
			}
		});
		editTextNotes = (EditText) findViewById(R.id.edit_text_task_notes);
		textViewDueDate = (TextView) findViewById(R.id.text_view_task_due_date);
		textViewDueDate.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				DialogFragment datePickerFragment = new DatePickerFragment();
				datePickerFragment.show(getSupportFragmentManager(), FRAGMENT_TAG_DUE_DATE_PICKER);
			}
		});
		textViewReminderDateTime = (TextView) findViewById(R.id.text_view_task_reminder_datetime);
		textViewReminderDateTime.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// Calls date picker fragment dialog
				DialogFragment datePickerFragment = new DatePickerFragment();
				datePickerFragment.show(getSupportFragmentManager(), FRAGMENT_TAG_REMINDER_DATE_PICKER);
				// Calls time picker fragment dialog
				DialogFragment timePickerFragment = new TimePickerFragment();
				timePickerFragment.show(getSupportFragmentManager(), FRAGMENT_TAG_REMINDER_TIME_PICKER);
			}
		});
		
		autoCompleteAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line);

		autoCompleteTextViewAddress = (AutoCompleteTextView) findViewById(R.id.auto_complete_text_view_task_reminder_location);
		autoCompleteTextViewAddress.setThreshold(AUTO_COMPLETE_TEXT_VIEW_THRESHOLD);
		autoCompleteTextViewAddress.setAdapter(autoCompleteAdapter);
		autoCompleteTextViewAddress.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				String toAutoCompleteAddressStr = s.toString();
				// If the address string isn't empty and longer then threshold, autocomplete
				if (!"".equals(toAutoCompleteAddressStr) && toAutoCompleteAddressStr.length() >= AUTO_COMPLETE_TEXT_VIEW_THRESHOLD) {
					new AddressesAutoCompleteTask().execute(new String[] { toAutoCompleteAddressStr });
				}
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}
			@Override
			public void afterTextChanged(Editable s) {
			}
		});
		autoCompleteTextViewAddress.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				String selectedAddressStr = ((TextView) view).getText().toString();
				autoCompleteTextViewAddress.setText(selectedAddressStr);
			}
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// Do nothing.
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
		
		// Sets task to edit task list id
		String taskListId = extras.getString(ToDo.Extras.EXTRA_TASK_LIST_ID);
		taskToEdit.set(ToDo.Tasks.COLUMN_NAME_TASKLIST_ID, taskListId);
		
		// Operates according to the current state
		switch (state) {
		case EDIT_EXISTING_TASK:
			// Sets task to edit client id
			long taskClientId = extras.getLong(ToDo.Extras.EXTRA_TASK_ID);
			// Gets the task to edit from local db
			taskToEdit = tasksDAO.get(taskClientId);
			// Views the fields of the task to edit
			viewTask(taskToEdit);
			break;
		case EDIT_NEW_TASK:
			// Gets the new task's title
			String taskToEditTitle = extras.getString(ToDo.Extras.EXTRA_TASK_TITLE);
			// Sets edit text task's title
			editTextTitle.setText(taskToEditTitle);
			break;
		}
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Logger
		Log.d(TAG, "onCreateOptionsMenu(Menu menu)");
		
		getMenuInflater().inflate(R.menu.activity_task_editor, menu);
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
		case R.id.menu_item_save_task:
			saveTask();
			return true;
		case R.id.menu_item_random_task:
			URL url = null;
			try {
				url = new URL(RandomTaskUtils.URL_ADDRESS);
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
	
	/* (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onStart()
	 */
	@Override
	protected void onStart() {
		super.onStart();
		
		// Logger
		Log.d(TAG, "onStart()");
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onResume()
	 */
	@Override
	protected void onResume() {
		super.onResume();
		
		// Logger
		Log.d(TAG, "onResume()");
	}

	/**
	 * View renders a given task fields.
	 * 
	 * @param task	Task to view
	 */
	private void viewTask(Task task) {
		// Logger
		Log.d(TAG, "viewTask(Task taskToEdit)");
		
		String taskTitle = task.getTitle();
		editTextTitle.setText(taskTitle);
		String taskNotes = task.getNotes();
		editTextNotes.setText(taskNotes);
		DateTime taskDue = task.getDue();
		textViewDueDate.setText(DateTimeUtils.getDateFromDateTime(taskDue));
		String datetimeReminder = (String) task.get(ToDo.Tasks.COLUMN_NAME_DATETIME_REMINDER);
		textViewReminderDateTime.setText(datetimeReminder);
		String locationReminder = (String) task.get(ToDo.Tasks.COLUMN_NAME_LOCATION_REMINDER);
		autoCompleteTextViewAddress.setText(locationReminder);
	}

	/**
	 * Saves a new task and returns to the parent activity (TasksListActivity).
	 */
	private void saveTask() {
		// Logger
		Log.d(TAG, "saveTask()");
		
		// Gets the task title
		String textTitle = editTextTitle.getText().toString();
		// If title text is empty, notify user
		if (TextUtils.isEmpty(textTitle)) {
			Toast.makeText(TaskEditorActivity.this, "Failed to save task, title is empty", Toast.LENGTH_SHORT).show();
			return;
		}
		taskToEdit.setTitle(textTitle);
		
		// Gets the task notes
		String textNotes = editTextNotes.getText().toString();
		// If notes text isn't empty, persist it
		if (!TextUtils.isEmpty(textNotes)) {
			taskToEdit.setNotes(textNotes.toString());
		}
		
		// Gets the task locaiton reminder
		String textLocationReminder = autoCompleteTextViewAddress.getText().toString();
		// If out of focus, persist address
		if (!TextUtils.isEmpty(textLocationReminder)) {
			taskToEdit.set(ToDo.Tasks.COLUMN_NAME_LOCATION_REMINDER, textLocationReminder);
		}

		switch (state) {
		case EDIT_EXISTING_TASK:
			// Updates the task to save
			tasksDAO.update(taskToEdit);
			// Notify the user, task updated
			Toast.makeText(TaskEditorActivity.this, "Task updated", Toast.LENGTH_SHORT).show();
			break;
		case EDIT_NEW_TASK:
			// Inserts the task to save
			tasksDAO.insert(taskToEdit);
			// Notify the user, task updated
			Toast.makeText(TaskEditorActivity.this, "Task created", Toast.LENGTH_SHORT).show();
			break;
		}
		
		// Syncs with the cloud
		// FIXME: uncomment the sync utils
		//SyncUtils.syncWithGoogleTasks();
	}
	
	/**
	 * Deletes a task and returns to the parent activity (TasksListActivity).
	 * There is no need in deleting the task because it was not presisted,
	 * So jsut redirects to the parent activity (TasksListActivity).
	 */
	private void deleteTask() {
		// Logger
		Log.d(TAG, "deleteTask()");
		
		switch (state) {
		case EDIT_EXISTING_TASK:
			// Deletes the edit task
			tasksDAO.delete(taskToEdit);
			// Syncs with the cloud
			//SyncUtils.syncWithGoogleTasks();
			// Notify the user, task updated
			Toast.makeText(TaskEditorActivity.this, "Task deleted", Toast.LENGTH_SHORT).show();
			// Close this activity
			finish();
			break;
		case EDIT_NEW_TASK:
			// Do nothing, the task wasn't persisted
			// Close this activity
			finish();
			break;
		}
	}

	/* (non-Javadoc)
	 * @see il.ac.shenkar.todo.controller.fragments.TimePickerFragment.OnTimePickedListener#onTimePicked(int, int)
	 */
	@Override
	public void onTimePicked(String fragmentTag, int hourOfDay, int minute) {
		// Logger
		Log.d(TAG, "(String fragmentTag, int hourOfDay, int minute)");
		
		// If returned from reminder time picker dialog fragment
		if (FRAGMENT_TAG_REMINDER_TIME_PICKER.equals(fragmentTag)) {
			// Sets the text view tags
			textViewReminderDateTime.setTag(R.id.TAG_DATETIME_HOUR, hourOfDay);
			textViewReminderDateTime.setTag(R.id.TAG_DATETIME_MINUTE, minute);
		}
	}

	/* (non-Javadoc)
	 * @see il.ac.shenkar.todo.controller.fragments.DatePickerFragment.OnDatePickedListener#onDatePicked(int, int, int)
	 */
	@Override
	public void onDatePicked(String fragmentTag, int year, int monthOfYear, int dayOfMonth) {
		// Logger
		Log.d(TAG, "onDatePicked(String fragmentTag, int year, int monthOfYear, int dayOfMonth)");
		
		// If returned from due date picker dialog fragment
		if (FRAGMENT_TAG_DUE_DATE_PICKER.equals(fragmentTag)) {
			String dateStr = DateTimeUtils.formatDateToString(year, monthOfYear, dayOfMonth);
			textViewDueDate.setText("Due " + dateStr);
			DateTime due = DateTimeUtils.fromStringToDateTime(dateStr);
			taskToEdit.setDue(due);
		// If returned from reminder date picker dialog fragment
		} else if (FRAGMENT_TAG_REMINDER_DATE_PICKER.equals(fragmentTag)) {
			int hourOfDay = (Integer) textViewReminderDateTime.getTag(R.id.TAG_DATETIME_HOUR);
			int minute = (Integer) textViewReminderDateTime.getTag(R.id.TAG_DATETIME_MINUTE);
			String timeStr = DateTimeUtils.formatTimeToString(hourOfDay, minute);
			String dateStr = DateTimeUtils.formatDateToString(year, monthOfYear, dayOfMonth);
			textViewReminderDateTime.setText("Remind me at " + timeStr + " " + dateStr);
			DateTime dateTime = DateTimeUtils.fromStringsToDateTime(dateStr, timeStr);
			taskToEdit.set(ToDo.Tasks.COLUMN_NAME_DATETIME_REMINDER, dateTime.toStringRfc3339());
		}
	}
	
	/**
	 * Gets random task from given URL.
	 * 
	 * @author ran
	 *
	 */
	private class GetRandomTaskFromUrl extends AsyncTask<URL, Void, Task> {
		
		/**
		 * Logger's tag.
		 */
		private static final String TAG = "GetRandomTaskFromUrl: TaskEditorActivity";
		
		/* (non-Javadoc)
		 * Runs in background thread.
		 * Gets the random task from a URL.
		 * Sends the random task to onPostExecute(Task).
		 * 
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected Task doInBackground(URL... urls) {
			// Logger
			Log.d(TAG, "doInBackground(URL... urls)");
			
			Task randomTask = null;
			try {
				randomTask = RandomTaskUtils.featchRandomTaskFromUrl(urls[0]);
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
		protected void onPostExecute(Task task) {
			// Logger
			Log.d(TAG, "onPostExecute(Task task)");
						
			// Sets edit text task's title
			editTextTitle.setText(task.getTitle());
			// Sets edit text task's notes
			editTextNotes.setText(task.getNotes());
		}
		
	}
	
	/**
	 * Auto complete async task to geocode addresses.
	 * 
	 * @author ran
	 * 
	 */
	private class AddressesAutoCompleteTask extends AsyncTask<String, Void, List<Address>> {
		
		/**
		 * Logger's tag.
		 */
		private static final String TAG = "AddressesAutoCompleteTask: TaskEditorActivity";

		/**
		 * Max results per autocomplete.
		 */
		private static final int MAX_RESULTS = 3;

		@Override
		protected List<Address> doInBackground(String... addresses) {
			// Logger
			Log.d(TAG, "doInBackground(String... addresses)");
			
			String toAutoCompleteAddressStr = addresses[0];
				try {
					return new Geocoder(TaskEditorActivity.this, LOCALE)
							.getFromLocationName(toAutoCompleteAddressStr, MAX_RESULTS);
				} catch (IOException e) {
					e.printStackTrace();
				}
			return null;
		}
		@Override
		protected void onPostExecute(List<Address> autoCompleteAddressesSuggestions) {
			// Logger
			Log.d(TAG, "onPostExecute(List<Address> autoCompleteAddressesSuggestions)");
			
			autoCompleteAdapter.clear();
			for (Address address : autoCompleteAddressesSuggestions) {
				String addressLine = (address.getMaxAddressLineIndex() > 0) ? address.getAddressLine(0) : "";
				String addressLocality = (null != address.getLocality()) ? address.getLocality() : "";
				String addressCountry = (null != address.getCountryName()) ? address.getCountryName() : "";
				String addressText = String.format("%s, %s, %s", addressLine, addressLocality, addressCountry);
				autoCompleteAdapter.add(addressText);
			}
			autoCompleteAdapter.notifyDataSetChanged();
		}
	}
	
//	/**
//	 * Gets the address object out of the addres string.
//	 * 
//	 * @author ran
//	 * 
//	 */
//	private class GeocodeTask extends AsyncTask<String, Void, Address> {
//		
//		/**
//		 * Logger's tag.
//		 */
//		private static final String TAG = "GeocodeTask: TaskEditorActivity";
//
//		/**
//		 * Max results per autocomplete.
//		 */
//		private static final int MAX_RESULTS = 1;
//
//		@Override
//		protected Address doInBackground(String... addresses) {
//			// Logger
//			Log.d(TAG, "doInBackground(String... addresses)");
//			
//			String addressStr = addresses[0];
//				try {
//					List<Address> addressesList = new Geocoder(TaskEditorActivity.this, LOCALE)
//					.getFromLocationName(addressStr, MAX_RESULTS);
//					return (addressesList.isEmpty()) ? null : addressesList.get(0);
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			return null;
//		}
//		@Override
//		protected void onPostExecute(Address address) {
//			// Logger
//			Log.d(TAG, "onPostExecute(Address address)");
//			
//			// If geocoding succedded
//			if (address != null) {
//				// Sets the address
//				autoCompleteTextViewAddress.setTag(R.id.TAG_LOCATION_ADDRESS, address);
//				// Notify the user geocoding succedded
//				Toast.makeText(TaskEditorActivity.this, "Valid location address", Toast.LENGTH_SHORT).show();
//			// If geocoding failed
//			} else {
//				// Notify the user geocoding failed
//				Toast.makeText(TaskEditorActivity.this, "Invalid location address", Toast.LENGTH_SHORT).show();
//			}
//		}
//	}
	
//	/**
//	 * Reverse geocoding, gets latitude and longitude (the order is importent).
//	 * Returns the address in that lat,lng.
//	 * 
//	 * @author ran
//	 * 
//	 */
//	private class ReverseGeocodeTask extends AsyncTask<Double, Void, Address> {
//		
//		/**
//		 * Logger's tag.
//		 */
//		private static final String TAG = "ReverseGeocodeTask: TaskEditorActivity";
//
//		/**
//		 * Max results per autocomplete.
//		 */
//		private static final int MAX_RESULTS = 1;
//
//		@Override
//		protected Address doInBackground(Double... coords) {
//			// Logger
//			Log.d(TAG, "doInBackground(Double... coords)");
//			
//			double lat = coords[0];
//			double lng = coords[1];
//				try {
//					List<Address> addressesList = new Geocoder(TaskEditorActivity.this, LOCALE)
//					.getFromLocation(lat, lng, MAX_RESULTS);
//					return (addressesList.isEmpty()) ? null : addressesList.get(0);
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			return null;
//		}
//		@Override
//		protected void onPostExecute(Address address) {
//			// Logger
//			Log.d(TAG, "onPostExecute(Address address)");
//			
//			// If reverse geocoding succedded
//			if (address != null) {
//				// Saves lat lng
//				String addressLine = (address.getMaxAddressLineIndex() > 0) ? address.getAddressLine(0) : "";
//				String addressLocality = (null != address.getLocality()) ? address.getLocality() : "";
//				String addressCountry = (null != address.getCountryName()) ? address.getCountryName() : "";
//				String addressText = String.format("%s, %s, %s", addressLine, addressLocality, addressCountry);
//				autoCompleteTextViewAddress.setText(addressText);
//				// Notify the user geocoding succedded
//				Toast.makeText(TaskEditorActivity.this, "Valid location address", Toast.LENGTH_SHORT).show();
//			// If reverse geocoding failed
//			} else {
//				// Notify the user geocoding failed
//				Toast.makeText(TaskEditorActivity.this, "Invalid location address", Toast.LENGTH_SHORT).show();
//			}
//		}
//	}
	
}