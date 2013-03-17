/**
 * 
 */
package il.ac.shenkar.todo.syncadapter;

import il.ac.shenkar.todo.config.ToDo;
import il.ac.shenkar.todo.model.dao.tasks.TasksDAOFactory;
import il.ac.shenkar.todo.model.dao.tasks.logic.ITaskListsDAO;
import il.ac.shenkar.todo.model.dao.tasks.logic.ITaskListsDAO.ListMode;
import il.ac.shenkar.todo.model.dao.tasks.logic.ITasksDAO;
import il.ac.shenkar.todo.model.dto.TaskListWrapper;
import il.ac.shenkar.todo.model.dto.TaskWrapper;
import il.ac.shenkar.todo.utilities.PrefsUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.tasks.Tasks;
import com.google.api.services.tasks.TasksScopes;
import com.google.api.services.tasks.model.Task;
import com.google.api.services.tasks.model.TaskList;

/**
 * SyncAdapter implementation for syncing sample SyncAdapter contacts to the
 * platform ContactOperations provider. This sample shows a basic 2-way sync
 * between the client and a sample server. It also contains an example of how to
 * update the contacts' status messages, which would be useful for a messaging
 * or social networking client.
 */
// FIXME: use the SyncResult class to comunicate with the syncmanager.
public class SyncAdapter extends AbstractThreadedSyncAdapter {

	/**
	 * Logger's tag.
	 */
	private static final String TAG = "SyncAdapter";

	/**
	 * Holds the invoking context.
	 */
	private final Context context;

	/**
	 * Google account credential for OAuth 2.0 protocol.
	 */
	private static GoogleAccountCredential credential = null;

	/**
	 * HttpTransport.
	 */
	private static final HttpTransport TRANSPORT = AndroidHttp.newCompatibleTransport();

	/**
	 * JSON Factory.
	 */
	private static final JsonFactory JSON_FACTORY = new GsonFactory();

	/**
	 * Holds the google account name to sync with.
	 */
	private String accountName = null;
	
	/**
	 * Holds the access auth token.
	 */
	private String authToken = null;
	
	/**
	 * Holds Google Tasks API Service.
	 */
	private Tasks service = null;
	
	/**
	 * Holds the dao to the tasks resouce.
	 */
	private ITasksDAO tasksDAO = null;

	/**
	 * Holds the dao to the task lists resouce.
	 */
	private ITaskListsDAO taskListsDAO = null;

	/**
	 * Full constructor.
	 * 
	 * @param context
	 * @param autoInitialize
	 */
	public SyncAdapter(Context context, boolean autoInitialize) {
		super(context, autoInitialize);
		// Logger
		Log.d(TAG, "SyncAdapter(Context context, boolean autoInitialize)");

		this.context = context;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.content.AbstractThreadedSyncAdapter#onPerformSync(android.accounts
	 * .Account, android.os.Bundle, java.lang.String,
	 * android.content.ContentProviderClient, android.content.SyncResult)
	 */
	@Override
	public void onPerformSync(Account account, Bundle extras, String authority,
			ContentProviderClient provider, SyncResult syncResult) {
		// Logger
		Log.d(TAG, "onPerformSync(...)");

		Log.d(TAG, "Sync with Google account name: " + account.name);
		Log.d(TAG, "Sync with Google account type: " + account.type);
		
		try {
			// Gets account name and access auth token from prefs
			accountName = PrefsUtils.getPrivatePref(context,
					ToDo.Prefs.PREFS_FILE_AUTH,
					ToDo.Prefs.PREF_ACCOUNT_NAME);
			authToken = PrefsUtils.getPrivatePref(context,
					ToDo.Prefs.PREFS_FILE_AUTH,
					ToDo.Prefs.PREF_AUTH_TOKEN);
			
			Log.d(TAG, "Sync with preference account name: " + accountName);
			Log.d(TAG, "Sync with preference auth token: " + authToken);
	
			// If the user authenticated a google account, fire sync
			if (accountName != null && authToken != null) {
				// Google Accounts
				credential = GoogleAccountCredential.usingOAuth2(
						this.context,
						TasksScopes.TASKS,
						TasksScopes.TASKS_READONLY);
				credential.setSelectedAccountName(accountName);
				
				// Gets Google Tasks API Service
				service = new Tasks(TRANSPORT, JSON_FACTORY, credential);
				
				// Gets the tasks resource DAO
				tasksDAO = TasksDAOFactory.getFactory(this.context, TasksDAOFactory.SQLITE).getTasksDAO();
				// Gets the task lists resource DAO
				taskListsDAO = TasksDAOFactory.getFactory(this.context, TasksDAOFactory.SQLITE).getTaskListsDAO();
				
				Log.d(TAG, "SYNCING !!!");
				
				sync();
			// If the user not authenticated a google account, don't sync
			} else {
				Log.d(TAG, "NOT SYNCING !!!");
			}
		} catch (final IOException e) {
			e.printStackTrace();
			syncResult.stats.numIoExceptions++;
		}
	}
	
