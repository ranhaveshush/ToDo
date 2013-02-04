/**
 * 
 */
package il.ac.shenkar.todo.syncadapter;

import il.ac.shenkar.todo.config.ToDo;
import il.ac.shenkar.todo.model.dao.tasks.TasksDAOFactory;
import il.ac.shenkar.todo.model.dao.tasks.logic.ITasksDAO;
import il.ac.shenkar.todo.model.dto.TaskWrapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SyncResult;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.tasks.Tasks;
import com.google.api.services.tasks.TasksScopes;
import com.google.api.services.tasks.model.Task;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * SyncAdapter implementation for syncing sample SyncAdapter contacts to the
 * platform ContactOperations provider. This sample shows a basic 2-way sync
 * between the client and a sample server. It also contains an example of how to
 * update the contacts' status messages, which would be useful for a messaging
 * or social networking client.
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {

	private static final String TAG = "SyncAdapter";

//	private static final String SYNC_MARKER_KEY = "il.ac.shenkar.todo.sync.marker";

//	private static final boolean NOTIFY_AUTH_FAILURE = true;

	/**
	 * Holds the invoking context.
	 */
	private final Context context;

	/**
	 * Holds the account manager.
	 */
//	private final AccountManager mAccountManager;

	/**
	 * Holds the content resolver to the content provider.
	 */
	private final ContentResolver contentResolver;

	/**
	 * Google account credential for OAuth 2.0 protocol.
	 */
	private static GoogleAccountCredential credential = null;

	/**
	 * HttpTransport.
	 */
	private static final HttpTransport TRANSPORT = AndroidHttp
			.newCompatibleTransport();

	/**
	 * JSON Factory.
	 */
	private static final JsonFactory JSON_FACTORY = new GsonFactory();

	/**
	 * Holds the google account name to sync with.
	 */
	private static String accountName = null;
	
	/**
	 * 
	 */
//	private static String authtoken = null;

	/**
	 * Holds Google Tasks API Service.
	 */
	private final Tasks service;
	
	/**
	 * Holds the dao to the tasks resouce.
	 */
	private final ITasksDAO tasksDAO;

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
//		mAccountManager = AccountManager.get(context);
		contentResolver = context.getContentResolver();

		// Gets access token from the auth prefs file
		SharedPreferences prefsFileAuth = context.getApplicationContext()
				.getSharedPreferences(ToDo.Prefs.PREFS_FILE_AUTH,
						Context.MODE_PRIVATE);
		accountName = prefsFileAuth.getString(ToDo.Prefs.PREF_ACCOUNT_NAME,
				null);

		// Google Accounts
		credential = GoogleAccountCredential.usingOAuth2(this.context,
				TasksScopes.TASKS);
		credential.setSelectedAccountName(accountName);

		// Gets Google Tasks API Service
		service = new Tasks(TRANSPORT, JSON_FACTORY, credential);
		
		// Gets the tasks resource DAO
		tasksDAO = TasksDAOFactory.getFactory(this.context, TasksDAOFactory.SQLITE).getTasksDAO();
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

		Log.d(TAG, "######################### START SYNC #########################");

		try {
//			// See if we already have a sync-state attached to this account.
//			// By handing this value to the server, we can just get the tasks
//			// that have
//			// been updated on the server-side since our last sync-up
//			// long lastSyncMarker = getServerSyncMarker(account);
//
//			// Use the account manager to request the AuthToken we'll need
//			// to talk to our sample server. If we don't have an AuthToken
//			// yet, this could involve a round-trip to the server to request
//			// and AuthToken.
//			// authtoken = mAccountManager.blockingGetAuthToken(account, "ah",
//			// NOTIFY_AUTH_FAILURE);
//			
////			Cursor cursor = tasksDAO.getNotSynced("@default");
////			
////			Task task = null;
////
////			while (cursor.moveToNext()) {
////				task = getTask(cursor);
////				String taskTransState = (String) task
////						.get(ToDo.Tasks.COLUMN_NAME_TRANS_STATE);
////
////				// Operates according the transitional state
////				if (ToDo.Trans.STATE_UPDATING.equals(taskTransState)) {
////					updateTask(task);
////				} else if (ToDo.Trans.STATE_INSERTING.equals(taskTransState)) {
////					insertTask(task);
////				} else if (ToDo.Trans.STATE_DELETING.equals(taskTransState)) {
////					deleteTask(task);
////				} else if (ToDo.Trans.STATE_GETTING.equals(taskTransState)) {
////					// Do nothing
////				}
////			}
////
////			cursor.close();
//			
			sync("@default");
//
//			// Save off the new sync marker. On our next sync, we only want to
//			// receive
//			// tasks that have changed since this sync...
//			// setServerSyncMarker(account, newSyncState);
//
		} catch (final IOException e) {
			Log.e(TAG, "IOException", e);
			syncResult.stats.numIoExceptions++;
		}

		Log.d(TAG, "########################## END SYNC ##########################");
	}
	
	/**
	 * Syncs local and remote task list (Two-way).
	 * 
	 * @param taskListId
	 */
	private void sync(String taskListId) throws IOException {
		// Logger
		Log.d(TAG, "sync(String taskListId)");

		List<TaskWrapper> taskListLocal = getLocalTaskList(taskListId);
		List<TaskWrapper> taskListRemote = getRemoteTaskList(taskListId);
		
		// FIXME: no need for deleting only updating to delete
//		// Deleting
//		List<TaskWrapper> tasksUnion = new ArrayList<TaskWrapper>();
//		tasksUnion.addAll(taskListLocal);
//		tasksUnion.removeAll(taskListRemote);
//		tasksUnion.addAll(taskListRemote);
//		for (TaskWrapper taskWrapper : tasksUnion) {
//			Task task = taskWrapper.getTask();
//			// If task marked for deletetion
//			if (task.getDeleted() != null && task.getDeleted()) {
//				// Gets task location local or remote
//				String taskLocation = (String) task.get(ToDo.Tasks.TASK_LOCATION);
//				// If task is local, delete task from remote
//				if (ToDo.Tasks.LOCATION_LOCAL.equals(taskLocation)) {
//					Log.d(TAG, "TASK BEFORE DELETE FROM REMOTE >>> \n" + task);
//					service.tasks().delete(taskListId, task.getId()).execute();
//				// If task is remote, delete task from local
//				} else {
//					Log.d(TAG, "TASK BEFORE DELETE FROM LOCAL >>> \n" + task);
//					tasksDAO.delete(task);
//				}
//			}
//		}
		
		// Updating
		List<TaskWrapper> localIntersactionRemote = new ArrayList<TaskWrapper>();
		localIntersactionRemote.addAll(taskListLocal);
		localIntersactionRemote.retainAll(taskListRemote);
		Collections.sort( localIntersactionRemote );
		
		List<TaskWrapper> remoteIntersactionLocal = new ArrayList<TaskWrapper>();
		remoteIntersactionLocal.addAll(taskListRemote);
		remoteIntersactionLocal.retainAll(taskListLocal);
		Collections.sort( remoteIntersactionLocal );
		
		Task localTask = null;
		Task remoteTask = null;
		DateTime localUpdated = null;
		DateTime remoteUpdated = null;
		long localUpdatedMillis;
		long remoteUpdatedMillis;
		
		for (int i=0; i<localIntersactionRemote.size(); ++i) {
			localTask = localIntersactionRemote.get(i).getTask();
			remoteTask = remoteIntersactionLocal.get(i).getTask();
			localUpdated = localTask.getUpdated();
			remoteUpdated = remoteTask.getUpdated();
			localUpdatedMillis = localUpdated.getValue();
			remoteUpdatedMillis = remoteUpdated.getValue();
			// If no need for updated, continue
			if (localUpdatedMillis == remoteUpdatedMillis) {
				continue;
			// If local updated then remote, updated remote
			} else if (localUpdatedMillis > remoteUpdatedMillis) {
				Log.d(TAG, "TASK BEFORE UPDATE REMOTE >>> \n" + localTask);
				// Blocking code (Networking)
				service.tasks().update(taskListId, localTask.getId(), localTask).execute();
			// If remote updated then local, update local
			} else if (localUpdatedMillis < remoteUpdatedMillis) {
				// Preserve unknown fields from local task
				Map<String, Object> unknownFields = localTask.getUnknownKeys();
				// Sets the remote task with the local task unkown fields
				remoteTask.setUnknownKeys(unknownFields);
				Log.d(TAG, "TASK BEFORE UPDATE LOCAL >>> \n" + localTask);
				tasksDAO.update(remoteTask);
				Log.d(TAG, "TASK AFTER UPDATE LOCAL >>> " + remoteTask);
			}
		}
		
		// Inserting from remote to local
		List<TaskWrapper> remoteDiffLocal = new ArrayList<TaskWrapper>();
		remoteDiffLocal.addAll(taskListRemote);
		remoteDiffLocal.removeAll(taskListLocal);
		for (TaskWrapper remoteTaskWrapper : remoteDiffLocal) {
			remoteTask = remoteTaskWrapper.getTask();
			Log.d(TAG, "TASK BEFORE INSERT LOCAL >>> \n" + remoteTask);
			remoteTask = tasksDAO.insert(remoteTask);
			Log.d(TAG, "TASK AFTER INSERT LOCAL >>> \n" + remoteTask);
		}
		
		// Inserting from local to remote
		List<TaskWrapper> localDiffRemote = new ArrayList<TaskWrapper>();
		localDiffRemote.addAll(taskListLocal);
		localDiffRemote.removeAll(taskListRemote);
		for (TaskWrapper localTaskWrapper : localDiffRemote) {
			localTask = localTaskWrapper.getTask();
			Log.d(TAG, "TASK BEFORE INSERT REMOTE >>> \n" + localTask);
			// Preserve unknown fields before overriding the task
			Map<String, Object> unknownFields = localTask.getUnknownKeys();
			// Blocking code (Networking)
			localTask = service.tasks().insert(taskListId, localTask).execute();
			// Retrieve unkown fields after overriding the task
			localTask.setUnknownKeys(unknownFields);
			Log.d(TAG, "TASK AFTER INSERT REMOTE >>> \n" + localTask);
			tasksDAO.update(localTask);
		}
	}
	
	/**
	 * Gets the local task list.
	 * 
	 * @param taskListId
	 * @return
	 */
	private List<TaskWrapper> getLocalTaskList(String taskListId) {
		List<Task> localTaskList = new ArrayList<Task>();

		String selection = ToDo.Tasks.COLUMN_NAME_TASKLIST_ID + "=?";
		String[] selectionArgs = { taskListId };

		Cursor cursor = contentResolver.query(
				ToDo.Tasks.CONTENT_URI,
				ToDo.Tasks.PROJECTION,
				selection, selectionArgs, null);
		
		while (cursor.moveToNext()) {
			Task task = getTask(cursor);
			localTaskList.add(task);
		}
		
		cursor.close();
		
		List<TaskWrapper> wrapperTaskListLocal = new ArrayList<TaskWrapper>();
		for (Task task : localTaskList) {
			TaskWrapper taskWrapper = new TaskWrapper(task);
			wrapperTaskListLocal.add(taskWrapper);
		}

		return wrapperTaskListLocal;
	}

	/**
	 * Gets the remote task list.
	 * 
	 * @param taskListId
	 * @return
	 */
	private List<TaskWrapper> getRemoteTaskList(String taskListId) {
		List<Task> remoteTaskList = null;

		try {
			remoteTaskList = service.tasks().list(taskListId)
					/*.setUpdatedMin(updatedMin)*/// Set as last sync datetime 
					.setMaxResults(1000L)
					.setShowDeleted(true).execute().getItems();
		} catch (IOException e) {
			e.printStackTrace();
			handleException(e);
		}
		
		// Creates a comparable list with the help or TaskWrapper class
		List<TaskWrapper> wrapperTaskListRemote = new ArrayList<TaskWrapper>();
		for (Task task : remoteTaskList) {
			TaskWrapper taskWrapper = new TaskWrapper(task);
			wrapperTaskListRemote.add(taskWrapper);
		}

		return wrapperTaskListRemote;
	}

	/**
	 * Gets the task from the cursor in its current position.
	 * 
	 * @param cursor
	 * @return
	 */
	private Task getTask(Cursor cursor) {
		long taskClientId = cursor.getLong(ToDo.Tasks.COLUMN_POSITION_CLIENT_ID);
		String taskKind = cursor.getString(ToDo.Tasks.COLUMN_POSITION_KIND);
		String taskServerId = cursor.getString(ToDo.Tasks.COLUMN_POSITION_SERVER_ID);
		String taskTitle = cursor.getString(ToDo.Tasks.COLUMN_POSITION_TITLE);
		String taskUpdated = cursor.getString(ToDo.Tasks.COLUMN_POSITION_UPDATED);
		String taskSelfLink = cursor.getString(ToDo.Tasks.COLUMN_POSITION_SELF_LINK);
		String taskParent = cursor.getString(ToDo.Tasks.COLUMN_POSITION_PARENT);
		String taskPosition = cursor.getString(ToDo.Tasks.COLUMN_POSITION_POSITION);
		String taskNotes = cursor.getString(ToDo.Tasks.COLUMN_POSITION_NOTES);
		String taskStatus = cursor.getString(ToDo.Tasks.COLUMN_POSITION_STATUS);
		String taskDue = cursor.getString(ToDo.Tasks.COLUMN_POSITION_DUE);
		String taskCompleted = cursor.getString(ToDo.Tasks.COLUMN_POSITION_COMPLETED);
		int taskDeleted = cursor.getInt(ToDo.Tasks.COLUMN_POSITION_DELETED);
		int taskHidden = cursor.getInt(ToDo.Tasks.COLUMN_POSITION_HIDDEN);
		String taskListId = cursor.getString(ToDo.Tasks.COLUMN_POSITION_TASKLIST_ID);

		Task task = new Task();
		task.set(ToDo.Tasks.COLUMN_NAME_CLIENT_ID, taskClientId);
		task.setKind(taskKind);
		task.set(ToDo.Tasks.COLUMN_NAME_SERVER_ID, taskServerId);
		task.setTitle(taskTitle);
		task.setUpdated(DateTime.parseRfc3339(taskUpdated));
		task.setSelfLink(taskSelfLink);
		task.setParent(taskParent);
		task.setPosition(taskPosition);
		task.setNotes(taskNotes);
		task.setStatus(taskStatus);
		if (taskDue != null) {
			task.setDue(DateTime.parseRfc3339(taskDue));
		}
		if (taskCompleted != null) {
			task.setCompleted(DateTime.parseRfc3339(taskCompleted));
		}
		task.setDeleted((taskDeleted == ToDo.Tasks.COLUMN_VALUE_FALSE) ? false : true);
		task.setHidden((taskHidden == ToDo.Tasks.COLUMN_VALUE_FALSE) ? false : true);
		task.set(ToDo.Tasks.COLUMN_NAME_TASKLIST_ID, taskListId);

		return task;
	}

	/**
	 * Handles the exception.
	 * 
	 * @param exception
	 *            Exception to handle
	 */
	private void handleException(Exception exception) {
		String message = exception.getMessage();
		int posLeftBracket = message.indexOf('{');
		int posRightBracket = message.lastIndexOf('}');
		message = message.substring(posLeftBracket, posRightBracket + 1);
		JsonObject error = new JsonParser().parse(message).getAsJsonObject();
		int errorCode = error.get("code").getAsInt();
		String errorMsg = error.get("message").getAsString();

		switch (errorCode) {
		// Invalid authorization header.
		// The access token you're using is either expired or invalid.
		case 401:
			credential.getGoogleAccountManager()
					.invalidateAuthToken(""/* authtoken */);
			break;
		case 403:
			if ("The authenticated user has not granted the app {appId} access to the file {fileId}"
					.equals(errorMsg)) {
				Toast.makeText(this.context, errorMsg, Toast.LENGTH_SHORT).show();
				// FIXME: handle it properly.
			}
			// FIXME: for debug only.
			Toast.makeText(this.context, errorMsg, Toast.LENGTH_SHORT).show();
		}
	}

}
