/**
 * 
 */
package il.ac.shenkar.todo.controller.fragments;

import il.ac.shenkar.todo.R;
import il.ac.shenkar.todo.config.ToDo;
import il.ac.shenkar.todo.controller.adapters.TaskListsCursorAdapter;
import il.ac.shenkar.todo.model.dao.tasks.TasksDAOFactory;
import il.ac.shenkar.todo.model.dao.tasks.logic.ITaskListsDAO;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.google.api.services.tasks.model.TaskList;
import com.mobeta.android.dslv.DragSortListView;

/**
 * @author ran
 *
 */
public class MenuFragment extends SherlockFragment implements LoaderManager.LoaderCallbacks<Cursor> {
	
	/**
	 * Logger's tag.
	 */
	private static final String TAG = "MenuFragment";
	
	/**
	 * Holds the hosting activity as listener.
	 */
	private MenuListener mListener = null;
	
	/**
	 * Holds the hosting activity.
	 */
	private FragmentActivity hostingActivity = null;
	
	/**
	 * Holds the text view of the account name.
	 */
	private TextView textViewAccountName = null;
	
	/**
	 * Holds the list view adapter.
	 */
	private TaskListsCursorAdapter taskListsCursorAdapter = null;
	
	/**
	 * Holds the drag sort list view of task lists.
	 */
	private DragSortListView dragSortListViewTaskLists = null;

	/**
	 * Holds the task list title edit text.
	 */
	private EditText editTextTaskListTitle = null;
	
	/**
	 * Holds the task list add image view.
	 */
	private ImageView imageViewAddTaskList = null;
	
	/**
	 * Holds the task lists dao.
	 */
	private ITaskListsDAO taskListsDAO = null;
	
	/**
	 * Listener interface for the fragment menu.
	 * Container Activity must implement this interface.
	 * 
	 * @author ran
	 *
	 */
	public interface MenuListener {
		/**
		 * Task list selected event.
		 * Container activity must implement this method,
		 * To change the fragment task list content when promoted to.
		 * 
		 * @param taskListClientId  Long represents the task list to render client id
		 */
		public void onTaskListSelected(long taskListClientId);
		
		/**
		 * Task list deleted event.
		 * Container activity must implement this method,
		 * To change the fragment task list content when promoted to.
		 * 
		 * @param taskListClientId  Long represents the deleted task list client id
		 */
		public void onTaskListDeleted(long taskListClientId);
	}

	/* (non-Javadoc)
	 * @see com.actionbarsherlock.app.SherlockListFragment#onAttach(android.app.Activity)
	 */
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		// Logger
		Log.d(TAG, "onAttach(Activity activity)");
		
		// Ensures hosting activity implements the Listener interface
		try {
			mListener = (MenuListener) activity;
			// Gets fragment hosting activity
			hostingActivity = getActivity();
		} catch (ClassCastException cce) {
			throw new ClassCastException(activity.toString() + " must implement the MenuListener");
		}
	}
	
	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Logger
		Log.d(TAG, "onCreate(Bundle savedInstanceState)");
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
			return inflater.inflate(R.layout.fragment_menu, null);
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
		
		taskListsDAO = TasksDAOFactory.getFactory(hostingActivity, TasksDAOFactory.SQLITE).getTaskListsDAO();
		
		// Gets account name text view widget
		textViewAccountName = (TextView) hostingActivity.findViewById(R.id.text_view_account_name);
		// Gets the account name from the prefs file
		SharedPreferences prefsFileAuth = hostingActivity.getApplicationContext()
				.getSharedPreferences(ToDo.Prefs.PREFS_FILE_AUTH, Context.MODE_PRIVATE);
		String accountName = prefsFileAuth.getString(ToDo.Prefs.PREF_ACCOUNT_NAME, null);
		// If there is an account name, set it
		if (accountName != null) {
			textViewAccountName.setText(accountName);
		} else {
			textViewAccountName.setText("Please log in");
		}
		
		// Gets the task lists drag sort list view widget
		dragSortListViewTaskLists = (DragSortListView) hostingActivity.findViewById(R.id.dslv_task_lists);
		taskListsCursorAdapter = new TaskListsCursorAdapter(hostingActivity, null, 0);
		dragSortListViewTaskLists.setAdapter(taskListsCursorAdapter);
		dragSortListViewTaskLists.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				mListener.onTaskListSelected(id);
			}
		});
		
		// Gets the task list title edit text widget
		editTextTaskListTitle = (EditText) hostingActivity.findViewById(R.id.edit_text_task_list_title);
		// Gets the add task list image view
		imageViewAddTaskList = (ImageView) hostingActivity.findViewById(R.id.image_view_add_task_list);
		imageViewAddTaskList.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// Gets the task list title
				String taskListTitle = editTextTaskListTitle.getText().toString();
				// Create a new task list
				TaskList taskList = new TaskList();
				taskList.setTitle(taskListTitle);
				// Persists the new task list
				taskListsDAO.insert(taskList);
				// Deletes the task list to add title
				editTextTaskListTitle.setText(null);
			}
		});
		
		// Prepares the loader.
		// Either re-connect with an existing one, or start a new one.
		hostingActivity.getSupportLoaderManager().initLoader(1, null, this);
	}

	/* (non-Javadoc)
	 * @see com.actionbarsherlock.app.SherlockFragment#onCreateOptionsMenu(com.actionbarsherlock.view.Menu, com.actionbarsherlock.view.MenuInflater)
	 */
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		
		// Logger
		Log.d(TAG, "onCreateOptionsMenu(Menu menu, MenuInflater inflater)");
		
		inflater.inflate(R.menu.fragment_menu, menu);
	}

	/* (non-Javadoc)
	 * @see com.actionbarsherlock.app.SherlockFragment#onOptionsItemSelected(com.actionbarsherlock.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Logger
		Log.d(TAG, "onOptionsItemSelected(MenuItem item)");
		
		return super.onOptionsItemSelected(item);
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
		String selection = ToDo.TaskLists.COLUMN_NAME_DELETED + "=?";
		String[] selectionArgs = new String[] { String.valueOf(ToDo.TaskLists.COLUMN_VALUE_FALSE) };
		
		return new CursorLoader(
				hostingActivity,
				ToDo.TaskLists.CONTENT_URI,
				ToDo.TaskLists.PROJECTION,
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
        taskListsCursorAdapter.swapCursor(data);
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
		taskListsCursorAdapter.swapCursor(null);
	}

}
