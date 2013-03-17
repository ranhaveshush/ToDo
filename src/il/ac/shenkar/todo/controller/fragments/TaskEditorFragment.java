/**
 * 
 */
package il.ac.shenkar.todo.controller.fragments;

import il.ac.shenkar.todo.R;
import il.ac.shenkar.todo.config.ToDo;
import il.ac.shenkar.todo.controller.fragments.DatePickerFragment.DatePickerListener;
import il.ac.shenkar.todo.controller.fragments.DateTimePickerFragment.DateTimePickedListener;
import il.ac.shenkar.todo.controller.fragments.LocationPickerFragment.LocationPickerListener;
import il.ac.shenkar.todo.model.dao.tasks.TasksDAOFactory;
import il.ac.shenkar.todo.model.dao.tasks.logic.ITasksDAO;
import il.ac.shenkar.todo.utilities.DateTimeUtils;
import il.ac.shenkar.todo.utilities.SyncUtils;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.google.api.client.util.DateTime;
import com.google.api.services.tasks.model.Task;

/**
 * @author ran
 *
 */
public class TaskEditorFragment extends SherlockFragment 
	implements DateTimePickedListener, DatePickerListener, LocationPickerListener {
	
	/**
	 * Task Editor states enum.
	 * Inner class representing all the possiable state the
	 * task editor may be in.
	 * 
	 * @author ran
	 *
	 */
	public enum State {
		VIEW_TASK,
		EDIT_TASK
    }
	
	/**
	 * Logger's tag.
	 */
	private static final String TAG = "TaskEditorFragment";
	
	/**
	 * Holds the task editor fragment state.
	 */
	private static final String STATE = "State";
	
	/**
	 * Holds the task client id state.
	 */
	private static final String STATE_TASK_CLIENT_ID = "TaskClientId";
	
	/**
	 * Fragment's tag due date picker dialog fragment.
	 */
	private static final String FRAGMENT_TAG_DUE_DATE_PICKER = "DueDatePicker";
	
	/**
	 * Fragment's tag reminder datetime picker dialog fragment.
	 */
	private static final String FRAGMENT_TAG_REMINDER_DATETIME_PICKER = "ReminderDateTimePicker";
	
	/**
	 * Fragment's tag reminder location picker dialog fragment.
	 */
	protected static final String FRAGMENT_TAG_REMINDER_LOCATION_PICKER = "ReminderLocationPicker";
	
	/**
	 * The address locale.
	 */
	private static final Locale LOCALE = new Locale("IW", "IL");
    
    /**
     * Task editor state.
     */
    public static State mState;
    
    /**
     * Holds the task to view or edit info.
     */
    private Task mTask = null;
    
    /**
	 * Holds the task client id to task content. 
	 */
	private long mTaskClientId;
    
	/**
	 * Holds the hosting activity.
	 */
	private FragmentActivity hostingActivity = null;
	
	/**
	 * Checkbox represents the task's completed.
	 */
	private CheckBox checkBoxTaskCompleted = null;
	
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
	 * Text view represents the task's reminder location (address).
	 */
	private TextView textViewReminderLocation = null;
    
    /**
     * Holds the dao to the tasks resouce.
     */
    private ITasksDAO tasksDAO = null;

    /**
	 * Holds the hosting activity as listener.
	 */
	private TaskEditorListener mListener = null;
    
    /**
     * Listener interface for the fragment task editor.
	 * Container activity must implement this interface.
     * 
     * @author ran
     *
     */
    public interface TaskEditorListener {
    	/**
    	 * Task changed event.
		 * Container activity must implement this method,
		 * To change the fragment task editor content when promoted to.
		 * 
    	 * @param taskClientId		Long represents the task to edit client id
    	 */
    	public void onTaskChanged(long taskClientId);
    }
    
    /**
     * Instantiates a new instance of task editor fragment.
     * State should be view or edit existing task
     * by given task client id.
     * 
     * @param state				State enum represents the view or edit state the task editor fragment may be in
     * @param taskClientId		Long represents the task client identifier
     * @return					Fragment new task editor fragment
     */
    public static Fragment newInstance(State state, long taskClientId) {
    	// Logger
    	Log.d(TAG, "newInstance(State state, long taskClientId)");
    	
    	// Validates state arguemnt
    	if (state != State.VIEW_TASK && state != State.EDIT_TASK) {
    		throw new IllegalArgumentException(TAG
    				+ " newInstance(State, long), state should be VIEW_TASK or EDIT_TASK.");
    	}
    	
    	TaskEditorFragment newFragment = new TaskEditorFragment();
    	
    	Bundle args = new Bundle();
    	args.putSerializable(STATE, state);
    	args.putLong(STATE_TASK_CLIENT_ID, taskClientId);
    	newFragment.setArguments(args);
    	
    	return newFragment;
    }

	/* (non-Javadoc)
	 * @see com.actionbarsherlock.app.SherlockFragment#onAttach(android.app.Activity)
	 */
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		// Logger
		Log.d(TAG, "onAttach(Activity activity)");
		
		// Ensures hosting activity implements the Listener interface
		try {
			mListener = (TaskEditorListener) activity;
			// Gets the hosting activity
			hostingActivity = getActivity();
		} catch (Exception e) {
			throw new ClassCastException(activity.toString() + " must implement the TaskEditorListener");
		}
	}
	
	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onSaveInstanceState(android.os.Bundle)
	 */
	@Override
	public void onSaveInstanceState(Bundle outState) {
		// Logger
		Log.d(TAG, "onSaveInstanceState(Bundle outState)");
		
		// Saves the task editor fragment current state
		outState.putSerializable(STATE, mState);
		outState.putLong(STATE_TASK_CLIENT_ID, mTaskClientId);
		
		// Calls the superclass so it can save the view hierarchy state
		super.onSaveInstanceState(outState);
	}
	
	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Logger
		Log.d(TAG, "onCreate(Bundle savedInstanceState)");
		
		// Indicates that this fragment has action bar items
		setHasOptionsMenu(true);
		
		// During creation, if arguments have been supplied to the fragment
	    // then parse those out
		Bundle args = getArguments();
		if (args != null) {
			mState = (State) args.getSerializable(STATE);
			mTaskClientId = args.getLong(STATE_TASK_CLIENT_ID);
		}
		
		// If recreating a previously destroyed instance
		if (savedInstanceState != null) {
			// Restore value of members from saved state
			mState = (State) savedInstanceState.getSerializable(STATE);
			mTaskClientId = savedInstanceState.getLong(STATE_TASK_CLIENT_ID);
		}
		
		// Gets the dao to the tasks resource
		tasksDAO = TasksDAOFactory.getFactory(hostingActivity, TasksDAOFactory.SQLITE).getTasksDAO();
		
		// Operates according to the current state
		switch (mState) {
		case VIEW_TASK:
		case EDIT_TASK:
			// Gets the task from local db
			mTask = tasksDAO.get(mTaskClientId);
			// Sets the hosting activity title to the task title
			hostingActivity.setTitle(mTask.getTitle());
			break;
		default:
			throw new IllegalArgumentException(this.toString()
    				+ " newInstance(State, long), invalid State argument value.");
		}
		
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Logger
		Log.d(TAG, "onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)");
		
		// If the container view is null,
		// There is no need for us to create the fragment view
		if (container == null) {
			return null;
		// If the container view isn't null,
		// There is need for us to create the fragment view
		} else {
			return inflater.inflate(R.layout.fragment_task_editor , null);
		}
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onActivityCreated(android.os.Bundle)
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		// Logger
		Log.d(TAG, "onActivityCreated(Bundle savedInstanceState)");
		
		// Gets all the widgets
		checkBoxTaskCompleted = (CheckBox) hostingActivity.findViewById(R.id.checkbox_task_completed);
		checkBoxTaskCompleted.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					checkBoxTaskCompleted.setTag(DateTimeUtils.getDateTimeNow());
				} else {
					checkBoxTaskCompleted.setTag(null);
				}
			}
		});
		editTextTitle = (EditText) hostingActivity.findViewById(R.id.edit_text_task_title);
		editTextTitle.setText(hostingActivity.getTitle());
		editTextTitle.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				hostingActivity.setTitle(s);
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}
			@Override
			public void afterTextChanged(Editable s) {
			}
		});
		editTextNotes = (EditText) hostingActivity.findViewById(R.id.edit_text_task_notes);
		textViewDueDate = (TextView) hostingActivity.findViewById(R.id.text_view_task_due_date);
		textViewDueDate.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// Calss date picker fragment dialog
				DialogFragment datePickerFragment = new DatePickerFragment();
				datePickerFragment.show(hostingActivity.getSupportFragmentManager(), FRAGMENT_TAG_DUE_DATE_PICKER);
			}
		});
		textViewReminderDateTime = (TextView) hostingActivity.findViewById(R.id.text_view_task_reminder_datetime);
		textViewReminderDateTime.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// Calls datetime picker fragment dialog
				DialogFragment timePickerFragment = new DateTimePickerFragment();
				timePickerFragment.show(hostingActivity.getSupportFragmentManager(), FRAGMENT_TAG_REMINDER_DATETIME_PICKER);
			}
		});
		
		textViewReminderLocation = (TextView) hostingActivity.findViewById(R.id.text_view_task_reminder_location);
		textViewReminderLocation.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// Calls location picker fragment dialog
				String location = textViewReminderLocation.getText().toString();
				DialogFragment locationPickerFragment = null;
				// If locaiton string isn't empty or null
				if (!TextUtils.isEmpty(location)) {
					locationPickerFragment = LocationPickerFragment.newInstance(location);
				// If locaiton string is empty or null
				} else {
					locationPickerFragment = new LocationPickerFragment();
				}
				locationPickerFragment.show(hostingActivity.getSupportFragmentManager(), FRAGMENT_TAG_REMINDER_LOCATION_PICKER);
			}
		});
		
		// Operates according to the current state
		switch (mState) {
		case EDIT_TASK:
			// Views the fields of the task
			viewTask(mTask);
			break;
		case VIEW_TASK:
			break;
		}
	}
	
	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onResume()
	 */
	@Override
	public void onResume() {
		super.onResume();
		
		// Logger
		Log.d(TAG, "onResume()");
		
		setMenuVisibility(true);
	}
	
	/* (non-Javadoc)
	 * @see com.actionbarsherlock.app.SherlockFragmentActivity#onPause()
	 */
	@Override
	public void onPause() {
		super.onPause();
		
		// Logger
		Log.d(TAG, "onPause()");
		
		setMenuVisibility(false);
	}
	
	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onStop()
	 */
	@Override
	public void onStop() {
		super.onStop();
		
		// Logger
		Log.d(TAG, "onStop()");
	}

	/* (non-Javadoc)
	 * @see com.actionbarsherlock.app.SherlockFragment#onCreateOptionsMenu
	 * (com.actionbarsherlock.view.Menu, com.actionbarsherlock.view.MenuInflater)
	 */
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		
		// Logger
		Log.d(TAG, "onCreateOptionsMenu(Menu menu, MenuInflater inflater)");
		
		inflater.inflate(R.menu.fragment_task_editor, menu);
	}

	/* (non-Javadoc)
	 * @see com.actionbarsherlock.app
	 * .SherlockFragment#onOptionsItemSelected(com.actionbarsherlock.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Logger
		Log.d(TAG, "onOptionsItemSelected(MenuItem item)");
		
		switch (item.getItemId()) {
		case R.id.menu_item_save_task:
			saveTask(mTask);
			return true;
		case R.id.menu_item_delete_task:
			deleteTask(mTask);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * View renders a given task fields.
	 * 
	 * @param task	Task to view
	 */
	private void viewTask(Task task) {
		// Logger
		Log.d(TAG, "viewTask(Task task)");
		
		DateTime taskCompleted = task.getCompleted();
		if (taskCompleted != null) {
			checkBoxTaskCompleted.setChecked(true);
		} else {
			checkBoxTaskCompleted.setChecked(false);
		}
		String taskNotes = task.getNotes();
		editTextNotes.setText(taskNotes);
		DateTime taskDue = task.getDue();
		if (taskDue != null) {
			String dateStr = DateTimeUtils.getDateStrFromDateTime(taskDue);
			textViewDueDate.setText("Due " + dateStr);
		} else {
			textViewDueDate.setText(null);
		}
		String dateTimeRFC3999 = (String) task.get(ToDo.Tasks.COLUMN_NAME_DATETIME_REMINDER);
		if (dateTimeRFC3999 != null) {
			String dateTimeStr = DateTimeUtils.fromDateTimeRFC3999ToDateTimeStr(dateTimeRFC3999);
			textViewReminderDateTime.setText("Remind me at " + dateTimeStr);
		} else {
			textViewReminderDateTime.setText(null);
		}
		String locationReminder = (String) task.get(ToDo.Tasks.COLUMN_NAME_LOCATION_REMINDER);
		textViewReminderLocation.setText(locationReminder);
	}

	/**
	 * Saves a new task.
	 * 
	 * @param task 		Task to save
	 */
	private void saveTask(Task task) {
		// Logger
		Log.d(TAG, "saveTask()");
		
		// Gets the task title
		String textTitle = hostingActivity.getTitle().toString();
		// If title text is empty, notify user
		if (TextUtils.isEmpty(textTitle)) {
			Toast.makeText(hostingActivity, "Task's title is empty", Toast.LENGTH_SHORT).show();
			return;
		}
		task.setTitle(textTitle);
		
		// Gets the task notes
		String textNotes = editTextNotes.getText().toString();
		// If notes text isn't empty, persist it
		if (!TextUtils.isEmpty(textNotes)) {
			task.setNotes(textNotes.toString());
		}
		
		// Gets the task completed
		if (checkBoxTaskCompleted.isChecked()) {
			task.setStatus(ToDo.Tasks.COLUMN_STATUS_COMPLETED);
			task.setCompleted((DateTime) checkBoxTaskCompleted.getTag());
		} else {
			task.setStatus(ToDo.Tasks.COLUMN_STATUS_NEEDS_ACTION);
			task.setCompleted(null);
		}

		// Operates according to the current state
		switch (mState) {
		case EDIT_TASK:
			// Updates the task to save
			task.setUpdated(DateTimeUtils.getDateTimeNow());
			tasksDAO.update(task);
			// Notify the user, task updated
			Toast.makeText(hostingActivity, "Task updated", Toast.LENGTH_SHORT).show();
			break;
		case VIEW_TASK:
			break;
		}

		
		String dateTimeStr = textViewReminderDateTime.getText().toString();
		// If the date time reminder is not empty or null
		if (!TextUtils.isEmpty(dateTimeStr)) {
			String[] stringArray = dateTimeStr.split(" ");
			String timeStr = stringArray[3];
			String dateStr = stringArray[4];
			DateTime dateTime = DateTimeUtils.fromDateTimeStrsToDateTime(dateStr, timeStr);
			DateTime dateTimeZoned = DateTimeUtils.getTimeZonedDateTime(dateTime);
			// Sets a notification for the datetime reminder
			Intent dateTimeIntent = new Intent(ToDo.Actions.ACTION_DATETIME_REMINDER_BROADCAST);
			dateTimeIntent.putExtra(ToDo.Extras.EXTRA_TASK_CLIENT_ID, mTaskClientId);
			dateTimeIntent.putExtra(ToDo.Extras.EXTRA_TASK_TITLE, hostingActivity.getTitle());
			dateTimeIntent.putExtra(ToDo.Extras.EXTRA_TASK_NOTES, editTextNotes.getText().toString());
			PendingIntent dateTimePendingIntent = PendingIntent.getBroadcast(
					hostingActivity.getApplicationContext(),
					((Long)mTaskClientId).intValue(),
					dateTimeIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			AlarmManager alarmManager = (AlarmManager) hostingActivity.getSystemService(Context.ALARM_SERVICE);
			alarmManager.set(AlarmManager.RTC_WAKEUP, dateTimeZoned.getValue(), dateTimePendingIntent);
		// If the date time reminder is empty or null
		} else {
			// Cancels the datetime reminder notification, if any
			Intent dateTimeIntent = new Intent(ToDo.Actions.ACTION_DATETIME_REMINDER_BROADCAST);
			PendingIntent dateTimePendingIntent = PendingIntent.getBroadcast(
					hostingActivity.getApplicationContext(),
					((Long)mTaskClientId).intValue(),
					dateTimeIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			AlarmManager alarmManager = (AlarmManager) hostingActivity.getSystemService(Context.ALARM_SERVICE);
			alarmManager.cancel(dateTimePendingIntent);
			dateTimePendingIntent.cancel();
		}
		

		String locationStr = textViewReminderLocation.getText().toString();
		// If the location reminder is not null
		if (!TextUtils.isEmpty(locationStr)) {
			// Sets a notification for the location reminder
			new GeocodeTask().execute(locationStr);
		// If the location reminder is null
		} else {
			// Cancels the location reminder notification, if any
			Intent locationIntent = new Intent(ToDo.Actions.ACTION_LOCATION_REMINDER_BROADCAST);
			PendingIntent locationPendingIntent = PendingIntent.getBroadcast(
					hostingActivity.getApplicationContext(),
					((Long)mTaskClientId).intValue(),
					locationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			LocationManager locationManager = (LocationManager) hostingActivity.getSystemService(Context.LOCATION_SERVICE);
			locationManager.removeProximityAlert(locationPendingIntent);
			locationPendingIntent.cancel();
		}
		
		// Syncs with the cloud
		SyncUtils.syncWithGoogleTasks(hostingActivity.getApplicationContext());
	}
	
	/**
	 * Deletes a task and returns to the parent activity (TasksListActivity).
	 * There is no need in deleting the task because it was not presisted,
	 * So jsut redirects to the parent activity (TasksListActivity).
	 * 
	 * @param task 		Task to delete
	 */
	private void deleteTask(Task task) {
		// Logger
		Log.d(TAG, "deleteTask(Task task)");
		
		// Operates according to the current state
		switch (mState) {
		case VIEW_TASK:
		case EDIT_TASK:
			// Deletes the edit task
			tasksDAO.delete(task);
			// Syncs with the cloud
			SyncUtils.syncWithGoogleTasks(hostingActivity.getApplicationContext());
			// Notify the user, task updated
			Toast.makeText(hostingActivity, "Task deleted", Toast.LENGTH_SHORT).show();
			break;
		}
		
		// Closes the activity
		hostingActivity.finish();
	}

	/* (non-Javadoc)
	 * @see il.ac.shenkar.todo.controller.fragments.DatePickerFragment
	 * .DatePickerListener#onDateOn(java.lang.String, int, int, int)
	 */
	@Override
	public void onDateOn(String fragmentTag, int year, int monthOfYear, int dayOfMonth) {
		// Logger
		Log.d(TAG, "onDateOn(String fragmentTag, int year, int monthOfYear, int dayOfMonth)");
		
		// If returned from due date picker dialog fragment
		if (FRAGMENT_TAG_DUE_DATE_PICKER.equals(fragmentTag)) {
			String dateStr = DateTimeUtils.formatDateParamsToDateStr(year, monthOfYear, dayOfMonth);
			textViewDueDate.setText("Due " + dateStr);
			DateTime due = DateTimeUtils.fromDateStrToDateTime(dateStr);
			mTask.setDue(due);
		}
	}
	
	/* (non-Javadoc)
	 * @see il.ac.shenkar.todo.controller.fragments.DatePickerFragment
	 * .DatePickerListener#onDateOff(java.lang.String)
	 */
	@Override
	public void onDateOff(String fragmentTag) {
		// Logger
		Log.d(TAG, "onDateOff(String fragmentTag)");
		
		// If returned from due date picker dialog fragment
		if (FRAGMENT_TAG_DUE_DATE_PICKER.equals(fragmentTag)) {
			textViewDueDate.setText(null);
			mTask.setDue(null);
		}
	}
	
	/* (non-Javadoc)
	 * @see il.ac.shenkar.todo.controller.fragments.DateTimePickerFragment
	 * .DateTimePickedListener#onDateTimeOn(java.lang.String, int, int, int, int, int)
	 */
	@Override
	public void onDateTimeOn(String fragmentTag, int year, int monthOfYear, int dayOfMonth, int hourOfDay, int minute) {
		// Logger
		Log.d(TAG, "onDateTimeOn(String fragmentTag, int year, int monthOfYear, int dayOfMonth, int hourOfDay, int minute)");
		
		// If returned from reminder datetime picker dialog fragment
		if (FRAGMENT_TAG_REMINDER_DATETIME_PICKER.equals(fragmentTag)) {
			String timeStr = DateTimeUtils.formatTimeParamsToTimeStr(hourOfDay, minute);
			String dateStr = DateTimeUtils.formatDateParamsToDateStr(year, monthOfYear, dayOfMonth);
			textViewReminderDateTime.setText("Remind me at " + timeStr + " " + dateStr);
			DateTime dateTime = DateTimeUtils.fromDateTimeStrsToDateTime(dateStr, timeStr);
			mTask.set(ToDo.Tasks.COLUMN_NAME_DATETIME_REMINDER, dateTime.toStringRfc3339());
		}
	}
	
	/* (non-Javadoc)
	 * @see il.ac.shenkar.todo.controller.fragments.DateTimePickerFragment
	 * .DateTimePickedListener#onDateTimeOff(java.lang.String)
	 */
	@Override
	public void onDateTimeOff(String fragmentTag) {
		// Logger
		Log.d(TAG, "onDateTimeOff(String fragmentTag)");
		
		// If returned from reminder datetime picker dialog fragment
		if (FRAGMENT_TAG_REMINDER_DATETIME_PICKER.equals(fragmentTag)) {
			textViewReminderDateTime.setText(null);
			mTask.set(ToDo.Tasks.COLUMN_NAME_DATETIME_REMINDER, null);
		}
	}
	
	/* (non-Javadoc)
	 * @see il.ac.shenkar.todo.controller.fragments.LocationPickerFragment
	 * .LocationPickerListener#onLocationOn(java.lang.String, java.lang.String)
	 */
	@Override
	public void onLocationOn(String fragmentTag, String location, boolean isProximityEntering) {
		// Logger
		Log.d(TAG, "onLocationOn(String fragmentTag, String location, boolean isProximityEntering)");
		
		// If returned from reminder location picker dialog fragment
		if (FRAGMENT_TAG_REMINDER_LOCATION_PICKER.equals(fragmentTag)) {
			textViewReminderLocation.setText(location);
			textViewReminderLocation.setTag(R.id.TAG_IS_PROXIMITY_ENTERING, isProximityEntering);
			mTask.set(ToDo.Tasks.COLUMN_NAME_LOCATION_REMINDER, location);
		}
	}

	/* (non-Javadoc)
	 * @see il.ac.shenkar.todo.controller.fragments.LocationPickerFragment
	 * .LocationPickerListener#onLocationOff(java.lang.String)
	 */
	@Override
	public void onLocationOff(String fragmentTag) {
		// Logger
		Log.d(TAG, "onLocationOff(String fragmentTag)");
		
		// If returned from reminder location picker dialog fragment
		if (FRAGMENT_TAG_REMINDER_LOCATION_PICKER.equals(fragmentTag)) {
			textViewReminderLocation.setText(null);
			mTask.set(ToDo.Tasks.COLUMN_NAME_LOCATION_REMINDER, null);
		}
	}
	
	/**
	 * Gets the address object out of the addres string.
	 * 
	 * @author ran
	 * 
	 */
	private class GeocodeTask extends AsyncTask<String, Void, Address> {
		
		/**
		 * Logger's tag.
		 */
		private static final String TAG = "GeocodeTask: TaskEditorActivity";

		/**
		 * Max results per autocomplete.
		 */
		private static final int MAX_RESULTS = 1;

		@Override
		protected Address doInBackground(String... addresses) {
			// Logger
			Log.d(TAG, "doInBackground(String... addresses)");
			
			String addressStr = addresses[0];
				try {
					List<Address> addressesList = new Geocoder(hostingActivity, LOCALE)
					.getFromLocationName(addressStr, MAX_RESULTS);
					return (addressesList.isEmpty()) ? null : addressesList.get(0);
				} catch (IOException e) {
					e.printStackTrace();
				}
			return null;
		}
		@Override
		protected void onPostExecute(Address address) {
			// Logger
			Log.d(TAG, "onPostExecute(Address address)");
			
			// If geocoding succedded
			if (address != null) {
				// Notify the user geocoding succedded
				Toast.makeText(hostingActivity, "Valid location address", Toast.LENGTH_SHORT).show();
				// Sets a notification for the location reminder
				Intent locationIntent = new Intent(ToDo.Actions.ACTION_LOCATION_REMINDER_BROADCAST);
				locationIntent.putExtra(ToDo.Extras.EXTRA_IS_PROXIMITY_ENTERING, 
						(Boolean) textViewReminderLocation.getTag(R.id.TAG_IS_PROXIMITY_ENTERING));
				locationIntent.putExtra(ToDo.Extras.EXTRA_TASK_CLIENT_ID, mTaskClientId);
				locationIntent.putExtra(ToDo.Extras.EXTRA_TASK_TITLE, hostingActivity.getTitle());
				locationIntent.putExtra(ToDo.Extras.EXTRA_TASK_NOTES, editTextNotes.getText().toString());
				locationIntent.putExtra(ToDo.Extras.EXTRA_TASK_LOCATION_REMINDER_ADDRESS,
						textViewReminderLocation.getText().toString());
				PendingIntent locationPendingIntent = PendingIntent.getBroadcast(
						hostingActivity.getApplicationContext(),
						((Long)mTaskClientId).intValue(),
						locationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
				LocationManager locationManager = (LocationManager)
						hostingActivity.getSystemService(Context.LOCATION_SERVICE);
				locationManager.addProximityAlert(
						address.getLatitude(), address.getLongitude(),
						200, 1000, locationPendingIntent);
			// If geocoding failed
			} else {
				// Notify the user geocoding failed
				Toast.makeText(hostingActivity, "Invalid location address", Toast.LENGTH_SHORT).show();
			}
		}
	}
	
}
