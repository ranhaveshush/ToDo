/**
 * 
 */
package il.ac.shenkar.todo.controller.fragments;

import il.ac.shenkar.todo.R;
import il.ac.shenkar.todo.config.ToDo;
import il.ac.shenkar.todo.controller.adapters.TasksCursorAdapter;
import il.ac.shenkar.todo.model.dao.tasks.TasksDAOFactory;
import il.ac.shenkar.todo.model.dao.tasks.logic.ITaskListsDAO;
import il.ac.shenkar.todo.model.dao.tasks.logic.ITaskListsDAO.ListMode;
import il.ac.shenkar.todo.model.dao.tasks.logic.ITasksDAO;
import il.ac.shenkar.todo.utilities.SyncUtils;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.google.api.services.tasks.model.Task;
import com.google.api.services.tasks.model.TaskList;
import com.mobeta.android.dslv.DragSortListView;

/**
 * @author ran
 *
 */
public class TaskListFragment extends SherlockListFragment implements LoaderManager.LoaderCallbacks<Cursor> {
	
	/**
	 * Logger's tag.
	 */
	private static final String TAG = "TaskListFragment";
	
	/**
	 * Request code for voice recognition activity for result.
	 */
	private static final int REQUEST_VOICE_RECOGNITION_REQUEST_CODE = 1;
	
	/**
	 * Holds the hosting activity as listener.
	 */
	private TaskListListener mListener = null;
	
	/**
	 * Holds the hosting activity.
	 */
	private FragmentActivity hostingActivity = null;
	
	/**
	 * Holds the task list client id to list content tasks.
	 */
	private long mTaskListClientId;
	
	/**
	 * Holds the action bar menu.
	 */
	private Menu mMenu;
	
	/**
	 * Holds the task cursor adapter.
	 */
	private TasksCursorAdapter tasksCursorAdapter = null;

	/**
	 * Holds the drag sort list view.
	 */
	private DragSortListView dragSortListViewTasks = null;
	
	/**
	 * Holds the dao to the task lists.
	 */
	private ITaskListsDAO taskListsDAO = null;
	
	/**
	 * Holds the dao to the tasks.
	 */
	private ITasksDAO tasksDAO = null;
	
	/**
	 * Holds the task list client id state key.
	 */
	private static final String STATE_TASK_LIST_CLIENT_ID = "TaskListClientId";
	
	/**
	 * Listener interface for the fragment task list.
	 * Container activity must implement this interface.
	 * 
	 * @author ran
	 *
	 */
	public interface TaskListListener {
		/**
		 * Task list changed event.
		 * Container activity must implement this method,
		 * To change the fragment task list content when promoted to.
		 * 
		 * @param taskListClientId		Long represents the task list to render client id
		 */
		public void onTaskListChanged(long taskListClientId);
		
		/**
		 * Task selected event.
		 * Container activity must implement this method,
		 * To change the fragment task note content when promoted to.
		 * 
		 * @param taskClientId		Long represnets the task to select client id
		 */
		public void onTaskSelected(long taskClientId);
	}
	