	/**
	 * Syncs local and remote (Two-way) TaskLists and Tasks.
	 */
	private void sync() throws IOException {
		Log.d(TAG, "########## START SYNC ####################################");
		Log.d(TAG, "########## START SYNC TASK LISTS #########################");
		
		List<TaskListWrapper> localTaskListsNotDeleted = listLocalTaskLists(
				new DateTime("2013-02-01T00:00:00.000"), ListMode.MODE_NOT_DELETED);
		List<TaskListWrapper> localTaskListsDeleted = listLocalTaskLists(
				new DateTime("2013-02-01T00:00:00.000"), ListMode.MODE_DELETED);
		List<TaskListWrapper> remoteTaskLists = listRemoteTaskLists(
				new DateTime("2013-02-01T00:00:00.000"));
		
		// Deletes task lists from both local and remote
		deleteTaskListsFromLocal(localTaskListsNotDeleted, remoteTaskLists);
		deleteTaskListsFromRemote(localTaskListsDeleted, remoteTaskLists);
		
		localTaskListsNotDeleted = listLocalTaskLists(
				new DateTime("2013-02-01T00:00:00.000"), ListMode.MODE_NOT_DELETED);
		remoteTaskLists = listRemoteTaskLists(
				new DateTime("2013-02-01T00:00:00.000"));
		
		// Update >> Insert order cos of preformance resonses
		updateTaskLists(localTaskListsNotDeleted, remoteTaskLists);
		insetTaskLists(localTaskListsNotDeleted, remoteTaskLists);
		
		Log.d(TAG, "########## END SYNC TASK LISTS ##########################");
		Log.d(TAG, "########## START SYNC TASKS #############################");
		
		List<TaskWrapper> localTasks = listLocalTasks(new DateTime("2013-02-01T00:00:00.000"));
		List<TaskWrapper> remoteTasks = listRemoteTasks(new DateTime("2013-02-01T00:00:00.000"));

		// Update >> insert order cos of preformance resonses
		updateTasks(localTasks, remoteTasks);
		insertTasks(localTasks, remoteTasks);
		
		Log.d(TAG, "########## END SYNC TASKS ##############################");
		Log.d(TAG, "########## END SYNC ####################################");
	}
	
