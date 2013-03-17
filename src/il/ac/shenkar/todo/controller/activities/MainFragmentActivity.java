package il.ac.shenkar.todo.controller.activities;

import il.ac.shenkar.todo.R;
import il.ac.shenkar.todo.config.ToDo;
import il.ac.shenkar.todo.controller.fragments.MenuFragment;
import il.ac.shenkar.todo.controller.fragments.MenuFragment.MenuListener;
import il.ac.shenkar.todo.controller.fragments.TaskListFragment;
import il.ac.shenkar.todo.controller.fragments.TaskListFragment.TaskListListener;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.analytics.tracking.android.EasyTracker;
import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.app.SlidingFragmentActivity;

public class MainFragmentActivity extends SlidingFragmentActivity implements MenuListener, TaskListListener {
	
	/**
	 * Logger's tag.
	 */
	private static final String TAG = "MainFragmentActivity";
	
	/**
	 * The content fragment tag.
	 */
	private static final String TAG_CONTENT_FRAGMENT = "ContentFragment";
	
	/**
	 * Holds the menu fragment.
	 */
	private Fragment mMenuFragment = null;
	
	/**
	 * Holds the content fragment.
	 */
	private Fragment mContentFragment = null;

	/* (non-Javadoc)
	 * @see com.slidingmenu.lib.app.SlidingFragmentActivity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Logger
		Log.d(TAG, "onCreate(Bundle savedInstanceState)");
		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		// Checks if the user selected a sync with google account
		SharedPreferences prefsFileAuth = getApplicationContext()
				.getSharedPreferences(ToDo.Prefs.PREFS_FILE_AUTH, Context.MODE_PRIVATE);
		String accountName = prefsFileAuth.getString(ToDo.Prefs.PREF_ACCOUNT_NAME, null);
		
		// If the user not selected an account, fire account selection
		if (accountName == null) {
			Intent intent = new Intent(MainFragmentActivity.this, CredentialActivity.class);
			startActivity(intent);
		}
		
		// Sets the menu fragment
		mMenuFragment = new MenuFragment();
		
		FragmentManager supportFragmentManager = getSupportFragmentManager();
		
		// Restores previous fragments state if exists
		if (savedInstanceState != null) {
			mContentFragment = supportFragmentManager
					.getFragment(savedInstanceState, TAG_CONTENT_FRAGMENT);
		}
		
		// If content is null asign default value
		if (mContentFragment == null) {
			mContentFragment = new TaskListFragment();
		}
		
		// Gets the sliding menu
		SlidingMenu slidingMenu = getSlidingMenu();
		
		FragmentTransaction fragmentTransaction = supportFragmentManager
				.beginTransaction();
		
		// Sets the Above View
		setContentView(R.layout.content_frame);
		fragmentTransaction
		.replace(R.id.content_frame, mContentFragment);
		
		// Sets the Behind View
		setBehindContentView(R.layout.menu_frame);
		fragmentTransaction
		.replace(R.id.menu_frame, mMenuFragment);
		
		fragmentTransaction.commit();
		
		// Customizes the SlidingMenu
		setSlidingActionBarEnabled(false);
		slidingMenu.setMode(SlidingMenu.LEFT);
		slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		slidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		slidingMenu.setBehindScrollScale(0F);
		slidingMenu.setShadowWidthRes(R.dimen.slidingmenu_shadow_width);
		slidingMenu.setShadowDrawable(R.drawable.shadow_left);
		slidingMenu.setFadeEnabled(true);
		slidingMenu.setFadeDegree(0.7F);
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
		
		supportFragmentManager.putFragment(outState, TAG_CONTENT_FRAGMENT, mContentFragment);
	}

	/* (non-Javadoc)
	 * @see com.actionbarsherlock.app.SherlockFragmentActivity#onCreateOptionsMenu(android.view.Menu)
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
			toggle();
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
	 * @see il.ac.shenkar.todo.controller.fragments.MenuFragment.MenuListener#onTaskListSelected(long)
	 */
	@Override
	public void onTaskListSelected(long taskListClientId) {
		// Logger
		Log.d(TAG, "onTasksListSelected(long taskListClientId)");
		
		Log.d(TAG, "taskListId: " + taskListClientId);
		
		onTaskListChanged(taskListClientId);
	}

	/* (non-Javadoc)
	 * @see il.ac.shenkar.todo.controller.fragments.TaskListFragment.TaskListListener#onTaskListChanged(long)
	 */
	@Override
	public void onTaskListChanged(long taskListClientId) {
		// Logger
		Log.d(TAG, "onTasksListChanged(long taskListClientId)");
		
		Log.d(TAG, "taskListId: " + taskListClientId);
		
		// Creates the new content fragment
		mContentFragment = TaskListFragment.newInstance(taskListClientId);
		// Replaces the old fragment with the new one
		getSupportFragmentManager()
		.beginTransaction()
		.replace(R.id.content_frame, mContentFragment)
		.commit();
		// Shows the new task content fragment
		getSlidingMenu().showContent();
	}
	
	/* (non-Javadoc)
	 * @see il.ac.shenkar.todo.controller.fragments.MenuFragment.MenuListener#onTaskListDeleted(long)
	 */
	@Override
	public void onTaskListDeleted(long taskListClientId) {
		// Logger
		Log.d(TAG, "onTaskListDeleted(long taskListClientId)");
		
		Log.d(TAG, "taskListId: " + taskListClientId);

		// Creates the new content fragment
		mContentFragment = new TaskListFragment();
		// Replaces the old fragment with the new one
		getSupportFragmentManager()
		.beginTransaction()
		.replace(R.id.content_frame, mContentFragment)
		.commit();
		// Shows the new task content fragment
		getSlidingMenu().showContent();
	}
	
	/* (non-Javadoc)
	 * @see il.ac.shenkar.todo.controller.fragments.TaskListFragment.TaskListListener#onTaskSelected(long)
	 */
	@Override
	public void onTaskSelected(long taskClientId) {
		// Logger
		Log.d(TAG, "onTaskSelected(long taskClientId)");
		
		Log.d(TAG, "taskClientId: " + taskClientId);
		
		// Redirects to task editor activity
		Intent intent = new Intent(MainFragmentActivity.this, TaskEditorFragmentActivity.class);
		// FIXME: change to view task action when supported
		intent.setAction(ToDo.Actions.ACTION_EDIT_TASK);
		intent.putExtra(ToDo.Extras.EXTRA_TASK_CLIENT_ID, taskClientId);
		startActivity(intent);
	}
	
}
