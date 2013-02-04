/**
 * 
 */
package il.ac.shenkar.todo.controller.loaders;

import il.ac.shenkar.todo.config.ToDo;
import il.ac.shenkar.todo.controller.activities.TasksListActivity;

import java.util.List;

import android.os.Bundle;
import android.util.Log;

import com.google.api.services.tasks.model.Task;

/**
 * @author ran
 * 
 */
//FIXME: if not needed delete
public class TasksListAsyncLoader extends AsyncLoader<List<Task>> {
	
	/**
	 * Logger tag.
	 */
	public static final String TAG = "TasksListAsyncLoader";

	/**
	 * Holds the task list activity.
	 */
	TasksListActivity tasksListActivity = null;
	
	/**
	 * Holds the tasks list identifier.
	 */
	String tasksListId = null;

	/**
	 * Holds the tasks list data to load.
	 */
	List<Task> tasksList = null;

	/**
	 * Full constructor.
	 * 
	 * @param context
	 */
	public TasksListAsyncLoader(TasksListActivity tasksListActivity, Bundle args) {
		super(tasksListActivity);
		
		// Logger
		Log.d(TAG, "TasksListAsyncLoader(TasksListActivity tasksListActivity, Bundle args)");
		
		this.tasksListId = args.getString(ToDo.Args.ARG_TASKS_LIST_ID);
		
		this.tasksListActivity = tasksListActivity;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.content.AsyncTaskLoader#loadInBackground()
	 */
	@Override
	public List<Task> loadInBackground() {
		// Logger
		Log.d(TAG, "loadInBackground()");
		
		return null; //tasksListActivity.tasksDAO.list(tasksListId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.content.Loader#deliverResult(java.lang.Object)
	 */
	/*@Override
	public void deliverResult(List<Task> tasksList) {
		// Logger
		Log.d(TAG, "deliverResult(List<Task> tasksList)");
				
		// this.tasksList = tasksList;

		// If the Loader is currently started, we can immediately
		// deliver its results.
		if (isStarted()) {
			super.deliverResult(tasksList);
		}
	}*/

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.content.Loader#onStartLoading()
	 */
	/*@Override
	protected void onStartLoading() {
		// Logger
		Log.d(TAG, "onStartLoading()");
		
		// Sets progress bar visiable
		ProgressBar progressBar = (ProgressBar) tasksListActivity.findViewById(R.id.refresh_progress);
		progressBar.setVisibility(View.VISIBLE);
		
		// If we currently have a result available, deliver it
		// immediately.
		if (this.tasksList != null) {
			deliverResult(this.tasksList);
		}
	}*/

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.content.Loader#onStopLoading()
	 */
	/*@Override
	protected void onStopLoading() {
		// Logger
		Log.d(TAG, "onStopLoading()");
		
		// Sets progress bar gone
		ProgressBar progressBar = (ProgressBar) tasksListActivity.findViewById(R.id.refresh_progress);
		progressBar.setVisibility(View.GONE);
				
		// Attempts to cancel the current load task if possible
		cancelLoad();
	}*/

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.support.v4.content.AsyncTaskLoader#onCanceled(java.lang.Object)
	 */
	/*@Override
	public void onCanceled(List<Task> data) {
		// Logger
		Log.d(TAG, "onCanceled(List<Task> data)");
				
		super.onCanceled(data);
	}*/

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.content.Loader#onReset()
	 */
	/*@Override
	protected void onReset() {
		super.onReset();
		
		// Logger
		Log.d(TAG, "onReset()");

		// Ensures the loader is stopped
		onStopLoading();

		// Releases the resources associated with tasks list
		this.tasksList = null;
	}*/

	/* (non-Javadoc)
	 * @see android.support.v4.content.AsyncTaskLoader#onForceLoad()
	 */
	/*@Override
	protected void onForceLoad() {
		super.onForceLoad();
	}*/

}