	private void deleteTaskListsFromLocal(
			List<TaskListWrapper> localTaskListsNotDeleted,
			List<TaskListWrapper> remoteTaskLists) throws IOException {
		Log.d(TAG, "********** START DELETE LOCAL TASK LISTS ***************");
		
		TaskList localTaskList = null;
		
		// Deleting from local
		List<TaskListWrapper> localDiffRemote = new ArrayList<TaskListWrapper>();
		localDiffRemote.addAll(localTaskListsNotDeleted);
		localDiffRemote.removeAll(remoteTaskLists);
		int localDeletedCounter = 0;
		for (TaskListWrapper localTaskListWrapper : localDiffRemote) {
			++localDeletedCounter;
			localTaskList = localTaskListWrapper.getTaskList();
			String localTaskListServerId = localTaskList.getId();
			// If local task list isn't at remote and have server id,
			// Means the task list was deleted from remote,
			// So delete from local
			if (localTaskListServerId != null) {
				Log.d(TAG, "TASK LIST BEFORE DELETE LOCAL >>> \n" + localTaskList);
				taskListsDAO.delete((Long) localTaskList.get(ToDo.TaskLists.COLUMN_NAME_CLIENT_ID));
			}
		}
		
		Log.d(TAG, "********************************************************");
		Log.d(TAG, "Total LOCAL task lists Deleted: " + localDeletedCounter);
		Log.d(TAG, "********** END DELETE LOCAL TASK LISTS *****************");
	}
	
	private void deleteTaskListsFromRemote(
			List<TaskListWrapper> localTaskListsDeleted,
			List<TaskListWrapper> remoteTaskLists) throws IOException {
		Log.d(TAG, "********** START DELETE REMOTE TASK LISTS **************");
		
		// FIXME: default task list can't be deleted by Google.
		// It will be fixed in the next release, so fix it then.
		// Gets the the default task list server id
		TaskList taskListDefault = service.tasklists().get("@default").execute();
		String taskListDefaultServerId = taskListDefault.getId();
		
		TaskList localTaskList = null;
		
		// Deleting from remote
		List<TaskListWrapper> localIntersactionRemote = new ArrayList<TaskListWrapper>();
		localIntersactionRemote.addAll(localTaskListsDeleted);
		localIntersactionRemote.retainAll(remoteTaskLists);
		Collections.sort( localIntersactionRemote );
		int remoteDeletedCounter = 0;
		for (TaskListWrapper localTaskListWrapper : localIntersactionRemote) {
			localTaskList = localTaskListWrapper.getTaskList();
			// FIXME:
			// If this is the default task list, it can't be deleted from google,
			// it's a bug by google that will be fixed in the next release.
			if (taskListDefaultServerId.equals(localTaskList.getId())) {
				Log.d(TAG, "BUG: default task list can't be deleted");
				// Undeletes default task list
				localTaskList.set(ToDo.TaskLists.COLUMN_NAME_DELETED, ToDo.TaskLists.COLUMN_VALUE_FALSE);
				taskListsDAO.update(localTaskList);
				continue;
			}
			++remoteDeletedCounter;
			Log.d(TAG, "TASK LIST BEFORE DELETE REMOTE >>> \n" + localTaskList);
			// Blocking code (Networking)
			service.tasklists().delete(localTaskList.getId()).execute();
			taskListsDAO.delete((Long) localTaskList.get(ToDo.TaskLists.COLUMN_NAME_CLIENT_ID));
		}
		
		Log.d(TAG, "********************************************************");
		Log.d(TAG, "Total REMOTE task lists Deleted: " + remoteDeletedCounter);
		Log.d(TAG, "********** END DELETE REMOTE TASK LISTS ****************");
	}

