package il.ac.shenkar.todo.controller.activities;

import il.ac.shenkar.todo.R;
import il.ac.shenkar.todo.config.ToDo;
import il.ac.shenkar.todo.controller.fragments.DatePickerFragment.DatePickerListener;
import il.ac.shenkar.todo.controller.fragments.DateTimePickerFragment.DateTimePickedListener;
import il.ac.shenkar.todo.controller.fragments.LocationPickerFragment.LocationPickerListener;
import il.ac.shenkar.todo.controller.fragments.TaskEditorFragment;
import il.ac.shenkar.todo.controller.fragments.TaskEditorFragment.TaskEditorListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.analytics.tracking.android.EasyTracker;

/**
 * 
 * 
 * @author ran
 *
 */
public class TaskEditorFragmentActivity extends SherlockFragmentActivity
	implements TaskEditorListener, DatePickerListener, DateTimePickedListener, LocationPickerListener {
	
	/**
	 * Logger's tag.
	 */
	private static final String TAG = "TaskEditorFragmentActivity";
	
	/**
	 * Holds the task editor fragment tag.
	 */
	private static final String TAG_TASK_EDITOR_FRAGMENT = "TaskEditorFragment";
	
	/**
	 * Holds the manipulation state of the task.
	 */
	private TaskEditorFragment.State mState = null;

	/**
	 * Holds the task editor fragment.
	 */
	private Fragment mTaskEditorFragment = null;

	/* (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Logger
		Log.d(TAG , "onCreate(Bundle savedInstanceState)");
		
		setContentView(R.layout.fragment_activity_task_editor);
		
		getSupportActionBar().setHomeButtonEnabled(true);
		
		Intent intent = getIntent();

		// Gets action
		String action = intent.getAction();
		// Sets the state according to intent action
		if(ToDo.Actions.ACTION_VIEW_TASK.equals(action)) {
			mState = TaskEditorFragment.State.VIEW_TASK;
		} else if (ToDo.Actions.ACTION_EDIT_TASK.equals(action)) {
			mState = TaskEditorFragment.State.EDIT_TASK;
		}
		
		// Gets extras
		Bundle extras = intent.getExtras();
		long taskClientId = extras.getLong(ToDo.Extras.EXTRA_TASK_CLIENT_ID);
		
		FragmentManager supportFragmentManager = getSupportFragmentManager();
		
		// Restores previous fragments state if exists
		if (savedInstanceState != null) {
			mTaskEditorFragment = supportFragmentManager
					.getFragment(savedInstanceState, TAG_TASK_EDITOR_FRAGMENT);
		}
		// If task editor fragment is null asign default value
		if (mTaskEditorFragment == null) {
			mTaskEditorFragment = TaskEditorFragment.newInstance(mState, taskClientId);
		}

		getSupportFragmentManager()
		.beginTransaction()
		.replace(R.id.task_editor_frame, mTaskEditorFragment)
		.commit();
	}
	
	/* (non-Javadoc)
	 * @see com.actionbarsherlock.app.SherlockFragmentActivity#onCreateOptionsMenu(com.actionbarsherlock.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Logger
		Log.d(TAG, "onCreateOptionsMenu(Menu menu)");
		
		return super.onCreateOptionsMenu(menu);
	}



	/* (non-Javadoc)
	 * @see com.actionbarsherlock.app.SherlockFragmentActivity#onOptionsItemSelected(com.actionbarsherlock.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Logger
		Log.d(TAG, "onOptionsItemSelected(MenuItem item)");
		
		switch (item.getItemId()) {
		case android.R.id.home:
			Intent parentActivityIntent = new Intent(
					TaskEditorFragmentActivity.this,
					MainFragmentActivity.class);
			parentActivityIntent.addFlags(
				Intent.FLAG_ACTIVITY_CLEAR_TOP |
				Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(parentActivityIntent);
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/* (non-Javadoc)
	 * @see com.slidingmenu.lib.app.SlidingFragmentActivity#onSaveInstanceState(android.os.Bundle)
	 */
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		
		// Logger
		Log.d(TAG, "onSaveInstanceState(Bundle outState)");
		
		FragmentManager supportFragmentManager = getSupportFragmentManager();
		
		supportFragmentManager.putFragment(outState, TAG_TASK_EDITOR_FRAGMENT, mTaskEditorFragment);
	}
	
	/* (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onStart()
	 */
	@Override
	protected void onStart() {
		super.onStart();
		
		// Logger
		Log.d(TAG, "onStart()");
		
		EasyTracker.getInstance().activityStart(this);
	}
	
	/* (non-Javadoc)
	 * @see com.actionbarsherlock.app.SherlockFragmentActivity#onStop()
	 */
	@Override
	protected void onStop() {
		super.onStop();
		
		// Logger
		Log.d(TAG, "onStop()");
		
		EasyTracker.getInstance().activityStop(this);
	}

	/* (non-Javadoc)
	 * @see il.ac.shenkar.todo.controller.fragments.TaskEditorFragment
	 * .TaskEditorListener#onTaskChanged(long)
	 */
	@Override
	public void onTaskChanged(long taskClientId) {
		
	}

	/* (non-Javadoc)
	 * @see il.ac.shenkar.todo.controller.fragments.DateTimePickerFragment
	 * .DateTimePickedListener#onDateTimeOn(java.lang.String, int, int, int, int, int)
	 */
	@Override
	public void onDateTimeOn(String fragmentTag, int year, int monthOfYear, int dayOfMonth,
			int hourOfDay, int minute) {
		((TaskEditorFragment) mTaskEditorFragment).onDateTimeOn(fragmentTag,
				year, monthOfYear, dayOfMonth,
				hourOfDay, minute);
	}
	
	/* (non-Javadoc)
	 * @see il.ac.shenkar.todo.controller.fragments.DateTimePickerFragment
	 * .DateTimePickedListener#onDateTimeOff(java.lang.String)
	 */
	@Override
	public void onDateTimeOff(String fragmentTag) {
		((TaskEditorFragment) mTaskEditorFragment).onDateTimeOff(fragmentTag);
	}

	/* (non-Javadoc)
	 * @see il.ac.shenkar.todo.controller.fragments.DatePickerFragment
	 * .DatePickerListener#onDateOn(java.lang.String, int, int, int)
	 */
	@Override
	public void onDateOn(String fragmentTag, int year, int monthOfYear, int dayOfMonth) {
		((TaskEditorFragment) mTaskEditorFragment).onDateOn(fragmentTag, 
				year, monthOfYear, dayOfMonth);
	}

	/* (non-Javadoc)
	 * @see il.ac.shenkar.todo.controller.fragments
	 * .DatePickerFragment.DatePickerListener#onDateOff(java.lang.String)
	 */
	@Override
	public void onDateOff(String fragmentTag) {
		((TaskEditorFragment) mTaskEditorFragment).onDateOff(fragmentTag);
	}

	/* (non-Javadoc)
	 * @see il.ac.shenkar.todo.controller.fragments.LocationPickerFragment
	 * .LocationPickerListener#onLocationOn(java.lang.String, java.lang.String, boolean)
	 */
	@Override
	public void onLocationOn(String fragmentTag, String address, boolean isProximityEntering) {
		((TaskEditorFragment) mTaskEditorFragment).onLocationOn(fragmentTag, address, isProximityEntering);
	}

	/* (non-Javadoc)
	 * @see il.ac.shenkar.todo.controller.fragments.LocationPickerFragment
	 * .LocationPickerListener#onLocationOff(java.lang.String)
	 */
	@Override
	public void onLocationOff(String fragmentTag) {
		((TaskEditorFragment) mTaskEditorFragment).onLocationOff(fragmentTag);
	}
	
}