	/**
	 * Create a new instance of TaskListFragment that will be initialized
     * with the given arguments.
     * 
     * RECOMANDED OVER A SIMPLE CONSTRUCTOR BY GOOGLE API GUIDES.
	 * 
	 * @param taskListClientId		Long represents task list client identifier
	 * @return						Fragment the newly instanciated fragment
	 */
	public static TaskListFragment newInstance(long taskListClientId) {
		// Logger
		Log.d(TAG, "newInstance(long taskListClientId)");
		
		TaskListFragment newFragment = new TaskListFragment();
		
		// Sets the argments for the new fragment
		Bundle args = new Bundle();
		args.putLong(STATE_TASK_LIST_CLIENT_ID, taskListClientId);
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
			mListener = (TaskListListener) activity;
			// Gets the hosting activity
			hostingActivity = getActivity();
		} catch (ClassCastException cce) {
			throw new ClassCastException(activity.toString() + " must implement the TaskListListener");
		}
	}
	
	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onSaveInstanceState(android.os.Bundle)
	 */
	@Override
	public void onSaveInstanceState(Bundle outState) {
		// Logger
		Log.d(TAG, "onSaveInstanceState(Bundle outState)");
		
		// Saves the tasks list fragment current state
		outState.putLong(STATE_TASK_LIST_CLIENT_ID, mTaskListClientId);
		
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
		
		// Gets the daos
		taskListsDAO = TasksDAOFactory.getFactory(hostingActivity, TasksDAOFactory.SQLITE).getTaskListsDAO();
		tasksDAO = TasksDAOFactory.getFactory(hostingActivity, TasksDAOFactory.SQLITE).getTasksDAO();
		
		// During creation, if arguments have been supplied to the fragment
	    // then parse those out
		Bundle args = getArguments();
		if (args != null) {
			mTaskListClientId = args.getLong(STATE_TASK_LIST_CLIENT_ID);
		}
		
		// If recreating a previously destroyed instance
		if (savedInstanceState != null) {
			// Restore value of members from saved state
			mTaskListClientId = savedInstanceState.getLong(STATE_TASK_LIST_CLIENT_ID);
		}
		
		// During creation with a default constructor without extras or args.
		if (args == null && savedInstanceState == null) {
			Cursor cursor = taskListsDAO.list(ListMode.MODE_NOT_DELETED);
			// If the list isn't empty
			if (cursor.moveToFirst()) {
				// Gets the first task list client id
				mTaskListClientId = cursor.getLong(ToDo.TaskLists.COLUMN_POSITION_CLIENT_ID);
			}
			cursor.close();
		}
		
		// Sets the hosting activity title to be the task list title
		TaskList taskList = taskListsDAO.get(mTaskListClientId);
		if (taskList != null) {
			hostingActivity.setTitle(taskList.getTitle());
		} else {
			hostingActivity.setTitle("");
		}
		
		// Creates the tasks cursor adapter for the given task list client id
		tasksCursorAdapter = new TasksCursorAdapter(hostingActivity, null, 0);
		
		// Prepares the loader.
		// Either re-connect with an existing one, or start a new one.
		hostingActivity.getSupportLoaderManager().restartLoader(0, null, this);
	}
	
	/* (non-Javadoc)
	 * @see android.support.v4.app.ListFragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
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
			return inflater.inflate(R.layout.fragment_tasks_list, null);
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
		
		// Gets list view widget
		dragSortListViewTasks = (DragSortListView) getListView();
		setListAdapter(tasksCursorAdapter);
	}
	
	/* (non-Javadoc)
	 * @see com.actionbarsherlock.app.SherlockListFragment#onCreateOptionsMenu(com.actionbarsherlock.view.Menu, com.actionbarsherlock.view.MenuInflater)
	 */
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		
		// Logger
		Log.d(TAG, "onCreateOptionsMenu(Menu menu, MenuInflater inflater)");
		
		inflater.inflate(R.menu.fragment_task_list, menu);
		mMenu = menu;
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
				Toast.makeText(hostingActivity, "Voice recognition is not available", Toast.LENGTH_SHORT).show();
			}
			return true;
		case R.id.menu_item_add_task:
			addTask(mTaskListClientId);
			return true;
		default:
            return super.onOptionsItemSelected(item);
		}
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.ListFragment#onListItemClick(android.widget.ListView, android.view.View, int, long)
	 */
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		// Logger
		Log.d(TAG, "onListItemClick(ListView l, View v, int position, long id)");
		
		mListener.onTaskSelected(id);
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
	 */
	@SuppressLint("NewApi")
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// Logger
		Log.d(TAG, "onActivityResult(int requestCode, int resultCode, Intent data)");
		
		super.onActivityResult(requestCode, resultCode, data);
		
		switch (requestCode) {
		case REQUEST_VOICE_RECOGNITION_REQUEST_CODE:
			// If the voice recognition is successful then it returns RESULT_OK
			if (resultCode == Activity.RESULT_OK) {
				ArrayList<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
				// If there are results
				if (!results.isEmpty()) {
					// Sets the best matched result to the menu item task title 
					String bestMatch = results.get(0);
					MenuItem menuItemTaskTitle = mMenu.findItem(R.id.menu_item_task_title);
					EditText editTextTaskTitle = (EditText) menuItemTaskTitle.getActionView();
					editTextTaskTitle.setText(bestMatch);
				}
			// Result code for various error.
			} else if(resultCode == RecognizerIntent.RESULT_AUDIO_ERROR) {
				Toast.makeText(hostingActivity, "Audio Error", Toast.LENGTH_SHORT).show();
			} else if(resultCode == RecognizerIntent.RESULT_CLIENT_ERROR) {
				Toast.makeText(hostingActivity, "Client Error", Toast.LENGTH_SHORT).show();
			} else if(resultCode == RecognizerIntent.RESULT_NETWORK_ERROR) {
				Toast.makeText(hostingActivity, "Network Error", Toast.LENGTH_SHORT).show();
			} else if(resultCode == RecognizerIntent.RESULT_NO_MATCH) {
				Toast.makeText(hostingActivity, "No Match", Toast.LENGTH_SHORT).show();
			} else if(resultCode == RecognizerIntent.RESULT_SERVER_ERROR) {
				Toast.makeText(hostingActivity, "Server Error", Toast.LENGTH_SHORT).show();
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
		
		PackageManager pm = hostingActivity.getPackageManager();
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
		intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, hostingActivity.getPackageName());
		
		MenuItem menuItemTaskTitle = mMenu.findItem(R.id.menu_item_task_title);
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
	 *
	 * @param taskListClientId	Long represents task list client identifier
	 */
	@SuppressLint("NewApi")
	private void addTask(long taskListClientId) {
		// Logger
		Log.d(TAG, "addTask(long taskListClientId)");
		
		// Gets menu item task title
		MenuItem menuItemTaskTitle = mMenu.findItem(R.id.menu_item_task_title);
		// Gets menu item task title edit text view
		EditText editTextTaskTitle = (EditText) menuItemTaskTitle.getActionView();
		// Gets the text out of the task title edit text view
		String taskTitle = editTextTaskTitle.getText().toString();
		
		// Resets menu item task title
		editTextTaskTitle.setText("");
		
		// Validates the task params
		if (TextUtils.isEmpty(taskTitle)) {
			// Notify the user
			Toast.makeText(hostingActivity, "Task's title is empty", Toast.LENGTH_SHORT).show();
			return;
		}
		
		// Creates new task with the title
		Task task = new Task();
		task.setTitle(taskTitle);
		task.set(ToDo.Tasks.COLUMN_NAME_TASK_LIST_CLIENT_ID, taskListClientId);
		
		tasksDAO.insert(task);
		
		// Gets focus on the task jsut inserted
		dragSortListViewTasks.setSelection(dragSortListViewTasks.getCount());
		
		// Syncs to the cloud
		SyncUtils.syncWithGoogleTasks(hostingActivity.getApplicationContext());
	}
	
	/* (non-Javadoc)
	 * 
	 * Called when a new Loader needs to be created.
	 * 
	 * @see android.support.v4.app.LoaderManager.LoaderCallbacks#onCreateLoader(int, android.os.Bundle)
	 */
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		// Logger
		Log.d(TAG, "onCreateLoader(int id, Bundle args)");
		
		// Now create and return a CursorLoader that will take care of
        // creating a Cursor for the data being displayed
		String selection = ToDo.Tasks.COLUMN_NAME_TASK_LIST_CLIENT_ID + "=? AND "
				+ ToDo.Tasks.COLUMN_NAME_DELETED + "=?";
		
		Log.d(TAG, "SELECTION: " + selection);
		
		String[] selectionArgs = new String[] {
				String.valueOf(mTaskListClientId),
				String.valueOf(ToDo.Tasks.COLUMN_VALUE_FALSE) };
		
		return new CursorLoader(
				hostingActivity,
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
		// Logger
		Log.d(TAG, "onLoadFinished(Loader<Cursor> loader, Cursor data)");
		
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
		// Logger
		Log.d(TAG, "onLoaderReset(Loader<Cursor> loader)");
		
		// This is called when the last Cursor provided to onLoadFinished()
        // above is about to be closed.  We need to make sure we are no
        // longer using it
		tasksCursorAdapter.swapCursor(null);
	}

}