	private void updateTaskLists(
			List<TaskListWrapper> localTaskListsNotDeleted,
			List<TaskListWrapper> remoteTaskLists) throws IOException {
		Log.d(TAG, "********** START UPDATE TASK LISTS **********************");
		
		// Updating the intersacting task lists from local and remote
		// The most updated task list updates the the other task list
		List<TaskListWrapper> localIntersactionRemote = new ArrayList<TaskListWrapper>();
		localIntersactionRemote.addAll(localTaskListsNotDeleted);
		localIntersactionRemote.retainAll(remoteTaskLists);
		Collections.sort( localIntersactionRemote );
		
		List<TaskListWrapper> remoteIntersactionLocal = new ArrayList<TaskListWrapper>();
		remoteIntersactionLocal.addAll(remoteTaskLists);
		remoteIntersactionLocal.retainAll(localTaskListsNotDeleted);
		Collections.sort( remoteIntersactionLocal );
		
		TaskList localTaskList = null;
		TaskList remoteTaskList = null;
		long localUpdatedMillis;
		long remoteUpdatedMillis;
		
		// Runs over all the intersacting task lists and update
		int localUpdatedCounter = 0;
		int remoteUpdatedCounter = 0;
		for (int i=0; i<localIntersactionRemote.size(); ++i) {
			localTaskList = localIntersactionRemote.get(i).getTaskList();
			remoteTaskList = remoteIntersactionLocal.get(i).getTaskList();
			localUpdatedMillis = localTaskList.getUpdated().getValue();
			remoteUpdatedMillis = remoteTaskList.getUpdated().getValue();
			// If no need for updated, continue
			if (localUpdatedMillis == remoteUpdatedMillis) {
				continue;
			// If local updated then remote, updated remote
			} else if (localUpdatedMillis > remoteUpdatedMillis) {
				++remoteUpdatedCounter;
				Log.d(TAG, "TASK LIST BEFORE UPDATE REMOTE >>> \n" + localTaskList);
				// Blocking code (Networking)
				service.tasklists().update(localTaskList.getId(), localTaskList).execute();
			// If remote updated then local, update local
			} else if (localUpdatedMillis < remoteUpdatedMillis) {
				++localUpdatedCounter;
				// Preserve unknown fields from local task
				Map<String, Object> unknownFields = localTaskList.getUnknownKeys();
				// Sets the remote task with the local task unkown fields
				remoteTaskList.setUnknownKeys(unknownFields);
				Log.d(TAG, "TASK LIST BEFORE UPDATE LOCAL >>> \n" + localTaskList);
				taskListsDAO.update(remoteTaskList);
				Log.d(TAG, "TASK LIST AFTER UPDATE LOCAL >>> " + remoteTaskList);
			}
		}
		
		Log.d(TAG, "*********************************************************");
		Log.d(TAG, "Total LOCAL task lists Updated: " + localUpdatedCounter);
		Log.d(TAG, "Total REMOTE task lists Updated: " + remoteUpdatedCounter);
		Log.d(TAG, "********** END UPDATE TASK LISTS ************************");
	}
	
	private void insetTaskLists(
			List<TaskListWrapper> localTaskListsNotDeleted,
			List<TaskListWrapper> remoteTaskLists) throws IOException {
		Log.d(TAG, "********** START INSERT TASK LISTS **********************");
		
		TaskList localTaskList = null;
		TaskList remoteTaskList = null;
		
		// Inserting from remote to local
		List<TaskListWrapper> remoteDiffLocal = new ArrayList<TaskListWrapper>();
		remoteDiffLocal.addAll(remoteTaskLists);
		remoteDiffLocal.removeAll(localTaskListsNotDeleted);
		for (TaskListWrapper remoteTaskListWrapper : remoteDiffLocal) {
			remoteTaskList = remoteTaskListWrapper.getTaskList();
			Log.d(TAG, "TASK LIST BEFORE INSERT LOCAL >>> \n" + remoteTaskList);
			remoteTaskList = taskListsDAO.insert(remoteTaskList);
			Log.d(TAG, "TASK LIST AFTER INSERT LOCAL >>> \n" + remoteTaskList);
		}
		
		// Inserting from local to remote
		List<TaskListWrapper> localDiffRemote = new ArrayList<TaskListWrapper>();
		localDiffRemote.addAll(localTaskListsNotDeleted);
		localDiffRemote.removeAll(remoteTaskLists);
		int localInsertedCounter = 0;
		for (TaskListWrapper localTaskListWrapper : localDiffRemote) {
			++localInsertedCounter;
			localTaskList = localTaskListWrapper.getTaskList();
			Log.d(TAG, "TASK LIST BEFORE INSERT REMOTE >>> \n" + localTaskList);
			// Preserve unknown fields before overriding the task
			Map<String, Object> unknownFields = localTaskList.getUnknownKeys();
			// Blocking code (Networking)
			localTaskList = service.tasklists().insert(localTaskList).execute();
			// Retrieve unkown fields after overriding the task
			localTaskList.setUnknownKeys(unknownFields);
			Log.d(TAG, "TASK LIST AFTER INSERT REMOTE >>> \n" + localTaskList);
			taskListsDAO.update(localTaskList);
		}
		
		Log.d(TAG, "*********************************************************");
		Log.d(TAG, "Total LOCAL task lists Inserted: " + localInsertedCounter);
		Log.d(TAG, "********** END INSERT TASK LISTS ************************");
	}

	private void updateTasks(
			List<TaskWrapper> localTasks,
			List<TaskWrapper> remoteTasks) throws IOException {
		Log.d(TAG, "********** START UPDATE TASKS ***************************");
		
		// Updating the intersacting tasks from local and remote
		// The most updated task updates the the other task
		List<TaskWrapper> localIntersactionRemote = new ArrayList<TaskWrapper>();
		localIntersactionRemote.addAll(localTasks);
		localIntersactionRemote.retainAll(remoteTasks);
		Collections.sort( localIntersactionRemote );
		
		List<TaskWrapper> remoteIntersactionLocal = new ArrayList<TaskWrapper>();
		remoteIntersactionLocal.addAll(remoteTasks);
		remoteIntersactionLocal.retainAll(localTasks);
		Collections.sort( remoteIntersactionLocal );
		
		Task localTask = null;
		Task remoteTask = null;
		long localUpdatedMillis;
		long remoteUpdatedMillis;
		
		// Runs over all the intersacting tasks and update
		int localUpdatedCounter = 0;
		int remoteUpdatedCounter = 0;
		for (int i=0; i<localIntersactionRemote.size(); ++i) {
			localTask = localIntersactionRemote.get(i).getTask();
			remoteTask = remoteIntersactionLocal.get(i).getTask();
			localUpdatedMillis = localTask.getUpdated().getValue();
			remoteUpdatedMillis = remoteTask.getUpdated().getValue();
			// If no need for updated, continue
			if (localUpdatedMillis == remoteUpdatedMillis) {
				continue;
			// If local updated then remote, updated remote
			} else if (localUpdatedMillis > remoteUpdatedMillis) {
				++remoteUpdatedCounter;
				Log.d(TAG, "TASK BEFORE UPDATE REMOTE >>> \n" + localTask);
				long taskListClientId = (Long) localTask.get(ToDo.Tasks.COLUMN_NAME_TASK_LIST_CLIENT_ID);
				String taskListServerId = taskListsDAO.get(taskListClientId).getId();
				// Blocking code (Networking)
				service.tasks().update(taskListServerId, localTask.getId(), localTask).execute();
			// If remote updated then local, update local
			} else if (localUpdatedMillis < remoteUpdatedMillis) {
				++localUpdatedCounter;
				// Preserve unknown fields from local task
				Map<String, Object> unknownFields = localTask.getUnknownKeys();
				// Sets the remote task with the local task unkown fields
				remoteTask.setUnknownKeys(unknownFields);
				Log.d(TAG, "TASK BEFORE UPDATE LOCAL >>> \n" + localTask);
				tasksDAO.update(remoteTask);
				Log.d(TAG, "TASK AFTER UPDATE LOCAL >>> " + remoteTask);
			}
		}
		
		Log.d(TAG, "*********************************************************");
		Log.d(TAG, "Total LOCAL tasks Updated: " + localUpdatedCounter);
		Log.d(TAG, "Total REMOTE tasks Updated: " + remoteUpdatedCounter);
		Log.d(TAG, "********** END UPDATE TASKS *****************************");
	}
	
	private void insertTasks(
			List<TaskWrapper> localTasks,
			List<TaskWrapper> remoteTasks) throws IOException {
		Log.d(TAG, "********** START INSERT TASKS ***************************");
		
		Task localTask = null;
		Task remoteTask = null;
		
		// Inserting from remote to local
		List<TaskWrapper> remoteDiffLocal = new ArrayList<TaskWrapper>();
		remoteDiffLocal.addAll(remoteTasks);
		remoteDiffLocal.removeAll(localTasks);
		int localInsertedCounter = 0;
		for (TaskWrapper remoteTaskWrapper : remoteDiffLocal) {
			++localInsertedCounter;
			remoteTask = remoteTaskWrapper.getTask();
			Log.d(TAG, "TASK BEFORE INSERT LOCAL >>> \n" + remoteTask);
			
			///////////////////////
			// Gets the task list server id from task's selflink
			String selfLink = remoteTask.getSelfLink();
			String[] stringsArray = selfLink.split("/");
			String taskListServerId = stringsArray[6];
			// Gets the task list client id from the local db
			long taskListClientId = taskListsDAO.getTaskListClientId(taskListServerId);
			// Sets the task's task list client id
			remoteTask.set(ToDo.Tasks.COLUMN_NAME_TASK_LIST_CLIENT_ID, taskListClientId);
			///////////////////////
			
			remoteTask = tasksDAO.insert(remoteTask);
			Log.d(TAG, "TASK AFTER INSERT LOCAL >>> \n" + remoteTask);
		}
		
		// Inserting from local to remote
		List<TaskWrapper> localDiffRemote = new ArrayList<TaskWrapper>();
		localDiffRemote.addAll(localTasks);
		localDiffRemote.removeAll(remoteTasks);
		int remoteInsertedCounter = 0;
		for (TaskWrapper localTaskWrapper : localDiffRemote) {
			++remoteInsertedCounter;
			localTask = localTaskWrapper.getTask();
			Log.d(TAG, "TASK BEFORE INSERT REMOTE >>> \n" + localTask);
			long taskListClientId = (Long) localTask.get(ToDo.Tasks.COLUMN_NAME_TASK_LIST_CLIENT_ID);
			// FIXME: IDAN HTC NPE
			String taskListServerId = taskListsDAO.get(taskListClientId).getId();
			// Preserve unknown fields before overriding the task
			Map<String, Object> unknownFields = localTask.getUnknownKeys();
			// Blocking code (Networking)
			localTask = service.tasks().insert(taskListServerId, localTask).execute();
			// Retrieve unkown fields after overriding the task
			localTask.setUnknownKeys(unknownFields);
			Log.d(TAG, "TASK AFTER INSERT REMOTE >>> \n" + localTask);
			tasksDAO.update(localTask);
		}
		
		Log.d(TAG, "*********************************************************");
		Log.d(TAG, "Total LOCAL tasks Inserted: " + localInsertedCounter);
		Log.d(TAG, "Total REMOTE tasks Inserted: " + remoteInsertedCounter);
		Log.d(TAG, "********** END INSERT TASKS *****************************");
	}
	
	/**
	 * Gets all the remote task lists with updated datetime later
	 * then a given last sync datetime.
	 * 
	 * @param lastSyncDate	DateTime represents the last sync date.
	 * @return				List of taskListWarppers
	 */
	private List<TaskListWrapper> listLocalTaskLists(DateTime lastSyncDate, ListMode listMode) {
		// Creates a comparable task lists list with the help or TaskListWrapper class
		List<TaskListWrapper> localTaskListWarppers = new ArrayList<TaskListWrapper>();
		
		Cursor cursor = taskListsDAO.list(listMode);
		
		long lastSyncDateMillis = lastSyncDate.getValue();
		while (cursor.moveToNext()) {
			TaskList taskList = taskListsDAO.get(cursor);
			long taskListUpdatedMillis = taskList.getUpdated().getValue();
			// If task list updated later then the last sync
			if (lastSyncDateMillis < taskListUpdatedMillis) {
				// add the task list to the list
				localTaskListWarppers.add(new TaskListWrapper(taskList));
			}
		}
		
		cursor.close();

		// returns the comparable task list wrappers list
		return localTaskListWarppers;
	}

	/**
	 * Gets all the remote task lists list with updated datetime later
	 * then a given last sync datetime.
	 * 
	 * @param lastSyncDate	DateTime represents the last sync date.
	 * @return				List of taskListWarppers
	 */
	private List<TaskListWrapper> listRemoteTaskLists(DateTime lastSyncDate) throws IOException {
		// Creates a comparable task lists list with the help or TaskListWrapper class
		List<TaskListWrapper> remoteTaskListWrappers = new ArrayList<TaskListWrapper>();
		List<TaskList> remoteTaskLists = service.tasklists().list().execute().getItems();

		long lastSyncDateMillis = lastSyncDate.getValue();
		for (TaskList taskList : remoteTaskLists) {
			long taskListUpdatedMillis = taskList.getUpdated().getValue();
			// If task list updated later then the last sync
			if (lastSyncDateMillis < taskListUpdatedMillis) {
				// Wraps task list with a wrapper and adds to the task list wrappers list
				remoteTaskListWrappers.add(new TaskListWrapper(taskList));
			}
		}

		// returns the comparable task list wrappers list
		return remoteTaskListWrappers;
	}

	/**
	 * Gets all the local tasks with updated datetime later
	 * then a given last sync datetime.
	 * 
	 * @param lastSyncDate	DateTime represents the last sync date.
	 * @return				List of taskwarppers
	 */
	private List<TaskWrapper> listLocalTasks(DateTime lastSyncDate) {
		// Creates a comparable task list with the help or TaskWrapper class
		List<TaskWrapper> localTaskWarppers = new ArrayList<TaskWrapper>();

		Cursor cursor = tasksDAO.listAll();
		
		long lastSyncDateMillis = lastSyncDate.getValue();
		while (cursor.moveToNext()) {
			Task task = tasksDAO.get(cursor);
			long taskUpdatedMillis = task.getUpdated().getValue();
			// If task updated later then the last sync
			// FIXME: when support lastSyncDate
			//if (lastSyncDateMillis < taskUpdatedMillis) {
				// Wraps task with a wrapper and adds to the task wrappers list
				localTaskWarppers.add(new TaskWrapper(task));
			//}
		}
		
		cursor.close();

		// returns the comparable task wrappers list
		return localTaskWarppers;
	}

	/**
	 * Gets all the remote tasks with updated datetime later
	 * then a given last sync datetime.
	 * 
	 * @param lastSyncDate	DateTime represents the last sync date.
	 * @return				List of taskwarppers
	 */
	private List<TaskWrapper> listRemoteTasks(DateTime lastSyncDate) throws IOException {
		List<TaskList> remoteTaskLists = null;
		List<Task> remoteTasks = new ArrayList<Task>();
		List<Task> tempTasks = null;
		
		remoteTaskLists = service.tasklists().list().execute().getItems();

		for (TaskList taskList : remoteTaskLists) {
			// Gets the task list server id
			String taskListServerId = taskList.getId();
			// Gets all the task list tasks later then lastSyncDate
			tempTasks = service.tasks().list(taskListServerId)
					// FIXME: see if it works
					/*.setUpdatedMin(lastSyncDate.toString())*/ 
					.setMaxResults(1000L)
					.setShowDeleted(true).execute().getItems();
			// Collects all the remote tasks from every remote task list
			if (tempTasks != null) {
				remoteTasks.addAll(tempTasks);
			}
		}
		
		// Creates a comparable task list with the help or TaskWrapper class
		List<TaskWrapper> remoteWrapperTaskList = new ArrayList<TaskWrapper>();
		for (Task task : remoteTasks) {
			TaskWrapper taskWrapper = new TaskWrapper(task);
			remoteWrapperTaskList.add(taskWrapper);
		}

		// returns the comparable task wrappers list
		return remoteWrapperTaskList;
	}

}
